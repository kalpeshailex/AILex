package com.example.ailex.features.live_situation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.components.domainIcon
import com.example.ailex.ui.theme.Background
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.ShapeField
import com.example.ailex.ui.theme.ShapePill
import com.example.ailex.ui.theme.Surface
import com.example.ailex.ui.theme.Typography

@Composable
fun QuestionScreen(
    viewModel: LiveSituationViewModel,
    onBack: () -> Unit,
    onTypeInstead: () -> Unit,
    onAllQuestionsAnswered: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.questionsAnswered) {
        if (state.questionsAnswered) onAllQuestionsAnswered()
    }

    val domain = state.domain ?: return
    val question = state.currentQuestion ?: return
    val questionCount = state.questions.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = {
                    if (state.questionIndex > 0) viewModel.goToPreviousQuestion() else onBack()
                },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink700)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .background(Line200, ShapePill)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth((state.questionIndex + 1f) / questionCount)
                        .background(Blue600, ShapePill)
                )
            }
            Text(
                text = "Question ${state.questionIndex + 1} of $questionCount",
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                color = Ink500
            )
        }

        Row(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .background(domain.tileBackground, ShapePill)
                .padding(vertical = 6.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(domainIcon(domain), contentDescription = null, tint = domain.accentColor, modifier = Modifier.size(16.dp))
            Text(text = domain.displayName, style = TextStyle(fontSize = 12.5.sp, fontWeight = FontWeight.Medium), color = domain.accentColor)
        }

        Text(
            text = question.text,
            style = Typography.headlineSmall,
            color = Ink900,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = question.why,
            style = TextStyle(fontSize = 13.5.sp, lineHeight = 20.sp),
            color = Ink500,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            question.options.forEach { option ->
                AilexCard(pressedBorder = Blue600, onClick = { viewModel.answerCurrentQuestion(option) }) {
                    Row(
                        modifier = Modifier.padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(11.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(2.dp, Line300, CircleShape)
                        )
                        Text(text = option, style = TextStyle(fontSize = 15.sp, lineHeight = 20.sp), color = Ink900)
                    }
                }
            }
        }

        Text(
            text = "Type an answer instead",
            style = TextStyle(fontSize = 13.5.sp, fontWeight = FontWeight.Medium),
            color = Blue600,
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable(onClick = onTypeInstead)
        )

        Spacer(modifier = Modifier.weight(1f))

        AilexCard(shape = ShapeField, fill = Background, border = Line200) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Icon(Icons.Filled.VisibilityOff, contentDescription = null, tint = Ink500, modifier = Modifier.size(17.dp))
                Text(
                    text = "I won't ask for your name, licence number or vehicle number unless it changes the answer.",
                    style = TextStyle(fontSize = 12.5.sp, lineHeight = 18.sp),
                    color = Ink600
                )
            }
        }
    }
}
