package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.CefrLevel
import com.example.model.StudyMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.rafeeqDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "rafeeq_english_prefs"
)

data class UserProgress(
    val completedLessonIds: Set<String> = emptySet(),
    val completedCanDoIds: Set<String> = emptySet(),
    val bookmarkedSentenceIds: Set<String> = emptySet(),
    val studyMode: StudyMode = StudyMode.NORMAL,
    val assessedLevel: CefrLevel? = null,
    val streakDays: Int = 1,
    val stars: Int = 60,
    val diamonds: Int = 250,
    val dailyReminderEnabled: Boolean = false,
    val dailyReminderHour: Int = 20,
    val dailyReminderMinute: Int = 0
)

class ProgressDataStore(private val context: Context) {
    private object Keys {
        val COMPLETED_LESSONS = stringSetPreferencesKey("completed_lessons")
        val COMPLETED_CANDO = stringSetPreferencesKey("completed_cando")
        val BOOKMARKED_SENTENCES = stringSetPreferencesKey("bookmarked_sentences")
        val STUDY_MODE = stringPreferencesKey("study_mode")
        val ASSESSED_LEVEL = stringPreferencesKey("assessed_level")
        val STREAK_DAYS = intPreferencesKey("streak_days")
        val BONUS_STARS = intPreferencesKey("bonus_stars")
        val BONUS_DIAMONDS = intPreferencesKey("bonus_diamonds")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val DAILY_REMINDER_HOUR = intPreferencesKey("daily_reminder_hour")
        val DAILY_REMINDER_MINUTE = intPreferencesKey("daily_reminder_minute")
    }

    val progressFlow: Flow<UserProgress> = context.rafeeqDataStore.data.map { prefs ->
        val modeName = prefs[Keys.STUDY_MODE]
        val levelName = prefs[Keys.ASSESSED_LEVEL]
        val completed = prefs[Keys.COMPLETED_LESSONS] ?: emptySet()
        val canDos = prefs[Keys.COMPLETED_CANDO] ?: emptySet()
        val bonusStars = prefs[Keys.BONUS_STARS] ?: 0
        val bonusDiamonds = prefs[Keys.BONUS_DIAMONDS] ?: 0

        UserProgress(
            completedLessonIds = completed,
            completedCanDoIds = canDos,
            bookmarkedSentenceIds = prefs[Keys.BOOKMARKED_SENTENCES] ?: emptySet(),
            studyMode = StudyMode.entries.find { it.name == modeName } ?: StudyMode.NORMAL,
            assessedLevel = CefrLevel.entries.find { it.name == levelName },
            streakDays = prefs[Keys.STREAK_DAYS] ?: 1,
            stars = 60 + (completed.size * 30) + (canDos.size * 10) + bonusStars,
            diamonds = 250 + (completed.size * 50) + bonusDiamonds,
            dailyReminderEnabled = prefs[Keys.DAILY_REMINDER_ENABLED] ?: false,
            dailyReminderHour = prefs[Keys.DAILY_REMINDER_HOUR] ?: 20,
            dailyReminderMinute = prefs[Keys.DAILY_REMINDER_MINUTE] ?: 0
        )
    }

    suspend fun toggleLessonCompleted(lessonId: String) {
        context.rafeeqDataStore.edit { prefs ->
            val current = prefs[Keys.COMPLETED_LESSONS] ?: emptySet()
            prefs[Keys.COMPLETED_LESSONS] = if (lessonId in current) {
                current - lessonId
            } else {
                current + lessonId
            }
        }
    }

    suspend fun markLessonCompleted(lessonId: String) {
        context.rafeeqDataStore.edit { prefs ->
            val current = prefs[Keys.COMPLETED_LESSONS] ?: emptySet()
            prefs[Keys.COMPLETED_LESSONS] = current + lessonId
        }
    }

    suspend fun toggleCanDo(canDoId: String) {
        context.rafeeqDataStore.edit { prefs ->
            val current = prefs[Keys.COMPLETED_CANDO] ?: emptySet()
            prefs[Keys.COMPLETED_CANDO] = if (canDoId in current) {
                current - canDoId
            } else {
                current + canDoId
            }
        }
    }

    suspend fun toggleBookmark(sentenceId: String) {
        context.rafeeqDataStore.edit { prefs ->
            val current = prefs[Keys.BOOKMARKED_SENTENCES] ?: emptySet()
            prefs[Keys.BOOKMARKED_SENTENCES] = if (sentenceId in current) {
                current - sentenceId
            } else {
                current + sentenceId
            }
        }
    }

    suspend fun setStudyMode(mode: StudyMode) {
        context.rafeeqDataStore.edit { prefs ->
            prefs[Keys.STUDY_MODE] = mode.name
        }
    }

    suspend fun setAssessedLevel(level: CefrLevel) {
        context.rafeeqDataStore.edit { prefs ->
            prefs[Keys.ASSESSED_LEVEL] = level.name
            prefs[Keys.BONUS_DIAMONDS] = (prefs[Keys.BONUS_DIAMONDS] ?: 0) + 50
            prefs[Keys.BONUS_STARS] = (prefs[Keys.BONUS_STARS] ?: 0) + 20
        }
    }

    suspend fun setDailyReminder(enabled: Boolean, hour: Int, minute: Int) {
        context.rafeeqDataStore.edit { prefs ->
            prefs[Keys.DAILY_REMINDER_ENABLED] = enabled
            prefs[Keys.DAILY_REMINDER_HOUR] = hour.coerceIn(0, 23)
            prefs[Keys.DAILY_REMINDER_MINUTE] = minute.coerceIn(0, 59)
        }
    }
}
