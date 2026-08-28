package com.example.ailex.features.settings

/** One tab's worth of Q&A-style entries on the Understanding AILex screen. */
data class HelpEntry(val title: String, val body: String)

enum class HelpTopic(val label: String) { HOW("How Legal AI works"), LIMITS("What it cannot do"), FAQ("Common questions") }

/**
 * Understanding AILex's three tabs, lifted verbatim from
 * design_handoff_ailex_v1's `HELP` object (`AILex Prototype.dc.html`,
 * `<script data-dc-script>` block).
 */
object HelpContent {
    val byTopic: Map<HelpTopic, List<HelpEntry>> = mapOf(
        HelpTopic.HOW to listOf(
            HelpEntry(
                "It answers from verified sources, not from memory",
                "Every answer is built against a stored set of legal provisions and official pages, each with a date on which it was last verified. Where a provision has not been verified, the app says so instead of guessing."
            ),
            HelpEntry(
                "It asks only what changes the answer",
                "Questions are limited to the facts that move the guidance — whether a challan exists, which line you are on, how long ago money left your account. Your name, licence number and vehicle number are not asked for unless they matter."
            ),
            HelpEntry(
                "Rights, obligations and powers are kept apart",
                "Most confusion in a live situation comes from mixing these three. The app separates what you may do, what you are required to do, and what the authority may lawfully do."
            ),
            HelpEntry(
                "It will not tell you a fine amount it cannot verify",
                "Penalty figures change by notification. Where the current official figure is not verified, the app describes the route rather than quoting a number."
            )
        ),
        HelpTopic.LIMITS to listOf(
            HelpEntry(
                "It is not your lawyer",
                "There is no lawyer-client relationship, no privilege, and no representation. For an arrest, a serious accusation, a large loss or a deadline you could miss, get a lawyer."
            ),
            HelpEntry(
                "It does not file anything for you",
                "Complaint drafts are drafts. Nothing is submitted to any authority by the app."
            ),
            HelpEntry(
                "It covers Mumbai and MMR only",
                "Guidance is built for Mumbai City, Mumbai Suburban, Thane and Navi Mumbai. Outside these areas the app says so rather than applying Maharashtra rules to you."
            ),
            HelpEntry(
                "It cannot judge who is telling the truth",
                "The app works from what you tell it. If a fact changes, the guidance can change with it."
            )
        ),
        HelpTopic.FAQ to listOf(
            HelpEntry(
                "Is my conversation saved?",
                "No, not on its own. A conversation becomes an incident only when you choose to save it, and saved incidents stay on this device."
            ),
            HelpEntry(
                "Can I use this while being stopped?",
                "Yes, that is what live help is for. Safety comes before the app: if the situation is unsafe, deal with that first and call 112."
            ),
            HelpEntry(
                "Should I record the officer?",
                "The app does not advise covert recording. Note names, numbers, times and places openly instead."
            ),
            HelpEntry(
                "What if the answer is wrong?",
                "Use the report button on any answer. A reported answer is reviewed against the source it was built from."
            ),
            HelpEntry(
                "Does it work in Hindi and Marathi?",
                "You can ask in English, Hindi or Marathi, by text or by voice. The set of laws covered does not change with the language."
            )
        )
    )
}
