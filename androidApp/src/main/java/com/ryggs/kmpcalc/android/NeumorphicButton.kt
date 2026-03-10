package com.ryggs.kmpcalc.android

import android.graphics.BlurMaskFilter
import android.graphics.Paint as NativePaint // FIX: Explicitly use native Android Paint
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer // FIX: Import this
import androidx.compose.ui.graphics.nativeCanvas // FIX: Import native canvas
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
        targetValue = if (isPressed) 2f else 12f,
        animationSpec = tween(durationMillis = 100),
        label = "blur"
    )

    val shadowOffset by animateFloatAsState(
        targetValue = if (isPressed) 2f else 8f,
        animationSpec = tween(durationMillis = 100),
        label = "offset"
    )

    val baseColor = getButtonColor(buttonType, isDarkTheme)
    val textColor = getButtonTextColor(buttonType, isDarkTheme)
    val shape = RoundedCornerShape(16.dp)

    val lightShadow = if (isDarkTheme) CalculatorColors.darkTopLeftShadow else CalculatorColors.lightTopLeftShadow
    val darkShadow = if (isDarkTheme) CalculatorColors.darkBottomRightShadow else CalculatorColors.lightBottomRightShadow

    Box(
        modifier = modifier
            .size(width, height)
            .drawBehind {
                drawIntoCanvas { canvas ->
                    // FIX: Use NativePaint directly
                    val paint = NativePaint().apply {
                        color = android.graphics.Color.TRANSPARENT
                    }

                    // Bottom Right Shadow (Dark)
                    paint.color = darkShadow.toArgb()
                    paint.maskFilter = BlurMaskFilter(shadowBlur, BlurMaskFilter.Blur.NORMAL)

                    // FIX: Route through nativeCanvas
                    canvas.nativeCanvas.drawRoundRect(
                        shadowOffset, // left
                        shadowOffset, // top
                        size.width + shadowOffset, // right
                        size.height + shadowOffset, // bottom
                        16.dp.toPx(), // rx
                        16.dp.toPx(), // ry
                        paint
                    )

                    // Top Left Shadow (Light)
                    paint.color = lightShadow.toArgb()
                    paint.maskFilter = BlurMaskFilter(shadowBlur, BlurMaskFilter.Blur.NORMAL)
                    canvas.nativeCanvas.drawRoundRect(
                        -shadowOffset,
                        -shadowOffset,
                        size.width - shadowOffset,
                        size.height - shadowOffset,
                        16.dp.toPx(),
                        16.dp.toPx(),
                        paint
                    )
                }
            }
            .clip(shape)
            .background(baseColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            // FIX: Removed the inline "androidx.compose..." package path
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = if (text.length > 1) 22.sp else 26.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
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
    return CalculatorColors.lightButtonText
}