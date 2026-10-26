package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PersistedLauncherSettings
import com.example.model.DesktopIconSize
import com.example.model.DesktopSortMode
import com.example.model.StickyNoteItem
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Win7DesktopGadgetsColumn(
    settings: PersistedLauncherSettings,
    onCloseGadget: (String) -> Unit,
    onAddStickyNote: () -> Unit,
    onUpdateStickyNote: (String, String) -> Unit,
    onCycleStickyColor: (String) -> Unit,
    onDeleteStickyNote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(136.dp)
            .padding(top = 8.dp, end = 6.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (settings.showClockGadget) {
            Win7ClockGadgetCard(onClose = { onCloseGadget("clock") })
        }
        if (settings.showCpuMeterGadget) {
            Win7CpuMeterGadgetCard(onClose = { onCloseGadget("cpu") })
        }
        if (settings.showCalendarGadget) {
            Win7CalendarGadgetCard(onClose = { onCloseGadget("calendar") })
        }
        if (settings.showWeatherGadget) {
            Win7WeatherGadgetCard(onClose = { onCloseGadget("weather") })
        }
        if (settings.showStickyNotesOnDesktop) {
            settings.stickyNotes.take(2).forEach { note ->
                Win7StickyNoteDesktopWidget(
                    note = note,
                    onAdd = onAddStickyNote,
                    onTextChange = { onUpdateStickyNote(note.id, it) },
                    onCycleColor = { onCycleStickyColor(note.id) },
                    onDelete = { onDeleteStickyNote(note.id) }
                )
            }
        }
    }
}

@Composable
private fun Win7ClockGadgetCard(onClose: () -> Unit) {
    var timeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            timeMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }

    Box(
        modifier = Modifier
            .size(106.dp)
            .testTag("gadget_clock")
    ) {
        Canvas(modifier = Modifier.size(102.dp).align(Alignment.Center)) {
            val cal = Calendar.getInstance().apply { timeInMillis = timeMillis }
            val sec = cal.get(Calendar.SECOND)
            val min = cal.get(Calendar.MINUTE)
            val hr = cal.get(Calendar.HOUR)

            val r = size.minDimension / 2f
            val c = Offset(r, r)

            // Outer dark metallic bezel
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF263238), Color(0xFF0D151C))
                ),
                radius = r * 0.94f,
                center = c
            )
            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFCFD8DC), Color(0xFF455A64), Color(0xFF90A4AE))
                ),
                radius = r * 0.94f,
                center = c,
                style = Stroke(width = 3.5f)
            )

            // Hour markers
            for (i in 0 until 12) {
                val a = (i * 30 - 90) * (Math.PI / 180.0)
                val p1 = Offset(c.x + (cos(a) * r * 0.72f).toFloat(), c.y + (sin(a) * r * 0.72f).toFloat())
                val p2 = Offset(c.x + (cos(a) * r * 0.84f).toFloat(), c.y + (sin(a) * r * 0.84f).toFloat())
                drawLine(
                    color = if (i % 3 == 0) Color(0xFF80D8FF) else Color(0xFFB0BEC5),
                    start = p1,
                    end = p2,
                    strokeWidth = if (i % 3 == 0) 2.5f else 1.3f
                )
            }

            // Hour hand
            val hrAngle = ((hr + min / 60f) * 30f - 90f) * (Math.PI / 180.0)
            drawLine(
                color = Color.White,
                start = c,
                end = Offset(c.x + (cos(hrAngle) * r * 0.45f).toFloat(), c.y + (sin(hrAngle) * r * 0.45f).toFloat()),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )

            // Minute hand
            val minAngle = ((min + sec / 60f) * 6f - 90f) * (Math.PI / 180.0)
            drawLine(
                color = Color(0xFFE0F7FA),
                start = c,
                end = Offset(c.x + (cos(minAngle) * r * 0.66f).toFloat(), c.y + (sin(minAngle) * r * 0.66f).toFloat()),
                strokeWidth = 2.4f,
                cap = StrokeCap.Round
            )

            // Sweeping red second hand
            val secAngle = (sec * 6f - 90f) * (Math.PI / 180.0)
            drawLine(
                color = Color(0xFFFF5252),
                start = c,
                end = Offset(c.x + (cos(secAngle) * r * 0.76f).toFloat(), c.y + (sin(secAngle) * r * 0.76f).toFloat()),
                strokeWidth = 1.4f,
                cap = StrokeCap.Round
            )
            drawCircle(Color(0xFFFF5252), radius = 3.2f, center = c)
        }

        GadgetCloseButton(onClick = onClose, modifier = Modifier.align(Alignment.TopEnd))
    }
}

@Composable
private fun Win7CpuMeterGadgetCard(onClose: () -> Unit) {
    var cpuLoad by remember { mutableIntStateOf(24) }
    var ramUsage by remember { mutableIntStateOf(52) }

    LaunchedEffect(Unit) {
        while (true) {
            val rt = Runtime.getRuntime()
            val usedMem = rt.totalMemory() - rt.freeMemory()
            val maxMem = rt.maxMemory().coerceAtLeast(1L)
            ramUsage = ((usedMem * 100f) / maxMem).toInt().coerceIn(28, 86)
            cpuLoad = (14 + (System.currentTimeMillis() / 1000L % 27)).toInt()
            delay(2000L)
        }
    }

    Box(
        modifier = Modifier
            .width(124.dp)
            .height(82.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xDD263238), Color(0xDD101820))
                )
            )
            .border(1.5.dp, Color(0xAA90A4AE), RoundedCornerShape(12.dp))
            .padding(6.dp)
            .testTag("gadget_cpu_meter")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Bigger Dial: CPU
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GaugeDialCanvas(percent = cpuLoad, dialSize = 48.dp, needleColor = Color(0xFFFF7043))
                Text(text = "CPU $cpuLoad%", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            // Right Dial: RAM
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GaugeDialCanvas(percent = ramUsage, dialSize = 42.dp, needleColor = Color(0xFF40C4FF))
                Text(text = "RAM $ramUsage%", fontSize = 9.sp, color = Color(0xFFB3E5FC))
            }
        }
        GadgetCloseButton(onClick = onClose, modifier = Modifier.align(Alignment.TopEnd))
    }
}

@Composable
private fun GaugeDialCanvas(
    percent: Int,
    dialSize: androidx.compose.ui.unit.Dp,
    needleColor: Color
) {
    Canvas(modifier = Modifier.size(dialSize)) {
        val r = size.minDimension / 2f
        val c = Offset(r, r)
        drawCircle(color = Color(0xFF0B1117), radius = r * 0.92f, center = c)
        drawCircle(color = Color(0xFF78909C), radius = r * 0.92f, center = c, style = Stroke(width = 2f))
        drawArc(
            color = Color(0xFF37474F),
            startAngle = 135f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(r * 0.22f, r * 0.22f),
            size = Size(r * 1.56f, r * 1.56f),
            style = Stroke(width = 3f)
        )
        val angleDeg = 135f + (percent / 100f) * 270f
        val angleRad = angleDeg * (Math.PI / 180.0)
        drawLine(
            color = needleColor,
            start = c,
            end = Offset(c.x + (cos(angleRad) * r * 0.72f).toFloat(), c.y + (sin(angleRad) * r * 0.72f).toFloat()),
            strokeWidth = 2.2f,
            cap = StrokeCap.Round
        )
        drawCircle(color = needleColor, radius = 2.5f, center = c)
    }
}

@Composable
private fun Win7CalendarGadgetCard(onClose: () -> Unit) {
    val now = remember { Date() }
    val dayOfWeek = remember { SimpleDateFormat("EEEE", Locale.getDefault()).format(now).uppercase() }
    val dayNumber = remember { SimpleDateFormat("d", Locale.getDefault()).format(now) }
    val monthYear = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(now) }

    Box(
        modifier = Modifier
            .width(108.dp)
            .shadow(6.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFB0BEC5), RoundedCornerShape(6.dp))
            .testTag("gadget_calendar")
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFF7043), Color(0xFFD84315))
                        )
                    )
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = dayOfWeek, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = dayNumber,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF263238),
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = monthYear,
                fontSize = 9.sp,
                color = Color(0xFF546E7A),
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        GadgetCloseButton(onClick = onClose, modifier = Modifier.align(Alignment.TopEnd))
    }
}

@Composable
private fun Win7WeatherGadgetCard(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .shadow(6.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xDD1565C0), Color(0xDD0D47A1))
                )
            )
            .border(1.dp, Color(0xAA80D8FF), RoundedCornerShape(8.dp))
            .padding(8.dp)
            .testTag("gadget_weather")
    ) {
        Column {
            Text(text = "72°F", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "Mostly Sunny", color = Color(0xFFE1F5FE), fontSize = 10.sp)
            Text(text = "Redmond, WA", color = Color(0xFF90CAF9), fontSize = 9.sp)
        }
        GadgetCloseButton(onClick = onClose, modifier = Modifier.align(Alignment.TopEnd))
    }
}

@Composable
private fun GadgetCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x88000000))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close gadget",
            tint = Color.White,
            modifier = Modifier.size(10.dp)
        )
    }
}

@Composable
fun Win7StickyNoteDesktopWidget(
    note: StickyNoteItem,
    onAdd: () -> Unit,
    onTextChange: (String) -> Unit,
    onCycleColor: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (headerBg, bodyBg) = when (note.colorIndex % 4) {
        0 -> Color(0xFFF5E663) to Color(0xFFFEF9B8) // Classic Yellow
        1 -> Color(0xFF90CAF9) to Color(0xFFE3F2FD) // Aero Blue
        2 -> Color(0xFFA5D6A7) to Color(0xFFE8F5E9) // Mint Green
        else -> Color(0xFFF48FB1) to Color(0xFFFCE4EC) // Pastel Pink
    }

    Column(
        modifier = modifier
            .width(134.dp)
            .shadow(6.dp, RoundedCornerShape(3.dp))
            .clip(RoundedCornerShape(3.dp))
            .background(bodyBg)
            .border(1.dp, Color(0x55000000), RoundedCornerShape(3.dp))
            .testTag("desktop_sticky_note_${note.id}")
    ) {
        // Sticky Note Top Bar (+, color, x)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(headerBg)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "New Sticky Note",
                tint = Color(0xFF37474F),
                modifier = Modifier
                    .size(13.dp)
                    .clickable(onClick = onAdd)
            )
            Text(
                text = "Sticky Note",
                fontSize = 9.sp,
                color = Color(0xFF455A64),
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    imageVector = Icons.Default.ColorLens,
                    contentDescription = "Change Note Color",
                    tint = Color(0xFF37474F),
                    modifier = Modifier
                        .size(12.dp)
                        .clickable(onClick = onCycleColor)
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete Sticky Note",
                    tint = Color(0xFF37474F),
                    modifier = Modifier
                        .size(12.dp)
                        .clickable(onClick = onDelete)
                )
            }
        }

        BasicTextField(
            value = note.text,
            onValueChange = onTextChange,
            textStyle = TextStyle(
                fontSize = 10.sp,
                lineHeight = 13.sp,
                color = Color(0xFF212121)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        )
    }
}

@Composable
fun Win7DesktopContextMenu(
    settings: PersistedLauncherSettings,
    isDefaultLauncher: Boolean,
    onSelectIconSize: (DesktopIconSize) -> Unit,
    onSelectSortMode: (DesktopSortMode) -> Unit,
    onToggleShowDesktopIcons: () -> Unit,
    onToggleAutoArrange: () -> Unit,
    onToggleAlignToGrid: () -> Unit,
    onResetIconPositions: () -> Unit,
    onAddAllAppsToDesktop: () -> Unit,
    onRefresh: () -> Unit,
    onNewStickyNote: () -> Unit,
    onOpenFlip3d: () -> Unit,
    onLockComputer: () -> Unit,
    onOpenGadgets: () -> Unit,
    onOpenPersonalize: () -> Unit,
    onSetDefaultLauncher: () -> Unit,
    modifier: Modifier = Modifier
) {
    val menuShape = RoundedCornerShape(3.dp)
    Column(
        modifier = modifier
            .width(244.dp)
            .shadow(12.dp, menuShape)
            .clip(menuShape)
            .background(Color(0xFFF3F5F8))
            .border(1.dp, Color(0xFF8E9BAE), menuShape)
            .padding(vertical = 3.dp)
            .testTag("desktop_context_menu")
    ) {
        DesktopContextItem(
            icon = Icons.Default.ViewModule,
            label = "View: ${
                when (settings.iconSize) {
                    DesktopIconSize.LARGE -> "Large → Medium"
                    DesktopIconSize.MEDIUM -> "Medium → Small"
                    DesktopIconSize.SMALL -> "Small → Large"
                }
            }",
            onClick = {
                val next = when (settings.iconSize) {
                    DesktopIconSize.LARGE -> DesktopIconSize.MEDIUM
                    DesktopIconSize.MEDIUM -> DesktopIconSize.SMALL
                    DesktopIconSize.SMALL -> DesktopIconSize.LARGE
                }
                onSelectIconSize(next)
            }
        )

        DesktopContextItem(
            icon = null,
            label = if (settings.autoArrangeIcons) "✓ Auto arrange icons" else "Auto arrange icons (Free Move ON)",
            onClick = onToggleAutoArrange
        )

        DesktopContextItem(
            icon = null,
            label = if (settings.alignIconsToGrid) "✓ Align icons to grid" else "Align icons to grid",
            onClick = onToggleAlignToGrid
        )

        if (settings.customIconPositions.isNotEmpty()) {
            DesktopContextItem(
                icon = Icons.Default.Refresh,
                label = "Reset Icon Positions",
                onClick = onResetIconPositions
            )
        }

        DesktopContextItem(
            icon = null,
            label = if (settings.showDesktopIcons) "✓ Show desktop icons" else "Show desktop icons",
            onClick = onToggleShowDesktopIcons
        )

        DesktopContextItem(
            icon = Icons.Default.SortByAlpha,
            label = "Sort by: ${settings.sortMode.label} (Next)",
            onClick = {
                val entries = DesktopSortMode.entries
                val next = entries[(settings.sortMode.ordinal + 1) % entries.size]
                onSelectSortMode(next)
            }
        )

        DesktopContextItem(
            icon = Icons.Default.Refresh,
            label = "Refresh (F5)",
            onClick = onRefresh
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 3.dp), color = Color(0xFFD5DCE6))

        DesktopContextItem(
            icon = Icons.Default.Add,
            label = "Add All Android Apps to Desktop",
            onClick = onAddAllAppsToDesktop
        )

        DesktopContextItem(
            icon = Icons.Default.Add,
            label = "New Sticky Note",
            onClick = onNewStickyNote
        )

        DesktopContextItem(
            icon = null,
            label = "Aero Flip 3D (Win + Tab)",
            onClick = onOpenFlip3d
        )

        DesktopContextItem(
            icon = null,
            label = "Lock Computer (Win + L)",
            onClick = onLockComputer
        )

        if (!isDefaultLauncher) {
            DesktopContextItem(
                icon = Icons.Default.Home,
                label = "Set as Default Launcher",
                onClick = onSetDefaultLauncher
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 3.dp), color = Color(0xFFD5DCE6))

        DesktopContextItem(
            icon = Icons.Default.Widgets,
            label = "Gadgets",
            onClick = onOpenGadgets
        )

        DesktopContextItem(
            icon = Icons.Default.Settings,
            label = "Personalize (Wallpapers & Aero)",
            onClick = onOpenPersonalize
        )
    }
}

@Composable
private fun DesktopContextItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(22.dp), contentAlignment = Alignment.CenterStart) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF2B579A),
                    modifier = Modifier.size(15.dp)
                )
            }
        }
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF1A1A1A)
        )
    }
}
