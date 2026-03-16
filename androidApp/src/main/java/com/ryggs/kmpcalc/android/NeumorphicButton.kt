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
    Number, Function, Operator, Equals, Clear
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
            // Gradient background — strong top-to-bottom for convex glass dome
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        baseColor.lighten(0.18f),
                        baseColor.lighten(0.05f),
                        baseColor,
                        baseColor.darken(0.12f)
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
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

private fun DrawScope.drawGlassOverlay(isPressed: Boolean) {
    val w = size.width
    val h = size.height

    // === Layer 1: Full top-half gloss (the big dome reflection) ===
    // This is the main "glass" look — a bright wash across the top ~45%
    val glossPath = Path().apply {
        moveTo(0f, 0f)
        lineTo(w, 0f)
        lineTo(w, h * 0.20f)
        quadraticBezierTo(w * 0.5f, h * 0.52f, 0f, h * 0.38f)
        close()
    }
    drawPath(
        path = glossPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = if (isPressed) 0.15f else 0.40f),
                Color.White.copy(alpha = if (isPressed) 0.02f else 0.05f)
            ),
            startY = 0f,
            endY = h * 0.50f
        )
    )

    if (!isPressed) {
        // === Layer 2: Curved highlight accent (top-left crescent) ===
        val crescentPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(w * 0.55f, 0f)
            quadraticBezierTo(w * 0.18f, h * 0.20f, 0f, h * 0.35f)
            close()
        }
        drawPath(
            path = crescentPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.60f),
                    Color.White.copy(alpha = 0.0f)
                ),
                start = Offset(0f, 0f),
                end = Offset(w * 0.35f, h * 0.30f)
            )
        )

        // === Layer 3: Bright specular hotspot ===
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.85f),
                    Color.White.copy(alpha = 0.25f),
                    Color.White.copy(alpha = 0f)
                ),
                center = Offset(w * 0.27f, h * 0.17f),
                radius = w * 0.13f
            ),
            center = Offset(w * 0.27f, h * 0.17f),
            radius = w * 0.13f
        )

        // === Layer 4: Secondary soft reflection (lower-right) ===
        val secondaryPath = Path().apply {
            moveTo(w, h)
            lineTo(w * 0.50f, h)
            quadraticBezierTo(w * 0.75f, h * 0.78f, w, h * 0.70f)
            close()
        }
        drawPath(
            path = secondaryPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.0f),
                    Color.White.copy(alpha = 0.12f)
                ),
                start = Offset(w * 0.50f, h * 0.85f),
                end = Offset(w, h)
            )
        )
    }

    // === Layer 5: Bottom edge reflected light ===
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.15f)
            ),
            startY = h * 0.85f,
            endY = h
        ),
        topLeft = Offset(0f, h * 0.85f),
        size = Size(w, h * 0.15f)
    )

    // === Layer 6: Glass rim border ===
    // Top-left bright, bottom-right dark — like light hitting a glass edge
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.50f),
                Color.White.copy(alpha = 0.10f),
                Color.Transparent,
                Color.Black.copy(alpha = 0.10f)
            ),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        ),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
        style = Stroke(width = 2f)
    )
}

private fun getButtonColor(type: ButtonType, isDark: Boolean): Color {
    return when (type) {
        ButtonType.Number -> if (isDark) CalculatorColors.darkNumberButton else CalculatorColors.lightNumberButton
        ButtonType.Function -> if (isDark) CalculatorColors.darkFunctionButton else CalculatorColors.lightFunctionButton
        ButtonType.Operator -> if (isDark) CalculatorColors.darkOperatorButton else CalculatorColors.lightOperatorButton
        ButtonType.Equals -> if (isDark) CalculatorColors.darkEqualsButton else CalculatorColors.lightEqualsButton
        ButtonType.Clear -> CalculatorColors.clearButton
    }
}

private fun getButtonTextColor(type: ButtonType, isDark: Boolean): Color {
    return when (type) {
        ButtonType.Number -> if (isDark) CalculatorColors.darkButtonText else CalculatorColors.lightButtonText
        ButtonType.Function -> if (isDark) CalculatorColors.darkFunctionText else CalculatorColors.lightFunctionText
        ButtonType.Operator -> if (isDark) CalculatorColors.darkFunctionText else CalculatorColors.lightFunctionText
        ButtonType.Equals -> Color.White
        ButtonType.Clear -> CalculatorColors.clearButtonText
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
