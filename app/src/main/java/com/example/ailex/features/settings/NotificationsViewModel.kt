package com.example.ailex.features.settings

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

enum class NotificationIcon { PRIORITY_HIGH, TRAFFIC, MENU_BOOK, BROKEN_IMAGE }

data class AppNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val body: String,
    val whenText: String,
    val icon: NotificationIcon,
    val unread: Boolean,
    /** Incident id this deep-links to, or null if nothing is attached. */
    val incidentId: String? = null
)

/**
 * The four seeded notifications, lifted verbatim from
 * design_handoff_ailex_v1's `NOTIFICATIONS` array (`AILex Prototype.dc.html`,
 * `<script data-dc-script>` block). They reference the same seeded
 * incidents (i1, i2) and the same re-verified traffic source as
 * `LiveSituationResults` — the content is cross-consistent by design.
 */
private val NotificationSeedData = listOf(
    AppNotification(
        title = "Your NCRP portal complaint is still not filed",
        body = "The 1930 report is on record. The written portal complaint for the ₹18,400 debit has not been filed yet.",
        whenText = "Today, 9:00 am",
        icon = NotificationIcon.PRIORITY_HIGH,
        unread = true,
        incidentId = "i2"
    ),
    AppNotification(
        title = "Second e-challan still showing as pending",
        body = "The duplicate challan at Sion Circle has not changed status since you saved it.",
        whenText = "Yesterday",
        icon = NotificationIcon.TRAFFIC,
        unread = true,
        incidentId = "i1"
    ),
    AppNotification(
        title = "A source behind one of your answers was updated",
        body = "The Maharashtra e-challan payment route page was re-verified on 04 Aug 2026. Your saved guidance is unchanged.",
        whenText = "24 Aug 2026",
        icon = NotificationIcon.MENU_BOOK,
        unread = false,
        incidentId = null
    ),
    AppNotification(
        title = "Evidence file missing",
        body = "payment-receipt.pdf is no longer on this device, so the reference in your challan incident no longer opens.",
        whenText = "23 Aug 2026",
        icon = NotificationIcon.BROKEN_IMAGE,
        unread = false,
        incidentId = "i1"
    )
)

/** Activity-scoped so Home's bell badge and the Notifications screen share the same read/unread state. */
class NotificationsViewModel : ViewModel() {
    private val _notifications = MutableStateFlow(NotificationSeedData)
    val notifications: StateFlow<List<AppNotification>> = _notifications

    val unreadCount: StateFlow<Int> = _notifications
        .map { list -> list.count { it.unread } }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), NotificationSeedData.count { it.unread })

    fun markRead(id: String) {
        _notifications.value = _notifications.value.map { if (it.id == id) it.copy(unread = false) else it }
    }
}

val LocalNotificationsViewModel = staticCompositionLocalOf<NotificationsViewModel> {
    error("No NotificationsViewModel provided")
}
