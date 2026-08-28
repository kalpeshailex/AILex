package com.example.ailex.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ailex.ui.components.AilexTextField
import com.example.ailex.ui.components.PrimaryButton
import com.example.ailex.ui.theme.Background
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.ShapeField
import com.example.ailex.ui.theme.Surface
import com.example.ailex.ui.theme.Typography

@Composable
fun PhoneScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
            text = "What's your mobile number?",
            style = Typography.headlineMedium,
            color = Navy900,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
        )
        Text(
            text = "We'll send a six-digit code to verify it. This is the only number we store.",
            style = Typography.bodyMedium,
            color = Ink500,
            modifier = Modifier.padding(bottom = 28.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(56.dp)
                    .background(Background, ShapeField)
                    .border(1.dp, Line300, ShapeField)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "+91", style = TextStyle(fontSize = 16.sp), color = Ink700)
            }
            AilexTextField(
                value = state.mobileNumber,
                onValueChange = viewModel::updateMobileNumber,
                modifier = Modifier.weight(1f),
                placeholder = "98765 43210",
                keyboardType = KeyboardType.Number,
                letterSpacing = 0.5.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            text = "Send code",
            enabled = state.isPhoneValid,
            onClick = onContinue
        )
        Text(
            text = "India only in this release. No Aadhaar, address or date of birth is collected.",
            style = TextStyle(fontSize = 12.sp, lineHeight = 17.sp),
            color = Ink500,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
        )
    }
}
