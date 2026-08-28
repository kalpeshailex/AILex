package com.example.ailex.features.live_situation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ailex.core.common.LegalDomain
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.components.IconTile
import com.example.ailex.ui.components.SomethingElseIcon
import com.example.ailex.ui.components.domainIcon
import com.example.ailex.ui.theme.DomainAccents
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.Surface
import com.example.ailex.ui.theme.Typography

@Composable
fun CategoryScreen(
    onBack: () -> Unit,
    onDomainSelected: (LegalDomain) -> Unit,
    onSomethingElseSelected: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink700)
        }
        Text(
            text = "What kind of situation is it?",
            style = Typography.headlineMedium,
            color = Navy900,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
        )
        Text(
            text = "Pick the closest one. You can correct me later.",
            style = Typography.bodyMedium,
            color = Ink500,
            modifier = Modifier.padding(bottom = 22.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            LegalDomain.entries.forEach { domain ->
                CategoryRow(
                    icon = domainIcon(domain),
                    tint = domain.tileBackground,
                    ink = domain.accentColor,
                    label = domain.displayName,
                    blurb = domain.description,
                    onClick = { onDomainSelected(domain) }
                )
            }
            CategoryRow(
                icon = SomethingElseIcon,
                tint = DomainAccents.SomethingElse.tint,
                ink = DomainAccents.SomethingElse.ink,
                label = "Something else",
                blurb = "Describe it in your own words",
                onClick = onSomethingElseSelected
            )
        }
    }
}

@Composable
private fun CategoryRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: androidx.compose.ui.graphics.Color,
    ink: androidx.compose.ui.graphics.Color,
    label: String,
    blurb: String,
    onClick: () -> Unit
) {
    AilexCard(onClick = onClick) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            IconTile(icon = icon, tint = tint, ink = ink, size = 40.dp, iconSize = 21.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold), color = Ink900)
                Text(
                    text = blurb,
                    style = TextStyle(fontSize = 12.5.sp, lineHeight = 18.sp),
                    color = Ink500,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Ink500)
        }
    }
}
