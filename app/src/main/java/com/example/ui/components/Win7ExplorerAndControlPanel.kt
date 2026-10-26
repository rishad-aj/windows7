package com.example.ui.components

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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PersistedLauncherSettings
import com.example.model.AeroGlassSwatch
import com.example.model.DesktopIconSize
import com.example.model.ExplorerPath
import com.example.model.InstalledAppInfo
import com.example.model.StorageDriveInfo
import com.example.model.WallpaperFitMode
import com.example.model.Win7AvatarPreset
import com.example.model.Win7SystemIconType
import com.example.model.Win7ThemePreset
import com.example.model.Win7WindowInstance
import com.example.model.Win7WindowType
import com.example.ui.theme.Win7ControlPanelCategoryGreen
import com.example.ui.theme.Win7ControlPanelHeaderBlue
import com.example.ui.theme.Win7ControlPanelLinkBlue
import com.example.ui.theme.Win7DriveBarCyanBottom
import com.example.ui.theme.Win7DriveBarCyanTop
import com.example.ui.theme.Win7DriveBarRedBottom
import com.example.ui.theme.Win7DriveBarRedTop
import com.example.ui.theme.Win7ExplorerCommandBarBottom
import com.example.ui.theme.Win7ExplorerCommandBarTop
import com.example.viewmodel.DesktopUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Win7ExplorerWindowContent(
    window: Win7WindowInstance,
    uiState: DesktopUiState,
    settings: PersistedLauncherSettings,
    onNavigatePath: (ExplorerPath) -> Unit,
    onSearchChange: (String) -> Unit,
    onLaunchApp: (String) -> Unit,
    onOpenAppProperties: (InstalledAppInfo) -> Unit,
    onOpenWindow: (Win7WindowType) -> Unit,
    onLockComputer: () -> Unit,
    onPromptDefaultLauncher: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // 1. Windows 7 Explorer Top Address Bar + Back/Forward Round Buttons + Search Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFD9ECFF), Color(0xFFBBD8F8))
                    )
                )
                .padding(horizontal = 6.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF64B5F6), Color(0xFF1565C0))
                        )
                    )
                    .border(1.dp, Color(0xFF0D47A1), CircleShape)
                    .clickable { onNavigatePath(ExplorerPath.COMPUTER_ROOT) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF90CAF9), Color(0xFF1E88E5))
                        )
                    )
                    .border(1.dp, Color(0xFF1565C0), CircleShape)
                    .clickable { onNavigatePath(ExplorerPath.USER_STORAGE_D) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFFBFDFF))
                    .border(1.dp, Color(0xFF7B99B8), RoundedCornerShape(3.dp))
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Win7SystemIconCanvas(iconType = Win7SystemIconType.COMPUTER, size = 14.dp)
                Spacer(modifier = Modifier.width(4.dp))
                window.explorerPath.breadcrumb.forEachIndexed { idx, crumb ->
                    if (idx > 0) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFF607D8B),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Text(
                        text = crumb,
                        fontSize = 11.sp,
                        color = Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier
                        .size(14.dp)
                        .clickable(onClick = onRefresh)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Row(
                modifier = Modifier
                    .width(112.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFF7B99B8), RoundedCornerShape(3.dp))
                    .padding(horizontal = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (uiState.explorerSearchQuery.isEmpty()) {
                        Text(
                            text = "Search...",
                            fontSize = 10.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF90A4AE),
                            maxLines = 1
                        )
                    }
                    BasicTextField(
                        value = uiState.explorerSearchQuery,
                        onValueChange = onSearchChange,
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 11.sp, color = Color(0xFF1E1E1E)),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF3E78B2),
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        // 2. Windows 7 Explorer Sky-Blue Gradient Command Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Win7ExplorerCommandBarTop, Win7ExplorerCommandBarBottom)
                    )
                )
                .border(0.5.dp, Color(0xFFA0B5CA))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ExplorerCommandButton("Organize ▾") { onNavigatePath(ExplorerPath.COMPUTER_ROOT) }
            ExplorerCommandButton("System properties") { onOpenWindow(Win7WindowType.CONTROL_PANEL) }
            ExplorerCommandButton("Installed Apps (${uiState.installedApps.size})") {
                onNavigatePath(ExplorerPath.USER_STORAGE_D)
            }
            ExplorerCommandButton("Personalize (12 Wallpapers)") { onOpenWindow(Win7WindowType.PERSONALIZATION) }
            if (!uiState.isDefaultLauncher) {
                ExplorerCommandButton("Set Default Launcher") { onPromptDefaultLauncher() }
            } else {
                ExplorerCommandButton("Lock Screen (Win+L)") { onLockComputer() }
            }
        }

        // 3. Split Navigation Tree (Left) + Folder/Drives Content (Right)
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .width(106.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFF6F9FC))
                    .border(0.5.dp, Color(0xFFD6E2EE))
                    .verticalScroll(rememberScrollState())
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text("Favorites", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E5871))
                ExplorerTreeItem(
                    label = "Desktop",
                    selected = false,
                    onClick = { onNavigatePath(ExplorerPath.COMPUTER_ROOT) }
                )
                ExplorerTreeItem(
                    label = "All Programs",
                    selected = window.explorerPath == ExplorerPath.USER_STORAGE_D,
                    onClick = { onNavigatePath(ExplorerPath.USER_STORAGE_D) }
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text("Libraries", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E5871))
                ExplorerTreeItem(
                    label = "Documents",
                    selected = window.explorerPath == ExplorerPath.LIBRARIES_DOCUMENTS,
                    onClick = { onNavigatePath(ExplorerPath.LIBRARIES_DOCUMENTS) }
                )
                ExplorerTreeItem(
                    label = "Pictures",
                    selected = window.explorerPath == ExplorerPath.LIBRARIES_PICTURES,
                    onClick = { onNavigatePath(ExplorerPath.LIBRARIES_PICTURES) }
                )
                ExplorerTreeItem(
                    label = "Music",
                    selected = window.explorerPath == ExplorerPath.LIBRARIES_MUSIC,
                    onClick = { onNavigatePath(ExplorerPath.LIBRARIES_MUSIC) }
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text("Computer", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E5871))
                ExplorerTreeItem(
                    label = "Local Disk (C:)",
                    selected = window.explorerPath == ExplorerPath.LOCAL_DISK_C,
                    onClick = { onNavigatePath(ExplorerPath.LOCAL_DISK_C) }
                )
                ExplorerTreeItem(
                    label = "Storage (D:)",
                    selected = window.explorerPath == ExplorerPath.USER_STORAGE_D,
                    onClick = { onNavigatePath(ExplorerPath.USER_STORAGE_D) }
                )
                ExplorerTreeItem(
                    label = "Network",
                    selected = window.explorerPath == ExplorerPath.NETWORK_PLACES,
                    onClick = { onOpenWindow(Win7WindowType.NETWORK_CENTER) }
                )
            }

            val query = uiState.explorerSearchQuery.trim().lowercase()
            val filteredApps = remember(uiState.installedApps, window.explorerPath, query) {
                val base = when (window.explorerPath) {
                    ExplorerPath.LIBRARIES_PICTURES -> uiState.installedApps.filter { it.categoryFolder == "Media & Photos" }
                        .ifEmpty { uiState.installedApps }
                    ExplorerPath.LIBRARIES_MUSIC -> uiState.installedApps.filter { it.categoryFolder == "Media & Photos" }
                        .ifEmpty { uiState.installedApps }
                    ExplorerPath.LOCAL_DISK_C -> uiState.installedApps.filter { it.isSystemApp }
                        .ifEmpty { uiState.installedApps }
                    else -> uiState.installedApps
                }
                if (query.isEmpty()) base else base.filter {
                    it.label.lowercase().contains(query) || it.packageName.lowercase().contains(query)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.White)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (window.explorerPath == ExplorerPath.COMPUTER_ROOT && query.isEmpty()) {
                    item {
                        Win7ExplorerSectionHeader(title = "Hard Disk Drives (${uiState.drives.size})")
                    }
                    items(uiState.drives, key = { it.driveLetter }) { drive ->
                        Win7DriveTile(
                            drive = drive,
                            onClick = {
                                if (drive.isSystemDrive) {
                                    onNavigatePath(ExplorerPath.LOCAL_DISK_C)
                                } else {
                                    onNavigatePath(ExplorerPath.USER_STORAGE_D)
                                }
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Win7ExplorerSectionHeader(title = "Windows 7 Built-in Applications")
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ExplorerQuickCard(
                                title = "Media Player 12",
                                subtitle = "Audio Visualizer",
                                iconType = Win7SystemIconType.MEDIA_PLAYER,
                                modifier = Modifier.weight(1f),
                                onClick = { onOpenWindow(Win7WindowType.MEDIA_PLAYER) }
                            )
                            ExplorerQuickCard(
                                title = "Win7 Paint",
                                subtitle = "Drawing Studio",
                                iconType = Win7SystemIconType.PAINT,
                                modifier = Modifier.weight(1f),
                                onClick = { onOpenWindow(Win7WindowType.PAINT) }
                            )
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ExplorerQuickCard(
                                title = "Lock Screen",
                                subtitle = "Win + L Logon",
                                iconType = Win7SystemIconType.LOCK_SCREEN,
                                modifier = Modifier.weight(1f),
                                onClick = onLockComputer
                            )
                            ExplorerQuickCard(
                                title = "Win7 Visual Tour",
                                subtitle = "6 Reference Presets",
                                iconType = Win7SystemIconType.AERO_TOUR_GALLERY,
                                modifier = Modifier.weight(1f),
                                onClick = { onOpenWindow(Win7WindowType.AERO_TOUR_GALLERY) }
                            )
                        }
                    }
                }

                item {
                    Win7ExplorerSectionHeader(
                        title = "${window.explorerPath.title} — Programs (${filteredApps.size})"
                    )
                    Text(
                        text = "Tip: Tap any program to launch it, or long-press for Windows 7 Properties & Pin options.",
                        fontSize = 10.sp,
                        color = Color(0xFF607D8B),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                items(filteredApps, key = { it.packageName }) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFF8FBFF))
                            .border(1.dp, Color(0xFFDCE8F5), RoundedCornerShape(4.dp))
                            .combinedClickable(
                                onClick = { onLaunchApp(app.packageName) },
                                onLongClick = { onOpenAppProperties(app) }
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (app.iconBitmap != null) {
                            Image(
                                bitmap = app.iconBitmap,
                                contentDescription = app.label,
                                modifier = Modifier.size(30.dp)
                            )
                        } else {
                            Win7SystemIconCanvas(iconType = Win7SystemIconType.ANDROID_APP, size = 30.dp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = app.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${app.categoryFolder} • v${app.versionName}",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "Properties",
                            fontSize = 10.sp,
                            color = Win7ControlPanelLinkBlue,
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .clickable { onOpenAppProperties(app) }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 4. Windows 7 Bottom Explorer Details Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFEAF2FB), Color(0xFFD5E5F6))
                    )
                )
                .border(0.5.dp, Color(0xFFB0C4DE))
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${uiState.installedApps.size} installed programs • ${uiState.drives.size} local drives",
                fontSize = 10.sp,
                color = Color(0xFF1E395B)
            )
            Text(
                text = "Microsoft Windows 7 Ultimate Aero",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF3E5871)
            )
        }
    }
}

@Composable
private fun ExplorerCommandButton(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        fontSize = 11.sp,
        color = Color(0xFF1E395B),
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 3.dp)
    )
}

@Composable
private fun ExplorerTreeItem(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(if (selected) Color(0xFFD6EBFF) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            tint = Color(0xFFFBC02D),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = Color(0xFF1E293B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Win7ExplorerSectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF003399)
        )
        Spacer(modifier = Modifier.width(8.dp))
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFD0DFEE))
    }
}

@Composable
private fun Win7DriveTile(
    drive: StorageDriveInfo,
    onClick: () -> Unit
) {
    val freeGb = remember(drive.freeBytes) {
        String.format("%.1f GB", drive.freeBytes / (1024f * 1024f * 1024f))
    }
    val totalGb = remember(drive.totalBytes) {
        String.format("%.1f GB", drive.totalBytes / (1024f * 1024f * 1024f))
    }
    val isNearlyFull = drive.usageRatio > 0.88f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFEFF6FE))
            .border(1.dp, Color(0xFFA8C8E8), RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Win7SystemIconCanvas(iconType = Win7SystemIconType.COMPUTER, size = 38.dp)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = drive.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(13.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFE6E9ED))
                    .border(1.dp, Color(0xFF9EAAB8), RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(drive.usageRatio)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = if (isNearlyFull) {
                                    listOf(Win7DriveBarRedTop, Win7DriveBarRedBottom)
                                } else {
                                    listOf(Win7DriveBarCyanTop, Win7DriveBarCyanBottom)
                                }
                            )
                        )
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "$freeGb free of $totalGb",
                fontSize = 10.sp,
                color = Color(0xFF475569)
            )
        }
    }
}

@Composable
private fun ExplorerQuickCard(
    title: String,
    subtitle: String,
    iconType: Win7SystemIconType,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFF3F8FE))
            .border(1.dp, Color(0xFFBDD4EC), RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Win7SystemIconCanvas(iconType = iconType, size = 28.dp)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF003399))
            Text(text = subtitle, fontSize = 9.sp, color = Color(0xFF546E7A))
        }
    }
}

@Composable
fun Win7ControlPanelWindowContent(
    uiState: DesktopUiState,
    onOpenWindow: (Win7WindowType, ExplorerPath) -> Unit,
    onLockComputer: () -> Unit,
    onPromptDefaultLauncher: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
            .testTag("control_panel_content")
    ) {
        Text(
            text = "Adjust your computer's settings",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Win7ControlPanelHeaderBlue
        )
        Text(
            text = "Control Panel Home • Category View",
            fontSize = 11.sp,
            color = Color(0xFF607D8B),
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        // ONLY show "Make Windows 7 Aero your System Default Launcher" card if NOT already default!
        if (!uiState.isDefaultLauncher) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFE3F2FD), Color(0xFFF1F8E9))
                        )
                    )
                    .border(1.dp, Color(0xFF64B5F6), RoundedCornerShape(6.dp))
                    .clickable(onClick = onPromptDefaultLauncher)
                    .padding(12.dp)
                    .testTag("cp_set_default_launcher_card"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Win7SystemIconCanvas(iconType = Win7SystemIconType.SET_DEFAULT_LAUNCHER, size = 40.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Make Windows 7 Aero your System Default Launcher",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Win7ControlPanelHeaderBlue
                    )
                    Text(
                        text = "Tap here to open Android Default Home App settings and select 'Aero7 Launcher'.",
                        fontSize = 11.sp,
                        color = Color(0xFF334155)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1565C0))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Set Default",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Classic Windows 7 Control Panel Categories
        ControlPanelCategoryBlock(
            iconType = Win7SystemIconType.CONTROL_PANEL,
            categoryTitle = "Appearance and Personalization",
            links = listOf(
                "Change the theme & choose from 12 Windows 7 wallpapers" to { onOpenWindow(Win7WindowType.PERSONALIZATION, ExplorerPath.COMPUTER_ROOT) },
                "Adjust Aero Glass window color, transparency & taskbar" to { onOpenWindow(Win7WindowType.PERSONALIZATION, ExplorerPath.COMPUTER_ROOT) },
                "Add or remove Desktop Gadgets (Clock, CPU Meter)" to { onOpenWindow(Win7WindowType.GADGET_GALLERY, ExplorerPath.COMPUTER_ROOT) },
                "Open Windows 7 Reference Screenshots Gallery" to { onOpenWindow(Win7WindowType.AERO_TOUR_GALLERY, ExplorerPath.COMPUTER_ROOT) }
            )
        )

        ControlPanelCategoryBlock(
            iconType = Win7SystemIconType.USER_ACCOUNTS,
            categoryTitle = "User Accounts and Lock Screen Security",
            links = listOf(
                "Configure Windows 7 Lock Screen PIN & Startup Lock" to { onOpenWindow(Win7WindowType.USER_ACCOUNTS, ExplorerPath.COMPUTER_ROOT) },
                "Change account picture or username" to { onOpenWindow(Win7WindowType.USER_ACCOUNTS, ExplorerPath.COMPUTER_ROOT) },
                "Lock computer right now (Win + L)" to onLockComputer
            )
        )

        ControlPanelCategoryBlock(
            iconType = Win7SystemIconType.COMPUTER,
            categoryTitle = "System and Security",
            links = listOf(
                "View amount of RAM, storage drives, and battery" to { onOpenWindow(Win7WindowType.COMPUTER_EXPLORER, ExplorerPath.COMPUTER_ROOT) },
                "Open Windows Task Manager (Performance & Processes)" to { onOpenWindow(Win7WindowType.TASK_MANAGER, ExplorerPath.COMPUTER_ROOT) },
                "Launch Administrator Command Prompt (cmd.exe)" to { onOpenWindow(Win7WindowType.COMMAND_PROMPT, ExplorerPath.COMPUTER_ROOT) }
            )
        )

        ControlPanelCategoryBlock(
            iconType = Win7SystemIconType.MEDIA_PLAYER,
            categoryTitle = "Programs, Media & Accessories",
            links = listOf(
                "Launch Windows Media Player 12 (Visualizer)" to { onOpenWindow(Win7WindowType.MEDIA_PLAYER, ExplorerPath.COMPUTER_ROOT) },
                "Open Windows 7 Paint (mspaint.exe)" to { onOpenWindow(Win7WindowType.PAINT, ExplorerPath.COMPUTER_ROOT) },
                "Open Windows 7 Notepad (notepad.exe)" to { onOpenWindow(Win7WindowType.NOTEPAD, ExplorerPath.COMPUTER_ROOT) },
                "Browse all installed Android programs (${uiState.installedApps.size})" to {
                    onOpenWindow(Win7WindowType.COMPUTER_EXPLORER, ExplorerPath.USER_STORAGE_D)
                }
            )
        )

        ControlPanelCategoryBlock(
            iconType = Win7SystemIconType.NETWORK,
            categoryTitle = "Network and Internet",
            links = listOf(
                "View network status and tasks (Network and Sharing Center)" to { onOpenWindow(Win7WindowType.NETWORK_CENTER, ExplorerPath.NETWORK_PLACES) },
                "Connect to a network (${uiState.networkName})" to { onOpenWindow(Win7WindowType.NETWORK_CENTER, ExplorerPath.NETWORK_PLACES) }
            )
        )
    }
}

@Composable
private fun ControlPanelCategoryBlock(
    iconType: Win7SystemIconType,
    categoryTitle: String,
    links: List<Pair<String, () -> Unit>>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Win7SystemIconCanvas(iconType = iconType, size = 36.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = categoryTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Win7ControlPanelCategoryGreen,
                modifier = Modifier.clickable { links.firstOrNull()?.second?.invoke() }
            )
            links.forEach { (label, action) ->
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = Win7ControlPanelLinkBlue,
                    modifier = Modifier.clickable(onClick = action)
                )
            }
        }
    }
}

@Composable
fun Win7PersonalizationWindowContent(
    settings: PersistedLauncherSettings,
    onSelectTheme: (Win7ThemePreset) -> Unit,
    onBrowseCustomWallpaper: () -> Unit,
    onClearCustomWallpaper: () -> Unit,
    onSelectFitMode: (WallpaperFitMode) -> Unit,
    onSelectSwatch: (AeroGlassSwatch) -> Unit,
    onSetTransparency: (Boolean) -> Unit,
    onSetIntensity: (Float) -> Unit,
    onSetSmallTaskbar: (Boolean) -> Unit,
    onSetShowFlip3d: (Boolean) -> Unit,
    onSetHighContrast: (Boolean) -> Unit,
    onToggleAutoArrange: () -> Unit,
    onToggleAlignToGrid: () -> Unit,
    onResetIconPositions: () -> Unit,
    onAddAllAppsToDesktop: () -> Unit,
    onSelectIconSize: (DesktopIconSize) -> Unit,
    onUpdateUserProfile: (String, Win7AvatarPreset) -> Unit,
    onSetSoundEffects: (Boolean) -> Unit,
    onOpenUserAccounts: () -> Unit
) {
    var editedName by remember(settings.userName) { mutableStateOf(settings.userName) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
            .testTag("personalization_content"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Change the visuals and sounds on your computer",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Win7ControlPanelHeaderBlue
        )

        // Windows 7 Desktop Background Location Bar + Browse Original Image File Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFFF3F8FE))
                .border(1.dp, Color(0xFFBDD4EC), RoundedCornerShape(5.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Picture location:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E395B)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(26.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFF7B99B8), RoundedCornerShape(3.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = if (settings.customWallpaperPath.isNotBlank()) {
                            "C:\\Users\\${settings.userName}\\Pictures\\custom_win7_wallpaper.jpg"
                        } else {
                            settings.themePreset.winPath
                        },
                        fontSize = 10.sp,
                        color = Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .height(26.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFF8FAFC), Color(0xFFDCE6F2))
                            )
                        )
                        .border(1.dp, Color(0xFF6B8EB2), RoundedCornerShape(3.dp))
                        .clickable(onClick = onBrowseCustomWallpaper)
                        .padding(horizontal = 10.dp)
                        .testTag("browse_wallpaper_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Browse...",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF003399)
                    )
                }

                if (settings.customWallpaperPath.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .height(26.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF1565C0))
                            .clickable(onClick = onClearCustomWallpaper)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Win7 Default",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Picture position:",
                    fontSize = 10.sp,
                    color = Color(0xFF475569)
                )
                WallpaperFitMode.entries.forEach { mode ->
                    val selected = settings.wallpaperFitMode == mode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (selected) Color(0xFF1565C0) else Color.White)
                            .border(1.dp, Color(0xFF90A4AE), RoundedCornerShape(3.dp))
                            .clickable { onSelectFitMode(mode) }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = mode.label,
                            fontSize = 10.sp,
                            color = if (selected) Color.White else Color(0xFF1E293B)
                        )
                    }
                }
            }
        }

        // 1. All 12 Original Windows 7 Wallpapers & Aero Themes Grid
        Text(
            text = "Original Windows 7 Wallpapers (C:\\Windows\\Web\\Wallpaper)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E395B)
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Win7ThemePreset.entries.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pair.forEach { preset ->
                        val isSelected = settings.themePreset == preset && settings.customWallpaperPath.isBlank()
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(5.dp))
                                .background(if (isSelected) Color(0xFFE3F2FD) else Color(0xFFF8FAFC))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF1976D2) else Color(0xFFCBD5E1),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable { onSelectTheme(preset) }
                                .padding(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .border(1.dp, Color.White, RoundedCornerShape(3.dp))
                            ) {
                                Win7DesktopWallpaper(themePreset = preset)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = preset.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = preset.subtitle,
                                fontSize = 9.sp,
                                color = Color(0xFF64748B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = preset.winPath,
                                fontSize = 8.sp,
                                color = Color(0xFF1976D2),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = Color(0xFFD6E2EE))

        // 2. Window Color and Appearance (12 Aero Glass Swatches)
        Text(
            text = "Window Color and Appearance (12 Aero Glass Swatches)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E395B)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AeroGlassSwatch.entries.forEach { swatch ->
                val selected = settings.aeroSwatch == swatch
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onSelectSwatch(swatch) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(swatch.activeGlow, swatch.glassTint, swatch.taskbarTint)
                                )
                            )
                            .border(
                                width = if (selected) 2.5.dp else 1.dp,
                                color = if (selected) Color(0xFF0D47A1) else Color(0xFF90A4AE),
                                shape = RoundedCornerShape(5.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = swatch.displayName,
                        fontSize = 10.sp,
                        color = Color(0xFF334155)
                    )
                }
            }
        }

        // Transparency, Desktop Icon Movement & Taskbar Toggles
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = !settings.autoArrangeIcons,
                    onCheckedChange = { onToggleAutoArrange() }
                )
                Text(
                    "Allow moving apps & icons anywhere on Desktop (Free Move)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0D47A1)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = settings.alignIconsToGrid,
                    onCheckedChange = { onToggleAlignToGrid() }
                )
                Text("Align dragged desktop icons to grid", fontSize = 11.sp, color = Color(0xFF1E293B))
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1565C0))
                        .clickable(onClick = onAddAllAppsToDesktop)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("Add All Apps to Desktop", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFE2E8F0))
                        .clickable(onClick = onResetIconPositions)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("Reset Icon Positions", color = Color(0xFF1E293B), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = settings.aeroTransparency, onCheckedChange = onSetTransparency)
                Text("Enable Aero Glass transparency", fontSize = 11.sp, color = Color(0xFF1E293B))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = settings.smallTaskbarIcons, onCheckedChange = onSetSmallTaskbar)
                Text("Use small icons on Superbar Taskbar", fontSize = 11.sp, color = Color(0xFF1E293B))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = settings.showFlip3dOnTaskbar, onCheckedChange = onSetShowFlip3d)
                Text("Show Aero Flip 3D (Win+Tab) button on Taskbar", fontSize = 11.sp, color = Color(0xFF1E293B))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = settings.soundEffectsEnabled, onCheckedChange = onSetSoundEffects)
                Text("Enable Windows 7 Sound Effects", fontSize = 11.sp, color = Color(0xFF1E293B))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = settings.highContrastMode, onCheckedChange = onSetHighContrast)
                Text("High Contrast Black Mode (Ease of Access)", fontSize = 11.sp, color = Color(0xFF1E293B))
            }
        }

        Column {
            Text(
                text = "Glass color intensity: ${(settings.glassIntensity * 100).toInt()}%",
                fontSize = 11.sp,
                color = Color(0xFF334155)
            )
            Slider(
                value = settings.glassIntensity,
                onValueChange = onSetIntensity,
                valueRange = 0.40f..0.95f
            )
        }

        HorizontalDivider(color = Color(0xFFD6E2EE))

        // 3. Desktop Icon Size & Lock Screen Security Shortcut
        Text(
            text = "Desktop Icon Size & Lock Screen Account",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E395B)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DesktopIconSize.entries.forEach { sizeOption ->
                val selected = settings.iconSize == sizeOption
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (selected) Color(0xFF1565C0) else Color(0xFFEEF2F6))
                        .clickable { onSelectIconSize(sizeOption) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = sizeOption.label,
                        fontSize = 11.sp,
                        color = if (selected) Color.White else Color(0xFF1E293B)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Win7AvatarPreset.entries.forEach { avatar ->
                    val selected = settings.avatarPreset == avatar
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .border(
                                width = if (selected) 2.5.dp else 1.dp,
                                color = if (selected) Color(0xFF1565C0) else Color(0xFFB0BEC5),
                                shape = RoundedCornerShape(5.dp)
                            )
                            .clickable { onUpdateUserProfile(editedName, avatar) }
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Win7UserAvatarCanvas(preset = avatar, size = 36.dp)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1565C0))
                    .clickable(onClick = onOpenUserAccounts)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("Lock Screen & PIN", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
