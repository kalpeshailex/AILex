package com.example.ailex.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// design_handoff_ailex_v1 corner radii. Cards use borders, not elevation, to
// read as separated from the background — see Theme.kt.
val RadiusSheet = 22.dp   // bottom sheet top corners
val RadiusCardLg = 20.dp  // live-help hero card
val RadiusCard = 16.dp    // standard card, list row, tinted panel
val RadiusCardSm = 14.dp  // nested card, banner, compact row
val RadiusField = 12.dp   // input, button, small panel
val RadiusChip = 11.dp    // icon tile
val RadiusPill = 999.dp   // chips, filters, badges, nav pill

val ShapeCardLg = RoundedCornerShape(RadiusCardLg)
val ShapeCard = RoundedCornerShape(RadiusCard)
val ShapeCardSm = RoundedCornerShape(RadiusCardSm)
val ShapeField = RoundedCornerShape(RadiusField)
val ShapeChip = RoundedCornerShape(RadiusChip)
val ShapePill = RoundedCornerShape(RadiusPill)
val ShapeSheetTop = RoundedCornerShape(topStart = RadiusSheet, topEnd = RadiusSheet)
