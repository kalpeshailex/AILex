package com.example.ailex.features.conversation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ailex.domain.voice.VoiceDemo
import com.example.ailex.ui.components.DangerButton
import com.example.ailex.ui.components.PrimaryButton
import com.example.ailex.ui.components.SectionKicker
import com.example.ailex.ui.theme.Danger600
import com.example.ailex.ui.theme.Navy700
import com.example.ailex.ui.theme.Navy800
import com.example.ailex.ui.theme.NavyBody
import com.example.ailex.ui.theme.NavyCard
import com.example.ailex.ui.theme.NavyLine
import com.example.ailex.ui.theme.NavyMuted
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.Preserve500
import com.example.ailex.ui.theme.ShapeCard
import com.example.ailex.ui.theme.ShapeField
import com.example.ailex.ui.theme.Surface
import kotlinx.coroutines.delay

enum class VoiceStage { LISTENING, TRANSCRIBING, REVIEW }

/**
 * Turn-based voice input — see `CLAUDE.md`: no continuous background
 * capture, and there is no real speech recognition in this build.
 * Listening → Transcribing is the only user-driven step; Transcribing →
 * Review happens on its own after a short simulated delay, matching what
 * a real recognizer completing would look like.
 */
@Composable
fun VoiceScreen(
    onClose: () -> Unit,
    onTypeInstead: () -> Unit,
    onSend: (String) -> Unit
) {
    var stage by remember { mutableStateOf(VoiceStage.LISTENING) }
    var transcript by remember { mutableStateOf(VoiceDemo.Transcript) }

    LaunchedEffect(stage) {
        if (stage == VoiceStage.TRANSCRIBING) {
            delay(1400)
            stage = VoiceStage.REVIEW
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy800)
            .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 28.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            IconButton(onClick = onClose, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = NavyBody)
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            VoiceOrb(stage = stage)
            Spacer(modifier = Modifier.height(26.dp))
            Text(
                text = titleFor(stage),
                style = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.2).sp),
                color = Surface,
                textAlign = TextAlign.Center
            )
            Text(
                text = blurbFor(stage),
                style = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
                color = NavyBody,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .widthIn(max = 250.dp)
            )
            if (stage != VoiceStage.LISTENING) {
                Column(
                    modifier = Modifier
                        .padding(top = 26.dp)
                        .fillMaxWidth()
                        .background(NavyCard, ShapeCard)
                        .border(1.dp, NavyLine, ShapeCard)
                        .padding(15.dp)
                ) {
                    SectionKicker(text = "I heard", color = NavyMuted, modifier = Modifier.padding(bottom = 8.dp))
                    if (stage == VoiceStage.REVIEW) {
                        BasicTextField(
                            value = transcript,
                            onValueChange = { transcript = it },
                            textStyle = TextStyle(fontSize = 15.5.sp, lineHeight = 23.sp, color = Surface),
                            cursorBrush = SolidColor(Blue600),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(text = transcript, style = TextStyle(fontSize = 15.5.sp, lineHeight = 23.sp), color = Surface)
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            when (stage) {
                VoiceStage.LISTENING -> DangerButton(
                    text = "Stop",
                    onClick = { stage = VoiceStage.TRANSCRIBING },
                    leadingIcon = Icons.Filled.Stop
                )
                VoiceStage.TRANSCRIBING -> DangerButton(
                    text = "Cancel",
                    onClick = onClose,
                    leadingIcon = Icons.Filled.Close
                )
                VoiceStage.REVIEW -> PrimaryButton(
                    text = "Send this",
                    onClick = { onSend(transcript) },
                    leadingIcon = Icons.AutoMirrored.Filled.Send
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(1.dp, NavyLine, ShapeField)
                    .clickable(onClick = onTypeInstead),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Type instead",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    color = NavyBody
                )
            }
            Text(
                text = "Voice recordings are not stored. Only the text is sent.",
                style = TextStyle(fontSize = 11.5.sp, lineHeight = 17.sp),
                color = NavyMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun VoiceOrb(stage: VoiceStage) {
    val (orbColor, icon) = when (stage) {
        VoiceStage.LISTENING -> Preserve500 to Icons.Filled.GraphicEq
        VoiceStage.TRANSCRIBING -> Navy700 to Icons.Filled.HourglassTop
        VoiceStage.REVIEW -> Blue600 to Icons.Filled.TaskAlt
    }
    Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
        if (stage == VoiceStage.LISTENING) {
            val transition = rememberInfiniteTransition(label = "voicePulse")
            val scale by transition.animateFloat(
                initialValue = 1f,
                targetValue = 1.9f,
                animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
                label = "pulseScale"
            )
            val alpha by transition.animateFloat(
                initialValue = 0.55f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
                label = "pulseAlpha"
            )
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
                    .background(Preserve500, CircleShape)
            )
        }
        Box(
            modifier = Modifier
                .size(88.dp)
                .background(orbColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Surface, modifier = Modifier.size(40.dp))
        }
    }
}

private fun titleFor(stage: VoiceStage) = when (stage) {
    VoiceStage.LISTENING -> "Listening…"
    VoiceStage.TRANSCRIBING -> "Understanding what you said…"
    VoiceStage.REVIEW -> "Is this right?"
}

private fun blurbFor(stage: VoiceStage) = when (stage) {
    VoiceStage.LISTENING -> "Speak in English, हिंदी or मराठी. Tap stop when you are done."
    VoiceStage.TRANSCRIBING -> "Converting your words to text. Nothing is recorded."
    VoiceStage.REVIEW -> "Correct it if I misheard, then send it."
}
