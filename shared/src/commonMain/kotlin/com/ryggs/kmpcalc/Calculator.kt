package com.ryggs.kmpcalc

class Calculator {

    fun evaluateExpression(expression: String): Double {
        return try {
            val result = evaluateSimpleExpression(expression)
            result
        } catch (e: Exception) {
            Double.NaN
        }
    }

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
                    sb.append(expr[i++])
                }
                tokens.add(sb.toString())
            } else if (c == '-' && (tokens.isEmpty() || tokens.last() in listOf("+", "-", "*", "/", "("))) {
                val sb = StringBuilder("-")
                i++
                while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                    sb.append(expr[i++])
                }
                tokens.add(sb.toString())
            } else if (c in "+-*/()") {
                tokens.add(c.toString())
                i++
            } else {
                i++
            }
        }
        return tokens
    }

    private fun parseExpression(tokens: List<String>, pos: IntArray): Double {
        var result = parseTerm(tokens, pos)
        while (pos[0] < tokens.size && tokens[pos[0]] in listOf("+", "-")) {
            val op = tokens[pos[0]++]
            val right = parseTerm(tokens, pos)
            result = if (op == "+") result + right else result - right
        }
        return result
    }

    private fun parseTerm(tokens: List<String>, pos: IntArray): Double {
        var result = parseFactor(tokens, pos)
        while (pos[0] < tokens.size && tokens[pos[0]] in listOf("*", "/")) {
            val op = tokens[pos[0]++]
            val right = parseFactor(tokens, pos)
            result = if (op == "*") result * right else {
                if (right == 0.0) throw ArithmeticException("Divide by zero")
                result / right
            }
        }
        return result
    }

    private fun parseFactor(tokens: List<String>, pos: IntArray): Double {
        if (pos[0] < tokens.size && tokens[pos[0]] == "(") {
            pos[0]++
            val result = parseExpression(tokens, pos)
            if (pos[0] < tokens.size && tokens[pos[0]] == ")") pos[0]++
            return result
        }
        if (pos[0] >= tokens.size) return Double.NaN
        return tokens[pos[0]++].toDoubleOrNull() ?: Double.NaN
    }
}