package com.example.ailex.domain.complaint

import com.example.ailex.core.common.LegalDomain

/** One section of a generated complaint draft — "To", "Subject", "Background", etc. */
data class DraftSection(val label: String, val text: String)

/**
 * The five domain-specific complaint drafts, lifted verbatim from
 * design_handoff_ailex_v1's `DRAFTS` and `DRAFT` objects
 * (`AILex Prototype.dc.html`, `<script data-dc-script>` block). Illustrative
 * demo content generated for the seeded incidents — see the handoff's
 * "Legal content caveat". Square-bracketed placeholders are intentional;
 * the draft-only banner tells the user to fill them in before sharing.
 */
object ComplaintDraftTemplates {
    private val traffic = listOf(
        DraftSection("To", "The Deputy Commissioner of Police (Traffic)\nMumbai Traffic Police"),
        DraftSection("Subject", "Duplicate e-challan issued for a single alleged signal violation at Sion Circle on 22 August 2026"),
        DraftSection("Background", "I am the registered owner of the vehicle bearing registration number [vehicle number]. On 22 August 2026 at approximately 5:58 pm I was stopped at Sion Circle, Mumbai, and a challan was issued for an alleged signal violation."),
        DraftSection("Facts", "1. A challan was issued at the spot on 22 August 2026 at about 5:58 pm.\n2. I paid the corresponding e-challan online at about 6:22 pm the same day. The payment reference is [payment reference].\n3. At about 6:29 pm a second e-challan appeared against the same vehicle for the same alleged offence at the same location.\n4. Both challans relate to a single incident. Only one has been paid, and the second is still shown as pending."),
        DraftSection("Requested action", "I request that the duplicate e-challan be examined and withdrawn, and that written confirmation be provided once the record is corrected."),
        DraftSection("Supporting information", "Copies of both challans, the online payment receipt and the SMS alerts are available and can be produced on request."),
        DraftSection("Contact", "[Your name]\n[Mobile number]\nDate: 26 August 2026")
    )

    private val cyber = listOf(
        DraftSection("To", "The Grievance Redressal Officer\n[Bank name], Mumbai"),
        DraftSection("Subject", "Unauthorised UPI debit of ₹18,400 on 14 August 2026 and request for reversal"),
        DraftSection("Background", "I hold account number [account number] with your branch at [branch]. On 14 August 2026 at about 8:41 pm I received a call from a person claiming to be from your customer-care team, stating that my debit card had been blocked."),
        DraftSection("Facts", "1. During the call I was directed through steps on my phone which I now understand were used to authorise a transfer.\n2. At about 8:47 pm an amount of ₹18,400 was debited from my account by UPI. The transaction reference is [UPI reference].\n3. I reported the transaction to the bank the same evening at about 8:58 pm. The dispute reference is [dispute reference].\n4. I reported the matter on the national cyber-crime helpline 1930 at about 9:06 pm and hold the acknowledgement number [1930 acknowledgement]."),
        DraftSection("Requested action", "I request that the transaction be treated as unauthorised, that the amount be reversed in accordance with the applicable Reserve Bank of India directions on customer liability, and that I be informed in writing of the outcome and the reasons for it."),
        DraftSection("Supporting information", "The SMS debit alert, the call log entry for the caller’s number and the helpline acknowledgement are available and can be produced on request."),
        DraftSection("Contact", "[Your name]\n[Mobile number]\nDate: 27 August 2026")
    )

    private val railway = listOf(
        DraftSection("To", "The Divisional Railway Manager\nCentral Railway, Mumbai Division"),
        DraftSection("Subject", "Penalty demanded without a receipt during ticket checking at Kurla on 2 August 2026"),
        DraftSection("Background", "I was travelling on the Central line and was checked at Kurla station on 2 August 2026 at about 7:20 pm. I did not hold a valid ticket for the journey and accepted that a penalty was payable."),
        DraftSection("Facts", "1. The checking staff asked for cash and did not offer a receipt or an excess-fare ticket.\n2. I stated that I was willing to pay the penalty against an official receipt.\n3. On my request the matter was taken to the station manager, and a receipted penalty was issued at about 7:48 pm.\n4. The excess-fare ticket number is [ticket number]."),
        DraftSection("Requested action", "I request that this be recorded, and that checking staff on this section be reminded that penalties are to be collected only against an official receipt. I am not seeking a refund of the penalty, which was properly payable."),
        DraftSection("Supporting information", "A photograph of the excess-fare receipt is available and can be produced on request."),
        DraftSection("Contact", "[Your name]\n[Mobile number]\nDate: 27 August 2026")
    )

    private val government = listOf(
        DraftSection("To", "The First Appellate Authority\nUnder the Maharashtra Right to Public Services Act, 2015\nOffice of the Tahsildar, Kurla"),
        DraftSection("Subject", "First appeal for non-delivery of an income certificate within the stipulated time limit"),
        DraftSection("Background", "I applied for an income certificate on 19 June 2026 through the official portal. The acknowledgement number is [acknowledgement number]. The service is notified under the Maharashtra Right to Public Services Act, 2015."),
        DraftSection("Facts", "1. The application was submitted on 19 June 2026 and an acknowledgement was issued the same day.\n2. The published time limit for this service expired on 4 July 2026.\n3. The online status has continued to show the application as under process.\n4. I sent a written status request to the designated officer on 21 July 2026 and have received no reply."),
        DraftSection("Requested action", "I request that the designated officer be directed to deliver the service, and that I be informed in writing of the reason for the delay."),
        DraftSection("Supporting information", "A copy of the acknowledgement, the portal status screenshot and the written status request are available and can be produced on request."),
        DraftSection("Contact", "[Your name]\n[Mobile number]\nDate: 27 August 2026")
    )

    private val police = listOf(
        DraftSection("To", "The Deputy Commissioner of Police\n[Zone], Mumbai"),
        DraftSection("Subject", "Representation regarding the procedure followed at [police station] on [date]"),
        DraftSection("Background", "I was approached by police personnel on [date] at about [time] at [place] and asked to attend [police station]. No written notice was served on me at that time."),
        DraftSection("Facts", "1. I was stopped at [place] on [date] at about [time].\n2. I was asked to attend [police station] and was not told the subject matter.\n3. I asked for the request in writing and none was provided.\n4. The officer’s name and buckle number, as noted by me, are [details]."),
        DraftSection("Requested action", "I request that the position be examined, and that I be informed in writing whether I am required to attend and under which provision."),
        DraftSection("Supporting information", "The name and number of the person who was present with me are available and can be produced on request."),
        DraftSection("Contact", "[Your name]\n[Mobile number]\nDate: 27 August 2026")
    )

    val byDomain: Map<LegalDomain, List<DraftSection>> = mapOf(
        LegalDomain.TRAFFIC to traffic,
        LegalDomain.CYBER to cyber,
        LegalDomain.RAILWAY to railway,
        LegalDomain.GOVERNMENT to government,
        LegalDomain.POLICE to police
    )
}
