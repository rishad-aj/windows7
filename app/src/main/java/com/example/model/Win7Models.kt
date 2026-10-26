package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

data class InstalledAppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val iconBitmap: ImageBitmap? = null,
    val categoryFolder: String = "Installed Programs",
    val versionName: String = "1.0",
    val installTimeMillis: Long = 0L,
    val isSystemApp: Boolean = false
)

enum class Win7SystemIconType {
    COMPUTER,
    USER_FILES,
    NETWORK,
    RECYCLE_BIN,
    CONTROL_PANEL,
    AERO_TOUR_GALLERY,
    COMMAND_PROMPT,
    CALCULATOR,
    STICKY_NOTES,
    GADGETS,
    SET_DEFAULT_LAUNCHER,
    TASK_MANAGER,
    PAINT,
    NOTEPAD,
    MEDIA_PLAYER,
    USER_ACCOUNTS,
    LOCK_SCREEN,
    ANDROID_APP
}

data class DesktopIconItem(
    val id: String,
    val title: String,
    val iconType: Win7SystemIconType,
    val packageName: String? = null,
    val appIcon: ImageBitmap? = null,
    val subtitle: String = "System Folder",
    val sizeKb: Int = 4096,
    val modifiedTime: Long = 1256169600000L // Oct 22, 2009 Windows 7 release date
)

data class IconPosition(
    val xDp: Float,
    val yDp: Float
)

enum class WallpaperFitMode(val id: String, val label: String) {
    FILL("fill", "Fill"),
    FIT("fit", "Fit"),
    STRETCH("stretch", "Stretch"),
    CENTER("center", "Center")
}

enum class DesktopIconSize(val label: String, val iconDp: Int, val cellWidthDp: Int, val cellHeightDp: Int) {
    LARGE("Large icons", 54, 96, 102),
    MEDIUM("Medium icons", 42, 82, 88),
    SMALL("Small icons", 32, 70, 76)
}

enum class DesktopSortMode(val label: String) {
    NAME("Name"),
    SIZE("Size"),
    ITEM_TYPE("Item type"),
    DATE_MODIFIED("Date modified")
}

enum class Win7WindowType(val defaultTitle: String, val iconType: Win7SystemIconType) {
    COMPUTER_EXPLORER("Computer", Win7SystemIconType.COMPUTER),
    DOCUMENTS_LIBRARY("Libraries\\Documents", Win7SystemIconType.USER_FILES),
    CONTROL_PANEL("All Control Panel Items", Win7SystemIconType.CONTROL_PANEL),
    PERSONALIZATION("Personalization", Win7SystemIconType.CONTROL_PANEL),
    USER_ACCOUNTS("User Accounts and Lock Screen", Win7SystemIconType.USER_ACCOUNTS),
    RECYCLE_BIN("Recycle Bin", Win7SystemIconType.RECYCLE_BIN),
    COMMAND_PROMPT("Administrator: C:\\Windows\\System32\\cmd.exe", Win7SystemIconType.COMMAND_PROMPT),
    CALCULATOR("Calculator", Win7SystemIconType.CALCULATOR),
    PAINT("Untitled - Paint", Win7SystemIconType.PAINT),
    NOTEPAD("Welcome.txt - Notepad", Win7SystemIconType.NOTEPAD),
    MEDIA_PLAYER("Windows Media Player", Win7SystemIconType.MEDIA_PLAYER),
    AERO_TOUR_GALLERY("Windows Photo Viewer - Windows 7 Reference Screenshots", Win7SystemIconType.AERO_TOUR_GALLERY),
    GADGET_GALLERY("Desktop Gadget Gallery", Win7SystemIconType.GADGETS),
    TASK_MANAGER("Windows Task Manager", Win7SystemIconType.TASK_MANAGER),
    NETWORK_CENTER("Network and Sharing Center", Win7SystemIconType.NETWORK),
    APP_PROPERTIES("Application Properties", Win7SystemIconType.COMPUTER)
}

enum class ExplorerPath(val breadcrumb: List<String>, val title: String) {
    COMPUTER_ROOT(listOf("Computer"), "Computer"),
    LOCAL_DISK_C(listOf("Computer", "Local Disk (C:)"), "Local Disk (C:) - Android System"),
    USER_STORAGE_D(listOf("Computer", "Android Storage (D:)"), "Android Storage (D:) - Installed Apps"),
    LIBRARIES_DOCUMENTS(listOf("Libraries", "Documents"), "Documents Library"),
    LIBRARIES_PICTURES(listOf("Libraries", "Pictures"), "Pictures Library"),
    LIBRARIES_MUSIC(listOf("Libraries", "Music"), "Music Library"),
    NETWORK_PLACES(listOf("Network"), "Network")
}

data class Win7WindowInstance(
    val id: String,
    val type: Win7WindowType,
    val title: String,
    val isMinimized: Boolean = false,
    val isMaximized: Boolean = false,
    val zIndex: Int = 1,
    val offsetX: Float = 14f,
    val offsetY: Float = 20f,
    val explorerPath: ExplorerPath = ExplorerPath.COMPUTER_ROOT,
    val targetApp: InstalledAppInfo? = null
)

enum class AeroGlassSwatch(
    val id: String,
    val displayName: String,
    val glassTint: Color,
    val activeGlow: Color,
    val taskbarTint: Color
) {
    SKY("sky", "Sky", Color(0xFF3E95F6), Color(0xFF80D8FF), Color(0xCC0D2748)),
    TWILIGHT("twilight", "Twilight", Color(0xFF19457A), Color(0xFF5393DF), Color(0xD908182C)),
    FROST("frost", "Frost", Color(0xFF9AC5EB), Color(0xFFD6ECFF), Color(0xCC1E3A54)),
    SEAFOAM("seafoam", "Seafoam", Color(0xFF1E9C99), Color(0xFF72EAE6), Color(0xCC0A2E2E)),
    EMERALD("emerald", "Leaf", Color(0xFF2E8B44), Color(0xFF7CE894), Color(0xCC0D2B16)),
    AMBER("amber", "Sun", Color(0xFFD48817), Color(0xFFFFD166), Color(0xCC332005)),
    ORANGE("orange", "Pumpkin", Color(0xFFD84315), Color(0xFFFFAB91), Color(0xCC311106)),
    RUBY("ruby", "Ruby", Color(0xFFB52828), Color(0xFFFF8585), Color(0xCC2E0A0A)),
    FUCHSIA("fuchsia", "Magenta", Color(0xFFAD1457), Color(0xFFF48FB1), Color(0xCC2C0718)),
    VIOLET("violet", "Violet", Color(0xFF6E479B), Color(0xFFC29FFA), Color(0xCC1F1230)),
    GRAPHITE("graphite", "Slate", Color(0xFF455561), Color(0xFF9FB1BC), Color(0xD9161D24)),
    OBSIDIAN("obsidian", "Obsidian", Color(0xFF1E242B), Color(0xFF78909C), Color(0xE60B0E12))
}

enum class Win7ThemePreset(
    val id: String,
    val displayName: String,
    val subtitle: String,
    val winPath: String,
    val defaultSwatch: AeroGlassSwatch
) {
    HARMONY(
        "harmony",
        "Windows 7 Harmony (img0.jpg)",
        "Official 4-color Windows 7 flag, light ribbons & flora",
        "C:\\Windows\\Web\\Wallpaper\\Windows\\img0.jpg",
        AeroGlassSwatch.SKY
    ),
    LOGON_SWALLOW(
        "logon",
        "Windows 7 Logon (authui.exe)",
        "Official azure logon swallow bird, white vines & bokeh",
        "C:\\Windows\\System32\\oobe\\info\\backgrounds\\backgroundDefault.jpg",
        AeroGlassSwatch.TWILIGHT
    ),
    BETTA_FISH(
        "betta",
        "Siamese Betta Fish (Beta)",
        "Official Windows 7 Beta flowing white & aqua betta fish",
        "C:\\Windows\\Web\\Wallpaper\\Windows\\Betta.jpg",
        AeroGlassSwatch.SEAFOAM
    ),
    LANDSCAPES(
        "landscapes",
        "Landscapes (img7.jpg)",
        "Photographic emerald valley, alpine peaks & cumulus sky",
        "C:\\Windows\\Web\\Wallpaper\\Landscapes\\img7.jpg",
        AeroGlassSwatch.EMERALD
    ),
    NATURE(
        "nature",
        "Nature — Lotus Dewdrops (img1.jpg)",
        "Macro emerald lotus leaf veins & refractive water drops",
        "C:\\Windows\\Web\\Wallpaper\\Nature\\img1.jpg",
        AeroGlassSwatch.EMERALD
    ),
    SCENES(
        "scenes",
        "Scenes — Alpine Sunset (img25.jpg)",
        "Golden sunset horizon & shimmering lake water ripples",
        "C:\\Windows\\Web\\Wallpaper\\Scenes\\img25.jpg",
        AeroGlassSwatch.AMBER
    ),
    AURORA(
        "aurora",
        "Windows Aurora (Aurora.jpg)",
        "Luminous emerald, cyan & sapphire northern light curtains",
        "C:\\Windows\\Web\\Wallpaper\\Windows\\Aurora.jpg",
        AeroGlassSwatch.TWILIGHT
    ),
    ARCHITECTURE(
        "architecture",
        "Architecture — Glass Dome (img13.jpg)",
        "Steel-and-glass diagrid skyscraper atrium & sky reflection",
        "C:\\Windows\\Web\\Wallpaper\\Architecture\\img13.jpg",
        AeroGlassSwatch.FROST
    ),
    CHARACTERS(
        "characters",
        "Characters — Aero World (img19.jpg)",
        "Official Frutiger Aero floating islands, rainbows & 3D orbs",
        "C:\\Windows\\Web\\Wallpaper\\Characters\\img19.jpg",
        AeroGlassSwatch.VIOLET
    ),
    SAKURA_SPRING(
        "sakura",
        "Sakura Blossoms (img3.jpg)",
        "Detailed cherry blossom branch, petals & spring bokeh",
        "C:\\Windows\\Web\\Wallpaper\\Nature\\img3.jpg",
        AeroGlassSwatch.FUCHSIA
    ),
    BLACK_CARBON(
        "carbon",
        "Windows 7 Ultimate Dark",
        "Dark carbon weave with glowing 3D Windows 7 flag",
        "C:\\Windows\\Web\\Wallpaper\\Windows\\img0_ultimate.jpg",
        AeroGlassSwatch.OBSIDIAN
    ),
    CLASSIC_TEAL(
        "classic",
        "Windows Classic Teal",
        "Authentic Windows Classic #008080 desktop background",
        "Control Panel\\Appearance\\Classic #008080",
        AeroGlassSwatch.GRAPHITE
    )
}

enum class Win7AvatarPreset(val id: String, val label: String) {
    SUNFLOWER("sunflower", "Orange Sunflower"),
    ASTRONAUT("astronaut", "Space Explorer"),
    CHESS("chess", "Crystal Chess"),
    SOCCER("soccer", "Soccer Match"),
    BUTTERFLY("butterfly", "Monarch Butterfly")
}

data class StickyNoteItem(
    val id: String,
    val text: String,
    val colorIndex: Int = 0
)

data class StorageDriveInfo(
    val driveLetter: String,
    val label: String,
    val freeBytes: Long,
    val totalBytes: Long,
    val isSystemDrive: Boolean
) {
    val usedBytes: Long get() = (totalBytes - freeBytes).coerceAtLeast(0L)
    val usageRatio: Float
        get() = if (totalBytes > 0) (usedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0.05f, 0.98f) else 0.35f
}

data class Win7ScreenshotSlide(
    val id: String,
    val title: String,
    val caption: String,
    val highlights: List<String>,
    val presetTheme: Win7ThemePreset,
    val presetSwatch: AeroGlassSwatch,
    val windowToOpen: Win7WindowType? = null
)

data class PaintStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

data class MediaTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationSec: Int,
    val accentColor: Color
)
