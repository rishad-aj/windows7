package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.PersistedLauncherSettings
import com.example.model.MediaTrack
import com.example.model.PaintStroke
import com.example.model.Win7AvatarPreset
import com.example.model.Win7WindowInstance
import com.example.ui.theme.ConsolasFontFamily
import com.example.ui.theme.Win7ControlPanelHeaderBlue
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sin

@Composable
fun Win7PaintWindowContent() {
    val strokes = remember { mutableStateListOf<PaintStroke>() }
    var activePoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var selectedColor by remember { mutableStateOf(Color(0xFF1565C0)) }
    var selectedWidth by remember { mutableFloatStateOf(6f) }
    var isEraser by remember { mutableStateOf(false) }

    val palette = listOf(
        Color(0xFF000000),
        Color(0xFFE53935),
        Color(0xFFFF8F00),
        Color(0xFFFDD835),
        Color(0xFF43A047),
        Color(0xFF00ACC1),
        Color(0xFF1565C0),
        Color(0xFF8E24AA),
        Color(0xFFF06292),
        Color(0xFF795548)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDCE6F4))
            .testTag("paint_window_content")
    ) {
        // Windows 7 Paint Sky-Blue Ribbon Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFF5F9FF), Color(0xFFDCEBFA))
                    )
                )
                .border(0.5.dp, Color(0xFFA0B5CA))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Brush / Eraser Toggle
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (!isEraser) Color(0xFFFFECB3) else Color.White)
                    .border(1.dp, if (!isEraser) Color(0xFFFFB300) else Color(0xFFB0BEC5), RoundedCornerShape(4.dp))
                    .clickable { isEraser = false }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Brush, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Brush", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isEraser) Color(0xFFFFECB3) else Color.White)
                    .border(1.dp, if (isEraser) Color(0xFFFFB300) else Color(0xFFB0BEC5), RoundedCornerShape(4.dp))
                    .clickable { isEraser = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Eraser", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
            }

            // Brush Sizes
            listOf(3f to "Thin", 7f to "Med", 14f to "Thick").forEach { (w, label) ->
                val active = selectedWidth == w
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (active) Color(0xFFD6EBFF) else Color.White)
                        .border(1.dp, Color(0xFF90CAF9), RoundedCornerShape(4.dp))
                        .clickable { selectedWidth = w }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(label, fontSize = 10.sp, color = Color(0xFF0D47A1))
                }
            }

            // Color Swatches
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                palette.forEach { swatch ->
                    val chosen = !isEraser && selectedColor == swatch
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(swatch)
                            .border(
                                width = if (chosen) 2.dp else 1.dp,
                                color = if (chosen) Color.White else Color(0xFF78909C),
                                shape = RoundedCornerShape(3.dp)
                            )
                            .clickable {
                                selectedColor = swatch
                                isEraser = false
                            }
                    )
                }
            }

            // Clear Canvas Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFFFEBEE))
                    .border(1.dp, Color(0xFFEF9A9A), RoundedCornerShape(4.dp))
                    .clickable {
                        strokes.clear()
                        activePoints = emptyList()
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Clear, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Clear", fontSize = 10.sp, color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Interactive White Paint Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(4.dp, RoundedCornerShape(2.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFF8EA2B8))
                .pointerInput(selectedColor, selectedWidth, isEraser) {
                    detectDragGestures(
                        onDragStart = { startOffset ->
                            activePoints = listOf(startOffset)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            activePoints = activePoints + change.position
                        },
                        onDragEnd = {
                            if (activePoints.isNotEmpty()) {
                                strokes.add(
                                    PaintStroke(
                                        points = activePoints,
                                        color = if (isEraser) Color.White else selectedColor,
                                        strokeWidth = if (isEraser) selectedWidth * 2.4f else selectedWidth
                                    )
                                )
                                activePoints = emptyList()
                            }
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val allStrokes = if (activePoints.isNotEmpty()) {
                    strokes + PaintStroke(
                        points = activePoints,
                        color = if (isEraser) Color.White else selectedColor,
                        strokeWidth = if (isEraser) selectedWidth * 2.4f else selectedWidth
                    )
                } else strokes

                allStrokes.forEach { stroke ->
                    if (stroke.points.size == 1) {
                        drawCircle(
                            color = stroke.color,
                            radius = stroke.strokeWidth / 2f,
                            center = stroke.points.first()
                        )
                    } else if (stroke.points.size > 1) {
                        val path = Path().apply {
                            moveTo(stroke.points.first().x, stroke.points.first().y)
                            for (i in 1 until stroke.points.size) {
                                lineTo(stroke.points[i].x, stroke.points[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = stroke.color,
                            style = Stroke(
                                width = stroke.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            if (strokes.isEmpty() && activePoints.isEmpty()) {
                Text(
                    text = "Draw on the Windows 7 Paint canvas with your finger...",
                    fontSize = 11.sp,
                    color = Color(0xFFB0BEC5),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Paint Bottom Status Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp)
                .background(Color(0xFFEDF2F8))
                .border(0.5.dp, Color(0xFFB0C4DE))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Strokes: ${strokes.size} • Brush: ${selectedWidth.toInt()}px", fontSize = 9.sp, color = Color(0xFF334155))
            Text("100% Zoom", fontSize = 9.sp, color = Color(0xFF334155))
        }
    }
}

@Composable
fun Win7NotepadWindowContent(
    content: String,
    onContentChange: (String) -> Unit
) {
    val linesCount = remember(content) { content.lines().size.coerceAtLeast(1) }
    val charsCount = remember(content) { content.length }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("notepad_window_content")
    ) {
        // Classic Windows 7 Notepad Menu Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFF8FAFE), Color(0xFFE4ECF7))
                    )
                )
                .border(0.5.dp, Color(0xFFB0C4DE))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "File",
                fontSize = 11.sp,
                color = Color(0xFF1E293B),
                modifier = Modifier.clickable {
                    onContentChange("")
                }
            )
            Text(
                text = "Insert Time/Date (F5)",
                fontSize = 11.sp,
                color = Color(0xFF003399),
                modifier = Modifier.clickable {
                    val stamp = SimpleDateFormat("h:mm a M/d/yyyy", Locale.getDefault()).format(Date())
                    onContentChange("$content\n$stamp")
                }
            )
            Text(
                text = "Clear All",
                fontSize = 11.sp,
                color = Color(0xFFC62828),
                modifier = Modifier.clickable { onContentChange("") }
            )
        }

        // Main Notepad Text Area
        BasicTextField(
            value = content,
            onValueChange = onContentChange,
            textStyle = TextStyle(
                fontFamily = ConsolasFontFamily,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color(0xFF111827)
            ),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(10.dp)
                .testTag("notepad_editor_field")
        )

        // Classic Windows 7 Notepad Bottom Status Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp)
                .background(Color(0xFFF1F4F9))
                .border(0.5.dp, Color(0xFFCBD5E1))
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Ln $linesCount, Col ${charsCount % 80 + 1}", fontSize = 10.sp, color = Color(0xFF475569))
            Text("Windows (CRLF)  |  UTF-8", fontSize = 10.sp, color = Color(0xFF475569))
        }
    }
}

@Composable
fun Win7MediaPlayerWindowContent(
    onPlaySampleNote: () -> Unit
) {
    val sampleTracks = remember {
        listOf(
            MediaTrack("t1", "Kalimba", "Mr. Scruff", "Ninja Tuna (Win7 Sample Music)", 348, Color(0xFF00E5FF)),
            MediaTrack("t2", "Maid with the Flaxen Hair", "Richard Stoltzman", "Fine Music, Vol. 1", 170, Color(0xFF76FF03)),
            MediaTrack("t3", "Sleep Away", "Bob Acri", "Bob Acri (2004)", 200, Color(0xFFFFD54F)),
            MediaTrack("t4", "Windows 7 Aero Logon Chord", "Microsoft Sound Design", "Windows 7 Media", 12, Color(0xFFE040FB))
        )
    }

    var currentTrackIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    var elapsedSec by remember { mutableIntStateOf(42) }
    var animTick by remember { mutableFloatStateOf(0f) }

    val currentTrack = sampleTracks[currentTrackIndex]

    LaunchedEffect(isPlaying, currentTrackIndex) {
        while (isPlaying) {
            delay(80L)
            animTick += 0.25f
            if (animTick.toInt() % 12 == 0) {
                elapsedSec = (elapsedSec + 1) % currentTrack.durationSec
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1526))
            .testTag("media_player_content")
    ) {
        // 1. Top Now Playing Visualizer + Playlist Split
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Left: Animated Windows Media Player 12 Alchemy / Bars Visualizer
            Box(
                modifier = Modifier
                    .weight(0.58f)
                    .fillMaxHeight()
                    .background(Color(0xFF050A14))
                    .padding(8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    // Ambient radial glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(currentTrack.accentColor.copy(alpha = 0.35f), Color.Transparent)
                        ),
                        radius = w * 0.55f,
                        center = Offset(w * 0.5f, h * 0.5f)
                    )

                    // 18 Animated Equalizer Bars
                    val barCount = 18
                    val barWidth = (w / barCount) * 0.68f
                    for (i in 0 until barCount) {
                        val wave = if (isPlaying) {
                            (0.25f + 0.65f * kotlin.math.abs(sin(animTick + i * 0.55f))).coerceIn(0.15f, 0.92f)
                        } else {
                            0.18f
                        }
                        val barH = h * wave * 0.72f
                        val x = i * (w / barCount) + 4f
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.White, currentTrack.accentColor, Color(0xFF0277BD)),
                                startY = h - barH,
                                endY = h
                            ),
                            topLeft = Offset(x, h - barH),
                            size = Size(barWidth, barH)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = currentTrack.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${currentTrack.artist} • ${currentTrack.album}",
                        color = Color(0xFF90CAF9),
                        fontSize = 10.sp
                    )
                }
            }

            // Right: Sample Music Library Playlist
            Column(
                modifier = Modifier
                    .weight(0.42f)
                    .fillMaxHeight()
                    .background(Color(0xFFF4F8FD))
                    .border(0.5.dp, Color(0xFF90A4AE))
                    .verticalScroll(rememberScrollState())
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Sample Music Library",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Win7ControlPanelHeaderBlue
                )
                sampleTracks.forEachIndexed { idx, track ->
                    val selected = idx == currentTrackIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (selected) Color(0xFFD6EBFF) else Color.White)
                            .border(1.dp, if (selected) Color(0xFF2979FF) else Color(0xFFCFD8DC), RoundedCornerShape(4.dp))
                            .clickable {
                                currentTrackIndex = idx
                                elapsedSec = 0
                                isPlaying = true
                                onPlaySampleNote()
                            }
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = Color(0xFF1E293B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = track.artist,
                                fontSize = 9.sp,
                                color = Color(0xFF64748B),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // 2. Signature Windows Media Player 12 Glossy Dark-Blue Curved Bottom Transport Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1B3A64), Color(0xFF0B1E3B), Color(0xFF061226))
                    )
                )
                .border(1.dp, Color(0xFF3E6B9E))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = String.format("%02d:%02d / %02d:%02d", elapsedSec / 60, elapsedSec % 60, currentTrack.durationSec / 60, currentTrack.durationSec % 60),
                color = Color(0xFFB3E5FC),
                fontSize = 11.sp,
                fontFamily = ConsolasFontFamily
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Stop
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            isPlaying = false
                            elapsedSec = 0
                        }
                )
                // Previous
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            currentTrackIndex = (currentTrackIndex - 1 + sampleTracks.size) % sampleTracks.size
                            elapsedSec = 0
                            onPlaySampleNote()
                        }
                )
                // Glossy Circular Blue Play/Pause Orb
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF80D8FF), Color(0xFF0288D1), Color(0xFF01579B))
                            )
                        )
                        .border(1.5.dp, Color.White, CircleShape)
                        .clickable {
                            isPlaying = !isPlaying
                            onPlaySampleNote()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                // Next
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            currentTrackIndex = (currentTrackIndex + 1) % sampleTracks.size
                            elapsedSec = 0
                            onPlaySampleNote()
                        }
                )
            }

            Text(
                text = if (isPlaying) "Now Playing" else "Paused",
                color = Color(0xFF80D8FF),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun Win7UserAccountsWindowContent(
    settings: PersistedLauncherSettings,
    onUpdateProfile: (String, Win7AvatarPreset) -> Unit,
    onUpdateLockConfig: (String, Boolean) -> Unit,
    onLockDesktopNow: () -> Unit
) {
    var nameInput by remember(settings.userName) { mutableStateOf(settings.userName) }
    var pinInput by remember(settings.lockScreenPin) { mutableStateOf(settings.lockScreenPin) }
    var lockStartup by remember(settings.requireLockOnStartup) { mutableStateOf(settings.requireLockOnStartup) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
            .testTag("user_accounts_content"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Make changes to your user account & Windows 7 Lock Screen",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Win7ControlPanelHeaderBlue
        )

        // Current User Summary Card + "Lock Computer Now" Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF0F7FF))
                .border(1.dp, Color(0xFF90CAF9), RoundedCornerShape(6.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Win7UserAvatarCanvas(preset = settings.avatarPreset, size = 56.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = settings.userName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    Text(text = "Administrator", fontSize = 11.sp, color = Color(0xFF475569))
                    Text(
                        text = if (settings.lockScreenPin.isBlank()) "No password required" else "Password protected",
                        fontSize = 11.sp,
                        color = Color(0xFF0066CC)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF42A5F5), Color(0xFF1565C0))
                        )
                    )
                    .clickable(onClick = onLockDesktopNow)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("lock_computer_now_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Lock Screen Now", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        HorizontalDivider(color = Color(0xFFD6E2EE))

        // Account Name & PIN Configuration
        Text("1. Account Name & Lock Screen Password / PIN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E395B))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Account Name:", fontSize = 11.sp, color = Color(0xFF475569))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFF7B99B8), RoundedCornerShape(3.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 12.sp, color = Color(0xFF1E293B)),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text("Lock Screen PIN (leave blank for none):", fontSize = 11.sp, color = Color(0xFF475569))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFF7B99B8), RoundedCornerShape(3.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 12.sp, color = Color(0xFF1E293B)),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = lockStartup,
                onCheckedChange = {
                    lockStartup = it
                    onUpdateLockConfig(pinInput, it)
                }
            )
            Text("Show Windows 7 Lock / Welcome Screen when launcher starts", fontSize = 11.sp, color = Color(0xFF1E293B))
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1565C0))
                .clickable {
                    onUpdateProfile(nameInput, settings.avatarPreset)
                    onUpdateLockConfig(pinInput, lockStartup)
                }
                .padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Text("Save Account & Password Settings", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }

        HorizontalDivider(color = Color(0xFFD6E2EE))

        Text("2. Choose Your Account Picture", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E395B))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Win7AvatarPreset.entries.forEach { preset ->
                val selected = settings.avatarPreset == preset
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onUpdateProfile(nameInput, preset) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(
                                width = if (selected) 2.5.dp else 1.dp,
                                color = if (selected) Color(0xFF1565C0) else Color(0xFF90A4AE),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Win7UserAvatarCanvas(preset = preset, size = 44.dp)
                    }
                    Text(preset.label.substringAfter(" "), fontSize = 9.sp, color = Color(0xFF475569))
                }
            }
        }
    }
}

@Composable
fun Win7Flip3dOverlay(
    openWindows: List<Win7WindowInstance>,
    onSelectWindow: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var topCardIndex by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA041022))
            .clickable(onClick = onDismiss)
            .zIndex(1900f)
            .testTag("win7_flip3d_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Windows Aero Flip 3D (Win + Tab)",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap any 3D cascading window card to bring it to the front, or tap Cycle Next",
                color = Color(0xFFB3E5FC),
                fontSize = 11.sp
            )
        }

        if (openWindows.isEmpty()) {
            Box(
                modifier = Modifier
                    .width(260.dp)
                    .height(160.dp)
                    .graphicsLayer {
                        rotationY = -22f
                        cameraDistance = 14f * density
                    }
                    .shadow(16.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xDD1565C0))
                    .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center
            ) {
                Text("Windows 7 Desktop (No open windows)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp),
                contentAlignment = Alignment.Center
            ) {
                val ordered = remember(openWindows, topCardIndex) {
                    val shift = topCardIndex % openWindows.size
                    openWindows.drop(shift) + openWindows.take(shift)
                }

                ordered.reversed().forEachIndexed { idxFromBack, win ->
                    val depth = (ordered.size - 1 - idxFromBack)
                    val xOff = (-depth * 34).dp
                    val yOff = (-depth * 22).dp
                    val scaleVal = (1f - depth * 0.10f).coerceIn(0.65f, 1f)

                    Box(
                        modifier = Modifier
                            .offset(x = xOff, y = yOff)
                            .width(250.dp)
                            .height(165.dp)
                            .graphicsLayer {
                                rotationY = -24f
                                rotationX = 6f
                                scaleX = scaleVal
                                scaleY = scaleVal
                                cameraDistance = 16f * density
                            }
                            .shadow(18.dp, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xEE1E88E5), Color(0xEE0D47A1))
                                )
                            )
                            .border(2.dp, Color(0xEEFFFFFF), RoundedCornerShape(8.dp))
                            .clickable { onSelectWindow(win.id) }
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Win7SystemIconCanvas(iconType = win.type.iconType, size = 20.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = win.title,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFF5F9FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Win7SystemIconCanvas(iconType = win.type.iconType, size = 42.dp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Click to Focus Window",
                                        fontSize = 11.sp,
                                        color = Color(0xFF003399),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Flip 3D Cycle Button
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (openWindows.size > 1) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E88E5))
                        .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                        .clickable { topCardIndex = (topCardIndex + 1) % openWindows.size }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Flip Next Window (Tab)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x55FFFFFF))
                    .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Return to Desktop", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}
