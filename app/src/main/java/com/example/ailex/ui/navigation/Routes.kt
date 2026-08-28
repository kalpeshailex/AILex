package com.example.ailex.ui.navigation

/** Single source of truth for every navigable route in the app. */
object Routes {
    object Auth {
        const val GRAPH = "auth_graph"
        const val WELCOME = "auth/welcome"
        const val MOBILE = "auth/mobile"
        const val EMAIL = "auth/email"
        const val OTP = "auth/otp"
        const val NAME = "auth/name"
        const val LANGUAGE = "auth/language"
    }

    object Home {
        const val ROOT = "home"
    }

    object LiveSituation {
        const val GRAPH = "live_situation_graph"
        const val SAFETY_PATTERN = "live_situation/safety?domainId={domainId}"
        const val URGENT = "live_situation/urgent"
        const val CATEGORY = "live_situation/category"
        const val QUESTION = "live_situation/question"
        const val FREETEXT = "live_situation/freetext"
        const val RESULT = "live_situation/result"

        /** No pre-picked domain — entry via Home's "Start live help". */
        val SAFETY = safety(null)

        /** Pre-picked domain — entry via a Home domain tile; safety-check still gates every entry. */
        fun safety(domainId: String?) = "live_situation/safety?domainId=${domainId.orEmpty()}"
    }

    object Ask {
        const val GRAPH = "ask_graph"
        const val ROOT = "ask"
        const val VOICE = "ask/voice"
        const val CONVERSATION = "ask/conversation"
    }

    object Escalation {
        const val PATTERN = "escalation?domain={domain}"
        fun route(domainId: String?) = "escalation?domain=${domainId.orEmpty()}"
    }

    object Incidents {
        const val ROOT = "incidents"
        const val DETAIL = "incidents/{incidentId}"
        const val COMPLAINT_DRAFT = "incidents/{incidentId}/complaint_draft"
        fun detail(id: String) = "incidents/$id"
        fun complaintDraft(id: String) = "incidents/$id/complaint_draft"
    }

    object Me {
        const val ROOT = "me"
    }

    const val NOTIFICATIONS = "notifications"

    object Help {
        const val PATTERN = "help?topic={topic}"
        fun route(topic: String) = "help?topic=$topic"
    }

    object Settings {
        const val PRIVACY = "settings/privacy"
        const val DELETE = "settings/delete"
    }

    val topLevelRoutes = setOf(Home.ROOT, Ask.ROOT, Incidents.ROOT, Me.ROOT)
}
