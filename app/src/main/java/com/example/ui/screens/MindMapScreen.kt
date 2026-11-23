package com.example.ui.screens

import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.data.DailyWordRecord
import com.example.data.UserProgress
import com.example.model.MindMapNode
import com.example.model.StudyMode
import com.example.ui.components.CefrBadge
import com.example.ui.components.DailyReminderDialog
import com.example.ui.components.DailyWordHomeCard
import com.example.ui.components.EnglishLtrText
import com.example.viewmodel.DailyWordUiState
import com.example.ui.components.FloatingBalloonBadge
import com.example.ui.components.JourneyNodeState
import com.example.ui.components.MentorRobotMascot
import com.example.ui.components.SteppingStoneButton
import com.example.ui.components.WoodenStageSign
import com.example.ui.components.drawAdventureScenery
import com.example.ui.components.drawWindingAdventurePath
import com.example.ui.theme.WorldMapPalette
import kotlin.math.roundToInt

@Composable
fun MindMapScreen(
    nodes: List<MindMapNode>,
    totalLessons: Int,
    userProgress: UserProgress,
    onToggleStudyMode: (StudyMode) -> Unit,
    onOpenLesson: (String) -> Unit,
    onOpenPhonetics: () -> Unit,
    onOpenPlacementTest: () -> Unit,
    onOpenStudyPlan: () -> Unit,
    onOpenMentor: () -> Unit,
    onUpdateDailyReminder: (Boolean, Int, Int) -> Unit,
    onSendTestReminder: () -> Boolean,
    dailyWordState: DailyWordUiState,
    onListenDailyWord: (DailyWordRecord) -> Unit,
    onSpeakText: (String, String) -> Unit,
    onToggleDailyWordPracticed: (DailyWordRecord) -> Unit,
    onSwitchDailyWord: (DailyWordRecord) -> Unit,
    onRetryDailyWord: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine node completion states along the journey path
    fun isNodeCompleted(node: MindMapNode): Boolean = when (node.lessonId) {
        null -> false
        "placement_test" -> userProgress.assessedLevel != null
        "phonetics_studio" -> userProgress.completedLessonIds.size >= 4
        else -> node.lessonId in userProgress.completedLessonIds
    }

    val currentNode = nodes.firstOrNull { !isNodeCompleted(it) } ?: nodes.last()
    var selectedNode by remember { mutableStateOf(currentNode) }
    var showReminderDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Scroll toward bottom (Start / Level 1) on first open so user begins at Level 1 like a game journey
    LaunchedEffect(Unit) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    val completedCount = userProgress.completedLessonIds.size
    val progressPercent = if (totalLessons > 0) {
        ((completedCount.toFloat() / totalLessons.toFloat()) * 100f).roundToInt().coerceIn(0, 100)
    } else 0
    val shimmerTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "water_shimmer")
    val waterShimmerPhase by shimmerTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(2200),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "shimmer_phase"
    )

    Column(modifier = modifier.fillMaxSize()) {
        // 1. Gamified Top HUD ("رحلة الإنجليزية" + Stars + Diamonds + Profile)
        AdventureTopHud(
            stars = userProgress.stars,
            diamonds = userProgress.diamonds,
            levelLabel = userProgress.assessedLevel?.let { "${it.titleAr} - $progressPercent%" }
                ?: "المستوى الأول - $progressPercent%",
            progressPercent = progressPercent / 100f,
            studyMode = userProgress.studyMode,
            dailyReminderEnabled = userProgress.dailyReminderEnabled,
            onProfileClick = onOpenStudyPlan,
            onReminderClick = { showReminderDialog = true },
            onToggleStudyMode = {
                val next = if (userProgress.studyMode == StudyMode.NORMAL) {
                    StudyMode.HEAVY
                } else {
                    StudyMode.NORMAL
                }
                onToggleStudyMode(next)
            }
        )

        // 2. Daily Word of the Day ("كلمة اليوم" synced via Firebase Firestore)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(WorldMapPalette.SkyTop.copy(alpha = 0.25f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            DailyWordHomeCard(
                dailyWordState = dailyWordState,
                onListenWord = onListenDailyWord,
                onSpeakExample = onSpeakText,
                onTogglePracticed = onToggleDailyWordPracticed,
                onSwitchWord = onSwitchDailyWord,
                onRetry = onRetryDailyWord,
                modifier = Modifier.widthIn(max = 680.dp)
            )
        }

        // 3. Scrollable Illustrated Adventure Map + Floating Action Card
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                val density = LocalDensity.current
                val mapWidth = maxWidth
                val mapHeight = 1160.dp
                val mapWidthPx = with(density) { mapWidth.toPx() }
                val mapHeightPx = with(density) { mapHeight.toPx() }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(mapHeight)
                ) {
                    val pathPoints = remember(nodes, mapWidthPx, mapHeightPx) {
                        nodes.map { node ->
                            Offset(
                                x = node.xFraction * mapWidthPx,
                                y = node.yFraction * mapHeightPx
                            )
                        }
                    }
                    val completedLeadingSegments = remember(nodes, userProgress) {
                        nodes.indexOfFirst { !isNodeCompleted(it) }.let { idx ->
                            if (idx == -1) nodes.size - 1 else idx
                        }
                    }

                    // Illustrated 3D Landscape + Winding Path Canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawAdventureScenery(waterShimmerPhase = waterShimmerPhase)
                        drawWindingAdventurePath(
                            points = pathPoints,
                            completedSegmentCount = completedLeadingSegments
                        )
                    }

                    // Floating ABC Balloons & "Hello!" Bubble
                    FloatingBalloonBadge(
                        letter = "A",
                        balloonColor = WorldMapPalette.BalloonRed,
                        modifier = Modifier.offset {
                            IntOffset((mapWidthPx * 0.14f).roundToInt(), (mapHeightPx * 0.11f).roundToInt())
                        }
                    )
                    FloatingBalloonBadge(
                        letter = "B",
                        balloonColor = WorldMapPalette.BalloonYellow,
                        modifier = Modifier.offset {
                            IntOffset((mapWidthPx * 0.25f).roundToInt(), (mapHeightPx * 0.13f).roundToInt())
                        }
                    )
                    FloatingBalloonBadge(
                        letter = "C",
                        balloonColor = WorldMapPalette.BalloonPurple,
                        modifier = Modifier.offset {
                            IntOffset((mapWidthPx * 0.34f).roundToInt(), (mapHeightPx * 0.16f).roundToInt())
                        }
                    )

                    Surface(
                        modifier = Modifier.offset {
                            IntOffset((mapWidthPx * 0.42f).roundToInt(), (mapHeightPx * 0.11f).roundToInt())
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        shadowElevation = 3.dp,
                        border = BorderStroke(1.5.dp, WorldMapPalette.NodeCompletedTop)
                    ) {
                        EnglishLtrText(
                            text = "Hello!",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = WorldMapPalette.BannerNavy
                        )
                    }

                    // Fluency City Ribbon Banner at Top-Right
                    Surface(
                        modifier = Modifier
                            .offset {
                                IntOffset((mapWidthPx * 0.61f).roundToInt(), (mapHeightPx * 0.125f).roundToInt())
                            }
                            .clickable { onOpenMentor() }
                            .testTag("fluency_city_banner"),
                        shape = RoundedCornerShape(12.dp),
                        color = WorldMapPalette.GoldProgress,
                        shadowElevation = 4.dp,
                        border = BorderStroke(2.dp, WorldMapPalette.WoodBorder)
                    ) {
                        Text(
                            text = "مدينة الطلاقة",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = WorldMapPalette.SignTextDark
                        )
                    }

                    // Wooden Signboards for the 3 Stages
                    WoodenStageSign(
                        title = "المستوى الأول: البداية والتأسيس",
                        modifier = Modifier.offset {
                            IntOffset((mapWidthPx * 0.06f).roundToInt(), (mapHeightPx * 0.73f).roundToInt())
                        }
                    )
                    WoodenStageSign(
                        title = "المستوى الثاني: القراءة والمحادثة",
                        modifier = Modifier.offset {
                            IntOffset((mapWidthPx * 0.42f).roundToInt(), (mapHeightPx * 0.37f).roundToInt())
                        }
                    )
                    WoodenStageSign(
                        title = "المستوى الثالث: المتقدم",
                        modifier = Modifier.offset {
                            IntOffset((mapWidthPx * 0.06f).roundToInt(), (mapHeightPx * 0.095f).roundToInt())
                        }
                    )

                    // Stepping-Stone Nodes + Mentor Robot Mascot on Current Active Node
                    nodes.forEach { node ->
                        val nodeCenterX = node.xFraction * mapWidthPx
                        val nodeCenterY = node.yFraction * mapHeightPx
                        val state = when {
                            isNodeCompleted(node) -> JourneyNodeState.COMPLETED
                            node.id == currentNode.id -> JourneyNodeState.CURRENT
                            else -> JourneyNodeState.UPCOMING
                        }
                        val nodeWidthPx = with(density) { 126.dp.toPx() }
                        val nodeTopOffsetPx = with(density) { 38.dp.toPx() }

                        if (state == JourneyNodeState.CURRENT) {
                            val robotW = with(density) { 64.dp.toPx() }
                            val robotH = with(density) { 64.dp.toPx() }
                            MentorRobotMascot(
                                modifier = Modifier.offset {
                                    IntOffset(
                                        (nodeCenterX - robotW / 2f).roundToInt(),
                                        (nodeCenterY - nodeTopOffsetPx - robotH * 0.72f).roundToInt()
                                    )
                                }
                            )
                            // "الدرس الحالي!" signpost next to the robot
                            Surface(
                                modifier = Modifier.offset {
                                    IntOffset(
                                        (nodeCenterX + robotW * 0.55f).coerceAtMost(mapWidthPx - 190f).roundToInt(),
                                        (nodeCenterY - nodeTopOffsetPx - 14f).roundToInt()
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = WorldMapPalette.WoodSign,
                                shadowElevation = 3.dp,
                                border = BorderStroke(1.5.dp, WorldMapPalette.WoodBorder)
                            ) {
                                Text(
                                    text = "الدرس الحالي!",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = WorldMapPalette.SignTextDark
                                )
                            }
                        }

                        SteppingStoneButton(
                            label = node.titleAr,
                            state = state,
                            isSelected = selectedNode.id == node.id,
                            onClick = { selectedNode = node },
                            testTag = "mindmap_node_${node.id}",
                            modifier = Modifier
                                .width(126.dp)
                                .offset {
                                    IntOffset(
                                        (nodeCenterX - nodeWidthPx / 2f).roundToInt(),
                                        (nodeCenterY - nodeTopOffsetPx).roundToInt()
                                    )
                                }
                        )
                    }
                }
            }

            // Floating Selected Node Launch Card pinned cleanly at the bottom
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .fillMaxWidth()
                    .widthIn(max = 680.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, WorldMapPalette.NodeCompletedTop.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = selectedNode.titleAr,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            selectedNode.level?.let { CefrBadge(it) }
                        }
                        Text(
                            text = selectedNode.subtitleAr,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = {
                            when (selectedNode.lessonId) {
                                null -> onOpenMentor()
                                "placement_test" -> onOpenPlacementTest()
                                "phonetics_studio" -> onOpenPhonetics()
                                else -> onOpenLesson(selectedNode.lessonId!!)
                            }
                        },
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("mindmap_open_selected_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WorldMapPalette.NodeCompletedTop,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = when (selectedNode.lessonId) {
                                "placement_test" -> "ابدأ الاختبار"
                                "phonetics_studio" -> "افتح الصوتيات"
                                else -> "ابدأ الدرس"
                            },
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
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

    if (showReminderDialog) {
        DailyReminderDialog(
            enabled = userProgress.dailyReminderEnabled,
            hour = userProgress.dailyReminderHour,
            minute = userProgress.dailyReminderMinute,
            onUpdateReminder = onUpdateDailyReminder,
            onSendTestNotification = onSendTestReminder,
            onDismiss = { showReminderDialog = false }
        )
    }
}

@Composable
private fun AdventureTopHud(
    stars: Int,
    diamonds: Int,
    levelLabel: String,
    progressPercent: Float,
    studyMode: StudyMode,
    dailyReminderEnabled: Boolean,
    onProfileClick: () -> Unit,
    onReminderClick: () -> Unit,
    onToggleStudyMode: () -> Unit
) {
    Surface(
        color = WorldMapPalette.SkyTop,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Profile & Study Plan shortcut
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onProfileClick)
                            .testTag("hud_profile_button"),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(2.dp, WorldMapPalette.BannerNavy)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "خطة المذاكرة والملف",
                                tint = WorldMapPalette.BannerNavy,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    // Stars Pill
                    HudCounterBadge(
                        icon = Icons.Filled.Star,
                        iconTint = WorldMapPalette.GoldStar,
                        countText = "$stars",
                        testTag = "hud_stars_badge"
                    )

                    // Diamonds Pill
                    HudCounterBadge(
                        icon = Icons.Filled.Diamond,
                        iconTint = WorldMapPalette.DiamondCyan,
                        countText = "$diamonds",
                        testTag = "hud_diamonds_badge"
                    )
                }

                // Study Mode Quick Switch (25m vs 5m) + Daily Reminder Bell
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onReminderClick)
                            .testTag("hud_reminder_button"),
                        shape = RoundedCornerShape(14.dp),
                        color = if (dailyReminderEnabled) WorldMapPalette.GoldProgress else Color.White,
                        border = BorderStroke(2.dp, WorldMapPalette.BannerNavy)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (dailyReminderEnabled) {
                                    Icons.Filled.NotificationsActive
                                } else {
                                    Icons.Outlined.Notifications
                                },
                                contentDescription = androidx.compose.ui.res.stringResource(com.example.R.string.cd_daily_reminder),
                                tint = WorldMapPalette.BannerNavy,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = onToggleStudyMode,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("hud_study_mode_button"),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (studyMode == StudyMode.NORMAL) Icons.Filled.Timer else Icons.Filled.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (studyMode == StudyMode.NORMAL) "25 د" else "5 د",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Center 3D Glossy Navy Banner: "رحلة الإنجليزية" + Beveled Golden Progress Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(18.dp),
                shadowElevation = 8.dp,
                border = BorderStroke(2.2.dp, Color.White.copy(alpha = 0.8f)),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    WorldMapPalette.BannerNavyLight,
                                    WorldMapPalette.BannerNavy,
                                    WorldMapPalette.BannerNavyDark
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 9.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "رحلة الإنجليزية",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(23.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.30f))
                            .border(1.2.dp, Color.White.copy(alpha = 0.45f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progressPercent.coerceAtLeast(0.14f))
                                    .height(19.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                WorldMapPalette.GoldHighlight,
                                                WorldMapPalette.GoldProgress,
                                                WorldMapPalette.GoldStarBorder
                                            )
                                        )
                                    )
                            )
                        }
                        Text(
                            text = levelLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HudCounterBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    countText: String,
    testTag: String
) {
    Surface(
        modifier = Modifier.testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        color = WorldMapPalette.BannerNavyDark.copy(alpha = 0.85f),
        border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = countText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}
