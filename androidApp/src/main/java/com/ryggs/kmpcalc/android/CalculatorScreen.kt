package com.ryggs.kmpcalc.android

import android.content.res.Configuration
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

    val config = LocalConfiguration.current
    val isLandscape = config.orientation == Configuration.ORIENTATION_LANDSCAPE

    val bodyColor = if (isDark) CalculatorColors.darkBody else CalculatorColors.lightBody
    val displayBg = if (isDark) CalculatorColors.darkDisplayBg else CalculatorColors.lightDisplayBg
    val displayText = if (isDark) CalculatorColors.darkDisplayText else CalculatorColors.lightDisplayText

    val screenWidth = config.screenWidthDp.dp
    val screenHeight = config.screenHeightDp.dp
    val horizontalPadding = if (isLandscape) 16.dp else 24.dp
    val availableWidth = screenWidth - (horizontalPadding * 2)
    val buttonSpacing = if (isLandscape) 7.dp else 10.dp

    // Constrain button size so all 5 rows fit vertically in landscape
    val widthBasedSize = (availableWidth - (buttonSpacing * 3)) / 4
    val topAreaReserved = if (isLandscape) 72.dp else 52.dp
    val displayReservedHeight = if (isLandscape) 70.dp else 0.dp
    val bottomPad = if (isLandscape) 8.dp else 16.dp
    val topPad = if (isLandscape) 4.dp else 8.dp
    val gapAboveButtons = if (isLandscape) 8.dp else 20.dp
    val availableForButtons = screenHeight - topAreaReserved - displayReservedHeight - gapAboveButtons - bottomPad - (buttonSpacing * 4) - topPad
    val heightBasedSize = availableForButtons / 5
    val buttonSize = minOf(widthBasedSize, heightBasedSize).coerceAtLeast(36.dp)

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
                .padding(top = topPad, bottom = bottomPad),
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

            Spacer(modifier = Modifier.height(if (isLandscape) 6.dp else 12.dp))

            // When isResult=true, show expression large (it holds the answer).
            // When typing, show expression on top and result (live) on bottom.
            val displayTop = if (viewModel.isResult) "" else viewModel.expression
            val displayBottom = if (viewModel.isResult) viewModel.expression else viewModel.result

            if (isLandscape) {
                DisplayPanel(
                    expression = displayTop,
                    result = displayBottom,
                    isLandscape = true,
                    displayBg = displayBg,
                    displayText = displayText,
                    fixedHeight = displayReservedHeight
                )
            } else {
                DisplayPanel(
                    expression = displayTop,
                    result = displayBottom,
                    isLandscape = false,
                    displayBg = displayBg,
                    displayText = displayText,
                    fixedHeight = null,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(gapAboveButtons))

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
private fun DisplayPanel(
    expression: String,
    result: String,
    isLandscape: Boolean,
    displayBg: Color,
    displayText: Color,
    fixedHeight: Dp?,
    modifier: Modifier = Modifier
) {
    val glassModifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(displayBg)
        .drawWithContent {
            drawContent()
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

    if (isLandscape && fixedHeight != null) {
        Box(
            modifier = glassModifier
                .height(fixedHeight)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (expression.isNotEmpty()) {
                    Text(
                        text = expression,
                        color = displayText.copy(alpha = 0.5f),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = result,
                    color = displayText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    } else {
        Box(
            modifier = glassModifier
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = expression,
                    color = displayText.copy(alpha = 0.5f),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = result,
                    color = displayText,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
    val equalsHeight = buttonSize * 2 + buttonSpacing

    Row {
        // Left side: 5 rows × 3 columns + operator column
        Column(
            verticalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            // Row 1: AC, ←, √, ÷
            ButtonRow(buttonSpacing) {
                NeumorphicButton("AC", ButtonType.Function,  isDark, { viewModel.onButtonClick("AC") }, width = buttonSize, height = buttonSize)
                NeumorphicButton("←", ButtonType.Function, isDark, { viewModel.onButtonClick("⌫") },  width = buttonSize, height = buttonSize)
                NeumorphicButton("√",  ButtonType.Function, isDark, { viewModel.onButtonClick("√") },  width = buttonSize, height = buttonSize)
                NeumorphicButton("÷",  ButtonType.Operator, isDark, { viewModel.onButtonClick("÷") },  width = buttonSize, height = buttonSize)
            }
            // Row 2: 7, 8, 9, −
            ButtonRow(buttonSpacing) {
                NeumorphicButton("7", ButtonType.Number,   isDark, { viewModel.onButtonClick("7") }, width = buttonSize, height = buttonSize)
                NeumorphicButton("8", ButtonType.Number,   isDark, { viewModel.onButtonClick("8") }, width = buttonSize, height = buttonSize)
                NeumorphicButton("9", ButtonType.Number,   isDark, { viewModel.onButtonClick("9") }, width = buttonSize, height = buttonSize)
                NeumorphicButton("−", ButtonType.Operator, isDark, { viewModel.onButtonClick("−") }, width = buttonSize, height = buttonSize)
            }
            // Row 3: 4, 5, 6, +
            ButtonRow(buttonSpacing) {
                NeumorphicButton("4", ButtonType.Number,   isDark, { viewModel.onButtonClick("4") }, width = buttonSize, height = buttonSize)
                NeumorphicButton("5", ButtonType.Number,   isDark, { viewModel.onButtonClick("5") }, width = buttonSize, height = buttonSize)
                NeumorphicButton("6", ButtonType.Number,   isDark, { viewModel.onButtonClick("6") }, width = buttonSize, height = buttonSize)
                NeumorphicButton("+", ButtonType.Operator, isDark, { viewModel.onButtonClick("+") }, width = buttonSize, height = buttonSize)
            }
            // Rows 4-5: 1,2,3 / %,0,. on left; tall = on right
            Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                // Left 3 columns: two stacked rows
                Column(verticalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                    // Row 4: 1, 2, 3
                    ButtonRow(buttonSpacing) {
                        NeumorphicButton("1", ButtonType.Number, isDark, { viewModel.onButtonClick("1") }, width = buttonSize, height = buttonSize)
                        NeumorphicButton("2", ButtonType.Number, isDark, { viewModel.onButtonClick("2") }, width = buttonSize, height = buttonSize)
                        NeumorphicButton("3", ButtonType.Number, isDark, { viewModel.onButtonClick("3") }, width = buttonSize, height = buttonSize)
                    }
                    // Row 5: %, 0, .
                    ButtonRow(buttonSpacing) {
                        NeumorphicButton("%", ButtonType.Number,   isDark, { viewModel.onButtonClick("%") }, width = buttonSize, height = buttonSize)
                        NeumorphicButton("0", ButtonType.Number,   isDark, { viewModel.onButtonClick("0") }, width = buttonSize, height = buttonSize)
                        NeumorphicButton(".", ButtonType.Number,   isDark, { viewModel.onButtonClick(".") }, width = buttonSize, height = buttonSize)
                    }
                }
                // Tall equals button spanning rows 4-5
                NeumorphicButton("=", ButtonType.Equals, isDark, { viewModel.onButtonClick("=") }, width = buttonSize, height = equalsHeight)
            }
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
