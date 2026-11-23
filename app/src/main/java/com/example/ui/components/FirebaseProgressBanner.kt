package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.CefrLevel
import com.example.model.Lesson
import com.example.ui.theme.WorldMapPalette
import com.example.viewmodel.FirestoreProgressState
import kotlin.math.roundToInt

@Composable
fun FirebaseLearningProgressCard(
    progressState: FirestoreProgressState,
    lessons: List<Lesson>,
    onRetrySync: () -> Unit,
    onOpenNextLesson: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val completedIds = when (progressState) {
        is FirestoreProgressState.Synced -> progressState.completedLessonIds
        else -> emptySet()
    }
    val totalCount = lessons.size.coerceAtLeast(1)
    val completedCount = completedIds.count { id -> lessons.any { it.id == id } }
    val rawFraction = (completedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(
        targetValue = rawFraction,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "firebase_progress_fraction"
    )
    val percentInt = (rawFraction * 100f).roundToInt().coerceIn(0, 100)
    val nextLesson = lessons.firstOrNull { it.id !in completedIds }

    val (motivationTitle, motivationSubtitle) = when {
        completedCount == 0 ->
            "انطلق الآن!" to "أكمل درسك الأول لإضاءة شريط التقدّم في Firebase"
        completedCount == totalCount ->
            "بطل الطلاقة!" to "أنجزت جميع الدروس الـ $totalCount بنجاح مبهر"
        rawFraction >= 0.75f ->
            "على أعتاب الإتقان!" to "بقيت ${totalCount - completedCount} دروس فقط لإتمام الرحلة"
        rawFraction >= 0.45f ->
            "زخم رائع!" to "تجاوزت منتصف الطريق، استمر بهذا التفوّق"
        else ->
            "بداية موفّقة!" to "كل درس مكتمل يقرّبك خطوة نحو التحدث بثقة"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("firebase_learning_progress_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.5.dp, WorldMapPalette.NodeCompletedTop.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            WorldMapPalette.BannerNavyLight,
                            WorldMapPalette.BannerNavy,
                            WorldMapPalette.BannerNavyDark
                        )
                    )
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Row: Firebase Sync Status + Percentage Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SyncStatusPill(
                    progressState = progressState,
                    onRetry = onRetrySync
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WorldMapPalette.GoldProgress,
                    border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.75f)),
                    modifier = Modifier.testTag("firebase_progress_percent_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (completedCount >= totalCount) Icons.Filled.EmojiEvents else Icons.Filled.Star,
                            contentDescription = null,
                            tint = WorldMapPalette.SignTextDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$percentInt%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = WorldMapPalette.SignTextDark
                        )
                    }
                }
            }

            // Motivational Headline & Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = motivationTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = motivationSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.88f)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$completedCount / $totalCount",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = WorldMapPalette.GoldHighlight,
                        modifier = Modifier.testTag("firebase_completed_count_text")
                    )
                    Text(
                        text = "درس مكتمل",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // 3D Beveled Golden-Cyan Learning Progress Bar with Milestone Ticks
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.34f))
                        .border(1.5.dp, Color.White.copy(alpha = 0.45f), CircleShape)
                        .testTag("firebase_progress_bar_track")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(3.dp)
                    ) {
                        if (animatedFraction > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedFraction.coerceAtLeast(0.06f))
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                WorldMapPalette.GoldStarBorder,
                                                WorldMapPalette.GoldProgress,
                                                WorldMapPalette.GoldHighlight
                                            )
                                        )
                                    )
                            )
                        }
                    }

                    // Milestone marker labels inside bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (completedCount == 0) {
                                "أكمل أول درس لبدء شريط التقدم"
                            } else {
                                "تقدّمك السحابي: $completedCount من أصل $totalCount دروس ($percentInt%)"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                // Milestone Badges Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MilestoneStepLabel(label = "البداية", reached = completedCount >= 1)
                    MilestoneStepLabel(label = "25%", reached = rawFraction >= 0.25f)
                    MilestoneStepLabel(label = "50%", reached = rawFraction >= 0.50f)
                    MilestoneStepLabel(label = "75%", reached = rawFraction >= 0.75f)
                    MilestoneStepLabel(label = "الإتقان 100%", reached = completedCount >= totalCount)
                }
            }

            // Per-Level CEFR Breakdown Strip (A1 -> C2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CefrLevel.entries.forEach { level ->
                    val levelLessons = lessons.filter { it.level == level }
                    val levelDone = levelLessons.count { it.id in completedIds }
                    val allLevelDone = levelLessons.isNotEmpty() && levelDone == levelLessons.size
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = if (allLevelDone) {
                            WorldMapPalette.CheckGreen.copy(alpha = 0.26f)
                        } else {
                            Color.White.copy(alpha = 0.12f)
                        },
                        border = BorderStroke(
                            1.dp,
                            if (allLevelDone) WorldMapPalette.CheckGreen else Color.White.copy(alpha = 0.25f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = level.code,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "$levelDone/${levelLessons.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (allLevelDone) {
                                    WorldMapPalette.GoldHighlight
                                } else {
                                    Color.White.copy(alpha = 0.8f)
                                }
                            )
                        }
                    }
                }
            }

            // Next Lesson Motivational CTA
            if (nextLesson != null) {
                Button(
                    onClick = { onOpenNextLesson(nextLesson.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("firebase_progress_next_lesson_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WorldMapPalette.GoldProgress,
                        contentColor = WorldMapPalette.SignTextDark
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "أكمل التالي (${nextLesson.level.code}): ${nextLesson.titleAr}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SyncStatusPill(
    progressState: FirestoreProgressState,
    onRetry: () -> Unit
) {
    when (progressState) {
        is FirestoreProgressState.Loading -> {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.16f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = WorldMapPalette.GoldHighlight,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "جاري المزامنة مع Firebase...",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
        is FirestoreProgressState.Synced -> {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = WorldMapPalette.CheckGreen.copy(alpha = 0.25f),
                border = BorderStroke(1.dp, WorldMapPalette.CheckGreen.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudDone,
                        contentDescription = null,
                        tint = WorldMapPalette.GoldHighlight,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "مزامنة مباشرة عبر Firebase",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
        is FirestoreProgressState.Error -> {
            FilledTonalButton(
                onClick = onRetry,
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("firebase_progress_retry_button"),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CloudOff,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "تعذّر الاتصال • إعادة",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun MilestoneStepLabel(
    label: String,
    reached: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        if (reached) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = WorldMapPalette.GoldHighlight,
                modifier = Modifier.size(12.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (reached) FontWeight.Bold else FontWeight.Normal,
            color = if (reached) WorldMapPalette.GoldHighlight else Color.White.copy(alpha = 0.65f)
        )
    }
}
