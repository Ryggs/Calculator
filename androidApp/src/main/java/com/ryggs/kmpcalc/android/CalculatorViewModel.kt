package com.ryggs.kmpcalc.android

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {

    private val engine = CalculatorEngine()

    var expression by mutableStateOf("")
        private set

    var result by mutableStateOf("")
        private set

    // True when the display is showing an evaluated result (not mid-expression)
    var isResult by mutableStateOf(false)
        private set

    var limitToastTrigger by mutableStateOf(0)
        private set

    fun onButtonClick(value: String) {
        engine.onButtonClick(value)
        expression = engine.expression
        result = engine.result
        isResult = engine.isResult
        if (engine.limitReached) limitToastTrigger++
    }

    val formattedExpression: String get() = formatForDisplay(expression)
    val formattedResult: String get() = formatForDisplay(result)

    private fun formatForDisplay(raw: String): String =
        raw.replace(Regex("""\d+(\.\d*)?""")) { match -> formatNumberString(match.value) }

    private fun formatNumberString(numStr: String): String {
        val dotIndex = numStr.indexOf('.')
        val intPart = if (dotIndex >= 0) numStr.substring(0, dotIndex) else numStr
        val decPart = if (dotIndex >= 0) numStr.substring(dotIndex) else ""
        val formatted = intPart.reversed().chunked(3).joinToString(",").reversed()
        return formatted + decPart
    }
}
