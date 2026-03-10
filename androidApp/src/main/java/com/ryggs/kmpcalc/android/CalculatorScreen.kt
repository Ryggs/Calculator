package com.ryggs.kmpcalc.android

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
    var isDarkThemeEnabled by remember { mutableStateOf<Boolean?>(null) }
    val isDark = isDarkThemeEnabled ?: isSystemInDarkTheme()

    val bodyColor = if (isDark) CalculatorColors.darkBody else CalculatorColors.lightBody
    val displayBg = if (isDark) CalculatorColors.darkDisplayBg else CalculatorColors.lightDisplayBg
    val displayText = if (isDark) CalculatorColors.darkDisplayText else CalculatorColors.lightDisplayText

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bodyColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section: toggle + display
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Theme Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    ThemeToggle(
                        isDark = isDark,
                        onToggle = { isDarkThemeEnabled = !isDark }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display with glass shine
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(displayBg)
                        .drawWithContent {
                            drawContent()
                            // Diagonal glass shine across display
                            val shinePath = Path().apply {
                                moveTo(0f, 0f)
                                lineTo(size.width * 0.7f, 0f)
                                lineTo(size.width * 0.3f, size.height * 0.6f)
                                lineTo(0f, size.height * 0.6f)
                                close()
                            }
                            drawPath(
                                path = shinePath,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.12f),
                                        Color.White.copy(alpha = 0.03f)
                                    ),
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width * 0.5f, size.height * 0.5f)
                                )
                            )
                        }
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = viewModel.expression,
                            color = displayText.copy(alpha = 0.6f),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.End,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = viewModel.result,
                            color = displayText,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Button grid — fills remaining space
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val buttonSpacing = 12.dp

                // Row 1: AC, ←, √, ÷
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NeumorphicButton("AC", ButtonType.Function, isDark, { viewModel.onButtonClick("AC") })
                    NeumorphicButton("←", ButtonType.Function, isDark, { viewModel.onButtonClick("⌫") })
                    NeumorphicButton("√", ButtonType.Function, isDark, { viewModel.onButtonClick("√") })
                    NeumorphicButton("÷", ButtonType.Function, isDark, { viewModel.onButtonClick("÷") })
                }

                // Row 2: 7, 8, 9, −
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NeumorphicButton("7", ButtonType.Number, isDark, { viewModel.onButtonClick("7") })
                    NeumorphicButton("8", ButtonType.Number, isDark, { viewModel.onButtonClick("8") })
                    NeumorphicButton("9", ButtonType.Number, isDark, { viewModel.onButtonClick("9") })
                    NeumorphicButton("−", ButtonType.Operator, isDark, { viewModel.onButtonClick("−") })
                }

                // Row 3: 4, 5, 6, +
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NeumorphicButton("4", ButtonType.Number, isDark, { viewModel.onButtonClick("4") })
                    NeumorphicButton("5", ButtonType.Number, isDark, { viewModel.onButtonClick("5") })
                    NeumorphicButton("6", ButtonType.Number, isDark, { viewModel.onButtonClick("6") })
                    NeumorphicButton("+", ButtonType.Operator, isDark, { viewModel.onButtonClick("+") })
                }

                // Rows 4 & 5 with tall equals
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                        ) {
                            NeumorphicButton("1", ButtonType.Number, isDark, { viewModel.onButtonClick("1") })
                            NeumorphicButton("2", ButtonType.Number, isDark, { viewModel.onButtonClick("2") })
                            NeumorphicButton("3", ButtonType.Number, isDark, { viewModel.onButtonClick("3") })
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                        ) {
                            NeumorphicButton("%", ButtonType.Number, isDark, { viewModel.onButtonClick("%") })
                            NeumorphicButton("0", ButtonType.Number, isDark, { viewModel.onButtonClick("0") })
                            NeumorphicButton(".", ButtonType.Number, isDark, { viewModel.onButtonClick(".") })
                        }
                    }

                    // Tall equals button = 2 buttons + spacing
                    NeumorphicButton(
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
}

@Composable
fun ThemeToggle(isDark: Boolean, onToggle: () -> Unit) {
    val trackColor = if (isDark) Color(0xFFD9D9D9) else Color(0xFF4B4B4B)
    val thumbColor = if (isDark) Color(0xFF4B4B4B) else Color(0xFFF3B927)

    val bias by animateFloatAsState(
        targetValue = if (isDark) -1f else 1f,
        label = "toggleAnimation"
    )

    Box(
        modifier = Modifier
            .width(64.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(trackColor)
            .clickable { onToggle() }
            .padding(4.dp),
        contentAlignment = BiasAlignment(horizontalBias = bias, verticalBias = 0f)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}
