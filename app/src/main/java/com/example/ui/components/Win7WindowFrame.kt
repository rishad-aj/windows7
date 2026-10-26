package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AeroGlassSwatch
import com.example.model.Win7WindowInstance
import com.example.ui.theme.Win7CloseRedBorder
import com.example.ui.theme.Win7CloseRedBottom
import com.example.ui.theme.Win7CloseRedTop
import kotlin.math.roundToInt

@Composable
fun Win7AeroWindowFrame(
    window: Win7WindowInstance,
    isActive: Boolean,
    aeroSwatch: AeroGlassSwatch,
    aeroTransparency: Boolean,
    glassIntensity: Float,
    isAeroPeekActive: Boolean,
    onFocus: () -> Unit,
    onMinimize: () -> Unit,
    onMaximizeRestore: () -> Unit,
    onClose: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val windowShape = if (window.isMaximized) RoundedCornerShape(0.dp) else RoundedCornerShape(7.dp)

    val outerModifier = if (window.isMaximized) {
        modifier
            .fillMaxSize()
            .padding(bottom = 0.dp)
    } else {
        modifier
            .fillMaxSize()
            .padding(
                start = 6.dp,
                end = 6.dp,
                top = 8.dp,
                bottom = 8.dp
            )
            .offset {
                IntOffset(
                    x = window.offsetX.dp.roundToPx().coerceIn(-30, 80),
                    y = window.offsetY.dp.roundToPx().coerceIn(0, 120)
                )
            }
    }

    // In Aero Peek mode, render glowing translucent glass wireframe outline
    if (isAeroPeekActive) {
        Box(
            modifier = outerModifier
                .clip(windowShape)
                .background(Color(0x1880D8FF))
                .border(1.5.dp, Color(0xBBFFFFFF), windowShape)
                .clickable { onFocus() }
        ) {
            Text(
                text = window.title,
                color = Color(0xAAFFFFFF),
                fontSize = 11.sp,
                modifier = Modifier.padding(10.dp)
            )
        }
        return
    }

    val baseAlpha = if (aeroTransparency) {
        if (isActive) glassIntensity.coerceIn(0.55f, 0.92f) else (glassIntensity * 0.72f).coerceIn(0.45f, 0.78f)
    } else {
        0.98f
    }

    val glassTopColor = aeroSwatch.glassTint.copy(alpha = baseAlpha)
    val glassBottomColor = aeroSwatch.taskbarTint.copy(alpha = (baseAlpha + 0.08f).coerceAtMost(0.98f))

    Box(
        modifier = outerModifier
            .shadow(
                elevation = if (isActive) 18.dp else 8.dp,
                shape = windowShape,
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .clip(windowShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(glassTopColor, glassBottomColor)
                )
            )
            .drawBehind {
                // Iconic Windows 7 diagonal glass reflection streaks
                val w = size.width
                val h = size.height
                val streak1 = Path().apply {
                    moveTo(w * 0.08f, 0f)
                    lineTo(w * 0.26f, 0f)
                    lineTo(w * 0.04f, h * 0.45f)
                    lineTo(0f, h * 0.35f)
                    close()
                }
                drawPath(streak1, Color(0x28FFFFFF))

                val streak2 = Path().apply {
                    moveTo(w * 0.58f, 0f)
                    lineTo(w * 0.72f, 0f)
                    lineTo(w * 0.48f, h * 0.50f)
                    lineTo(w * 0.36f, h * 0.50f)
                    close()
                }
                drawPath(streak2, Color(0x1EFFFFFF))

                // Top specular inner highlight line
                drawLine(
                    color = Color(0xAAFFFFFF),
                    start = Offset(0f, 1f),
                    end = Offset(w, 1f),
                    strokeWidth = 1.5f
                )
            }
            .border(
                width = 1.dp,
                color = if (isActive) Color(0xBB091D34) else Color(0x771E354D),
                shape = windowShape
            )
            .pointerInput(window.id) {
                detectTapGestures(onTap = { onFocus() })
            }
            .testTag("aero_window_${window.type.name.lowercase()}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (window.isMaximized) 2.dp else 5.dp, vertical = if (window.isMaximized) 2.dp else 5.dp)
        ) {
            // Windows 7 Aero Glass Title Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .pointerInput(window.id) {
                        detectTapGestures(
                            onDoubleTap = { onMaximizeRestore() },
                            onPress = { onFocus() }
                        )
                    }
                    .pointerInput(window.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onDrag(
                                dragAmount.x / density,
                                dragAmount.y / density
                            )
                        }
                    }
                    .padding(start = 6.dp, end = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Window Icon
                Win7SystemIconCanvas(
                    iconType = window.type.iconType,
                    size = 18.dp
                )
                Spacer(modifier = Modifier.width(6.dp))

                // Title with soft white glow/drop shadow like Windows 7 DWM
                Text(
                    text = window.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        shadow = Shadow(
                            color = Color(0xCC000000),
                            offset = Offset(0f, 1f),
                            blurRadius = 4f
                        )
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Connected Windows 7 Caption Control Capsule (Minimize, Maximize, Red Close)
                Win7CaptionButtonsGroup(
                    isMaximized = window.isMaximized,
                    onMinimize = onMinimize,
                    onMaximizeRestore = onMaximizeRestore,
                    onClose = onClose
                )
            }

            // Inner Window Client Area with crisp inset border
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFF8FAFD))
                    .border(1.dp, Color(0xFF567494), RoundedCornerShape(3.dp))
            ) {
                content()
            }
        }
    }
}

@Composable
private fun Win7CaptionButtonsGroup(
    isMaximized: Boolean,
    onMinimize: () -> Unit,
    onMaximizeRestore: () -> Unit,
    onClose: () -> Unit
) {
    val capsuleShape = RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp, topStart = 3.dp, topEnd = 3.dp)
    Row(
        modifier = Modifier
            .height(22.dp)
            .clip(capsuleShape)
            .border(1.dp, Color(0xAA162E48), capsuleShape),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        // Minimize button (_)
        Box(
            modifier = Modifier
                .width(27.dp)
                .height(22.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0x66FFFFFF), Color(0x22FFFFFF), Color(0x44102A45))
                    )
                )
                .clickable(onClick = onMinimize)
                .testTag("window_minimize_button"),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .width(9.dp)
                    .height(2.dp)
                    .background(Color.White)
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(22.dp)
                .background(Color(0x88162E48))
        )

        // Maximize / Restore button (□)
        Box(
            modifier = Modifier
                .width(27.dp)
                .height(22.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0x66FFFFFF), Color(0x22FFFFFF), Color(0x44102A45))
                    )
                )
                .clickable(onClick = onMaximizeRestore)
                .testTag("window_maximize_button"),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(if (isMaximized) 8.dp else 9.dp)
                    .border(1.5.dp, Color.White)
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(22.dp)
                .background( Win7CloseRedBorder )
        )

        // Iconic Wider Red Glowing Windows 7 Close Button (✕)
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(22.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Win7CloseRedTop,
                            Color(0xFFE04338),
                            Win7CloseRedBottom
                        )
                    )
                )
                .clickable(onClick = onClose)
                .testTag("window_close_button"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✕",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
