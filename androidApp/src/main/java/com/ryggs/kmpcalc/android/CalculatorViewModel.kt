package com.ryggs.kmpcalc.android

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.sqrt

class CalculatorViewModel : ViewModel() {

    var expression by mutableStateOf("")
        private set

    var result by mutableStateOf("")
        private set

    private var lastResult: String = ""
    private var hasEvaluated = false

    fun onButtonClick(value: String) {
        when (value) {
            "AC" -> clear()
            "⌫" -> backspace()
            "√" -> squareRoot()
            "=" -> evaluate()
            "%"-> percentage()
            "+", "−", "×", "÷" -> appendOperator(value)
            else -> appendDigit(value)
        }
    }

    private fun clear() {
        expression = ""
        result = ""
        lastResult = ""
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
            expression = digit
            result = ""
            hasEvaluated = false
        } else {
            expression += digit
        }
    }

    private fun appendOperator(op: String) {
        if (hasEvaluated && result.isNotEmpty()) {
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
        val value = evaluateCurrentExpression()
        if (!value.isNaN() && value >= 0) {
            val sqrtResult = sqrt(value)
            result = formatResult(sqrtResult)
            expression = "√(${expression})"
            hasEvaluated = true
        }
    }

    private fun percentage() {
        val value = evaluateCurrentExpression()
        if (!value.isNaN()) {
            val percentResult = value / 100.0
            result = formatResult(percentResult)
            expression = "(${expression})%"
            hasEvaluated = true
        }
    }

    private fun evaluate() {
        val value = evaluateCurrentExpression()
        if (!value.isNaN()) {
            result = formatResult(value)
            lastResult = result
            hasEvaluated = true
        } else {
            result = "Error"
        }
    }

    private fun evaluateCurrentExpression(): Double {
        if (expression.isEmpty()) return Double.NaN

        // Convert display operators to math operators
        val mathExpr = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")

        return try {
            evaluateSimpleExpression(mathExpr)
        } catch (e: Exception) {
            Double.NaN
        }
    }

    /**
     * Simple expression evaluator that handles +, -, *, / with proper precedence.
     * No dependency on Rhino or javax.script.
     */
    private fun evaluateSimpleExpression(expr: String): Double {
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
    private fun parseExpression(tokens: List<String>, pos: IntArray): Double {
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
    private fun parseTerm(tokens: List<String>, pos: IntArray): Double {
        var result = parseFactor(tokens, pos)
        while (pos[0] < tokens.size && tokens[pos[0]] in listOf("*", "/")) {
            val op = tokens[pos[0]]
            pos[0]++
            val right = parseFactor(tokens, pos)
            result = if (op == "*") result * right else result / right
        }
        return result
    }

    // factor = number | '(' expression ')'
    private fun parseFactor(tokens: List<String>, pos: IntArray): Double {
        if (pos[0] < tokens.size && tokens[pos[0]] == "(") {
            pos[0]++ // skip '('
            val result = parseExpression(tokens, pos)
            if (pos[0] < tokens.size && tokens[pos[0]] == ")") {
                pos[0]++ // skip ')'
            }
            return result
        }
        if (pos[0] >= tokens.size) return Double.NaN
        val token = tokens[pos[0]]
        pos[0]++
        return token.toDoubleOrNull() ?: Double.NaN
    }

    private fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble() && !value.isInfinite()) {
            value.toLong().toString()
        } else {
            // Limit decimal places
            val formatted = "%.10g".format(value)
            // Remove trailing zeros
            if (formatted.contains('.')) {
                formatted.trimEnd('0').trimEnd('.')
            } else {
                formatted
            }
        }
    }
}
