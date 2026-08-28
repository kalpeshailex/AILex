package com.example.ailex.ui

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ailex.core.common.AppViewModel
import com.example.ailex.core.common.LocalAppViewModel
import com.example.ailex.features.incidents.IncidentsViewModel
import com.example.ailex.features.incidents.LocalIncidentsViewModel
import com.example.ailex.features.settings.LocalNotificationsViewModel
import com.example.ailex.features.settings.NotificationsViewModel
import com.example.ailex.ui.components.LocalToastHostState
import com.example.ailex.ui.components.Toast
import com.example.ailex.ui.navigation.AilexBottomBar
import com.example.ailex.ui.navigation.AilexNavHost
import com.example.ailex.ui.navigation.Routes

@Composable
fun AilexApp() {
    val navController = rememberNavController()
    val appViewModel: AppViewModel = viewModel()
    val incidentsViewModel: IncidentsViewModel = viewModel()
    val notificationsViewModel: NotificationsViewModel = viewModel()
    val toastHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalAppViewModel provides appViewModel,
        LocalIncidentsViewModel provides incidentsViewModel,
        LocalNotificationsViewModel provides notificationsViewModel,
        LocalToastHostState provides toastHostState
    ) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val isTopLevel = backStackEntry?.destination?.route in Routes.topLevelRoutes

        Scaffold(
            bottomBar = { if (isTopLevel) AilexBottomBar(navController) },
            snackbarHost = { SnackbarHost(toastHostState) { Toast(it) } }
        ) { innerPadding ->
            AilexNavHost(
                navController = navController,
                modifier = Modifier
                    .padding(innerPadding)
                    .then(if (!isTopLevel) Modifier.navigationBarsPadding() else Modifier)
            )
        }
    }
}
