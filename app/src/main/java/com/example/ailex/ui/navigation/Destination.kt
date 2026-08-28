package com.example.ailex.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

/** Bottom-nav destinations only; every other route lives in [Routes]. */
sealed class Destination(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Destination(Routes.Home.ROOT, "Home", Icons.Filled.Home)
    data object Ask : Destination(Routes.Ask.ROOT, "Ask", Icons.Filled.Forum)
    data object Incidents : Destination(Routes.Incidents.ROOT, "Incidents", Icons.Filled.FolderOpen)
    data object Me : Destination(Routes.Me.ROOT, "Me", Icons.Filled.Person)
}

val bottomNavDestinations = listOf(
    Destination.Home,
    Destination.Ask,
    Destination.Incidents,
    Destination.Me
)
