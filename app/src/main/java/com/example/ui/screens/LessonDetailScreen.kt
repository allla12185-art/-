package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.data.QuizQuestionRecord
import com.example.data.QuizResultRecord
import com.example.model.Lesson
import com.example.ui.components.AudioPlayIconButton
import com.example.ui.components.CefrBadge
import com.example.ui.components.EnglishLtrText
import com.example.ui.components.SpeedToggleChip
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.viewmodel.LessonQuizQuestionsState
import com.example.viewmodel.QuizSubmissionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    lesson: Lesson,
    isCompleted: Boolean,
    bookmarkedSentenceIds: Set<String>,
    audioState: AudioPlaybackState,
    quizQuestionsState: LessonQuizQuestionsState,
    quizSubmissionState: QuizSubmissionState,
    savedQuizResult: QuizResultRecord?,
    onEnsureQuizLoaded: () -> Unit,
    onRetryQuizLoad: () -> Unit,
    onSubmitQuiz: (correctCount: Int, totalCount: Int) -> Unit,
    onResetQuizForRetake: () -> Unit,
    onBack: () -> Unit,
    onToggleComplete: () -> Unit,
    onMarkCompleted: () -> Unit,
    onToggleBookmark: (String) -> Unit,
    onSpeak: (String, String) -> Unit,
    onSpeakSequence: (String, List<Pair<String, String>>) -> Unit,
    onToggleSpeed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember(lesson.id) { mutableIntStateOf(0) }
    val selectedQuizAnswers = remember(lesson.id) { mutableStateMapOf<String, Int>() }

    LaunchedEffect(lesson.id) {
        onEnsureQuizLoaded()
    }

    val firestoreQuestions = remember(quizQuestionsState, lesson.id) {
        when (quizQuestionsState) {
            is LessonQuizQuestionsState.Ready -> {
                if (quizQuestionsState.questions.isNotEmpty()) {
                    quizQuestionsState.questions
                } else {
                    lesson.quiz.mapIndexed { idx, q ->
                        QuizQuestionRecord(
                            id = q.id,
                            lessonId = lesson.id,
                            orderIndex = idx.toLong(),
                            promptAr = q.promptAr,
                            englishPrompt = q.englishPrompt,
                            options = q.options,
                            correctIndex = q.correctIndex.toLong(),
                            explanationAr = q.explanationAr,
                            levelCode = q.level.code
                        )
                    }
                }
            }
            else -> lesson.quiz.mapIndexed { idx, q ->
                QuizQuestionRecord(
                    id = q.id,
                    lessonId = lesson.id,
                    orderIndex = idx.toLong(),
                    promptAr = q.promptAr,
                    englishPrompt = q.englishPrompt,
                    options = q.options,
                    correctIndex = q.correctIndex.toLong(),
                    explanationAr = q.explanationAr,
                    levelCode = q.level.code
                )
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = lesson.titleAr,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1
                        )
                        EnglishLtrText(
                            text = lesson.titleEn,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("lesson_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleComplete,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("lesson_detail_complete_button")
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = stringResource(R.string.cd_complete_lesson),
                            tint = if (isCompleted) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Audio control & level bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CefrBadge(lesson.level)
                    Text(
                        text = "${lesson.durationMinutes} دقيقة",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                SpeedToggleChip(
                    isSlow = audioState.isSlowSpeed,
                    onToggle = onToggleSpeed,
                    testTag = "lesson_speed_toggle"
                )
            }

            TabRow(selectedTabIndex = selectedTab) {
                listOf("الشرح", "الحوار", "القصة", "الاختبار").forEachIndexed { idx, label ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("lesson_tab_$idx"),
                        text = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selectedTab == 0) {
                    // Golden Tip Banner
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 740.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                                Column {
                                    Text(
                                        text = "القاعدة الذهبية للدرس",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Text(
                                        text = lesson.goldenTipAr,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }
                    }

                    // Grammar Points
                    itemsIndexed(lesson.grammarPoints) { gIdx, gp ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 740.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = gp.titleAr,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                EnglishLtrText(
                                    text = gp.titleEn,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = gp.simpleRuleAr,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                // Formula pill
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                                ) {
                                    EnglishLtrText(
                                        text = gp.formula,
                                        modifier = Modifier.padding(12.dp),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Common Arab learner mistake alert
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.WarningAmber,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "انتبه لخطأ شائع: ${gp.commonArabMistakeAr}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Text(
                                    text = "أمثلة ناطقة (اضغط للاستماع):",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                gp.examples.forEachIndexed { exIdx, ex ->
                                    val utterId = "${lesson.id}_gp_${gIdx}_$exIdx"
                                    SentenceAudioRow(
                                        english = ex.english,
                                        transliteration = ex.transliterationAr,
                                        arabic = ex.arabic,
                                        isPlaying = audioState.activeItemId == utterId,
                                        isBookmarked = ex.id in bookmarkedSentenceIds,
                                        onPlay = { onSpeak(utterId, ex.english) },
                                        onBookmark = { onToggleBookmark(ex.id) },
                                        testTag = "play_grammar_ex_${gIdx}_$exIdx"
                                    )
                                }
                            }
                        }
                    }
                } else if (selectedTab == 1) {
                    // Play Full Dialogue Header
                    item {
                        val seqId = "${lesson.id}_dialogue_all"
                        val dialogueItems = lesson.dialogue.mapIndexed { idx, line ->
                            "${lesson.id}_dl_$idx" to line.english
                        }
                        val isSequencePlaying = audioState.activeItemId?.startsWith("${lesson.id}_dl_") == true ||
                            audioState.activeItemId == seqId

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 740.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "حوار الموقف الواقعي",
                                style = MaterialTheme.typography.titleLarge
                            )
                            FilledTonalButton(
                                onClick = { onSpeakSequence(seqId, dialogueItems) },
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("play_full_dialogue_button")
                            ) {
                                Icon(
                                    imageVector = if (isSequencePlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isSequencePlaying) "إيقاف الحوار" else "استمع للحوار كاملاً")
                            }
                        }
                    }

                    // Dialogue lines
                    itemsIndexed(lesson.dialogue) { idx, line ->
                        val itemId = "${lesson.id}_dl_$idx"
                        val isPlaying = audioState.activeItemId == itemId
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 740.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (idx % 2 == 0) {
                                    MaterialTheme.colorScheme.surface
                                } else {
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                                }
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AudioPlayIconButton(
                                    isPlaying = isPlaying,
                                    onClick = { onSpeak(itemId, line.english) },
                                    testTag = "play_dialogue_line_$idx"
                                )
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = line.speaker,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    EnglishLtrText(
                                        text = line.english,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "النطق: ${line.transliterationAr}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = line.arabic,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Essential categorized sentences
                    item {
                        Text(
                            text = "جمل أساسية للحفظ والممارسة",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 740.dp)
                                .padding(top = 6.dp)
                        )
                    }

                    itemsIndexed(lesson.sentences) { idx, item ->
                        val utterId = "${lesson.id}_sent_$idx"
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 740.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                SentenceAudioRow(
                                    english = item.english,
                                    transliteration = item.transliterationAr,
                                    arabic = item.arabic,
                                    isPlaying = audioState.activeItemId == utterId,
                                    isBookmarked = item.id in bookmarkedSentenceIds,
                                    onPlay = { onSpeak(utterId, item.english) },
                                    onBookmark = { onToggleBookmark(item.id) },
                                    testTag = "play_sentence_$idx"
                                )
                            }
                        }
                    }
                } else if (selectedTab == 2) {
                    // Story Card
                    item {
                        val storySeqId = "${lesson.id}_story_all"
                        val storyItems = lesson.story.paragraphs.mapIndexed { idx, p ->
                            "${lesson.id}_st_$idx" to p.first
                        }
                        val isStoryPlaying = audioState.activeItemId?.startsWith("${lesson.id}_st_") == true ||
                            audioState.activeItemId == storySeqId

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 740.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "قصة قصيرة: ${lesson.story.titleAr}",
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        EnglishLtrText(
                                            text = lesson.story.titleEn,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    FilledTonalButton(
                                        onClick = { onSpeakSequence(storySeqId, storyItems) },
                                        modifier = Modifier
                                            .defaultMinSize(minHeight = 48.dp)
                                            .testTag("play_story_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isStoryPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isStoryPlaying) "إيقاف" else "اسمع القصة")
                                    }
                                }

                                lesson.story.paragraphs.forEachIndexed { idx, (en, ar) ->
                                    val pId = "${lesson.id}_st_$idx"
                                    val playing = audioState.activeItemId == pId
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (playing) {
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            AudioPlayIconButton(
                                                isPlaying = playing,
                                                onClick = { onSpeak(pId, en) },
                                                testTag = "play_story_paragraph_$idx"
                                            )
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                EnglishLtrText(
                                                    text = en,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = ar,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }

                                Text(
                                    text = "الخلاصة: ${lesson.story.moralOrTipAr}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }

                    // CTA to jump straight to post-lesson Quick Quiz tab
                    item {
                        Button(
                            onClick = { selectedTab = 3 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 740.dp)
                                .defaultMinSize(minHeight = 52.dp)
                                .testTag("go_to_quiz_tab_button")
                        ) {
                            Icon(Icons.Filled.Quiz, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ابدأ الاختبار السريع لتقييم فهمك")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    }
                } else {
                    // Dedicated Post-Lesson Quick Quiz Tab (Firestore Questions + Instant Comprehension Evaluation)
                    item {
                        val isCloudSynced = quizQuestionsState is LessonQuizQuestionsState.Ready &&
                            quizQuestionsState.questions.isNotEmpty()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 740.dp)
                                .testTag("quiz_header_card"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.48f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Quiz,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "الاختبار السريع للدرس",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.surface
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.CloudDone,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = if (isCloudSynced) SuccessGreen else MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = if (isCloudSynced) "مرتبط بـ Firebase" else "تحضير الأسئلة",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isCloudSynced) SuccessGreen else MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                val answeredCount = firestoreQuestions.count { it.id in selectedQuizAnswers }
                                val answerProgress = if (firestoreQuestions.isNotEmpty()) {
                                    answeredCount.toFloat() / firestoreQuestions.size.toFloat()
                                } else 0f

                                Text(
                                    text = "أجب عن ${firestoreQuestions.size} أسئلة لتحصل على تقييم فوري لمستوى فهمك وحفظ نتيجتك في حسابك.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                LinearProgressIndicator(
                                    progress = { answerProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                )

                                Text(
                                    text = "تمت الإجابة على $answeredCount من ${firestoreQuestions.size} أسئلة",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                if (savedQuizResult != null && quizSubmissionState !is QuizSubmissionState.Evaluated) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("previous_quiz_result_banner")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "آخر نتيجة محفوظة: ${savedQuizResult.scorePercent}% • ${savedQuizResult.masteryLabelAr}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "أفضل: ${savedQuizResult.bestScorePercent}%",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (quizQuestionsState is LessonQuizQuestionsState.Error) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = 740.dp)
                                    .testTag("quiz_questions_error_banner"),
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = quizQuestionsState.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                    FilledTonalButton(
                                        onClick = onRetryQuizLoad,
                                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                                    ) {
                                        Text("إعادة المحاولة")
                                    }
                                }
                            }
                        }
                    }

                    val isLockedAfterSubmit = quizSubmissionState is QuizSubmissionState.Evaluated

                    itemsIndexed(firestoreQuestions, key = { _, q -> q.id }) { qIdx, q ->
                        val chosen = selectedQuizAnswers[q.id]
                        val correctIdx = q.correctIndex.toInt()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 740.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
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
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = "سؤال ${qIdx + 1} من ${firestoreQuestions.size}",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(text = q.promptAr, style = MaterialTheme.typography.titleMedium)
                                EnglishLtrText(
                                    text = q.englishPrompt,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )

                                q.options.forEachIndexed { optIdx, option ->
                                    val isPicked = chosen == optIdx
                                    val isCorrect = optIdx == correctIdx
                                    val bgColor = when {
                                        chosen == null -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                        isCorrect -> SuccessGreenContainer
                                        isPicked && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                    }
                                    val borderColor = when {
                                        chosen == null -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                        isCorrect -> SuccessGreen
                                        isPicked && !isCorrect -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .defaultMinSize(minHeight = 48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable(enabled = !isLockedAfterSubmit) {
                                                selectedQuizAnswers[q.id] = optIdx
                                            }
                                            .testTag("quiz_${q.id}_opt_$optIdx"),
                                        shape = RoundedCornerShape(12.dp),
                                        color = bgColor,
                                        border = BorderStroke(1.2.dp, borderColor)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            EnglishLtrText(
                                                text = option,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                if (chosen != null) {
                                    Text(
                                        text = if (chosen == correctIdx) {
                                            "أحسنت! ${q.explanationAr}"
                                        } else {
                                            "الإجابة الصحيحة هي: ${q.options.getOrNull(correctIdx).orEmpty()} • ${q.explanationAr}"
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (chosen == correctIdx) SuccessGreen else MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    // Instant Evaluation Card or Submit Button
                    item {
                        val answeredCount = firestoreQuestions.count { it.id in selectedQuizAnswers }
                        val correctCount = firestoreQuestions.count { q ->
                            selectedQuizAnswers[q.id] == q.correctIndex.toInt()
                        }
                        val allAnswered = firestoreQuestions.isNotEmpty() && answeredCount == firestoreQuestions.size

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 740.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            when (quizSubmissionState) {
                                is QuizSubmissionState.Evaluated -> {
                                    InstantQuizEvaluationCard(
                                        result = quizSubmissionState.result,
                                        onRetake = {
                                            selectedQuizAnswers.clear()
                                            onResetQuizForRetake()
                                        },
                                        onFinishAndReturn = {
                                            if (quizSubmissionState.result.passed) {
                                                onMarkCompleted()
                                            }
                                            onBack()
                                        }
                                    )
                                }
                                is QuizSubmissionState.Error -> {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.errorContainer,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = quizSubmissionState.message,
                                            modifier = Modifier.padding(12.dp),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                    Button(
                                        onClick = { onSubmitQuiz(correctCount, firestoreQuestions.size) },
                                        enabled = allAnswered,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .defaultMinSize(minHeight = 52.dp)
                                            .testTag("submit_lesson_quiz_button")
                                    ) {
                                        Text("إعادة إرسال النتيجة للتقييم الفوري")
                                    }
                                }
                                else -> {
                                    val isSubmitting = quizSubmissionState is QuizSubmissionState.Submitting
                                    Button(
                                        onClick = { onSubmitQuiz(correctCount, firestoreQuestions.size) },
                                        enabled = allAnswered && !isSubmitting,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .defaultMinSize(minHeight = 52.dp)
                                            .testTag("submit_lesson_quiz_button")
                                    ) {
                                        if (isSubmitting) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                strokeWidth = 2.dp,
                                                color = MaterialTheme.colorScheme.onPrimary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("جاري حفظ النتيجة وحساب التقييم...")
                                        } else {
                                            Icon(Icons.Filled.EmojiEvents, contentDescription = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                if (allAnswered) {
                                                    "اعتمد الإجابات واعرض التقييم الفوري"
                                                } else {
                                                    "أجب عن كل الأسئلة أولاً (${answeredCount}/${firestoreQuestions.size})"
                                                }
                                            )
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            onMarkCompleted()
                                            onBack()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .defaultMinSize(minHeight = 48.dp)
                                            .testTag("finish_lesson_button")
                                    ) {
                                        Icon(Icons.Filled.CheckCircle, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(if (isCompleted) "تم إتقان الدرس • العودة" else "تحديد الدرس كمكتمل والعودة")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InstantQuizEvaluationCard(
    result: QuizResultRecord,
    onRetake: () -> Unit,
    onFinishAndReturn: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (result.scorePercent.toFloat() / 100f).coerceIn(0f, 1f),
        label = "quiz_score_progress"
    )
    val accentColor = if (result.passed) SuccessGreen else MaterialTheme.colorScheme.error
    val containerColor = if (result.passed) {
        SuccessGreenContainer.copy(alpha = 0.72f)
    } else {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quiz_evaluation_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.5.dp, accentColor)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (result.passed) Icons.Filled.EmojiEvents else Icons.Filled.WarningAmber,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(30.dp)
                    )
                    Column {
                        Text(
                            text = "التقييم الفوري لمستوى الفهم",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = result.masteryLabelAr,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = "${result.scorePercent}%",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                }
            }

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = accentColor,
                trackColor = MaterialTheme.colorScheme.surface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EvaluationStatPill(
                    label = "الإجابات الصحيحة",
                    value = "${result.correctCount} من ${result.totalCount}",
                    modifier = Modifier.weight(1f)
                )
                EvaluationStatPill(
                    label = "أفضل نتيجة",
                    value = "${result.bestScorePercent}%",
                    modifier = Modifier.weight(1f)
                )
                EvaluationStatPill(
                    label = "المحاولات",
                    value = "${result.attemptCount}",
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = if (result.passed) {
                    "تم حفظ نتيجتك وتوثيق اجتياز الدرس تلقائياً في Firebase."
                } else {
                    "يُنصح بمراجعة تبويب (الشرح) ثم إعادة المحاولة للوصول إلى 60% فأكثر."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRetake,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("retake_quiz_button")
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إعادة الاختبار")
                }

                Button(
                    onClick = onFinishAndReturn,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("finish_lesson_button")
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("حفظ والعودة")
                }
            }
        }
    }
}

@Composable
private fun EvaluationStatPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SentenceAudioRow(
    english: String,
    transliteration: String,
    arabic: String,
    isPlaying: Boolean,
    isBookmarked: Boolean,
    onPlay: () -> Unit,
    onBookmark: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AudioPlayIconButton(
            isPlaying = isPlaying,
            onClick = onPlay,
            testTag = testTag
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            EnglishLtrText(
                text = english,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "النطق: $transliteration",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = arabic,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            onClick = onBookmark,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                contentDescription = stringResource(R.string.cd_bookmark),
                tint = if (isBookmarked) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
