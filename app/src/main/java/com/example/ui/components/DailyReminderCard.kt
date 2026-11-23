package com.example.ui.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.notifications.DailyReminderManager

private data class ReminderPreset(
    val label: String,
    val hour: Int,
    val minute: Int
)

private val reminderPresets = listOf(
    ReminderPreset("صباحاً 9:00", 9, 0),
    ReminderPreset("عصراً 5:00", 17, 0),
    ReminderPreset("مساءً 8:00", 20, 0)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyReminderCard(
    enabled: Boolean,
    hour: Int,
    minute: Int,
    onUpdateReminder: (Boolean, Int, Int) -> Unit,
    onSendTestNotification: () -> Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showTimePicker by remember { mutableStateOf(false) }
    var permissionDeniedFeedback by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            permissionDeniedFeedback = false
            pendingAction?.invoke()
        } else {
            permissionDeniedFeedback = true
            statusMessage = null
        }
        pendingAction = null
    }

    fun runWithNotificationPermission(action: () -> Unit) {
        if (DailyReminderManager.hasNotificationPermission(context)) {
            permissionDeniedFeedback = false
            action()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pendingAction = action
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            action()
        }
    }

    val formattedTime = DailyReminderManager.formatReminderTimeArabic(hour, minute)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_reminder_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = if (enabled) 1.5.dp else 1.dp,
            color = if (enabled) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (enabled) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (enabled) {
                                    Icons.Filled.NotificationsActive
                                } else {
                                    Icons.Outlined.Notifications
                                },
                                contentDescription = null,
                                tint = if (enabled) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "التذكير اليومي للتعلّم",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (enabled) {
                                "مفعّل يومياً الساعة $formattedTime"
                            } else {
                                "فعّله لاستلام إشعار محلي يومي يحفزك على المتابعة"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (enabled) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }

                Switch(
                    checked = enabled,
                    onCheckedChange = { wantEnabled ->
                        if (wantEnabled) {
                            runWithNotificationPermission {
                                onUpdateReminder(true, hour, minute)
                                statusMessage = "تم تفعيل التذكير اليومي الساعة $formattedTime"
                            }
                        } else {
                            onUpdateReminder(false, hour, minute)
                            statusMessage = "تم إيقاف التذكير اليومي."
                        }
                    },
                    modifier = Modifier.testTag("daily_reminder_switch")
                )
            }

            // Time selection presets & custom time picker
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "وقت التذكير المفضّل",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    reminderPresets.forEach { preset ->
                        val isSelected = hour == preset.hour && minute == preset.minute
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (enabled) {
                                    runWithNotificationPermission {
                                        onUpdateReminder(true, preset.hour, preset.minute)
                                        statusMessage = "تم ضبط التذكير على ${preset.label}"
                                    }
                                } else {
                                    onUpdateReminder(false, preset.hour, preset.minute)
                                    statusMessage = "تم اختيار الوقت ${preset.label}"
                                }
                            },
                            modifier = Modifier
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("reminder_preset_${preset.hour}"),
                            label = { Text(preset.label) }
                        )
                    }

                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("reminder_custom_time_button"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تخصيص ($formattedTime)")
                    }
                }
            }

            // Quick test notification button
            FilledTonalButton(
                onClick = {
                    runWithNotificationPermission {
                        val sent = onSendTestNotification()
                        statusMessage = if (sent) {
                            "تم إرسال إشعار محلي تجريبي! تفحّص شريط الإشعارات."
                        } else {
                            "تعذّر إرسال الإشعار. تأكد من تفعيل الإشعارات في إعدادات الجهاز."
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("reminder_test_notification_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.NotificationsActive,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تجربة إشعار محلي الآن",
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(visible = permissionDeniedFeedback) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "يلزم السماح بإذن الإشعارات لاستلام التذكير اليومي.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = {
                                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                runCatching { context.startActivity(intent) }.onFailure {
                                    val fallback = Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", context.packageName, null)
                                    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                                    context.startActivity(fallback)
                                }
                            },
                            modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                        ) {
                            Text("الإعدادات")
                        }
                    }
                }
            }

            AnimatedVisibility(visible = statusMessage != null && !permissionDeniedFeedback) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { statusMessage = null }
                ) {
                    Text(
                        text = statusMessage.orEmpty(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = hour,
            initialMinute = minute,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("اختر وقت التذكير اليومي") },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pickedHour = timePickerState.hour
                        val pickedMinute = timePickerState.minute
                        showTimePicker = false
                        runWithNotificationPermission {
                            onUpdateReminder(true, pickedHour, pickedMinute)
                            val label = DailyReminderManager.formatReminderTimeArabic(pickedHour, pickedMinute)
                            statusMessage = "تم ضبط التذكير اليومي على $label"
                        }
                    },
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text("حفظ الوقت")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTimePicker = false },
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun DailyReminderDialog(
    enabled: Boolean,
    hour: Int,
    minute: Int,
    onUpdateReminder: (Boolean, Int, Int) -> Unit,
    onSendTestNotification: () -> Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("منبّه المذاكرة اليومي") },
        text = {
            DailyReminderCard(
                enabled = enabled,
                hour = hour,
                minute = minute,
                onUpdateReminder = onUpdateReminder,
                onSendTestNotification = onSendTestNotification
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
            ) {
                Text("تم")
            }
        }
    )
}
