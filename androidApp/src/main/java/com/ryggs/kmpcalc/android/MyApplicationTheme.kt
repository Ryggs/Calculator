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

// Calculator-specific colors
object CalculatorColors {
    // Light theme
    val lightBody = Color(0xFFE8E8E8)
    val lightDisplayBg = Color(0xFFB8B8B0)
    val lightDisplayText = Color(0xFF2D2D2D)
    val lightNumberButton = Color(0xFFD4D4D4)
    val lightFunctionButton = Color(0xFF5B8BD4)
    val lightOperatorButton = Color(0xFF5B8BD4)
    val lightEqualsButton = Color(0xFFE86B7B)
    val lightButtonText = Color(0xFF333333)
    val lightFunctionText = Color(0xFF1A3A6B)
    val lightIndicatorPill = Color(0xFF4A4A4A)
    val lightIndicatorDot = Color(0xFFF5C542)

    // Dark theme
    val darkBody = Color(0xFF4A4A4A)
    val darkDisplayBg = Color(0xFFB0B0A8)
    val darkDisplayText = Color(0xFF2D2D2D)
    val darkNumberButton = Color(0xFFCCCCCC)
    val darkFunctionButton = Color(0xFFD4A520)
    val darkOperatorButton = Color(0xFFD4A520)
    val darkEqualsButton = Color(0xFFD4707A)
    val darkButtonText = Color(0xFF333333)
    val darkFunctionText = Color(0xFF4A3000)
    val darkIndicatorPill = Color(0xFF888888)
    val darkIndicatorDot = Color(0xFFF5C542)
}

@Composable
fun CalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFD4A520),
            secondary = Color(0xFFD4707A),
            tertiary = Color(0xFF4A4A4A),
            background = Color(0xFF4A4A4A),
            surface = Color(0xFF4A4A4A),
            onPrimary = Color(0xFF4A3000),
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF5B8BD4),
            secondary = Color(0xFFE86B7B),
            tertiary = Color(0xFFE8E8E8),
            background = Color(0xFFE8E8E8),
            surface = Color(0xFFE8E8E8),
            onPrimary = Color(0xFF1A3A6B),
            onSecondary = Color.White,
            onBackground = Color(0xFF333333),
            onSurface = Color(0xFF333333)
        )
    }
    val typography = Typography(
        displayLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 40.sp
        ),
        displayMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
