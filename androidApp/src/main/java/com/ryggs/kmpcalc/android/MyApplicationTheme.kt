package com.ryggs.kmpcalc.android

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object CalculatorColors {
    val lightBody = Color(0xFFE4E4E4)
    val lightDisplayBg = Color(0xFFAAAAAA)
    val lightDisplayText = Color(0xFF2D2D2D)
    val lightNumberButton = Color(0xFFE4E4E4)
    val lightFunctionButton = Color(0xFF6496E0)
    val lightOperatorButton = Color(0xFF6496E0)
    val lightEqualsButton = Color(0xFFF56B7B)
    val lightButtonText = Color(0xFF333333)
    val lightFunctionText = Color(0xFF1A3060)
    val lightTopLeftShadow = Color(0xFFFAFAFA)
    val lightBottomRightShadow = Color(0xFFBEBEBE)
    val darkBody = Color(0xFF3D3D3D)
    val darkDisplayBg = Color(0xFF909090)
    val darkDisplayText = Color(0xFF2D2D2D)
    val darkNumberButton = Color(0xFFDCDCDC)
    val darkFunctionButton = Color(0xFFDEA820)
    val darkOperatorButton = Color(0xFFDEA820)
    val darkEqualsButton = Color(0xFFF56B7B)
    val darkButtonText = Color(0xFF333333)
    val darkFunctionText = Color(0xFF4A3000)
    val darkTopLeftShadow = Color(0xFF505050)
    val darkBottomRightShadow = Color(0xFF262626)
    val clearButton = Color(0xFFD32F2F)
    val clearButtonText = Color.White
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
