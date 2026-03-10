package com.ryggs.kmpcalc.android

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ButtonType {
    Number,
    Function,
    Operator,
    Equals
}

@Composable
fun GlassyButton(
    text: String,
    buttonType: ButtonType,
    isDarkTheme: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 76.dp,
    height: Dp = 76.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = tween(durationMillis = 80),
        label = "scale"
    )

    val baseColor = getButtonColor(buttonType, isDarkTheme)
    val textColor = getButtonTextColor(buttonType, isDarkTheme)
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .size(width, height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        baseColor.copy(alpha = 0.9f).lighten(0.15f),
                        baseColor,
                        baseColor.darken(0.1f)
                    )
                ),
                shape = shape
            )
            .drawWithContent {
                drawContent()
                drawGlassEffect(baseColor, isPressed)
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Inner shadow/border effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (isPressed) 0.05f else 0.2f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.05f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = if (text.length > 1) 20.sp else 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun DrawScope.drawGlassEffect(baseColor: Color, isPressed: Boolean) {
    val w = size.width
    val h = size.height

    if (!isPressed) {
        // Top-left glass highlight - curved shine
        val highlightPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(w * 0.7f, 0f)
            quadraticBezierTo(w * 0.3f, h * 0.25f, 0f, h * 0.5f)
            close()
        }
        drawPath(
            path = highlightPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.45f),
                    Color.White.copy(alpha = 0.08f)
                ),
                start = Offset(0f, 0f),
                end = Offset(w * 0.5f, h * 0.4f)
            )
        )

        // Small specular highlight dot
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.6f),
                    Color.White.copy(alpha = 0f)
                ),
                center = Offset(w * 0.28f, h * 0.22f),
                radius = w * 0.12f
            ),
            center = Offset(w * 0.28f, h * 0.22f),
            radius = w * 0.12f
        )
    }

    // Bottom edge highlight (reflected light)
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.08f)
            ),
            startY = h * 0.85f,
            endY = h
        ),
        topLeft = Offset(0f, h * 0.85f),
        size = Size(w, h * 0.15f)
    )

    // Subtle border highlight
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.3f),
                Color.White.copy(alpha = 0.05f),
                Color.Black.copy(alpha = 0.1f)
            ),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        ),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
    )
}

private fun getButtonColor(type: ButtonType, isDark: Boolean): Color {
    return when (type) {
        ButtonType.Number -> if (isDark) CalculatorColors.darkNumberButton else CalculatorColors.lightNumberButton
        ButtonType.Function -> if (isDark) CalculatorColors.darkFunctionButton else CalculatorColors.lightFunctionButton
        ButtonType.Operator -> if (isDark) CalculatorColors.darkOperatorButton else CalculatorColors.lightOperatorButton
        ButtonType.Equals -> if (isDark) CalculatorColors.darkEqualsButton else CalculatorColors.lightEqualsButton
    }
}

private fun getButtonTextColor(type: ButtonType, isDark: Boolean): Color {
    return when (type) {
        ButtonType.Number -> if (isDark) CalculatorColors.darkButtonText else CalculatorColors.lightButtonText
        ButtonType.Function -> if (isDark) CalculatorColors.darkFunctionText else CalculatorColors.lightFunctionText
        ButtonType.Operator -> if (isDark) CalculatorColors.darkFunctionText else CalculatorColors.lightFunctionText
        ButtonType.Equals -> Color.White
    }
}

// Color utility extensions
fun Color.lighten(factor: Float): Color {
    return Color(
        red = (red + (1f - red) * factor).coerceIn(0f, 1f),
        green = (green + (1f - green) * factor).coerceIn(0f, 1f),
        blue = (blue + (1f - blue) * factor).coerceIn(0f, 1f),
        alpha = alpha
    )
}

fun Color.darken(factor: Float): Color {
    return Color(
        red = (red * (1f - factor)).coerceIn(0f, 1f),
        green = (green * (1f - factor)).coerceIn(0f, 1f),
        blue = (blue * (1f - factor)).coerceIn(0f, 1f),
        alpha = alpha
    )
}
