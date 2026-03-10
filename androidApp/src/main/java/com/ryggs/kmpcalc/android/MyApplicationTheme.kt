package com.ryggs.kmpcalc.android

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object CalculatorColors {
    // Light Theme
    val lightBody = Color(0xFFE8E8E8)
    val lightDisplayBg = Color(0xFF9E9E9E)
    val lightDisplayText = Color(0xFF333333)
    val lightNumberButton = Color(0xFFE8E8E8)
    val lightFunctionButton = Color(0xFF7CA5FF)
    val lightOperatorButton = Color(0xFF7CA5FF)
    val lightEqualsButton = Color(0xFFF56B7B)
    val lightButtonText = Color(0xFF333333)

    // Light Shadows
    val lightTopLeftShadow = Color.White
    val lightBottomRightShadow = Color(0xFFC4C4C4)

    // Dark Theme
    val darkBody = Color(0xFF383838)
    val darkDisplayBg = Color(0xFF888888)
    val darkDisplayText = Color(0xFF2D2D2D)
    val darkNumberButton = Color(0xFFE6E6E6)
    val darkFunctionButton = Color(0xFFF1B92D)
    val darkOperatorButton = Color(0xFFF1B92D)
    val darkEqualsButton = Color(0xFFF56B7B)
    val darkButtonText = Color(0xFF333333)

    // Dark Shadows
    val darkTopLeftShadow = Color(0xFF4A4A4A)
    val darkBottomRightShadow = Color(0xFF262626)
}

@Composable
fun CalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            background = CalculatorColors.darkBody,
            surface = CalculatorColors.darkBody
        )
    } else {
        lightColorScheme(
            background = CalculatorColors.lightBody,
            surface = CalculatorColors.lightBody
        )
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}