package com.example.ailex.domain.conversation

import com.example.ailex.core.common.LegalDomain

/** The four example prompts on the Ask Legal AI landing screen, lifted verbatim from the prototype's `PROMPTS` array. */
data class SuggestedPrompt(val text: String, val domain: LegalDomain)

val SuggestedPrompts = listOf(
    SuggestedPrompt("Police ne mujhe roka hai, kya karu?", LegalDomain.POLICE),
    SuggestedPrompt("Can traffic police demand cash for a challan?", LegalDomain.TRAFFIC),
    SuggestedPrompt("What should I do after a UPI fraud?", LegalDomain.CYBER),
    SuggestedPrompt("My ration card application is delayed. What now?", LegalDomain.GOVERNMENT)
)
