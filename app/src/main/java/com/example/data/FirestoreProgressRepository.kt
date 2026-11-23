package com.example.data

import android.content.Context
import androidx.annotation.Keep
import com.example.R
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.example.model.CefrLevel
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

private val VALID_ID_REGEX = Regex("^[a-zA-Z0-9_\\-]+$")

@Keep
@IgnoreExtraProperties
data class LessonProgressRecord(
    @DocumentId val lessonId: String = "",
    val userId: String = "",
    val levelCode: String = CefrLevel.A1.code,
    val titleAr: String = "",
    val completed: Boolean = true,
    @ServerTimestamp val completedAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

class FirestoreProgressRepository(
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
            ?: throw IllegalStateException("يجب تسجيل الدخول بحساب Google قبل المزامنة مع Firebase.")
        check(uid == expectedUserId) { "تغيّر المستخدم المسجّل؛ تم إلغاء العملية." }
        return uid
    }

    fun observeLessonProgress(userId: String): Flow<List<LessonProgressRecord>> {
        val path = "users/$userId/lessonProgress"
        return db.collection("users")
            .document(userId)
            .collection("lessonProgress")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    LessonProgressRecord::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).filter { it.completed }
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

    suspend fun markLessonCompleted(
        userId: String,
        lessonId: String,
        levelCode: String,
        titleAr: String
    ): Result<Unit> {
        val docPath = "users/$userId/lessonProgress/$lessonId"
        return try {
            val uid = requireUserId(userId)
            require(lessonId.isNotBlank() && lessonId.length <= 128 && VALID_ID_REGEX.matches(lessonId)) {
                "معرّف الدرس غير صالح."
            }
            require(levelCode in setOf("A1", "A2", "B1", "B2", "C1", "C2")) {
                "مستوى الدرس غير صالح."
            }
            val safeTitle = titleAr.trim().take(120).ifEmpty { lessonId }

            val docRef = db.collection("users")
                .document(uid)
                .collection("lessonProgress")
                .document(lessonId)

            val existing = docRef.get().await()
            if (existing.exists()) {
                docRef.update(
                    mapOf(
                        "levelCode" to levelCode,
                        "titleAr" to safeTitle,
                        "completed" to true,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                val payload = mapOf(
                    "userId" to uid,
                    "levelCode" to levelCode,
                    "titleAr" to safeTitle,
                    "completed" to true,
                    "completedAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun unmarkLessonCompleted(
        userId: String,
        lessonId: String
    ): Result<Unit> {
        val docPath = "users/$userId/lessonProgress/$lessonId"
        return try {
            val uid = requireUserId(userId)
            require(lessonId.isNotBlank() && lessonId.length <= 128 && VALID_ID_REGEX.matches(lessonId)) {
                "معرّف الدرس غير صالح."
            }
            db.collection("users")
                .document(uid)
                .collection("lessonProgress")
                .document(lessonId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }
}
