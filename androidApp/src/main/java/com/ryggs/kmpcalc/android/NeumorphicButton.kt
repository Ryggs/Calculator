package com.ryggs.kmpcalc.android

import android.graphics.BlurMaskFilter
import android.graphics.Paint as NativePaint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ButtonType {
    Number, Function, Operator, Equals
}

@Composable
fun NeumorphicButton(
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
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "scale"
    )

    val shadowBlur by animateFloatAsState(
        targetValue = if (isPressed) 2f else 10f,
        animationSpec = tween(durationMillis = 100),
        label = "blur"
    )

    val shadowOffset by animateFloatAsState(
        targetValue = if (isPressed) 1f else 6f,
        animationSpec = tween(durationMillis = 100),
        label = "offset"
    )

    val baseColor = getButtonColor(buttonType, isDarkTheme)
    val textColor = getButtonTextColor(buttonType, isDarkTheme)
    val shape = RoundedCornerShape(14.dp)

    val lightShadow = if (isDarkTheme) CalculatorColors.darkTopLeftShadow else CalculatorColors.lightTopLeftShadow
    val darkShadow = if (isDarkTheme) CalculatorColors.darkBottomRightShadow else CalculatorColors.lightBottomRightShadow

    Box(
        modifier = modifier
            .size(width, height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            // Neumorphic shadows behind the button
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = NativePaint().apply {
                        color = android.graphics.Color.TRANSPARENT
                    }

                    // Bottom-right dark shadow
                    paint.color = darkShadow.toArgb()
                    paint.maskFilter = BlurMaskFilter(shadowBlur, BlurMaskFilter.Blur.NORMAL)
                    canvas.nativeCanvas.drawRoundRect(
                        shadowOffset,
                        shadowOffset,
                        size.width + shadowOffset,
                        size.height + shadowOffset,
                        14.dp.toPx(),
                        14.dp.toPx(),
                        paint
                    )

                    // Top-left light shadow
                    paint.color = lightShadow.toArgb()
                    paint.maskFilter = BlurMaskFilter(shadowBlur, BlurMaskFilter.Blur.NORMAL)
                    canvas.nativeCanvas.drawRoundRect(
                        -shadowOffset,
                        -shadowOffset,
                        size.width - shadowOffset,
                        size.height - shadowOffset,
                        14.dp.toPx(),
                        14.dp.toPx(),
                        paint
                    )
                }
            }
            .clip(shape)
            // Gradient background for 3D depth
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        baseColor.lighten(0.08f),
                        baseColor,
                        baseColor.darken(0.06f)
                    )
                )
            )
            // Glass overlay drawn on top of content
            .drawWithContent {
                drawContent()
                drawGlassOverlay(isPressed)
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = if (text.length > 1) 22.sp else 26.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Draws the glassy highlight effects on top of the button:
 * - Curved top-left shine (the main glass reflection)
 * - Small specular highlight dot
 * - Bottom edge reflected light
 * - Gradient border stroke for glass edge
 */
private fun DrawScope.drawGlassOverlay(isPressed: Boolean) {
    val w = size.width
    val h = size.height

    if (!isPressed) {
        // Main glass highlight — curved shine from top-left
        val highlightPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(w * 0.65f, 0f)
            quadraticBezierTo(w * 0.25f, h * 0.22f, 0f, h * 0.45f)
            close()
        }
        drawPath(
            path = highlightPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.50f),
                    Color.White.copy(alpha = 0.05f)
                ),
                start = Offset(0f, 0f),
                end = Offset(w * 0.45f, h * 0.35f)
            )
        )

        // Small specular highlight dot (bright reflection point)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.7f),
                    Color.White.copy(alpha = 0f)
                ),
                center = Offset(w * 0.25f, h * 0.18f),
                radius = w * 0.10f
            ),
            center = Offset(w * 0.25f, h * 0.18f),
            radius = w * 0.10f
        )
    }

    // Bottom edge reflected light
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.10f)
            ),
            startY = h * 0.88f,
            endY = h
        ),
        topLeft = Offset(0f, h * 0.88f),
        size = Size(w, h * 0.12f)
    )

    // Gradient border stroke — gives the glass edge look
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.05f),
                Color.Black.copy(alpha = 0.08f)
            ),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        ),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
        style = Stroke(width = 1.5f)
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
        ButtonType.Number -> CalculatorColors.lightButtonText
        ButtonType.Function -> if (isDark) Color(0xFF4A3000) else Color(0xFF1A3A6B)
        ButtonType.Operator -> if (isDark) Color(0xFF4A3000) else Color(0xFF1A3A6B)
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
