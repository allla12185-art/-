package com.example.ui.screens

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AudioPlaybackState
import com.example.data.UserProgress
import com.example.model.CefrLevel
import com.example.model.MentorMessage
import com.example.model.QuizQuestion
import com.example.model.StudyMode
import com.example.model.StudyRule
import com.example.ui.components.AudioPlayIconButton
import com.example.ui.components.CefrBadge
import com.example.ui.components.DailyReminderCard
import com.example.ui.components.EnglishLtrText
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.viewmodel.AdviceUiState
import com.example.viewmodel.AiUiState

@Composable
fun MentorScreen(
    messages: List<MentorMessage>,
    aiUiState: AiUiState,
    adviceUiState: AdviceUiState,
    audioState: AudioPlaybackState,
    userProgress: UserProgress,
    totalLessons: Int,
    nextLessonTitle: String?,
    studyRules: List<StudyRule>,
    weeklyQuestions: List<String>,
    placementQuestions: List<QuizQuestion>,
    activeTab: Int,
    onSelectTab: (Int) -> Unit,
    onRefreshAdvice: (Boolean) -> Unit,
    onSpeak: (String, String) -> Unit,
    onSendMessage: (String) -> Unit,
    onSetStudyMode: (StudyMode) -> Unit,
    onToggleCanDo: (String) -> Unit,
    onSavePlacementScore: (Int) -> CefrLevel,
    onFilterLessonsByLevel: (CefrLevel) -> Unit,
    userDisplayName: String?,
    userEmail: String?,
    onSignOut: () -> Unit,
    onUpdateDailyReminder: (Boolean, Int, Int) -> Unit,
    onSendTestReminder: () -> Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = activeTab) {
            Tab(
                selected = activeTab == 0,
                onClick = { onSelectTab(0) },
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("mentor_tab_ai"),
                icon = { Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp)) },
                text = { Text("المرشد الذكي") }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { onSelectTab(1) },
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("mentor_tab_placement"),
                icon = { Icon(Icons.Filled.Quiz, contentDescription = null, modifier = Modifier.size(18.dp)) },
                text = { Text("تحديد المستوى") }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { onSelectTab(2) },
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("mentor_tab_plan"),
                icon = { Icon(Icons.Filled.Route, contentDescription = null, modifier = Modifier.size(18.dp)) },
                text = { Text("خطة المذاكرة") }
            )
        }

        when (activeTab) {
            0 -> AiMentorChatTab(
                messages = messages,
                aiUiState = aiUiState,
                adviceUiState = adviceUiState,
                audioState = audioState,
                userProgress = userProgress,
                totalLessons = totalLessons,
                nextLessonTitle = nextLessonTitle,
                onRefreshAdvice = onRefreshAdvice,
                onSpeak = onSpeak,
                onSendMessage = onSendMessage
            )
            1 -> PlacementTestTab(
                questions = placementQuestions,
                assessedLevel = userProgress.assessedLevel,
                onSavePlacementScore = onSavePlacementScore,
                onStartLevelLessons = onFilterLessonsByLevel
            )
            else -> StudyPlanTab(
                userProgress = userProgress,
                studyRules = studyRules,
                weeklyQuestions = weeklyQuestions,
                userDisplayName = userDisplayName,
                userEmail = userEmail,
                onSetStudyMode = onSetStudyMode,
                onToggleCanDo = onToggleCanDo,
                onSignOut = onSignOut,
                onUpdateDailyReminder = onUpdateDailyReminder,
                onSendTestReminder = onSendTestReminder
            )
        }
    }
}

@Composable
private fun AiMentorChatTab(
    messages: List<MentorMessage>,
    aiUiState: AiUiState,
    adviceUiState: AdviceUiState,
    audioState: AudioPlaybackState,
    userProgress: UserProgress,
    totalLessons: Int,
    nextLessonTitle: String?,
    onRefreshAdvice: (Boolean) -> Unit,
    onSpeak: (String, String) -> Unit,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val isBusy = aiUiState is AiUiState.Loading || aiUiState is AiUiState.Streaming

    LaunchedEffect(Unit) {
        if (adviceUiState is AdviceUiState.Idle) {
            onRefreshAdvice(false)
        }
    }

    LaunchedEffect(messages.size, aiUiState) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size)
        }
    }

    val nextPrompt = if (nextLessonTitle != null) {
        "درّبني بتمارين قصيرة تمهيداً لدرسي التالي: $nextLessonTitle"
    } else {
        "اختبرني في مراجعة شاملة لمستوى الإتقان C2"
    }

    val quickPrompts = listOf(
        nextPrompt,
        "صحّح جملتي: I very like learn English every days",
        "اعمل معي حواراً واقعياً يناسب مستواي الحالي خطوة بخطوة",
        "أعطني نصيحة عملية لتثبيت النطق والمفردات اليوم"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 760.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item(key = "personalized_advice_card") {
                PersonalizedAdviceCard(
                    adviceUiState = adviceUiState,
                    audioState = audioState,
                    userProgress = userProgress,
                    totalLessons = totalLessons,
                    onRefresh = { onRefreshAdvice(true) },
                    onSpeak = onSpeak
                )
            }

            items(messages, key = { it.id }) { msg ->
                MentorBubble(
                    text = msg.text,
                    isUser = msg.isUser,
                    isError = msg.isError
                )
            }

            if (aiUiState is AiUiState.Loading) {
                item(key = "loading_indicator") {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Text("المرشد يكتب الرد...", style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else if (aiUiState is AiUiState.Streaming && aiUiState.partialReply.isNotBlank()) {
                item(key = "streaming_bubble") {
                    MentorBubble(
                        text = aiUiState.partialReply,
                        isUser = false,
                        isError = false
                    )
                }
            }
        }

        // Quick starter prompt chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickPrompts.forEachIndexed { idx, prompt ->
                AssistChip(
                    onClick = { if (!isBusy) onSendMessage(prompt) },
                    modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("mentor_quick_prompt_$idx"),
                    label = {
                        Text(
                            text = when (idx) {
                                0 -> "تمرين للدرس التالي"
                                1 -> "صحّح لي جملة"
                                2 -> "محادثة حسب مستواي"
                                else -> "نصيحة نطق وتثبيت"
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("mentor_input_field"),
                placeholder = {
                    Text(
                        text = stringResource(R.string.ai_mentor_hint),
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                maxLines = 3,
                shape = RoundedCornerShape(18.dp)
            )

            FilledIconButton(
                onClick = {
                    val toSend = inputText
                    inputText = ""
                    onSendMessage(toSend)
                },
                enabled = inputText.isNotBlank() && !isBusy,
                modifier = Modifier
                    .size(52.dp)
                    .testTag("mentor_send_button"),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.cd_send_message)
                )
            }
        }
    }
}

@Composable
private fun PersonalizedAdviceCard(
    adviceUiState: AdviceUiState,
    audioState: AudioPlaybackState,
    userProgress: UserProgress,
    totalLessons: Int,
    onRefresh: () -> Unit,
    onSpeak: (String, String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f)
        ),
        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Column {
                        Text(
                            text = "نصيحة المرشد لتقدمك اليوم",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "الدروس المنجزة: ${userProgress.completedLessonIds.size} / $totalLessons • ${userProgress.studyMode.titleAr}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
                userProgress.assessedLevel?.let { CefrBadge(it) }
                IconButton(
                    onClick = onRefresh,
                    enabled = adviceUiState !is AdviceUiState.Loading,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("mentor_refresh_advice_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "تحديث النصيحة حسب تقدمي",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            when (adviceUiState) {
                AdviceUiState.Idle -> {
                    Button(
                        onClick = onRefresh,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("mentor_get_advice_button")
                    ) {
                        Text("اطلب نصيحة مخصصة لمستواك الآن")
                    }
                }
                AdviceUiState.Loading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Text(
                            text = "المرشد يحلل تقدمك لإعداد نصيحة مخصصة...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
                is AdviceUiState.Error -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = adviceUiState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            OutlinedButton(
                                onClick = onRefresh,
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("mentor_advice_retry_button")
                            ) {
                                Text("إعادة المحاولة")
                            }
                        }
                    }
                }
                is AdviceUiState.Success -> {
                    val advice = adviceUiState.advice
                    Text(
                        text = advice.headlineAr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = advice.personalizedTipAr,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AudioPlayIconButton(
                                isPlaying = audioState.activeItemId == "mentor_advice_sentence",
                                onClick = { onSpeak("mentor_advice_sentence", advice.practiceSentenceEn) },
                                testTag = "mentor_advice_play_audio"
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                EnglishLtrText(
                                    text = advice.practiceSentenceEn,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = advice.practiceHintAr,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Text(
                        text = "الخطوة القادمة: ${advice.nextStepActionAr}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun MentorBubble(
    text: String,
    isUser: Boolean,
    isError: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 560.dp),
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            ),
            color = when {
                isError -> MaterialTheme.colorScheme.errorContainer
                isUser -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surface
            },
            border = if (!isUser && !isError) {
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))
            } else null
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    isError -> MaterialTheme.colorScheme.onSurface
                    isUser -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
private fun PlacementTestTab(
    questions: List<QuizQuestion>,
    assessedLevel: CefrLevel?,
    onSavePlacementScore: (Int) -> CefrLevel,
    onStartLevelLessons: (CefrLevel) -> Unit
) {
    val answers = remember { mutableStateMapOf<String, Int>() }
    var submittedLevel by remember { mutableStateOf<CefrLevel?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "اختبار تحديد المستوى السريع (A1 — C2)",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "أجب عن 6 أسئلة متدرجة ليحدد لك المرشد نقطة البداية الأنسب في الخارطة الذهنية.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    (submittedLevel ?: assessedLevel)?.let { lvl ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "مستواك المسجل حالياً:",
                                style = MaterialTheme.typography.labelLarge
                            )
                            CefrBadge(lvl)
                            Text(lvl.titleAr, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }

        itemsIndexed(questions) { idx, q ->
            val picked = answers[q.id]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(q.promptAr, style = MaterialTheme.typography.titleSmall)
                        CefrBadge(q.level)
                    }
                    EnglishLtrText(
                        text = q.englishPrompt,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    q.options.forEachIndexed { optIdx, opt ->
                        val isSelected = picked == optIdx
                        val showResult = submittedLevel != null
                        val isCorrect = optIdx == q.correctIndex

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (submittedLevel == null) answers[q.id] = optIdx
                                }
                                .testTag("pt_q${idx}_opt$optIdx"),
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                showResult && isCorrect -> SuccessGreenContainer
                                showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            },
                            border = BorderStroke(
                                1.2.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                EnglishLtrText(
                                    text = opt,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (submittedLevel != null) {
                        Text(
                            text = q.explanationAr,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (picked == q.correctIndex) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val score = questions.count { answers[it.id] == it.correctIndex }
                        submittedLevel = onSavePlacementScore(score)
                    },
                    enabled = answers.size == questions.size,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 52.dp)
                        .testTag("placement_submit_button")
                ) {
                    Text(
                        if (answers.size < questions.size) {
                            "أجب عن كل الأسئلة (${answers.size}/${questions.size})"
                        } else {
                            "اعرض مستواي واحفظ النتيجة"
                        }
                    )
                }

                submittedLevel?.let { lvl ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "نتيجتك المقترحة: ${lvl.code} • ${lvl.titleAr}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = lvl.subtitleAr,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Button(
                                onClick = { onStartLevelLessons(lvl) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("placement_go_to_lessons_button")
                            ) {
                                Text("انتقل إلى دروس ${lvl.code}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyPlanTab(
    userProgress: UserProgress,
    studyRules: List<StudyRule>,
    weeklyQuestions: List<String>,
    userDisplayName: String?,
    userEmail: String?,
    onSetStudyMode: (StudyMode) -> Unit,
    onToggleCanDo: (String) -> Unit,
    onSignOut: () -> Unit,
    onUpdateDailyReminder: (Boolean, Int, Int) -> Unit,
    onSendTestReminder: () -> Boolean
) {
    var selectedCanDoLevel by remember {
        mutableStateOf(userProgress.assessedLevel ?: CefrLevel.A1)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = userDisplayName?.takeIf { it.isNotBlank() } ?: "متعلّم رفيق الإنجليزية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (!userEmail.isNullOrBlank()) {
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "حساب متزامن مع Firebase • ${userProgress.completedLessonIds.size} درس مكتمل",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedButton(
                        onClick = onSignOut,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("sign_out_button")
                    ) {
                        Text("تسجيل الخروج")
                    }
                }
            }
        }

        item {
            DailyReminderCard(
                enabled = userProgress.dailyReminderEnabled,
                hour = userProgress.dailyReminderHour,
                minute = userProgress.dailyReminderMinute,
                onUpdateReminder = onUpdateDailyReminder,
                onSendTestNotification = onSendTestReminder,
                modifier = Modifier.widthIn(max = 760.dp)
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("اختر نمط يومك", style = MaterialTheme.typography.titleLarge)
                    StudyMode.entries.forEach { mode ->
                        val active = userProgress.studyMode == mode
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSetStudyMode(mode) }
                                .testTag("plan_mode_${mode.name.lowercase()}"),
                            shape = RoundedCornerShape(14.dp),
                            color = if (active) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            },
                            border = BorderStroke(
                                1.5.dp,
                                if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = mode.titleAr,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = mode.descriptionAr,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "القواعد الذهبية الخمس للمذاكرة",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp)
            )
        }

        items(studyRules, key = { it.number }) { rule ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "${rule.number}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(rule.titleAr, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = rule.bodyAr,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Can-Do Checklist
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("قائمة الأهداف (Can-Do)", style = MaterialTheme.typography.titleLarge)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CefrLevel.entries.forEach { lvl ->
                            val selected = selectedCanDoLevel == lvl
                            Surface(
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedCanDoLevel = lvl }
                                    .testTag("cando_level_${lvl.code.lowercase()}"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Text(
                                    text = lvl.code,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) {
                                        MaterialTheme.colorScheme.onSecondary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }

                    selectedCanDoLevel.canDoGoals.forEachIndexed { idx, goal ->
                        val goalId = "${selectedCanDoLevel.code}_goal_$idx"
                        val checked = goalId in userProgress.completedCanDoIds
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onToggleCanDo(goalId) }
                                .padding(horizontal = 6.dp, vertical = 6.dp)
                                .testTag("cando_item_$goalId"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (checked) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                                contentDescription = null,
                                tint = if (checked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = goal,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Weekly check-in card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "وقفة المراجعة الأسبوعية (3 أسئلة)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    weeklyQuestions.forEachIndexed { idx, q ->
                        Text(
                            text = "${idx + 1}. $q",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
}
