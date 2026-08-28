package com.example.ailex.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ailex.ui.components.AilexTextField
import com.example.ailex.ui.components.PrimaryButton
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.Success500
import com.example.ailex.ui.theme.Success700
import com.example.ailex.ui.theme.Surface
import com.example.ailex.ui.theme.Typography

@Composable
fun NameScreen(
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
        Row(
            modifier = Modifier.padding(top = 12.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Verified,
                contentDescription = null,
                tint = Success500,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Number verified",
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                color = Success700
            )
        }
        Text(
            text = "What should we call you?",
            style = Typography.headlineMedium,
            color = Navy900,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Used only to address you in the app.",
            style = Typography.bodyMedium,
            color = Ink500,
            modifier = Modifier.padding(bottom = 28.dp)
        )
        AilexTextField(
            value = state.name,
            onValueChange = viewModel::updateName,
            placeholder = "Your name"
        )
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            text = "Continue",
            enabled = state.isNameValid,
            onClick = onContinue
        )
    }
}
