package com.example.ailex.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Translate
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
import com.example.ailex.core.common.AppLanguage
import com.example.ailex.core.common.LocalAppViewModel
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.components.PrimaryButton
import com.example.ailex.ui.theme.Blue050
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.ShapeCardSm
import com.example.ailex.ui.theme.Surface
import com.example.ailex.ui.theme.Typography

@Composable
fun LanguageScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appViewModel = LocalAppViewModel.current

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
            text = "Preferred language",
            style = Typography.headlineMedium,
            color = Navy900,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
        )
        Text(
            text = "Affects explanations and voice. It does not change which laws are covered.",
            style = Typography.bodyMedium,
            color = Ink500,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AppLanguage.entries.forEach { language ->
                val selected = state.language == language
                AilexCard(
                    shape = ShapeCardSm,
                    fill = if (selected) Blue050 else Surface,
                    border = if (selected) Blue600 else Line200,
                    onClick = { viewModel.updateLanguage(language) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Translate,
                            contentDescription = null,
                            tint = Blue600,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = language.displayName,
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                                color = Ink900
                            )
                            Text(
                                text = language.nativeLabel,
                                style = TextStyle(fontSize = 13.sp),
                                color = Ink500,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            text = "Finish setup",
            onClick = {
                appViewModel.setUserProfile(state.name, state.language)
                appViewModel.setMobileNumber(state.mobileNumber)
                onFinish()
            }
        )
    }
}
