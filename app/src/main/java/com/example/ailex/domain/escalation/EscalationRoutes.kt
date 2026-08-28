package com.example.ailex.domain.escalation

import com.example.ailex.core.common.LegalDomain

/**
 * [CALL] numbers dial via `ACTION_DIAL`. [MAIL] and [WEB] contacts are
 * deliberately not tappable — the prototype labels web routes "verify the
 * URL in the app" rather than hardcoding one that may move, and
 * `CLAUDE.md` forbids fabricating a URL. Resolve and wire real ones before
 * shipping; until then these render as reference text only.
 */
enum class ContactType { CALL, MAIL, WEB }

data class EscalationContact(val type: ContactType, val value: String, val label: String)

data class EscalationAuthority(
    val name: String,
    val role: String,
    val what: String,
    val contacts: List<EscalationContact>,
    val note: String? = null
)

/**
 * The five domain-specific escalation routes plus the legal-aid entry
 * always appended last, lifted verbatim from design_handoff_ailex_v1's
 * `ESCALATION` and `LEGAL_AID` objects (`AILex Prototype.dc.html`,
 * `<script data-dc-script>` block).
 */
object EscalationRoutes {
    val legalAid = EscalationAuthority(
        name = "Maharashtra State Legal Services Authority",
        role = "Free legal aid if you qualify",
        what = "Advice and representation at no cost for eligible citizens, through the district legal services authority.",
        contacts = listOf(EscalationContact(ContactType.CALL, "15100", "Legal aid helpline"))
    )

    private val traffic = listOf(
        EscalationAuthority(
            name = "Mumbai Traffic Police — grievance",
            role = "First stop for a challan dispute",
            what = "Raise the challan with the traffic branch that issued it. Quote the challan number and any payment reference.",
            contacts = listOf(
                EscalationContact(ContactType.WEB, "Official Mumbai Traffic Police grievance page", "Web · verify the URL in the app"),
                EscalationContact(ContactType.CALL, "103", "Traffic helpline")
            )
        ),
        EscalationAuthority(
            name = "Deputy Commissioner of Police (Traffic)",
            role = "If there is no response in a reasonable time",
            what = "A written representation to the zonal DCP (Traffic), attaching the earlier grievance reference.",
            contacts = listOf(EscalationContact(ContactType.MAIL, "Written representation with the grievance reference", "Post or in person"))
        ),
        EscalationAuthority(
            name = "Anti-Corruption Bureau, Maharashtra",
            role = "Only where cash was demanded in place of a challan",
            what = "A cash demand instead of an official challan is the ACB’s subject matter, not the traffic branch’s.",
            contacts = listOf(EscalationContact(ContactType.CALL, "1064", "ACB helpline")),
            note = "Do not attempt a trap, a sting or covert recording yourself. Report it and let the agency act."
        )
    )

    private val police = listOf(
        EscalationAuthority(
            name = "Senior officer at the same police station",
            role = "First stop for conduct or procedure",
            what = "Ask to speak to the senior inspector in charge of the station. Many procedural problems close here.",
            contacts = listOf(EscalationContact(ContactType.CALL, "100 · 112", "Police control room"))
        ),
        EscalationAuthority(
            name = "Deputy Commissioner of Police (zone)",
            role = "If the station does not resolve it",
            what = "A written complaint to the zonal DCP, naming the station, the officer and the dates.",
            contacts = listOf(EscalationContact(ContactType.MAIL, "Written complaint to the zonal DCP office", "Post or in person"))
        ),
        EscalationAuthority(
            name = "Mumbai Police Commissionerate — grievance",
            role = "Formal complaint against police conduct",
            what = "The Commissionerate accepts written and online grievances about the conduct of police personnel.",
            contacts = listOf(EscalationContact(ContactType.WEB, "Official Mumbai Police grievance channel", "Web · verify the URL in the app")),
            note = "If there is any allegation of assault or custodial ill-treatment, get a lawyer involved before filing anything."
        )
    )

    private val railway = listOf(
        EscalationAuthority(
            name = "Station manager or ticket-checking supervisor",
            role = "Fix it while you are still at the station",
            what = "A refused receipt is normally resolved on the spot by the station manager.",
            contacts = listOf(EscalationContact(ContactType.CALL, "139", "Railway enquiry and assistance"))
        ),
        EscalationAuthority(
            name = "RailMadad — railway grievance",
            role = "Official railway complaint channel",
            what = "File the complaint with the train number, coach, date, time and staff details.",
            contacts = listOf(EscalationContact(ContactType.WEB, "RailMadad grievance portal", "Web · verify the URL in the app"))
        ),
        EscalationAuthority(
            name = "Divisional Railway Manager — Central or Western Railway",
            role = "If the grievance is not addressed",
            what = "A written representation to the divisional office administering your line, attaching the grievance reference.",
            contacts = listOf(EscalationContact(ContactType.MAIL, "Divisional Railway Manager, relevant division", "Post or in person"))
        ),
        EscalationAuthority(
            name = "Railway Protection Force",
            role = "For theft, harassment or assault on railway premises",
            what = "RPF handles offences on railway property; GRP handles cognisable offences on trains and stations.",
            contacts = listOf(EscalationContact(ContactType.CALL, "182", "RPF security helpline"))
        )
    )

    private val government = listOf(
        EscalationAuthority(
            name = "Designated officer for the service",
            role = "Ask in writing first",
            what = "A written status request to the designated officer creates the record an appeal needs.",
            contacts = listOf(EscalationContact(ContactType.WEB, "Aaple Sarkar — track your application", "Web · verify the URL in the app"))
        ),
        EscalationAuthority(
            name = "First appellate authority",
            role = "Once the RTS time limit has lapsed",
            what = "A first appeal against non-delivery of a notified service, filed with the designated appellate officer.",
            contacts = listOf(EscalationContact(ContactType.MAIL, "First appeal form with the acknowledgement number", "Usually no fee"))
        ),
        EscalationAuthority(
            name = "Maharashtra State Right to Public Service Commission",
            role = "Second appeal",
            what = "Hears second appeals where the first appeal does not resolve the matter.",
            contacts = listOf(EscalationContact(ContactType.WEB, "RTS Commission, Government of Maharashtra", "Web · verify the URL in the app"))
        ),
        EscalationAuthority(
            name = "Anti-Corruption Bureau, Maharashtra",
            role = "Only where an unofficial payment was demanded",
            what = "A demand for money to move a file is the ACB’s subject matter, not the department’s.",
            contacts = listOf(EscalationContact(ContactType.CALL, "1064", "ACB helpline")),
            note = "Do not attempt a trap or a sting yourself. Report it and let the agency act."
        )
    )

    private val cyber = listOf(
        EscalationAuthority(
            name = "Cyber-crime helpline 1930",
            role = "Do this first, within the hour if you can",
            what = "The fastest route to getting a financial fraud on record so downstream accounts can be flagged.",
            contacts = listOf(EscalationContact(ContactType.CALL, "1930", "National cyber-crime financial fraud helpline"))
        ),
        EscalationAuthority(
            name = "National Cyber Crime Reporting Portal",
            role = "File the written complaint",
            what = "File with the transaction reference, the caller number and screenshots. Keep the acknowledgement number.",
            contacts = listOf(EscalationContact(ContactType.WEB, "National Cyber Crime Reporting Portal", "Web · verify the URL in the app"))
        ),
        EscalationAuthority(
            name = "Your bank’s grievance officer, then the RBI ombudsman",
            role = "For the money itself",
            what = "If the bank does not resolve the dispute, the RBI ombudsman scheme is the next step.",
            contacts = listOf(
                EscalationContact(ContactType.MAIL, "Bank grievance officer, with the dispute reference", "In writing"),
                EscalationContact(ContactType.CALL, "14448", "RBI complaint helpline")
            )
        ),
        EscalationAuthority(
            name = "Cyber police station, Mumbai",
            role = "For blackmail, sextortion or threats",
            what = "Ongoing threats, image misuse or extortion should go to the cyber police station in person.",
            contacts = listOf(EscalationContact(ContactType.CALL, "112", "Emergency response")),
            note = "If the threat involves intimate images, do not pay and do not delete anything. Report it and get a lawyer."
        )
    )

    private val byDomain: Map<LegalDomain, List<EscalationAuthority>> = mapOf(
        LegalDomain.TRAFFIC to traffic,
        LegalDomain.POLICE to police,
        LegalDomain.RAILWAY to railway,
        LegalDomain.GOVERNMENT to government,
        LegalDomain.CYBER to cyber
    )

    /** Falls back to the traffic route set for an unresolved domain, matching the prototype's own `ESCALATION[cat] || ESCALATION.traffic` default. */
    fun authoritiesFor(domain: LegalDomain?): List<EscalationAuthority> =
        (byDomain[domain] ?: byDomain.getValue(LegalDomain.TRAFFIC)) + legalAid
}
