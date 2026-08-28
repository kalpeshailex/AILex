package com.example.ailex.features.escalation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ailex.core.common.LegalDomain
import com.example.ailex.domain.escalation.ContactType
import com.example.ailex.domain.escalation.EscalationAuthority
import com.example.ailex.domain.escalation.EscalationContact
import com.example.ailex.domain.escalation.EscalationRoutes
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.theme.Background
import com.example.ailex.ui.theme.Blue050
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.BlueBorder
import com.example.ailex.ui.theme.Caution100
import com.example.ailex.ui.theme.Caution700
import com.example.ailex.ui.theme.CautionBorder
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line100
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.ShapeCard
import com.example.ailex.ui.theme.ShapeChip
import com.example.ailex.ui.theme.Surface

@Composable
fun EscalationScreen(
    domain: LegalDomain?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val authorities = EscalationRoutes.authoritiesFor(domain)
    val titleDomain = domain?.displayName ?: "Traffic"

    Column(modifier = Modifier.fillMaxSize().background(Surface)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink700)
            }
            Text(
                text = "Escalation · $titleDomain",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                color = Ink900,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line200))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Where this can go next",
                style = TextStyle(fontSize = 22.sp, lineHeight = 29.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp),
                color = Navy900,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Start at the top. Move down only if you get no response or the matter is serious.",
                style = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
                color = Ink500,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                authorities.forEachIndexed { index, authority ->
                    AuthorityCard(
                        authority = authority,
                        step = index + 1,
                        emphasized = index == 0,
                        onDial = { number ->
                            val dialable = number.substringBefore(" ·").trim()
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$dialable")))
                        }
                    )
                }
            }

            AilexCard(
                fill = Blue050,
                border = BlueBorder,
                modifier = Modifier.padding(top = 18.dp)
            ) {
                Row(
                    modifier = Modifier.padding(15.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Filled.Balance, contentDescription = null, tint = Blue600, modifier = Modifier.size(20.dp))
                    Column {
                        Text(
                            text = "When to get a lawyer",
                            style = TextStyle(fontSize = 14.5.sp, fontWeight = FontWeight.Bold),
                            color = Navy900,
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        Text(
                            text = "If there is an arrest, a serious accusation, a large loss, or a deadline you could miss, talk to a lawyer. Free legal aid is available through the Maharashtra State Legal Services Authority.",
                            style = TextStyle(fontSize = 13.sp, lineHeight = 20.sp),
                            color = Ink600
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthorityCard(
    authority: EscalationAuthority,
    step: Int,
    emphasized: Boolean,
    onDial: (String) -> Unit
) {
    AilexCard(
        shape = ShapeCard,
        fill = Surface,
        border = if (emphasized) BlueBorder else Line200
    ) {
        Column {
            Row(
                modifier = Modifier
                    .background(if (emphasized) Blue050 else Surface)
                    .padding(15.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(if (emphasized) Blue600 else Line100, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = step.toString(),
                        style = TextStyle(fontSize = 12.5.sp, fontWeight = FontWeight.Bold),
                        color = if (emphasized) Surface else Ink600
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = authority.name, style = TextStyle(fontSize = 15.5.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold), color = Ink900)
                    Text(
                        text = authority.role,
                        style = TextStyle(fontSize = 12.5.sp, lineHeight = 18.sp),
                        color = Ink500,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 15.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Text(text = authority.what, style = TextStyle(fontSize = 13.5.sp, lineHeight = 20.sp), color = Ink700)
                authority.contacts.forEach { contact ->
                    ContactRow(contact = contact, onDial = onDial)
                }
                if (authority.note != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Caution100, ShapeChip)
                            .border(1.dp, CautionBorder, ShapeChip)
                            .padding(11.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.PriorityHigh, contentDescription = null, tint = Caution700, modifier = Modifier.size(16.dp))
                        Text(text = authority.note, style = TextStyle(fontSize = 12.5.sp, lineHeight = 18.sp), color = Caution700)
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactRow(contact: EscalationContact, onDial: (String) -> Unit) {
    val icon: ImageVector = when (contact.type) {
        ContactType.CALL -> Icons.Filled.Call
        ContactType.MAIL -> Icons.Filled.Mail
        ContactType.WEB -> Icons.Filled.Language
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Background, ShapeChip)
            .border(1.dp, Line200, ShapeChip)
            .let { if (contact.type == ContactType.CALL) it.clickable { onDial(contact.value) } else it }
            .padding(11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Blue600, modifier = Modifier.size(18.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = contact.value, style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium), color = Ink900)
            Text(
                text = contact.label,
                style = TextStyle(fontSize = 11.5.sp, lineHeight = 15.sp),
                color = Ink500,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
