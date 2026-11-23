package com.example.data

import android.content.Context
import androidx.annotation.Keep
import com.example.R
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.example.model.CefrLevel
import com.example.model.Lesson
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.math.roundToInt

private val VALID_QUIZ_ID_REGEX = Regex("^[a-zA-Z0-9_\\-]+$")
private val ALLOWED_CEFR_CODES = setOf("A1", "A2", "B1", "B2", "C1", "C2")

@Keep
@IgnoreExtraProperties
data class QuizQuestionRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val lessonId: String = "",
    val orderIndex: Long = 0L,
    val promptAr: String = "",
    val englishPrompt: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Long = 0L,
    val explanationAr: String = "",
    val levelCode: String = CefrLevel.A1.code,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class QuizResultRecord(
    @DocumentId val lessonId: String = "",
    val userId: String = "",
    val lessonTitleAr: String = "",
    val levelCode: String = CefrLevel.A1.code,
    val correctCount: Long = 0L,
    val totalCount: Long = 0L,
    val scorePercent: Long = 0L,
    val bestScorePercent: Long = 0L,
    val attemptCount: Long = 0L,
    val masteryLabelAr: String = "",
    val passed: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

class FirestoreQuizRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        db = FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(expectedUserId: String): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("يجب تسجيل الدخول بحساب Google أولاً.")
        check(uid == expectedUserId) { "تغيّر المستخدم المسجّل؛ تم إلغاء العملية." }
        return uid
    }

    fun observeLessonQuizQuestions(
        userId: String,
        lessonId: String
    ): Flow<List<QuizQuestionRecord>> {
        val path = "users/$userId/quizQuestions"
        return db.collection("users")
            .document(userId)
            .collection("quizQuestions")
            .whereEqualTo("lessonId", lessonId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    QuizQuestionRecord::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedBy { it.orderIndex }
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                throw error
            }
    }

    fun observeAllQuizResults(userId: String): Flow<Map<String, QuizResultRecord>> {
        val path = "users/$userId/quizResults"
        return db.collection("users")
            .document(userId)
            .collection("quizResults")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    QuizResultRecord::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).associateBy { it.lessonId }
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                throw error
            }
    }

    suspend fun ensureLessonQuizSeeded(
        userId: String,
        lesson: Lesson
    ): Result<Unit> {
        val colPath = "users/$userId/quizQuestions"
        return try {
            val uid = requireUserId(userId)
            require(
                lesson.id.isNotBlank() &&
                    lesson.id.length <= 128 &&
                    VALID_QUIZ_ID_REGEX.matches(lesson.id)
            ) {
                "معرّف الدرس غير صالح."
            }
            if (lesson.quiz.isEmpty()) return Result.success(Unit)

            val colRef = db.collection("users")
                .document(uid)
                .collection("quizQuestions")

            val existing = colRef
                .whereEqualTo("lessonId", lesson.id)
                .limit(1)
                .get()
                .await()

            if (existing.isEmpty) {
                val batch = db.batch()
                lesson.quiz.take(100).forEachIndexed { index, q ->
                    val rawDocId = "${lesson.id}_${q.id}".replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
                    val docId = rawDocId.take(128).ifBlank { "${lesson.id}_q_$index" }
                    val safeOptions = q.options
                        .map { it.trim().ifEmpty { "-" } }
                        .take(6)
                        .let { list ->
                            when {
                                list.size >= 2 -> list
                                list.size == 1 -> list + "—"
                                else -> listOf("Option A", "Option B")
                            }
                        }
                    val safeCorrectIndex = q.correctIndex.coerceIn(0, safeOptions.lastIndex).toLong()
                    val safeLevelCode = q.level.code.takeIf { it in ALLOWED_CEFR_CODES }
                        ?: lesson.level.code.takeIf { it in ALLOWED_CEFR_CODES }
                        ?: CefrLevel.A1.code

                    val payload = mapOf(
                        "userId" to uid,
                        "lessonId" to lesson.id,
                        "orderIndex" to index.coerceIn(0, 100).toLong(),
                        "promptAr" to q.promptAr.trim().take(240).ifEmpty { "اختر الإجابة الصحيحة" },
                        "englishPrompt" to q.englishPrompt.trim().take(300).ifEmpty { "-" },
                        "options" to safeOptions,
                        "correctIndex" to safeCorrectIndex,
                        "explanationAr" to q.explanationAr.trim().take(320).ifEmpty { "راجع القاعدة الأساسية للدرس." },
                        "levelCode" to safeLevelCode,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    batch.set(colRef.document(docId), payload)
                }
                batch.commit().await()
            }

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, colPath)
            Result.failure(e)
        }
    }

    suspend fun submitQuizAttempt(
        userId: String,
        lesson: Lesson,
        correctCount: Int,
        totalCount: Int
    ): Result<QuizResultRecord> {
        val docPath = "users/$userId/quizResults/${lesson.id}"
        return try {
            val uid = requireUserId(userId)
            require(
                lesson.id.isNotBlank() &&
                    lesson.id.length <= 128 &&
                    VALID_QUIZ_ID_REGEX.matches(lesson.id)
            ) {
                "معرّف الدرس غير صالح."
            }
            val safeTotal = totalCount.coerceIn(1, 100)
            val safeCorrect = correctCount.coerceIn(0, safeTotal)
            val scorePercent = ((safeCorrect.toDouble() / safeTotal.toDouble()) * 100.0)
                .roundToInt()
                .coerceIn(0, 100)
            val passed = scorePercent >= 60
            val masteryLabelAr = evaluateMasteryLabelAr(scorePercent).take(100)
            val safeTitle = lesson.titleAr.trim().take(120).ifEmpty { lesson.id }
            val safeLevelCode = lesson.level.code.takeIf { it in ALLOWED_CEFR_CODES } ?: CefrLevel.A1.code

            val docRef = db.collection("users")
                .document(uid)
                .collection("quizResults")
                .document(lesson.id)

            val existingSnap = docRef.get().await()
            val newBestScore: Int
            val newAttemptCount: Int

            if (existingSnap.exists()) {
                val prevBest = (existingSnap.get("bestScorePercent") as? Number)?.toInt() ?: 0
                val prevAttempts = (existingSnap.get("attemptCount") as? Number)?.toInt() ?: 0
                newBestScore = maxOf(prevBest, scorePercent).coerceIn(0, 100)
                newAttemptCount = (prevAttempts + 1).coerceIn(1, 10_000)

                docRef.update(
                    mapOf(
                        "lessonTitleAr" to safeTitle,
                        "levelCode" to safeLevelCode,
                        "correctCount" to safeCorrect.toLong(),
                        "totalCount" to safeTotal.toLong(),
                        "scorePercent" to scorePercent.toLong(),
                        "bestScorePercent" to newBestScore.toLong(),
                        "attemptCount" to newAttemptCount.toLong(),
                        "masteryLabelAr" to masteryLabelAr,
                        "passed" to passed,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                newBestScore = scorePercent
                newAttemptCount = 1

                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "lessonTitleAr" to safeTitle,
                        "levelCode" to safeLevelCode,
                        "correctCount" to safeCorrect.toLong(),
                        "totalCount" to safeTotal.toLong(),
                        "scorePercent" to scorePercent.toLong(),
                        "bestScorePercent" to newBestScore.toLong(),
                        "attemptCount" to newAttemptCount.toLong(),
                        "masteryLabelAr" to masteryLabelAr,
                        "passed" to passed,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            Result.success(
                QuizResultRecord(
                    lessonId = lesson.id,
                    userId = uid,
                    lessonTitleAr = safeTitle,
                    levelCode = safeLevelCode,
                    correctCount = safeCorrect.toLong(),
                    totalCount = safeTotal.toLong(),
                    scorePercent = scorePercent.toLong(),
                    bestScorePercent = newBestScore.toLong(),
                    attemptCount = newAttemptCount.toLong(),
                    masteryLabelAr = masteryLabelAr,
                    passed = passed
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    companion object {
        fun evaluateMasteryLabelAr(scorePercent: Int): String = when {
            scorePercent >= 90 -> "ممتاز! إتقان رائع للدرس"
            scorePercent >= 70 -> "فهم جيد جداً • جاهز للمتابعة"
            scorePercent >= 50 -> "فهم مقبول • يُنصح بمراجعة سريعة"
            else -> "يحتاج مراجعة القواعد والأمثلة"
        }
    }
}
