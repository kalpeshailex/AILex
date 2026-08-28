package com.example.ailex.core.common

import androidx.compose.ui.graphics.Color
import com.example.ailex.ui.theme.DomainAccents

/**
 * Single source of truth for the five domains supported in V1 — used by
 * Home's grid, the Live Situation category screen, and incident tinting.
 * [tileBackground] is a pale wash suitable as a large fill; [accentColor] is
 * the saturated counterpart used for icon tints and badges, where a pale
 * wash-on-wash combination would be nearly invisible.
 *
 * Colors and blurbs match design_handoff_ailex_v1's domain accent table.
 */
enum class LegalDomain(
    val id: String,
    val displayName: String,
    val description: String,
    val tileBackground: Color,
    val accentColor: Color
) {
    POLICE("police", "Police", "Stops, notices, FIRs, arrest", DomainAccents.Police.tint, DomainAccents.Police.ink),
    TRAFFIC("traffic", "Traffic", "Challans, towing, documents", DomainAccents.Traffic.tint, DomainAccents.Traffic.ink),
    RAILWAY("railway", "Mumbai Local", "Tickets, TC, RPF, theft", DomainAccents.MumbaiLocal.tint, DomainAccents.MumbaiLocal.ink),
    GOVERNMENT("government", "Government / RTS", "Delays, refusals, appeals", DomainAccents.Government.tint, DomainAccents.Government.ink),
    CYBER("cyber", "Cyber", "Fraud, hacking, harassment", DomainAccents.Cyber.tint, DomainAccents.Cyber.ink)
}
