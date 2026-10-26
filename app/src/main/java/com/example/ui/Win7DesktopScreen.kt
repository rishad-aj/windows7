package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.model.DesktopIconItem
import com.example.model.DesktopIconSize
import com.example.model.IconPosition
import com.example.model.Win7SystemIconType
import com.example.model.Win7WindowType
import com.example.ui.components.Win7AeroTourGalleryWindowContent
import com.example.ui.components.Win7AeroWindowFrame
import com.example.ui.components.Win7AppPropertiesWindowContent
import com.example.ui.components.Win7AuthenticLockScreenOverlay
import com.example.ui.components.Win7CalculatorWindowContent
import com.example.ui.components.Win7CommandPromptWindowContent
import com.example.ui.components.Win7ControlPanelWindowContent
import com.example.ui.components.Win7DesktopContextMenu
import com.example.ui.components.Win7DesktopGadgetsColumn
import com.example.ui.components.Win7DesktopWallpaper
import com.example.ui.components.Win7ExplorerWindowContent
import com.example.ui.components.Win7Flip3dOverlay
import com.example.ui.components.Win7GadgetGalleryWindowContent
import com.example.ui.components.Win7MediaPlayerWindowContent
import com.example.ui.components.Win7NetworkCenterWindowContent
import com.example.ui.components.Win7NotepadWindowContent
import com.example.ui.components.Win7PaintWindowContent
import com.example.ui.components.Win7PersonalizationWindowContent
import com.example.ui.components.Win7RecycleBinWindowContent
import com.example.ui.components.Win7StartMenuPopup
import com.example.ui.components.Win7SuperbarTaskbar
import com.example.ui.components.Win7SystemIconCanvas
import com.example.ui.components.Win7TaskManagerWindowContent
import com.example.ui.components.Win7TrayFlyoutPopup
import com.example.ui.components.Win7UserAccountsWindowContent
import com.example.viewmodel.PowerActionState
import com.example.viewmodel.TrayPopupType
import com.example.viewmodel.Win7LauncherViewModel
import kotlin.math.hypot
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun Win7DesktopScreen(
    viewModel: Win7LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()

    // Zero-permission Android Photo Picker for importing any original wallpaper image
    val wallpaperPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.importCustomWallpaperUri(uri, context)
        }
    }

    val desktopIcons = remember(settings, uiState.installedApps, uiState.isDefaultLauncher) {
        viewModel.getDesktopIcons(settings, uiState.installedApps, uiState.isDefaultLauncher)
    }

    // Intercept system Back press so the launcher behaves like a true desktop OS
    BackHandler(
        enabled = uiState.isStartMenuOpen ||
            uiState.isContextMenuOpen ||
            uiState.activeTrayPopup != TrayPopupType.NONE ||
            uiState.isAeroPeekActive ||
            uiState.isFlip3dActive ||
            uiState.powerState != PowerActionState.NONE ||
            uiState.openWindows.any { !it.isMinimized }
    ) {
        when {
            uiState.powerState != PowerActionState.NONE -> {
                if (settings.lockScreenPin.isBlank()) {
                    viewModel.triggerPowerAction(PowerActionState.NONE)
                }
            }
            uiState.isFlip3dActive || uiState.isStartMenuOpen || uiState.isContextMenuOpen ||
                uiState.activeTrayPopup != TrayPopupType.NONE || uiState.isAeroPeekActive -> {
                viewModel.dismissAllOverlays()
            }
            else -> {
                val topWin = uiState.openWindows.filter { !it.isMinimized }.maxByOrNull { it.zIndex }
                if (topWin != null) {
                    viewModel.closeWindow(topWin.id)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("win7_desktop_root")
    ) {
        // 1. Full-Screen Authentic Windows 7 Wallpaper (12 Official Presets + Custom Image + High Contrast)
        Win7DesktopWallpaper(
            themePreset = settings.themePreset,
            highContrast = settings.highContrastMode,
            customWallpaperPath = settings.customWallpaperPath,
            fitMode = settings.wallpaperFitMode,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Main Desktop Workspace + Bottom Superbar Column
        Column(modifier = Modifier.fillMaxSize()) {
            // Desktop Workspace Area (above Superbar)
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { viewModel.dismissAllOverlays() },
                            onLongPress = { viewModel.setContextMenuOpen(true) }
                        )
                    }
            ) {
                val workspaceWidthDp = maxWidth.value.coerceAtLeast(320f)
                val workspaceHeightDp = maxHeight.value.coerceAtLeast(400f)
                val cellW = settings.iconSize.cellWidthDp.toFloat()
                val cellH = settings.iconSize.cellHeightDp.toFloat()
                val stepX = cellW + 6f
                val stepY = cellH + 6f
                val rowsPerCol = ((workspaceHeightDp - 16f) / stepY).toInt().coerceAtLeast(4)

                // Compute resolved (xDp, yDp) for every desktop icon (either custom dragged position or default column slot)
                val resolvedIconPositions: Map<String, IconPosition> = remember(
                    desktopIcons,
                    settings.customIconPositions,
                    settings.autoArrangeIcons,
                    workspaceWidthDp,
                    workspaceHeightDp,
                    cellW,
                    cellH
                ) {
                    val map = mutableMapOf<String, IconPosition>()
                    desktopIcons.forEachIndexed { index, item ->
                        val col = index / rowsPerCol
                        val row = index % rowsPerCol
                        val defaultX = (6f + col * stepX).coerceIn(2f, (workspaceWidthDp - cellW - 2f).coerceAtLeast(2f))
                        val defaultY = (6f + row * stepY).coerceIn(2f, (workspaceHeightDp - cellH - 2f).coerceAtLeast(2f))
                        val saved = if (!settings.autoArrangeIcons) settings.customIconPositions[item.id] else null
                        val finalX = (saved?.xDp ?: defaultX).coerceIn(0f, (workspaceWidthDp - cellW).coerceAtLeast(0f))
                        val finalY = (saved?.yDp ?: defaultY).coerceIn(0f, (workspaceHeightDp - cellH).coerceAtLeast(0f))
                        map[item.id] = IconPosition(finalX, finalY)
                    }
                    map
                }

                // 2A. Free-Move Desktop Icons Canvas (Move any app or system icon anywhere on screen!)
                if (settings.showDesktopIcons && !uiState.isRefreshingDesktop) {
                    val recyclePos = resolvedIconPositions["sys_recycle_bin"]

                    desktopIcons.forEach { item ->
                        val basePos = resolvedIconPositions[item.id] ?: IconPosition(6f, 6f)
                        Win7DraggableDesktopIconCell(
                            item = item,
                            basePositionDp = basePos,
                            iconSize = settings.iconSize,
                            isSelected = uiState.selectedDesktopIconId == item.id,
                            isRecycleBinFull = settings.recycledItemIds.isNotEmpty(),
                            onClick = { viewModel.onDesktopIconActivated(item, context) },
                            onLongClick = {
                                if (item.id != "sys_recycle_bin") {
                                    viewModel.sendDesktopItemToRecycleBin(item.id, item.title)
                                } else {
                                    viewModel.openWindow(Win7WindowType.RECYCLE_BIN)
                                }
                            },
                            onDropAtDp = { rawXDp, rawYDp ->
                                val clampedX = rawXDp.coerceIn(0f, (workspaceWidthDp - cellW).coerceAtLeast(0f))
                                val clampedY = rawYDp.coerceIn(0f, (workspaceHeightDp - cellH).coerceAtLeast(0f))

                                // Check if dropped directly onto Recycle Bin center
                                if (item.id != "sys_recycle_bin" && recyclePos != null) {
                                    val dist = hypot(
                                        (clampedX - recyclePos.xDp).toDouble(),
                                        (clampedY - recyclePos.yDp).toDouble()
                                    )
                                    if (dist < 30.0) {
                                        viewModel.sendDesktopItemToRecycleBin(item.id, item.title)
                                        return@Win7DraggableDesktopIconCell
                                    }
                                }

                                val finalX = if (settings.alignIconsToGrid) {
                                    val col = ((clampedX - 6f) / stepX).roundToInt().coerceAtLeast(0)
                                    (6f + col * stepX).coerceIn(0f, (workspaceWidthDp - cellW).coerceAtLeast(0f))
                                } else {
                                    clampedX
                                }
                                val finalY = if (settings.alignIconsToGrid) {
                                    val row = ((clampedY - 6f) / stepY).roundToInt().coerceAtLeast(0)
                                    (6f + row * stepY).coerceIn(0f, (workspaceHeightDp - cellH).coerceAtLeast(0f))
                                } else {
                                    clampedY
                                }
                                viewModel.moveDesktopIcon(item.id, finalX, finalY)
                            }
                        )
                    }

                    // 2B. Draggable Right Sidebar: Windows 7 Desktop Gadgets & Sticky Notes
                    val defaultGadgetX = (workspaceWidthDp - 142f).coerceAtLeast(8f)
                    val savedGadgetPos = settings.customIconPositions["__gadgets_panel__"]
                    val gadgetBaseX = (savedGadgetPos?.xDp ?: defaultGadgetX).coerceIn(0f, (workspaceWidthDp - 136f).coerceAtLeast(0f))
                    val gadgetBaseY = (savedGadgetPos?.yDp ?: 6f).coerceIn(0f, (workspaceHeightDp - 120f).coerceAtLeast(0f))
                    var gadgetDragOffsetPx by remember(gadgetBaseX, gadgetBaseY) { mutableStateOf(Offset.Zero) }

                    Box(
                        modifier = Modifier
                            .offset {
                                val baseXPx = with(density) { gadgetBaseX.dp.toPx() }
                                val baseYPx = with(density) { gadgetBaseY.dp.toPx() }
                                IntOffset(
                                    (baseXPx + gadgetDragOffsetPx.x).roundToInt(),
                                    (baseYPx + gadgetDragOffsetPx.y).roundToInt()
                                )
                            }
                            .pointerInput(gadgetBaseX, gadgetBaseY) {
                                detectDragGestures(
                                    onDragEnd = {
                                        val dxDp = with(density) { gadgetDragOffsetPx.x.toDp().value }
                                        val dyDp = with(density) { gadgetDragOffsetPx.y.toDp().value }
                                        val nx = (gadgetBaseX + dxDp).coerceIn(0f, (workspaceWidthDp - 136f).coerceAtLeast(0f))
                                        val ny = (gadgetBaseY + dyDp).coerceIn(0f, (workspaceHeightDp - 120f).coerceAtLeast(0f))
                                        gadgetDragOffsetPx = Offset.Zero
                                        viewModel.moveDesktopIcon("__gadgets_panel__", nx, ny)
                                    },
                                    onDragCancel = {
                                        gadgetDragOffsetPx = Offset.Zero
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        gadgetDragOffsetPx += dragAmount
                                    }
                                )
                            }
                    ) {
                        Win7DesktopGadgetsColumn(
                            settings = settings,
                            onCloseGadget = { key -> viewModel.toggleGadget(key, false) },
                            onAddStickyNote = { viewModel.addStickyNote() },
                            onUpdateStickyNote = { id, txt -> viewModel.updateStickyNoteText(id, txt) },
                            onCycleStickyColor = { id -> viewModel.cycleStickyNoteColor(id) },
                            onDeleteStickyNote = { id -> viewModel.deleteStickyNote(id) }
                        )
                    }
                }

                // 2C. Floating / Maximizable Z-Ordered Windows 7 Aero Glass Windows
                val visibleWindows = remember(uiState.openWindows) {
                    uiState.openWindows.filter { !it.isMinimized }.sortedBy { it.zIndex }
                }

                visibleWindows.forEach { win ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(win.zIndex.toFloat() + 100f)
                    ) {
                        Win7AeroWindowFrame(
                            window = win,
                            isActive = uiState.activeWindowId == win.id,
                            aeroSwatch = settings.aeroSwatch,
                            aeroTransparency = settings.aeroTransparency,
                            glassIntensity = settings.glassIntensity,
                            isAeroPeekActive = uiState.isAeroPeekActive,
                            onFocus = { viewModel.focusWindow(win.id) },
                            onMinimize = { viewModel.toggleMinimizeWindow(win.id) },
                            onMaximizeRestore = { viewModel.toggleMaximizeWindow(win.id) },
                            onClose = { viewModel.closeWindow(win.id) },
                            onDrag = { dx, dy -> viewModel.moveWindow(win.id, dx, dy) }
                        ) {
                            when (win.type) {
                                Win7WindowType.COMPUTER_EXPLORER,
                                Win7WindowType.DOCUMENTS_LIBRARY -> {
                                    Win7ExplorerWindowContent(
                                        window = win,
                                        uiState = uiState,
                                        settings = settings,
                                        onNavigatePath = { path -> viewModel.navigateExplorer(win.id, path) },
                                        onSearchChange = { q -> viewModel.setExplorerSearchQuery(q) },
                                        onLaunchApp = { pkg -> viewModel.launchAndroidApp(pkg, context) },
                                        onOpenAppProperties = { app ->
                                            viewModel.openWindow(Win7WindowType.APP_PROPERTIES, targetApp = app)
                                        },
                                        onOpenWindow = { type -> viewModel.openWindow(type) },
                                        onLockComputer = { viewModel.triggerPowerAction(PowerActionState.LOCKED_SCREEN) },
                                        onPromptDefaultLauncher = { viewModel.promptSetDefaultLauncher(context) },
                                        onRefresh = { viewModel.refreshDesktop() }
                                    )
                                }

                                Win7WindowType.CONTROL_PANEL -> {
                                    Win7ControlPanelWindowContent(
                                        uiState = uiState,
                                        onOpenWindow = { type, path -> viewModel.openWindow(type, path) },
                                        onLockComputer = { viewModel.triggerPowerAction(PowerActionState.LOCKED_SCREEN) },
                                        onPromptDefaultLauncher = { viewModel.promptSetDefaultLauncher(context) }
                                    )
                                }

                                Win7WindowType.PERSONALIZATION -> {
                                    Win7PersonalizationWindowContent(
                                        settings = settings,
                                        onSelectTheme = { viewModel.selectThemePreset(it) },
                                        onBrowseCustomWallpaper = {
                                            wallpaperPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        onClearCustomWallpaper = { viewModel.clearCustomWallpaper() },
                                        onSelectFitMode = { viewModel.setWallpaperFitMode(it) },
                                        onSelectSwatch = { viewModel.selectAeroSwatch(it) },
                                        onSetTransparency = { viewModel.setAeroTransparency(it) },
                                        onSetIntensity = { viewModel.setGlassIntensity(it) },
                                        onSetSmallTaskbar = { viewModel.setSmallTaskbarIcons(it) },
                                        onSetShowFlip3d = { viewModel.setShowFlip3dOnTaskbar(it) },
                                        onSetHighContrast = { viewModel.setHighContrastMode(it) },
                                        onToggleAutoArrange = { viewModel.toggleAutoArrangeIcons() },
                                        onToggleAlignToGrid = { viewModel.toggleAlignIconsToGrid() },
                                        onResetIconPositions = { viewModel.resetDesktopIconPositions() },
                                        onAddAllAppsToDesktop = { viewModel.addAllInstalledAppsToDesktop() },
                                        onSelectIconSize = { viewModel.setDesktopIconSize(it) },
                                        onUpdateUserProfile = { n, av -> viewModel.updateUserProfile(n, av) },
                                        onSetSoundEffects = { viewModel.setSoundEffectsEnabled(it) },
                                        onOpenUserAccounts = { viewModel.openWindow(Win7WindowType.USER_ACCOUNTS) }
                                    )
                                }

                                Win7WindowType.USER_ACCOUNTS -> {
                                    Win7UserAccountsWindowContent(
                                        settings = settings,
                                        onUpdateProfile = { name, av -> viewModel.updateUserProfile(name, av) },
                                        onUpdateLockConfig = { pin, startup -> viewModel.updateLockScreenConfig(pin, startup) },
                                        onLockDesktopNow = { viewModel.triggerPowerAction(PowerActionState.LOCKED_SCREEN) }
                                    )
                                }

                                Win7WindowType.PAINT -> {
                                    Win7PaintWindowContent()
                                }

                                Win7WindowType.NOTEPAD -> {
                                    Win7NotepadWindowContent(
                                        content = settings.notepadContent,
                                        onContentChange = { viewModel.saveNotepadText(it) }
                                    )
                                }

                                Win7WindowType.MEDIA_PLAYER -> {
                                    Win7MediaPlayerWindowContent(
                                        onPlaySampleNote = { viewModel.playSampleNote() }
                                    )
                                }

                                Win7WindowType.AERO_TOUR_GALLERY -> {
                                    Win7AeroTourGalleryWindowContent(
                                        slides = viewModel.screenshotTourSlides,
                                        activeIndex = uiState.activeTourSlideIndex,
                                        onSelectSlide = { viewModel.selectTourSlide(it) },
                                        onApplyPreset = { viewModel.applyTourSlidePreset(it) },
                                        onOpenFeaturedWindow = { viewModel.openWindow(it) }
                                    )
                                }

                                Win7WindowType.COMMAND_PROMPT -> {
                                    Win7CommandPromptWindowContent(
                                        uiState = uiState,
                                        userName = settings.userName,
                                        onExecuteCommand = { cmd -> viewModel.executeCommandPrompt(cmd, context) }
                                    )
                                }

                                Win7WindowType.CALCULATOR -> {
                                    Win7CalculatorWindowContent()
                                }

                                Win7WindowType.RECYCLE_BIN -> {
                                    Win7RecycleBinWindowContent(
                                        settings = settings,
                                        onRestoreItem = { viewModel.restoreRecycleBinItem(it) },
                                        onRestoreAll = { viewModel.emptyOrRestoreAllRecycleBin() }
                                    )
                                }

                                Win7WindowType.GADGET_GALLERY -> {
                                    Win7GadgetGalleryWindowContent(
                                        settings = settings,
                                        onToggleGadget = { k, en -> viewModel.toggleGadget(k, en) },
                                        onAddStickyNote = { viewModel.addStickyNote() }
                                    )
                                }

                                Win7WindowType.TASK_MANAGER -> {
                                    Win7TaskManagerWindowContent(
                                        uiState = uiState,
                                        onFocusWindow = { viewModel.focusWindow(it) },
                                        onCloseWindow = { viewModel.closeWindow(it) }
                                    )
                                }

                                Win7WindowType.NETWORK_CENTER -> {
                                    Win7NetworkCenterWindowContent(uiState = uiState)
                                }

                                Win7WindowType.APP_PROPERTIES -> {
                                    Win7AppPropertiesWindowContent(
                                        app = win.targetApp,
                                        settings = settings,
                                        onLaunchApp = { pkg -> viewModel.launchAndroidApp(pkg, context) },
                                        onTogglePinTaskbar = { pkg, lbl -> viewModel.togglePinAppToTaskbar(pkg, lbl) },
                                        onToggleDesktopShortcut = { pkg, lbl -> viewModel.toggleAppDesktopShortcut(pkg, lbl) },
                                        onOpenAndroidAppInfo = { pkg -> viewModel.openAndroidAppSettings(pkg, context) }
                                    )
                                }
                            }
                        }
                    }
                }

                // 2D. Desktop Right-Click Context Menu Overlay
                if (uiState.isContextMenuOpen) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .zIndex(990f)
                    ) {
                        Win7DesktopContextMenu(
                            settings = settings,
                            isDefaultLauncher = uiState.isDefaultLauncher,
                            onSelectIconSize = { viewModel.setDesktopIconSize(it) },
                            onSelectSortMode = { viewModel.setDesktopSortMode(it) },
                            onToggleShowDesktopIcons = { viewModel.setShowDesktopIcons(!settings.showDesktopIcons) },
                            onToggleAutoArrange = { viewModel.toggleAutoArrangeIcons() },
                            onToggleAlignToGrid = { viewModel.toggleAlignIconsToGrid() },
                            onResetIconPositions = { viewModel.resetDesktopIconPositions() },
                            onAddAllAppsToDesktop = { viewModel.addAllInstalledAppsToDesktop() },
                            onRefresh = { viewModel.refreshDesktop() },
                            onNewStickyNote = { viewModel.addStickyNote() },
                            onOpenFlip3d = { viewModel.toggleFlip3d() },
                            onLockComputer = { viewModel.triggerPowerAction(PowerActionState.LOCKED_SCREEN) },
                            onOpenGadgets = { viewModel.openWindow(Win7WindowType.GADGET_GALLERY) },
                            onOpenPersonalize = { viewModel.openWindow(Win7WindowType.PERSONALIZATION) },
                            onSetDefaultLauncher = { viewModel.promptSetDefaultLauncher(context) }
                        )
                    }
                }

                // 2E. Two-Column Windows 7 Start Menu Popup (Anchored Bottom-Start above Superbar)
                if (uiState.isStartMenuOpen) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .zIndex(1000f)
                    ) {
                        Win7StartMenuPopup(
                            uiState = uiState,
                            settings = settings,
                            onLaunchAndroidApp = { pkg -> viewModel.launchAndroidApp(pkg, context) },
                            onOpenWindow = { type, path, app -> viewModel.openWindow(type, path, app) },
                            onToggleAllPrograms = { viewModel.setAllProgramsInStart(it) },
                            onSearchQueryChange = { viewModel.updateStartSearchQuery(it) },
                            onToggleFolder = { viewModel.toggleStartFolder(it) },
                            onPromptSetDefaultLauncher = { viewModel.promptSetDefaultLauncher(context) },
                            onToggleShutdownMenu = { viewModel.toggleShutdownSubmenu() },
                            onTriggerPowerAction = { viewModel.triggerPowerAction(it) },
                            onRefreshDesktop = { viewModel.refreshDesktop() }
                        )
                    }
                }

                // 2F. System Tray Notification Flyout Popup (Anchored Bottom-End above Superbar)
                if (uiState.activeTrayPopup != TrayPopupType.NONE) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .zIndex(1001f)
                    ) {
                        Win7TrayFlyoutPopup(
                            uiState = uiState,
                            onVolumeChange = { viewModel.setSystemVolume(it) },
                            onToggleMute = { viewModel.toggleMuteVolume() },
                            onSelectPowerPlan = { viewModel.selectPowerPlan(it) },
                            onOpenNetworkCenter = { viewModel.openWindow(Win7WindowType.NETWORK_CENTER) },
                            onOpenControlPanel = { viewModel.openWindow(Win7WindowType.CONTROL_PANEL) },
                            onLockComputer = { viewModel.triggerPowerAction(PowerActionState.LOCKED_SCREEN) },
                            onPromptDefaultLauncher = { viewModel.promptSetDefaultLauncher(context) }
                        )
                    }
                }

                // 2G. Aero Balloon Status Toast Notification (Bottom-Right Balloon Tip)
                uiState.statusToastMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 12.dp, bottom = 8.dp)
                            .zIndex(1005f)
                            .shadow(8.dp, RoundedCornerShape(6.dp))
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFFDE7))
                            .border(1.dp, Color(0xFF90A4AE), RoundedCornerShape(6.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = msg,
                            fontSize = 11.sp,
                            color = Color(0xFF1E293B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 3. Bottom Windows 7 Aero Glass Superbar Taskbar
            Win7SuperbarTaskbar(
                uiState = uiState,
                settings = settings,
                onToggleStartMenu = { viewModel.toggleStartMenu() },
                onToggleFlip3d = { viewModel.toggleFlip3d() },
                onOpenWindow = { type, path -> viewModel.openWindow(type, path) },
                onToggleWindowFromTaskbar = { winId -> viewModel.toggleMinimizeWindow(winId) },
                onLaunchAndroidApp = { pkg -> viewModel.launchAndroidApp(pkg, context) },
                onToggleTrayPopup = { popup -> viewModel.toggleTrayPopup(popup) },
                onShowDesktopClick = { viewModel.toggleShowDesktopOrAeroPeek(enableXRayPeekOnly = false) },
                onAeroPeekLongClick = { viewModel.toggleShowDesktopOrAeroPeek(enableXRayPeekOnly = true) }
            )
        }

        // 4. Windows Aero Flip 3D (Win + Tab) Cascading 3D Window Switcher Overlay
        if (uiState.isFlip3dActive) {
            Win7Flip3dOverlay(
                openWindows = uiState.openWindows,
                onSelectWindow = { winId -> viewModel.focusWindow(winId) },
                onDismiss = { viewModel.toggleFlip3d() }
            )
        }

        // 5. Authentic Windows 7 Lock & Logon Screen Overlay (authui.exe)
        if (uiState.powerState != PowerActionState.NONE) {
            Win7AuthenticLockScreenOverlay(
                powerState = uiState.powerState,
                settings = settings,
                onUnlockSuccess = { viewModel.triggerPowerAction(PowerActionState.NONE) },
                onSwitchUserAccount = { name, av -> viewModel.updateUserProfile(name, av) },
                onTriggerPowerState = { state -> viewModel.triggerPowerAction(state) },
                onRestartDesktop = { viewModel.refreshDesktop() },
                onToggleHighContrast = { viewModel.setHighContrastMode(it) },
                onToggleLargeIcons = { large ->
                    viewModel.setDesktopIconSize(if (large) DesktopIconSize.LARGE else DesktopIconSize.MEDIUM)
                },
                onToggleSoundEffects = { viewModel.setSoundEffectsEnabled(it) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Win7DraggableDesktopIconCell(
    item: DesktopIconItem,
    basePositionDp: IconPosition,
    iconSize: DesktopIconSize,
    isSelected: Boolean,
    isRecycleBinFull: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDropAtDp: (Float, Float) -> Unit
) {
    val density = LocalDensity.current
    val cellShape = RoundedCornerShape(4.dp)
    var dragOffsetPx by remember(item.id, basePositionDp.xDp, basePositionDp.yDp) {
        mutableStateOf(Offset.Zero)
    }
    var isDragging by remember(item.id) { mutableStateOf(false) }

    val highlightActive = isSelected || isDragging

    Column(
        modifier = Modifier
            .offset {
                val baseXPx = with(density) { basePositionDp.xDp.dp.toPx() }
                val baseYPx = with(density) { basePositionDp.yDp.dp.toPx() }
                IntOffset(
                    x = (baseXPx + dragOffsetPx.x).roundToInt(),
                    y = (baseYPx + dragOffsetPx.y).roundToInt()
                )
            }
            .zIndex(if (isDragging) 60f else if (isSelected) 10f else 1f)
            .width(iconSize.cellWidthDp.dp)
            .height(iconSize.cellHeightDp.dp)
            .clip(cellShape)
            .background(
                if (highlightActive) {
                    Brush.verticalGradient(
                        colors = listOf(Color(0x66B3E5FC), Color(0x884FC3F7))
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    )
                }
            )
            .then(
                if (highlightActive) {
                    Modifier.border(1.dp, Color(0xDDE1F5FE), cellShape)
                } else Modifier
            )
            .pointerInput(item.id, basePositionDp.xDp, basePositionDp.yDp) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false
                        val dxDp = with(density) { dragOffsetPx.x.toDp().value }
                        val dyDp = with(density) { dragOffsetPx.y.toDp().value }
                        val targetX = basePositionDp.xDp + dxDp
                        val targetY = basePositionDp.yDp + dyDp
                        dragOffsetPx = Offset.Zero
                        onDropAtDp(targetX, targetY)
                    },
                    onDragCancel = {
                        isDragging = false
                        dragOffsetPx = Offset.Zero
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffsetPx += dragAmount
                    }
                )
            }
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .testTag("desktop_icon_${item.id}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.BottomStart) {
            if (item.iconType == Win7SystemIconType.ANDROID_APP && item.appIcon != null) {
                Image(
                    bitmap = item.appIcon,
                    contentDescription = item.title,
                    modifier = Modifier.size(iconSize.iconDp.dp)
                )
            } else {
                Win7SystemIconCanvas(
                    iconType = item.iconType,
                    size = iconSize.iconDp.dp,
                    isRecycleBinFull = isRecycleBinFull
                )
            }

            // Classic Windows 7 Curved Blue Shortcut Arrow Overlay Badge on app shortcuts
            if (item.iconType == Win7SystemIconType.ANDROID_APP ||
                item.iconType == Win7SystemIconType.SET_DEFAULT_LAUNCHER
            ) {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFF1565C0), RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "↗",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1),
                        lineHeight = 9.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.White,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                shadow = Shadow(
                    color = Color(0xEE000000),
                    offset = Offset(1f, 1.5f),
                    blurRadius = 3f
                )
            ),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
