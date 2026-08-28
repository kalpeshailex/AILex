package com.example.ailex.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.components.AppMark
import com.example.ailex.ui.components.PrimaryButton
import com.example.ailex.ui.theme.Background
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.Preserve500
import com.example.ailex.ui.theme.Spacing
import com.example.ailex.ui.theme.Surface
import com.example.ailex.ui.theme.Typography

private val WelcomePoints = listOf(
    "Practical steps first, legal theory second.",
    "Rights, obligations and authority powers kept separate.",
    "Nothing is saved as an incident unless you say so."
)

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(top = 44.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        AppMark(modifier = Modifier.padding(bottom = 32.dp))
        Text(
            text = "Legal first-aid for everyday situations.",
            style = Typography.displaySmall,
            color = Navy900,
            modifier = Modifier.padding(bottom = 14.dp)
        )
        Text(
            text = "Tell us what happened. Understand what may apply and what to do next.",
            style = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
            color = Ink600
        )
        Spacer(modifier = Modifier.weight(1f))
        AilexCard(
            fill = Background,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WelcomePoints.forEach { point ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Preserve500,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = point,
                            style = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
                            color = Ink700
                        )
                    }
                }
            }
        }
        PrimaryButton(text = "Continue with mobile number", onClick = onContinue)
        Text(
            text = "We never ask for OTPs, PINs, passwords or CVVs in chat.",
            style = TextStyle(fontSize = 12.sp, lineHeight = 17.sp),
            color = Ink500,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
        )
    }
}
