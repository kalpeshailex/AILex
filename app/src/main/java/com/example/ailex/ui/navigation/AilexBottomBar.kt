package com.example.ailex.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ailex.features.settings.LocalNotificationsViewModel
import com.example.ailex.ui.theme.Blue100
import com.example.ailex.ui.theme.Danger500
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Navy700
import com.example.ailex.ui.theme.ShapePill
import com.example.ailex.ui.theme.Surface

/**
 * design_handoff_ailex_v1's bottom nav: `Navy700` active label with a
 * `Blue100` pill behind the icon, `Ink500` inactive with no pill, a 1dp
 * `Line200` top border and no elevation — a flat bar, not Material3's
 * default surface-tint nav bar.
 */
@Composable
fun AilexBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val unreadCount by LocalNotificationsViewModel.current.unreadCount.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .background(Surface)
            .navigationBarsPadding()
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line200))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bottomNavDestinations.forEach { destination ->
                val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                val showBadge = destination == Destination.Me && unreadCount > 0
                Column(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            if (!selected) {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 44.dp, height = 28.dp)
                            .background(if (selected) Blue100 else Color.Transparent, ShapePill),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            destination.icon,
                            contentDescription = destination.label,
                            tint = if (selected) Navy700 else Ink500,
                            modifier = Modifier.size(22.dp)
                        )
                        if (showBadge) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(7.dp)
                                    .background(Danger500, CircleShape)
                            )
                        }
                    }
                    Text(
                        text = destination.label,
                        style = TextStyle(fontSize = 11.5.sp, fontWeight = FontWeight.Medium),
                        color = if (selected) Navy700 else Ink500,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
