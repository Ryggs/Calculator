package com.ryggs.kmpcalc.android

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel()
) {
    val isDark = isSystemInDarkTheme()

    val bodyColor = if (isDark) CalculatorColors.darkBody else CalculatorColors.lightBody
    val displayBg = if (isDark) CalculatorColors.darkDisplayBg else CalculatorColors.lightDisplayBg
    val displayText = if (isDark) CalculatorColors.darkDisplayText else CalculatorColors.lightDisplayText
    val indicatorPill = if (isDark) CalculatorColors.darkIndicatorPill else CalculatorColors.lightIndicatorPill
    val indicatorDot = if (isDark) CalculatorColors.darkIndicatorDot else CalculatorColors.lightIndicatorDot

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bodyColor),
        contentAlignment = Alignment.Center
    ) {
        // Calculator body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Power indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(68.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(indicatorPill),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Box(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(indicatorDot)
                    )
                }
            }

            // Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(displayBg)
                    .drawWithContent {
                        drawContent()
                        // Glass shine on display
                        drawRoundRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.15f),
                                    Color.Transparent
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(size.width * 0.6f, size.height * 0.6f)
                            ),
                            cornerRadius = CornerRadius(8.dp.toPx())
                        )
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Expression
                    Text(
                        text = viewModel.expression,
                        color = displayText.copy(alpha = 0.6f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.End,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Result
                    Text(
                        text = viewModel.result,
                        color = displayText,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Button grid
            val buttonSpacing = 10.dp

            // Row 1: AC, ←, √, ÷
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GlassyButton("AC", ButtonType.Function, isDark, { viewModel.onButtonClick("AC") })
                GlassyButton("⌫", ButtonType.Function, isDark, { viewModel.onButtonClick("⌫") })
                GlassyButton("√", ButtonType.Function, isDark, { viewModel.onButtonClick("√") })
                GlassyButton("÷", ButtonType.Function, isDark, { viewModel.onButtonClick("÷") })
            }

            Spacer(modifier = Modifier.height(buttonSpacing))

            // Row 2: 7, 8, 9, −
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GlassyButton("7", ButtonType.Number, isDark, { viewModel.onButtonClick("7") })
                GlassyButton("8", ButtonType.Number, isDark, { viewModel.onButtonClick("8") })
                GlassyButton("9", ButtonType.Number, isDark, { viewModel.onButtonClick("9") })
                GlassyButton("−", ButtonType.Operator, isDark, { viewModel.onButtonClick("−") })
            }

            Spacer(modifier = Modifier.height(buttonSpacing))

            // Row 3: 4, 5, 6, +
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GlassyButton("4", ButtonType.Number, isDark, { viewModel.onButtonClick("4") })
                GlassyButton("5", ButtonType.Number, isDark, { viewModel.onButtonClick("5") })
                GlassyButton("6", ButtonType.Number, isDark, { viewModel.onButtonClick("6") })
                GlassyButton("+", ButtonType.Operator, isDark, { viewModel.onButtonClick("+") })
            }

            Spacer(modifier = Modifier.height(buttonSpacing))

            // Row 4 & 5: 1,2,3 / %,0,. with tall = button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Left side: two rows of 3 buttons
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                    ) {
                        GlassyButton("1", ButtonType.Number, isDark, { viewModel.onButtonClick("1") })
                        GlassyButton("2", ButtonType.Number, isDark, { viewModel.onButtonClick("2") })
                        GlassyButton("3", ButtonType.Number, isDark, { viewModel.onButtonClick("3") })
                    }
                    Spacer(modifier = Modifier.height(buttonSpacing))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                    ) {
                        GlassyButton("%", ButtonType.Number, isDark, { viewModel.onButtonClick("%") })
                        GlassyButton("0", ButtonType.Number, isDark, { viewModel.onButtonClick("0") })
                        GlassyButton(".", ButtonType.Number, isDark, { viewModel.onButtonClick(".") })
                    }
                }

                // Tall equals button
                GlassyButton(
                    text = "=",
                    buttonType = ButtonType.Equals,
                    isDarkTheme = isDark,
                    onClick = { viewModel.onButtonClick("=") },
                    width = 76.dp,
                    height = (76.dp * 2) + buttonSpacing
                )
            }
        }
    }
}
