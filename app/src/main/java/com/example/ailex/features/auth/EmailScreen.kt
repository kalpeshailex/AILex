package com.example.ailex.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ailex.ui.components.AilexTextField
import com.example.ailex.ui.components.PrimaryButton
import com.example.ailex.ui.theme.Danger600
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.Surface
import com.example.ailex.ui.theme.Typography

@Composable
fun EmailScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.selectMethod(AuthMethod.EMAIL) }

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
            text = "What's your email?",
            style = Typography.headlineMedium,
            color = Navy900,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
        )
        Text(
            text = "We'll send a one-time code to verify it. This is the only address we store.",
            style = Typography.bodyMedium,
            color = Ink500,
            modifier = Modifier.padding(bottom = 28.dp)
        )
        AilexTextField(
            value = state.email,
            onValueChange = viewModel::updateEmail,
            placeholder = "you@example.com",
            keyboardType = KeyboardType.Email
        )
        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage.orEmpty(),
                style = TextStyle(fontSize = 13.sp, lineHeight = 18.sp),
                color = Danger600,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            text = "Send code",
            enabled = state.isEmailValid && !state.isSending,
            loading = state.isSending,
            onClick = { viewModel.sendCode(onSuccess = onContinue) }
        )
        Text(
            text = "No Aadhaar, address or date of birth is collected.",
            style = TextStyle(fontSize = 12.sp, lineHeight = 17.sp),
            color = Ink500,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
        )
    }
}
