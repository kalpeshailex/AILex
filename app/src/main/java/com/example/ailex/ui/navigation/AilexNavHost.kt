package com.example.ailex.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.ailex.core.common.LegalDomain
import com.example.ailex.features.auth.AuthViewModel
import com.example.ailex.features.auth.EmailScreen
import com.example.ailex.features.auth.NameScreen
import com.example.ailex.features.auth.OtpScreen
import com.example.ailex.features.auth.PhoneScreen
import com.example.ailex.features.auth.WelcomeScreen
import com.example.ailex.features.auth.LanguageScreen as AuthLanguageScreen
import com.example.ailex.features.conversation.AskScreen
import com.example.ailex.features.conversation.AskLegalAiSessionViewModel
import com.example.ailex.features.conversation.ConversationScreen
import com.example.ailex.features.conversation.VoiceScreen
import com.example.ailex.features.complaint.ComplaintDraftScreen
import com.example.ailex.features.escalation.EscalationScreen
import com.example.ailex.features.home.HomeScreen
import com.example.ailex.features.incidents.IncidentDetailScreen
import com.example.ailex.features.incidents.IncidentListScreen
import com.example.ailex.features.live_situation.CategoryScreen
import com.example.ailex.features.live_situation.FreeTextScreen
import com.example.ailex.features.live_situation.QuestionScreen
import com.example.ailex.features.live_situation.ResultScreen
import com.example.ailex.features.live_situation.SafetyScreen
import com.example.ailex.features.live_situation.UrgentScreen
import com.example.ailex.features.live_situation.LiveSituationViewModel
import com.example.ailex.features.profile.MeScreen
import com.example.ailex.features.settings.DeleteDataScreen
import com.example.ailex.features.settings.HelpScreen
import com.example.ailex.features.settings.HelpTopic
import com.example.ailex.features.settings.NotificationsScreen
import com.example.ailex.features.settings.PrivacyScreen

@Composable
fun AilexNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Auth.GRAPH,
        modifier = modifier
    ) {
        navigation(startDestination = Routes.Auth.WELCOME, route = Routes.Auth.GRAPH) {
            composable(Routes.Auth.WELCOME) {
                WelcomeScreen(
                    onContinueWithPhone = { navController.navigate(Routes.Auth.MOBILE) },
                    onContinueWithEmail = { navController.navigate(Routes.Auth.EMAIL) }
                )
            }
            composable(Routes.Auth.MOBILE) { backStackEntry ->
                val authGraphEntry = remember { navController.getBackStackEntry(Routes.Auth.GRAPH) }
                val authViewModel: AuthViewModel = viewModel(authGraphEntry)
                PhoneScreen(
                    viewModel = authViewModel,
                    onBack = { navController.popBackStack() },
                    onContinue = { navController.navigate(Routes.Auth.OTP) }
                )
            }
            composable(Routes.Auth.EMAIL) {
                val authGraphEntry = remember { navController.getBackStackEntry(Routes.Auth.GRAPH) }
                val authViewModel: AuthViewModel = viewModel(authGraphEntry)
                EmailScreen(
                    viewModel = authViewModel,
                    onBack = { navController.popBackStack() },
                    onContinue = { navController.navigate(Routes.Auth.OTP) }
                )
            }
            composable(Routes.Auth.OTP) {
                val authGraphEntry = remember { navController.getBackStackEntry(Routes.Auth.GRAPH) }
                val authViewModel: AuthViewModel = viewModel(authGraphEntry)
                OtpScreen(
                    viewModel = authViewModel,
                    onBack = { navController.popBackStack() },
                    onChangeNumber = { navController.popBackStack() },
                    onVerified = { navController.navigate(Routes.Auth.NAME) }
                )
            }
            composable(Routes.Auth.NAME) {
                val authGraphEntry = remember { navController.getBackStackEntry(Routes.Auth.GRAPH) }
                val authViewModel: AuthViewModel = viewModel(authGraphEntry)
                NameScreen(
                    viewModel = authViewModel,
                    onBack = { navController.popBackStack() },
                    onContinue = { navController.navigate(Routes.Auth.LANGUAGE) }
                )
            }
            composable(Routes.Auth.LANGUAGE) {
                val authGraphEntry = remember { navController.getBackStackEntry(Routes.Auth.GRAPH) }
                val authViewModel: AuthViewModel = viewModel(authGraphEntry)
                AuthLanguageScreen(
                    viewModel = authViewModel,
                    onBack = { navController.popBackStack() },
                    onFinish = {
                        navController.navigate(Routes.Home.ROOT) {
                            popUpTo(Routes.Auth.GRAPH) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Routes.Home.ROOT) {
            HomeScreen(
                onStartLiveSituation = { navController.navigate(Routes.LiveSituation.SAFETY) },
                onDomainClick = { domainId -> navController.navigate(Routes.LiveSituation.safety(domainId)) },
                onIncidentClick = { id -> navController.navigate(Routes.Incidents.detail(id)) },
                onSeeAllIncidentsClick = {
                    navController.navigate(Routes.Incidents.ROOT) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onAskLegalAiClick = { navController.navigate(Routes.Ask.ROOT) { launchSingleTop = true } },
                onNotificationsClick = { navController.navigate(Routes.NOTIFICATIONS) }
            )
        }

        navigation(startDestination = Routes.LiveSituation.SAFETY_PATTERN, route = Routes.LiveSituation.GRAPH) {
            composable(
                route = Routes.LiveSituation.SAFETY_PATTERN,
                arguments = listOf(navArgument("domainId") { type = NavType.StringType; nullable = true; defaultValue = null })
            ) { backStackEntry ->
                val liveSituationGraphEntry = remember { navController.getBackStackEntry(Routes.LiveSituation.GRAPH) }
                val vm: LiveSituationViewModel = viewModel(liveSituationGraphEntry)
                val domainId = backStackEntry.arguments?.getString("domainId")
                LaunchedEffect(domainId) {
                    if (!domainId.isNullOrEmpty()) {
                        LegalDomain.entries.find { it.id == domainId }?.let(vm::selectDomain)
                    }
                }
                val hasDomain = !domainId.isNullOrEmpty()
                SafetyScreen(
                    onClose = { navController.popBackStack() },
                    onSafe = {
                        navController.navigate(if (hasDomain) Routes.LiveSituation.QUESTION else Routes.LiveSituation.CATEGORY)
                    },
                    onUrgent = { navController.navigate(Routes.LiveSituation.URGENT) }
                )
            }
            composable(Routes.LiveSituation.URGENT) {
                val liveSituationGraphEntry = remember { navController.getBackStackEntry(Routes.LiveSituation.GRAPH) }
                val vm: LiveSituationViewModel = viewModel(liveSituationGraphEntry)
                UrgentScreen(
                    onBack = { navController.popBackStack() },
                    onContinue = {
                        val hasDomain = vm.state.value.domain != null
                        navController.navigate(if (hasDomain) Routes.LiveSituation.QUESTION else Routes.LiveSituation.CATEGORY)
                    }
                )
            }
            composable(Routes.LiveSituation.CATEGORY) {
                val liveSituationGraphEntry = remember { navController.getBackStackEntry(Routes.LiveSituation.GRAPH) }
                val vm: LiveSituationViewModel = viewModel(liveSituationGraphEntry)
                CategoryScreen(
                    onBack = { navController.popBackStack() },
                    onDomainSelected = { domain ->
                        vm.selectDomain(domain)
                        navController.navigate(Routes.LiveSituation.QUESTION)
                    },
                    onSomethingElseSelected = {
                        vm.selectGeneral()
                        navController.navigate(Routes.LiveSituation.FREETEXT)
                    }
                )
            }
            composable(Routes.LiveSituation.QUESTION) {
                val liveSituationGraphEntry = remember { navController.getBackStackEntry(Routes.LiveSituation.GRAPH) }
                val vm: LiveSituationViewModel = viewModel(liveSituationGraphEntry)
                QuestionScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onTypeInstead = { navController.navigate(Routes.LiveSituation.FREETEXT) },
                    onAllQuestionsAnswered = {
                        navController.navigate(Routes.LiveSituation.RESULT) {
                            popUpTo(Routes.LiveSituation.QUESTION) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.LiveSituation.FREETEXT) {
                val liveSituationGraphEntry = remember { navController.getBackStackEntry(Routes.LiveSituation.GRAPH) }
                val vm: LiveSituationViewModel = viewModel(liveSituationGraphEntry)
                val freeTextState by vm.state.collectAsStateWithLifecycle()
                FreeTextScreen(
                    domain = freeTextState.domain,
                    questionText = freeTextState.currentQuestion?.text ?: "Describe what happened",
                    onBack = { navController.popBackStack() },
                    onSubmit = { text ->
                        if (freeTextState.isGeneral) {
                            vm.submitGeneralDescription(text)
                            navController.navigate(Routes.LiveSituation.RESULT) {
                                popUpTo(Routes.LiveSituation.CATEGORY) { inclusive = false }
                            }
                        } else {
                            vm.answerCurrentQuestion(text)
                            if (vm.state.value.questionsAnswered) {
                                navController.navigate(Routes.LiveSituation.RESULT) {
                                    popUpTo(Routes.LiveSituation.QUESTION) { inclusive = true }
                                }
                            } else {
                                navController.popBackStack()
                            }
                        }
                    }
                )
            }
            composable(Routes.LiveSituation.RESULT) {
                val liveSituationGraphEntry = remember { navController.getBackStackEntry(Routes.LiveSituation.GRAPH) }
                val vm: LiveSituationViewModel = viewModel(liveSituationGraphEntry)
                ResultScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onEscalation = { navController.navigate(Routes.Escalation.route(vm.state.value.domain?.id)) },
                    onDone = {
                        navController.navigate(Routes.Home.ROOT) {
                            popUpTo(Routes.LiveSituation.GRAPH) { inclusive = true }
                        }
                    }
                )
            }
        }

        navigation(startDestination = Routes.Ask.ROOT, route = Routes.Ask.GRAPH) {
            composable(Routes.Ask.ROOT) {
                val askGraphEntry = remember { navController.getBackStackEntry(Routes.Ask.GRAPH) }
                val sessionViewModel: AskLegalAiSessionViewModel = viewModel(askGraphEntry)
                AskScreen(
                    sessionViewModel = sessionViewModel,
                    onOpenConversation = { navController.navigate(Routes.Ask.CONVERSATION) },
                    onStartVoice = { navController.navigate(Routes.Ask.VOICE) },
                    onIncidentsClick = {
                        navController.navigate(Routes.Incidents.ROOT) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Routes.Ask.VOICE) {
                val askGraphEntry = remember { navController.getBackStackEntry(Routes.Ask.GRAPH) }
                val sessionViewModel: AskLegalAiSessionViewModel = viewModel(askGraphEntry)
                VoiceScreen(
                    onClose = { navController.popBackStack() },
                    onTypeInstead = { navController.popBackStack() },
                    onSend = { text ->
                        sessionViewModel.sendMessage(text)
                        navController.navigate(Routes.Ask.CONVERSATION) {
                            popUpTo(Routes.Ask.VOICE) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.Ask.CONVERSATION) {
                val askGraphEntry = remember { navController.getBackStackEntry(Routes.Ask.GRAPH) }
                val sessionViewModel: AskLegalAiSessionViewModel = viewModel(askGraphEntry)
                ConversationScreen(
                    sessionViewModel = sessionViewModel,
                    onBack = { navController.popBackStack() },
                    onEscalation = { navController.navigate(Routes.Escalation.route(LegalDomain.CYBER.id)) },
                    onStartVoice = { navController.navigate(Routes.Ask.VOICE) }
                )
            }
        }

        composable(
            route = Routes.Escalation.PATTERN,
            arguments = listOf(navArgument("domain") { type = NavType.StringType; nullable = true; defaultValue = null })
        ) { backStackEntry ->
            val domainId = backStackEntry.arguments?.getString("domain")
            val domain = LegalDomain.entries.find { it.id == domainId }
            EscalationScreen(domain = domain, onBack = { navController.popBackStack() })
        }

        composable(Routes.Incidents.ROOT) {
            IncidentListScreen(
                onIncidentClick = { id -> navController.navigate(Routes.Incidents.detail(id)) }
            )
        }
        composable(
            route = Routes.Incidents.DETAIL,
            arguments = listOf(navArgument("incidentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val incidentId = backStackEntry.arguments?.getString("incidentId").orEmpty()
            IncidentDetailScreen(
                incidentId = incidentId,
                onBack = { navController.popBackStack() },
                onCreateComplaintDraft = { navController.navigate(Routes.Incidents.complaintDraft(incidentId)) },
                onEscalation = { domain -> navController.navigate(Routes.Escalation.route(domain.id)) },
                onDeleted = { navController.popBackStack(Routes.Incidents.ROOT, false) }
            )
        }
        composable(
            route = Routes.Incidents.COMPLAINT_DRAFT,
            arguments = listOf(navArgument("incidentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val incidentId = backStackEntry.arguments?.getString("incidentId").orEmpty()
            ComplaintDraftScreen(incidentId = incidentId, onBack = { navController.popBackStack() })
        }

        composable(Routes.Me.ROOT) {
            MeScreen(
                onNotificationsClick = { navController.navigate(Routes.NOTIFICATIONS) },
                onHelpClick = { topic -> navController.navigate(Routes.Help.route(topic.name.lowercase())) },
                onPrivacyClick = { navController.navigate(Routes.Settings.PRIVACY) },
                onDeleteDataClick = { navController.navigate(Routes.Settings.DELETE) },
                onLogout = {
                    navController.navigate(Routes.Auth.WELCOME) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(
                onBack = { navController.popBackStack() },
                onOpenIncident = { id -> navController.navigate(Routes.Incidents.detail(id)) }
            )
        }
        composable(
            route = Routes.Help.PATTERN,
            arguments = listOf(navArgument("topic") { type = NavType.StringType; nullable = true; defaultValue = null })
        ) { backStackEntry ->
            val topicArg = backStackEntry.arguments?.getString("topic")
            val topic = HelpTopic.entries.find { it.name.equals(topicArg, ignoreCase = true) } ?: HelpTopic.HOW
            HelpScreen(initialTopic = topic, onBack = { navController.popBackStack() })
        }
        composable(Routes.Settings.PRIVACY) {
            PrivacyScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.Settings.DELETE) {
            DeleteDataScreen(
                onBack = { navController.popBackStack() },
                onDeleted = {
                    navController.navigate(Routes.Auth.WELCOME) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
    }
}
