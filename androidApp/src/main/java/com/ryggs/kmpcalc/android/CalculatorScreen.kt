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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel()
) {
    // Add manual theme toggle override
    var isDarkThemeEnabled by remember { mutableStateOf<Boolean?>(null) }
    val isDark = isDarkThemeEnabled ?: isSystemInDarkTheme()

    val bodyColor = if (isDark) CalculatorColors.darkBody else CalculatorColors.lightBody
    val displayBg = if (isDark) CalculatorColors.darkDisplayBg else CalculatorColors.lightDisplayBg
    val displayText = if (isDark) CalculatorColors.darkDisplayText else CalculatorColors.lightDisplayText

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bodyColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Theme Toggle Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                ThemeToggle(
                    isDark = isDark,
                    onToggle = { isDarkThemeEnabled = !isDark }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Display Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(displayBg)
                    .padding(20.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = viewModel.expression,
                        color = displayText.copy(alpha = 0.6f),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = viewModel.result,
                        color = displayText,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Button Grid Setup
            val buttonSpacing = 16.dp
            val buttonSize = 72.dp

            Column(
                verticalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NeumorphicButton("AC", ButtonType.Function, isDark, { viewModel.onButtonClick("AC") }, width = buttonSize, height = buttonSize)
                    NeumorphicButton("←", ButtonType.Function, isDark, { viewModel.onButtonClick("⌫") }, width = buttonSize, height = buttonSize)
                    NeumorphicButton("√", ButtonType.Function, isDark, { viewModel.onButtonClick("√") }, width = buttonSize, height = buttonSize)
                    NeumorphicButton("÷", ButtonType.Function, isDark, { viewModel.onButtonClick("÷") }, width = buttonSize, height = buttonSize)
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NeumorphicButton("7", ButtonType.Number, isDark, { viewModel.onButtonClick("7") }, width = buttonSize, height = buttonSize)
                    NeumorphicButton("8", ButtonType.Number, isDark, { viewModel.onButtonClick("8") }, width = buttonSize, height = buttonSize)
                    NeumorphicButton("9", ButtonType.Number, isDark, { viewModel.onButtonClick("9") }, width = buttonSize, height = buttonSize)
                    NeumorphicButton("−", ButtonType.Operator, isDark, { viewModel.onButtonClick("−") }, width = buttonSize, height = buttonSize)
                }

                // Row 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NeumorphicButton("4", ButtonType.Number, isDark, { viewModel.onButtonClick("4") }, width = buttonSize, height = buttonSize)
                    NeumorphicButton("5", ButtonType.Number, isDark, { viewModel.onButtonClick("5") }, width = buttonSize, height = buttonSize)
                    NeumorphicButton("6", ButtonType.Number, isDark, { viewModel.onButtonClick("6") }, width = buttonSize, height = buttonSize)
                    NeumorphicButton("+", ButtonType.Operator, isDark, { viewModel.onButtonClick("+") }, width = buttonSize, height = buttonSize)
                }

                // Row 4 & 5 Layout (Tall Equals Button)
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
                            NeumorphicButton("1", ButtonType.Number, isDark, { viewModel.onButtonClick("1") }, width = buttonSize, height = buttonSize)
                            NeumorphicButton("2", ButtonType.Number, isDark, { viewModel.onButtonClick("2") }, width = buttonSize, height = buttonSize)
                            NeumorphicButton("3", ButtonType.Number, isDark, { viewModel.onButtonClick("3") }, width = buttonSize, height = buttonSize)
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                        ) {
                            NeumorphicButton("%", ButtonType.Number, isDark, { viewModel.onButtonClick("%") }, width = buttonSize, height = buttonSize)
                            NeumorphicButton("0", ButtonType.Number, isDark, { viewModel.onButtonClick("0") }, width = buttonSize, height = buttonSize)
                            NeumorphicButton(".", ButtonType.Number, isDark, { viewModel.onButtonClick(".") }, width = buttonSize, height = buttonSize)
                        }
                    }

                    // Tall Equals Button spans exactly 2 heights + 1 spacing
                    NeumorphicButton(
                        text = "=",
                        buttonType = ButtonType.Equals,
                        isDarkTheme = isDark,
                        onClick = { viewModel.onButtonClick("=") },
                        width = buttonSize,
                        height = (buttonSize * 2) + buttonSpacing
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

    // FIX: Animate a float between -1f (left) and 1f (right)
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
        // FIX: Apply the animated float to a BiasAlignment
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