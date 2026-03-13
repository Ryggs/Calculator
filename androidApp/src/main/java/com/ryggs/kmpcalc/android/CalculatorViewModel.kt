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

    fun onButtonClick(value: String) {
        engine.onButtonClick(value)
        expression = engine.expression
        result = engine.result
        isResult = engine.isResult
    }
}
