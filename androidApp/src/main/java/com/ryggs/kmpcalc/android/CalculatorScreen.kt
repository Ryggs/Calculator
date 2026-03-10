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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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

    // Calculate button size based on screen width to fill exactly 4 columns
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val horizontalPadding = 24.dp
    val availableWidth = screenWidth - (horizontalPadding * 2)
    val buttonSpacing = 10.dp
    val buttonSize = (availableWidth - (buttonSpacing * 3)) / 4

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bodyColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(top = 8.dp, bottom = 16.dp),
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

            Spacer(modifier = Modifier.height(12.dp))

            // Display — inset LCD look
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Display takes remaining top space
                    .clip(RoundedCornerShape(8.dp))
                    .background(displayBg)
                    .drawWithContent {
                        drawContent()
                        // Diagonal glass shine across display
                        val shinePath = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(size.width * 0.65f, 0f)
                            lineTo(size.width * 0.25f, size.height * 0.55f)
                            lineTo(0f, size.height * 0.55f)
                            close()
                        }
                        drawPath(
                            path = shinePath,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.10f),
                                    Color.White.copy(alpha = 0.02f)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(size.width * 0.45f, size.height * 0.45f)
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
                        color = displayText.copy(alpha = 0.5f),
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

            Spacer(modifier = Modifier.height(20.dp))

            // Button grid — compact, no extra space
            ButtonGrid(
                isDark = isDark,
                buttonSize = buttonSize,
                buttonSpacing = buttonSpacing,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun ButtonGrid(
    isDark: Boolean,
    buttonSize: Dp,
    buttonSpacing: Dp,
    viewModel: CalculatorViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
    ) {
        // Row 1: AC, ←, √, ÷
        ButtonRow(buttonSpacing) {
            NeumorphicButton("AC", ButtonType.Function, isDark, { viewModel.onButtonClick("AC") }, width = buttonSize, height = buttonSize)
            NeumorphicButton("←", ButtonType.Function, isDark, { viewModel.onButtonClick("⌫") }, width = buttonSize, height = buttonSize)
            NeumorphicButton("√", ButtonType.Function, isDark, { viewModel.onButtonClick("√") }, width = buttonSize, height = buttonSize)
            NeumorphicButton("÷", ButtonType.Function, isDark, { viewModel.onButtonClick("÷") }, width = buttonSize, height = buttonSize)
        }

        // Row 2: 7, 8, 9, −
        ButtonRow(buttonSpacing) {
            NeumorphicButton("7", ButtonType.Number, isDark, { viewModel.onButtonClick("7") }, width = buttonSize, height = buttonSize)
            NeumorphicButton("8", ButtonType.Number, isDark, { viewModel.onButtonClick("8") }, width = buttonSize, height = buttonSize)
            NeumorphicButton("9", ButtonType.Number, isDark, { viewModel.onButtonClick("9") }, width = buttonSize, height = buttonSize)
            NeumorphicButton("−", ButtonType.Operator, isDark, { viewModel.onButtonClick("−") }, width = buttonSize, height = buttonSize)
        }

        // Row 3: 4, 5, 6, +
        ButtonRow(buttonSpacing) {
            NeumorphicButton("4", ButtonType.Number, isDark, { viewModel.onButtonClick("4") }, width = buttonSize, height = buttonSize)
            NeumorphicButton("5", ButtonType.Number, isDark, { viewModel.onButtonClick("5") }, width = buttonSize, height = buttonSize)
            NeumorphicButton("6", ButtonType.Number, isDark, { viewModel.onButtonClick("6") }, width = buttonSize, height = buttonSize)
            NeumorphicButton("+", ButtonType.Operator, isDark, { viewModel.onButtonClick("+") }, width = buttonSize, height = buttonSize)
        }

        // Rows 4 & 5 with tall equals
        Row(
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            // Left 3 columns: rows 4 and 5
            Column(
                verticalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                    NeumorphicButton("1", ButtonType.Number, isDark, { viewModel.onButtonClick("1") }, width = buttonSize, height = buttonSize)
                    NeumorphicButton("2", ButtonType.Number, isDark, { viewModel.onButtonClick("2") }, width = buttonSize, height = buttonSize)
                    NeumorphicButton("3", ButtonType.Number, isDark, { viewModel.onButtonClick("3") }, width = buttonSize, height = buttonSize)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                    NeumorphicButton("%", ButtonType.Number, isDark, { viewModel.onButtonClick("%") }, width = buttonSize, height = buttonSize)
                    NeumorphicButton("0", ButtonType.Number, isDark, { viewModel.onButtonClick("0") }, width = buttonSize, height = buttonSize)
                    NeumorphicButton(".", ButtonType.Number, isDark, { viewModel.onButtonClick(".") }, width = buttonSize, height = buttonSize)
                }
            }

            // Tall equals button
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

@Composable
private fun ButtonRow(
    spacing: Dp,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
        content = content
    )
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
