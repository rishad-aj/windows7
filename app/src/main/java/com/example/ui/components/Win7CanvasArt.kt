package com.example.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.WallpaperFitMode
import com.example.model.Win7AvatarPreset
import com.example.model.Win7SystemIconType
import com.example.model.Win7ThemePreset
import java.io.File
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Win7DesktopWallpaper(
    themePreset: Win7ThemePreset,
    highContrast: Boolean = false,
    customWallpaperPath: String = "",
    fitMode: WallpaperFitMode = WallpaperFitMode.FILL,
    modifier: Modifier = Modifier
) {
    val customBitmap: ImageBitmap? = remember(customWallpaperPath) {
        if (customWallpaperPath.isNotBlank()) {
            try {
                val file = File(customWallpaperPath)
                if (file.exists()) {
                    BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                } else null
            } catch (_: Exception) {
                null
            }
        } else null
    }

    if (highContrast) {
        Canvas(modifier = modifier.fillMaxSize()) {
            drawRect(Color.Black)
            drawRect(color = Color(0xFF00FFFF), style = Stroke(width = 2f))
        }
        return
    }

    if (customBitmap != null) {
        val contentScale = when (fitMode) {
            WallpaperFitMode.FILL -> ContentScale.Crop
            WallpaperFitMode.FIT -> ContentScale.Fit
            WallpaperFitMode.STRETCH -> ContentScale.FillBounds
            WallpaperFitMode.CENTER -> ContentScale.Inside
        }
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF041A3D)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = customBitmap,
                contentDescription = "Custom Windows 7 Desktop Wallpaper",
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        when (themePreset) {
            Win7ThemePreset.HARMONY -> drawOriginalHarmonyWallpaper(w, h)
            Win7ThemePreset.LOGON_SWALLOW -> drawWin7LogonArtwork(w, h)
            Win7ThemePreset.BETTA_FISH -> drawOriginalBettaFishWallpaper(w, h)
            Win7ThemePreset.LANDSCAPES -> drawOriginalBlissLandscapeWallpaper(w, h)
            Win7ThemePreset.NATURE -> drawOriginalNatureDewdropsWallpaper(w, h)
            Win7ThemePreset.SCENES -> drawOriginalScenesSunsetLakeWallpaper(w, h)
            Win7ThemePreset.AURORA -> drawOriginalAuroraWallpaper(w, h)
            Win7ThemePreset.ARCHITECTURE -> drawOriginalArchitectureGlassWallpaper(w, h)
            Win7ThemePreset.CHARACTERS -> drawOriginalCharactersAeroWallpaper(w, h)
            Win7ThemePreset.SAKURA_SPRING -> drawOriginalSakuraBranchWallpaper(w, h)
            Win7ThemePreset.BLACK_CARBON -> drawOriginalBlackCarbonFlagWallpaper(w, h)
            Win7ThemePreset.CLASSIC_TEAL -> drawClassicTealWallpaper(w, h)
        }
    }
}

/**
 * Reconstructs the iconic Windows 7 Default Wallpaper (C:\Windows\Web\Wallpaper\Windows\img0.jpg - "Harmony"):
 * - Rich azure-to-cerulean radial & diagonal backdrop
 * - Sweeping translucent Frutiger Aero silk light ribbons
 * - Delicate white botanical flora, fern fronds, and curling tendrils (Chuck Anderson / NoPattern design)
 * - Out-of-focus bokeh orbs & lens flare beams
 * - The iconic 3D waving 4-color Windows 7 Flag logo glowing at the center
 */
private fun DrawScope.drawOriginalHarmonyWallpaper(w: Float, h: Float) {
    val flagCenter = Offset(w * 0.52f, h * 0.45f)
    val maxDim = w.coerceAtLeast(h)

    // 1. Deep Cerulean to Bright Azure Windows 7 Harmony Backdrop
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF042458),
                Color(0xFF0C4FA3),
                Color(0xFF1D82DB),
                Color(0xFF43B0F6),
                Color(0xFF1465BE),
                Color(0xFF05275C)
            )
        )
    )

    // 2. Brilliant White-Cyan Corona Glow Behind the Windows Flag
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xEEB8F4FF),
                Color(0x8838B6FF),
                Color(0x220D57B6),
                Color.Transparent
            ),
            center = flagCenter,
            radius = maxDim * 0.58f
        ),
        center = flagCenter,
        radius = maxDim * 0.58f
    )

    // 3. Diagonal Volumetric Light Beams Radiating from Behind the Flag
    val rayAngles = listOf(-32f, -14f, 18f, 42f, 145f, 168f, 198f)
    rayAngles.forEachIndexed { idx, deg ->
        val rad1 = (deg - 4f) * (Math.PI / 180.0)
        val rad2 = (deg + 4f) * (Math.PI / 180.0)
        val len = maxDim * 0.72f
        val beam = Path().apply {
            moveTo(flagCenter.x, flagCenter.y)
            lineTo(flagCenter.x + (cos(rad1) * len).toFloat(), flagCenter.y + (sin(rad1) * len).toFloat())
            lineTo(flagCenter.x + (cos(rad2) * len).toFloat(), flagCenter.y + (sin(rad2) * len).toFloat())
            close()
        }
        drawPath(
            path = beam,
            color = if (idx % 2 == 0) Color(0x24FFFFFF) else Color(0x18A7F0FF)
        )
    }

    // 4. Signature Sweeping Silk Light Ribbons (Cyan, Emerald-Lime, and White Glass)
    val mainRibbon = Path().apply {
        moveTo(-w * 0.12f, h * 0.82f)
        cubicTo(w * 0.22f, h * 0.58f, w * 0.44f, h * 0.62f, w * 1.12f, h * 0.18f)
        lineTo(w * 1.12f, h * 0.36f)
        cubicTo(w * 0.54f, h * 0.72f, w * 0.28f, h * 0.74f, -w * 0.12f, h * 0.95f)
        close()
    }
    drawPath(
        path = mainRibbon,
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0x0AFFFFFF),
                Color(0x5584FFFF),
                Color(0x77FFFFFF),
                Color(0x3340C4FF)
            )
        )
    )

    val emeraldGoldRibbon = Path().apply {
        moveTo(w * 0.12f, -h * 0.06f)
        cubicTo(w * 0.40f, h * 0.30f, w * 0.62f, h * 0.42f, w * 0.92f, h * 0.92f)
        lineTo(w * 0.74f, h * 0.98f)
        cubicTo(w * 0.50f, h * 0.50f, w * 0.28f, h * 0.34f, -w * 0.02f, -h * 0.04f)
        close()
    }
    drawPath(
        path = emeraldGoldRibbon,
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0x10FFFFFF),
                Color(0x45B9F6CA),
                Color(0x48FFE57F),
                Color(0x10FFFFFF)
            )
        )
    )

    // Crisp glowing white silk spline wave curves
    for (i in 0..3) {
        val offsetH = (i - 1.5f) * h * 0.035f
        val spline = Path().apply {
            moveTo(0f, h * 0.76f + offsetH)
            cubicTo(
                w * 0.32f,
                h * 0.54f - offsetH,
                w * 0.64f,
                h * 0.50f + offsetH,
                w,
                h * 0.24f - offsetH
            )
        }
        drawPath(
            path = spline,
            color = Color(0x66FFFFFF),
            style = Stroke(width = 1.6f - i * 0.25f, cap = StrokeCap.Round)
        )
    }

    // 5. Authentic White Botanical Vines, Fern Leaves & Floral Silhouettes Behind the Flag
    drawBotanicalBranch(
        origin = Offset(flagCenter.x - w * 0.04f, flagCenter.y + h * 0.02f),
        tip = Offset(w * 0.18f, h * 0.66f),
        control = Offset(w * 0.36f, h * 0.60f),
        leafCount = 7
    )
    drawBotanicalBranch(
        origin = Offset(flagCenter.x + w * 0.04f, flagCenter.y - h * 0.03f),
        tip = Offset(w * 0.84f, h * 0.22f),
        control = Offset(w * 0.70f, h * 0.34f),
        leafCount = 8
    )
    drawBotanicalBranch(
        origin = Offset(flagCenter.x - w * 0.02f, flagCenter.y - h * 0.04f),
        tip = Offset(w * 0.26f, h * 0.22f),
        control = Offset(w * 0.38f, h * 0.30f),
        leafCount = 6
    )

    // 6. Luminous Bokeh Circles & Light Dust Particles
    val harmonyBokeh = listOf(
        Triple(0.24f, 0.34f, 26f),
        Triple(0.30f, 0.38f, 14f),
        Triple(0.36f, 0.58f, 32f),
        Triple(0.28f, 0.64f, 18f),
        Triple(0.70f, 0.30f, 34f),
        Triple(0.76f, 0.26f, 16f),
        Triple(0.66f, 0.58f, 28f),
        Triple(0.74f, 0.52f, 15f),
        Triple(0.44f, 0.26f, 20f),
        Triple(0.58f, 0.66f, 22f)
    )
    harmonyBokeh.forEach { (rx, ry, r) ->
        val pt = Offset(w * rx, h * ry)
        drawCircle(color = Color(0x28FFFFFF), radius = r, center = pt)
        drawCircle(color = Color(0x55FFFFFF), radius = r, center = pt, style = Stroke(width = 1.2f))
    }

    // 7. THE CENTERPIECE: Authentic 3D Waving 4-Color Windows 7 Flag Emblem
    val flagScale = w.coerceAtMost(h) * 0.24f
    drawAuthentic3dWindows7Flag(center = flagCenter, scale = flagScale)
}

private fun DrawScope.drawBotanicalBranch(
    origin: Offset,
    tip: Offset,
    control: Offset,
    leafCount: Int
) {
    val stem = Path().apply {
        moveTo(origin.x, origin.y)
        quadraticTo(control.x, control.y, tip.x, tip.y)
    }
    drawPath(stem, color = Color(0x88FFFFFF), style = Stroke(width = 2.0f, cap = StrokeCap.Round))

    for (i in 1..leafCount) {
        val t = i.toFloat() / (leafCount + 1)
        val u = 1f - t
        val bx = u * u * origin.x + 2 * u * t * control.x + t * t * tip.x
        val by = u * u * origin.y + 2 * u * t * control.y + t * t * tip.y
        val side = if (i % 2 == 0) 1f else -1f
        val leafLen = (1f - t * 0.45f) * 18f
        val leaf = Path().apply {
            moveTo(bx, by)
            quadraticTo(bx + leafLen * 0.6f, by + side * leafLen * 0.7f, bx + leafLen * 1.1f * side, by + side * leafLen * 0.3f)
            quadraticTo(bx + leafLen * 0.3f * side, by - side * leafLen * 0.2f, bx, by)
            close()
        }
        drawPath(leaf, color = Color(0x75FFFFFF))
    }
}

/**
 * Draws the authentic 3D waving 4-color Windows 7 Flag logo (Red, Green, Blue, Yellow curved panes)
 * with outer cyan/white bloom, internal gradient shading, and glossy glass specular highlights.
 */
fun DrawScope.drawAuthentic3dWindows7Flag(
    center: Offset,
    scale: Float
) {
    // Soft white-cyan outer glow behind the 4 waving panes
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xAAFFFFFF), Color(0x4480D8FF), Color.Transparent),
            center = center,
            radius = scale * 1.35f
        ),
        center = center,
        radius = scale * 1.35f
    )

    val gap = scale * 0.065f
    val paneW = scale * 0.76f
    val paneH = scale * 0.66f
    val waveAmp = scale * 0.11f
    val skewX = scale * 0.12f

    // Helper to build a waving flag quadrant path
    fun buildWavingQuadrant(
        baseX: Float,
        baseY: Float,
        isRightColumn: Boolean
    ): Path {
        val dir = if (isRightColumn) -1f else 1f
        return Path().apply {
            moveTo(baseX + skewX, baseY)
            cubicTo(
                baseX + skewX + paneW * 0.35f,
                baseY - waveAmp * dir,
                baseX + skewX + paneW * 0.65f,
                baseY + waveAmp * dir * 0.6f,
                baseX + skewX + paneW,
                baseY - waveAmp * 0.2f * dir
            )
            lineTo(baseX + paneW, baseY + paneH - waveAmp * 0.2f * dir)
            cubicTo(
                baseX + paneW * 0.65f,
                baseY + paneH + waveAmp * dir * 0.6f,
                baseX + paneW * 0.35f,
                baseY + paneH - waveAmp * dir,
                baseX,
                baseY + paneH
            )
            close()
        }
    }

    // 1. Top-Left Pane: Windows Red/Orange (#F25022)
    val tlX = center.x - paneW - gap
    val tlY = center.y - paneH - gap
    val redPath = buildWavingQuadrant(tlX, tlY, isRightColumn = false)
    drawPath(
        path = redPath,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFF8A50), Color(0xFFF25022), Color(0xFFC62828)),
            start = Offset(tlX, tlY),
            end = Offset(tlX + paneW, tlY + paneH)
        )
    )
    drawPath(redPath, color = Color(0x88FFFFFF), style = Stroke(width = 1.6f))

    // 2. Top-Right Pane: Windows Leaf Green (#7FBA00)
    val trX = center.x + gap
    val trY = center.y - paneH - gap + waveAmp * 0.2f
    val greenPath = buildWavingQuadrant(trX, trY, isRightColumn = true)
    drawPath(
        path = greenPath,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFCCFF90), Color(0xFF7FBA00), Color(0xFF33691E)),
            start = Offset(trX, trY),
            end = Offset(trX + paneW, trY + paneH)
        )
    )
    drawPath(greenPath, color = Color(0x88FFFFFF), style = Stroke(width = 1.6f))

    // 3. Bottom-Left Pane: Windows Cerulean Blue (#00A4EF)
    val blX = center.x - paneW - gap - skewX * 0.85f
    val blY = center.y + gap
    val bluePath = buildWavingQuadrant(blX, blY, isRightColumn = false)
    drawPath(
        path = bluePath,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF80D8FF), Color(0xFF00A4EF), Color(0xFF01579B)),
            start = Offset(blX, blY),
            end = Offset(blX + paneW, blY + paneH)
        )
    )
    drawPath(bluePath, color = Color(0x88FFFFFF), style = Stroke(width = 1.6f))

    // 4. Bottom-Right Pane: Windows Golden Yellow (#FFB900)
    val brX = center.x + gap - skewX * 0.85f
    val brY = center.y + gap + waveAmp * 0.2f
    val yellowPath = buildWavingQuadrant(brX, brY, isRightColumn = true)
    drawPath(
        path = yellowPath,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFFF59D), Color(0xFFFFB900), Color(0xFFE65100)),
            start = Offset(brX, brY),
            end = Offset(brX + paneW, brY + paneH)
        )
    )
    drawPath(yellowPath, color = Color(0x88FFFFFF), style = Stroke(width = 1.6f))
}

fun DrawScope.drawWin7LogonArtwork(w: Float, h: Float) {
    // 1. Deep Cerulean-to-Cyan Windows 7 Logon Screen radial & vertical gradient (authui.exe)
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF041C42),
                Color(0xFF0B4592),
                Color(0xFF1D7BD6),
                Color(0xFF3AA6F4),
                Color(0xFF145BB0),
                Color(0xFF041A3D)
            )
        )
    )

    // 2. Top-Center & Right-Center Sunburst Glow
    val lightOrigin = Offset(w * 0.50f, h * 0.24f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xEEFFFFFF),
                Color(0xAA9BE7FF),
                Color(0x4429B6F6),
                Color.Transparent
            ),
            center = lightOrigin,
            radius = w.coerceAtLeast(h) * 0.54f
        ),
        center = lightOrigin,
        radius = w.coerceAtLeast(h) * 0.54f
    )

    // 3. Sweeping Translucent Glass Light Arcs (signature Windows 7 authui.exe curves)
    val arc1 = Path().apply {
        moveTo(0f, h * 0.14f)
        cubicTo(w * 0.35f, h * 0.04f, w * 0.65f, h * 0.36f, w, h * 0.18f)
        lineTo(w, h * 0.34f)
        cubicTo(w * 0.60f, h * 0.50f, w * 0.30f, h * 0.18f, 0f, h * 0.32f)
        close()
    }
    drawPath(
        path = arc1,
        brush = Brush.horizontalGradient(
            colors = listOf(Color(0x14FFFFFF), Color(0x45B3E5FC), Color(0x55FFFFFF), Color(0x14FFFFFF))
        )
    )

    // 4. Intricate Curling White Botanical Vines, Leaves & Blossoms (Top-Left, Bottom-Left, Mid-Right)
    drawBotanicalBranch(
        origin = Offset(0f, h * 0.07f),
        tip = Offset(w * 0.38f, h * 0.13f),
        control = Offset(w * 0.18f, h * 0.20f),
        leafCount = 9
    )
    drawBotanicalBranch(
        origin = Offset(w, h * 0.74f),
        tip = Offset(w * 0.64f, h * 0.68f),
        control = Offset(w * 0.84f, h * 0.62f),
        leafCount = 8
    )
    drawBotanicalBranch(
        origin = Offset(0f, h * 0.84f),
        tip = Offset(w * 0.32f, h * 0.78f),
        control = Offset(w * 0.16f, h * 0.72f),
        leafCount = 7
    )

    // 5. Iconic Windows 7 White Barn Swallow Bird Silhouette in Flight (Top-Right of center)
    val bx = w * 0.74f
    val by = h * 0.21f
    val birdPath = Path().apply {
        moveTo(bx, by)
        quadraticTo(bx - 26f, by - 30f, bx - 52f, by - 16f)
        quadraticTo(bx - 22f, by - 4f, bx - 10f, by + 7f)
        lineTo(bx - 24f, by + 24f)
        lineTo(bx - 3f, by + 13f)
        lineTo(bx + 15f, by + 19f)
        quadraticTo(bx + 7f, by + 5f, bx + 16f, by - 3f)
        quadraticTo(bx + 36f, by - 26f, bx + 54f, by - 20f)
        quadraticTo(bx + 22f, by - 26f, bx, by)
        close()
    }
    drawPath(birdPath, color = Color(0xEEFFFFFF))

    // 6. Luminous Bokeh Circles & Curving Seed Dots
    val bokehs = listOf(
        Triple(0.16f, 0.25f, 28f),
        Triple(0.24f, 0.31f, 15f),
        Triple(0.31f, 0.27f, 10f),
        Triple(0.82f, 0.30f, 36f),
        Triple(0.76f, 0.36f, 18f),
        Triple(0.16f, 0.74f, 32f),
        Triple(0.26f, 0.78f, 16f),
        Triple(0.84f, 0.78f, 24f),
        Triple(0.72f, 0.82f, 14f)
    )
    bokehs.forEach { (rx, ry, r) ->
        val pt = Offset(w * rx, h * ry)
        drawCircle(color = Color(0x24FFFFFF), radius = r, center = pt)
        drawCircle(color = Color(0x55FFFFFF), radius = r, center = pt, style = Stroke(width = 1.5f))
    }
}

private fun DrawScope.drawOriginalBettaFishWallpaper(w: Float, h: Float) {
    // Official Windows 7 Beta Siamese Fighting Fish (Betta splendens) on Cerulean-Aqua Studio Backdrop
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF22D0F0),
                Color(0xFF0C84C6),
                Color(0xFF064684),
                Color(0xFF021E44)
            ),
            center = Offset(w * 0.56f, h * 0.44f),
            radius = w.coerceAtLeast(h) * 0.78f
        )
    )

    val cx = w * 0.56f
    val cy = h * 0.46f

    // Multi-layered flowing gossamer silk caudal & anal fins with 32 delicate fin rays
    val mainTail = Path().apply {
        moveTo(cx - 10f, cy)
        cubicTo(
            cx - w * 0.28f,
            cy - h * 0.26f,
            cx - w * 0.44f,
            cy + h * 0.06f,
            cx - w * 0.24f,
            cy + h * 0.28f
        )
        cubicTo(
            cx - w * 0.10f,
            cy + h * 0.22f,
            cx - w * 0.04f,
            cy + h * 0.10f,
            cx + 8f,
            cy + 8f
        )
        close()
    }
    drawPath(
        path = mainTail,
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xEEFFFFFF),
                Color(0xBB80D8FF),
                Color(0x77CE93D8),
                Color(0x18FFFFFF)
            ),
            start = Offset(cx, cy),
            end = Offset(cx - w * 0.36f, cy + h * 0.20f)
        )
    )

    // Radiating silk fin rays inside the Betta tail
    for (i in 0..22) {
        val angle = (135f + i * 5.2f) * (Math.PI / 180.0)
        val rayLen = w * (0.22f + (i % 4) * 0.025f)
        val end = Offset(cx + (cos(angle) * rayLen).toFloat(), cy + (sin(angle) * rayLen * 0.85f).toFloat())
        drawLine(
            color = Color(0x55FFFFFF),
            start = Offset(cx - 8f, cy),
            end = end,
            strokeWidth = 1.4f,
            cap = StrokeCap.Round
        )
    }

    // Ruffled Dorsal Fin
    val dorsalFin = Path().apply {
        moveTo(cx - 12f, cy - 12f)
        cubicTo(cx - w * 0.18f, cy - h * 0.18f, cx - w * 0.02f, cy - h * 0.16f, cx + w * 0.08f, cy - 10f)
        close()
    }
    drawPath(
        path = dorsalFin,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0x66FFFFFF), Color(0xCCB3E5FC))
        )
    )

    // Ventral Ribbon Fins
    val ventralFin = Path().apply {
        moveTo(cx + 12f, cy + 10f)
        quadraticTo(cx - 10f, cy + h * 0.16f, cx - 28f, cy + h * 0.20f)
        quadraticTo(cx + 22f, cy + h * 0.12f, cx + 28f, cy + 8f)
        close()
    }
    drawPath(ventralFin, color = Color(0xBBFFFFFF))

    // Sculpted Pearlescent Fish Body
    val bodyPath = Path().apply {
        moveTo(cx - 26f, cy)
        cubicTo(cx - 8f, cy - 22f, cx + 44f, cy - 18f, cx + 68f, cy - 4f)
        cubicTo(cx + 46f, cy + 18f, cx - 6f, cy + 18f, cx - 26f, cy)
        close()
    }
    drawPath(
        path = bodyPath,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF80DEEA), Color(0xFFFFFFFF), Color(0xFF4DD0E1), Color(0xFF00ACC1)),
            start = Offset(cx - 26f, cy - 16f),
            end = Offset(cx + 68f, cy + 16f)
        )
    )
    drawCircle(Color(0xFF041E42), radius = 3.8f, center = Offset(cx + 50f, cy - 3f))
    drawCircle(Color.White, radius = 1.4f, center = Offset(cx + 49f, cy - 4f))

    // Refractive Rising Pearl Water Bubbles
    for (i in 0..14) {
        val bx = cx + 64f + sin(i * 1.1).toFloat() * 24f + (i % 3) * 10f
        val by = cy - i * 20f
        val rad = 3.5f + (i % 4) * 2.4f
        drawCircle(
            color = Color(0x28FFFFFF),
            radius = rad,
            center = Offset(bx, by)
        )
        drawCircle(
            color = Color(0x88FFFFFF),
            radius = rad,
            center = Offset(bx, by),
            style = Stroke(width = 1.3f)
        )
        drawCircle(
            color = Color.White,
            radius = rad * 0.25f,
            center = Offset(bx - rad * 0.35f, by - rad * 0.35f)
        )
    }
}

private fun DrawScope.drawOriginalBlissLandscapeWallpaper(w: Float, h: Float) {
    // 1. Deep Cerulean to Horizon Cyan Sky (C:\Windows\Web\Wallpaper\Landscapes\img7.jpg)
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0B4DA2),
                Color(0xFF237EE0),
                Color(0xFF5FB0FA),
                Color(0xFFB8E2FF)
            ),
            startY = 0f,
            endY = h * 0.62f
        )
    )

    // 2. Volumetric Cumulus Cloud Banks with Sunlit Tops & Soft Lavender Shadows
    val cloudBanks = listOf(
        Triple(0.22f, 0.24f, w * 0.14f),
        Triple(0.32f, 0.21f, w * 0.17f),
        Triple(0.44f, 0.25f, w * 0.13f),
        Triple(0.68f, 0.22f, w * 0.16f),
        Triple(0.79f, 0.25f, w * 0.13f),
        Triple(0.14f, 0.34f, w * 0.11f),
        Triple(0.56f, 0.32f, w * 0.12f)
    )
    cloudBanks.forEach { (rx, ry, rad) ->
        val c = Offset(w * rx, h * ry)
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xEEFFFFFF), Color(0xCCE1F5FE), Color(0x5590CAF9)),
                startY = c.y - rad,
                endY = c.y + rad
            ),
            radius = rad,
            center = c
        )
    }

    // 3. Distant Snow-Dusted Blue Alpine Mountain Range (Atmospheric Perspective)
    val mountainPath = Path().apply {
        moveTo(0f, h * 0.56f)
        lineTo(w * 0.16f, h * 0.42f)
        lineTo(w * 0.30f, h * 0.50f)
        lineTo(w * 0.48f, h * 0.37f)
        lineTo(w * 0.64f, h * 0.49f)
        lineTo(w * 0.82f, h * 0.39f)
        lineTo(w, h * 0.48f)
        lineTo(w, h * 0.68f)
        lineTo(0f, h * 0.68f)
        close()
    }
    drawPath(
        path = mountainPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFE3F2FD), Color(0xFF5C8AB8), Color(0xFF2B5278)),
            startY = h * 0.37f,
            endY = h * 0.64f
        )
    )

    // 4. Midground Sunlit Emerald Hills
    val backHill = Path().apply {
        moveTo(0f, h * 0.62f)
        cubicTo(w * 0.34f, h * 0.47f, w * 0.66f, h * 0.57f, w, h * 0.48f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(
        path = backHill,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF7ED93A), Color(0xFF3E9C27), Color(0xFF1E6618)),
            startY = h * 0.48f,
            endY = h
        )
    )

    // 5. Foreground Rolling Bliss Hill with Warm Golden Sunlight Gradient & Grass Contours
    val frontHill = Path().apply {
        moveTo(0f, h * 0.55f)
        cubicTo(w * 0.38f, h * 0.59f, w * 0.68f, h * 0.74f, w, h * 0.65f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(
        path = frontHill,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFA8F048), Color(0xFF55B62A), Color(0xFF256D1B), Color(0xFF144511)),
            startY = h * 0.55f,
            endY = h
        )
    )

    // Subtle sunlit ridge contours
    for (i in 1..4) {
        val yOff = i * h * 0.06f
        val ridge = Path().apply {
            moveTo(0f, h * 0.57f + yOff)
            quadraticTo(w * 0.48f, h * 0.66f + yOff, w, h * 0.66f + yOff)
        }
        drawPath(ridge, color = Color(0x28DCEDC8), style = Stroke(width = 2f))
    }
}

private fun DrawScope.drawOriginalNatureDewdropsWallpaper(w: Float, h: Float) {
    // Official Windows 7 Nature Theme (C:\Windows\Web\Wallpaper\Nature\img1.jpg - Macro Lotus Leaf & Dewdrops)
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF54C84D),
                Color(0xFF288E35),
                Color(0xFF135C22),
                Color(0xFF082E12)
            ),
            center = Offset(w * 0.42f, h * 0.38f),
            radius = w.coerceAtLeast(h) * 0.82f
        )
    )

    // Primary & secondary botanical leaf veins radiating from bottom-left
    val veinOrigin = Offset(w * 0.08f, h * 0.88f)
    for (i in 0..8) {
        val angle = (-78f + i * 9.5f) * (Math.PI / 180.0)
        val len = w.coerceAtLeast(h) * 1.15f
        val end = Offset(veinOrigin.x + (cos(angle) * len).toFloat(), veinOrigin.y + (sin(angle) * len).toFloat())
        drawLine(
            color = Color(0x55B9F6CA),
            start = veinOrigin,
            end = end,
            strokeWidth = if (i == 4) 4.2f else 2.0f,
            cap = StrokeCap.Round
        )
    }

    // Photorealistic 3D Refractive Crystal Dewdrops with Cast Shadows & Caustic Highlights
    val droplets = listOf(
        Triple(0.32f, 0.30f, 28f),
        Triple(0.58f, 0.42f, 40f),
        Triple(0.45f, 0.62f, 24f),
        Triple(0.72f, 0.26f, 20f),
        Triple(0.24f, 0.52f, 16f),
        Triple(0.66f, 0.68f, 22f),
        Triple(0.48f, 0.24f, 14f),
        Triple(0.80f, 0.50f, 18f),
        Triple(0.36f, 0.44f, 11f),
        Triple(0.54f, 0.54f, 9f)
    )
    droplets.forEach { (rx, ry, r) ->
        val c = Offset(w * rx, h * ry)
        // 1. Dark emerald drop shadow beneath the water droplet
        drawOval(
            color = Color(0x66041E0A),
            topLeft = Offset(c.x - r * 0.85f, c.y - r * 0.65f + r * 0.28f),
            size = Size(r * 1.9f, r * 1.65f)
        )
        // 2. Refractive droplet body with internal lime caustic glow at bottom-right
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1B5E20),
                    Color(0xFF4CAF50),
                    Color(0xFFCCFF90),
                    Color(0xFFFFFFFF)
                ),
                center = Offset(c.x + r * 0.32f, c.y + r * 0.32f),
                radius = r * 1.15f
            ),
            radius = r,
            center = c
        )
        // 3. Rim light & top-left sky reflection highlight
        drawCircle(
            color = Color(0x88E8F5E9),
            radius = r,
            center = c,
            style = Stroke(width = 1.4f)
        )
        drawOval(
            color = Color(0xEEFFFFFF),
            topLeft = Offset(c.x - r * 0.52f, c.y - r * 0.58f),
            size = Size(r * 0.52f, r * 0.32f)
        )
    }
}

private fun DrawScope.drawOriginalScenesSunsetLakeWallpaper(w: Float, h: Float) {
    val horizonY = h * 0.54f

    // 1. Golden Hour Twilight Sky (C:\Windows\Web\Wallpaper\Scenes\img25.jpg)
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF141E30),
                Color(0xFF3B1D5E),
                Color(0xFF9E2A5B),
                Color(0xFFE65100),
                Color(0xFFFFB300),
                Color(0xFFFFF176)
            ),
            startY = 0f,
            endY = horizonY
        ),
        topLeft = Offset.Zero,
        size = Size(w, horizonY)
    )

    // 2. Setting Golden Sun Disk & Atmospheric Corona
    val sunCenter = Offset(w * 0.54f, horizonY - h * 0.04f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFFFFF), Color(0xCCFFE082), Color(0x44FF6F00), Color.Transparent),
            center = sunCenter,
            radius = w * 0.32f
        ),
        center = sunCenter,
        radius = w * 0.32f
    )
    drawCircle(
        color = Color(0xFFFFFDE7),
        radius = w * 0.075f,
        center = sunCenter
    )

    // 3. Silhouetted Mountain Ridges Along the Horizon
    val farMountain = Path().apply {
        moveTo(0f, horizonY)
        lineTo(0f, horizonY - h * 0.09f)
        lineTo(w * 0.22f, horizonY - h * 0.15f)
        lineTo(w * 0.42f, horizonY - h * 0.05f)
        lineTo(w * 0.66f, horizonY - h * 0.04f)
        lineTo(w * 0.85f, horizonY - h * 0.14f)
        lineTo(w, horizonY - h * 0.08f)
        lineTo(w, horizonY)
        close()
    }
    drawPath(farMountain, color = Color(0xFF281538))

    // 4. Mirror Alpine Lake Reflection Below Horizon
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFCA28),
                Color(0xFFD84315),
                Color(0xFF6A1B4D),
                Color(0xFF1B1938),
                Color(0xFF0B0E1D)
            ),
            startY = horizonY,
            endY = h
        ),
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY)
    )

    // 5. Shimmering Water Ripple Reflection Column
    for (i in 0..28) {
        val progress = i / 28f
        val y = horizonY + progress * (h - horizonY) * 0.92f
        val halfSpan = w * (0.24f * (1f - progress * 0.65f)) + sin(i * 1.7).toFloat() * 12f
        drawLine(
            color = if (i % 2 == 0) Color(0xAAFFF59D) else Color(0x77FF9800),
            start = Offset(sunCenter.x - halfSpan, y),
            end = Offset(sunCenter.x + halfSpan, y),
            strokeWidth = 1.6f + progress * 2.2f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawOriginalAuroraWallpaper(w: Float, h: Float) {
    // Official Windows Aurora Borealis (C:\Windows\Web\Wallpaper\Windows\Aurora.jpg)
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF020816),
                Color(0xFF071A3A),
                Color(0xFF0E3466),
                Color(0xFF071E3D),
                Color(0xFF030B1A)
            )
        )
    )

    // Starfield
    for (i in 0..45) {
        val sx = ((i * 73 + 19) % 100) / 100f * w
        val sy = ((i * 41 + 11) % 65) / 100f * h
        drawCircle(
            color = Color(0xAAFFFFFF),
            radius = if (i % 5 == 0) 1.8f else 1.0f,
            center = Offset(sx, sy)
        )
    }

    // 6 Multi-Colored Aurora Silk Curtains
    val curtains = listOf(
        Triple(0.22f, Color(0x6600E5FF), Color(0x1100E5FF)),
        Triple(0.36f, Color(0x6600E676), Color(0x1100E676)),
        Triple(0.50f, Color(0x5540C4FF), Color(0x1140C4FF)),
        Triple(0.64f, Color(0x557C4DFF), Color(0x117C4DFF)),
        Triple(0.76f, Color(0x4418FFFF), Color(0x0818FFFF))
    )
    curtains.forEachIndexed { idx, (yFactor, topCol, botCol) ->
        val yBase = h * yFactor
        val wave = Path().apply {
            moveTo(0f, yBase)
            cubicTo(
                w * 0.28f,
                yBase - h * 0.16f + idx * 12f,
                w * 0.68f,
                yBase + h * 0.14f - idx * 10f,
                w,
                yBase - h * 0.08f
            )
            lineTo(w, yBase + h * 0.22f)
            cubicTo(
                w * 0.68f,
                yBase + h * 0.30f,
                w * 0.28f,
                yBase + h * 0.04f,
                0f,
                yBase + h * 0.20f
            )
            close()
        }
        drawPath(
            path = wave,
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, topCol, botCol, Color.Transparent),
                startY = yBase - h * 0.16f,
                endY = yBase + h * 0.28f
            )
        )
    }
}

private fun DrawScope.drawOriginalArchitectureGlassWallpaper(w: Float, h: Float) {
    // Official Windows 7 Architecture Theme (C:\Windows\Web\Wallpaper\Architecture\img13.jpg - Glass Diagrid Dome)
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF0D2B4E),
                Color(0xFF235789),
                Color(0xFF5FA8D3),
                Color(0xFFCAE9FF),
                Color(0xFF1B4965)
            ),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        )
    )

    // Sunburst reflection on glass apex
    val apex = Offset(w * 0.72f, h * 0.24f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xEEFFFFFF), Color(0x88B3E5FC), Color.Transparent),
            center = apex,
            radius = w * 0.45f
        ),
        center = apex,
        radius = w * 0.45f
    )

    // Perspective steel-and-glass diagrid lattice beams
    val steps = 12
    for (i in 0..steps) {
        val t = i.toFloat() / steps
        // Primary curved steel ribs
        val rib1 = Path().apply {
            moveTo(w * t, 0f)
            quadraticTo(w * (0.3f + t * 0.4f), h * 0.5f, 0f, h * (1f - t * 0.6f))
        }
        drawPath(rib1, color = Color(0x66E1F5FE), style = Stroke(width = 2.8f))
        drawPath(rib1, color = Color(0xAA0D253F), style = Stroke(width = 1.0f))

        // Intersecting counter-diagrid steel ribs
        val rib2 = Path().apply {
            moveTo(0f, h * t)
            quadraticTo(w * 0.52f, h * (0.25f + t * 0.5f), w, h * (t * 0.85f))
        }
        drawPath(rib2, color = Color(0x55FFFFFF), style = Stroke(width = 2.2f))
    }
}

private fun DrawScope.drawOriginalCharactersAeroWallpaper(w: Float, h: Float) {
    // Official Windows 7 Characters Theme (C:\Windows\Web\Wallpaper\Characters\img19.jpg)
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1A237E),
                Color(0xFF283593),
                Color(0xFF1E88E5),
                Color(0xFF4DD0E1),
                Color(0xFFB2FF59)
            )
        )
    )

    // Rainbow light arches across the sky
    val rainbowColors = listOf(
        Color(0x55FF5252),
        Color(0x55FFD740),
        Color(0x5569F0AE),
        Color(0x5540C4FF),
        Color(0x55E040FB)
    )
    rainbowColors.forEachIndexed { idx, col ->
        val r = w * (0.68f - idx * 0.032f)
        drawCircle(
            color = col,
            radius = r,
            center = Offset(w * 0.5f, h * 0.85f),
            style = Stroke(width = 9f)
        )
    }

    // 3D Glossy Aero Character Orbs with Specular Highlights & Cute Eyes
    val characters = listOf(
        Triple(0.28f, 0.38f, 46f),
        Triple(0.68f, 0.30f, 58f),
        Triple(0.52f, 0.58f, 52f),
        Triple(0.24f, 0.66f, 36f),
        Triple(0.78f, 0.64f, 40f)
    )
    characters.forEachIndexed { idx, (rx, ry, rad) ->
        val c = Offset(w * rx, h * ry)
        val tint = when (idx % 4) {
            0 -> Color(0xFFFF4081)
            1 -> Color(0xFF00E5FF)
            2 -> Color(0xFF76FF03)
            else -> Color(0xFFFFD740)
        }
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, tint, tint.copy(alpha = 0.55f)),
                center = Offset(c.x - rad * 0.32f, c.y - rad * 0.32f),
                radius = rad * 1.25f
            ),
            radius = rad,
            center = c
        )
        // Character eyes
        drawCircle(Color(0xFF1A237E), radius = rad * 0.11f, center = Offset(c.x - rad * 0.22f, c.y - rad * 0.05f))
        drawCircle(Color(0xFF1A237E), radius = rad * 0.11f, center = Offset(c.x + rad * 0.22f, c.y - rad * 0.05f))
        drawCircle(Color.White, radius = rad * 0.04f, center = Offset(c.x - rad * 0.24f, c.y - rad * 0.08f))
        drawCircle(Color.White, radius = rad * 0.04f, center = Offset(c.x + rad * 0.20f, c.y - rad * 0.08f))
    }
}

private fun DrawScope.drawOriginalSakuraBranchWallpaper(w: Float, h: Float) {
    // Official Windows 7 Nature Cherry Blossoms (C:\Windows\Web\Wallpaper\Nature\img3.jpg)
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0D47A1),
                Color(0xFF29B6F6),
                Color(0xFFB3E5FC),
                Color(0xFFF8BBD0)
            )
        )
    )

    // Out-of-focus background pink bokeh circles
    val bgBokeh = listOf(
        Triple(0.20f, 0.22f, 44f),
        Triple(0.75f, 0.18f, 52f),
        Triple(0.55f, 0.35f, 38f),
        Triple(0.82f, 0.50f, 46f),
        Triple(0.30f, 0.62f, 40f)
    )
    bgBokeh.forEach { (rx, ry, r) ->
        drawCircle(color = Color(0x44FCE4EC), radius = r, center = Offset(w * rx, h * ry))
    }

    // Dark cherry wood main branch & secondary twigs
    val branch = Path().apply {
        moveTo(0f, h * 0.18f)
        quadraticTo(w * 0.42f, h * 0.34f, w * 0.88f, h * 0.52f)
        moveTo(w * 0.32f, h * 0.28f)
        quadraticTo(w * 0.52f, h * 0.18f, w * 0.74f, h * 0.22f)
        moveTo(w * 0.46f, h * 0.35f)
        quadraticTo(w * 0.36f, h * 0.54f, w * 0.22f, h * 0.68f)
    }
    drawPath(branch, color = Color(0xFF3E2723), style = Stroke(width = 6.5f, cap = StrokeCap.Round))

    // Detailed 5-petal Sakura flowers with stamens along the branches
    val blossoms = listOf(
        Triple(0.18f, 0.24f, 18f),
        Triple(0.34f, 0.28f, 22f),
        Triple(0.52f, 0.21f, 20f),
        Triple(0.68f, 0.23f, 17f),
        Triple(0.48f, 0.36f, 24f),
        Triple(0.66f, 0.43f, 21f),
        Triple(0.82f, 0.50f, 19f),
        Triple(0.36f, 0.52f, 20f),
        Triple(0.25f, 0.65f, 16f)
    )
    blossoms.forEach { (rx, ry, sizeR) ->
        val center = Offset(w * rx, h * ry)
        for (p in 0 until 5) {
            val a = (p * 72f - 18f) * (Math.PI / 180.0)
            val px = center.x + (cos(a) * sizeR * 0.68f).toFloat()
            val py = center.y + (sin(a) * sizeR * 0.68f).toFloat()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFF8BBD0), Color(0xFFF48FB1)),
                    center = Offset(px, py),
                    radius = sizeR * 0.65f
                ),
                radius = sizeR * 0.56f,
                center = Offset(px, py)
            )
        }
        drawCircle(Color(0xFFD81B60), radius = sizeR * 0.22f, center = center)
        for (s in 0 until 6) {
            val sa = (s * 60f) * (Math.PI / 180.0)
            val sx = center.x + (cos(sa) * sizeR * 0.36f).toFloat()
            val sy = center.y + (sin(sa) * sizeR * 0.36f).toFloat()
            drawLine(Color(0xFFC2185B), center, Offset(sx, sy), strokeWidth = 1.2f)
            drawCircle(Color(0xFFFFF59D), radius = 1.8f, center = Offset(sx, sy))
        }
    }
}

private fun DrawScope.drawOriginalBlackCarbonFlagWallpaper(w: Float, h: Float) {
    // Official Windows 7 Ultimate Dark Carbon Edition with Glowing 3D Flag
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF070A0F), Color(0xFF121A26), Color(0xFF080C12))
        )
    )
    val step = 24f
    var y = 0f
    while (y < h) {
        drawLine(Color(0x1480D8FF), Offset(0f, y), Offset(w, y + step), strokeWidth = 1f)
        drawLine(Color(0x0D80D8FF), Offset(w, y), Offset(0f, y + step), strokeWidth = 1f)
        y += step
    }

    val center = Offset(w * 0.50f, h * 0.45f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x4400E5FF), Color(0x181565C0), Color.Transparent),
            center = center,
            radius = w.coerceAtLeast(h) * 0.46f
        ),
        center = center,
        radius = w.coerceAtLeast(h) * 0.46f
    )
    drawAuthentic3dWindows7Flag(
        center = center,
        scale = w.coerceAtMost(h) * 0.22f
    )
}

private fun DrawScope.drawClassicTealWallpaper(w: Float, h: Float) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF007272), Color(0xFF008080), Color(0xFF006666))
        )
    )
}

@Composable
fun Win7StartOrbIcon(
    isOpenOrHovered: Boolean,
    size: Dp = 42.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val r = this.size.minDimension / 2f
        val center = Offset(r, r)

        if (isOpenOrHovered) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xAA80D8FF), Color(0x55FFD54F), Color.Transparent),
                    center = center,
                    radius = r * 1.1f
                ),
                radius = r
            )
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF2989D8), Color(0xFF10498B), Color(0xFF07234B)),
                center = Offset(r, r * 1.3f),
                radius = r
            ),
            radius = r * 0.90f,
            center = center
        )

        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xEEB3E5FC), Color(0xFF1565C0), Color(0xFF80D8FF))
            ),
            radius = r * 0.90f,
            center = center,
            style = Stroke(width = 2.2f)
        )

        val paneSize = r * 0.34f
        val gap = r * 0.06f
        val startX = center.x - paneSize - gap / 2f
        val startY = center.y - paneSize - gap / 2f

        drawRoundRect(
            color = Color(0xFFF25022),
            topLeft = Offset(startX, startY),
            size = Size(paneSize, paneSize),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawRoundRect(
            color = Color(0xFF7FBA00),
            topLeft = Offset(center.x + gap / 2f, startY),
            size = Size(paneSize, paneSize),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawRoundRect(
            color = Color(0xFF00A4EF),
            topLeft = Offset(startX, center.y + gap / 2f),
            size = Size(paneSize, paneSize),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawRoundRect(
            color = Color(0xFFFFB900),
            topLeft = Offset(center.x + gap / 2f, center.y + gap / 2f),
            size = Size(paneSize, paneSize),
            cornerRadius = CornerRadius(3f, 3f)
        )

        drawArc(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xAAFFFFFF), Color(0x11FFFFFF)),
                startY = 0f,
                endY = r
            ),
            startAngle = 195f,
            sweepAngle = 150f,
            useCenter = false,
            topLeft = Offset(r * 0.18f, r * 0.14f),
            size = Size(r * 1.64f, r * 0.95f)
        )
    }
}

@Composable
fun Win7SystemIconCanvas(
    iconType: Win7SystemIconType,
    size: Dp = 44.dp,
    isRecycleBinFull: Boolean = false,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        when (iconType) {
            Win7SystemIconType.COMPUTER -> {
                drawRoundRect(
                    color = Color(0xFF37474F),
                    topLeft = Offset(w * 0.10f, h * 0.12f),
                    size = Size(w * 0.80f, h * 0.58f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF4FC3F7), Color(0xFF0288D1), Color(0xFF01579B))
                    ),
                    topLeft = Offset(w * 0.15f, h * 0.17f),
                    size = Size(w * 0.70f, h * 0.46f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
                val glare = Path().apply {
                    moveTo(w * 0.15f, h * 0.17f)
                    lineTo(w * 0.60f, h * 0.17f)
                    lineTo(w * 0.35f, h * 0.63f)
                    lineTo(w * 0.15f, h * 0.63f)
                    close()
                }
                drawPath(glare, Color(0x44FFFFFF))
                drawRect(
                    color = Color(0xFF90A4AE),
                    topLeft = Offset(w * 0.42f, h * 0.70f),
                    size = Size(w * 0.16f, h * 0.10f)
                )
                drawRoundRect(
                    color = Color(0xFFB0BEC5),
                    topLeft = Offset(w * 0.25f, h * 0.79f),
                    size = Size(w * 0.50f, h * 0.08f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }

            Win7SystemIconType.USER_FILES -> {
                drawRoundRect(
                    color = Color(0xFFD49B1A),
                    topLeft = Offset(w * 0.12f, h * 0.16f),
                    size = Size(w * 0.36f, h * 0.14f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFE082), Color(0xFFFBC02D), Color(0xFFF57F17))
                    ),
                    topLeft = Offset(w * 0.10f, h * 0.24f),
                    size = Size(w * 0.80f, h * 0.62f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                drawRoundRect(
                    color = Color(0xFFF5F9FF),
                    topLeft = Offset(w * 0.24f, h * 0.14f),
                    size = Size(w * 0.52f, h * 0.32f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFECB3), Color(0xFFFFB300))
                    ),
                    topLeft = Offset(w * 0.10f, h * 0.38f),
                    size = Size(w * 0.80f, h * 0.48f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }

            Win7SystemIconType.NETWORK -> {
                val globeCenter = Offset(w * 0.50f, h * 0.44f)
                val rad = w * 0.33f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF80DEEA), Color(0xFF00ACC1), Color(0xFF006064)),
                        center = Offset(globeCenter.x - rad * 0.3f, globeCenter.y - rad * 0.3f),
                        radius = rad * 1.3f
                    ),
                    radius = rad,
                    center = globeCenter
                )
                drawCircle(Color(0x55A5D6A7), radius = rad * 0.65f, center = Offset(globeCenter.x + 3f, globeCenter.y - 2f))
                drawCircle(Color(0x88FFFFFF), radius = rad, center = globeCenter, style = Stroke(width = 2f))
                drawLine(Color(0xFFCFD8DC), Offset(w * 0.20f, h * 0.84f), Offset(w * 0.80f, h * 0.84f), strokeWidth = 4f)
                drawCircle(Color(0xFF00E676), radius = 4.5f, center = Offset(w * 0.50f, h * 0.84f))
            }

            Win7SystemIconType.RECYCLE_BIN -> {
                if (isRecycleBinFull) {
                    drawCircle(Color(0xFFF5F5F5), radius = w * 0.12f, center = Offset(w * 0.40f, h * 0.24f))
                    drawCircle(Color(0xFFE0E0E0), radius = w * 0.11f, center = Offset(w * 0.58f, h * 0.25f))
                }
                val binPath = Path().apply {
                    moveTo(w * 0.22f, h * 0.22f)
                    lineTo(w * 0.78f, h * 0.22f)
                    lineTo(w * 0.68f, h * 0.86f)
                    lineTo(w * 0.32f, h * 0.86f)
                    close()
                }
                drawPath(
                    path = binPath,
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xCC90CAF9), Color(0xCCE3F2FD), Color(0xCC64B5F6))
                    )
                )
                drawPath(binPath, color = Color(0xFF1E88E5), style = Stroke(width = 2f))
                drawCircle(Color(0xFF43A047), radius = w * 0.11f, center = Offset(w * 0.50f, h * 0.56f))
            }

            Win7SystemIconType.CONTROL_PANEL -> {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF42A5F5), Color(0xFF1565C0))
                    ),
                    topLeft = Offset(w * 0.12f, h * 0.14f),
                    size = Size(w * 0.76f, h * 0.72f),
                    cornerRadius = CornerRadius(7f, 7f)
                )
                for (i in 0..2) {
                    val y = h * (0.32f + i * 0.18f)
                    drawLine(Color(0xAAFFFFFF), Offset(w * 0.24f, y), Offset(w * 0.76f, y), strokeWidth = 3f)
                    val knobX = w * (0.36f + (i % 2) * 0.26f)
                    drawCircle(Color(0xFFFFCA28), radius = 4.5f, center = Offset(knobX, y))
                }
            }

            Win7SystemIconType.SET_DEFAULT_LAUNCHER -> {
                val shield = Path().apply {
                    moveTo(w * 0.50f, h * 0.10f)
                    lineTo(w * 0.84f, h * 0.22f)
                    cubicTo(w * 0.84f, h * 0.58f, w * 0.68f, h * 0.78f, w * 0.50f, h * 0.90f)
                    cubicTo(w * 0.32f, h * 0.78f, w * 0.16f, h * 0.58f, w * 0.16f, h * 0.22f)
                    close()
                }
                drawPath(
                    path = shield,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF1E88E5), Color(0xFFFFB300), Color(0xFF43A047), Color(0xFFE53935))
                    )
                )
                drawPath(shield, color = Color.White, style = Stroke(width = 2.4f))
            }

            Win7SystemIconType.AERO_TOUR_GALLERY -> {
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(w * 0.10f, h * 0.16f),
                    size = Size(w * 0.80f, h * 0.68f),
                    cornerRadius = CornerRadius(5f, 5f)
                )
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF29B6F6), Color(0xFF81C784))
                    ),
                    topLeft = Offset(w * 0.16f, h * 0.22f),
                    size = Size(w * 0.68f, h * 0.56f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
                drawCircle(Color(0xFFFFEE58), radius = w * 0.10f, center = Offset(w * 0.64f, h * 0.36f))
            }

            Win7SystemIconType.COMMAND_PROMPT -> {
                drawRoundRect(
                    color = Color(0xFF90A4AE),
                    topLeft = Offset(w * 0.10f, h * 0.16f),
                    size = Size(w * 0.80f, h * 0.68f),
                    cornerRadius = CornerRadius(5f, 5f)
                )
                drawRect(
                    color = Color(0xFF0C0C0C),
                    topLeft = Offset(w * 0.15f, h * 0.26f),
                    size = Size(w * 0.70f, h * 0.53f)
                )
                drawLine(Color.White, Offset(w * 0.24f, h * 0.38f), Offset(w * 0.36f, h * 0.46f), strokeWidth = 3f)
                drawLine(Color.White, Offset(w * 0.36f, h * 0.46f), Offset(w * 0.24f, h * 0.54f), strokeWidth = 3f)
                drawLine(Color(0xFF00E676), Offset(w * 0.42f, h * 0.54f), Offset(w * 0.58f, h * 0.54f), strokeWidth = 3f)
            }

            Win7SystemIconType.CALCULATOR -> {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF90CAF9), Color(0xFF37474F))
                    ),
                    topLeft = Offset(w * 0.18f, h * 0.12f),
                    size = Size(w * 0.64f, h * 0.76f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                drawRect(
                    color = Color(0xFFE1F5FE),
                    topLeft = Offset(w * 0.25f, h * 0.20f),
                    size = Size(w * 0.50f, h * 0.18f)
                )
                for (r in 0..2) {
                    for (c in 0..2) {
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(w * (0.25f + c * 0.18f), h * (0.44f + r * 0.13f)),
                            size = Size(w * 0.13f, h * 0.09f)
                        )
                    }
                }
            }

            Win7SystemIconType.PAINT -> {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFECB3), Color(0xFFD7CCC8), Color(0xFFA1887F))
                    ),
                    radius = w * 0.38f,
                    center = Offset(w * 0.48f, h * 0.52f)
                )
                drawCircle(Color(0xFFE53935), radius = w * 0.08f, center = Offset(w * 0.30f, h * 0.44f))
                drawCircle(Color(0xFF1E88E5), radius = w * 0.08f, center = Offset(w * 0.44f, h * 0.32f))
                drawCircle(Color(0xFF43A047), radius = w * 0.08f, center = Offset(w * 0.62f, h * 0.40f))
                drawCircle(Color(0xFFFFB300), radius = w * 0.08f, center = Offset(w * 0.60f, h * 0.60f))
                drawLine(
                    color = Color(0xFF5D4037),
                    start = Offset(w * 0.24f, h * 0.78f),
                    end = Offset(w * 0.78f, h * 0.18f),
                    strokeWidth = 5f,
                    cap = StrokeCap.Round
                )
            }

            Win7SystemIconType.NOTEPAD -> {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFE1F5FE), Color(0xFF81D4FA), Color(0xFF29B6F6))
                    ),
                    topLeft = Offset(w * 0.18f, h * 0.14f),
                    size = Size(w * 0.64f, h * 0.74f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                drawRect(
                    color = Color(0xFF0277BD),
                    topLeft = Offset(w * 0.18f, h * 0.14f),
                    size = Size(w * 0.64f, h * 0.12f)
                )
                for (i in 0..3) {
                    val y = h * (0.36f + i * 0.12f)
                    drawLine(Color(0xFF01579B), Offset(w * 0.28f, y), Offset(w * 0.72f, y), strokeWidth = 2f)
                }
            }

            Win7SystemIconType.MEDIA_PLAYER -> {
                val c = Offset(w * 0.50f, h * 0.50f)
                val r = w * 0.38f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF29B6F6), Color(0xFF0277BD), Color(0xFF01579B))
                    ),
                    radius = r,
                    center = c
                )
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(Color(0xFFFF9800), Color(0xFFFF5722), Color(0xFF29B6F6), Color(0xFFFF9800))
                    ),
                    radius = r,
                    center = c,
                    style = Stroke(width = 4.5f)
                )
                val playTri = Path().apply {
                    moveTo(w * 0.42f, h * 0.34f)
                    lineTo(w * 0.66f, h * 0.50f)
                    lineTo(w * 0.42f, h * 0.66f)
                    close()
                }
                drawPath(playTri, Color.White)
            }

            Win7SystemIconType.USER_ACCOUNTS, Win7SystemIconType.LOCK_SCREEN -> {
                drawCircle(Color(0xFF42A5F5), radius = w * 0.15f, center = Offset(w * 0.38f, h * 0.32f))
                drawRoundRect(
                    color = Color(0xFF1976D2),
                    topLeft = Offset(w * 0.20f, h * 0.48f),
                    size = Size(w * 0.36f, h * 0.30f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                drawCircle(Color(0xFFFFCA28), radius = w * 0.16f, center = Offset(w * 0.66f, h * 0.64f))
                drawCircle(Color(0xFFF57F17), radius = w * 0.06f, center = Offset(w * 0.66f, h * 0.64f))
            }

            Win7SystemIconType.STICKY_NOTES -> {
                drawRoundRect(
                    color = Color(0xFFFFF176),
                    topLeft = Offset(w * 0.14f, h * 0.14f),
                    size = Size(w * 0.72f, h * 0.72f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                drawRect(
                    color = Color(0xFFFBC02D),
                    topLeft = Offset(w * 0.14f, h * 0.14f),
                    size = Size(w * 0.72f, h * 0.14f)
                )
            }

            Win7SystemIconType.GADGETS, Win7SystemIconType.TASK_MANAGER, Win7SystemIconType.ANDROID_APP -> {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFB74D), Color(0xFFF57C00))
                    ),
                    radius = w * 0.36f,
                    center = Offset(w * 0.5f, h * 0.5f)
                )
                drawCircle(
                    color = Color.White,
                    radius = w * 0.36f,
                    center = Offset(w * 0.5f, h * 0.5f),
                    style = Stroke(width = 2.2f)
                )
                drawLine(Color.White, Offset(w * 0.5f, h * 0.5f), Offset(w * 0.5f, h * 0.26f), strokeWidth = 2.5f)
                drawLine(Color.White, Offset(w * 0.5f, h * 0.5f), Offset(w * 0.68f, h * 0.5f), strokeWidth = 2.5f)
            }
        }
    }
}

@Composable
fun Win7UserAvatarCanvas(
    preset: Win7AvatarPreset,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        when (preset) {
            Win7AvatarPreset.SUNFLOWER -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E88E5), Color(0xFF81D4FA))
                    )
                )
                val center = Offset(w * 0.5f, h * 0.52f)
                for (i in 0 until 12) {
                    val angle = (i * 30f) * (Math.PI / 180.0)
                    val px = center.x + (cos(angle) * w * 0.26f).toFloat()
                    val py = center.y + (sin(angle) * h * 0.26f).toFloat()
                    drawCircle(Color(0xFFFFB300), radius = w * 0.11f, center = Offset(px, py))
                }
                drawCircle(Color(0xFF4E342E), radius = w * 0.17f, center = center)
            }
            Win7AvatarPreset.ASTRONAUT -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0A192F), Color(0xFF1A365D))
                    )
                )
                drawCircle(Color.White, radius = w * 0.28f, center = Offset(w * 0.5f, h * 0.48f))
                drawCircle(Color(0xFF29B6F6), radius = w * 0.19f, center = Offset(w * 0.5f, h * 0.46f))
            }
            Win7AvatarPreset.CHESS -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF283593), Color(0xFF5C6BC0))
                    )
                )
                drawCircle(Color(0xFFE8EAF6), radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.34f))
                drawRoundRect(
                    color = Color(0xFFE8EAF6),
                    topLeft = Offset(w * 0.30f, h * 0.50f),
                    size = Size(w * 0.40f, h * 0.32f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
            Win7AvatarPreset.SOCCER -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2E7D32), Color(0xFF66BB6A))
                    )
                )
                drawCircle(Color.White, radius = w * 0.28f, center = Offset(w * 0.5f, h * 0.5f))
                drawCircle(Color(0xFF212121), radius = w * 0.10f, center = Offset(w * 0.5f, h * 0.5f))
            }
            Win7AvatarPreset.BUTTERFLY -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF00695C), Color(0xFF4DB6AC))
                    )
                )
                drawCircle(Color(0xFFFF7043), radius = w * 0.20f, center = Offset(w * 0.34f, h * 0.44f))
                drawCircle(Color(0xFFFF7043), radius = w * 0.20f, center = Offset(w * 0.66f, h * 0.44f))
                drawCircle(Color(0xFFFFAB91), radius = w * 0.14f, center = Offset(w * 0.38f, h * 0.64f))
                drawCircle(Color(0xFFFFAB91), radius = w * 0.14f, center = Offset(w * 0.62f, h * 0.64f))
            }
        }
    }
}
