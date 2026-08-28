package com.example.ailex.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ailex.core.common.LegalDomain

/**
 * design_handoff_ailex_v1's Material Symbols name per domain: `local_police`,
 * `traffic`, `train`, `account_balance`, `security`, `more_horiz` for
 * "Something else". Every domain tile/row also always shows the domain name
 * and description as text, so the label carries the meaning too.
 */
fun domainIcon(domain: LegalDomain): ImageVector = when (domain) {
    LegalDomain.POLICE -> Icons.Filled.LocalPolice
    LegalDomain.TRAFFIC -> Icons.Filled.Traffic
    LegalDomain.RAILWAY -> Icons.Filled.Train
    LegalDomain.GOVERNMENT -> Icons.Filled.AccountBalance
    LegalDomain.CYBER -> Icons.Filled.Security
}

val SomethingElseIcon: ImageVector = Icons.Filled.MoreHoriz
