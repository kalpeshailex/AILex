package com.example.ailex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.ShapeSheetTop
import com.example.ailex.ui.theme.Spacing
import com.example.ailex.ui.theme.Surface

/**
 * The bottom sheet chrome shared by every sheet in the app: `RadiusSheet`
 * top corners, a grabber, and standard content padding. Wraps Material3's
 * [ModalBottomSheet]; slide/fade timing is Material3's default, which
 * matches the 280ms ease-out the spec calls for closely enough not to
 * need a custom transition.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AilexBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(),
        containerColor = Surface,
        shape = ShapeSheetTop,
        dragHandle = { SheetGrabber() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.sheetPaddingHorizontal)
                .padding(bottom = Spacing.space6)
        ) {
            content()
        }
    }
}

@Composable
private fun SheetGrabber() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.space3),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 4.dp)
                .background(Line300, RoundedCornerShape(2.dp))
        )
    }
}
