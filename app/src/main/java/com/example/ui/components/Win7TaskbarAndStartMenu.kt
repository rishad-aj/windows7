package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterNone
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PersistedLauncherSettings
import com.example.model.AeroGlassSwatch
import com.example.model.ExplorerPath
import com.example.model.InstalledAppInfo
import com.example.model.Win7SystemIconType
import com.example.model.Win7WindowType
import com.example.ui.theme.Win7StartMenuRightPane
import com.example.viewmodel.DesktopUiState
import com.example.viewmodel.PowerActionState
import com.example.viewmodel.TrayPopupType
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Win7SuperbarTaskbar(
    uiState: DesktopUiState,
    settings: PersistedLauncherSettings,
    onToggleStartMenu: () -> Unit,
    onToggleFlip3d: () -> Unit,
    onOpenWindow: (Win7WindowType, ExplorerPath) -> Unit,
    onToggleWindowFromTaskbar: (String) -> Unit,
    onLaunchAndroidApp: (String) -> Unit,
    onToggleTrayPopup: (TrayPopupType) -> Unit,
    onShowDesktopClick: () -> Unit,
    onAeroPeekLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val timeFormatted = remember(currentTimeMillis) {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(currentTimeMillis))
    }
    val dateFormatted = remember(currentTimeMillis) {
        SimpleDateFormat("M/d/yyyy", Locale.getDefault()).format(Date(currentTimeMillis))
    }

    val taskbarGlassColor = if (settings.aeroTransparency) {
        settings.aeroSwatch.taskbarTint.copy(alpha = 0.86f)
    } else {
        settings.aeroSwatch.taskbarTint.copy(alpha = 0.98f)
    }

    val barHeight = if (settings.smallTaskbarIcons) 36.dp else 44.dp
    val iconSize = if (settings.smallTaskbarIcons) 21.dp else 26.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        settings.aeroSwatch.glassTint.copy(alpha = 0.50f),
                        taskbarGlassColor
                    )
                )
            )
            .drawBehind {
                drawLine(
                    color = Color(0x99FFFFFF),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.5f
                )
                drawLine(
                    color = Color(0x8804101E),
                    start = Offset(0f, 2f),
                    end = Offset(size.width, 2f),
                    strokeWidth = 1f
                )
            }
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(barHeight)
            .testTag("win7_superbar")
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Windows 7 Start Orb Button
            Box(
                modifier = Modifier
                    .padding(start = 4.dp, end = 3.dp)
                    .size(barHeight - 2.dp)
                    .clip(RoundedCornerShape(21.dp))
                    .clickable(onClick = onToggleStartMenu)
                    .testTag("win7_start_orb"),
                contentAlignment = Alignment.Center
            ) {
                Win7StartOrbIcon(
                    isOpenOrHovered = uiState.isStartMenuOpen,
                    size = barHeight - 6.dp
                )
            }

            // 1B. Optional Aero Flip 3D (Win + Tab) Quick Launch Button
            if (settings.showFlip3dOnTaskbar) {
                SuperbarTaskSlot(
                    title = "Aero Flip 3D",
                    isRunning = uiState.isFlip3dActive,
                    isActive = uiState.isFlip3dActive,
                    swatch = settings.aeroSwatch,
                    smallMode = settings.smallTaskbarIcons,
                    onClick = onToggleFlip3d,
                    iconContent = {
                        Icon(
                            imageVector = Icons.Default.FilterNone,
                            contentDescription = "Aero Flip 3D",
                            tint = Color(0xFF80D8FF),
                            modifier = Modifier.size(iconSize - 3.dp)
                        )
                    }
                )
                Spacer(modifier = Modifier.width(2.dp))
            }

            // 2. Superbar Pinned + Running Windows Strip
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // Default Pinned Explorer Icon
                val explorerWin = uiState.openWindows.find {
                    it.type == Win7WindowType.COMPUTER_EXPLORER || it.type == Win7WindowType.DOCUMENTS_LIBRARY
                }
                SuperbarTaskSlot(
                    title = "Windows Explorer",
                    isRunning = explorerWin != null,
                    isActive = explorerWin != null && explorerWin.id == uiState.activeWindowId && !explorerWin.isMinimized,
                    swatch = settings.aeroSwatch,
                    smallMode = settings.smallTaskbarIcons,
                    onClick = {
                        if (explorerWin != null) {
                            onToggleWindowFromTaskbar(explorerWin.id)
                        } else {
                            onOpenWindow(Win7WindowType.COMPUTER_EXPLORER, ExplorerPath.COMPUTER_ROOT)
                        }
                    },
                    iconContent = {
                        Win7SystemIconCanvas(iconType = Win7SystemIconType.USER_FILES, size = iconSize)
                    }
                )

                // Default Pinned Windows Media Player 12 Icon
                val wmpWin = uiState.openWindows.find { it.type == Win7WindowType.MEDIA_PLAYER }
                SuperbarTaskSlot(
                    title = "Windows Media Player",
                    isRunning = wmpWin != null,
                    isActive = wmpWin != null && wmpWin.id == uiState.activeWindowId && !wmpWin.isMinimized,
                    swatch = settings.aeroSwatch,
                    smallMode = settings.smallTaskbarIcons,
                    onClick = {
                        if (wmpWin != null) {
                            onToggleWindowFromTaskbar(wmpWin.id)
                        } else {
                            onOpenWindow(Win7WindowType.MEDIA_PLAYER, ExplorerPath.COMPUTER_ROOT)
                        }
                    },
                    iconContent = {
                        Win7SystemIconCanvas(iconType = Win7SystemIconType.MEDIA_PLAYER, size = iconSize)
                    }
                )

                // Default Pinned Control Panel
                val cpWin = uiState.openWindows.find { it.type == Win7WindowType.CONTROL_PANEL }
                SuperbarTaskSlot(
                    title = "Control Panel",
                    isRunning = cpWin != null,
                    isActive = cpWin != null && cpWin.id == uiState.activeWindowId && !cpWin.isMinimized,
                    swatch = settings.aeroSwatch,
                    smallMode = settings.smallTaskbarIcons,
                    onClick = {
                        if (cpWin != null) {
                            onToggleWindowFromTaskbar(cpWin.id)
                        } else {
                            onOpenWindow(Win7WindowType.CONTROL_PANEL, ExplorerPath.COMPUTER_ROOT)
                        }
                    },
                    iconContent = {
                        Win7SystemIconCanvas(iconType = Win7SystemIconType.CONTROL_PANEL, size = iconSize)
                    }
                )

                // Other Open Windows on the Superbar
                uiState.openWindows
                    .filter {
                        it.type != Win7WindowType.COMPUTER_EXPLORER &&
                            it.type != Win7WindowType.DOCUMENTS_LIBRARY &&
                            it.type != Win7WindowType.MEDIA_PLAYER &&
                            it.type != Win7WindowType.CONTROL_PANEL
                    }
                    .forEach { win ->
                        SuperbarTaskSlot(
                            title = win.title,
                            isRunning = true,
                            isActive = win.id == uiState.activeWindowId && !win.isMinimized,
                            swatch = settings.aeroSwatch,
                            smallMode = settings.smallTaskbarIcons,
                            onClick = { onToggleWindowFromTaskbar(win.id) },
                            iconContent = {
                                Win7SystemIconCanvas(iconType = win.type.iconType, size = iconSize)
                            }
                        )
                    }

                // Pinned Android Apps on the Superbar
                val pinnedApps = if (settings.pinnedTaskbarPackages.isEmpty()) {
                    uiState.installedApps.take(2)
                } else {
                    uiState.installedApps.filter { it.packageName in settings.pinnedTaskbarPackages }
                }
                pinnedApps.forEach { app ->
                    SuperbarTaskSlot(
                        title = app.label,
                        isRunning = false,
                        isActive = false,
                        swatch = settings.aeroSwatch,
                        smallMode = settings.smallTaskbarIcons,
                        onClick = { onLaunchAndroidApp(app.packageName) },
                        iconContent = {
                            if (app.iconBitmap != null) {
                                Image(
                                    bitmap = app.iconBitmap,
                                    contentDescription = app.label,
                                    modifier = Modifier.size(iconSize)
                                )
                            } else {
                                Win7SystemIconCanvas(iconType = Win7SystemIconType.ANDROID_APP, size = iconSize)
                            }
                        }
                    )
                }
            }

            // 3. System Tray (Notification Area)
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(Color(0x28000000))
                    .padding(start = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hidden Icons Caret (▲)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .clickable { onToggleTrayPopup(TrayPopupType.HIDDEN_TRAY_ICONS) }
                        .testTag("tray_hidden_icons"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Show hidden icons",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Network Icon
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .clickable { onToggleTrayPopup(TrayPopupType.NETWORK_FLYOUT) }
                        .testTag("tray_network_icon"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Network access",
                        tint = if (uiState.isNetworkConnected) Color(0xFF90CAF9) else Color(0xFFFF8A80),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Volume Speaker Icon
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .clickable { onToggleTrayPopup(TrayPopupType.VOLUME_MIXER) }
                        .testTag("tray_volume_icon"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (uiState.isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Speakers volume",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Battery Meter Icon
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .clickable { onToggleTrayPopup(TrayPopupType.BATTERY_POWER) }
                        .testTag("tray_battery_icon"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (uiState.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                        contentDescription = "Battery status",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Two-Line Time & Date Clock
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .clickable { onToggleTrayPopup(TrayPopupType.CLOCK_CALENDAR) }
                        .padding(horizontal = 6.dp)
                        .testTag("tray_clock_button"),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = timeFormatted,
                        color = Color.White,
                        fontSize = 11.sp,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (!settings.smallTaskbarIcons) {
                        Text(
                            text = dateFormatted,
                            color = Color(0xFFD6E8FA),
                            fontSize = 10.sp,
                            lineHeight = 11.sp
                        )
                    }
                }

                // 4. Far-Right 14.dp Windows 7 Aero Peek "Show Desktop" Glass Strip
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = if (uiState.isAeroPeekActive) {
                                    listOf(Color(0xAA80D8FF), Color(0x7729B6F6))
                                } else {
                                    listOf(Color(0x55FFFFFF), Color(0x22FFFFFF), Color(0x4480D8FF))
                                }
                            )
                        )
                        .border(width = 1.dp, color = Color(0x88FFFFFF))
                        .combinedClickable(
                            onClick = onShowDesktopClick,
                            onLongClick = onAeroPeekLongClick
                        )
                        .testTag("aero_peek_strip")
                )
            }
        }
    }
}

@Composable
private fun SuperbarTaskSlot(
    title: String,
    isRunning: Boolean,
    isActive: Boolean,
    swatch: AeroGlassSwatch,
    smallMode: Boolean = false,
    onClick: () -> Unit,
    iconContent: @Composable () -> Unit
) {
    val slotShape = RoundedCornerShape(4.dp)
    val bgBrush = when {
        isActive -> Brush.verticalGradient(
            colors = listOf(
                Color(0x77FFFFFF),
                swatch.activeGlow.copy(alpha = 0.55f),
                swatch.glassTint.copy(alpha = 0.70f)
            )
        )
        isRunning -> Brush.verticalGradient(
            colors = listOf(
                Color(0x40FFFFFF),
                Color(0x22FFFFFF),
                Color(0x44102840)
            )
        )
        else -> Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color.Transparent)
        )
    }

    Box(
        modifier = Modifier
            .width(if (smallMode) 36.dp else 44.dp)
            .height(if (smallMode) 30.dp else 36.dp)
            .clip(slotShape)
            .background(bgBrush)
            .then(
                if (isRunning) {
                    Modifier.border(
                        width = 1.dp,
                        color = if (isActive) Color(0xDDFFFFFF) else Color(0x77B3E5FC),
                        shape = slotShape
                    )
                } else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        iconContent()
        if (isRunning) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(if (isActive) 26.dp else 14.dp)
                    .height(2.5.dp)
                    .background(if (isActive) Color(0xFF80D8FF) else Color(0x99FFFFFF))
            )
        }
    }
}

@Composable
fun Win7StartMenuPopup(
    uiState: DesktopUiState,
    settings: PersistedLauncherSettings,
    onLaunchAndroidApp: (String) -> Unit,
    onOpenWindow: (Win7WindowType, ExplorerPath, InstalledAppInfo?) -> Unit,
    onToggleAllPrograms: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleFolder: (String) -> Unit,
    onPromptSetDefaultLauncher: () -> Unit,
    onToggleShutdownMenu: () -> Unit,
    onTriggerPowerAction: (PowerActionState) -> Unit,
    onRefreshDesktop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val outerShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomEnd = 4.dp, bottomStart = 0.dp)
    val glassColor = settings.aeroSwatch.taskbarTint.copy(alpha = 0.94f)

    Box(
        modifier = modifier
            .widthIn(min = 320.dp, max = 392.dp)
            .fillMaxWidth(0.95f)
            .heightIn(min = 420.dp, max = 500.dp)
            .padding(start = 2.dp)
            .testTag("win7_start_menu")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .shadow(20.dp, outerShape)
                .clip(outerShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            settings.aeroSwatch.glassTint.copy(alpha = 0.85f),
                            glassColor,
                            Win7StartMenuRightPane
                        )
                    )
                )
                .border(1.5.dp, Color(0xAA80D8FF), outerShape)
                .padding(6.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // LEFT WHITE PANE: Programs List + All Programs + Instant Search Box
                Column(
                    modifier = Modifier
                        .weight(0.58f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFA5B9CE), RoundedCornerShape(5.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        if (uiState.startSearchQuery.isNotBlank()) {
                            val query = uiState.startSearchQuery.lowercase()
                            val matchingApps = uiState.installedApps.filter {
                                it.label.lowercase().contains(query) || it.packageName.lowercase().contains(query)
                            }
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                item {
                                    Text(
                                        text = "Programs (${matchingApps.size})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E395B),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                                items(matchingApps, key = { it.packageName }) { app ->
                                    StartMenuAppRow(
                                        title = app.label,
                                        subtitle = app.categoryFolder,
                                        iconContent = {
                                            if (app.iconBitmap != null) {
                                                Image(
                                                    bitmap = app.iconBitmap,
                                                    contentDescription = app.label,
                                                    modifier = Modifier.size(26.dp)
                                                )
                                            } else {
                                                Win7SystemIconCanvas(Win7SystemIconType.ANDROID_APP, size = 26.dp)
                                            }
                                        },
                                        onClick = { onLaunchAndroidApp(app.packageName) }
                                    )
                                }
                                item {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFE1EAF3))
                                    Text(
                                        text = "Control Panel & Accessories",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E395B),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                    StartMenuAppRow(
                                        title = "Personalize (12 Wallpapers)",
                                        subtitle = "Appearance",
                                        iconContent = { Win7SystemIconCanvas(Win7SystemIconType.CONTROL_PANEL, size = 24.dp) },
                                        onClick = { onOpenWindow(Win7WindowType.PERSONALIZATION, ExplorerPath.COMPUTER_ROOT, null) }
                                    )
                                    StartMenuAppRow(
                                        title = "Windows 7 Lock Screen",
                                        subtitle = "Lock Workstation",
                                        iconContent = { Win7SystemIconCanvas(Win7SystemIconType.LOCK_SCREEN, size = 24.dp) },
                                        onClick = { onTriggerPowerAction(PowerActionState.LOCKED_SCREEN) }
                                    )
                                }
                            }
                        } else if (!uiState.isAllProgramsInStart) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Only show Set as Default Launcher if NOT already default!
                                if (!uiState.isDefaultLauncher) {
                                    item {
                                        StartMenuAppRow(
                                            title = "Set as Default Launcher",
                                            subtitle = "Make Aero7 Default Home",
                                            isHighlighted = true,
                                            iconContent = { Win7SystemIconCanvas(Win7SystemIconType.SET_DEFAULT_LAUNCHER, size = 28.dp) },
                                            onClick = onPromptSetDefaultLauncher
                                        )
                                    }
                                }
                                item {
                                    StartMenuAppRow(
                                        title = "Windows Media Player",
                                        subtitle = "wmplayer.exe",
                                        iconContent = { Win7SystemIconCanvas(Win7SystemIconType.MEDIA_PLAYER, size = 28.dp) },
                                        onClick = { onOpenWindow(Win7WindowType.MEDIA_PLAYER, ExplorerPath.COMPUTER_ROOT, null) }
                                    )
                                }
                                item {
                                    StartMenuAppRow(
                                        title = "Paint",
                                        subtitle = "Create & edit drawings",
                                        iconContent = { Win7SystemIconCanvas(Win7SystemIconType.PAINT, size = 28.dp) },
                                        onClick = { onOpenWindow(Win7WindowType.PAINT, ExplorerPath.COMPUTER_ROOT, null) }
                                    )
                                }
                                item {
                                    StartMenuAppRow(
                                        title = "Notepad",
                                        subtitle = "Text Editor",
                                        iconContent = { Win7SystemIconCanvas(Win7SystemIconType.NOTEPAD, size = 28.dp) },
                                        onClick = { onOpenWindow(Win7WindowType.NOTEPAD, ExplorerPath.COMPUTER_ROOT, null) }
                                    )
                                }
                                item {
                                    StartMenuAppRow(
                                        title = "Win7 Screenshots & Tour",
                                        subtitle = "Photo Viewer Presets",
                                        iconContent = { Win7SystemIconCanvas(Win7SystemIconType.AERO_TOUR_GALLERY, size = 28.dp) },
                                        onClick = { onOpenWindow(Win7WindowType.AERO_TOUR_GALLERY, ExplorerPath.COMPUTER_ROOT, null) }
                                    )
                                }
                                item {
                                    StartMenuAppRow(
                                        title = "Calculator",
                                        subtitle = "calc.exe",
                                        iconContent = { Win7SystemIconCanvas(Win7SystemIconType.CALCULATOR, size = 28.dp) },
                                        onClick = { onOpenWindow(Win7WindowType.CALCULATOR, ExplorerPath.COMPUTER_ROOT, null) }
                                    )
                                }
                                item {
                                    StartMenuAppRow(
                                        title = "Command Prompt",
                                        subtitle = "cmd.exe",
                                        iconContent = { Win7SystemIconCanvas(Win7SystemIconType.COMMAND_PROMPT, size = 28.dp) },
                                        onClick = { onOpenWindow(Win7WindowType.COMMAND_PROMPT, ExplorerPath.COMPUTER_ROOT, null) }
                                    )
                                }
                                item {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                        color = Color(0xFFD6E3F0)
                                    )
                                }
                                items(uiState.installedApps.take(12), key = { it.packageName }) { app ->
                                    StartMenuAppRow(
                                        title = app.label,
                                        subtitle = null,
                                        iconContent = {
                                            if (app.iconBitmap != null) {
                                                Image(
                                                    bitmap = app.iconBitmap,
                                                    contentDescription = app.label,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            } else {
                                                Win7SystemIconCanvas(Win7SystemIconType.ANDROID_APP, size = 28.dp)
                                            }
                                        },
                                        onClick = { onLaunchAndroidApp(app.packageName) }
                                    )
                                }
                            }
                        } else {
                            // ALL PROGRAMS Tree View
                            val grouped = remember(uiState.installedApps) {
                                uiState.installedApps.groupBy { it.categoryFolder }
                            }
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                item {
                                    val accFolder = "Accessories (Windows 7)"
                                    val isExpanded = accFolder in uiState.expandedStartFolders
                                    StartMenuFolderHeader(
                                        folderName = accFolder,
                                        count = 8,
                                        isExpanded = isExpanded,
                                        onClick = { onToggleFolder(accFolder) }
                                    )
                                    if (isExpanded) {
                                        Column(modifier = Modifier.padding(start = 12.dp)) {
                                            StartMenuAppRow(
                                                title = "Windows Media Player",
                                                subtitle = null,
                                                iconContent = { Win7SystemIconCanvas(Win7SystemIconType.MEDIA_PLAYER, size = 22.dp) },
                                                onClick = { onOpenWindow(Win7WindowType.MEDIA_PLAYER, ExplorerPath.COMPUTER_ROOT, null) }
                                            )
                                            StartMenuAppRow(
                                                title = "Paint",
                                                subtitle = null,
                                                iconContent = { Win7SystemIconCanvas(Win7SystemIconType.PAINT, size = 22.dp) },
                                                onClick = { onOpenWindow(Win7WindowType.PAINT, ExplorerPath.COMPUTER_ROOT, null) }
                                            )
                                            StartMenuAppRow(
                                                title = "Notepad",
                                                subtitle = null,
                                                iconContent = { Win7SystemIconCanvas(Win7SystemIconType.NOTEPAD, size = 22.dp) },
                                                onClick = { onOpenWindow(Win7WindowType.NOTEPAD, ExplorerPath.COMPUTER_ROOT, null) }
                                            )
                                            StartMenuAppRow(
                                                title = "Command Prompt",
                                                subtitle = null,
                                                iconContent = { Win7SystemIconCanvas(Win7SystemIconType.COMMAND_PROMPT, size = 22.dp) },
                                                onClick = { onOpenWindow(Win7WindowType.COMMAND_PROMPT, ExplorerPath.COMPUTER_ROOT, null) }
                                            )
                                            StartMenuAppRow(
                                                title = "Calculator",
                                                subtitle = null,
                                                iconContent = { Win7SystemIconCanvas(Win7SystemIconType.CALCULATOR, size = 22.dp) },
                                                onClick = { onOpenWindow(Win7WindowType.CALCULATOR, ExplorerPath.COMPUTER_ROOT, null) }
                                            )
                                            StartMenuAppRow(
                                                title = "Windows Explorer",
                                                subtitle = null,
                                                iconContent = { Win7SystemIconCanvas(Win7SystemIconType.USER_FILES, size = 22.dp) },
                                                onClick = { onOpenWindow(Win7WindowType.COMPUTER_EXPLORER, ExplorerPath.COMPUTER_ROOT, null) }
                                            )
                                            StartMenuAppRow(
                                                title = "Desktop Gadget Gallery",
                                                subtitle = null,
                                                iconContent = { Win7SystemIconCanvas(Win7SystemIconType.GADGETS, size = 22.dp) },
                                                onClick = { onOpenWindow(Win7WindowType.GADGET_GALLERY, ExplorerPath.COMPUTER_ROOT, null) }
                                            )
                                            StartMenuAppRow(
                                                title = "Windows Task Manager",
                                                subtitle = null,
                                                iconContent = { Win7SystemIconCanvas(Win7SystemIconType.TASK_MANAGER, size = 22.dp) },
                                                onClick = { onOpenWindow(Win7WindowType.TASK_MANAGER, ExplorerPath.COMPUTER_ROOT, null) }
                                            )
                                        }
                                    }
                                }

                                grouped.forEach { (folderName, folderApps) ->
                                    item(key = "folder_$folderName") {
                                        val expanded = folderName in uiState.expandedStartFolders
                                        StartMenuFolderHeader(
                                            folderName = folderName,
                                            count = folderApps.size,
                                            isExpanded = expanded,
                                            onClick = { onToggleFolder(folderName) }
                                        )
                                        if (expanded) {
                                            Column(modifier = Modifier.padding(start = 12.dp)) {
                                                folderApps.forEach { app ->
                                                    StartMenuAppRow(
                                                        title = app.label,
                                                        subtitle = null,
                                                        iconContent = {
                                                            if (app.iconBitmap != null) {
                                                                Image(
                                                                    bitmap = app.iconBitmap,
                                                                    contentDescription = app.label,
                                                                    modifier = Modifier.size(22.dp)
                                                                )
                                                            } else {
                                                                Win7SystemIconCanvas(Win7SystemIconType.ANDROID_APP, size = 22.dp)
                                                            }
                                                        },
                                                        onClick = { onLaunchAndroidApp(app.packageName) }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFD5E2F0))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleAllPrograms(!uiState.isAllProgramsInStart) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("start_all_programs_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFF1B5E20),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (uiState.isAllProgramsInStart) "Back" else "All Programs",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E395B)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5FB))
                            .padding(6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFF7E9DB9), RoundedCornerShape(3.dp))
                                .padding(horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (uiState.startSearchQuery.isEmpty()) {
                                    Text(
                                        text = "Search programs and files",
                                        fontSize = 11.sp,
                                        fontStyle = FontStyle.Italic,
                                        color = Color(0xFF78909C),
                                        maxLines = 1
                                    )
                                }
                                BasicTextField(
                                    value = uiState.startSearchQuery,
                                    onValueChange = onSearchQueryChange,
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        fontSize = 12.sp,
                                        color = Color(0xFF1E1E1E)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("start_search_input")
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF3E78B2),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                // RIGHT DARK AERO GLASS PANE: System Places & Shut Down Button
                Column(
                    modifier = Modifier
                        .weight(0.42f)
                        .fillMaxHeight()
                        .padding(start = 6.dp, top = 34.dp, end = 2.dp, bottom = 2.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        StartMenuRightLink(
                            label = settings.userName,
                            isBold = true,
                            onClick = { onOpenWindow(Win7WindowType.USER_ACCOUNTS, ExplorerPath.COMPUTER_ROOT, null) }
                        )
                        StartMenuRightLink(
                            label = "Documents",
                            onClick = { onOpenWindow(Win7WindowType.DOCUMENTS_LIBRARY, ExplorerPath.LIBRARIES_DOCUMENTS, null) }
                        )
                        StartMenuRightLink(
                            label = "Pictures",
                            onClick = { onOpenWindow(Win7WindowType.DOCUMENTS_LIBRARY, ExplorerPath.LIBRARIES_PICTURES, null) }
                        )
                        StartMenuRightLink(
                            label = "Music",
                            onClick = { onOpenWindow(Win7WindowType.MEDIA_PLAYER, ExplorerPath.LIBRARIES_MUSIC, null) }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = Color(0x4480D8FF)
                        )

                        StartMenuRightLink(
                            label = "Computer",
                            isBold = true,
                            onClick = { onOpenWindow(Win7WindowType.COMPUTER_EXPLORER, ExplorerPath.COMPUTER_ROOT, null) }
                        )
                        StartMenuRightLink(
                            label = "Network",
                            onClick = { onOpenWindow(Win7WindowType.NETWORK_CENTER, ExplorerPath.NETWORK_PLACES, null) }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = Color(0x4480D8FF)
                        )

                        StartMenuRightLink(
                            label = "Control Panel",
                            onClick = { onOpenWindow(Win7WindowType.CONTROL_PANEL, ExplorerPath.COMPUTER_ROOT, null) }
                        )
                        StartMenuRightLink(
                            label = "Personalization",
                            onClick = { onOpenWindow(Win7WindowType.PERSONALIZATION, ExplorerPath.COMPUTER_ROOT, null) }
                        )
                        StartMenuRightLink(
                            label = "Lock Screen (Win+L)",
                            onClick = { onTriggerPowerAction(PowerActionState.LOCKED_SCREEN) }
                        )
                        if (!uiState.isDefaultLauncher) {
                            StartMenuRightLink(
                                label = "Default Programs",
                                onClick = onPromptSetDefaultLauncher
                            )
                        }
                    }

                    // Bottom "Shut down | ▸" Split Glass Button
                    Column(horizontalAlignment = Alignment.End) {
                        if (uiState.isShutdownMenuExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xEEF5F8FC))
                                    .border(1.dp, Color(0xFF6B8EB2), RoundedCornerShape(4.dp))
                                    .padding(2.dp)
                            ) {
                                ShutdownMenuOption("Switch user") {
                                    onTriggerPowerAction(PowerActionState.LOCKED_SCREEN)
                                }
                                ShutdownMenuOption("Lock") {
                                    onTriggerPowerAction(PowerActionState.LOCKED_SCREEN)
                                }
                                ShutdownMenuOption("Restart Desktop") {
                                    onRefreshDesktop()
                                }
                                ShutdownMenuOption("Task Manager") {
                                    onOpenWindow(Win7WindowType.TASK_MANAGER, ExplorerPath.COMPUTER_ROOT, null)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .height(26.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0x66FFFFFF),
                                            Color(0x3380D8FF),
                                            Color(0x55102840)
                                        )
                                    )
                                )
                                .border(1.dp, Color(0xAA80D8FF), RoundedCornerShape(4.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clickable { onTriggerPowerAction(PowerActionState.SHUTTING_DOWN) }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                    .testTag("start_shutdown_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Shut down",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .fillMaxHeight()
                                    .background(Color(0x8880D8FF))
                            )
                            Box(
                                modifier = Modifier
                                    .clickable { onToggleShutdownMenu() }
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "More power options",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating User Account Picture Frame straddling top-right of Start Menu
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 36.dp)
                .size(54.dp)
                .shadow(8.dp, RoundedCornerShape(6.dp))
                .clip(RoundedCornerShape(6.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xEEFFFFFF), Color(0xAA90CAF9))
                    )
                )
                .border(1.5.dp, Color.White, RoundedCornerShape(6.dp))
                .padding(3.dp)
                .clickable {
                    onOpenWindow(Win7WindowType.USER_ACCOUNTS, ExplorerPath.COMPUTER_ROOT, null)
                },
            contentAlignment = Alignment.Center
        ) {
            Win7UserAvatarCanvas(
                preset = settings.avatarPreset,
                size = 46.dp,
                modifier = Modifier.clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
private fun StartMenuAppRow(
    title: String,
    subtitle: String?,
    isHighlighted: Boolean = false,
    iconContent: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(if (isHighlighted) Color(0xFFEAF4FE) else Color.Transparent)
            .then(
                if (isHighlighted) Modifier.border(1.dp, Color(0xFF90CAF9), RoundedCornerShape(4.dp))
                else Modifier
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        iconContent()
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Normal,
                color = Color(0xFF1A1A1A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = Color(0xFF5C728A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun StartMenuFolderHeader(
    folderName: String,
    count: Int,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(if (isExpanded) Color(0xFFEEF5FD) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            tint = Color(0xFFFBC02D),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = folderName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E395B),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "($count)",
            fontSize = 10.sp,
            color = Color(0xFF78909C)
        )
    }
}

@Composable
private fun StartMenuRightLink(
    label: String,
    isBold: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ShutdownMenuOption(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF1E293B)
        )
    }
}

@Composable
fun Win7TrayFlyoutPopup(
    uiState: DesktopUiState,
    onVolumeChange: (Int) -> Unit,
    onToggleMute: () -> Unit,
    onSelectPowerPlan: (String) -> Unit,
    onOpenNetworkCenter: () -> Unit,
    onOpenControlPanel: () -> Unit,
    onLockComputer: () -> Unit,
    onPromptDefaultLauncher: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.activeTrayPopup == TrayPopupType.NONE) return

    val flyoutShape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .padding(end = 8.dp, bottom = 4.dp)
            .shadow(14.dp, flyoutShape)
            .clip(flyoutShape)
            .background(Color(0xFAF5F9FE))
            .border(1.5.dp, Color(0xFF567EBB), flyoutShape)
            .padding(12.dp)
            .testTag("tray_flyout_${uiState.activeTrayPopup.name.lowercase()}")
    ) {
        when (uiState.activeTrayPopup) {
            TrayPopupType.CLOCK_CALENDAR -> Win7ClockCalendarFlyout()
            TrayPopupType.VOLUME_MIXER -> Win7VolumeMixerFlyout(
                volumePercent = uiState.volumePercent,
                isMuted = uiState.isMuted,
                onVolumeChange = onVolumeChange,
                onToggleMute = onToggleMute
            )
            TrayPopupType.NETWORK_FLYOUT -> Win7NetworkFlyout(
                isConnected = uiState.isNetworkConnected,
                networkName = uiState.networkName,
                onOpenNetworkCenter = onOpenNetworkCenter
            )
            TrayPopupType.BATTERY_POWER -> Win7BatteryFlyout(
                batteryPercent = uiState.batteryPercent,
                isCharging = uiState.isCharging,
                selectedPlan = uiState.powerPlan,
                onSelectPowerPlan = onSelectPowerPlan,
                onOpenControlPanel = onOpenControlPanel
            )
            TrayPopupType.HIDDEN_TRAY_ICONS -> Win7HiddenTrayFlyout(
                isDefaultLauncher = uiState.isDefaultLauncher,
                onLockComputer = onLockComputer,
                onPromptDefaultLauncher = onPromptDefaultLauncher,
                onOpenControlPanel = onOpenControlPanel
            )
            TrayPopupType.NONE -> {}
        }
    }
}

@Composable
private fun Win7ClockCalendarFlyout() {
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val fullDateHeader = remember(nowMillis) {
        SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date(nowMillis))
    }
    val digitalTime = remember(nowMillis) {
        SimpleDateFormat("h:mm:ss a", Locale.getDefault()).format(Date(nowMillis))
    }

    Column(
        modifier = Modifier.width(276.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = fullDateHeader,
            fontSize = 12.sp,
            color = Color(0xFF003399),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFCBD8E8), RoundedCornerShape(4.dp))
                    .padding(6.dp)
            ) {
                val monthTitle = remember(nowMillis) {
                    SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(nowMillis))
                }
                val currentDay = remember(nowMillis) {
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                }
                Text(
                    text = monthTitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E395B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { d ->
                        Text(text = d, fontSize = 9.sp, color = Color(0xFF607D8B), fontWeight = FontWeight.Bold)
                    }
                }
                for (week in 0..4) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 1.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (dow in 1..7) {
                            val dayNum = (week * 7 + dow).coerceAtMost(31)
                            val isToday = dayNum == currentDay
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(
                                        if (isToday) Color(0xFFD6EBFF) else Color.Transparent,
                                        RoundedCornerShape(2.dp)
                                    )
                                    .then(
                                        if (isToday) Modifier.border(1.dp, Color(0xFF2979FF), RoundedCornerShape(2.dp))
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNum.toString(),
                                    fontSize = 9.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isToday) Color(0xFF0D47A1) else Color(0xFF263238)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Canvas(modifier = Modifier.size(96.dp)) {
                    val cal = Calendar.getInstance().apply { timeInMillis = nowMillis }
                    val sec = cal.get(Calendar.SECOND)
                    val min = cal.get(Calendar.MINUTE)
                    val hr = cal.get(Calendar.HOUR)

                    val r = size.minDimension / 2f
                    val c = Offset(r, r)

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White, Color(0xFFE3F2FD), Color(0xFF90CAF9))
                        ),
                        radius = r * 0.92f,
                        center = c
                    )
                    drawCircle(
                        color = Color(0xFF455A64),
                        radius = r * 0.92f,
                        center = c,
                        style = Stroke(width = 3f)
                    )

                    for (i in 0 until 12) {
                        val a = (i * 30 - 90) * (Math.PI / 180.0)
                        val p1 = Offset(c.x + (cos(a) * r * 0.74f).toFloat(), c.y + (sin(a) * r * 0.74f).toFloat())
                        val p2 = Offset(c.x + (cos(a) * r * 0.85f).toFloat(), c.y + (sin(a) * r * 0.85f).toFloat())
                        drawLine(Color(0xFF37474F), p1, p2, strokeWidth = if (i % 3 == 0) 2.5f else 1.2f)
                    }

                    val hrAngle = ((hr + min / 60f) * 30f - 90f) * (Math.PI / 180.0)
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = c,
                        end = Offset(c.x + (cos(hrAngle) * r * 0.48f).toFloat(), c.y + (sin(hrAngle) * r * 0.48f).toFloat()),
                        strokeWidth = 3.5f,
                        cap = StrokeCap.Round
                    )

                    val minAngle = ((min + sec / 60f) * 6f - 90f) * (Math.PI / 180.0)
                    drawLine(
                        color = Color(0xFF334155),
                        start = c,
                        end = Offset(c.x + (cos(minAngle) * r * 0.68f).toFloat(), c.y + (sin(minAngle) * r * 0.68f).toFloat()),
                        strokeWidth = 2.5f,
                        cap = StrokeCap.Round
                    )

                    val secAngle = (sec * 6f - 90f) * (Math.PI / 180.0)
                    drawLine(
                        color = Color(0xFFD32F2F),
                        start = c,
                        end = Offset(c.x + (cos(secAngle) * r * 0.76f).toFloat(), c.y + (sin(secAngle) * r * 0.76f).toFloat()),
                        strokeWidth = 1.4f,
                        cap = StrokeCap.Round
                    )
                    drawCircle(Color(0xFFD32F2F), radius = 3.5f, center = c)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = digitalTime,
                    fontSize = 11.sp,
                    color = Color(0xFF37474F),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun Win7VolumeMixerFlyout(
    volumePercent: Int,
    isMuted: Boolean,
    onVolumeChange: (Int) -> Unit,
    onToggleMute: () -> Unit
) {
    Column(
        modifier = Modifier.width(160.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Speakers (High Definition Audio)",
            fontSize = 11.sp,
            color = Color(0xFF1E395B),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isMuted) "Muted (0%)" else "$volumePercent%",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0066CC)
        )
        Slider(
            value = volumePercent.toFloat(),
            onValueChange = { onVolumeChange(it.toInt()) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF1565C0),
                activeTrackColor = Color(0xFF42A5F5)
            )
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFE3F2FD))
                .border(1.dp, Color(0xFF90CAF9), RoundedCornerShape(4.dp))
                .clickable(onClick = onToggleMute)
                .padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Toggle Mute",
                tint = if (isMuted) Color(0xFFD32F2F) else Color(0xFF1565C0),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isMuted) "Unmute" else "Mute",
                fontSize = 11.sp,
                color = Color(0xFF1E395B)
            )
        }
    }
}

@Composable
private fun Win7NetworkFlyout(
    isConnected: Boolean,
    networkName: String,
    onOpenNetworkCenter: () -> Unit
) {
    Column(modifier = Modifier.width(230.dp)) {
        Text(
            text = if (isConnected) "Currently connected to:" else "Not connected",
            fontSize = 11.sp,
            color = Color(0xFF546E7A)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFCFD8DC), RoundedCornerShape(4.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Win7SystemIconCanvas(iconType = Win7SystemIconType.NETWORK, size = 30.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = networkName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E395B)
                )
                Text(
                    text = if (isConnected) "Internet access" else "Limited access",
                    fontSize = 11.sp,
                    color = if (isConnected) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Open Network and Sharing Center",
            fontSize = 11.sp,
            color = Color(0xFF0066CC),
            modifier = Modifier
                .clickable(onClick = onOpenNetworkCenter)
                .padding(vertical = 4.dp)
        )
    }
}

@Composable
private fun Win7BatteryFlyout(
    batteryPercent: Int,
    isCharging: Boolean,
    selectedPlan: String,
    onSelectPowerPlan: (String) -> Unit,
    onOpenControlPanel: () -> Unit
) {
    Column(modifier = Modifier.width(220.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "$batteryPercent% available",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E395B)
                )
                Text(
                    text = if (isCharging) "(plugged in, charging)" else "(on battery power)",
                    fontSize = 11.sp,
                    color = Color(0xFF546E7A)
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFCFD8DC))
        Text(
            text = "Select a power plan:",
            fontSize = 11.sp,
            color = Color(0xFF455A64),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        listOf("Balanced (recommended)", "High performance", "Power saver").forEach { plan ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectPowerPlan(plan) }
                    .padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = if (selectedPlan == plan) Color(0xFF0066CC) else Color.Transparent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = plan,
                    fontSize = 11.sp,
                    fontWeight = if (selectedPlan == plan) FontWeight.SemiBold else FontWeight.Normal,
                    color = Color(0xFF1E293B)
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFCFD8DC))
        Text(
            text = "More power options",
            fontSize = 11.sp,
            color = Color(0xFF0066CC),
            modifier = Modifier.clickable(onClick = onOpenControlPanel)
        )
    }
}

@Composable
private fun Win7HiddenTrayFlyout(
    isDefaultLauncher: Boolean,
    onLockComputer: () -> Unit,
    onPromptDefaultLauncher: () -> Unit,
    onOpenControlPanel: () -> Unit
) {
    Column(
        modifier = Modifier.width(196.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Windows Action Center",
                tint = Color(0xFF1E88E5),
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onOpenControlPanel)
            )
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock Windows 7",
                tint = Color(0xFFF57C00),
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onLockComputer)
            )
            if (!isDefaultLauncher) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Set Default Launcher",
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onPromptDefaultLauncher)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Windows Update",
                    tint = Color(0xFF43A047),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onOpenControlPanel)
                )
            }
        }
        HorizontalDivider(color = Color(0xFFCFD8DC))
        Text(
            text = if (isDefaultLauncher) "Lock Workstation (Win + L)" else "Set as Default Home Launcher...",
            fontSize = 11.sp,
            color = Color(0xFF0066CC),
            modifier = Modifier
                .clickable(onClick = if (isDefaultLauncher) onLockComputer else onPromptDefaultLauncher)
                .padding(top = 6.dp)
        )
    }
}
