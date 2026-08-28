package com.example.ailex.domain.incident

import com.example.ailex.core.common.IncidentStatus
import com.example.ailex.core.common.LegalDomain
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.Caution500
import com.example.ailex.ui.theme.Danger600
import com.example.ailex.ui.theme.Ink400
import com.example.ailex.ui.theme.Success500

/**
 * The four seeded incidents, lifted verbatim from design_handoff_ailex_v1's
 * `INCIDENTS` array (`AILex Prototype.dc.html`, `<script data-dc-script>`
 * block). Illustrative demo content, not real user data — see the
 * handoff's "Legal content caveat".
 */
object IncidentSeedData {
    val all: List<Incident> = listOf(
        Incident(
            id = "i1",
            domain = LegalDomain.TRAFFIC,
            title = "Duplicate challan at Sion Circle",
            status = IncidentStatus.ACTIVE,
            tags = listOf("Challan dispute", "Draft ready"),
            dateLocation = "22 Aug 2026 · Sion Circle",
            savedDetail = "Saved 22 Aug 2026, 6:40 pm · Sion Circle, Mumbai",
            summary = "Two e-challans were issued for the same signal violation at Sion Circle, seven minutes apart. One has been paid. The second is still showing as pending.",
            keyFacts = listOf(
                "Category" to "Traffic — challan dispute",
                "Date and time" to "22 Aug 2026, 5:58 pm",
                "Location" to "Sion Circle, Mumbai",
                "Authority" to "Mumbai Traffic Police",
                "Challan numbers" to "2 (one paid, one pending)",
                "Reference" to "Payment ref. on file"
            ),
            timeline = listOf(
                IncidentTimelineEvent("Stopped at Sion Circle", "22 Aug 2026, 5:58 pm", Blue600, "Signal violation alleged. Challan issued on the spot."),
                IncidentTimelineEvent("First e-challan paid online", "22 Aug 2026, 6:22 pm", Success500, "Payment reference kept in evidence below."),
                IncidentTimelineEvent("Second e-challan appeared for the same offence", "22 Aug 2026, 6:29 pm", Caution500, "Same location and same time window, seven minutes apart."),
                IncidentTimelineEvent("Saved to My Incidents", "22 Aug 2026, 6:40 pm", Ink400)
            ),
            evidence = listOf(
                EvidenceRef("IMG_20260822_1802.jpg", "Photo of the challan · on this device", available = true),
                EvidenceRef("payment-receipt.pdf", "No longer available on this device", available = false)
            )
        ),
        Incident(
            id = "i2",
            domain = LegalDomain.CYBER,
            title = "UPI debit of ₹18,400 after a fake customer-care call",
            status = IncidentStatus.ACTIVE,
            tags = listOf("Bank informed", "NCRP ack. pending"),
            dateLocation = "14 Aug 2026 · Reported to 1930",
            savedDetail = "Saved 14 Aug 2026, 9:12 pm · Chembur, Mumbai",
            summary = "A caller claiming to be from the bank obtained an approval on the phone and ₹18,400 left the account through UPI. The bank was informed the same evening and helpline 1930 was called.",
            keyFacts = listOf(
                "Category" to "Cyber — UPI fraud",
                "Date and time" to "14 Aug 2026, 8:41 pm",
                "Amount" to "₹18,400",
                "Caller number" to "On file",
                "Bank complaint" to "Reference on file",
                "Helpline 1930" to "Acknowledgement received"
            ),
            timeline = listOf(
                IncidentTimelineEvent("Call received from a number claiming to be the bank", "14 Aug 2026, 8:41 pm", Danger600, "Caller said the debit card had been blocked and asked for an approval on the phone."),
                IncidentTimelineEvent("₹18,400 debited by UPI", "14 Aug 2026, 8:47 pm", Danger600, "SMS alert kept, untouched."),
                IncidentTimelineEvent("Bank informed by phone and in writing", "14 Aug 2026, 8:58 pm", Success500, "Transaction marked unauthorised. Dispute reference on file."),
                IncidentTimelineEvent("Reported on helpline 1930", "14 Aug 2026, 9:06 pm", Success500, "Acknowledgement number received."),
                IncidentTimelineEvent("NCRP portal complaint pending", "Still to do", Caution500, "The portal complaint has not been filed yet.")
            ),
            evidence = listOf(
                EvidenceRef("sms-debit-alert.png", "Bank SMS alert · on this device", available = true),
                EvidenceRef("call-log-2041.png", "Call log screenshot · on this device", available = true)
            )
        ),
        Incident(
            id = "i3",
            domain = LegalDomain.RAILWAY,
            title = "TC asked for cash without a receipt — Kurla",
            status = IncidentStatus.RESOLVED,
            tags = listOf("Receipt obtained"),
            dateLocation = "02 Aug 2026 · Central line",
            savedDetail = "Saved 02 Aug 2026, 7:55 pm · Kurla, Central line",
            summary = "A ticket checker asked for cash without a receipt. After asking to be taken to the station manager, a proper excess-fare receipt was issued and the matter closed.",
            keyFacts = listOf(
                "Category" to "Railway — penalty receipt",
                "Date and time" to "02 Aug 2026, 7:20 pm",
                "Line and station" to "Central line, Kurla",
                "Outcome" to "Receipted penalty paid",
                "Reference" to "Excess-fare ticket on file"
            ),
            timeline = listOf(
                IncidentTimelineEvent("Checked at Kurla, no valid ticket", "02 Aug 2026, 7:20 pm", Blue600, "Cash asked for, no receipt offered."),
                IncidentTimelineEvent("Asked to be taken to the station manager", "02 Aug 2026, 7:34 pm", Caution500, "Request made quietly, without an argument."),
                IncidentTimelineEvent("Receipted penalty issued and paid", "02 Aug 2026, 7:48 pm", Success500, "Excess-fare ticket issued in the correct amount."),
                IncidentTimelineEvent("Marked resolved", "02 Aug 2026, 7:55 pm", Ink400)
            ),
            evidence = listOf(
                EvidenceRef("excess-fare-receipt.jpg", "Receipt photo · on this device", available = true)
            )
        ),
        Incident(
            id = "i4",
            domain = LegalDomain.GOVERNMENT,
            title = "Complaint about a delayed income certificate",
            status = IncidentStatus.DRAFT,
            tags = listOf("RTS appeal"),
            dateLocation = "28 Jul 2026 · Not sent",
            savedDetail = "Saved 28 Jul 2026, 11:20 am · Setu centre, Kurla",
            summary = "An income certificate application is well past the published timeline. The acknowledgement number is on file and a first appeal has been drafted but not sent.",
            keyFacts = listOf(
                "Category" to "Government — RTS delay",
                "Applied on" to "19 Jun 2026",
                "Promised by" to "04 Jul 2026",
                "Days overdue" to "24 as at 28 Jul 2026",
                "Acknowledgement" to "On file",
                "Office" to "Tahsildar, Kurla"
            ),
            timeline = listOf(
                IncidentTimelineEvent("Application submitted online", "19 Jun 2026", Blue600, "Acknowledgement number received the same day."),
                IncidentTimelineEvent("Promised delivery date passed", "04 Jul 2026", Caution500, "Status still showing as under process."),
                IncidentTimelineEvent("Written status request sent to the designated officer", "21 Jul 2026", Blue600, "No reply received."),
                IncidentTimelineEvent("First appeal drafted, not sent", "28 Jul 2026", Ink400)
            ),
            evidence = listOf(
                EvidenceRef("acknowledgement.pdf", "Application acknowledgement · on this device", available = true),
                EvidenceRef("status-screenshot-2807.png", "Portal status · on this device", available = true)
            )
        )
    )
}
