package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.AeroGlassSwatch
import com.example.model.DesktopIconSize
import com.example.model.DesktopSortMode
import com.example.model.IconPosition
import com.example.model.StickyNoteItem
import com.example.model.WallpaperFitMode
import com.example.model.Win7AvatarPreset
import com.example.model.Win7ThemePreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.launcherDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "aero7_launcher_prefs"
)

data class PersistedLauncherSettings(
    val themePreset: Win7ThemePreset = Win7ThemePreset.HARMONY,
    val customWallpaperPath: String = "",
    val wallpaperFitMode: WallpaperFitMode = WallpaperFitMode.FILL,
    val aeroSwatch: AeroGlassSwatch = AeroGlassSwatch.SKY,
    val aeroTransparency: Boolean = true,
    val glassIntensity: Float = 0.78f,
    val iconSize: DesktopIconSize = DesktopIconSize.MEDIUM,
    val sortMode: DesktopSortMode = DesktopSortMode.NAME,
    val showDesktopIcons: Boolean = true,
    val autoArrangeIcons: Boolean = false,
    val alignIconsToGrid: Boolean = false,
    val customIconPositions: Map<String, IconPosition> = emptyMap(),
    val smallTaskbarIcons: Boolean = false,
    val showFlip3dOnTaskbar: Boolean = true,
    val showClockGadget: Boolean = true,
    val showCpuMeterGadget: Boolean = true,
    val showCalendarGadget: Boolean = false,
    val showWeatherGadget: Boolean = false,
    val showStickyNotesOnDesktop: Boolean = true,
    val stickyNotes: List<StickyNoteItem> = listOf(
        StickyNoteItem(
            id = "note_1",
            text = "Welcome to Windows 7 Ultimate!\n• Drag any desktop icon or app anywhere on screen\n• Original Win7 wallpapers in Personalize",
            colorIndex = 0
        )
    ),
    val userName: String = "Administrator",
    val avatarPreset: Win7AvatarPreset = Win7AvatarPreset.SUNFLOWER,
    val lockScreenPin: String = "",
    val requireLockOnStartup: Boolean = false,
    val highContrastMode: Boolean = false,
    val notepadContent: String = "Microsoft Windows 7 Ultimate (Build 7601: Service Pack 1)\r\n\r\nQuick Tips:\r\n- Drag and move any app or system icon anywhere on the Desktop!\r\n- Right-click the Desktop to toggle Auto Arrange, Align to Grid, or add more Android apps to Desktop.\r\n- Use Personalization to switch between authentic Windows 7 wallpapers (img0.jpg Harmony, Betta Fish, Bliss, Nature, Scenes, Architecture) or browse your own wallpaper photo!",
    val soundEffectsEnabled: Boolean = true,
    val desktopShortcutPackages: Set<String> = emptySet(),
    val pinnedTaskbarPackages: Set<String> = emptySet(),
    val recycledItemIds: Set<String> = emptySet()
)

class LauncherPreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_PRESET = stringPreferencesKey("theme_preset")
        val CUSTOM_WALLPAPER_PATH = stringPreferencesKey("custom_wallpaper_path")
        val WALLPAPER_FIT_MODE = stringPreferencesKey("wallpaper_fit_mode")
        val AERO_SWATCH = stringPreferencesKey("aero_swatch")
        val AERO_TRANSPARENCY = booleanPreferencesKey("aero_transparency")
        val GLASS_INTENSITY = floatPreferencesKey("glass_intensity")
        val ICON_SIZE = stringPreferencesKey("icon_size")
        val SORT_MODE = stringPreferencesKey("sort_mode")
        val SHOW_DESKTOP_ICONS = booleanPreferencesKey("show_desktop_icons")
        val AUTO_ARRANGE_ICONS = booleanPreferencesKey("auto_arrange_icons")
        val ALIGN_ICONS_TO_GRID = booleanPreferencesKey("align_icons_to_grid")
        val ICON_POSITIONS_RAW = stringPreferencesKey("icon_positions_raw")
        val SMALL_TASKBAR_ICONS = booleanPreferencesKey("small_taskbar_icons")
        val SHOW_FLIP_3D = booleanPreferencesKey("show_flip_3d")
        val SHOW_CLOCK_GADGET = booleanPreferencesKey("show_clock_gadget")
        val SHOW_CPU_GADGET = booleanPreferencesKey("show_cpu_gadget")
        val SHOW_CALENDAR_GADGET = booleanPreferencesKey("show_calendar_gadget")
        val SHOW_WEATHER_GADGET = booleanPreferencesKey("show_weather_gadget")
        val SHOW_STICKY_NOTES = booleanPreferencesKey("show_sticky_notes")
        val STICKY_NOTES_RAW = stringPreferencesKey("sticky_notes_raw")
        val USER_NAME = stringPreferencesKey("user_name")
        val AVATAR_PRESET = stringPreferencesKey("avatar_preset")
        val LOCK_PIN = stringPreferencesKey("lock_pin")
        val LOCK_ON_STARTUP = booleanPreferencesKey("lock_on_startup")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        val NOTEPAD_CONTENT = stringPreferencesKey("notepad_content")
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val DESKTOP_PACKAGES = stringSetPreferencesKey("desktop_packages")
        val PINNED_PACKAGES = stringSetPreferencesKey("pinned_packages")
        val RECYCLED_IDS = stringSetPreferencesKey("recycled_ids")
    }

    val settingsFlow: Flow<PersistedLauncherSettings> = context.launcherDataStore.data.map { prefs ->
        val themeId = prefs[Keys.THEME_PRESET]
        val theme = Win7ThemePreset.entries.find { it.id == themeId } ?: Win7ThemePreset.HARMONY
        val fitId = prefs[Keys.WALLPAPER_FIT_MODE]
        val fitMode = WallpaperFitMode.entries.find { it.id == fitId } ?: WallpaperFitMode.FILL
        val swatchId = prefs[Keys.AERO_SWATCH]
        val swatch = AeroGlassSwatch.entries.find { it.id == swatchId } ?: AeroGlassSwatch.SKY
        val sizeName = prefs[Keys.ICON_SIZE]
        val iconSize = DesktopIconSize.entries.find { it.name == sizeName } ?: DesktopIconSize.MEDIUM
        val sortName = prefs[Keys.SORT_MODE]
        val sortMode = DesktopSortMode.entries.find { it.name == sortName } ?: DesktopSortMode.NAME
        val avatarId = prefs[Keys.AVATAR_PRESET]
        val avatar = Win7AvatarPreset.entries.find { it.id == avatarId } ?: Win7AvatarPreset.SUNFLOWER

        val rawPositions = prefs[Keys.ICON_POSITIONS_RAW] ?: ""
        val parsedPositions = mutableMapOf<String, IconPosition>()
        if (rawPositions.isNotBlank()) {
            rawPositions.split("|||").forEach { token ->
                val parts = token.split(":::")
                if (parts.size == 3) {
                    val id = parts[0]
                    val x = parts[1].toFloatOrNull()
                    val y = parts[2].toFloatOrNull()
                    if (id.isNotBlank() && x != null && y != null) {
                        parsedPositions[id] = IconPosition(xDp = x, yDp = y)
                    }
                }
            }
        }

        val rawNotes = prefs[Keys.STICKY_NOTES_RAW]
        val notes = if (rawNotes == null) {
            listOf(
                StickyNoteItem(
                    id = "note_1",
                    text = "Welcome to Windows 7 Ultimate!\n• Drag any desktop icon or app anywhere on screen\n• Original Win7 wallpapers in Personalize",
                    colorIndex = 0
                )
            )
        } else if (rawNotes.isBlank()) {
            emptyList()
        } else {
            rawNotes.split("|||").mapNotNull { entry ->
                val parts = entry.split(":::")
                if (parts.size >= 3) {
                    StickyNoteItem(
                        id = parts[0],
                        colorIndex = parts[1].toIntOrNull() ?: 0,
                        text = parts.subList(2, parts.size).joinToString(":::")
                    )
                } else null
            }
        }

        PersistedLauncherSettings(
            themePreset = theme,
            customWallpaperPath = prefs[Keys.CUSTOM_WALLPAPER_PATH] ?: "",
            wallpaperFitMode = fitMode,
            aeroSwatch = swatch,
            aeroTransparency = prefs[Keys.AERO_TRANSPARENCY] ?: true,
            glassIntensity = prefs[Keys.GLASS_INTENSITY] ?: 0.78f,
            iconSize = iconSize,
            sortMode = sortMode,
            showDesktopIcons = prefs[Keys.SHOW_DESKTOP_ICONS] ?: true,
            autoArrangeIcons = prefs[Keys.AUTO_ARRANGE_ICONS] ?: false,
            alignIconsToGrid = prefs[Keys.ALIGN_ICONS_TO_GRID] ?: false,
            customIconPositions = parsedPositions,
            smallTaskbarIcons = prefs[Keys.SMALL_TASKBAR_ICONS] ?: false,
            showFlip3dOnTaskbar = prefs[Keys.SHOW_FLIP_3D] ?: true,
            showClockGadget = prefs[Keys.SHOW_CLOCK_GADGET] ?: true,
            showCpuMeterGadget = prefs[Keys.SHOW_CPU_GADGET] ?: true,
            showCalendarGadget = prefs[Keys.SHOW_CALENDAR_GADGET] ?: false,
            showWeatherGadget = prefs[Keys.SHOW_WEATHER_GADGET] ?: false,
            showStickyNotesOnDesktop = prefs[Keys.SHOW_STICKY_NOTES] ?: true,
            stickyNotes = notes,
            userName = prefs[Keys.USER_NAME] ?: "Administrator",
            avatarPreset = avatar,
            lockScreenPin = prefs[Keys.LOCK_PIN] ?: "",
            requireLockOnStartup = prefs[Keys.LOCK_ON_STARTUP] ?: false,
            highContrastMode = prefs[Keys.HIGH_CONTRAST] ?: false,
            notepadContent = prefs[Keys.NOTEPAD_CONTENT]
                ?: "Microsoft Windows 7 Ultimate (Build 7601: Service Pack 1)\r\n\r\nQuick Tips:\r\n- Drag and move any app or system icon anywhere on the Desktop!\r\n- Use Personalization to switch between authentic Windows 7 wallpapers (img0.jpg Harmony, Betta Fish, Bliss, Nature, Scenes) or browse your own wallpaper photo!",
            soundEffectsEnabled = prefs[Keys.SOUND_EFFECTS] ?: true,
            desktopShortcutPackages = prefs[Keys.DESKTOP_PACKAGES] ?: emptySet(),
            pinnedTaskbarPackages = prefs[Keys.PINNED_PACKAGES] ?: emptySet(),
            recycledItemIds = prefs[Keys.RECYCLED_IDS] ?: emptySet()
        )
    }

    suspend fun setThemePreset(theme: Win7ThemePreset) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.THEME_PRESET] = theme.id
            prefs[Keys.CUSTOM_WALLPAPER_PATH] = "" // Switch to built-in original Win7 wallpaper
            prefs[Keys.AERO_SWATCH] = theme.defaultSwatch.id
        }
    }

    suspend fun setCustomWallpaperPath(path: String) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.CUSTOM_WALLPAPER_PATH] = path
        }
    }

    suspend fun setWallpaperFitMode(mode: WallpaperFitMode) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.WALLPAPER_FIT_MODE] = mode.id
        }
    }

    suspend fun setAeroSwatch(swatch: AeroGlassSwatch) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.AERO_SWATCH] = swatch.id
        }
    }

    suspend fun setAeroTransparency(enabled: Boolean) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.AERO_TRANSPARENCY] = enabled
        }
    }

    suspend fun setGlassIntensity(intensity: Float) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.GLASS_INTENSITY] = intensity.coerceIn(0.3f, 0.96f)
        }
    }

    suspend fun setDesktopIconSize(size: DesktopIconSize) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.ICON_SIZE] = size.name
        }
    }

    suspend fun setDesktopSortMode(mode: DesktopSortMode) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.SORT_MODE] = mode.name
            // Sorting icons resets manual positions so the user immediately sees the new sort order
            prefs[Keys.ICON_POSITIONS_RAW] = ""
        }
    }

    suspend fun setShowDesktopIcons(show: Boolean) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.SHOW_DESKTOP_ICONS] = show
        }
    }

    suspend fun setAutoArrangeIcons(autoArrange: Boolean) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.AUTO_ARRANGE_ICONS] = autoArrange
            if (autoArrange) {
                prefs[Keys.ICON_POSITIONS_RAW] = ""
            }
        }
    }

    suspend fun setAlignIconsToGrid(alignToGrid: Boolean) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.ALIGN_ICONS_TO_GRID] = alignToGrid
        }
    }

    suspend fun saveIconPosition(itemId: String, xDp: Float, yDp: Float) {
        context.launcherDataStore.edit { prefs ->
            val currentRaw = prefs[Keys.ICON_POSITIONS_RAW] ?: ""
            val map = mutableMapOf<String, IconPosition>()
            if (currentRaw.isNotBlank()) {
                currentRaw.split("|||").forEach { token ->
                    val parts = token.split(":::")
                    if (parts.size == 3) {
                        val id = parts[0]
                        val px = parts[1].toFloatOrNull()
                        val py = parts[2].toFloatOrNull()
                        if (id.isNotBlank() && px != null && py != null) {
                            map[id] = IconPosition(px, py)
                        }
                    }
                }
            }
            map[itemId] = IconPosition(xDp, yDp)
            prefs[Keys.ICON_POSITIONS_RAW] = map.entries.joinToString("|||") { (k, v) ->
                "$k:::${v.xDp}:::${v.yDp}"
            }
            // Moving an icon manually automatically turns off auto-arrange if it was on
            prefs[Keys.AUTO_ARRANGE_ICONS] = false
        }
    }

    suspend fun resetAllIconPositions() {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.ICON_POSITIONS_RAW] = ""
        }
    }

    suspend fun setSmallTaskbarIcons(small: Boolean) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.SMALL_TASKBAR_ICONS] = small
        }
    }

    suspend fun setShowFlip3dOnTaskbar(show: Boolean) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.SHOW_FLIP_3D] = show
        }
    }

    suspend fun setGadgetEnabled(gadgetKey: String, enabled: Boolean) {
        context.launcherDataStore.edit { prefs ->
            when (gadgetKey) {
                "clock" -> prefs[Keys.SHOW_CLOCK_GADGET] = enabled
                "cpu" -> prefs[Keys.SHOW_CPU_GADGET] = enabled
                "calendar" -> prefs[Keys.SHOW_CALENDAR_GADGET] = enabled
                "weather" -> prefs[Keys.SHOW_WEATHER_GADGET] = enabled
                "stickynotes" -> prefs[Keys.SHOW_STICKY_NOTES] = enabled
            }
        }
    }

    suspend fun saveStickyNotes(notes: List<StickyNoteItem>) {
        val serialized = notes.joinToString("|||") { "${it.id}:::${it.colorIndex}:::${it.text}" }
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.STICKY_NOTES_RAW] = serialized
        }
    }

    suspend fun setUserProfile(name: String, avatar: Win7AvatarPreset) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = name.ifBlank { "Administrator" }
            prefs[Keys.AVATAR_PRESET] = avatar.id
        }
    }

    suspend fun setLockScreenConfig(pin: String, lockOnStartup: Boolean) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.LOCK_PIN] = pin.trim()
            prefs[Keys.LOCK_ON_STARTUP] = lockOnStartup
        }
    }

    suspend fun setHighContrastMode(enabled: Boolean) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.HIGH_CONTRAST] = enabled
        }
    }

    suspend fun saveNotepadContent(text: String) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.NOTEPAD_CONTENT] = text
        }
    }

    suspend fun setSoundEffects(enabled: Boolean) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.SOUND_EFFECTS] = enabled
        }
    }

    suspend fun toggleDesktopShortcut(packageName: String) {
        context.launcherDataStore.edit { prefs ->
            val current = prefs[Keys.DESKTOP_PACKAGES] ?: emptySet()
            prefs[Keys.DESKTOP_PACKAGES] = if (packageName in current) current - packageName else current + packageName
        }
    }

    suspend fun setDesktopShortcuts(packages: Set<String>) {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.DESKTOP_PACKAGES] = packages
        }
    }

    suspend fun togglePinnedTaskbarApp(packageName: String) {
        context.launcherDataStore.edit { prefs ->
            val current = prefs[Keys.PINNED_PACKAGES] ?: emptySet()
            prefs[Keys.PINNED_PACKAGES] = if (packageName in current) current - packageName else current + packageName
        }
    }

    suspend fun moveItemToRecycleBin(itemId: String) {
        context.launcherDataStore.edit { prefs ->
            val current = prefs[Keys.RECYCLED_IDS] ?: emptySet()
            prefs[Keys.RECYCLED_IDS] = current + itemId
        }
    }

    suspend fun restoreFromRecycleBin(itemId: String) {
        context.launcherDataStore.edit { prefs ->
            val current = prefs[Keys.RECYCLED_IDS] ?: emptySet()
            prefs[Keys.RECYCLED_IDS] = current - itemId
        }
    }

    suspend fun restoreAllRecycleBin() {
        context.launcherDataStore.edit { prefs ->
            prefs[Keys.RECYCLED_IDS] = emptySet()
        }
    }
}
