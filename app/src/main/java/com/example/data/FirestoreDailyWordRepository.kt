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

private val VALID_DATE_KEY_REGEX = Regex("^[a-zA-Z0-9_\\-]+$")

@Keep
@IgnoreExtraProperties
data class DailyWordRecord(
    @DocumentId val dateKey: String = "",
    val userId: String = "",
    val wordEn: String = "",
    val ipa: String = "",
    val partOfSpeechAr: String = "",
    val meaningAr: String = "",
    val exampleEn: String = "",
    val exampleAr: String = "",
    val levelCode: String = CefrLevel.B1.code,
    val practiced: Boolean = false,
    val listenCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

class FirestoreDailyWordRepository(
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

    fun observeTodayWord(userId: String, dateKey: String = DailyWordsCatalog.todayDateKey()): Flow<DailyWordRecord?> {
        val path = "users/$userId/dailyWords/$dateKey"
        return db.collection("users")
            .document(userId)
            .collection("dailyWords")
            .document(dateKey)
            .snapshots()
            .map { snapshot ->
                if (!snapshot.exists()) {
                    null
                } else {
                    snapshot.toObject(
                        DailyWordRecord::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                    handleFirestoreError(exceptionToLog, OperationType.GET, path)
                }
                throw error
            }
    }

    fun observeMasteredDailyWordsCount(userId: String): Flow<Int> {
        val path = "users/$userId/dailyWords"
        return db.collection("users")
            .document(userId)
            .collection("dailyWords")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    DailyWordRecord::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).count { it.practiced }
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

    suspend fun ensureTodayWordExists(
        userId: String,
        dateKey: String = DailyWordsCatalog.todayDateKey(),
        seed: DailyWordSeed = DailyWordsCatalog.wordForToday()
    ): Result<Unit> {
        val docPath = "users/$userId/dailyWords/$dateKey"
        return try {
            val uid = requireUserId(userId)
            require(dateKey.isNotBlank() && dateKey.length <= 128 && VALID_DATE_KEY_REGEX.matches(dateKey)) {
                "مفتاح اليوم غير صالح."
            }
            val docRef = db.collection("users")
                .document(uid)
                .collection("dailyWords")
                .document(dateKey)

            val snap = docRef.get().await()
            if (!snap.exists()) {
                val payload = buildCreatePayload(uid, seed)
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun setPracticed(
        userId: String,
        dateKey: String,
        practiced: Boolean
    ): Result<Unit> {
        val docPath = "users/$userId/dailyWords/$dateKey"
        return try {
            val uid = requireUserId(userId)
            require(dateKey.isNotBlank() && dateKey.length <= 128 && VALID_DATE_KEY_REGEX.matches(dateKey)) {
                "مفتاح اليوم غير صالح."
            }
            db.collection("users")
                .document(uid)
                .collection("dailyWords")
                .document(dateKey)
                .update(
                    mapOf(
                        "practiced" to practiced,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun recordListen(
        userId: String,
        dateKey: String
    ): Result<Unit> {
        val docPath = "users/$userId/dailyWords/$dateKey"
        return try {
            val uid = requireUserId(userId)
            require(dateKey.isNotBlank() && dateKey.length <= 128 && VALID_DATE_KEY_REGEX.matches(dateKey)) {
                "مفتاح اليوم غير صالح."
            }
            db.collection("users")
                .document(uid)
                .collection("dailyWords")
                .document(dateKey)
                .update(
                    mapOf(
                        "listenCount" to FieldValue.increment(1L),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun switchTodayWord(
        userId: String,
        dateKey: String,
        nextSeed: DailyWordSeed
    ): Result<Unit> {
        val docPath = "users/$userId/dailyWords/$dateKey"
        return try {
            val uid = requireUserId(userId)
            require(dateKey.isNotBlank() && dateKey.length <= 128 && VALID_DATE_KEY_REGEX.matches(dateKey)) {
                "مفتاح اليوم غير صالح."
            }
            val docRef = db.collection("users")
                .document(uid)
                .collection("dailyWords")
                .document(dateKey)
            val snap = docRef.get().await()
            if (snap.exists()) {
                docRef.update(
                    mapOf(
                        "wordEn" to nextSeed.wordEn.take(60),
                        "ipa" to nextSeed.ipa.take(80),
                        "partOfSpeechAr" to nextSeed.partOfSpeechAr.take(40),
                        "meaningAr" to nextSeed.meaningAr.take(120),
                        "exampleEn" to nextSeed.exampleEn.take(240),
                        "exampleAr" to nextSeed.exampleAr.take(240),
                        "levelCode" to nextSeed.levelCode,
                        "practiced" to false,
                        "listenCount" to 0L,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.set(buildCreatePayload(uid, nextSeed)).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    private fun buildCreatePayload(uid: String, seed: DailyWordSeed): Map<String, Any> = mapOf(
        "userId" to uid,
        "wordEn" to seed.wordEn.take(60),
        "ipa" to seed.ipa.take(80),
        "partOfSpeechAr" to seed.partOfSpeechAr.take(40),
        "meaningAr" to seed.meaningAr.take(120),
        "exampleEn" to seed.exampleEn.take(240),
        "exampleAr" to seed.exampleAr.take(240),
        "levelCode" to seed.levelCode,
        "practiced" to false,
        "listenCount" to 0L,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}
