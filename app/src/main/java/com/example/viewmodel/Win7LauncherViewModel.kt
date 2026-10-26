package com.example.viewmodel

import android.app.Application
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LauncherPreferencesRepository
import com.example.data.PersistedLauncherSettings
import com.example.model.AeroGlassSwatch
import com.example.model.DesktopIconItem
import com.example.model.DesktopIconSize
import com.example.model.DesktopSortMode
import com.example.model.ExplorerPath
import com.example.model.InstalledAppInfo
import com.example.model.StickyNoteItem
import com.example.model.StorageDriveInfo
import com.example.model.WallpaperFitMode
import com.example.model.Win7AvatarPreset
import com.example.model.Win7ScreenshotSlide
import com.example.model.Win7SystemIconType
import com.example.model.Win7ThemePreset
import com.example.model.Win7WindowInstance
import com.example.model.Win7WindowType
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class TrayPopupType {
    NONE,
    CLOCK_CALENDAR,
    VOLUME_MIXER,
    NETWORK_FLYOUT,
    BATTERY_POWER,
    HIDDEN_TRAY_ICONS
}

enum class PowerActionState {
    NONE,
    SHUTTING_DOWN,
    LOCKED_SCREEN
}

data class DesktopUiState(
    val installedApps: List<InstalledAppInfo> = emptyList(),
    val isLoadingApps: Boolean = true,
    val isDefaultLauncher: Boolean = false,
    val openWindows: List<Win7WindowInstance> = emptyList(),
    val activeWindowId: String? = null,
    val isStartMenuOpen: Boolean = false,
    val isAllProgramsInStart: Boolean = false,
    val startSearchQuery: String = "",
    val expandedStartFolders: Set<String> = setOf("Accessories (Windows 7)", "Installed Android Apps"),
    val isShutdownMenuExpanded: Boolean = false,
    val activeTrayPopup: TrayPopupType = TrayPopupType.NONE,
    val isAeroPeekActive: Boolean = false,
    val isFlip3dActive: Boolean = false,
    val isContextMenuOpen: Boolean = false,
    val selectedDesktopIconId: String? = null,
    val isRefreshingDesktop: Boolean = false,
    val drives: List<StorageDriveInfo> = emptyList(),
    val batteryPercent: Int = 88,
    val isCharging: Boolean = true,
    val powerPlan: String = "Balanced (recommended)",
    val isNetworkConnected: Boolean = true,
    val networkName: String = "HomeGroup Aero-5G",
    val volumePercent: Int = 75,
    val isMuted: Boolean = false,
    val explorerSearchQuery: String = "",
    val cmdHistory: List<String> = listOf(
        "Microsoft Windows [Version 6.1.7601]",
        "Copyright (c) 2009 Microsoft Corporation. All rights reserved.",
        "Type 'help' for available commands, 'apps' to list Android apps, 'lock' to lock PC, or 'ver'.",
        ""
    ),
    val powerState: PowerActionState = PowerActionState.NONE,
    val activeTourSlideIndex: Int = 0,
    val statusToastMessage: String? = null
)

class Win7LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LauncherPreferencesRepository(application.applicationContext)

    val settings: StateFlow<PersistedLauncherSettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PersistedLauncherSettings()
    )

    private val _uiState = MutableStateFlow(DesktopUiState())
    val uiState: StateFlow<DesktopUiState> = _uiState.asStateFlow()

    val screenshotTourSlides: List<Win7ScreenshotSlide> = listOf(
        Win7ScreenshotSlide(
            id = "slide_harmony",
            title = "1. Windows 7 Aero Glass & Harmony Desktop",
            caption = "The iconic October 2009 Windows 7 Ultimate desktop featuring the Harmony sky-blue light ribbons, dandelion seed art, glowing Start Orb, translucent Superbar, and Aero Glass window chrome.",
            highlights = listOf("Sky Aero Glass Tint", "Harmony Wallpaper", "Superbar & Start Orb", "Aero Peek Strip"),
            presetTheme = Win7ThemePreset.HARMONY,
            presetSwatch = AeroGlassSwatch.SKY,
            windowToOpen = Win7WindowType.COMPUTER_EXPLORER
        ),
        Win7ScreenshotSlide(
            id = "slide_lockscreen",
            title = "2. Authentic Windows 7 Logon & Lock Screen",
            caption = "Exact Windows 7 authui.exe Lock Screen with glowing swallow bird & botanical vines, metallic glass user tile frame, sunken password box with blue submit arrow, Switch User, Ease of Access, and Windows 7 Ultimate emblem.",
            highlights = listOf("Logon Swallow Artwork", "Glass Bevel Avatar", "Switch User & PIN", "Ease of Access"),
            presetTheme = Win7ThemePreset.LOGON_SWALLOW,
            presetSwatch = AeroGlassSwatch.TWILIGHT,
            windowToOpen = Win7WindowType.USER_ACCOUNTS
        ),
        Win7ScreenshotSlide(
            id = "slide_betta",
            title = "3. Windows 7 Beta Siamese Betta Fish & Media Player 12",
            caption = "The legendary Windows 7 Beta Betta Fish wallpaper paired with Windows Media Player 12 featuring animated Alchemy equalizer bars and glossy dark-blue transport controls.",
            highlights = listOf("Betta Fish Wallpaper", "WMP 12 Visualizer", "Seafoam Aero Glass", "Sample Music Library"),
            presetTheme = Win7ThemePreset.BETTA_FISH,
            presetSwatch = AeroGlassSwatch.SEAFOAM,
            windowToOpen = Win7WindowType.MEDIA_PLAYER
        ),
        Win7ScreenshotSlide(
            id = "slide_explorer",
            title = "4. Windows Explorer, Paint & Notepad",
            caption = "Breadcrumb address bar, sky-blue Command Bar, cyan horizontal capacity bars for Local Disk (C:) & Android Storage (D:), plus classic Ribbon Paint and Notepad.",
            highlights = listOf("Cyan Storage Bars", "Ribbon Paint Canvas", "Classic Notepad", "App Properties"),
            presetTheme = Win7ThemePreset.HARMONY,
            presetSwatch = AeroGlassSwatch.SKY,
            windowToOpen = Win7WindowType.PAINT
        ),
        Win7ScreenshotSlide(
            id = "slide_gadgets",
            title = "5. Bliss Landscapes, Desktop Gadgets & Sticky Notes",
            caption = "Rolling emerald Bliss hills with floating Windows 7 Desktop Gadgets: glossy dark Analog Clock, Dual-Dial CPU/RAM Speedometer Gauge, Orange Spiral Calendar, and Yellow Sticky Notes.",
            highlights = listOf("Analog Clock Gadget", "CPU & RAM Dual Dial", "Spiral Date Calendar", "Yellow Sticky Notes"),
            presetTheme = Win7ThemePreset.LANDSCAPES,
            presetSwatch = AeroGlassSwatch.EMERALD,
            windowToOpen = Win7WindowType.GADGET_GALLERY
        ),
        Win7ScreenshotSlide(
            id = "slide_aeropeek",
            title = "6. Aero Flip 3D (Win+Tab) & Aero Peek X-Ray Glass",
            caption = "Cascade open windows in 3D perspective with Aero Flip 3D, or hold the 14px Show Desktop strip at the far right of the Taskbar to turn windows into glowing X-ray glass wireframes.",
            highlights = listOf("Aero Flip 3D Stack", "X-Ray Glass Wireframes", "Live Task Manager", "12 Win7 Wallpapers"),
            presetTheme = Win7ThemePreset.AURORA,
            presetSwatch = AeroGlassSwatch.TWILIGHT,
            windowToOpen = Win7WindowType.TASK_MANAGER
        )
    )

    init {
        refreshSystemTelemetry()
        loadInstalledApps()
        checkDefaultLauncherStatus()
        viewModelScope.launch {
            val initialPrefs = repository.settingsFlow.first()
            if (initialPrefs.requireLockOnStartup) {
                _uiState.update { it.copy(powerState = PowerActionState.LOCKED_SCREEN) }
            }
        }
    }

    fun checkDefaultLauncherStatus() {
        val context = getApplication<Application>().applicationContext
        var isDefault = false
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val roleManager = context.getSystemService(Context.ROLE_SERVICE) as? RoleManager
                if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                    isDefault = roleManager.isRoleHeld(RoleManager.ROLE_HOME)
                }
            }
            if (!isDefault) {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                }
                val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
                val currentHomePackage = resolveInfo?.activityInfo?.packageName
                isDefault = currentHomePackage == context.packageName
            }
        } catch (_: Exception) {
        }
        _uiState.update {
            it.copy(isDefaultLauncher = isDefault)
        }
    }

    fun refreshSystemTelemetry() {
        val context = getApplication<Application>().applicationContext

        val drivesList = mutableListOf<StorageDriveInfo>()
        try {
            val rootStat = StatFs(Environment.getRootDirectory().path)
            drivesList.add(
                StorageDriveInfo(
                    driveLetter = "C:",
                    label = "Local Disk (C:)",
                    freeBytes = rootStat.availableBytes,
                    totalBytes = rootStat.totalBytes.coerceAtLeast(4L * 1024 * 1024 * 1024),
                    isSystemDrive = true
                )
            )
        } catch (_: Exception) {
            drivesList.add(
                StorageDriveInfo(
                    driveLetter = "C:",
                    label = "Local Disk (C:)",
                    freeBytes = 12L * 1024 * 1024 * 1024,
                    totalBytes = 32L * 1024 * 1024 * 1024,
                    isSystemDrive = true
                )
            )
        }

        try {
            val dataStat = StatFs(Environment.getDataDirectory().path)
            drivesList.add(
                StorageDriveInfo(
                    driveLetter = "D:",
                    label = "Android Storage (D:)",
                    freeBytes = dataStat.availableBytes,
                    totalBytes = dataStat.totalBytes.coerceAtLeast(16L * 1024 * 1024 * 1024),
                    isSystemDrive = false
                )
            )
        } catch (_: Exception) {
            drivesList.add(
                StorageDriveInfo(
                    driveLetter = "D:",
                    label = "Android Storage (D:)",
                    freeBytes = 42L * 1024 * 1024 * 1024,
                    totalBytes = 64L * 1024 * 1024 * 1024,
                    isSystemDrive = false
                )
            )
        }

        var batteryPct = 88
        var charging = true
        try {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
            if (bm != null) {
                val capacity = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                if (capacity in 1..100) batteryPct = capacity
                charging = bm.isCharging
            }
        } catch (_: Exception) {
        }

        var netConnected = true
        var netLabel = "HomeGroup Aero-5G"
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val caps = cm?.getNetworkCapabilities(cm.activeNetwork)
            if (caps != null) {
                netConnected = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                netLabel = when {
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wireless Network (Wi-Fi)"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Broadband Connection (5G)"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Local Area Connection"
                    else -> "HomeGroup Network"
                }
            }
        } catch (_: Exception) {
        }

        var volPercent = 75
        try {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            if (am != null) {
                val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
                val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
                volPercent = ((cur * 100f) / max).toInt().coerceIn(0, 100)
            }
        } catch (_: Exception) {
        }

        _uiState.update {
            it.copy(
                drives = drivesList,
                batteryPercent = batteryPct,
                isCharging = charging,
                isNetworkConnected = netConnected,
                networkName = netLabel,
                volumePercent = volPercent,
                isMuted = volPercent == 0
            )
        }
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingApps = true) }
            val context = getApplication<Application>().applicationContext
            val pm = context.packageManager
            val apps = withContext(Dispatchers.IO) {
                try {
                    val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                        addCategory(Intent.CATEGORY_LAUNCHER)
                    }
                    val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
                    resolveInfos
                        .asSequence()
                        .filter { it.activityInfo.packageName != context.packageName }
                        .map { resolveInfo ->
                            val pkg = resolveInfo.activityInfo.packageName
                            val actName = resolveInfo.activityInfo.name
                            val label = resolveInfo.loadLabel(pm)?.toString()?.takeIf { it.isNotBlank() } ?: pkg
                            val iconDrawable = try {
                                resolveInfo.loadIcon(pm)
                            } catch (_: Exception) {
                                null
                            }
                            val bitmap = iconDrawable?.let { drawableToImageBitmap(it) }
                            val pkgInfo = try {
                                pm.getPackageInfo(pkg, 0)
                            } catch (_: Exception) {
                                null
                            }
                            val isSys = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                            val category = categorizeApp(label, pkg, isSys)
                            InstalledAppInfo(
                                packageName = pkg,
                                activityName = actName,
                                label = label,
                                iconBitmap = bitmap,
                                categoryFolder = category,
                                versionName = pkgInfo?.versionName ?: "1.0",
                                installTimeMillis = pkgInfo?.firstInstallTime ?: 1256169600000L,
                                isSystemApp = isSys
                            )
                        }
                        .distinctBy { it.packageName }
                        .sortedBy { it.label.lowercase() }
                        .toList()
                } catch (_: Exception) {
                    emptyList()
                }
            }
            _uiState.update {
                it.copy(
                    installedApps = apps,
                    isLoadingApps = false
                )
            }
        }
    }

    private fun categorizeApp(label: String, pkg: String, isSystem: Boolean): String {
        val text = "${label.lowercase()} ${pkg.lowercase()}"
        return when {
            text.contains("chrome") || text.contains("browser") || text.contains("web") || text.contains("internet") ->
                "Internet & Network"
            text.contains("camera") || text.contains("photo") || text.contains("gallery") || text.contains("music") ||
                text.contains("video") || text.contains("youtube") || text.contains("media") ->
                "Media & Photos"
            text.contains("phone") || text.contains("message") || text.contains("sms") || text.contains("mail") ||
                text.contains("contacts") || text.contains("chat") ->
                "Communication"
            text.contains("game") || text.contains("play") ->
                "Games & Entertainment"
            isSystem || text.contains("settings") || text.contains("clock") || text.contains("calc") || text.contains("files") ->
                "System Tools"
            else -> "Installed Android Apps"
        }
    }

    private fun drawableToImageBitmap(drawable: Drawable): ImageBitmap? {
        return try {
            if (drawable is BitmapDrawable && drawable.bitmap != null) {
                return drawable.bitmap.asImageBitmap()
            }
            val width = drawable.intrinsicWidth.coerceIn(48, 144)
            val height = drawable.intrinsicHeight.coerceIn(48, 144)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap.asImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    fun getDesktopIcons(
        currentSettings: PersistedLauncherSettings,
        apps: List<InstalledAppInfo>,
        isDefaultLauncher: Boolean
    ): List<DesktopIconItem> {
        val baseIcons = mutableListOf(
            DesktopIconItem(
                id = "sys_computer",
                title = "Computer",
                iconType = Win7SystemIconType.COMPUTER,
                subtitle = "System Folder",
                sizeKb = 8192,
                modifiedTime = 1256169600000L
            ),
            DesktopIconItem(
                id = "sys_user_files",
                title = currentSettings.userName,
                iconType = Win7SystemIconType.USER_FILES,
                subtitle = "User's Files",
                sizeKb = 6144,
                modifiedTime = 1256256000000L
            ),
            DesktopIconItem(
                id = "sys_network",
                title = "Network",
                iconType = Win7SystemIconType.NETWORK,
                subtitle = "System Folder",
                sizeKb = 2048,
                modifiedTime = 1256342400000L
            ),
            DesktopIconItem(
                id = "sys_recycle_bin",
                title = "Recycle Bin",
                iconType = Win7SystemIconType.RECYCLE_BIN,
                subtitle = if (currentSettings.recycledItemIds.isEmpty()) "Empty" else "${currentSettings.recycledItemIds.size} items",
                sizeKb = currentSettings.recycledItemIds.size * 512,
                modifiedTime = 1256428800000L
            ),
            DesktopIconItem(
                id = "sys_control_panel",
                title = "Control Panel",
                iconType = Win7SystemIconType.CONTROL_PANEL,
                subtitle = "System Applet",
                sizeKb = 5120,
                modifiedTime = 1256515200000L
            ),
            DesktopIconItem(
                id = "sys_lock_screen",
                title = "Lock Screen",
                iconType = Win7SystemIconType.LOCK_SCREEN,
                subtitle = "Win + L Lock",
                sizeKb = 1280,
                modifiedTime = 1256550000000L
            ),
            DesktopIconItem(
                id = "sys_media_player",
                title = "Media Player",
                iconType = Win7SystemIconType.MEDIA_PLAYER,
                subtitle = "wmplayer.exe",
                sizeKb = 7168,
                modifiedTime = 1256610000000L
            ),
            DesktopIconItem(
                id = "sys_paint",
                title = "Paint",
                iconType = Win7SystemIconType.PAINT,
                subtitle = "mspaint.exe",
                sizeKb = 4608,
                modifiedTime = 1256640000000L
            ),
            DesktopIconItem(
                id = "sys_notepad",
                title = "Notepad",
                iconType = Win7SystemIconType.NOTEPAD,
                subtitle = "notepad.exe",
                sizeKb = 1024,
                modifiedTime = 1256660000000L
            ),
            DesktopIconItem(
                id = "sys_aero_tour",
                title = "Win7 Visual Tour",
                iconType = Win7SystemIconType.AERO_TOUR_GALLERY,
                subtitle = "Windows Photo Viewer",
                sizeKb = 9216,
                modifiedTime = 1256688000000L
            ),
            DesktopIconItem(
                id = "sys_cmd",
                title = "Command Prompt",
                iconType = Win7SystemIconType.COMMAND_PROMPT,
                subtitle = "cmd.exe",
                sizeKb = 3072,
                modifiedTime = 1256774400000L
            ),
            DesktopIconItem(
                id = "sys_calculator",
                title = "Calculator",
                iconType = Win7SystemIconType.CALCULATOR,
                subtitle = "calc.exe",
                sizeKb = 1536,
                modifiedTime = 1256860800000L
            )
        )

        // ONLY show "Set Default Launcher" if Aero7 is NOT already the default launcher!
        if (!isDefaultLauncher) {
            baseIcons.add(
                5,
                DesktopIconItem(
                    id = "sys_default_launcher",
                    title = "Set Default Launcher",
                    iconType = Win7SystemIconType.SET_DEFAULT_LAUNCHER,
                    subtitle = "Default Programs",
                    sizeKb = 1024,
                    modifiedTime = 1256601600000L
                )
            )
        }

        val shortcutApps = if (currentSettings.desktopShortcutPackages.isEmpty()) {
            apps.take(6)
        } else {
            apps.filter { it.packageName in currentSettings.desktopShortcutPackages }
        }

        shortcutApps.forEachIndexed { idx, app ->
            baseIcons.add(
                DesktopIconItem(
                    id = "app_${app.packageName}",
                    title = app.label,
                    iconType = Win7SystemIconType.ANDROID_APP,
                    packageName = app.packageName,
                    appIcon = app.iconBitmap,
                    subtitle = "Application Shortcut",
                    sizeKb = 10240 + idx * 512,
                    modifiedTime = app.installTimeMillis
                )
            )
        }

        val visible = baseIcons.filter { item ->
            item.id == "sys_recycle_bin" || item.id !in currentSettings.recycledItemIds
        }

        return when (currentSettings.sortMode) {
            DesktopSortMode.NAME -> visible.sortedBy { it.title.lowercase() }
            DesktopSortMode.SIZE -> visible.sortedByDescending { it.sizeKb }
            DesktopSortMode.ITEM_TYPE -> visible.sortedBy { it.subtitle }
            DesktopSortMode.DATE_MODIFIED -> visible.sortedByDescending { it.modifiedTime }
        }
    }

    fun onDesktopIconActivated(item: DesktopIconItem, context: Context) {
        playUiSound(ToneGenerator.TONE_PROP_BEEP)
        _uiState.update {
            it.copy(
                selectedDesktopIconId = item.id,
                isStartMenuOpen = false,
                activeTrayPopup = TrayPopupType.NONE,
                isContextMenuOpen = false,
                isFlip3dActive = false
            )
        }
        when (item.iconType) {
            Win7SystemIconType.COMPUTER -> openWindow(Win7WindowType.COMPUTER_EXPLORER, ExplorerPath.COMPUTER_ROOT)
            Win7SystemIconType.USER_FILES -> openWindow(Win7WindowType.DOCUMENTS_LIBRARY, ExplorerPath.LIBRARIES_DOCUMENTS)
            Win7SystemIconType.NETWORK -> openWindow(Win7WindowType.NETWORK_CENTER)
            Win7SystemIconType.RECYCLE_BIN -> openWindow(Win7WindowType.RECYCLE_BIN)
            Win7SystemIconType.CONTROL_PANEL -> openWindow(Win7WindowType.CONTROL_PANEL)
            Win7SystemIconType.AERO_TOUR_GALLERY -> openWindow(Win7WindowType.AERO_TOUR_GALLERY)
            Win7SystemIconType.COMMAND_PROMPT -> openWindow(Win7WindowType.COMMAND_PROMPT)
            Win7SystemIconType.CALCULATOR -> openWindow(Win7WindowType.CALCULATOR)
            Win7SystemIconType.PAINT -> openWindow(Win7WindowType.PAINT)
            Win7SystemIconType.NOTEPAD -> openWindow(Win7WindowType.NOTEPAD)
            Win7SystemIconType.MEDIA_PLAYER -> openWindow(Win7WindowType.MEDIA_PLAYER)
            Win7SystemIconType.USER_ACCOUNTS -> openWindow(Win7WindowType.USER_ACCOUNTS)
            Win7SystemIconType.LOCK_SCREEN -> triggerPowerAction(PowerActionState.LOCKED_SCREEN)
            Win7SystemIconType.STICKY_NOTES -> toggleGadget("stickynotes", true)
            Win7SystemIconType.GADGETS -> openWindow(Win7WindowType.GADGET_GALLERY)
            Win7SystemIconType.TASK_MANAGER -> openWindow(Win7WindowType.TASK_MANAGER)
            Win7SystemIconType.SET_DEFAULT_LAUNCHER -> promptSetDefaultLauncher(context)
            Win7SystemIconType.ANDROID_APP -> {
                item.packageName?.let { launchAndroidApp(it, context) }
            }
        }
    }

    fun launchAndroidApp(packageName: String, context: Context) {
        playUiSound(ToneGenerator.TONE_PROP_ACK)
        _uiState.update {
            it.copy(
                isStartMenuOpen = false,
                activeTrayPopup = TrayPopupType.NONE,
                isContextMenuOpen = false,
                isFlip3dActive = false
            )
        }
        try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
            } else {
                showStatusToast("Unable to launch $packageName")
            }
        } catch (e: Exception) {
            showStatusToast("Could not start program: ${e.localizedMessage ?: packageName}")
        }
    }

    fun openAndroidAppSettings(packageName: String, context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            showStatusToast("Could not open system App Info")
        }
    }

    fun promptSetDefaultLauncher(context: Context) {
        playUiSound(ToneGenerator.TONE_PROP_ACK)
        _uiState.update { it.copy(isStartMenuOpen = false, isContextMenuOpen = false) }
        try {
            val homeSettingsIntent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(homeSettingsIntent)
        } catch (_: Exception) {
            try {
                val mainSettings = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(mainSettings)
            } catch (_: Exception) {
                showStatusToast("Select 'Aero7 Launcher' in Android Home App Settings")
            }
        }
    }

    fun openWindow(
        type: Win7WindowType,
        explorerPath: ExplorerPath = ExplorerPath.COMPUTER_ROOT,
        targetApp: InstalledAppInfo? = null
    ) {
        playUiSound(ToneGenerator.TONE_PROP_BEEP)
        _uiState.update { state ->
            val maxZ = (state.openWindows.maxOfOrNull { it.zIndex } ?: 0) + 1
            val existing = state.openWindows.find { it.type == type }
            if (existing != null) {
                val updatedList = state.openWindows.map { win ->
                    if (win.id == existing.id) {
                        win.copy(
                            isMinimized = false,
                            zIndex = maxZ,
                            explorerPath = if (type == Win7WindowType.COMPUTER_EXPLORER || type == Win7WindowType.DOCUMENTS_LIBRARY) explorerPath else win.explorerPath,
                            title = if (type == Win7WindowType.COMPUTER_EXPLORER || type == Win7WindowType.DOCUMENTS_LIBRARY) explorerPath.title
                            else if (type == Win7WindowType.APP_PROPERTIES && targetApp != null) "${targetApp.label} Properties"
                            else win.title,
                            targetApp = targetApp ?: win.targetApp
                        )
                    } else win
                }
                state.copy(
                    openWindows = updatedList,
                    activeWindowId = existing.id,
                    isStartMenuOpen = false,
                    isContextMenuOpen = false,
                    activeTrayPopup = TrayPopupType.NONE,
                    isAeroPeekActive = false,
                    isFlip3dActive = false
                )
            } else {
                val cascade = (state.openWindows.size % 5) * 14f
                val windowTitle = when (type) {
                    Win7WindowType.COMPUTER_EXPLORER, Win7WindowType.DOCUMENTS_LIBRARY -> explorerPath.title
                    Win7WindowType.APP_PROPERTIES -> "${targetApp?.label ?: "Application"} Properties"
                    else -> type.defaultTitle
                }
                val newWin = Win7WindowInstance(
                    id = "win_${type.name}_${System.currentTimeMillis()}",
                    type = type,
                    title = windowTitle,
                    isMinimized = false,
                    isMaximized = false,
                    zIndex = maxZ,
                    offsetX = 12f + cascade,
                    offsetY = 16f + cascade,
                    explorerPath = explorerPath,
                    targetApp = targetApp
                )
                state.copy(
                    openWindows = state.openWindows + newWin,
                    activeWindowId = newWin.id,
                    isStartMenuOpen = false,
                    isContextMenuOpen = false,
                    activeTrayPopup = TrayPopupType.NONE,
                    isAeroPeekActive = false,
                    isFlip3dActive = false
                )
            }
        }
    }

    fun focusWindow(windowId: String) {
        _uiState.update { state ->
            val maxZ = (state.openWindows.maxOfOrNull { it.zIndex } ?: 0) + 1
            state.copy(
                openWindows = state.openWindows.map {
                    if (it.id == windowId) it.copy(zIndex = maxZ, isMinimized = false) else it
                },
                activeWindowId = windowId,
                isStartMenuOpen = false,
                isContextMenuOpen = false,
                activeTrayPopup = TrayPopupType.NONE,
                isAeroPeekActive = false,
                isFlip3dActive = false
            )
        }
    }

    fun toggleMinimizeWindow(windowId: String) {
        playUiSound(ToneGenerator.TONE_PROP_BEEP)
        _uiState.update { state ->
            val target = state.openWindows.find { it.id == windowId } ?: return@update state
            val maxZ = (state.openWindows.maxOfOrNull { it.zIndex } ?: 0) + 1
            val willMinimize = !target.isMinimized && state.activeWindowId == windowId
            val updated = state.openWindows.map {
                if (it.id == windowId) {
                    it.copy(
                        isMinimized = willMinimize,
                        zIndex = if (willMinimize) it.zIndex else maxZ
                    )
                } else it
            }
            val nextActive = updated.filter { !it.isMinimized }.maxByOrNull { it.zIndex }?.id
            state.copy(
                openWindows = updated,
                activeWindowId = nextActive,
                isStartMenuOpen = false,
                isFlip3dActive = false
            )
        }
    }

    fun toggleMaximizeWindow(windowId: String) {
        playUiSound(ToneGenerator.TONE_PROP_BEEP)
        _uiState.update { state ->
            val maxZ = (state.openWindows.maxOfOrNull { it.zIndex } ?: 0) + 1
            state.copy(
                openWindows = state.openWindows.map {
                    if (it.id == windowId) it.copy(isMaximized = !it.isMaximized, zIndex = maxZ) else it
                },
                activeWindowId = windowId
            )
        }
    }

    fun closeWindow(windowId: String) {
        playUiSound(ToneGenerator.TONE_PROP_BEEP)
        _uiState.update { state ->
            val remaining = state.openWindows.filterNot { it.id == windowId }
            val nextActive = remaining.filter { !it.isMinimized }.maxByOrNull { it.zIndex }?.id
            state.copy(
                openWindows = remaining,
                activeWindowId = nextActive
            )
        }
    }

    fun moveWindow(windowId: String, deltaX: Float, deltaY: Float) {
        _uiState.update { state ->
            state.copy(
                openWindows = state.openWindows.map { win ->
                    if (win.id == windowId && !win.isMaximized) {
                        win.copy(
                            offsetX = (win.offsetX + deltaX).coerceIn(-40f, 220f),
                            offsetY = (win.offsetY + deltaY).coerceIn(0f, 340f)
                        )
                    } else win
                }
            )
        }
    }

    fun navigateExplorer(windowId: String, path: ExplorerPath) {
        playUiSound(ToneGenerator.TONE_PROP_BEEP)
        _uiState.update { state ->
            state.copy(
                openWindows = state.openWindows.map { win ->
                    if (win.id == windowId) win.copy(explorerPath = path, title = path.title) else win
                }
            )
        }
    }

    fun setExplorerSearchQuery(query: String) {
        _uiState.update { it.copy(explorerSearchQuery = query) }
    }

    fun toggleStartMenu() {
        playUiSound(ToneGenerator.TONE_PROP_BEEP)
        _uiState.update {
            it.copy(
                isStartMenuOpen = !it.isStartMenuOpen,
                isAllProgramsInStart = false,
                startSearchQuery = "",
                isShutdownMenuExpanded = false,
                activeTrayPopup = TrayPopupType.NONE,
                isContextMenuOpen = false,
                isAeroPeekActive = false,
                isFlip3dActive = false
            )
        }
    }

    fun toggleFlip3d() {
        playUiSound(ToneGenerator.TONE_PROP_ACK)
        _uiState.update {
            it.copy(
                isFlip3dActive = !it.isFlip3dActive,
                isStartMenuOpen = false,
                isContextMenuOpen = false,
                activeTrayPopup = TrayPopupType.NONE,
                isAeroPeekActive = false
            )
        }
    }

    fun setAllProgramsInStart(showAll: Boolean) {
        playUiSound(ToneGenerator.TONE_PROP_BEEP)
        _uiState.update { it.copy(isAllProgramsInStart = showAll) }
    }

    fun updateStartSearchQuery(query: String) {
        _uiState.update {
            it.copy(
                startSearchQuery = query,
                isAllProgramsInStart = query.isNotBlank() || it.isAllProgramsInStart
            )
        }
    }

    fun toggleStartFolder(folderName: String) {
        _uiState.update { state ->
            val current = state.expandedStartFolders
            val updated = if (folderName in current) current - folderName else current + folderName
            state.copy(expandedStartFolders = updated)
        }
    }

    fun toggleShutdownSubmenu() {
        _uiState.update { it.copy(isShutdownMenuExpanded = !it.isShutdownMenuExpanded) }
    }

    fun triggerPowerAction(action: PowerActionState) {
        playUiSound(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD)
        _uiState.update {
            it.copy(
                powerState = action,
                isStartMenuOpen = false,
                isShutdownMenuExpanded = false,
                isFlip3dActive = false,
                isContextMenuOpen = false
            )
        }
    }

    fun toggleTrayPopup(popup: TrayPopupType) {
        playUiSound(ToneGenerator.TONE_PROP_BEEP)
        refreshSystemTelemetry()
        _uiState.update { state ->
            val next = if (state.activeTrayPopup == popup) TrayPopupType.NONE else popup
            state.copy(
                activeTrayPopup = next,
                isStartMenuOpen = false,
                isContextMenuOpen = false,
                isFlip3dActive = false
            )
        }
    }

    fun toggleShowDesktopOrAeroPeek(enableXRayPeekOnly: Boolean = false) {
        playUiSound(ToneGenerator.TONE_PROP_BEEP)
        _uiState.update { state ->
            if (enableXRayPeekOnly) {
                state.copy(
                    isAeroPeekActive = !state.isAeroPeekActive,
                    isStartMenuOpen = false,
                    activeTrayPopup = TrayPopupType.NONE,
                    isFlip3dActive = false
                )
            } else {
                val anyVisible = state.openWindows.any { !it.isMinimized }
                val updated = state.openWindows.map { it.copy(isMinimized = anyVisible) }
                state.copy(
                    openWindows = updated,
                    activeWindowId = if (anyVisible) null else updated.maxByOrNull { it.zIndex }?.id,
                    isAeroPeekActive = false,
                    isStartMenuOpen = false,
                    activeTrayPopup = TrayPopupType.NONE,
                    isContextMenuOpen = false,
                    isFlip3dActive = false
                )
            }
        }
    }

    fun setContextMenuOpen(open: Boolean) {
        if (open) playUiSound(ToneGenerator.TONE_PROP_BEEP)
        _uiState.update {
            it.copy(
                isContextMenuOpen = open,
                isStartMenuOpen = false,
                activeTrayPopup = TrayPopupType.NONE,
                isFlip3dActive = false
            )
        }
    }

    fun dismissAllOverlays() {
        _uiState.update {
            it.copy(
                isStartMenuOpen = false,
                isShutdownMenuExpanded = false,
                activeTrayPopup = TrayPopupType.NONE,
                isContextMenuOpen = false,
                isAeroPeekActive = false,
                isFlip3dActive = false,
                selectedDesktopIconId = null
            )
        }
    }

    fun refreshDesktop() {
        playUiSound(ToneGenerator.TONE_PROP_BEEP)
        viewModelScope.launch {
            _uiState.update { it.copy(isContextMenuOpen = false, isRefreshingDesktop = true) }
            refreshSystemTelemetry()
            loadInstalledApps()
            checkDefaultLauncherStatus()
            kotlinx.coroutines.delay(220)
            _uiState.update { it.copy(isRefreshingDesktop = false) }
        }
    }

    fun setSystemVolume(newPercent: Int) {
        val clamped = newPercent.coerceIn(0, 100)
        _uiState.update { it.copy(volumePercent = clamped, isMuted = clamped == 0) }
        try {
            val context = getApplication<Application>().applicationContext
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            if (am != null) {
                val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
                val targetVol = ((clamped / 100f) * max).toInt()
                am.setStreamVolume(AudioManager.STREAM_MUSIC, targetVol, 0)
            }
        } catch (_: Exception) {
        }
    }

    fun toggleMuteVolume() {
        val current = _uiState.value
        if (current.isMuted) {
            setSystemVolume(65)
        } else {
            setSystemVolume(0)
        }
    }

    fun selectPowerPlan(plan: String) {
        _uiState.update { it.copy(powerPlan = plan) }
    }

    // Personalization & Preferences mutations
    fun selectThemePreset(theme: Win7ThemePreset) {
        playUiSound(ToneGenerator.TONE_PROP_ACK)
        viewModelScope.launch {
            repository.setThemePreset(theme)
            showStatusToast("Applied theme: ${theme.displayName}")
        }
    }

    fun selectAeroSwatch(swatch: AeroGlassSwatch) {
        playUiSound(ToneGenerator.TONE_PROP_BEEP)
        viewModelScope.launch {
            repository.setAeroSwatch(swatch)
        }
    }

    fun setAeroTransparency(enabled: Boolean) {
        viewModelScope.launch {
            repository.setAeroTransparency(enabled)
        }
    }

    fun setGlassIntensity(intensity: Float) {
        viewModelScope.launch {
            repository.setGlassIntensity(intensity)
        }
    }

    fun setDesktopIconSize(size: DesktopIconSize) {
        viewModelScope.launch {
            repository.setDesktopIconSize(size)
            _uiState.update { it.copy(isContextMenuOpen = false) }
        }
    }

    fun setDesktopSortMode(mode: DesktopSortMode) {
        viewModelScope.launch {
            repository.setDesktopSortMode(mode)
            _uiState.update { it.copy(isContextMenuOpen = false) }
        }
    }

    fun setShowDesktopIcons(show: Boolean) {
        viewModelScope.launch {
            repository.setShowDesktopIcons(show)
            _uiState.update { it.copy(isContextMenuOpen = false) }
        }
    }

    fun moveDesktopIcon(itemId: String, xDp: Float, yDp: Float) {
        viewModelScope.launch {
            repository.saveIconPosition(itemId, xDp, yDp)
        }
    }

    fun toggleAutoArrangeIcons() {
        val next = !settings.value.autoArrangeIcons
        viewModelScope.launch {
            repository.setAutoArrangeIcons(next)
            _uiState.update { it.copy(isContextMenuOpen = false) }
            showStatusToast(
                if (next) "Auto arrange icons ON (Aligned in columns)"
                else "Auto arrange OFF — Drag any icon or app anywhere on screen!"
            )
        }
    }

    fun toggleAlignIconsToGrid() {
        val next = !settings.value.alignIconsToGrid
        viewModelScope.launch {
            repository.setAlignIconsToGrid(next)
            _uiState.update { it.copy(isContextMenuOpen = false) }
            showStatusToast(
                if (next) "Align icons to grid ON"
                else "Free placement ON — Move apps to any exact spot!"
            )
        }
    }

    fun resetDesktopIconPositions() {
        playUiSound(ToneGenerator.TONE_PROP_BEEP)
        viewModelScope.launch {
            repository.resetAllIconPositions()
            _uiState.update { it.copy(isContextMenuOpen = false) }
            showStatusToast("Reset all desktop icons to default columns")
        }
    }

    fun addAllInstalledAppsToDesktop() {
        playUiSound(ToneGenerator.TONE_PROP_ACK)
        viewModelScope.launch {
            val allPackages = _uiState.value.installedApps.map { it.packageName }.toSet()
            repository.setDesktopShortcuts(allPackages)
            _uiState.update { it.copy(isContextMenuOpen = false) }
            showStatusToast("Added ${allPackages.size} installed apps to Desktop!")
        }
    }

    fun importCustomWallpaperUri(uri: Uri, context: Context) {
        playUiSound(ToneGenerator.TONE_PROP_ACK)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val input = context.contentResolver.openInputStream(uri)
                if (input != null) {
                    val outFile = File(context.filesDir, "custom_win7_wallpaper.jpg")
                    FileOutputStream(outFile).use { output ->
                        input.copyTo(output)
                    }
                    input.close()
                    repository.setCustomWallpaperPath(outFile.absolutePath)
                    showStatusToast("Applied custom desktop wallpaper")
                }
            } catch (e: Exception) {
                showStatusToast("Could not load wallpaper image: ${e.localizedMessage ?: "Error"}")
            }
        }
    }

    fun clearCustomWallpaper() {
        viewModelScope.launch {
            repository.setCustomWallpaperPath("")
            showStatusToast("Restored Windows 7 theme wallpaper")
        }
    }

    fun setWallpaperFitMode(mode: WallpaperFitMode) {
        viewModelScope.launch {
            repository.setWallpaperFitMode(mode)
        }
    }

    fun setSmallTaskbarIcons(small: Boolean) {
        viewModelScope.launch {
            repository.setSmallTaskbarIcons(small)
        }
    }

    fun setShowFlip3dOnTaskbar(show: Boolean) {
        viewModelScope.launch {
            repository.setShowFlip3dOnTaskbar(show)
        }
    }

    fun setHighContrastMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setHighContrastMode(enabled)
        }
    }

    fun saveNotepadText(text: String) {
        viewModelScope.launch {
            repository.saveNotepadContent(text)
        }
    }

    fun toggleGadget(gadgetKey: String, enabled: Boolean) {
        playUiSound(ToneGenerator.TONE_PROP_BEEP)
        viewModelScope.launch {
            repository.setGadgetEnabled(gadgetKey, enabled)
        }
    }

    fun addStickyNote() {
        playUiSound(ToneGenerator.TONE_PROP_BEEP)
        viewModelScope.launch {
            val current = settings.value.stickyNotes
            val nextColor = (current.size) % 4
            val newNote = StickyNoteItem(
                id = "note_${System.currentTimeMillis()}",
                text = "New Windows 7 Sticky Note...",
                colorIndex = nextColor
            )
            repository.saveStickyNotes(current + newNote)
            repository.setGadgetEnabled("stickynotes", true)
            _uiState.update { it.copy(isContextMenuOpen = false) }
        }
    }

    fun updateStickyNoteText(noteId: String, newText: String) {
        viewModelScope.launch {
            val updated = settings.value.stickyNotes.map {
                if (it.id == noteId) it.copy(text = newText) else it
            }
            repository.saveStickyNotes(updated)
        }
    }

    fun cycleStickyNoteColor(noteId: String) {
        viewModelScope.launch {
            val updated = settings.value.stickyNotes.map {
                if (it.id == noteId) it.copy(colorIndex = (it.colorIndex + 1) % 4) else it
            }
            repository.saveStickyNotes(updated)
        }
    }

    fun deleteStickyNote(noteId: String) {
        viewModelScope.launch {
            val updated = settings.value.stickyNotes.filterNot { it.id == noteId }
            repository.saveStickyNotes(updated)
        }
    }

    fun updateUserProfile(name: String, avatar: Win7AvatarPreset) {
        viewModelScope.launch {
            repository.setUserProfile(name, avatar)
            showStatusToast("Switched user account: $name")
        }
    }

    fun updateLockScreenConfig(pin: String, lockOnStartup: Boolean) {
        viewModelScope.launch {
            repository.setLockScreenConfig(pin, lockOnStartup)
            showStatusToast("Saved Windows 7 Lock Screen security settings")
        }
    }

    fun setSoundEffectsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setSoundEffects(enabled)
        }
    }

    fun playSampleNote() {
        playUiSound(ToneGenerator.TONE_PROP_ACK)
    }

    fun togglePinAppToTaskbar(packageName: String, label: String) {
        viewModelScope.launch {
            val pinned = packageName in settings.value.pinnedTaskbarPackages
            repository.togglePinnedTaskbarApp(packageName)
            showStatusToast(if (pinned) "Unpinned $label from Taskbar" else "Pinned $label to Taskbar")
        }
    }

    fun toggleAppDesktopShortcut(packageName: String, label: String) {
        viewModelScope.launch {
            val exists = packageName in settings.value.desktopShortcutPackages
            repository.toggleDesktopShortcut(packageName)
            showStatusToast(if (exists) "Removed $label shortcut from Desktop" else "Sent $label shortcut to Desktop")
        }
    }

    fun sendDesktopItemToRecycleBin(itemId: String, title: String) {
        if (itemId == "sys_recycle_bin") return
        viewModelScope.launch {
            repository.moveItemToRecycleBin(itemId)
            showStatusToast("Moved '$title' to Recycle Bin")
        }
    }

    fun restoreRecycleBinItem(itemId: String) {
        viewModelScope.launch {
            repository.restoreFromRecycleBin(itemId)
            showStatusToast("Restored item from Recycle Bin")
        }
    }

    fun emptyOrRestoreAllRecycleBin() {
        viewModelScope.launch {
            repository.restoreAllRecycleBin()
            showStatusToast("Recycle Bin items restored to Desktop")
        }
    }

    fun selectTourSlide(index: Int) {
        val safeIdx = (index + screenshotTourSlides.size) % screenshotTourSlides.size
        _uiState.update { it.copy(activeTourSlideIndex = safeIdx) }
    }

    fun applyTourSlidePreset(slide: Win7ScreenshotSlide) {
        playUiSound(ToneGenerator.TONE_PROP_ACK)
        viewModelScope.launch {
            repository.setThemePreset(slide.presetTheme)
            repository.setAeroSwatch(slide.presetSwatch)
            if (slide.id == "slide_gadgets") {
                repository.setGadgetEnabled("clock", true)
                repository.setGadgetEnabled("cpu", true)
                repository.setGadgetEnabled("calendar", true)
            }
            if (slide.id == "slide_aeropeek") {
                _uiState.update { it.copy(isAeroPeekActive = true) }
            } else if (slide.id == "slide_lockscreen") {
                _uiState.update { it.copy(powerState = PowerActionState.LOCKED_SCREEN) }
            }
            showStatusToast("Applied look: ${slide.title}")
        }
    }

    fun executeCommandPrompt(rawInput: String, context: Context) {
        val cmd = rawInput.trim()
        val lower = cmd.lowercase()
        val outputLines = mutableListOf<String>()
        outputLines.add("C:\\Users\\${settings.value.userName}>$cmd")

        when {
            lower == "help" -> {
                outputLines.add("Available Windows 7 Aero Launcher Commands:")
                outputLines.add("  VER          Displays the Windows 7 build version.")
                outputLines.add("  DIR          Displays a list of files and subdirectories.")
                outputLines.add("  APPS         Lists all installed Android applications.")
                outputLines.add("  START <name> Launches an installed Android app by name.")
                outputLines.add("  LOCK         Locks the computer (opens Windows 7 Lock Screen).")
                outputLines.add("  MSP          Opens Windows 7 Paint (mspaint.exe).")
                outputLines.add("  WMP          Opens Windows Media Player 12.")
                outputLines.add("  SYSTEMINFO   Displays device & storage configuration.")
                outputLines.add("  IPCONFIG     Displays Windows IP network configuration.")
                outputLines.add("  TASKLIST     Displays currently open Aero windows.")
                outputLines.add("  CLS          Clears the Command Prompt screen.")
            }
            lower == "cls" || lower == "clear" -> {
                _uiState.update { it.copy(cmdHistory = emptyList()) }
                return
            }
            lower == "lock" -> {
                outputLines.add("Locking workstation...")
                triggerPowerAction(PowerActionState.LOCKED_SCREEN)
            }
            lower == "msp" || lower == "mspaint" -> {
                outputLines.add("Launching Paint (mspaint.exe)...")
                openWindow(Win7WindowType.PAINT)
            }
            lower == "wmp" || lower == "wmplayer" -> {
                outputLines.add("Launching Windows Media Player 12...")
                openWindow(Win7WindowType.MEDIA_PLAYER)
            }
            lower == "ver" -> {
                outputLines.add("Microsoft Windows [Version 6.1.7601 Service Pack 1 - Aero7 Android Edition]")
            }
            lower == "dir" -> {
                outputLines.add(" Volume in drive C is Local Disk")
                outputLines.add(" Volume Serial Number is 7A09-2009")
                outputLines.add(" Directory of C:\\Users\\${settings.value.userName}")
                outputLines.add("10/25/2026  09:00 AM    <DIR>          Desktop")
                outputLines.add("10/25/2026  09:00 AM    <DIR>          Documents")
                outputLines.add("10/25/2026  09:00 AM    <DIR>          Pictures")
                outputLines.add("10/25/2026  09:00 AM    <DIR>          InstalledApps (${_uiState.value.installedApps.size})")
            }
            lower == "apps" -> {
                val apps = _uiState.value.installedApps
                outputLines.add("Installed Android Programs (${apps.size} total):")
                apps.take(15).forEach { app ->
                    outputLines.add("  - ${app.label.padEnd(22)} [${app.packageName}]")
                }
                if (apps.size > 15) {
                    outputLines.add("  ...and ${apps.size - 15} more programs.")
                }
            }
            lower.startsWith("start ") -> {
                val targetName = cmd.substringAfter(" ").trim().lowercase()
                val match = _uiState.value.installedApps.find {
                    it.label.lowercase().contains(targetName) || it.packageName.lowercase().contains(targetName)
                }
                if (match != null) {
                    outputLines.add("Starting ${match.label} (${match.packageName})...")
                    launchAndroidApp(match.packageName, context)
                } else {
                    outputLines.add("The system cannot find the program '$targetName'.")
                }
            }
            lower == "systeminfo" -> {
                val cDrive = _uiState.value.drives.firstOrNull()
                outputLines.add("Host Name:                 WIN7-AERO-PC")
                outputLines.add("OS Name:                   Microsoft Windows 7 Ultimate (Aero Launcher)")
                outputLines.add("OS Version:                6.1.7601 Service Pack 1 Build 7601")
                outputLines.add("Registered Owner:          ${settings.value.userName}")
                outputLines.add("Battery Status:            ${_uiState.value.batteryPercent}% (${if (_uiState.value.isCharging) "Plugged in" else "On battery"})")
                if (cDrive != null) {
                    val freeGb = String.format("%.1f GB", cDrive.freeBytes / (1024f * 1024f * 1024f))
                    outputLines.add("Available Disk Space:      $freeGb")
                }
            }
            lower == "ipconfig" -> {
                outputLines.add("Windows IP Configuration")
                outputLines.add("Wireless LAN adapter ${_uiState.value.networkName}:")
                outputLines.add("   Connection-specific DNS Suffix  . : home.local")
                outputLines.add("   IPv4 Address. . . . . . . . . . . : 192.168.1.107")
                outputLines.add("   Subnet Mask . . . . . . . . . . . : 255.255.255.0")
                outputLines.add("   Default Gateway . . . . . . . . . : 192.168.1.1")
            }
            lower == "tasklist" -> {
                outputLines.add("Image Name                     PID Session Name")
                outputLines.add("========================= ======== ================")
                outputLines.add("explorer.exe                  1420 Console")
                outputLines.add("dwm.exe                        884 Console (Aero Glass)")
                _uiState.value.openWindows.forEachIndexed { index, win ->
                    outputLines.add("${win.type.name.lowercase().take(24).padEnd(25)} ${2100 + index * 44} Console")
                }
            }
            cmd.isBlank() -> {}
            else -> {
                outputLines.add("'$cmd' is not recognized as an internal or external command.")
                outputLines.add("Type 'help' for a list of supported Windows 7 commands.")
            }
        }
        outputLines.add("")
        _uiState.update { state ->
            state.copy(cmdHistory = (state.cmdHistory + outputLines).takeLast(60))
        }
    }

    fun showStatusToast(message: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(statusToastMessage = message) }
            kotlinx.coroutines.delay(2400)
            _uiState.update {
                if (it.statusToastMessage == message) it.copy(statusToastMessage = null) else it
            }
        }
    }

    private fun playUiSound(toneType: Int) {
        if (!settings.value.soundEffectsEnabled) return
        try {
            val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 25)
            tg.startTone(toneType, 35)
        } catch (_: Throwable) {
        }
    }
}
