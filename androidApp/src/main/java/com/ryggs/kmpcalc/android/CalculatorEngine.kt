package com.ryggs.kmpcalc.android

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.sqrt

/**
 * Pure-Kotlin calculator state machine with no Android or Compose dependencies.
 * CalculatorViewModel wraps this and exposes its state as observable Compose state.
 */
class CalculatorEngine {

    var expression: String = ""
        private set

    var result: String = ""
        private set

    var isResult: Boolean = false
        private set

    var limitReached: Boolean = false
        private set

    private var lastResult: String = ""
    private var hasEvaluated = false
    private var lastOperator: String = ""
    private var lastOperand: String = ""

    fun onButtonClick(value: String) {
        limitReached = false
        when (value) {
            "AC"  -> clear()
            "⌫"  -> backspace()
            "√"   -> squareRoot()
            "="   -> evaluate()
            "%"   -> percentage()
            "+/-" -> toggleSign()
            "+", "−", "×", "÷" -> appendOperator(value)
            else  -> appendDigit(value)
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
            expression = if (digit == ".") "0." else digit
            result = ""
            hasEvaluated = false
            isResult = false
            return
        }

        if (digit == ".") {
            val currentNumber = expression.split(Regex("[+\\-×÷()]")).lastOrNull() ?: ""
            if (currentNumber.contains(".")) return
            if (currentNumber.isEmpty()) {
                expression += "0."
                return
            }
        }

        if (digit != ".") {
            val currentNumber = expression.split(Regex("[+\\-×÷()]")).lastOrNull() ?: ""
            if (currentNumber.count { it.isDigit() } >= 15) {
                limitReached = true
                return
            }
        }

        expression += digit
    }

    private fun appendOperator(op: String) {
        if (hasEvaluated) { hasEvaluated = false; isResult = false }
        if (expression.endsWith("(-")) return
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
                expression = formatResult(BigDecimal.valueOf(sqrt(value.toDouble())))
            } else {
                expression = "Error"
            }
            result = ""
            hasEvaluated = true
            isResult = true
        } catch (e: Exception) {
            expression = "Error"; result = ""; hasEvaluated = true; isResult = true
        }
    }

    private fun percentage() {
        try {
            val value = evaluateCurrentExpression()
            expression = formatResult(
                value.divide(BigDecimal("100"), 10, RoundingMode.HALF_UP).stripTrailingZeros()
            )
            result = ""
            hasEvaluated = true
            isResult = true
        } catch (e: Exception) {
            expression = "Error"; result = ""; hasEvaluated = true; isResult = true
        }
    }

    private fun evaluate() {
        if (hasEvaluated) {
            if (lastOperator.isNotEmpty() && lastOperand.isNotEmpty() && expression != "Error") {
                expression = expression + lastOperator + lastOperand
                try {
                    expression = formatResult(evaluateCurrentExpression())
                    result = ""
                    isResult = true
                } catch (e: Exception) {
                    expression = "Error"; result = ""; isResult = true
                }
            }
            return
        }

        captureLastOperatorAndOperand()

        try {
            expression = formatResult(evaluateCurrentExpression())
            result = ""
            lastResult = expression
            hasEvaluated = true
            isResult = true
        } catch (e: ArithmeticException) {
            expression = "Error"; result = ""; hasEvaluated = true; isResult = true
        } catch (e: Exception) {
            expression = "Error"; result = ""; hasEvaluated = true; isResult = true
        }
    }

    private fun captureLastOperatorAndOperand() {
        val expr = expression

        val negParenMatch = Regex("""\(\-\d+\.?\d*$""").find(expr)
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

        var i = expr.length - 1
        while (i >= 0 && (expr[i].isDigit() || expr[i] == '.')) i--
        if (i >= 0 && (expr[i] == '+' || expr[i] == '−' || expr[i] == '×' || expr[i] == '÷')) {
            lastOperator = expr[i].toString()
            lastOperand = expr.substring(i + 1)
        }
    }

    private fun toggleSign() {
        fun clearEval() { if (hasEvaluated) { hasEvaluated = false; isResult = false } }

        if (expression.isEmpty()) { expression = "(-"; return }
        if (expression == "(-") { expression = ""; return }

        val negMatch = Regex("""\(\-(\d+\.?\d*)$""").find(expression)
        if (negMatch != null) {
            expression = expression.removeRange(negMatch.range) + negMatch.groupValues[1]
            clearEval(); return
        }

        if (expression.endsWith("(-")) { expression = expression.dropLast(2); clearEval(); return }

        val posMatch = Regex("""\d+\.?\d*$""").find(expression)
        if (posMatch != null) {
            expression = expression.removeRange(posMatch.range) + "(-${posMatch.value}"
            clearEval(); return
        }
    }

    private fun evaluateCurrentExpression(): BigDecimal {
        if (expression.isEmpty()) return BigDecimal.ZERO
        val mathExpr = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
        return evaluateSimpleExpression(mathExpr)
    }

    private fun evaluateSimpleExpression(expr: String): BigDecimal =
        parseExpression(tokenize(expr), intArrayOf(0))

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) sb.append(expr[i++])
                    tokens.add(sb.toString())
                }
                c == '-' && (tokens.isEmpty() || tokens.last() in listOf("+", "-", "*", "/", "(")) -> {
                    val sb = StringBuilder("-")
                    i++
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) sb.append(expr[i++])
                    tokens.add(sb.toString())
                }
                c in "+-*/()" -> { tokens.add(c.toString()); i++ }
                else -> i++
            }
        }
        return tokens
    }

    private fun parseExpression(tokens: List<String>, pos: IntArray): BigDecimal {
        var res = parseTerm(tokens, pos)
        while (pos[0] < tokens.size && tokens[pos[0]] in listOf("+", "-")) {
            val op = tokens[pos[0]++]
            val right = parseTerm(tokens, pos)
            res = if (op == "+") res + right else res - right
        }
        return res
    }

    private fun parseTerm(tokens: List<String>, pos: IntArray): BigDecimal {
        var res = parseFactor(tokens, pos)
        while (pos[0] < tokens.size && tokens[pos[0]] in listOf("*", "/")) {
            val op = tokens[pos[0]++]
            val right = parseFactor(tokens, pos)
            res = if (op == "*") res * right else {
                if (right.compareTo(BigDecimal.ZERO) == 0) throw ArithmeticException("Divide by zero")
                res.divide(right, 10, RoundingMode.HALF_UP).stripTrailingZeros()
            }
        }
        return res
    }

    private fun parseFactor(tokens: List<String>, pos: IntArray): BigDecimal {
        if (pos[0] < tokens.size && tokens[pos[0]] == "(") {
            pos[0]++
            val res = parseExpression(tokens, pos)
            if (pos[0] < tokens.size && tokens[pos[0]] == ")") pos[0]++
            return res
        }
        if (pos[0] >= tokens.size) return BigDecimal.ZERO
        return try { BigDecimal(tokens[pos[0]++]) } catch (e: NumberFormatException) { BigDecimal.ZERO }
    }

    private fun formatResult(value: BigDecimal): String =
        value.stripTrailingZeros().toPlainString()
}
