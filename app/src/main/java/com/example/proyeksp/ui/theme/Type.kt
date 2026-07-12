package com.example.proyeksp.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    val textButton = TextStyle(
        fontSize = 25.sp,
        fontWeight = FontWeight.Black,
        color = Color.White
    )
}