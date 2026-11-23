package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AudioPlaybackState
import com.example.model.MinimalPair
import com.example.model.PhoneticCategory
import com.example.model.PhoneticSound
import com.example.ui.components.AudioPlayIconButton
import com.example.ui.components.EnglishLtrText
import com.example.ui.components.SectionHeaderRow
import com.example.ui.components.SpeedToggleChip
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer

@Composable
fun PhoneticsScreen(
    sounds: List<PhoneticSound>,
    minimalPairs: List<MinimalPair>,
    selectedCategory: PhoneticCategory?,
    audioState: AudioPlaybackState,
    onSelectCategory: (PhoneticCategory?) -> Unit,
    onSpeak: (String, String) -> Unit,
    onToggleSpeed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) }
    var earChallengeIndex by remember { mutableIntStateOf(0) }
    var earChallengeTargetIsA by remember { mutableStateOf(true) }
    var earChallengeFeedback by remember { mutableStateOf<String?>(null) }

    val filteredSounds = if (selectedCategory == null) {
        sounds
    } else {
        sounds.filter { it.category == selectedCategory }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionHeaderRow(
                title = "مختبر الصوتيات والنطق",
                subtitle = "أتقن النطق الأمريكي الصحيح وتخلّص من الأخطاء الشائعة",
                trailing = {
                    SpeedToggleChip(
                        isSlow = audioState.isSlowSpeed,
                        onToggle = onToggleSpeed,
                        testTag = "phonetics_speed_toggle"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp)
            )

            if (audioState.ttsUnavailable) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 760.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = "محرك النطق الصوتي غير متوفر حالياً على هذا الجهاز، لكن يمكنك قراءة رموز النطق والتدريبات كاملة.",
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        TabRow(selectedTabIndex = activeTab) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("phonetics_tab_sounds"),
                text = { Text("الأصوات والقواعد الصوتية") }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("phonetics_tab_pairs"),
                text = { Text("أزواج التشابه وتحدي الأذن") }
            )
        }

        if (activeTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 760.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { onSelectCategory(null) },
                            modifier = Modifier
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("phonetics_cat_all"),
                            label = { Text("كل الأصوات") }
                        )
                        PhoneticCategory.entries.forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { onSelectCategory(cat) },
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("phonetics_cat_${cat.name.lowercase()}"),
                                label = { Text(cat.titleAr) }
                            )
                        }
                    }
                }

                items(filteredSounds, key = { it.id }) { sound ->
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = sound.nameAr,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    EnglishLtrText(
                                        text = sound.nameEn,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    EnglishLtrText(
                                        text = sound.symbol,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }

                            Text(
                                text = "طريقة النطق: ${sound.howToPronounceAr}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = "تجنّب هذا الخطأ: ${sound.commonMistakeAr}",
                                    modifier = Modifier.padding(10.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            sound.examples.forEachIndexed { idx, ex ->
                                val wordId = "${sound.id}_w_$idx"
                                val sentId = "${sound.id}_s_$idx"
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        AudioPlayIconButton(
                                            isPlaying = audioState.activeItemId == wordId,
                                            onClick = { onSpeak(wordId, ex.word.replace("->", " ")) },
                                            testTag = "play_sound_${sound.id}_word_$idx"
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                EnglishLtrText(
                                                    text = ex.word,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                EnglishLtrText(
                                                    text = ex.ipa,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            Text(
                                                text = "النطق التقريبي: ${ex.arabicApprox}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                            EnglishLtrText(
                                                text = ex.sentence,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = ex.meaningAr,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        AudioPlayIconButton(
                                            isPlaying = audioState.activeItemId == sentId,
                                            onClick = { onSpeak(sentId, ex.sentence) },
                                            testTag = "play_sound_${sound.id}_sent_$idx",
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            val activePair = minimalPairs[earChallengeIndex % minimalPairs.size]
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Ear Challenge Game
                item {
                    val targetWord = if (earChallengeTargetIsA) activePair.wordA else activePair.wordB
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 760.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Hearing,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "تحدي الأذن الصوتية (Minimal Pairs)",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Text(
                                text = "استمع للكلمة ثم اختر أيهما سمعت بالضبط:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Button(
                                onClick = {
                                    earChallengeFeedback = null
                                    onSpeak("ear_challenge_target", targetWord)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("ear_challenge_listen_button")
                            ) {
                                Icon(Icons.Filled.VolumeUp, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("اسمع الكلمة المطلوبة")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        earChallengeFeedback = if (earChallengeTargetIsA) {
                                            "صحيح! الكلمة هي ${activePair.wordA} (${activePair.meaningA})"
                                        } else {
                                            "ليست تماماً — الكلمة المنطوقة كانت ${activePair.wordB} (${activePair.meaningB}). استمع مرة أخرى!"
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .defaultMinSize(minHeight = 48.dp)
                                        .testTag("ear_choice_a")
                                ) {
                                    EnglishLtrText(
                                        text = "${activePair.wordA} ${activePair.ipaA}",
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        earChallengeFeedback = if (!earChallengeTargetIsA) {
                                            "صحيح! الكلمة هي ${activePair.wordB} (${activePair.meaningB})"
                                        } else {
                                            "ليست تماماً — الكلمة المنطوقة كانت ${activePair.wordA} (${activePair.meaningA}). استمع مرة أخرى!"
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .defaultMinSize(minHeight = 48.dp)
                                        .testTag("ear_choice_b")
                                ) {
                                    EnglishLtrText(
                                        text = "${activePair.wordB} ${activePair.ipaB}",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            earChallengeFeedback?.let { fb ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (fb.startsWith("صحيح")) SuccessGreenContainer else MaterialTheme.colorScheme.surface
                                ) {
                                    Text(
                                        text = fb,
                                        modifier = Modifier.padding(10.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (fb.startsWith("صحيح")) SuccessGreen else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    earChallengeIndex = (earChallengeIndex + 1) % minimalPairs.size
                                    earChallengeTargetIsA = (earChallengeIndex % 2 == 0)
                                    earChallengeFeedback = null
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("ear_next_pair_button")
                            ) {
                                Icon(Icons.Filled.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("زوج صوتي جديد")
                            }
                        }
                    }
                }

                // Minimal pairs reference list
                items(minimalPairs, key = { it.id }) { pair ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 760.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "مقارنة: ${pair.soundA}  مقابل  ${pair.soundB}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MinimalPairWordTile(
                                    word = pair.wordA,
                                    ipa = pair.ipaA,
                                    meaning = pair.meaningA,
                                    isPlaying = audioState.activeItemId == "${pair.id}_a",
                                    onPlay = { onSpeak("${pair.id}_a", pair.wordA) },
                                    testTag = "play_pair_${pair.id}_a",
                                    modifier = Modifier.weight(1f)
                                )
                                MinimalPairWordTile(
                                    word = pair.wordB,
                                    ipa = pair.ipaB,
                                    meaning = pair.meaningB,
                                    isPlaying = audioState.activeItemId == "${pair.id}_b",
                                    onPlay = { onSpeak("${pair.id}_b", pair.wordB) },
                                    testTag = "play_pair_${pair.id}_b",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Text(
                                text = pair.tipAr,
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

@Composable
private fun MinimalPairWordTile(
    word: String,
    ipa: String,
    meaning: String,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AudioPlayIconButton(
                isPlaying = isPlaying,
                onClick = onPlay,
                testTag = testTag
            )
            Column(modifier = Modifier.weight(1f)) {
                EnglishLtrText(
                    text = word,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                EnglishLtrText(
                    text = ipa,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = meaning,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
