package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.CefrLevel
import com.example.model.MindMapBranch
import com.example.ui.theme.BranchAccentColors
import com.example.ui.theme.EnglishFontFamily
import com.example.ui.theme.LevelAccentColors

fun levelAccentColor(level: CefrLevel): Color = when (level) {
    CefrLevel.A1 -> LevelAccentColors.A1
    CefrLevel.A2 -> LevelAccentColors.A2
    CefrLevel.B1 -> LevelAccentColors.B1
    CefrLevel.B2 -> LevelAccentColors.B2
    CefrLevel.C1 -> LevelAccentColors.C1
    CefrLevel.C2 -> LevelAccentColors.C2
}

fun branchAccentColor(branch: MindMapBranch): Color = when (branch) {
    MindMapBranch.HUB -> LevelAccentColors.B1
    MindMapBranch.GRAMMAR -> BranchAccentColors.Grammar
    MindMapBranch.SPEAKING -> BranchAccentColors.Speaking
    MindMapBranch.PHONETICS -> BranchAccentColors.Phonetics
    MindMapBranch.VOCAB -> BranchAccentColors.Vocab
    MindMapBranch.STORY -> BranchAccentColors.Story
}

@Composable
fun EnglishLtrText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    maxLines: Int = Int.MAX_VALUE
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Text(
            text = text,
            modifier = modifier,
            style = style.copy(fontFamily = EnglishFontFamily),
            color = color,
            fontWeight = fontWeight,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CefrBadge(
    level: CefrLevel,
    modifier: Modifier = Modifier
) {
    val accent = levelAccentColor(level)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = accent.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Text(
            text = level.code,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = accent
        )
    }
}

@Composable
fun AudioPlayIconButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    val pulseTransition = rememberInfiniteTransition(label = "audio_pulse")
    val scale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPlaying) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(650),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .scale(if (isPlaying) scale else 1f)
            .testTag(testTag),
        shape = CircleShape,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = if (isPlaying) tint else tint.copy(alpha = 0.12f),
            contentColor = if (isPlaying) Color.White else tint
        )
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Filled.Stop else Icons.Filled.VolumeUp,
            contentDescription = stringResource(
                if (isPlaying) R.string.cd_stop_audio else R.string.cd_play_audio
            ),
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun SpeedToggleChip(
    isSlow: Boolean,
    onToggle: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSlow,
        onClick = onToggle,
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .testTag(testTag),
        label = {
            Text(
                text = stringResource(if (isSlow) R.string.speed_slow else R.string.speed_normal),
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = if (isSlow) Icons.Filled.GraphicEq else Icons.Filled.Speed,
                contentDescription = stringResource(R.string.cd_speed_toggle),
                modifier = Modifier.size(18.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    )
}

@Composable
fun SectionHeaderRow(
    title: String,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.weight(1f)) {
            androidx.compose.foundation.layout.Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(12.dp))
            trailing()
        }
    }
}
