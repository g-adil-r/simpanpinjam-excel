package com.example.proyeksp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.proyeksp.ui.theme.AppTypography

@Composable
fun InfoRow(label: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = label,
            style = AppTypography.textNormal,
            modifier = Modifier
                .weight(4f)
        )

        Text(
            text = ":",
            style = AppTypography.textNormal,
            modifier = Modifier.weight(0.1f)
        )

        Text(
            text = if (value.isNullOrEmpty() || value == "") "-" else value,
            style = AppTypography.textNormal,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(6f)
        )
    }
}