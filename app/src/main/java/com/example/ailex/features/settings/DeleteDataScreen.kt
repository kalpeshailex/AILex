package com.example.ailex.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ailex.core.common.LocalAppViewModel
import com.example.ailex.features.incidents.LocalIncidentsViewModel
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.components.AilexTextField
import com.example.ailex.ui.components.DangerButton
import com.example.ailex.ui.components.LocalToastHostState
import com.example.ailex.ui.components.showToast
import com.example.ailex.ui.theme.Danger050
import com.example.ailex.ui.theme.Danger600
import com.example.ailex.ui.theme.Danger700
import com.example.ailex.ui.theme.DangerBorderLt
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.Surface
import kotlinx.coroutines.launch

private const val ConfirmationWord = "DELETE"

@Composable
fun DeleteDataScreen(
    onBack: () -> Unit,
    onDeleted: () -> Unit
) {
    val incidentsViewModel = LocalIncidentsViewModel.current
    val appViewModel = LocalAppViewModel.current
    val incidents by incidentsViewModel.incidents.collectAsStateWithLifecycle()
    val toastHost = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    var confirmationText by remember { mutableStateOf("") }

    val savedIncidents = incidents.size
    val notesAndTimeline = incidents.sumOf { (if (it.notes.isNotBlank()) 1 else 0) + it.timeline.size }
    val complaintDrafts = incidents.count { it.complaintEdits.isNotEmpty() }

    Column(modifier = Modifier.fillMaxSize().background(Surface)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink700)
            }
            Text(text = "Delete my data", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold), color = Ink900)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line200))

        Column(modifier = Modifier.weight(1f).padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)) {
            AilexCard(fill = Danger050, border = DangerBorderLt) {
                Row(modifier = Modifier.padding(15.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = Danger600, modifier = Modifier.size(20.dp))
                    Text(
                        text = "This cannot be undone. Everything below is removed from this device only — there is nothing on a server to delete.",
                        style = TextStyle(fontSize = 13.5.sp, lineHeight = 20.sp),
                        color = Danger700
                    )
                }
            }

            Column(modifier = Modifier.padding(top = 18.dp, bottom = 4.dp)) {
                Text(text = "What gets removed", style = TextStyle(fontSize = 14.5.sp, fontWeight = FontWeight.Bold), color = Navy900, modifier = Modifier.padding(bottom = 10.dp))
                DeleteCountRow(label = "Saved incidents", count = savedIncidents)
                DeleteCountRow(label = "Notes and timeline events", count = notesAndTimeline)
                DeleteCountRow(label = "Complaint drafts", count = complaintDrafts)
                DeleteCountRow(label = "Profile and mobile number", count = 1)
            }

            Text(
                text = "Type $ConfirmationWord to confirm.",
                style = TextStyle(fontSize = 13.5.sp, fontWeight = FontWeight.Medium),
                color = Ink600,
                modifier = Modifier.padding(top = 18.dp, bottom = 8.dp)
            )
            AilexTextField(
                value = confirmationText,
                onValueChange = { input ->
                    confirmationText = input.uppercase().filter { it.isLetter() }.take(ConfirmationWord.length)
                },
                placeholder = ConfirmationWord,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            DangerButton(
                text = "Delete everything",
                enabled = confirmationText == ConfirmationWord,
                onClick = {
                    incidentsViewModel.clearAll()
                    appViewModel.clearSession()
                    scope.launch { toastHost.showToast("Your data has been deleted from this device.") }
                    onDeleted()
                }
            )
        }
    }
}

@Composable
private fun DeleteCountRow(label: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = TextStyle(fontSize = 14.sp), color = Ink700)
        Text(text = count.toString(), style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold), color = Navy900)
    }
}
