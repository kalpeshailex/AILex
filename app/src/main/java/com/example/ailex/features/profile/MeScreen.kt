package com.example.ailex.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ailex.core.common.AppLanguage
import com.example.ailex.core.common.LocalAppViewModel
import com.example.ailex.features.settings.HelpTopic
import com.example.ailex.ui.components.AilexBottomSheet
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.components.AilexTextField
import com.example.ailex.ui.components.DangerButton
import com.example.ailex.ui.components.IconTile
import com.example.ailex.ui.components.LocalToastHostState
import com.example.ailex.ui.components.PrimaryButton
import com.example.ailex.ui.components.showToast
import com.example.ailex.ui.theme.Background
import com.example.ailex.ui.theme.Blue100
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.Danger100
import com.example.ailex.ui.theme.Danger600
import com.example.ailex.ui.theme.Ink400
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.Spacing
import kotlinx.coroutines.launch

@Composable
fun MeScreen(
    onNotificationsClick: () -> Unit,
    onHelpClick: (HelpTopic) -> Unit,
    onPrivacyClick: () -> Unit,
    onDeleteDataClick: () -> Unit,
    onLogout: () -> Unit
) {
    val appViewModel = LocalAppViewModel.current
    val state by appViewModel.state.collectAsStateWithLifecycle()
    val toastHost = LocalToastHostState.current
    val scope = rememberCoroutineScope()

    var showEditSheet by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showLogoutSheet by remember { mutableStateOf(false) }
    var editedName by remember(state.displayName) { mutableStateOf(state.displayName) }

    fun notAvailable(what: String) {
        scope.launch { toastHost.showToast("$what isn't available in this preview.") }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(top = Spacing.screenPaddingTop, start = Spacing.screenHorizontal, end = Spacing.screenHorizontal, bottom = 24.dp)
    ) {
        Text(text = "Me", style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp), color = Navy900)
        Spacer(modifier = Modifier.height(18.dp))

        AilexCard {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier.size(48.dp).background(Blue100, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = initialsOf(state.displayName), style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold), color = Blue600)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.displayName.ifBlank { "Your name" },
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        color = Ink900
                    )
                    val contact = state.maskedMobile.ifBlank { state.maskedEmail }.ifBlank { "Not set" }
                    Text(
                        text = "$contact · ${state.language.displayName}",
                        style = TextStyle(fontSize = 13.sp),
                        color = Ink500,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                IconButton(onClick = { editedName = state.displayName; showEditSheet = true }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit profile", tint = Ink600, modifier = Modifier.size(19.dp))
                }
            }
        }

        SettingsGroup(
            title = "Preferences",
            rows = listOf(
                SettingsRowSpec(icon = Icons.Filled.Translate, title = "Language", value = state.language.displayName, onClick = { showLanguageSheet = true }),
                SettingsRowSpec(icon = Icons.Filled.Notifications, title = "Notifications", onClick = onNotificationsClick),
                SettingsRowSpec(icon = Icons.Filled.RecordVoiceOver, title = "Voice", onClick = { notAvailable("Voice preferences") })
            )
        )

        SettingsGroup(
            title = "Privacy and data",
            rows = listOf(
                SettingsRowSpec(icon = Icons.Filled.Shield, title = "Privacy and data", onClick = onPrivacyClick),
                SettingsRowSpec(icon = Icons.Filled.Download, title = "Export my incidents", onClick = { notAvailable("Exporting") }),
                SettingsRowSpec(
                    icon = Icons.Filled.DeleteForever,
                    title = "Delete my data",
                    tint = Danger100,
                    ink = Danger600,
                    titleColor = Danger600,
                    onClick = onDeleteDataClick
                )
            )
        )

        SettingsGroup(
            title = "Understanding AILex",
            rows = listOf(
                SettingsRowSpec(icon = Icons.AutoMirrored.Filled.MenuBook, title = "How Legal AI works", onClick = { onHelpClick(HelpTopic.HOW) }),
                SettingsRowSpec(icon = Icons.AutoMirrored.Filled.HelpOutline, title = "Common questions", onClick = { onHelpClick(HelpTopic.FAQ) }),
                SettingsRowSpec(icon = Icons.Filled.Block, title = "What it cannot do", onClick = { onHelpClick(HelpTopic.LIMITS) })
            )
        )

        SettingsGroup(
            title = "Account",
            rows = listOf(
                SettingsRowSpec(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Log out",
                    tint = Danger100,
                    ink = Danger600,
                    titleColor = Danger600,
                    onClick = { showLogoutSheet = true }
                )
            )
        )

        Text(
            text = "AILex is a preview. It gives general legal information for Mumbai and the MMR, not legal advice, and everything above runs on this device.",
            style = TextStyle(fontSize = 12.sp, lineHeight = 18.sp),
            color = Ink400,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

    if (showEditSheet) {
        AilexBottomSheet(onDismissRequest = { showEditSheet = false }) {
            Text(text = "Edit name", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold), color = Ink900, modifier = Modifier.padding(bottom = 14.dp))
            AilexTextField(value = editedName, onValueChange = { editedName = it }, placeholder = "Your name", modifier = Modifier.padding(bottom = 16.dp))
            PrimaryButton(
                text = "Save",
                enabled = editedName.isNotBlank(),
                onClick = {
                    appViewModel.setUserProfile(editedName.trim(), state.language)
                    showEditSheet = false
                }
            )
        }
    }

    if (showLanguageSheet) {
        AilexBottomSheet(onDismissRequest = { showLanguageSheet = false }) {
            Text(text = "Preferred language", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold), color = Ink900, modifier = Modifier.padding(bottom = 14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                AppLanguage.entries.forEach { language ->
                    val selected = state.language == language
                    AilexCard(
                        fill = if (selected) Blue100 else com.example.ailex.ui.theme.Surface,
                        border = if (selected) Blue600 else Line200,
                        onClick = { appViewModel.setLanguage(language); showLanguageSheet = false }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = language.displayName, style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold), color = Ink900)
                            Text(text = language.nativeLabel, style = TextStyle(fontSize = 12.5.sp), color = Ink500, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
        }
    }

    if (showLogoutSheet) {
        AilexBottomSheet(onDismissRequest = { showLogoutSheet = false }) {
            Text(text = "Log out?", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold), color = Ink900, modifier = Modifier.padding(bottom = 8.dp))
            Text(
                text = "You'll need to verify your mobile number again to sign back in. Your saved incidents stay on this device.",
                style = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
                color = Ink600,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            DangerButton(
                text = "Log out",
                onClick = {
                    appViewModel.clearSession()
                    showLogoutSheet = false
                    onLogout()
                }
            )
        }
    }
}

private data class SettingsRowSpec(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit,
    val value: String? = null,
    val tint: androidx.compose.ui.graphics.Color = Line200,
    val ink: androidx.compose.ui.graphics.Color = Ink600,
    val titleColor: androidx.compose.ui.graphics.Color = Ink900
)

@Composable
private fun SettingsGroup(title: String, rows: List<SettingsRowSpec>) {
    Column(modifier = Modifier.padding(top = 22.dp)) {
        Text(
            text = title,
            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold),
            color = Ink500,
            modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
        )
        AilexCard {
            Column {
                rows.forEachIndexed { index, row ->
                    SettingsRow(row)
                    if (index != rows.lastIndex) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).padding(start = 60.dp).background(Line200))
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(row: SettingsRowSpec) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableRow(row.onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconTile(icon = row.icon, tint = row.tint, ink = row.ink, size = 34.dp, iconSize = 18.dp)
        Text(text = row.title, style = TextStyle(fontSize = 14.5.sp, fontWeight = FontWeight.Medium), color = row.titleColor, modifier = Modifier.weight(1f))
        if (row.value != null) {
            Text(text = row.value, style = TextStyle(fontSize = 13.sp), color = Ink500, modifier = Modifier.padding(end = 4.dp))
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Ink400, modifier = Modifier.size(18.dp))
    }
}

private fun Modifier.clickableRow(onClick: () -> Unit): Modifier = this.clickable(onClick = onClick)

private fun initialsOf(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(1).uppercase()
        else -> (parts.first().take(1) + parts.last().take(1)).uppercase()
    }
}
