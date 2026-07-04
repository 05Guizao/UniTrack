package com.example.unitrack.ui.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.unitrack.data.model.RequestStatus

@Composable
fun StatusChip(
    status: String
) {
    val label = RequestStatus.getLabelFromDbValue(status)

    val containerColor = when (status) {
        "SUBMITTED" -> MaterialTheme.colorScheme.secondaryContainer
        "IN_ANALYSIS" -> MaterialTheme.colorScheme.primaryContainer
        "COMPLETED" -> MaterialTheme.colorScheme.tertiaryContainer
        "REJECTED" -> MaterialTheme.colorScheme.errorContainer
        "CANCELLED" -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val labelColor = when (status) {
        "REJECTED" -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    AssistChip(
        onClick = {},
        label = {
            Text(label)
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = labelColor
        )
    )
}