package com.example.ailex.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ailex.ui.components.PrimaryButton
import com.example.ailex.ui.theme.Background
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.Danger600
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.ShapeField
import com.example.ailex.ui.theme.Surface
import com.example.ailex.ui.theme.Typography
import kotlinx.coroutines.delay

private const val ResendSeconds = 24

@Composable
fun OtpScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onChangeNumber: () -> Unit,
    onVerified: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var secondsRemaining by remember { mutableIntStateOf(ResendSeconds) }

    LaunchedEffect(secondsRemaining) {
        if (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining -= 1
        }
    }

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
            text = "Enter the code",
            style = Typography.headlineMedium,
            color = Navy900,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
        )
        Row(modifier = Modifier.padding(bottom = 28.dp)) {
            Text(
                text = "Sent to ${state.maskedContact} · ",
                style = Typography.bodyMedium,
                color = Ink500
            )
            Text(
                text = "change",
                style = Typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline
                ),
                color = Blue600,
                modifier = Modifier.clickable(onClick = onChangeNumber)
            )
        }

        Box {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 0 until OtpLength) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(Background, ShapeField)
                            .border(1.dp, Line300, ShapeField),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.otp.getOrNull(i)?.toString().orEmpty(),
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                            color = Ink900
                        )
                    }
                }
            }
            // Invisible field overlaying the cells: captures real keyboard input
            // (and SMS autofill, once wired to a BroadcastReceiver) while the
            // Row above renders the visible digits.
            BasicTextField(
                value = state.otp,
                onValueChange = viewModel::updateOtp,
                textStyle = TextStyle(color = Color.Transparent, fontSize = 22.sp),
                cursorBrush = SolidColor(Color.Transparent),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.matchParentSize()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (secondsRemaining > 0) {
                Text(
                    text = "Resend in 0:${secondsRemaining.toString().padStart(2, '0')}",
                    style = TextStyle(fontSize = 13.sp),
                    color = Ink500
                )
            } else {
                Text(
                    text = "Resend code",
                    style = Typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = Blue600,
                    modifier = Modifier.clickable {
                        viewModel.resetOtp()
                        viewModel.sendCode(onSuccess = {})
                        secondsRemaining = ResendSeconds
                    }
                )
            }
        }

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
            text = "Verify",
            enabled = state.isOtpValid && !state.isVerifying,
            loading = state.isVerifying,
            onClick = { viewModel.verifyCode(onSuccess = onVerified) }
        )
    }
}
