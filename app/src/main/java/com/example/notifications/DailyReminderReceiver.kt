package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.ProgressDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DailyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        DailyReminderManager.showReminderNotification(context, isTestPreview = false)
    }
}

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val progress = ProgressDataStore(appContext).progressFlow.first()
                if (progress.dailyReminderEnabled &&
                    DailyReminderManager.hasNotificationPermission(appContext)
                ) {
                    DailyReminderManager.scheduleDailyReminder(
                        context = appContext,
                        hour = progress.dailyReminderHour,
                        minute = progress.dailyReminderMinute
                    )
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
