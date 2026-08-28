package com.example.ailex.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.components.IconTile
import com.example.ailex.ui.components.LocalToastHostState
import com.example.ailex.ui.components.showToast
import com.example.ailex.ui.theme.Blue100
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.Caution100
import com.example.ailex.ui.theme.Caution700
import com.example.ailex.ui.theme.Danger100
import com.example.ailex.ui.theme.Danger600
import com.example.ailex.ui.theme.Ink400
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.ShapeChip
import com.example.ailex.ui.theme.Surface
import kotlinx.coroutines.launch

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onOpenIncident: (String) -> Unit
) {
    val viewModel = LocalNotificationsViewModel.current
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val toastHost = LocalToastHostState.current
    val scope = rememberCoroutineScope()

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
            Text(text = "Notifications", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold), color = Ink900)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line200))

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            item {
                Text(
                    text = "Reminders about your own incidents, and changes to the sources behind your saved answers. Nothing is sent to anyone else.",
                    style = TextStyle(fontSize = 13.5.sp, lineHeight = 20.sp),
                    color = Ink500,
                    modifier = Modifier.padding(top = 16.dp, bottom = 9.dp)
                )
            }
            items(notifications, key = { it.id }) { notification ->
                NotificationRow(
                    notification = notification,
                    onClick = {
                        viewModel.markRead(notification.id)
                        if (notification.incidentId != null) {
                            onOpenIncident(notification.incidentId)
                        } else {
                            scope.launch { toastHost.showToast("No incident attached to this one.") }
                        }
                    }
                )
            }
            item { Box(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun NotificationRow(notification: AppNotification, onClick: () -> Unit) {
    val (icon, tint, ink) = when (notification.icon) {
        NotificationIcon.PRIORITY_HIGH -> Triple(Icons.Filled.PriorityHigh, Danger100, Danger600)
        NotificationIcon.TRAFFIC -> Triple(Icons.Filled.Traffic, Caution100, Caution700)
        NotificationIcon.MENU_BOOK -> Triple(Icons.AutoMirrored.Filled.MenuBook, Blue100, Blue600)
        NotificationIcon.BROKEN_IMAGE -> Triple(Icons.Filled.BrokenImage, Danger100, Danger600)
    }
    AilexCard(onClick = onClick) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTile(icon = icon, tint = tint, ink = ink, size = 36.dp)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(
                        text = notification.title,
                        style = TextStyle(
                            fontSize = 14.5.sp,
                            lineHeight = 20.sp,
                            fontWeight = if (notification.unread) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = Ink900,
                        modifier = Modifier.weight(1f)
                    )
                    if (notification.unread) {
                        Box(modifier = Modifier.size(8.dp).background(Blue600, CircleShape))
                    }
                }
                Text(
                    text = notification.body,
                    style = TextStyle(fontSize = 12.5.sp, lineHeight = 18.sp),
                    color = Ink600,
                    modifier = Modifier.padding(top = 5.dp)
                )
                Text(
                    text = notification.whenText,
                    style = TextStyle(fontSize = 11.5.sp),
                    color = Ink400,
                    modifier = Modifier.padding(top = 7.dp)
                )
            }
        }
    }
}
