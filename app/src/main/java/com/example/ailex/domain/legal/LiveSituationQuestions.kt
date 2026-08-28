package com.example.ailex.domain.legal

import com.example.ailex.core.common.LegalDomain

/**
 * One guided-intake question. [why] is shown under the question as "why we
 * ask" — it exists so the user can see the question is not idle curiosity.
 */
data class DomainQuestion(val text: String, val why: String, val options: List<String>)

/**
 * The three-question intake set per domain, lifted verbatim from
 * design_handoff_ailex_v1's `QUESTIONS` object (`AILex Prototype.dc.html`,
 * `<script data-dc-script>` block). Content, not UI — do not paraphrase.
 */
object LiveSituationQuestionSets {
    val byDomain: Map<LegalDomain, List<DomainQuestion>> = mapOf(
        LegalDomain.TRAFFIC to listOf(
            DomainQuestion(
                text = "Has a challan actually been issued?",
                why = "Whether a challan exists changes what you should ask for next.",
                options = listOf("Yes, an e-challan or paper challan", "No, nothing has been issued", "I am not sure")
            ),
            DomainQuestion(
                text = "What is the officer asking you for?",
                why = "A cash demand is treated very differently from a document check.",
                options = listOf("Cash on the spot", "Vehicle documents", "Both", "Something else")
            ),
            DomainQuestion(
                text = "Where are you right now?",
                why = "Jurisdiction decides which procedure and which grievance route applies.",
                options = listOf("Mumbai City", "Mumbai Suburban", "Thane", "Navi Mumbai", "Somewhere else")
            )
        ),
        LegalDomain.POLICE to listOf(
            DomainQuestion(
                text = "What has the officer told you so far?",
                why = "A stop, a notice and an arrest each follow a different procedure.",
                options = listOf(
                    "They stopped me for questioning",
                    "They asked me to come to the station",
                    "They served a written notice",
                    "They said I am being arrested"
                )
            ),
            DomainQuestion(
                text = "Are you at a police station or elsewhere?",
                why = "Where this is happening affects what should be recorded.",
                options = listOf("On the street", "At a police station", "At my home or workplace")
            ),
            DomainQuestion(
                text = "Is anyone with you?",
                why = "Having a witness or informing a family member matters for a record.",
                options = listOf("Yes, someone is with me", "No, I am alone", "I could call someone")
            )
        ),
        LegalDomain.RAILWAY to listOf(
            DomainQuestion(
                text = "What is the ticket position?",
                why = "The applicable rule depends on which ticket you hold.",
                options = listOf("No ticket", "Wrong class or wrong destination", "Valid ticket, dispute anyway", "Season pass or UTS issue")
            ),
            DomainQuestion(
                text = "What is the TC or RPF asking for?",
                why = "A receipted penalty and a cash demand are not the same thing.",
                options = listOf("Cash without a receipt", "A penalty with a receipt", "My identity documents", "Nothing yet")
            ),
            DomainQuestion(
                text = "Which line are you on?",
                why = "Central, Western and Harbour lines are administered separately.",
                options = listOf("Central", "Western", "Harbour / Trans-Harbour", "Not sure")
            )
        ),
        LegalDomain.GOVERNMENT to listOf(
            DomainQuestion(
                text = "What happened with your application?",
                why = "RTS, RTI and appeal routes each have their own trigger.",
                options = listOf("It is delayed past the promised date", "It was rejected", "They refused to accept it", "They asked for extra documents")
            ),
            DomainQuestion(
                text = "Do you have an acknowledgement or receipt number?",
                why = "Almost every escalation route needs this reference.",
                options = listOf("Yes", "No", "I applied online only")
            ),
            DomainQuestion(
                text = "Was any unofficial payment mentioned?",
                why = "This changes the escalation route entirely.",
                options = listOf("Yes", "No", "It was implied")
            )
        ),
        LegalDomain.CYBER to listOf(
            DomainQuestion(
                text = "What has happened?",
                why = "Money moving out needs a different first hour than a hacked account.",
                options = listOf("Money left my account", "My account was hacked", "I am being blackmailed or threatened", "My photos or data were misused")
            ),
            DomainQuestion(
                text = "When did it happen?",
                why = "The first hours matter most for stopping or reversing a transfer.",
                options = listOf("Within the last hour", "Today", "Within the last few days", "Longer ago")
            ),
            DomainQuestion(
                text = "Have you told your bank or the platform yet?",
                why = "This tells me whether mitigation has already started.",
                options = listOf("Yes", "No", "I tried but could not reach them")
            )
        )
    )

    /**
     * 0-based index of the question whose answer becomes a saved incident's
     * location segment. Government and Cyber have no location question —
     * absent from this map means omit the segment, never substitute another
     * answer (see design_handoff_ailex_v1 §State management).
     */
    val locationQuestionIndex: Map<LegalDomain, Int> = mapOf(
        LegalDomain.TRAFFIC to 2,
        LegalDomain.POLICE to 1,
        LegalDomain.RAILWAY to 2
    )
}
