package com.example.ailex.domain.legal

import com.example.ailex.core.common.LegalDomain
import java.time.LocalDate

/**
 * The five domain results, lifted verbatim from design_handoff_ailex_v1's
 * `RESULTS` object (`AILex Prototype.dc.html`, `<script data-dc-script>`
 * block). This is illustrative content written for layout review, not
 * verified legal drafting — see the handoff's "Legal content caveat". Do
 * not paraphrase; the wording distinguishes what is verified from what is
 * situational, and that distinction is the product.
 */
object LiveSituationResults {

    private val traffic = SituationResult(
        domain = LegalDomain.TRAFFIC,
        title = "Traffic stop — cash demanded",
        risk = RiskLevel.HIGH,
        safetyNote = "Stay in your vehicle if the road is unsafe. Do not argue, drive off, or record covertly. Keep your hands visible and your tone even.",
        situationSummary = "You were stopped in Mumbai City. No challan has been issued yet, and the officer is asking for cash rather than putting the offence on record.",
        factChips = listOf("Traffic stop", "No challan issued", "Cash requested", "Mumbai City"),
        escalationBlurb = "Mumbai Traffic Police grievance and ACB Maharashtra",
        actionSteps = listOf(
            ActionStep("Stay calm and stay put.", "Do not drive away and do not raise your voice. Nothing here is worth an obstruction allegation."),
            ActionStep("Ask for the officer’s name and buckle number, and note the time and location.", "You can write this in your phone notes in the open. No covert recording."),
            ActionStep("Ask for a challan and say you will pay any fine through the official e-challan route.", "This is the single most useful sentence in this situation."),
            ActionStep("If a challan is issued, check that the offence, the section and the amount are printed on it before you accept it."),
            ActionStep("Keep the challan or the e-challan reference number safe.", "You will need it for any dispute or grievance.")
        ),
        sections = listOf(
            ResultSection(
                id = SectionId.POSITION, title = "Your position", meta = "What you may be required to do",
                items = listOf(
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "You must stop when signalled by a uniformed officer in the course of duty."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "You are generally required to produce your driving licence when asked, and to produce registration, insurance and PUC as required."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "If an offence did occur, a challan is the lawful outcome. Asking for a challan is not an admission of anything beyond what is on it.")
                ),
                caveat = "What exactly you must carry versus produce later can depend on the document and the vehicle. Check the official source before relying on it."
            ),
            ResultSection(
                id = SectionId.RIGHTS, title = "Your rights", meta = "Procedural protections that may apply",
                items = listOf(
                    SectionItem(SectionItemIcon.CHECK, "You may ask the officer to identify themselves."),
                    SectionItem(SectionItemIcon.CHECK, "You may ask for the offence and the provision it is booked under to be stated on the challan."),
                    SectionItem(SectionItemIcon.CHECK, "You may pay through the official e-challan system rather than in cash on the road."),
                    SectionItem(SectionItemIcon.CHECK, "You may dispute a challan you believe is wrong through the official grievance or court route.")
                )
            ),
            ResultSection(
                id = SectionId.POWERS, title = "Authority powers", meta = "What the officer may lawfully do",
                items = listOf(
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Stop your vehicle and check documents."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Issue a challan for an offence that is made out."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "In specified circumstances, seize documents or the vehicle, or require it to be towed.")
                ),
                caveat = "Seizure and towing powers are situation-specific. Do not assume a seizure is improper simply because you disagree with it."
            ),
            ResultSection(
                id = SectionId.IMPROPER, title = "What should not happen", meta = "Only where the position is clear",
                items = listOf(
                    SectionItem(SectionItemIcon.CLOSE, "A demand for cash in place of an official challan is not a lawful way to settle a traffic offence."),
                    SectionItem(SectionItemIcon.CLOSE, "A penalty collected with no receipt and no record leaves you with nothing to dispute later.")
                )
            ),
            ResultSection(
                id = SectionId.AVOID, title = "Avoid", meta = "Actions that make this worse",
                items = listOf(
                    SectionItem(SectionItemIcon.CLOSE, "Do not pay cash to avoid a challan."),
                    SectionItem(SectionItemIcon.CLOSE, "Do not argue, threaten to complain, or record covertly."),
                    SectionItem(SectionItemIcon.CLOSE, "Do not drive away, and do not obstruct the officer."),
                    SectionItem(SectionItemIcon.CLOSE, "Do not hand over original documents without noting what was taken.")
                )
            ),
            ResultSection(
                id = SectionId.PRESERVE, title = "Preserve", meta = "Keep this while it is fresh",
                items = listOf(
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Date, time and exact location of the stop."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Officer name and buckle number if given."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Challan number or e-challan reference."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Names and numbers of anyone who saw the stop.")
                )
            ),
            ResultSection(
                id = SectionId.LEGAL, title = "Legal basis", meta = "2 verified sources",
                items = emptyList(),
                caveat = "Exact fine amounts change by notification and are not shown here unless verified against the current official figure.",
                sources = listOf(
                    LegalSource(
                        title = "Motor Vehicles Act, 1988 — Section 130",
                        excerpt = "Duty of a driver to produce a driving licence for examination on demand by an authorised officer.",
                        authority = "India Code", lastVerified = LocalDate.of(2026, 8, 12)
                    ),
                    LegalSource(
                        title = "Maharashtra e-challan system — official payment route",
                        excerpt = "Traffic fines in Maharashtra are payable against a challan through the official e-challan portal.",
                        authority = "Maharashtra Traffic Police", lastVerified = LocalDate.of(2026, 8, 4)
                    )
                )
            )
        )
    )

    private val police = SituationResult(
        domain = LegalDomain.POLICE,
        title = "Police asked me to come to the station",
        risk = RiskLevel.HIGH,
        safetyNote = "Do not resist, argue or walk away. Tell one family member or friend where you are going and who asked you, before you go anywhere.",
        situationSummary = "You were stopped on the street in Mumbai and asked to come to a police station. Nothing has been served in writing yet and someone is with you.",
        factChips = listOf("Police interaction", "Asked to attend station", "On the street", "Witness present"),
        escalationBlurb = "Senior officer, then the Police Commissionerate",
        actionSteps = listOf(
            ActionStep("Ask what this is about, and ask for it in writing.", "A written notice or summons tells you who is calling you, under what provision, and when. Ask politely; do not demand."),
            ActionStep("Note the officer’s name, rank, buckle number and the police station.", "Write it in your phone in the open. No covert recording."),
            ActionStep("Tell someone where you are going before you move.", "Give them the station name and the officer’s name. Ask them to call you in an hour."),
            ActionStep("Go if you are required to. Take an ID and, if you can, someone with you.", "Attending when required is not an admission of anything."),
            ActionStep("If arrest is mentioned at any point, ask to inform a family member and to speak to a lawyer.", "Free legal aid is available through the district legal services authority.")
        ),
        sections = listOf(
            ResultSection(
                id = SectionId.POSITION, title = "Your position", meta = "What you may be required to do",
                items = listOf(
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "If you are required to attend, attending is generally the safer course. Refusing to attend can have consequences of its own."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "You are expected not to obstruct an officer acting in the course of duty."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Whether you must answer a particular question, and whether you can be required to attend at all, depends on your role in the matter.")
                ),
                caveat = "Whether you are a witness, a suspect or neither changes this materially. Say clearly that you do not know which you are, and ask."
            ),
            ResultSection(
                id = SectionId.RIGHTS, title = "Your rights", meta = "Procedural protections that may apply",
                items = listOf(
                    SectionItem(SectionItemIcon.CHECK, "You may ask why you are being called and ask for a written notice."),
                    SectionItem(SectionItemIcon.CHECK, "You may ask the officer to identify themselves and note their details."),
                    SectionItem(SectionItemIcon.CHECK, "You may inform a family member or friend."),
                    SectionItem(SectionItemIcon.CHECK, "If you are arrested, protections around being informed of the grounds, informing a relative and consulting a lawyer apply.")
                )
            ),
            ResultSection(
                id = SectionId.POWERS, title = "Authority powers", meta = "What the police may lawfully do",
                items = listOf(
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Require attendance in specified circumstances, by notice or summons."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Question a person in connection with an investigation."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Arrest without a warrant in the circumstances the law permits.")
                ),
                caveat = "These powers are procedure-bound and situation-specific. Do not assume any single step is improper because you disagree with it."
            ),
            ResultSection(
                id = SectionId.IMPROPER, title = "What should not happen", meta = "Only where the position is clear",
                items = listOf(
                    SectionItem(SectionItemIcon.CLOSE, "A demand for money to drop, delay or soften a matter is not a lawful step at any stage."),
                    SectionItem(SectionItemIcon.CLOSE, "Threats, abuse or physical force against a person in custody are not permitted.")
                )
            ),
            ResultSection(
                id = SectionId.AVOID, title = "Avoid", meta = "Actions that make this worse",
                items = listOf(
                    SectionItem(SectionItemIcon.CLOSE, "Do not resist, run, or physically obstruct anyone."),
                    SectionItem(SectionItemIcon.CLOSE, "Do not pay anyone to make this go away."),
                    SectionItem(SectionItemIcon.CLOSE, "Do not sign a document you have not read, and do not sign a blank page."),
                    SectionItem(SectionItemIcon.CLOSE, "Do not guess at facts to fill a silence. \"I do not remember\" is an answer.")
                )
            ),
            ResultSection(
                id = SectionId.PRESERVE, title = "Preserve", meta = "Keep this while it is fresh",
                items = listOf(
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Officer name, rank, buckle number and station."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Date, time and place you were stopped, and when you were asked to attend."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "A photo of any notice served on you."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Name and number of the person who was with you.")
                )
            ),
            ResultSection(
                id = SectionId.LEGAL, title = "Legal basis", meta = "2 verified sources",
                items = emptyList(),
                caveat = "Which provision applies depends on whether you are a witness or an accused, and on the offence alleged. Verify against the official text before relying on it.",
                sources = listOf(
                    LegalSource(
                        title = "Bharatiya Nagarik Suraksha Sanhita, 2023 — attendance of witnesses",
                        excerpt = "A police officer may, by written order, require the attendance of a person who appears to be acquainted with the facts of a case, subject to the limits the provision sets.",
                        authority = "India Code", lastVerified = LocalDate.of(2026, 8, 11)
                    ),
                    LegalSource(
                        title = "Constitution of India — Article 22",
                        excerpt = "A person arrested is to be informed of the grounds of arrest and is not to be denied the right to consult a legal practitioner.",
                        authority = "India Code", lastVerified = LocalDate.of(2026, 8, 11)
                    )
                )
            )
        )
    )

    private val railway = SituationResult(
        domain = LegalDomain.RAILWAY,
        title = "TC asking for cash without a receipt",
        risk = RiskLevel.STANDARD,
        safetyNote = "Stay on the platform or in a lit public area. Do not get off at an unfamiliar station to settle this.",
        situationSummary = "You are travelling on the Harbour line without a ticket, and the TC is asking for cash rather than issuing a receipted penalty.",
        factChips = listOf("Railway", "No ticket", "Cash requested, no receipt", "Harbour line"),
        escalationBlurb = "Station manager, then the divisional railway office",
        actionSteps = listOf(
            ActionStep("Accept that a penalty is payable, and ask for it on an official receipt.", "Travelling without a valid ticket does attract a penalty. Asking for the receipt is the point, not avoiding the fare."),
            ActionStep("Say clearly that you will pay the penalty, but only against a receipt.", "Keep it factual and quiet. No crowd, no argument."),
            ActionStep("Note the TC’s name and number from their badge, the train, and the time."),
            ActionStep("If the receipt is refused, ask to be taken to the station manager or the ticket-checking supervisor.", "This is a normal request, not an escalation against anyone."),
            ActionStep("Keep the receipt. It closes the matter and is your only proof if it comes up again.")
        ),
        sections = listOf(
            ResultSection(
                id = SectionId.POSITION, title = "Your position", meta = "What you may be required to do",
                items = listOf(
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Travelling without a valid ticket is an offence under railway law, and an excess fare and penalty are payable."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "You may be required to give your name and address to the checking staff."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Refusing to pay a lawfully assessed penalty can lead to further proceedings.")
                ),
                caveat = "The exact penalty depends on the class, the distance and the current rules. Amounts are not shown here unless verified against the official figure."
            ),
            ResultSection(
                id = SectionId.RIGHTS, title = "Your rights", meta = "Procedural protections that may apply",
                items = listOf(
                    SectionItem(SectionItemIcon.CHECK, "You may ask the checking staff to identify themselves."),
                    SectionItem(SectionItemIcon.CHECK, "You may ask for an official receipt or excess-fare ticket for anything you pay."),
                    SectionItem(SectionItemIcon.CHECK, "You may ask to be taken to the station manager if a receipt is refused.")
                )
            ),
            ResultSection(
                id = SectionId.POWERS, title = "Authority powers", meta = "What the TC and RPF may lawfully do",
                items = listOf(
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Check tickets and require you to produce a valid ticket or pass."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Assess and collect the excess fare and penalty, against a receipt."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Remove a person from a train or hand the matter on where the rules permit.")
                )
            ),
            ResultSection(
                id = SectionId.IMPROPER, title = "What should not happen", meta = "Only where the position is clear",
                items = listOf(
                    SectionItem(SectionItemIcon.CLOSE, "A payment collected with no receipt and no record is not a lawful settlement of a railway penalty."),
                    SectionItem(SectionItemIcon.CLOSE, "A penalty amount that changes depending on what you are willing to pay is not an assessed penalty.")
                )
            ),
            ResultSection(
                id = SectionId.AVOID, title = "Avoid", meta = "Actions that make this worse",
                items = listOf(
                    SectionItem(SectionItemIcon.CLOSE, "Do not pay cash without a receipt."),
                    SectionItem(SectionItemIcon.CLOSE, "Do not run, jump between coaches, or get off at a deserted station."),
                    SectionItem(SectionItemIcon.CLOSE, "Do not argue loudly or draw a crowd."),
                    SectionItem(SectionItemIcon.CLOSE, "Do not give a false name or a false address.")
                )
            ),
            ResultSection(
                id = SectionId.PRESERVE, title = "Preserve", meta = "Keep this while it is fresh",
                items = listOf(
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "The receipt or excess-fare ticket."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Train number, coach, station and time."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "TC name and badge number."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Names of any fellow passengers who saw it.")
                )
            ),
            ResultSection(
                id = SectionId.LEGAL, title = "Legal basis", meta = "2 verified sources",
                items = emptyList(),
                caveat = "Central, Western and Harbour lines are administered separately and grievance routes differ by division.",
                sources = listOf(
                    LegalSource(
                        title = "Railways Act, 1989 — travelling without a proper pass or ticket",
                        excerpt = "A person travelling without a valid pass or ticket is liable to pay the excess fare together with a penalty as provided.",
                        authority = "India Code", lastVerified = LocalDate.of(2026, 8, 7)
                    ),
                    LegalSource(
                        title = "Indian Railways — ticket checking and excess-fare receipts",
                        excerpt = "Excess fare and penalty collected by checking staff are to be issued against an official excess-fare ticket or receipt.",
                        authority = "Indian Railways", lastVerified = LocalDate.of(2026, 8, 7)
                    )
                )
            )
        )
    )

    private val government = SituationResult(
        domain = LegalDomain.GOVERNMENT,
        title = "Application delayed past the promised date",
        risk = RiskLevel.STANDARD,
        safetyNote = null,
        situationSummary = "A government application is past the date you were promised, you hold an acknowledgement number, and no unofficial payment has been mentioned.",
        factChips = listOf("Government / RTS", "Delayed past due date", "Acknowledgement held", "No payment demanded"),
        escalationBlurb = "First appellate authority, then the RTS Commission",
        actionSteps = listOf(
            ActionStep("Check the promised timeline for this exact service and note how many days it is overdue.", "Most Maharashtra RTS services publish a stated number of days. The overdue count is what an appeal turns on."),
            ActionStep("Track the application online using your acknowledgement number and take a screenshot of the current status."),
            ActionStep("Ask the designated officer in writing for the current status and the reason for the delay.", "A written request creates the record an appeal needs. Email or a stamped letter both work."),
            ActionStep("If the service falls under RTS and the period has lapsed, file a first appeal with the designated appellate officer.", "The appeal is a form, not a court case. There is usually no fee."),
            ActionStep("Keep every acknowledgement, receipt and screenshot together.")
        ),
        sections = listOf(
            ResultSection(
                id = SectionId.POSITION, title = "Your position", meta = "What is expected of you",
                items = listOf(
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "The application needs to be complete. A genuine document shortfall is a legitimate reason for delay."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Escalation routes normally run in order: designated officer, first appellate authority, then above."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Keep your acknowledgement number. Almost nothing can be pursued without it.")
                ),
                caveat = "Whether your specific service is notified under the Maharashtra RTS Act decides which route applies. Verify the service on the official list."
            ),
            ResultSection(
                id = SectionId.RIGHTS, title = "Your rights", meta = "Procedural protections that may apply",
                items = listOf(
                    SectionItem(SectionItemIcon.CHECK, "For a notified service, you may expect it within the published time limit."),
                    SectionItem(SectionItemIcon.CHECK, "You may ask for a written reason if your application is rejected."),
                    SectionItem(SectionItemIcon.CHECK, "You may appeal to the designated first appellate authority when the period lapses."),
                    SectionItem(SectionItemIcon.CHECK, "You may seek related records through the RTI route where appropriate.")
                )
            ),
            ResultSection(
                id = SectionId.POWERS, title = "Department powers", meta = "What the office may lawfully do",
                items = listOf(
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Ask for documents the service genuinely requires."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Reject an application, with reasons recorded."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Seek verification or an inspection where the service provides for it.")
                )
            ),
            ResultSection(
                id = SectionId.IMPROPER, title = "What should not happen", meta = "Only where the position is clear",
                items = listOf(
                    SectionItem(SectionItemIcon.CLOSE, "A demand for an unofficial payment to move a file is not a lawful step at any stage."),
                    SectionItem(SectionItemIcon.CLOSE, "A refusal to accept a complete application, or to give any acknowledgement, leaves you with nothing to appeal against.")
                )
            ),
            ResultSection(
                id = SectionId.AVOID, title = "Avoid", meta = "Actions that make this worse",
                items = listOf(
                    SectionItem(SectionItemIcon.CLOSE, "Do not pay an agent or a middleman to speed up a file."),
                    SectionItem(SectionItemIcon.CLOSE, "Do not file a fresh application for the same thing. It resets the clock and confuses the record."),
                    SectionItem(SectionItemIcon.CLOSE, "Do not go straight to the police. A service delay is a departmental matter.")
                )
            ),
            ResultSection(
                id = SectionId.PRESERVE, title = "Preserve", meta = "Keep this while it is fresh",
                items = listOf(
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Acknowledgement or receipt number and the date of application."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Screenshots of the online status, with dates."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Copies of everything you submitted."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Names and designations of anyone you spoke to, with dates.")
                )
            ),
            ResultSection(
                id = SectionId.LEGAL, title = "Legal basis", meta = "2 verified sources",
                items = emptyList(),
                caveat = "Time limits differ by service. Confirm the number of days published for your exact service before counting the delay.",
                sources = listOf(
                    LegalSource(
                        title = "Maharashtra Right to Public Services Act, 2015",
                        excerpt = "Notified public services are to be delivered within the stipulated time limit, with a first and second appeal against failure to do so.",
                        authority = "Government of Maharashtra", lastVerified = LocalDate.of(2026, 8, 5)
                    ),
                    LegalSource(
                        title = "Aaple Sarkar — service list and application tracking",
                        excerpt = "Official portal for applying for and tracking notified Maharashtra public services.",
                        authority = "Government of Maharashtra", lastVerified = LocalDate.of(2026, 8, 5)
                    )
                )
            )
        )
    )

    private val cyber = SituationResult(
        domain = LegalDomain.CYBER,
        title = "Money left my account — UPI fraud",
        risk = RiskLevel.HIGH,
        safetyNote = "Do not call back the number that called you, and do not install anything they sent. Do not share an OTP, PIN, CVV or UPI PIN with anyone, including someone claiming to be from the bank or the police.",
        situationSummary = "Money left your account through UPI within the last hour, and you have already told your bank. Speed matters more than anything else right now.",
        factChips = listOf("Cyber", "Outgoing UPI transfer", "Within the last hour", "Bank informed"),
        escalationBlurb = "Helpline 1930, NCRP, then the cyber police station",
        actionSteps = listOf(
            ActionStep("Report on the national cyber-crime helpline 1930 and get an acknowledgement number.", "The sooner a report is on record, the better the chance of a hold on the money downstream."),
            ActionStep("File the complaint on the National Cyber Crime Reporting Portal as well.", "Keep the acknowledgement. Every later step will ask for it."),
            ActionStep("Ask your bank in writing to mark the transaction unauthorised, and note the complaint reference.", "A phone call alone leaves no record. Follow it with the bank’s own online dispute form."),
            ActionStep("Freeze or change UPI access on the affected account.", "Through your bank, or the UPI app’s own security settings."),
            ActionStep("Change the passwords and enable two-factor login on the email and phone number linked to the account.")
        ),
        sections = listOf(
            ResultSection(
                id = SectionId.POSITION, title = "Your position", meta = "What affects the outcome",
                items = listOf(
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "How quickly you notify the bank is the single biggest factor in what you may recover."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Whether you approved anything on your phone — a UPI PIN, a collect request, an app install — affects how the transaction is treated."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Answer the bank’s questions accurately, including anything you did approve. An inaccurate account weakens a genuine claim.")
                ),
                caveat = "Liability in an unauthorised electronic transaction turns on the type of negligence and the notification timeline. Verify your position with your bank in writing."
            ),
            ResultSection(
                id = SectionId.RIGHTS, title = "Your rights", meta = "Procedural protections that may apply",
                items = listOf(
                    SectionItem(SectionItemIcon.CHECK, "You may report an unauthorised electronic transaction to your bank and have it examined."),
                    SectionItem(SectionItemIcon.CHECK, "You may file a cyber-crime complaint and receive an acknowledgement."),
                    SectionItem(SectionItemIcon.CHECK, "You may escalate to the bank’s grievance officer, and beyond that to the RBI ombudsman scheme.")
                )
            ),
            ResultSection(
                id = SectionId.POWERS, title = "What the bank and police can do", meta = "The realistic picture",
                items = listOf(
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "The bank can investigate, raise a chargeback where applicable, and place the outcome on record."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Police can act on the complaint and pursue the receiving accounts."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "A hold on the money depends on where it has already moved and how quickly it was reported.")
                ),
                caveat = "No one can guarantee recovery. Be cautious of anyone who does."
            ),
            ResultSection(
                id = SectionId.IMPROPER, title = "Watch for a second fraud", meta = "This is common after the first one",
                items = listOf(
                    SectionItem(SectionItemIcon.CLOSE, "Nobody from a bank, the police or a portal will ask you for an OTP, PIN, CVV or UPI PIN to reverse a transaction."),
                    SectionItem(SectionItemIcon.CLOSE, "A paid \"recovery agent\" who contacts you after the fraud is very often the same network coming back.")
                )
            ),
            ResultSection(
                id = SectionId.AVOID, title = "Avoid", meta = "Actions that make this worse",
                items = listOf(
                    SectionItem(SectionItemIcon.CLOSE, "Do not call back the number that called you."),
                    SectionItem(SectionItemIcon.CLOSE, "Do not install any app, or grant screen sharing or remote access to anyone."),
                    SectionItem(SectionItemIcon.CLOSE, "Do not pay anyone who promises to recover the money."),
                    SectionItem(SectionItemIcon.CLOSE, "Do not delete the SMS alerts, the call log or the chat. That is your evidence.")
                )
            ),
            ResultSection(
                id = SectionId.PRESERVE, title = "Preserve", meta = "Keep this while it is fresh",
                items = listOf(
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "The caller’s number and the exact time of the call."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "All SMS and app alerts for the debit, untouched."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "UPI reference or transaction ID, and the amount."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "Screenshots of any chat, payment link or QR code involved."),
                    SectionItem(SectionItemIcon.ARROW_RIGHT, "The 1930 and portal acknowledgement numbers.")
                )
            ),
            ResultSection(
                id = SectionId.LEGAL, title = "Legal basis", meta = "2 verified sources",
                items = emptyList(),
                caveat = "Which offence is registered depends on the facts. That is for the investigating officer, not for this app, to decide.",
                sources = listOf(
                    LegalSource(
                        title = "RBI — customer liability in unauthorised electronic banking transactions",
                        excerpt = "Customer liability in an unauthorised electronic banking transaction depends on the type of negligence and how quickly the customer notifies the bank.",
                        authority = "Reserve Bank of India", lastVerified = LocalDate.of(2026, 8, 9)
                    ),
                    LegalSource(
                        title = "National Cyber Crime Reporting Portal and helpline 1930",
                        excerpt = "Official route for reporting cyber financial fraud, operated with the Ministry of Home Affairs.",
                        authority = "Government of India", lastVerified = LocalDate.of(2026, 8, 9)
                    )
                )
            )
        )
    )

    val byDomain: Map<LegalDomain, SituationResult> = mapOf(
        LegalDomain.TRAFFIC to traffic,
        LegalDomain.POLICE to police,
        LegalDomain.RAILWAY to railway,
        LegalDomain.GOVERNMENT to government,
        LegalDomain.CYBER to cyber
    )
}
