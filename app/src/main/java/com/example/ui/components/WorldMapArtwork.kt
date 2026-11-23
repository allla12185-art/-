package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WorldMapPalette

fun DrawScope.drawAdventureScenery(waterShimmerPhase: Float) {
    val w = size.width
    val h = size.height

    // 1. Atmospheric 3D Sky & Sunlight Glow
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                WorldMapPalette.SkyTop,
                WorldMapPalette.SkyMid,
                WorldMapPalette.SkyBottom,
                WorldMapPalette.HillHighlight
            ),
            startY = 0f,
            endY = h * 0.25f
        ),
        size = Size(w, h * 0.27f)
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(WorldMapPalette.SunGlow, Color.Transparent),
            center = Offset(w * 0.14f, h * 0.03f),
            radius = 160.dp.toPx()
        ),
        center = Offset(w * 0.14f, h * 0.03f),
        radius = 160.dp.toPx()
    )

    // 2. Volumetric 3D Meadow Base + Elevated Terraced Cliffs
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                WorldMapPalette.HillHighlight,
                WorldMapPalette.HillBright,
                WorldMapPalette.HillMid,
                WorldMapPalette.HillBright
            ),
            startY = h * 0.14f,
            endY = h
        ),
        topLeft = Offset(0f, h * 0.14f),
        size = Size(w, h * 0.86f)
    )

    // 3D Clouds with shaded bottoms
    draw3DCloud(Offset(w * 0.17f, h * 0.048f), 26.dp.toPx())
    draw3DCloud(Offset(w * 0.56f, h * 0.032f), 22.dp.toPx())

    // 3D Faceted Mountains with Lit Ridge + Shaded Ridge + Snow Caps
    draw3DMountain(Offset(w * 0.12f, h * 0.155f), 118.dp.toPx(), 84.dp.toPx())
    draw3DMountain(Offset(w * 0.25f, h * 0.16f), 88.dp.toPx(), 62.dp.toPx())
    draw3DMountain(Offset(w * 0.90f, h * 0.156f), 104.dp.toPx(), 72.dp.toPx())

    // 3D Terraced Hill Ledges (depth cliff shadow + lit grass top)
    drawTerracedHill(
        topLeft = Offset(-w * 0.14f, h * 0.25f),
        size = Size(w * 0.86f, h * 0.14f)
    )
    drawTerracedHill(
        topLeft = Offset(w * 0.34f, h * 0.57f),
        size = Size(w * 0.86f, h * 0.16f)
    )
    drawTerracedHill(
        topLeft = Offset(-w * 0.18f, h * 0.78f),
        size = Size(w * 0.78f, h * 0.15f)
    )

    // 3D Recessed River Gorge with Shimmering Animated Water Ripples
    val riverPath = Path().apply {
        moveTo(-w * 0.06f, h * 0.495f)
        cubicTo(
            w * 0.28f, h * 0.44f,
            w * 0.66f, h * 0.545f,
            w * 1.06f, h * 0.455f
        )
    }
    // Outer green bank cliff shadow
    drawPath(
        path = riverPath,
        color = WorldMapPalette.RiverBankShadow,
        style = Stroke(width = 44.dp.toPx(), cap = StrokeCap.Round)
    )
    // Deep water trench
    drawPath(
        path = riverPath,
        color = WorldMapPalette.RiverDeep,
        style = Stroke(width = 36.dp.toPx(), cap = StrokeCap.Round)
    )
    // Main bright water surface
    drawPath(
        path = riverPath,
        color = WorldMapPalette.RiverBlue,
        style = Stroke(width = 27.dp.toPx(), cap = StrokeCap.Round)
    )
    // Specular water highlight & animated shimmer wave
    drawPath(
        path = riverPath,
        color = WorldMapPalette.RiverHighlight.copy(alpha = 0.5f + 0.25f * waterShimmerPhase),
        style = Stroke(width = 11.dp.toPx(), cap = StrokeCap.Round)
    )
    drawPath(
        path = riverPath,
        color = WorldMapPalette.RiverFoam.copy(alpha = 0.45f + 0.35f * (1f - waterShimmerPhase)),
        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
    )

    // Lower-right tributary stream
    val lowerStream = Path().apply {
        moveTo(w * 0.63f, h * 0.995f)
        cubicTo(
            w * 0.78f, h * 0.93f,
            w * 0.90f, h * 0.90f,
            w * 1.06f, h * 0.86f
        )
    }
    drawPath(lowerStream, WorldMapPalette.RiverDeep, style = Stroke(width = 27.dp.toPx(), cap = StrokeCap.Round))
    drawPath(lowerStream, WorldMapPalette.RiverBlue, style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round))
    drawPath(lowerStream, WorldMapPalette.RiverFoam.copy(alpha = 0.65f), style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))

    // 3D Arched Wooden Bridge with Cast Water Shadow
    draw3DWoodenBridge(Offset(w * 0.48f, h * 0.485f), 50.dp.toPx(), 58.dp.toPx())

    // 3D Trees with radial canopy lighting & ground ambient shadows
    draw3DPineTree(Offset(w * 0.10f, h * 0.22f), 28.dp.toPx())
    draw3DPineTree(Offset(w * 0.89f, h * 0.26f), 29.dp.toPx())
    draw3DPineTree(Offset(w * 0.11f, h * 0.62f), 30.dp.toPx())
    draw3DPineTree(Offset(w * 0.89f, h * 0.40f), 25.dp.toPx())
    draw3DLeafyTree(Offset(w * 0.90f, h * 0.72f), 25.dp.toPx())
    draw3DLeafyTree(Offset(w * 0.09f, h * 0.83f), 27.dp.toPx())

    // 3D Graduation Caps & Books with Ground Shadows
    draw3DGradCap(Offset(w * 0.14f, h * 0.34f), 17.dp.toPx())
    draw3DGradCap(Offset(w * 0.86f, h * 0.56f), 17.dp.toPx())
    draw3DBook(Offset(w * 0.16f, h * 0.73f), 17.dp.toPx(), WorldMapPalette.BalloonRed)
    draw3DBook(Offset(w * 0.85f, h * 0.84f), 17.dp.toPx(), WorldMapPalette.NodeCompletedTop)

    // 3D Fluency City Castle at Summit
    draw3DFluencyCastle(Offset(w * 0.80f, h * 0.088f), 68.dp.toPx())
}

private fun DrawScope.draw3DCloud(center: Offset, radius: Float) {
    // Shaded base
    drawOval(
        color = WorldMapPalette.CloudShade,
        topLeft = Offset(center.x - radius * 1.45f, center.y + radius * 0.15f),
        size = Size(radius * 2.9f, radius * 0.85f)
    )
    drawCircle(WorldMapPalette.CloudWhite, radius * 0.72f, center + Offset(-radius * 0.68f, radius * 0.10f))
    drawCircle(WorldMapPalette.CloudWhite, radius * 0.98f, center)
    drawCircle(WorldMapPalette.CloudWhite, radius * 0.74f, center + Offset(radius * 0.72f, radius * 0.12f))
}

private fun DrawScope.draw3DMountain(baseCenter: Offset, width: Float, height: Float) {
    val peak = Offset(baseCenter.x, baseCenter.y - height)
    val left = Offset(baseCenter.x - width / 2f, baseCenter.y)
    val right = Offset(baseCenter.x + width / 2f, baseCenter.y)
    val ridgeBottom = Offset(baseCenter.x + width * 0.04f, baseCenter.y)

    // Sunlit left face
    val litFace = Path().apply {
        moveTo(peak.x, peak.y)
        lineTo(ridgeBottom.x, ridgeBottom.y)
        lineTo(left.x, left.y)
        close()
    }
    drawPath(
        path = litFace,
        brush = Brush.verticalGradient(
            colors = listOf(WorldMapPalette.MountainLit, WorldMapPalette.MountainSlate),
            startY = peak.y,
            endY = baseCenter.y
        )
    )

    // Shaded right face
    val shadowFace = Path().apply {
        moveTo(peak.x, peak.y)
        lineTo(right.x, right.y)
        lineTo(ridgeBottom.x, ridgeBottom.y)
        close()
    }
    drawPath(shadowFace, WorldMapPalette.MountainShadow)

    // Volumetric Snow Cap (lit left + cool blue right)
    val snowLeft = Path().apply {
        moveTo(peak.x, peak.y)
        lineTo(peak.x + width * 0.02f, peak.y + height * 0.30f)
        lineTo(peak.x - width * 0.19f, peak.y + height * 0.38f)
        close()
    }
    drawPath(snowLeft, WorldMapPalette.SnowCap)
    val snowRight = Path().apply {
        moveTo(peak.x, peak.y)
        lineTo(peak.x + width * 0.19f, peak.y + height * 0.38f)
        lineTo(peak.x + width * 0.02f, peak.y + height * 0.30f)
        close()
    }
    drawPath(snowRight, WorldMapPalette.SnowShade)
}

private fun DrawScope.drawTerracedHill(topLeft: Offset, size: Size) {
    // Drop cliff rim underneath
    drawOval(
        color = WorldMapPalette.HillCliffShadow.copy(alpha = 0.25f),
        topLeft = topLeft + Offset(0f, 8.dp.toPx()),
        size = size
    )
    // Highlighted grass shelf
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(
                WorldMapPalette.HillHighlight.copy(alpha = 0.45f),
                WorldMapPalette.HillDeep.copy(alpha = 0.24f)
            ),
            startY = topLeft.y,
            endY = topLeft.y + size.height
        ),
        topLeft = topLeft,
        size = size
    )
}

private fun DrawScope.draw3DWoodenBridge(center: Offset, width: Float, height: Float) {
    val left = center.x - width / 2f
    val top = center.y - height / 2f

    // Cast shadow onto water beneath bridge
    drawRoundRect(
        color = WorldMapPalette.RiverBankShadow.copy(alpha = 0.45f),
        topLeft = Offset(left - 2.dp.toPx(), top + 7.dp.toPx()),
        size = Size(width + 8.dp.toPx(), height + 4.dp.toPx()),
        cornerRadius = CornerRadius(8.dp.toPx())
    )

    // Bridge frame base
    drawRoundRect(
        color = WorldMapPalette.WoodBorder,
        topLeft = Offset(left - 3.dp.toPx(), top - 2.dp.toPx()),
        size = Size(width + 6.dp.toPx(), height + 4.dp.toPx()),
        cornerRadius = CornerRadius(6.dp.toPx())
    )

    // Cylindrical plank shading gradient
    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                WorldMapPalette.WoodPlankDark,
                WorldMapPalette.WoodHighlight,
                WorldMapPalette.WoodSign,
                WorldMapPalette.WoodPlankDark
            ),
            startX = left,
            endX = left + width
        ),
        topLeft = Offset(left, top),
        size = Size(width, height),
        cornerRadius = CornerRadius(4.dp.toPx())
    )

    // Plank grooves + highlight lips
    val planks = 5
    val step = height / planks
    for (i in 1 until planks) {
        val y = top + i * step
        drawLine(WorldMapPalette.WoodBorder, Offset(left, y), Offset(left + width, y), strokeWidth = 2.dp.toPx())
        drawLine(WorldMapPalette.WoodHighlight.copy(alpha = 0.65f), Offset(left, y + 1.5.dp.toPx()), Offset(left + width, y + 1.5.dp.toPx()), strokeWidth = 1.dp.toPx())
    }

    // 3D Side Rails & Corner Posts
    listOf(left - 2.dp.toPx(), left + width + 2.dp.toPx()).forEach { railX ->
        drawLine(
            color = WorldMapPalette.WoodBorder,
            start = Offset(railX, top),
            end = Offset(railX, top + height),
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = WorldMapPalette.WoodHighlight,
            start = Offset(railX - 1.dp.toPx(), top),
            end = Offset(railX - 1.dp.toPx(), top + height),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawCircle(WorldMapPalette.WoodSign, 4.dp.toPx(), Offset(railX, top))
        drawCircle(WorldMapPalette.WoodSign, 4.dp.toPx(), Offset(railX, top + height))
    }
}

private fun DrawScope.draw3DPineTree(base: Offset, scale: Float) {
    // Ground ambient shadow
    drawOval(
        color = WorldMapPalette.GroundShadow,
        topLeft = Offset(base.x - scale * 0.65f, base.y - scale * 0.05f),
        size = Size(scale * 1.35f, scale * 0.34f)
    )
    // 3D cylindrical trunk
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(WorldMapPalette.TreeTrunk, WorldMapPalette.TreeTrunkDark),
            startX = base.x - scale * 0.15f,
            endX = base.x + scale * 0.15f
        ),
        topLeft = Offset(base.x - scale * 0.15f, base.y - scale * 0.32f),
        size = Size(scale * 0.30f, scale * 0.45f)
    )
    val lower = Path().apply {
        moveTo(base.x, base.y - scale * 1.12f)
        lineTo(base.x + scale * 0.68f, base.y - scale * 0.25f)
        lineTo(base.x - scale * 0.68f, base.y - scale * 0.25f)
        close()
    }
    drawPath(
        path = lower,
        brush = Brush.horizontalGradient(
            colors = listOf(WorldMapPalette.TreeLight, WorldMapPalette.TreeDark),
            startX = base.x - scale * 0.68f,
            endX = base.x + scale * 0.68f
        )
    )
    val upper = Path().apply {
        moveTo(base.x, base.y - scale * 1.62f)
        lineTo(base.x + scale * 0.52f, base.y - scale * 0.72f)
        lineTo(base.x - scale * 0.52f, base.y - scale * 0.72f)
        close()
    }
    drawPath(
        path = upper,
        brush = Brush.horizontalGradient(
            colors = listOf(WorldMapPalette.TreeHighlight, WorldMapPalette.TreeDark),
            startX = base.x - scale * 0.52f,
            endX = base.x + scale * 0.52f
        )
    )
}

private fun DrawScope.draw3DLeafyTree(base: Offset, scale: Float) {
    drawOval(
        color = WorldMapPalette.GroundShadow,
        topLeft = Offset(base.x - scale * 0.72f, base.y - scale * 0.04f),
        size = Size(scale * 1.44f, scale * 0.34f)
    )
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(WorldMapPalette.TreeTrunk, WorldMapPalette.TreeTrunkDark),
            startX = base.x - scale * 0.17f,
            endX = base.x + scale * 0.17f
        ),
        topLeft = Offset(base.x - scale * 0.17f, base.y - scale * 0.58f),
        size = Size(scale * 0.34f, scale * 0.68f)
    )
    val lobes = listOf(
        Offset(-scale * 0.28f, -scale * 0.85f) to scale * 0.56f,
        Offset(scale * 0.28f, -scale * 0.82f) to scale * 0.52f,
        Offset(0f, -scale * 1.18f) to scale * 0.66f
    )
    lobes.forEach { (offset, rad) ->
        val c = base + offset
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(WorldMapPalette.TreeHighlight, WorldMapPalette.TreeLight, WorldMapPalette.TreeDark),
                center = c + Offset(-rad * 0.3f, -rad * 0.3f),
                radius = rad * 1.4f
            ),
            radius = rad,
            center = c
        )
    }
}

private fun DrawScope.draw3DGradCap(center: Offset, sizePx: Float) {
    drawOval(
        color = WorldMapPalette.GroundShadow,
        topLeft = Offset(center.x - sizePx * 0.75f, center.y + sizePx * 0.35f),
        size = Size(sizePx * 1.5f, sizePx * 0.45f)
    )
    val base = Path().apply {
        moveTo(center.x - sizePx * 0.45f, center.y + sizePx * 0.1f)
        lineTo(center.x + sizePx * 0.45f, center.y + sizePx * 0.1f)
        lineTo(center.x + sizePx * 0.36f, center.y + sizePx * 0.42f)
        lineTo(center.x - sizePx * 0.36f, center.y + sizePx * 0.42f)
        close()
    }
    drawPath(base, WorldMapPalette.RobotTrimDark)
    val diamond = Path().apply {
        moveTo(center.x, center.y - sizePx * 0.45f)
        lineTo(center.x + sizePx * 0.85f, center.y)
        lineTo(center.x, center.y + sizePx * 0.45f)
        lineTo(center.x - sizePx * 0.85f, center.y)
        close()
    }
    drawPath(
        path = diamond,
        brush = Brush.linearGradient(
            colors = listOf(WorldMapPalette.RobotTrim, WorldMapPalette.RobotVisor),
            start = center + Offset(-sizePx, -sizePx),
            end = center + Offset(sizePx, sizePx)
        )
    )
    drawLine(
        color = WorldMapPalette.GoldStar,
        start = Offset(center.x + sizePx * 0.65f, center.y + sizePx * 0.08f),
        end = Offset(center.x + sizePx * 0.65f, center.y + sizePx * 0.58f),
        strokeWidth = 2.5.dp.toPx(),
        cap = StrokeCap.Round
    )
}

private fun DrawScope.draw3DBook(center: Offset, sizePx: Float, coverColor: Color) {
    drawOval(
        color = WorldMapPalette.GroundShadow,
        topLeft = Offset(center.x - sizePx * 0.72f, center.y + sizePx * 0.34f),
        size = Size(sizePx * 1.45f, sizePx * 0.42f)
    )
    // Extruded pages thickness
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(center.x - sizePx * 0.65f, center.y - sizePx * 0.35f),
        size = Size(sizePx * 1.32f, sizePx * 0.92f),
        cornerRadius = CornerRadius(3.dp.toPx())
    )
    // Colored top cover
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(coverColor, coverColor.copy(alpha = 0.78f)),
            start = center - Offset(sizePx, sizePx),
            end = center + Offset(sizePx, sizePx)
        ),
        topLeft = Offset(center.x - sizePx * 0.68f, center.y - sizePx * 0.52f),
        size = Size(sizePx * 1.30f, sizePx * 0.92f),
        cornerRadius = CornerRadius(3.dp.toPx())
    )
    drawLine(
        color = Color.White.copy(alpha = 0.85f),
        start = Offset(center.x - sizePx * 0.38f, center.y - sizePx * 0.16f),
        end = Offset(center.x + sizePx * 0.32f, center.y - sizePx * 0.16f),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round
    )
}

private fun DrawScope.draw3DFluencyCastle(center: Offset, scale: Float) {
    // Ground ambient shadow
    drawOval(
        color = WorldMapPalette.GroundShadow,
        topLeft = Offset(center.x - scale * 0.78f, center.y + scale * 0.40f),
        size = Size(scale * 1.56f, scale * 0.32f)
    )

    // Left Cylindrical Tower
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(WorldMapPalette.CastleWallShade, WorldMapPalette.CastleWall, WorldMapPalette.CastleWallShade),
            startX = center.x - scale * 0.62f,
            endX = center.x - scale * 0.28f
        ),
        topLeft = Offset(center.x - scale * 0.62f, center.y - scale * 0.10f),
        size = Size(scale * 0.34f, scale * 0.60f)
    )
    val leftRoof = Path().apply {
        moveTo(center.x - scale * 0.45f, center.y - scale * 0.50f)
        lineTo(center.x - scale * 0.24f, center.y - scale * 0.10f)
        lineTo(center.x - scale * 0.66f, center.y - scale * 0.10f)
        close()
    }
    drawPath(
        path = leftRoof,
        brush = Brush.horizontalGradient(
            colors = listOf(WorldMapPalette.CastleRoofPink, WorldMapPalette.CastleRoofPinkDark),
            startX = center.x - scale * 0.66f,
            endX = center.x - scale * 0.24f
        )
    )

    // Right Cylindrical Tower
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(WorldMapPalette.CastleWallShade, WorldMapPalette.CastleWall, WorldMapPalette.CastleWallShade),
            startX = center.x + scale * 0.28f,
            endX = center.x + scale * 0.62f
        ),
        topLeft = Offset(center.x + scale * 0.28f, center.y - scale * 0.10f),
        size = Size(scale * 0.34f, scale * 0.60f)
    )
    val rightRoof = Path().apply {
        moveTo(center.x + scale * 0.45f, center.y - scale * 0.50f)
        lineTo(center.x + scale * 0.66f, center.y - scale * 0.10f)
        lineTo(center.x + scale * 0.24f, center.y - scale * 0.10f)
        close()
    }
    drawPath(
        path = rightRoof,
        brush = Brush.horizontalGradient(
            colors = listOf(WorldMapPalette.CastleRoofPink, WorldMapPalette.CastleRoofPinkDark),
            startX = center.x + scale * 0.24f,
            endX = center.x + scale * 0.66f
        )
    )

    // Central 3D Keep
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(WorldMapPalette.CastleWallShade, Color.White, WorldMapPalette.CastleWallShade),
            startX = center.x - scale * 0.30f,
            endX = center.x + scale * 0.30f
        ),
        topLeft = Offset(center.x - scale * 0.30f, center.y - scale * 0.26f),
        size = Size(scale * 0.60f, scale * 0.76f)
    )
    val mainRoof = Path().apply {
        moveTo(center.x, center.y - scale * 0.78f)
        lineTo(center.x + scale * 0.36f, center.y - scale * 0.26f)
        lineTo(center.x - scale * 0.36f, center.y - scale * 0.26f)
        close()
    }
    drawPath(
        path = mainRoof,
        brush = Brush.horizontalGradient(
            colors = listOf(WorldMapPalette.CastleRoofBlue, WorldMapPalette.CastleRoofBlueDark),
            startX = center.x - scale * 0.36f,
            endX = center.x + scale * 0.36f
        )
    )

    // Golden Spire & Pennant Flag
    drawLine(
        color = WorldMapPalette.CastleGold,
        start = Offset(center.x, center.y - scale * 0.78f),
        end = Offset(center.x, center.y - scale * 0.96f),
        strokeWidth = 2.5.dp.toPx()
    )
    val flag = Path().apply {
        moveTo(center.x, center.y - scale * 0.96f)
        lineTo(center.x + scale * 0.22f, center.y - scale * 0.89f)
        lineTo(center.x, center.y - scale * 0.82f)
        close()
    }
    drawPath(flag, WorldMapPalette.GoldStar)

    // Castle Arched Golden Gate
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(WorldMapPalette.GoldHighlight, WorldMapPalette.CastleGold, WorldMapPalette.WoodBorder),
            startY = center.y + scale * 0.16f,
            endY = center.y + scale * 0.50f
        ),
        topLeft = Offset(center.x - scale * 0.13f, center.y + scale * 0.16f),
        size = Size(scale * 0.26f, scale * 0.34f),
        cornerRadius = CornerRadius(12.dp.toPx())
    )
}

fun DrawScope.drawWindingAdventurePath(
    points: List<Offset>,
    completedSegmentCount: Int
) {
    if (points.size < 2) return
    for (i in 0 until points.size - 1) {
        val start = points[i]
        val end = points[i + 1]
        val midY = (start.y + end.y) / 2f
        val seg = Path().apply {
            moveTo(start.x, start.y)
            cubicTo(start.x, midY, end.x, midY, end.x, end.y)
        }
        val isCompletedPath = i < completedSegmentCount
        // Outer drop shadow under stone path
        drawPath(
            path = seg,
            color = WorldMapPalette.GroundShadow,
            style = Stroke(width = 34.dp.toPx(), cap = StrokeCap.Round)
        )
        // Raised 3D path curb
        drawPath(
            path = seg,
            color = if (isCompletedPath) WorldMapPalette.NodeCompletedSide else WorldMapPalette.PathEdge,
            style = Stroke(width = 29.dp.toPx(), cap = StrokeCap.Round)
        )
        // Main path fill
        drawPath(
            path = seg,
            color = if (isCompletedPath) WorldMapPalette.PathCompleted else WorldMapPalette.PathSand,
            style = Stroke(width = 22.dp.toPx(), cap = StrokeCap.Round)
        )
        // Center sunlit 3D highlight ridge
        drawPath(
            path = seg,
            color = if (isCompletedPath) {
                WorldMapPalette.PathCompletedGlow.copy(alpha = 0.7f)
            } else {
                WorldMapPalette.PathHighlight.copy(alpha = 0.75f)
            },
            style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun FloatingBalloonBadge(
    letter: String,
    balloonColor: Color,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "balloon_$letter")
    val dy by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(2100), RepeatMode.Reverse),
        label = "balloon_y"
    )
    Column(
        modifier = modifier.offset(y = dy.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .shadow(5.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.85f), balloonColor, balloonColor.copy(alpha = 0.8f)),
                        center = Offset(10f, 10f),
                        radius = 45f
                    )
                )
                .border(1.5.dp, Color.White.copy(alpha = 0.75f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            )
        }
        Box(
            modifier = Modifier
                .width(1.5.dp)
                .height(15.dp)
                .background(WorldMapPalette.WoodBorder.copy(alpha = 0.65f))
        )
    }
}

@Composable
fun WoodenStageSign(
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.Transparent,
            shadowElevation = 6.dp,
            border = BorderStroke(2.2.dp, WorldMapPalette.WoodBorder)
        ) {
            Box(
                modifier = Modifier.background(
                    Brush.verticalGradient(
                        colors = listOf(
                            WorldMapPalette.WoodHighlight,
                            WorldMapPalette.WoodSign,
                            WorldMapPalette.WoodPlankDark
                        )
                    )
                )
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = WorldMapPalette.SignTextDark,
                    textAlign = TextAlign.Center
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            listOf(0, 1).forEach { _ ->
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(11.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(WorldMapPalette.WoodSign, WorldMapPalette.WoodBorder)
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun MentorRobotMascot(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "robot_bob")
    val bobY by transition.animateFloat(
        initialValue = -5f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(1150), RepeatMode.Reverse),
        label = "robot_y"
    )
    val shadowScale = 1f - ((bobY + 5f) / 36f)

    Box(modifier = modifier.size(72.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Ground shadow underneath floating robot
            drawOval(
                color = WorldMapPalette.GroundShadow,
                topLeft = Offset(
                    w * (0.5f - 0.25f * shadowScale),
                    h * 0.90f
                ),
                size = Size(w * 0.50f * shadowScale, h * 0.09f)
            )

            val yShift = bobY.dp.toPx()

            // 3D Headphone band
            drawArc(
                color = WorldMapPalette.RobotTrimDark,
                startAngle = 190f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = Offset(w * 0.13f, h * 0.04f + yShift),
                size = Size(w * 0.74f, h * 0.55f),
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = WorldMapPalette.RobotTrim,
                startAngle = 195f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(w * 0.14f, h * 0.05f + yShift),
                size = Size(w * 0.72f, h * 0.53f),
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3D Headphone Ear Cups
            listOf(w * 0.06f, w * 0.79f).forEach { cupX ->
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(WorldMapPalette.NodeActiveHighlight, WorldMapPalette.RobotTrim, WorldMapPalette.RobotTrimDark),
                        startY = h * 0.20f + yShift,
                        endY = h * 0.44f + yShift
                    ),
                    topLeft = Offset(cupX, h * 0.20f + yShift),
                    size = Size(w * 0.15f, h * 0.24f),
                    cornerRadius = CornerRadius(7.dp.toPx())
                )
            }

            // 3D Volumetric Rounded Helmet Head
            drawRoundRect(
                brush = Brush.radialGradient(
                    colors = listOf(WorldMapPalette.RobotHighlight, WorldMapPalette.RobotBody, WorldMapPalette.RobotShade),
                    center = Offset(w * 0.36f, h * 0.20f + yShift),
                    radius = w * 0.52f
                ),
                topLeft = Offset(w * 0.17f, h * 0.11f + yShift),
                size = Size(w * 0.66f, h * 0.43f),
                cornerRadius = CornerRadius(16.dp.toPx())
            )
            drawRoundRect(
                color = WorldMapPalette.RobotTrim,
                topLeft = Offset(w * 0.17f, h * 0.11f + yShift),
                size = Size(w * 0.66f, h * 0.43f),
                cornerRadius = CornerRadius(16.dp.toPx()),
                style = Stroke(width = 1.8.dp.toPx())
            )

            // Glossy Glass Visor
            drawRoundRect(
                color = WorldMapPalette.RobotVisor,
                topLeft = Offset(w * 0.24f, h * 0.18f + yShift),
                size = Size(w * 0.52f, h * 0.28f),
                cornerRadius = CornerRadius(10.dp.toPx())
            )
            // Specular visor reflection streak
            drawLine(
                color = Color.White.copy(alpha = 0.30f),
                start = Offset(w * 0.28f, h * 0.22f + yShift),
                end = Offset(w * 0.46f, h * 0.21f + yShift),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Glowing LED Eyes & Smile
            drawCircle(WorldMapPalette.RobotGlow, 3.2.dp.toPx(), Offset(w * 0.39f, h * 0.31f + yShift))
            drawCircle(WorldMapPalette.RobotGlow, 3.2.dp.toPx(), Offset(w * 0.61f, h * 0.31f + yShift))
            drawArc(
                color = WorldMapPalette.RobotGlow,
                startAngle = 15f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(w * 0.41f, h * 0.33f + yShift),
                size = Size(w * 0.18f, h * 0.10f),
                style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3D Volumetric Torso
            drawRoundRect(
                brush = Brush.radialGradient(
                    colors = listOf(WorldMapPalette.RobotHighlight, WorldMapPalette.RobotBody, WorldMapPalette.RobotShade),
                    center = Offset(w * 0.42f, h * 0.60f + yShift),
                    radius = w * 0.34f
                ),
                topLeft = Offset(w * 0.29f, h * 0.55f + yShift),
                size = Size(w * 0.42f, h * 0.29f),
                cornerRadius = CornerRadius(11.dp.toPx())
            )
            drawRoundRect(
                color = WorldMapPalette.RobotTrim,
                topLeft = Offset(w * 0.29f, h * 0.55f + yShift),
                size = Size(w * 0.42f, h * 0.29f),
                cornerRadius = CornerRadius(11.dp.toPx()),
                style = Stroke(width = 1.6.dp.toPx())
            )
            // Chest core emblem
            drawCircle(
                color = WorldMapPalette.RobotGlow,
                radius = 3.5.dp.toPx(),
                center = Offset(w * 0.50f, h * 0.67f + yShift)
            )

            // Waving 3D Arm
            drawLine(
                color = WorldMapPalette.RobotShade,
                start = Offset(w * 0.70f, h * 0.63f + yShift),
                end = Offset(w * 0.88f, h * 0.48f + yShift),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = WorldMapPalette.RobotHighlight,
                start = Offset(w * 0.70f, h * 0.62f + yShift),
                end = Offset(w * 0.88f, h * 0.47f + yShift),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // 3D Boots
            listOf(w * 0.32f, w * 0.54f).forEach { footX ->
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(WorldMapPalette.RobotTrim, WorldMapPalette.RobotTrimDark),
                        startY = h * 0.82f + yShift,
                        endY = h * 0.93f + yShift
                    ),
                    topLeft = Offset(footX, h * 0.82f + yShift),
                    size = Size(w * 0.14f, h * 0.11f),
                    cornerRadius = CornerRadius(5.dp.toPx())
                )
            }
        }
    }
}

enum class JourneyNodeState {
    COMPLETED,
    CURRENT,
    UPCOMING
}

@Composable
fun SteppingStoneButton(
    label: String,
    state: JourneyNodeState,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val highlightColor = when (state) {
        JourneyNodeState.COMPLETED -> WorldMapPalette.NodeCompletedHighlight
        JourneyNodeState.CURRENT -> WorldMapPalette.NodeActiveHighlight
        JourneyNodeState.UPCOMING -> WorldMapPalette.NodeUpcomingHighlight
    }
    val topColor = when (state) {
        JourneyNodeState.COMPLETED -> WorldMapPalette.NodeCompletedTop
        JourneyNodeState.CURRENT -> WorldMapPalette.NodeActiveTop
        JourneyNodeState.UPCOMING -> WorldMapPalette.NodeUpcomingTop
    }
    val sideColor = when (state) {
        JourneyNodeState.COMPLETED -> WorldMapPalette.NodeCompletedSide
        JourneyNodeState.CURRENT -> WorldMapPalette.NodeActiveSide
        JourneyNodeState.UPCOMING -> WorldMapPalette.NodeUpcomingSide
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(82.dp),
            contentAlignment = Alignment.Center
        ) {
            // Soft ground ambient shadow under the 3D pedestal
            Box(
                modifier = Modifier
                    .offset(y = 11.dp)
                    .size(width = 70.dp, height = 56.dp)
                    .clip(CircleShape)
                    .background(WorldMapPalette.GroundShadow)
            )

            if (state == JourneyNodeState.CURRENT || isSelected) {
                Box(
                    modifier = Modifier
                        .size(78.dp)
                        .clip(CircleShape)
                        .background(WorldMapPalette.NodeActiveGlow)
                )
            }

            // 3D Extruded Cylinder Rim with Horizontal Cylindrical Shading
            Box(
                modifier = Modifier
                    .offset(y = 6.dp)
                    .size(width = 64.dp, height = 56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(sideColor, topColor.copy(alpha = 0.85f), sideColor)
                        )
                    )
            )

            // Glossy 3D Domed Top Disc + Specular Highlight
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 58.dp, minHeight = 58.dp)
                    .size(width = 64.dp, height = 54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(highlightColor, topColor, sideColor),
                            center = Offset(32f, 20f),
                            radius = 95f
                        )
                    )
                    .border(
                        width = if (isSelected) 3.dp else 2.dp,
                        color = if (isSelected) WorldMapPalette.GoldStar else Color.White.copy(alpha = 0.85f),
                        shape = CircleShape
                    )
                    .clickable(onClick = onClick)
                    .testTag(testTag),
                contentAlignment = Alignment.Center
            ) {
                // Top-left glossy specular arc
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp)
                        .size(width = 34.dp, height = 10.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.34f))
                )

                when (state) {
                    JourneyNodeState.COMPLETED -> Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = label,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    JourneyNodeState.CURRENT -> Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = label,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                    JourneyNodeState.UPCOMING -> Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = label,
                        tint = Color.White.copy(alpha = 0.92f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Glossy 3D Green Checkmark Badge
            if (state == JourneyNodeState.COMPLETED) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-3).dp, y = (-3).dp)
                        .size(24.dp)
                        .shadow(3.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(WorldMapPalette.CheckGreen, WorldMapPalette.CheckGreenDark)
                            )
                        )
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // 3 Beveled Golden Stars under completed nodes
        if (state == JourneyNodeState.COMPLETED) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = WorldMapPalette.GoldStar,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
        }

        // Crisp 3D Label Pill
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White.copy(alpha = 0.96f),
            shadowElevation = 4.dp,
            border = BorderStroke(1.2.dp, sideColor.copy(alpha = 0.55f))
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = WorldMapPalette.SignTextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
