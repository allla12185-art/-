package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AudioPlaybackState
import com.example.data.QuizResultRecord
import com.example.model.AudioLessonTrack
import com.example.model.CefrLevel
import com.example.model.Lesson
import com.example.ui.components.AudioPlayIconButton
import com.example.ui.components.CefrBadge
import com.example.ui.components.EnglishLtrText
import com.example.ui.components.SectionHeaderRow
import com.example.ui.components.SpeedToggleChip
import com.example.ui.components.branchAccentColor
import com.example.ui.components.FirebaseLearningProgressCard
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.viewmodel.FirestoreProgressState

@Composable
fun LessonsScreen(
    lessons: List<Lesson>,
    audioTracksByLesson: Map<String, AudioLessonTrack>,
    quizResultsByLesson: Map<String, QuizResultRecord>,
    audioState: AudioPlaybackState,
    firestoreProgressState: FirestoreProgressState,
    syncActionError: String?,
    selectedLevel: CefrLevel?,
    completedLessonIds: Set<String>,
    onSelectLevel: (CefrLevel?) -> Unit,
    onToggleComplete: (String) -> Unit,
    onRetrySync: () -> Unit,
    onDismissSyncError: () -> Unit,
    onPlayTrack: (AudioLessonTrack) -> Unit,
    onNextSegment: () -> Unit,
    onPrevSegment: () -> Unit,
    onToggleSpeed: () -> Unit,
    onOpenLesson: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredLessons = if (selectedLevel == null) {
        lessons
    } else {
        lessons.filter { it.level == selectedLevel }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeaderRow(
                    title = "الدروس الصوتية والمبسطة",
                    subtitle = "استمع لكل درس عبر المشغل المدمج أو افتح التفاصيل الكاملة",
                    trailing = {
                        SpeedToggleChip(
                            isSlow = audioState.isSlowSpeed,
                            onToggle = onToggleSpeed,
                            testTag = "lessons_global_speed_toggle"
                        )
                    }
                )

                // Motivating Firebase-backed Learning Progress Bar
                FirebaseLearningProgressCard(
                    progressState = firestoreProgressState,
                    lessons = lessons,
                    onRetrySync = onRetrySync,
                    onOpenNextLesson = onOpenLesson
                )

                if (syncActionError != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("lessons_sync_error_banner"),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = syncActionError,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = onDismissSyncError,
                                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                            ) {
                                Text("إغلاق")
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedLevel == null,
                        onClick = { onSelectLevel(null) },
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("filter_level_all"),
                        label = { Text("الكل (${lessons.size})") }
                    )
                    CefrLevel.entries.forEach { level ->
                        FilterChip(
                            selected = selectedLevel == level,
                            onClick = { onSelectLevel(level) },
                            modifier = Modifier
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("filter_level_${level.code.lowercase()}"),
                            label = { Text("${level.code} • ${level.titleAr}") }
                        )
                    }
                }
            }
        }

        items(filteredLessons, key = { it.id }) { lesson ->
            val isDone = lesson.id in completedLessonIds
            val branchAccent = branchAccentColor(lesson.branch)
            val audioTrack = audioTracksByLesson[lesson.id] ?: lesson.toAudioLessonTrack()
            val isTrackActive = audioState.activeTrackId == audioTrack.trackId
            val quizResult = quizResultsByLesson[lesson.id]

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenLesson(lesson.id) }
                    .testTag("lesson_card_${lesson.id}"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    width = if (isTrackActive || isDone) 1.6.dp else 1.dp,
                    color = when {
                        isTrackActive -> MaterialTheme.colorScheme.primary
                        isDone -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CefrBadge(lesson.level)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = branchAccent.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = lesson.branch.titleAr,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = branchAccent
                                )
                            }
                            if (quizResult != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (quizResult.passed) {
                                        SuccessGreenContainer
                                    } else {
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    },
                                    modifier = Modifier.testTag("lesson_quiz_badge_${lesson.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Quiz,
                                            contentDescription = null,
                                            modifier = Modifier.size(13.dp),
                                            tint = if (quizResult.passed) {
                                                SuccessGreen
                                            } else {
                                                MaterialTheme.colorScheme.onTertiaryContainer
                                            }
                                        )
                                        Text(
                                            text = "الاختبار ${quizResult.scorePercent}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (quizResult.passed) {
                                                SuccessGreen
                                            } else {
                                                MaterialTheme.colorScheme.onTertiaryContainer
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        IconButton(
                            onClick = { onToggleComplete(lesson.id) },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("lesson_complete_toggle_${lesson.id}")
                        ) {
                            Icon(
                                imageVector = if (isDone) {
                                    Icons.Filled.CheckCircle
                                } else {
                                    Icons.Outlined.RadioButtonUnchecked
                                },
                                contentDescription = stringResource(R.string.cd_complete_lesson),
                                tint = if (isDone) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = lesson.unitAr,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = lesson.titleAr,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        EnglishLtrText(
                            text = lesson.titleEn,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = lesson.summaryAr,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Built-in Mini Audio Player for every lesson
                    EmbeddedLessonAudioPlayer(
                        track = audioTrack,
                        audioState = audioState,
                        isTrackActive = isTrackActive,
                        onPlayToggle = { onPlayTrack(audioTrack) },
                        onNextSegment = onNextSegment,
                        onPrevSegment = onPrevSegment
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${lesson.durationMinutes} دقيقة • ${lesson.quiz.size} أسئلة",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isDone) "مراجعة الدرس" else "افتح الدرس الكامل",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmbeddedLessonAudioPlayer(
    track: AudioLessonTrack,
    audioState: AudioPlaybackState,
    isTrackActive: Boolean,
    onPlayToggle: () -> Unit,
    onNextSegment: () -> Unit,
    onPrevSegment: () -> Unit
) {
    val progress = if (isTrackActive && audioState.totalSegments > 0) {
        (audioState.currentSegmentIndex + 1).toFloat() / audioState.totalSegments.toFloat()
    } else 0f

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isTrackActive) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50f)
        },
        border = BorderStroke(
            1.dp,
            if (isTrackActive) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AudioPlayIconButton(
                    isPlaying = isTrackActive,
                    onClick = onPlayToggle,
                    testTag = "lesson_audio_play_${track.lessonId}"
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Headphones,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (isTrackActive) "جاري تشغيل الدرس الصوتي" else "مشغل الدرس الصوتي",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = if (isTrackActive) {
                            "المقطع ${audioState.currentSegmentIndex + 1} من ${audioState.totalSegments} • ${track.accentLabel}"
                        } else {
                            "${track.segments.size} مقاطع ناطقة • ~${track.totalDurationSeconds} ثانية"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isTrackActive) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            FilledTonalIconButton(
                                onClick = onPrevSegment,
                                enabled = audioState.currentSegmentIndex > 0,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("lesson_audio_prev_${track.lessonId}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipPrevious,
                                    contentDescription = "المقطع السابق"
                                )
                            }
                            FilledTonalIconButton(
                                onClick = onNextSegment,
                                enabled = audioState.currentSegmentIndex < audioState.totalSegments - 1,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("lesson_audio_next_${track.lessonId}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipNext,
                                    contentDescription = "المقطع التالي"
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isTrackActive) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                    audioState.currentSegmentEnglish?.let { eng ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                audioState.currentSegmentSpeaker?.let { speaker ->
                                    Text(
                                        text = speaker,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                EnglishLtrText(
                                    text = eng,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                audioState.currentSegmentArabic?.let { ar ->
                                    Text(
                                        text = ar,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
