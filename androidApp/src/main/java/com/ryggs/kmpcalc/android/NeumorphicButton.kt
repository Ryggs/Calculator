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
import androidx.compose.ui.geometry.CornerRadius
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
        targetValue = if (isPressed) 3f else 14f,
        animationSpec = tween(durationMillis = 100),
        label = "blur"
    )

    val shadowOffset by animateFloatAsState(
        targetValue = if (isPressed) 1f else 8f,
        animationSpec = tween(durationMillis = 100),
        label = "offset"
    )

    val baseColor = getButtonColor(buttonType, isDarkTheme)
    val textColor = getButtonTextColor(buttonType, isDarkTheme)
    val cornerRadius = 16.dp
    val shape = RoundedCornerShape(cornerRadius)

    val lightShadow = if (isDarkTheme) CalculatorColors.darkTopLeftShadow else CalculatorColors.lightTopLeftShadow
    val darkShadow = if (isDarkTheme) CalculatorColors.darkBottomRightShadow else CalculatorColors.lightBottomRightShadow

    Box(
        modifier = modifier
            .size(width, height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            // Multi-layered neumorphic shadows for depth
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = NativePaint().apply {
                        color = android.graphics.Color.TRANSPARENT
                    }
                    val cr = cornerRadius.toPx()

                    // Outer dark shadow (bottom-right) — deeper for glassmorphism
                    paint.color = darkShadow.toArgb()
                    paint.maskFilter = BlurMaskFilter(shadowBlur * 1.2f, BlurMaskFilter.Blur.NORMAL)
                    canvas.nativeCanvas.drawRoundRect(
                        shadowOffset * 1.1f,
                        shadowOffset * 1.1f,
                        size.width + shadowOffset * 1.1f,
                        size.height + shadowOffset * 1.1f,
                        cr, cr, paint
                    )

                    // Secondary dark shadow — softer, wider spread
                    paint.color = darkShadow.copy(alpha = 0.3f).toArgb()
                    paint.maskFilter = BlurMaskFilter(shadowBlur * 2f, BlurMaskFilter.Blur.NORMAL)
                    canvas.nativeCanvas.drawRoundRect(
                        shadowOffset * 0.5f,
                        shadowOffset * 0.5f,
                        size.width + shadowOffset * 1.5f,
                        size.height + shadowOffset * 1.5f,
                        cr, cr, paint
                    )

                    // Top-left light shadow — bright highlight
                    paint.color = lightShadow.toArgb()
                    paint.maskFilter = BlurMaskFilter(shadowBlur * 1.2f, BlurMaskFilter.Blur.NORMAL)
                    canvas.nativeCanvas.drawRoundRect(
                        -shadowOffset * 1.1f,
                        -shadowOffset * 1.1f,
                        size.width - shadowOffset * 1.1f,
                        size.height - shadowOffset * 1.1f,
                        cr, cr, paint
                    )
                }
            }
            .clip(shape)
            // Richer gradient background — convex glass dome effect
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        baseColor.lighten(0.25f),
                        baseColor.lighten(0.12f),
                        baseColor,
                        baseColor.darken(0.08f),
                        baseColor.darken(0.15f)
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
    // More pronounced for a polished glass/plastic look
    val glossPath = Path().apply {
        moveTo(0f, 0f)
        lineTo(w, 0f)
        lineTo(w, h * 0.18f)
        quadraticBezierTo(w * 0.5f, h * 0.55f, 0f, h * 0.40f)
        close()
    }
    drawPath(
        path = glossPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = if (isPressed) 0.18f else 0.50f),
                Color.White.copy(alpha = if (isPressed) 0.03f else 0.08f)
            ),
            startY = 0f,
            endY = h * 0.55f
        )
    )

    if (!isPressed) {
        // === Layer 2: Curved highlight accent (top-left crescent) ===
        // Stronger crescent for that polished glass look
        val crescentPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(w * 0.60f, 0f)
            quadraticBezierTo(w * 0.15f, h * 0.22f, 0f, h * 0.38f)
            close()
        }
        drawPath(
            path = crescentPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.70f),
                    Color.White.copy(alpha = 0.0f)
                ),
                start = Offset(0f, 0f),
                end = Offset(w * 0.40f, h * 0.35f)
            )
        )

        // === Layer 3: Primary specular hotspot — bright "light source" reflection ===
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.95f),
                    Color.White.copy(alpha = 0.40f),
                    Color.White.copy(alpha = 0f)
                ),
                center = Offset(w * 0.25f, h * 0.15f),
                radius = w * 0.15f
            ),
            center = Offset(w * 0.25f, h * 0.15f),
            radius = w * 0.15f
        )

        // === Layer 4: Secondary specular sparkle — smaller, sharper ===
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.90f),
                    Color.White.copy(alpha = 0f)
                ),
                center = Offset(w * 0.22f, h * 0.12f),
                radius = w * 0.06f
            ),
            center = Offset(w * 0.22f, h * 0.12f),
            radius = w * 0.06f
        )

        // === Layer 5: Secondary soft reflection (lower-right) ===
        val secondaryPath = Path().apply {
            moveTo(w, h)
            lineTo(w * 0.45f, h)
            quadraticBezierTo(w * 0.70f, h * 0.75f, w, h * 0.65f)
            close()
        }
        drawPath(
            path = secondaryPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.0f),
                    Color.White.copy(alpha = 0.18f)
                ),
                start = Offset(w * 0.45f, h * 0.80f),
                end = Offset(w, h)
            )
        )

        // === Layer 6: Inner glow around edge — warm diffused light ===
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.08f),
                    Color.White.copy(alpha = 0.15f)
                ),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = w * 0.8f
            ),
            cornerRadius = CornerRadius(16.dp.toPx()),
            style = Stroke(width = 6f)
        )
    }

    // === Layer 7: Bottom edge reflected light — polished base reflection ===
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.20f)
            ),
            startY = h * 0.82f,
            endY = h
        ),
        topLeft = Offset(0f, h * 0.82f),
        size = Size(w, h * 0.18f)
    )

    // === Layer 8: Glass rim border — polished edge ===
    // Top-left bright, bottom-right dark — like light hitting a glass edge
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.65f),
                Color.White.copy(alpha = 0.20f),
                Color.Transparent,
                Color.Black.copy(alpha = 0.15f)
            ),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        ),
        cornerRadius = CornerRadius(16.dp.toPx()),
        style = Stroke(width = 2.5f)
    )

    // === Layer 9: Inner rim highlight — subtle inset glow ===
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = if (isPressed) 0.05f else 0.30f),
                Color.Transparent,
                Color.Transparent,
                Color.White.copy(alpha = 0.08f)
            ),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        ),
        topLeft = Offset(2f, 2f),
        size = Size(w - 4f, h - 4f),
        cornerRadius = CornerRadius(14.dp.toPx()),
        style = Stroke(width = 1.5f)
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
