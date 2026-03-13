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

    // True when the display is showing an evaluated result (not mid-expression)
    var isResult by mutableStateOf(false)
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
        isResult = false
    }

    private fun backspace() {
        if (hasEvaluated) { hasEvaluated = false; isResult = false }
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
            isResult = false
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
        if (hasEvaluated) { hasEvaluated = false; isResult = false }
        if (expression.isNotEmpty() && expression != "Error") {
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
                val sqrtResult = BigDecimal.valueOf(sqrt(value.toDouble()))
                expression = formatResult(sqrtResult)
                result = ""
                hasEvaluated = true
                isResult = true
            } else {
                expression = "Error"
                result = ""
                hasEvaluated = true
                isResult = true
            }
        } catch (e: Exception) {
            expression = "Error"
            result = ""
            hasEvaluated = true
            isResult = true
        }
    }

    private fun percentage() {
        try {
            val value = evaluateCurrentExpression()
            val percentResult = value.divide(BigDecimal("100"), 10, RoundingMode.HALF_UP).stripTrailingZeros()
            expression = formatResult(percentResult)
            result = ""
            hasEvaluated = true
            isResult = true
        } catch (e: Exception) {
            expression = "Error"
            result = ""
            hasEvaluated = true
            isResult = true
        }
    }

    private fun evaluate() {
        if (hasEvaluated) {
            // Repeated =: reapply last operator+operand to the current expression value
            if (lastOperator.isNotEmpty() && lastOperand.isNotEmpty() && expression != "Error") {
                expression = expression + lastOperator + lastOperand
                try {
                    val value = evaluateCurrentExpression()
                    expression = formatResult(value)
                    result = ""
                    isResult = true
                } catch (e: Exception) {
                    expression = "Error"
                    result = ""
                    isResult = true
                }
            }
            return
        }

        captureLastOperatorAndOperand()

        try {
            val value = evaluateCurrentExpression()
            expression = formatResult(value)
            result = ""
            lastResult = expression
            hasEvaluated = true
            isResult = true
        } catch (e: ArithmeticException) {
            expression = "Error"
            result = ""
            hasEvaluated = true
            isResult = true
        } catch (e: Exception) {
            expression = "Error"
            result = ""
            hasEvaluated = true
            isResult = true
        }
    }

    private fun captureLastOperatorAndOperand() {
        val expr = expression

        // Check if expression ends with (-digits) — negative number in parens
        val negParenMatch = Regex("""\(\-\d+\.?\d*\)$""").find(expr)
        if (negParenMatch != null) {
            val opIdx = negParenMatch.range.first - 1
            if (opIdx >= 0) {
                val op = expr[opIdx]
                if (op == '+' || op == '−' || op == '×' || op == '÷') {
                    lastOperator = op.toString()
                    lastOperand = expr.substring(negParenMatch.range.first)
                }
            }
            return
        }

        // Regular number at the end
        var i = expr.length - 1
        while (i >= 0 && (expr[i].isDigit() || expr[i] == '.')) i--
        if (i >= 0 && (expr[i] == '+' || expr[i] == '−' || expr[i] == '×' || expr[i] == '÷')) {
            lastOperator = expr[i].toString()
            lastOperand = expr.substring(i + 1)
        }
    }

    private fun toggleSign() {
        if (expression.isEmpty()) return

        // Case 1: expression ends with (-digits) → unwrap to plain digits
        val negParenRegex = Regex("""\(\-(\d+\.?\d*)\)$""")
        val negMatch = negParenRegex.find(expression)
        if (negMatch != null) {
            expression = expression.removeRange(negMatch.range) + negMatch.groupValues[1]
            if (hasEvaluated) { hasEvaluated = false; isResult = false }
            return
        }

        // Case 2: expression ends with plain digits → wrap as (-digits)
        val posRegex = Regex("""\d+\.?\d*$""")
        val posMatch = posRegex.find(expression)
        if (posMatch != null) {
            expression = expression.removeRange(posMatch.range) + "(-${posMatch.value})"
            if (hasEvaluated) { hasEvaluated = false; isResult = false }
            return
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