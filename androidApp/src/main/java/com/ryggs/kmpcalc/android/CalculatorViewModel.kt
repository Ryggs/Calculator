package com.ryggs.kmpcalc.android

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.sqrt

class CalculatorViewModel : ViewModel() {

    var expression by mutableStateOf("")
        private set

    var result by mutableStateOf("")
        private set

    private var lastResult: String = ""
    private var hasEvaluated = false
    private var lastOperator: String = ""
    private var lastOperand: String = ""

    fun onButtonClick(value: String) {
        when (value) {
            "AC" -> clear()
            "⌫" -> backspace()
            "√" -> squareRoot()
            "=" -> evaluate()
            "%" -> percentage()
            "+/-" -> toggleSign()
            "+", "−", "×", "÷" -> appendOperator(value)
            else -> appendDigit(value)
        }
    }

    private fun clear() {
        expression = ""
        result = ""
        lastResult = ""
        lastOperator = ""
        lastOperand = ""
        hasEvaluated = false
    }

    private fun backspace() {
        if (hasEvaluated) {
            // After evaluation, backspace clears
            expression = result
            result = ""
            hasEvaluated = false
        }
        if (expression.isNotEmpty()) {
            expression = expression.dropLast(1)
        }
    }

    private fun appendDigit(digit: String) {
        if (hasEvaluated) {
            // Start fresh after pressing = then a digit
            expression = if (digit == ".") "0." else digit
            result = ""
            hasEvaluated = false
            return
        }

        if (digit == ".") {
            // Prevent multiple decimals in a single number block
            val currentNumber = expression.split(Regex("[+\\-×÷()]")).lastOrNull() ?: ""
            if (currentNumber.contains(".")) {
                return
            }
            if (currentNumber.isEmpty()) {
                expression += "0." // Auto-fill leading zero
                return
            }
        }

        expression += digit
    }

    private fun appendOperator(op: String) {
        if (hasEvaluated && result.isNotEmpty() && result != "Error") {
            expression = result + op
            result = ""
            hasEvaluated = false
        } else if (expression.isNotEmpty()) {
            val last = expression.last()
            if (last == '+' || last == '−' || last == '×' || last == '÷') {
                expression = expression.dropLast(1) + op
            } else {
                expression += op
            }
        }
    }

    private fun squareRoot() {
        try {
            val value = evaluateCurrentExpression()
            if (value >= BigDecimal.ZERO) {
                // Safely convert back and forth for the square root function
                val sqrtResult = BigDecimal.valueOf(sqrt(value.toDouble()))
                result = formatResult(sqrtResult)
                expression = "√(${expression})"
                hasEvaluated = true
            } else {
                result = "Error"
                hasEvaluated = true
            }
        } catch (e: Exception) {
            result = "Error"
            hasEvaluated = true
        }
    }

    private fun percentage() {
        try {
            val value = evaluateCurrentExpression()
            val percentResult = value.divide(BigDecimal("100"), 10, RoundingMode.HALF_UP).stripTrailingZeros()
            result = formatResult(percentResult)
            expression = "(${expression})%"
            hasEvaluated = true
        } catch (e: Exception) {
            result = "Error"
            hasEvaluated = true
        }
    }

    private fun evaluate() {
        if (hasEvaluated) {
            // Repeated =: reapply the last operator and operand
            if (lastOperator.isNotEmpty() && lastOperand.isNotEmpty() && result != "Error") {
                expression = result + lastOperator + lastOperand
                try {
                    val value = evaluateCurrentExpression()
                    result = formatResult(value)
                    lastResult = result
                } catch (e: Exception) {
                    result = "Error"
                }
            }
            return
        }

        // Capture last operator and operand before evaluating
        captureLastOperatorAndOperand()

        try {
            val value = evaluateCurrentExpression()
            result = formatResult(value)
            lastResult = result
            hasEvaluated = true
        } catch (e: ArithmeticException) {
            result = "Error"
            hasEvaluated = true
        } catch (e: Exception) {
            result = "Error"
            hasEvaluated = true
        }
    }

    private fun captureLastOperatorAndOperand() {
        // Find the last binary operator and the operand that follows it
        val expr = expression
        var i = expr.length - 1
        // Walk back past trailing digits/decimals to find the last operand
        while (i >= 0 && (expr[i].isDigit() || expr[i] == '.')) i--
        if (i > 0 && (expr[i] == '+' || expr[i] == '−' || expr[i] == '×' || expr[i] == '÷')) {
            lastOperator = expr[i].toString()
            lastOperand = expr.substring(i + 1)
        }
    }

    private fun toggleSign() {
        if (hasEvaluated && result.isNotEmpty() && result != "Error") {
            val bd = result.toBigDecimalOrNull() ?: return
            result = formatResult(bd.negate())
            expression = result
            hasEvaluated = false
            return
        }
        if (expression.isEmpty()) return
        // Find last operator position to isolate the last number
        var i = expression.length - 1
        while (i >= 0 && (expression[i].isDigit() || expression[i] == '.')) i--
        val prefix = expression.substring(0, i + 1)
        val lastNum = expression.substring(i + 1)
        if (lastNum.isEmpty()) return
        expression = if (lastNum.startsWith("−")) {
            prefix + lastNum.removePrefix("−")
        } else {
            prefix + "−" + lastNum
        }
    }

    private fun evaluateCurrentExpression(): BigDecimal {
        if (expression.isEmpty()) return BigDecimal.ZERO

        // Convert display operators to math operators
        val mathExpr = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")

        return evaluateSimpleExpression(mathExpr)
    }

    /**
     * Simple expression evaluator that handles +, -, *, / with proper precedence.
     * Uses BigDecimal for absolute precision.
     */
    private fun evaluateSimpleExpression(expr: String): BigDecimal {
        val tokens = tokenize(expr)
        return parseExpression(tokens, intArrayOf(0))
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            if (c.isDigit() || c == '.') {
                val sb = StringBuilder()
                while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                    sb.append(expr[i])
                    i++
                }
                tokens.add(sb.toString())
            } else if (c == '-' && (tokens.isEmpty() || tokens.last() in listOf("+", "-", "*", "/", "("))) {
                // Negative number
                val sb = StringBuilder("-")
                i++
                while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                    sb.append(expr[i])
                    i++
                }
                tokens.add(sb.toString())
            } else if (c in "+-*/()") {
                tokens.add(c.toString())
                i++
            } else {
                i++ // skip whitespace or unknown
            }
        }
        return tokens
    }

    // Recursive descent parser: expression = term ((+|-) term)*
    private fun parseExpression(tokens: List<String>, pos: IntArray): BigDecimal {
        var result = parseTerm(tokens, pos)
        while (pos[0] < tokens.size && tokens[pos[0]] in listOf("+", "-")) {
            val op = tokens[pos[0]]
            pos[0]++
            val right = parseTerm(tokens, pos)
            result = if (op == "+") result + right else result - right
        }
        return result
    }

    // term = factor ((*|/) factor)*
    private fun parseTerm(tokens: List<String>, pos: IntArray): BigDecimal {
        var result = parseFactor(tokens, pos)
        while (pos[0] < tokens.size && tokens[pos[0]] in listOf("*", "/")) {
            val op = tokens[pos[0]]
            pos[0]++
            val right = parseFactor(tokens, pos)
            if (op == "*") {
                result *= right
            } else {
                if (right.compareTo(BigDecimal.ZERO) == 0) throw ArithmeticException("Divide by zero")
                // Define scale and rounding mode to avoid non-terminating decimal exceptions
                result = result.divide(right, 10, RoundingMode.HALF_UP).stripTrailingZeros()
            }
        }
        return result
    }

    // factor = number | '(' expression ')'
    private fun parseFactor(tokens: List<String>, pos: IntArray): BigDecimal {
        if (pos[0] < tokens.size && tokens[pos[0]] == "(") {
            pos[0]++ // skip '('
            val result = parseExpression(tokens, pos)
            if (pos[0] < tokens.size && tokens[pos[0]] == ")") {
                pos[0]++ // skip ')'
            }
            return result
        }
        if (pos[0] >= tokens.size) return BigDecimal.ZERO
        val token = tokens[pos[0]]
        pos[0]++
        return try {
            BigDecimal(token)
        } catch (e: NumberFormatException) {
            BigDecimal.ZERO
        }
    }

    private fun formatResult(value: BigDecimal): String {
        // Formats down to plain text, stripping any trailing 0s automatically
        var plainString = value.stripTrailingZeros().toPlainString()

        // Handle a case where stripTrailingZeros leaves a format like "50"
        // without decimals, we just want to return the string directly.
        return plainString
    }
}