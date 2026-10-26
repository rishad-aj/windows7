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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.PersistedLauncherSettings
import com.example.model.DesktopIconSize
import com.example.model.Win7AvatarPreset
import com.example.model.Win7ThemePreset
import com.example.ui.theme.Win7CloseRedBottom
import com.example.ui.theme.Win7CloseRedTop
import com.example.viewmodel.PowerActionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Win7AuthenticLockScreenOverlay(
    powerState: PowerActionState,
    settings: PersistedLauncherSettings,
    onUnlockSuccess: () -> Unit,
    onSwitchUserAccount: (String, Win7AvatarPreset) -> Unit,
    onTriggerPowerState: (PowerActionState) -> Unit,
    onRestartDesktop: () -> Unit,
    onToggleHighContrast: (Boolean) -> Unit,
    onToggleLargeIcons: (Boolean) -> Unit,
    onToggleSoundEffects: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var enteredPassword by remember { mutableStateOf("") }
    var showWrongPasswordError by remember { mutableStateOf(false) }
    var isSwitchUserMode by remember { mutableStateOf(false) }
    var isWelcomeTransition by remember { mutableStateOf(false) }
    var showEaseOfAccessDialog by remember { mutableStateOf(false) }
    var showPowerMenu by remember { mutableStateOf(false) }

    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val lockClockText = remember(currentTimeMillis) {
        SimpleDateFormat("h:mm a  •  EEEE, MMMM d", Locale.getDefault()).format(Date(currentTimeMillis))
    }

    fun attemptUnlock() {
        val expected = settings.lockScreenPin.trim()
        if (expected.isNotEmpty() && enteredPassword.trim() != expected) {
            showWrongPasswordError = true
        } else {
            scope.launch {
                isWelcomeTransition = true
                delay(550L)
                isWelcomeTransition = false
                onUnlockSuccess()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(2000f)
            .testTag("win7_logon_overlay")
    ) {
        // 1. Official Windows 7 Logon Screen Background (Swallow Bird, Vines, Sunburst & Bokeh)
        Win7DesktopWallpaper(
            themePreset = Win7ThemePreset.LOGON_SWALLOW,
            highContrast = settings.highContrastMode,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle top & bottom dark cinema vignette just like authui.exe
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x66000818),
                            Color.Transparent,
                            Color.Transparent,
                            Color(0x88000A1E)
                        )
                    )
                )
        )

        // Top subtle clock & date indicator
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 10.dp)
        ) {
            Text(
                text = lockClockText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xDDFFFFFF),
                    fontSize = 12.sp,
                    shadow = Shadow(Color.Black, Offset(0f, 1.5f), 4f)
                )
            )
        }

        // 2. Center Area: Shutting Down / Welcome Ring / Wrong Password / Switch User / Main User Tile
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                powerState == PowerActionState.SHUTTING_DOWN -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF80D8FF),
                                strokeWidth = 3.5.dp,
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = "Shutting down...",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    shadow = Shadow(Color.Black, Offset(1f, 2f), 5f)
                                )
                            )
                        }
                        Win7GlassActionButton(
                            label = "Wake Up / Log On",
                            onClick = onUnlockSuccess
                        )
                    }
                }

                isWelcomeTransition -> {
                    // Authentic Windows 7 "Welcome" Spinning Teal Ring Transition
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF4DD0E1),
                            strokeWidth = 3.5.dp,
                            modifier = Modifier.size(30.dp)
                        )
                        Text(
                            text = "Welcome",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Normal,
                                shadow = Shadow(Color.Black, Offset(1f, 2f), 5f)
                            )
                        )
                    }
                }

                showWrongPasswordError -> {
                    // Authentic Windows 7 Incorrect Password Prompt
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Win7GlassBevelAvatarTile(
                            avatarPreset = settings.avatarPreset,
                            frameSize = 114.dp,
                            onClick = { showWrongPasswordError = false }
                        )
                        Text(
                            text = "The user name or password is incorrect.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White,
                                fontSize = 14.sp,
                                shadow = Shadow(Color.Black, Offset(1f, 1.5f), 4f)
                            )
                        )
                        Win7GlassActionButton(
                            label = "OK",
                            onClick = {
                                enteredPassword = ""
                                showWrongPasswordError = false
                            },
                            modifier = Modifier.width(88.dp)
                        )
                    }
                }

                isSwitchUserMode -> {
                    // Windows 7 Multi-User Switch Screen (Administrator + Guest + Space Explorer)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Text(
                            text = "Select a user account to log on",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontSize = 15.sp,
                                shadow = Shadow(Color.Black, Offset(1f, 1.5f), 4f)
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SwitchUserTileOption(
                                name = "Administrator",
                                status = "Locked",
                                avatar = Win7AvatarPreset.SUNFLOWER,
                                onClick = {
                                    onSwitchUserAccount("Administrator", Win7AvatarPreset.SUNFLOWER)
                                    isSwitchUserMode = false
                                }
                            )
                            SwitchUserTileOption(
                                name = "Guest",
                                status = "Logged on",
                                avatar = Win7AvatarPreset.ASTRONAUT,
                                onClick = {
                                    onSwitchUserAccount("Guest", Win7AvatarPreset.ASTRONAUT)
                                    isSwitchUserMode = false
                                }
                            )
                        }
                    }
                }

                else -> {
                    // Standard Windows 7 Lock Screen Center Tile
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Glossy Metallic-Glass Avatar Frame (tapping when no PIN logs right in!)
                        Win7GlassBevelAvatarTile(
                            avatarPreset = settings.avatarPreset,
                            frameSize = 118.dp,
                            onClick = {
                                if (settings.lockScreenPin.isBlank()) {
                                    attemptUnlock()
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // User Name
                        Text(
                            text = settings.userName,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Normal,
                                shadow = Shadow(Color(0xEE000000), Offset(1f, 2f), 5f)
                            )
                        )

                        // "Locked" Status Label
                        Text(
                            text = if (settings.lockScreenPin.isBlank()) "Locked (Click arrow or picture to unlock)" else "Locked (PIN required)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFD6ECFF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                shadow = Shadow(Color(0xEE000000), Offset(1f, 1.5f), 4f)
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Authentic Windows 7 Sunken Password Box + Glossy Blue Arrow Button (➔)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(196.dp)
                                    .height(28.dp)
                                    .shadow(4.dp, RoundedCornerShape(3.dp))
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color.White)
                                    .border(1.5.dp, Color(0xFF567EBB), RoundedCornerShape(3.dp))
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (enteredPassword.isEmpty()) {
                                    Text(
                                        text = if (settings.lockScreenPin.isBlank()) "Password (optional)" else "Enter PIN / Password",
                                        fontSize = 11.sp,
                                        fontStyle = FontStyle.Italic,
                                        color = Color(0xFF78909C)
                                    )
                                }
                                BasicTextField(
                                    value = enteredPassword,
                                    onValueChange = { enteredPassword = it },
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    textStyle = TextStyle(
                                        fontSize = 13.sp,
                                        color = Color(0xFF1E1E1E)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("lock_password_input")
                                )
                            }

                            // Signature Windows 7 Glossy Blue Submit Arrow Button (➔)
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .shadow(4.dp, RoundedCornerShape(4.dp))
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF7CE8F8),
                                                Color(0xFF1E88E5),
                                                Color(0xFF0D47A1)
                                            )
                                        )
                                    )
                                    .border(1.dp, Color(0xCCFFFFFF), RoundedCornerShape(4.dp))
                                    .clickable { attemptUnlock() }
                                    .testTag("lock_submit_arrow_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Unlock Windows 7",
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // "Switch User" Aero Glass Button
                        Win7GlassActionButton(
                            label = "Switch User",
                            onClick = { isSwitchUserMode = true },
                            modifier = Modifier.width(118.dp)
                        )
                    }
                }
            }

            // Optional Ease of Access Popup Modal on Lock Screen
            if (showEaseOfAccessDialog) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(280.dp)
                        .shadow(16.dp, RoundedCornerShape(6.dp))
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF5F8FC))
                        .border(1.5.dp, Color(0xFF567EBB), RoundedCornerShape(6.dp))
                        .padding(12.dp)
                        .testTag("ease_of_access_dialog")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Ease of Access",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF003399)
                        )
                        Text(
                            text = "Make Windows 7 easier to see and use:",
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = settings.highContrastMode,
                                onCheckedChange = onToggleHighContrast
                            )
                            Text("High Contrast Black (Accessibility)", fontSize = 11.sp, color = Color(0xFF1E293B))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = settings.iconSize == DesktopIconSize.LARGE,
                                onCheckedChange = onToggleLargeIcons
                            )
                            Text("Make desktop icons larger (125%)", fontSize = 11.sp, color = Color(0xFF1E293B))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = settings.soundEffectsEnabled,
                                onCheckedChange = onToggleSoundEffects
                            )
                            Text("Provide audio feedback when clicking", fontSize = 11.sp, color = Color(0xFF1E293B))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1565C0))
                                    .clickable { showEaseOfAccessDialog = false }
                                    .padding(horizontal = 14.dp, vertical = 5.dp)
                            ) {
                                Text("OK", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // 3. Bottom Bar: Ease of Access (Bottom-Left) + Windows 7 Ultimate Branding (Center) + Red Power Split Button (Bottom-Right)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Bottom-Left: Glossy Blue Ease of Access Button + EN Keyboard Badge
            Row(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .shadow(4.dp, RoundedCornerShape(4.dp))
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF4FC3F7), Color(0xFF0277BD))
                            )
                        )
                        .border(1.dp, Color(0xBBFFFFFF), RoundedCornerShape(4.dp))
                        .clickable { showEaseOfAccessDialog = !showEaseOfAccessDialog }
                        .testTag("lock_ease_of_access_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessibilityNew,
                        contentDescription = "Ease of Access",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0x44000000))
                        .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(3.dp))
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "EN", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Bottom-Center: Official Windows 7 Ultimate Branding
            Row(
                modifier = Modifier.align(Alignment.BottomCenter),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Win7FourColorFlagEmblem(size = 20.dp)
                Text(
                    text = "Windows 7",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(Color.Black, Offset(1f, 1.5f), 4f)
                    )
                )
                Text(
                    text = "Ultimate",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFE1F5FE),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        shadow = Shadow(Color.Black, Offset(1f, 1.5f), 4f)
                    )
                )
            }

            // Bottom-Right: Red/Glass Windows 7 Power Split Button (⏻ | ▴)
            Column(
                modifier = Modifier.align(Alignment.BottomEnd),
                horizontalAlignment = Alignment.End
            ) {
                if (showPowerMenu) {
                    Column(
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .width(126.dp)
                            .shadow(8.dp, RoundedCornerShape(4.dp))
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFF5F8FC))
                            .border(1.dp, Color(0xFF6B8EB2), RoundedCornerShape(4.dp))
                            .padding(2.dp)
                    ) {
                        LockPowerMenuOption("Restart") {
                            showPowerMenu = false
                            onRestartDesktop()
                            onUnlockSuccess()
                        }
                        LockPowerMenuOption("Shut down") {
                            showPowerMenu = false
                            onTriggerPowerState(PowerActionState.SHUTTING_DOWN)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .height(30.dp)
                        .shadow(4.dp, RoundedCornerShape(4.dp))
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Win7CloseRedTop, Color(0xFFD6382E), Win7CloseRedBottom)
                            )
                        )
                        .border(1.dp, Color(0xBBFFFFFF), RoundedCornerShape(4.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clickable { onTriggerPowerState(PowerActionState.SHUTTING_DOWN) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Shut down",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(Color(0x88FFFFFF))
                    )
                    Box(
                        modifier = Modifier
                            .clickable { showPowerMenu = !showPowerMenu }
                            .padding(horizontal = 5.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Power menu",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Win7GlassBevelAvatarTile(
    avatarPreset: Win7AvatarPreset,
    frameSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val outerShape = RoundedCornerShape(10.dp)
    val innerShape = RoundedCornerShape(6.dp)

    Box(
        modifier = Modifier
            .size(frameSize)
            .shadow(16.dp, outerShape)
            .clip(outerShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xEEFFFFFF),
                        Color(0x99B3E5FC),
                        Color(0xCC5C8AB8),
                        Color(0xDDE1F5FE)
                    )
                )
            )
            .border(2.dp, Color.White, outerShape)
            .clickable(onClick = onClick)
            .padding(8.dp)
            .testTag("lock_avatar_frame"),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(innerShape)
                .border(1.dp, Color(0xFF1E395B), innerShape)
                .drawBehind {
                    // Top diagonal glass reflection overlay
                    drawLine(
                        color = Color(0x55FFFFFF),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height * 0.4f),
                        strokeWidth = 18f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Win7UserAvatarCanvas(
                preset = avatarPreset,
                size = frameSize - 16.dp
            )
        }
    }
}

@Composable
private fun SwitchUserTileOption(
    name: String,
    status: String,
    avatar: Win7AvatarPreset,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Win7GlassBevelAvatarTile(
            avatarPreset = avatar,
            frameSize = 96.dp,
            onClick = onClick
        )
        Text(
            text = name,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = status,
            color = Color(0xFFB3E5FC),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun Win7GlassActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(4.dp)
    Box(
        modifier = modifier
            .height(28.dp)
            .shadow(4.dp, shape)
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x66FFFFFF),
                        Color(0x334FC3F7),
                        Color(0x660B2548)
                    )
                )
            )
            .border(1.dp, Color(0xCCB3E5FC), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LockPowerMenuOption(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = label, fontSize = 11.sp, color = Color(0xFF1E293B))
    }
}

@Composable
fun Win7FourColorFlagEmblem(
    size: androidx.compose.ui.unit.Dp = 20.dp
) {
    Canvas(modifier = Modifier.size(size)) {
        val pane = this.size.minDimension * 0.42f
        val gap = this.size.minDimension * 0.08f
        drawRoundRect(
            color = Color(0xFFF25022),
            topLeft = Offset(0f, 0f),
            size = Size(pane, pane),
            cornerRadius = CornerRadius(2f, 2f)
        )
        drawRoundRect(
            color = Color(0xFF7FBA00),
            topLeft = Offset(pane + gap, 0f),
            size = Size(pane, pane),
            cornerRadius = CornerRadius(2f, 2f)
        )
        drawRoundRect(
            color = Color(0xFF00A4EF),
            topLeft = Offset(0f, pane + gap),
            size = Size(pane, pane),
            cornerRadius = CornerRadius(2f, 2f)
        )
        drawRoundRect(
            color = Color(0xFFFFB900),
            topLeft = Offset(pane + gap, pane + gap),
            size = Size(pane, pane),
            cornerRadius = CornerRadius(2f, 2f)
        )
    }
}
