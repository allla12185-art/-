package com.example.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

object DailyReminderManager {
    const val CHANNEL_ID = "rafeeq_daily_study_reminder"
    private const val NOTIFICATION_ID = 4101
    private const val REQUEST_CODE_ALARM = 4102
    private const val REQUEST_CODE_CONTENT = 4103

    private val motivationalMessages = listOf(
        "خطوة واحدة اليوم تصنع فرقاً كبيراً! خصّص 5 دقائق لدرسك التالي في رفيق الإنجليزية.",
        "حافظ على حماسك! استمع لدرس صوتي سريع وتابع تقدّمك في رحلة الإنجليزية.",
        "وقت الإنجليزية حان! تقدّم خطوة جديدة نحو الطلاقة واكسب نجوماً إضافية اليوم.",
        "الممارسة اليومية سرّ الإتقان — درسك المبسّط بانتظارك الآن!"
    )

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun ensureNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existing = manager.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.reminder_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.getString(R.string.reminder_channel_description)
                    enableVibration(true)
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        ensureNotificationChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildAlarmPendingIntent(context)

        val triggerCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
            set(Calendar.MINUTE, minute.coerceIn(0, 59))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerCalendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancelDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(buildAlarmPendingIntent(context))
    }

    fun showReminderNotification(
        context: Context,
        isTestPreview: Boolean = false
    ): Boolean {
        if (!hasNotificationPermission(context)) return false
        ensureNotificationChannel(context)

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_CONTENT,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dayIndex = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val bodyText = if (isTestPreview) {
            "تذكير تجريبي: حان موعد تعلّم الإنجليزية! خصّص بضع دقائق لمتابعة رحلتك الآن."
        } else {
            motivationalMessages[dayIndex % motivationalMessages.size]
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_reminder)
            .setContentTitle("رفيق الإنجليزية • وقت التعلّم!")
            .setContentText(bodyText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bodyText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .build()

        return try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
            true
        } catch (_: SecurityException) {
            false
        }
    }

    fun formatReminderTimeArabic(hour: Int, minute: Int): String {
        val safeHour = hour.coerceIn(0, 23)
        val safeMinute = minute.coerceIn(0, 59)
        val period = if (safeHour < 12) "صباحاً" else "مساءً"
        val hour12 = when {
            safeHour == 0 -> 12
            safeHour > 12 -> safeHour - 12
            else -> safeHour
        }
        val minPadded = safeMinute.toString().padStart(2, '0')
        return "$hour12:$minPadded $period"
    }

    private fun buildAlarmPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, DailyReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_ALARM,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

class DailyReminderScheduler(context: Context) {
    private val appContext = context.applicationContext

    fun applySchedule(enabled: Boolean, hour: Int, minute: Int) {
        if (enabled && DailyReminderManager.hasNotificationPermission(appContext)) {
            DailyReminderManager.scheduleDailyReminder(appContext, hour, minute)
        } else {
            DailyReminderManager.cancelDailyReminder(appContext)
        }
    }

    fun sendTestNotification(): Boolean {
        return DailyReminderManager.showReminderNotification(appContext, isTestPreview = true)
    }
}
