package com.example.ailex.domain.conversation

import java.time.LocalDate

data class ConversationStep(val text: String, val note: String)
data class ConversationSource(val title: String, val excerpt: String, val authority: String, val lastVerified: LocalDate)

/**
 * The one fully-worked example conversation turn shown in `AILex Prototype.dc.html`
 * (`convoSteps`/`convoSources`/`convoChips`), lifted verbatim. This is the app's
 * single fixed, illustrative Ask Legal AI reply — see `AskLegalAiSessionViewModel`
 * for how and when it's shown. Not generated, not tailored to what the user asked.
 */
object UpiFraudDemoTurn {
    const val Topic = "UPI fraud"
    const val UrgencyKicker = "Act in the next few minutes"
    const val UrgencyBody =
        "Money that has just moved is sometimes still recoverable. Reporting speed matters more than anything else right now."
    const val AnswerIntro =
        "Based on what you've told me, this looks like a fake customer-care fraud with an outgoing UPI transfer. Do these four things in order."

    val Steps = listOf(
        ConversationStep(
            "Call your bank’s official number and report an unauthorised transaction.",
            "Use the number on your card or passbook, never a number from the caller."
        ),
        ConversationStep(
            "Report on the national cyber-crime helpline 1930 and get an acknowledgement number.",
            "The sooner a report is on record, the better the chance of a hold on the money."
        ),
        ConversationStep(
            "File the complaint on the National Cyber Crime Reporting Portal as well.",
            "Keep the acknowledgement. Every later step will ask for it."
        ),
        ConversationStep(
            "Freeze or change UPI access on the affected account.",
            "Through your bank or the UPI app’s own security settings."
        )
    )

    const val WarningNoShare =
        "Do not share any OTP, PIN, CVV or UPI PIN with anyone, including someone claiming to be from the bank or the police."
    const val WarningPreserve =
        "Keep the caller's number, the SMS alerts, the UPI reference number and the exact time."

    val Sources = listOf(
        ConversationSource(
            title = "RBI — customer liability in unauthorised electronic transactions",
            excerpt = "Customer liability in an unauthorised electronic banking transaction depends on the type of negligence and how quickly the customer notifies the bank.",
            authority = "Reserve Bank of India",
            lastVerified = LocalDate.of(2026, 8, 9)
        ),
        ConversationSource(
            title = "National Cyber Crime Reporting Portal and helpline 1930",
            excerpt = "Official route for reporting cyber financial fraud, operated with the Ministry of Home Affairs.",
            authority = "Government of India",
            lastVerified = LocalDate.of(2026, 8, 9)
        )
    )

    const val FollowUpQuestion =
        "One more fact changes what happens next: did you approve anything on your phone — a UPI PIN, a collect request, or an app install?"
    val FollowUpChips = listOf(
        "I entered my UPI PIN",
        "I only shared an OTP",
        "I installed an app they sent",
        "I am not sure"
    )
}
