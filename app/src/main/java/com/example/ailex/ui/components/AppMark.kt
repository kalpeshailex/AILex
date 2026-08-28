package com.example.ailex.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ailex.R

/** The brand mark: the shield-and-cross logo, matching its native aspect ratio at 72dp tall. */
@Composable
fun AppMark(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.app_mark),
        contentDescription = "Ailex",
        modifier = modifier.size(width = 66.dp, height = 72.dp)
    )
}
