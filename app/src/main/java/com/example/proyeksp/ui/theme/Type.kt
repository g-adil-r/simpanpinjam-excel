package com.example.proyeksp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object MyTypography {
    val text = TextStyle(
        color = Color.Black
    )

    val textNormal = text.copy(
        fontSize = 16.sp
    )

    val textTitle = text.copy(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )

    val textBold = text.copy(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
}