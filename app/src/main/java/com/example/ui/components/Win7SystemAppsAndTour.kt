package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PersistedLauncherSettings
import com.example.model.ExplorerPath
import com.example.model.InstalledAppInfo
import com.example.model.Win7ScreenshotSlide
import com.example.model.Win7SystemIconType
import com.example.model.Win7WindowType
import com.example.ui.theme.ConsolasFontFamily
import com.example.ui.theme.Win7ControlPanelHeaderBlue
import com.example.viewmodel.DesktopUiState

@Composable
fun Win7AeroTourGalleryWindowContent(
    slides: List<Win7ScreenshotSlide>,
    activeIndex: Int,
    onSelectSlide: (Int) -> Unit,
    onApplyPreset: (Win7ScreenshotSlide) -> Unit,
    onOpenFeaturedWindow: (Win7WindowType) -> Unit
) {
    val currentSlide = slides.getOrElse(activeIndex) { slides.first() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEEF3FA))
            .testTag("aero_tour_gallery_content")
    ) {
        // Photo Viewer Top Info Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFF8FBFF), Color(0xFFDCEBFA))
                    )
                )
                .border(0.5.dp, Color(0xFFA0B5CA))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Windows 7 Reference Screenshots (${activeIndex + 1} of ${slides.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E395B)
            )
            Text(
                text = "Tap 'Apply This Look' to switch desktop preset",
                fontSize = 10.sp,
                color = Color(0xFF0066CC)
            )
        }

        // Main Interactive Windows 7 Screenshot Reference Canvas
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Miniature High-Fidelity Windows 7 Desktop Screenshot Preview Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(2.dp, Color(0xFF334E68), RoundedCornerShape(6.dp))
            ) {
                Win7DesktopWallpaper(themePreset = currentSlide.presetTheme)

                // Simulated miniature Desktop Icons on Left
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Win7SystemIconCanvas(iconType = Win7SystemIconType.COMPUTER, size = 22.dp)
                    Win7SystemIconCanvas(iconType = Win7SystemIconType.USER_FILES, size = 22.dp)
                    Win7SystemIconCanvas(iconType = Win7SystemIconType.RECYCLE_BIN, size = 22.dp)
                }

                // Center Miniature Window / Start Menu / Gadgets Preview depending on slide
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(210.dp)
                        .height(112.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(currentSlide.presetSwatch.glassTint.copy(alpha = 0.78f))
                        .border(1.dp, Color.White, RoundedCornerShape(5.dp))
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (currentSlide.id == "slide_aeropeek") Color(0x2280D8FF)
                                else Color.White
                            )
                            .padding(6.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = currentSlide.title,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentSlide.id == "slide_aeropeek") Color.White else Win7ControlPanelHeaderBlue,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            currentSlide.highlights.take(2).forEach { badge ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color(0xFFE3F2FD))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(text = badge, fontSize = 8.sp, color = Color(0xFF0D47A1))
                                }
                            }
                        }
                        Text(
                            text = "Theme: ${currentSlide.presetTheme.displayName} • Glass: ${currentSlide.presetSwatch.displayName}",
                            fontSize = 9.sp,
                            color = if (currentSlide.id == "slide_aeropeek") Color(0xFFE1F5FE) else Color(0xFF475569)
                        )
                    }
                }

                // Miniature Superbar at Bottom of Screenshot
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(22.dp)
                        .background(currentSlide.presetSwatch.taskbarTint)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Win7StartOrbIcon(isOpenOrHovered = true, size = 18.dp)
                    Text(text = "10:25 AM", fontSize = 8.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = currentSlide.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Win7ControlPanelHeaderBlue,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentSlide.caption,
                fontSize = 11.sp,
                color = Color(0xFF334155),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Highlight Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                currentSlide.highlights.forEach { feat ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFD6EBFF))
                            .border(1.dp, Color(0xFF64B5F6), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "✓ $feat", fontSize = 10.sp, color = Color(0xFF0D47A1), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Classic Windows Photo Viewer Bottom Glossy Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFDCE7F5), Color(0xFFB8CCE4), Color(0xFF9EB7D6))
                    )
                )
                .border(1.dp, Color(0xFF8AA2C0))
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Prev Slide Button
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1976D2))
                    .clickable { onSelectSlide(activeIndex - 1) }
                    .testTag("tour_prev_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous screenshot",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF42A5F5), Color(0xFF1565C0))
                            )
                        )
                        .border(1.dp, Color.White, RoundedCornerShape(16.dp))
                        .clickable { onApplyPreset(currentSlide) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("tour_apply_preset_button")
                ) {
                    Text(
                        text = "Apply This Look",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                currentSlide.windowToOpen?.let { targetWin ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFF1565C0), RoundedCornerShape(16.dp))
                            .clickable { onOpenFeaturedWindow(targetWin) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Open Feature",
                            color = Color(0xFF1565C0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Next Slide Button
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1976D2))
                    .clickable { onSelectSlide(activeIndex + 1) }
                    .testTag("tour_next_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next screenshot",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun Win7CommandPromptWindowContent(
    uiState: DesktopUiState,
    userName: String,
    onExecuteCommand: (String) -> Unit
) {
    var inputCmd by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C0C))
            .padding(8.dp)
            .testTag("cmd_prompt_content")
    ) {
        // Quick-command helper strip at top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("help", "ver", "dir", "apps", "systeminfo", "ipconfig", "tasklist", "cls").forEach { quick ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF263238))
                        .border(1.dp, Color(0xFF546E7A), RoundedCornerShape(3.dp))
                        .clickable { onExecuteCommand(quick) }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = quick,
                        fontFamily = ConsolasFontFamily,
                        fontSize = 10.sp,
                        color = Color(0xFF80D8FF)
                    )
                }
            }
        }

        // Terminal Scroll Output
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(uiState.cmdHistory) { line ->
                Text(
                    text = line,
                    fontFamily = ConsolasFontFamily,
                    fontSize = 11.sp,
                    color = Color(0xFFE0E0E0)
                )
            }
        }

        // Prompt Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF161616))
                .border(1.dp, Color(0xFF424242))
                .padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "C:\\Users\\$userName>",
                fontFamily = ConsolasFontFamily,
                fontSize = 11.sp,
                color = Color(0xFF00E676)
            )
            Spacer(modifier = Modifier.width(4.dp))
            BasicTextField(
                value = inputCmd,
                onValueChange = { inputCmd = it },
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = ConsolasFontFamily,
                    fontSize = 11.sp,
                    color = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("cmd_input_field")
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF1E88E5))
                    .clickable {
                        onExecuteCommand(inputCmd)
                        inputCmd = ""
                    }
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                    .testTag("cmd_run_button")
            ) {
                Text(text = "Enter", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun Win7CalculatorWindowContent() {
    var display by remember { mutableStateOf("0") }
    var accumulator by remember { mutableDoubleStateOf(0.0) }
    var pendingOp by remember { mutableStateOf<String?>(null) }
    var resetOnNextDigit by remember { mutableStateOf(false) }

    fun onDigit(d: String) {
        if (display == "0" || resetOnNextDigit) {
            display = d
            resetOnNextDigit = false
        } else if (display.length < 12) {
            display += d
        }
    }

    fun onOperator(op: String) {
        val currentVal = display.toDoubleOrNull() ?: 0.0
        if (pendingOp != null && !resetOnNextDigit) {
            val res = when (pendingOp) {
                "+" -> accumulator + currentVal
                "-" -> accumulator - currentVal
                "×" -> accumulator * currentVal
                "÷" -> if (currentVal != 0.0) accumulator / currentVal else 0.0
                else -> currentVal
            }
            accumulator = res
            display = if (res % 1.0 == 0.0) res.toLong().toString() else String.format("%.4f", res).trimEnd('0').trimEnd('.')
        } else {
            accumulator = currentVal
        }
        pendingOp = if (op == "=") null else op
        resetOnNextDigit = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFD9E7F8), Color(0xFFC5D8F0))
                )
            )
            .padding(10.dp)
            .testTag("calculator_content"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Sunken Windows 7 LCD Readout
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFE6F0FA), Color(0xFFFFFFFF))
                    )
                )
                .border(1.5.dp, Color(0xFF7B99B8), RoundedCornerShape(4.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = pendingOp?.let { "$accumulator $it" } ?: "",
                fontSize = 11.sp,
                color = Color(0xFF546E7A)
            )
            Text(
                text = display,
                fontFamily = ConsolasFontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF102A43),
                maxLines = 1
            )
        }

        val rows = listOf(
            listOf("C", "±", "÷", "×"),
            listOf("7", "8", "9", "-"),
            listOf("4", "5", "6", "+"),
            listOf("1", "2", "3", "="),
            listOf("0", ".")
        )

        rows.forEach { rowKeys ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                rowKeys.forEach { key ->
                    val isAccent = key == "=" || key in listOf("+", "-", "×", "÷")
                    Box(
                        modifier = Modifier
                            .weight(if (key == "0") 2f else 1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = if (key == "=") {
                                        listOf(Color(0xFFFFD54F), Color(0xFFF57F17))
                                    } else if (isAccent) {
                                        listOf(Color(0xFFD6E8FA), Color(0xFFA8C8E8))
                                    } else {
                                        listOf(Color(0xFFFFFFFF), Color(0xFFDCE7F3))
                                    }
                                )
                            )
                            .border(1.dp, Color(0xFF88A2BE), RoundedCornerShape(4.dp))
                            .clickable {
                                when (key) {
                                    "C" -> {
                                        display = "0"
                                        accumulator = 0.0
                                        pendingOp = null
                                    }
                                    "±" -> {
                                        val v = (display.toDoubleOrNull() ?: 0.0) * -1
                                        display = if (v % 1.0 == 0.0) v.toLong().toString() else v.toString()
                                    }
                                    "+", "-", "×", "÷", "=" -> onOperator(key)
                                    "." -> if (!display.contains(".")) display += "."
                                    else -> onDigit(key)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = key,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E395B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Win7RecycleBinWindowContent(
    settings: PersistedLauncherSettings,
    onRestoreItem: (String) -> Unit,
    onRestoreAll: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFF7FAFE), Color(0xFFDCEBFA))
                    )
                )
                .border(0.5.dp, Color(0xFFA0B5CA))
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Restore all items",
                fontSize = 11.sp,
                color = Color(0xFF003399),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onRestoreAll)
            )
        }

        if (settings.recycledItemIds.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Win7SystemIconCanvas(iconType = Win7SystemIconType.RECYCLE_BIN, size = 56.dp, isRecycleBinFull = false)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("This folder is empty.", fontSize = 12.sp, color = Color(0xFF607D8B))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(settings.recycledItemIds.toList(), key = { it }) { itemId ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFF5F9FF))
                            .border(1.dp, Color(0xFFCBD8E8), RoundedCornerShape(4.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = itemId.removePrefix("sys_").removePrefix("app_"), fontSize = 12.sp, color = Color(0xFF1E293B))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1565C0))
                                .clickable { onRestoreItem(itemId) }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Restore", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Win7GadgetGalleryWindowContent(
    settings: PersistedLauncherSettings,
    onToggleGadget: (String, Boolean) -> Unit,
    onAddStickyNote: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
            .testTag("gadget_gallery_content"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Desktop Gadget Gallery",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Win7ControlPanelHeaderBlue
        )
        Text(
            text = "Check or uncheck gadgets below to place them directly on your Windows 7 Desktop.",
            fontSize = 11.sp,
            color = Color(0xFF546E7A)
        )

        val gadgets = listOf(
            Triple("clock", "Analog Clock", settings.showClockGadget),
            Triple("cpu", "CPU & RAM Dual Meter", settings.showCpuMeterGadget),
            Triple("calendar", "Spiral Desktop Calendar", settings.showCalendarGadget),
            Triple("weather", "Aero Weather Card", settings.showWeatherGadget),
            Triple("stickynotes", "Desktop Sticky Notes", settings.showStickyNotesOnDesktop)
        )

        gadgets.forEach { (key, title, enabled) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(5.dp))
                    .background(if (enabled) Color(0xFFEAF4FE) else Color(0xFFF8FAFC))
                    .border(1.dp, if (enabled) Color(0xFF64B5F6) else Color(0xFFCBD5E1), RoundedCornerShape(5.dp))
                    .clickable { onToggleGadget(key, !enabled) }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Win7SystemIconCanvas(iconType = Win7SystemIconType.GADGETS, size = 32.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                        Text(
                            text = if (enabled) "Showing on Desktop" else "Hidden",
                            fontSize = 10.sp,
                            color = if (enabled) Color(0xFF2E7D32) else Color(0xFF64748B)
                        )
                    }
                }
                Checkbox(checked = enabled, onCheckedChange = { onToggleGadget(key, it) })
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1565C0))
                .clickable(onClick = onAddStickyNote)
                .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
            Text(text = "+ Add New Sticky Note to Desktop", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun Win7TaskManagerWindowContent(
    uiState: DesktopUiState,
    onFocusWindow: (String) -> Unit,
    onCloseWindow: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Applications, 1 = Performance

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5F9))
            .padding(8.dp)
            .testTag("task_manager_content")
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("Applications (${uiState.openWindows.size})", "Performance (CPU & RAM)").forEachIndexed { idx, tabTitle ->
                val active = selectedTab == idx
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(if (active) Color.White else Color(0xFFDCE4EE))
                        .border(1.dp, Color(0xFF9EAAB8), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .clickable { selectedTab = idx }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = tabTitle,
                        fontSize = 11.sp,
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                        color = Color(0xFF1E293B)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, Color(0xFF9EAAB8))
                .padding(8.dp)
        ) {
            if (selectedTab == 0) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(uiState.openWindows, key = { it.id }) { win ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFF8FBFF))
                                .border(1.dp, Color(0xFFCBD8E8), RoundedCornerShape(3.dp))
                                .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Win7SystemIconCanvas(iconType = win.type.iconType, size = 20.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = win.title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                    Text(text = "Status: Running", fontSize = 9.sp, color = Color(0xFF2E7D32))
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Switch To",
                                    fontSize = 10.sp,
                                    color = Color(0xFF0066CC),
                                    modifier = Modifier.clickable { onFocusWindow(win.id) }
                                )
                                Text(
                                    text = "End Task",
                                    fontSize = 10.sp,
                                    color = Color(0xFFD32F2F),
                                    modifier = Modifier.clickable { onCloseWindow(win.id) }
                                )
                            }
                        }
                    }
                }
            } else {
                // Classic Windows 7 Black & Green Grid CPU / Memory Performance Oscilloscope
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("CPU Usage History", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E395B))
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(78.dp)
                            .background(Color.Black)
                            .border(1.dp, Color(0xFF546E7A))
                    ) {
                        val step = 18f
                        var x = 0f
                        while (x < size.width) {
                            drawLine(Color(0xFF004D00), Offset(x, 0f), Offset(x, size.height), 1f)
                            x += step
                        }
                        var y = 0f
                        while (y < size.height) {
                            drawLine(Color(0xFF004D00), Offset(0f, y), Offset(size.width, y), 1f)
                            y += step
                        }
                        val pts = listOf(0.7f, 0.55f, 0.62f, 0.38f, 0.45f, 0.52f, 0.30f, 0.48f, 0.40f)
                        for (i in 0 until pts.size - 1) {
                            val x1 = (i.toFloat() / (pts.size - 1)) * size.width
                            val x2 = ((i + 1).toFloat() / (pts.size - 1)) * size.width
                            drawLine(
                                color = Color(0xFF00FF00),
                                start = Offset(x1, size.height * pts[i]),
                                end = Offset(x2, size.height * pts[i + 1]),
                                strokeWidth = 2f
                            )
                        }
                    }

                    Text("Physical Memory & System Telemetry", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E395B))
                    Text(
                        text = "• Installed Android Apps: ${uiState.installedApps.size}\n• Open Aero Windows (DWM): ${uiState.openWindows.size}\n• Battery Level: ${uiState.batteryPercent}% (${uiState.powerPlan})",
                        fontSize = 11.sp,
                        color = Color(0xFF334155)
                    )
                }
            }
        }
    }
}

@Composable
fun Win7NetworkCenterWindowContent(
    uiState: DesktopUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(14.dp)
            .testTag("network_center_content"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "View your basic network information and set up connections",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Win7ControlPanelHeaderBlue
        )

        // Classic Windows 7 Network Map: THIS COMPUTER ---- NETWORK ---- INTERNET
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF3F8FE))
                .border(1.dp, Color(0xFFBDD4EC), RoundedCornerShape(6.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Win7SystemIconCanvas(iconType = Win7SystemIconType.COMPUTER, size = 34.dp)
                Text("WIN7-AERO-PC", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
            Box(modifier = Modifier.width(36.dp).height(3.dp).background(Color(0xFF43A047)))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Win7SystemIconCanvas(iconType = Win7SystemIconType.USER_FILES, size = 32.dp)
                Text(uiState.networkName, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(3.dp)
                    .background(if (uiState.isNetworkConnected) Color(0xFF43A047) else Color(0xFFD32F2F))
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Win7SystemIconCanvas(iconType = Win7SystemIconType.NETWORK, size = 34.dp)
                Text("Internet", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Text(
            text = "Active Networks",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E395B)
        )
        Text(
            text = "Network: ${uiState.networkName}\nAccess type: ${if (uiState.isNetworkConnected) "Internet" else "Local only"}\nHomeGroup: Joined\nConnections: Wireless Network Connection",
            fontSize = 11.sp,
            color = Color(0xFF334155)
        )
    }
}

@Composable
fun Win7AppPropertiesWindowContent(
    app: InstalledAppInfo?,
    settings: PersistedLauncherSettings,
    onLaunchApp: (String) -> Unit,
    onTogglePinTaskbar: (String, String) -> Unit,
    onToggleDesktopShortcut: (String, String) -> Unit,
    onOpenAndroidAppInfo: (String) -> Unit
) {
    if (app == null) return
    val isPinned = app.packageName in settings.pinnedTaskbarPackages
    val isOnDesktop = app.packageName in settings.desktopShortcutPackages

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6FA))
            .padding(12.dp)
            .testTag("app_properties_content"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (app.iconBitmap != null) {
                Image(bitmap = app.iconBitmap, contentDescription = app.label, modifier = Modifier.size(44.dp))
            } else {
                Win7SystemIconCanvas(iconType = Win7SystemIconType.ANDROID_APP, size = 44.dp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = app.label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Text(text = "Type of file: Android Application (.apk)", fontSize = 11.sp, color = Color(0xFF546E7A))
            }
        }

        HorizontalDivider(color = Color(0xFFCFD8DC))

        Text("Package: ${app.packageName}", fontSize = 11.sp, color = Color(0xFF334155))
        Text("Category folder: ${app.categoryFolder}", fontSize = 11.sp, color = Color(0xFF334155))
        Text("Version: ${app.versionName}", fontSize = 11.sp, color = Color(0xFF334155))

        HorizontalDivider(color = Color(0xFFCFD8DC))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1565C0))
                    .clickable { onLaunchApp(app.packageName) }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Open Program", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFF1565C0), RoundedCornerShape(4.dp))
                    .clickable { onOpenAndroidAppInfo(app.packageName) }
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            ) {
                Text("Android App Info / Uninstall", color = Color(0xFF1565C0), fontSize = 11.sp)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE3F2FD))
                    .border(1.dp, Color(0xFF90CAF9), RoundedCornerShape(4.dp))
                    .clickable { onTogglePinTaskbar(app.packageName, app.label) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isPinned) "Unpin from Taskbar" else "Pin to Taskbar",
                    fontSize = 11.sp,
                    color = Color(0xFF0D47A1)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE3F2FD))
                    .border(1.dp, Color(0xFF90CAF9), RoundedCornerShape(4.dp))
                    .clickable { onToggleDesktopShortcut(app.packageName, app.label) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isOnDesktop) "Remove Desktop Shortcut" else "Send to Desktop (Shortcut)",
                    fontSize = 11.sp,
                    color = Color(0xFF0D47A1)
                )
            }
        }
    }
}
