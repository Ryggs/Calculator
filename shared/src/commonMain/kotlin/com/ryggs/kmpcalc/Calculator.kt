package com.ryggs.kmpcalc

class Calculator {

    fun evaluateExpression(expression: String): Double {
        return try {
            val  engine = javax.script.ScriptEngineManager().getEngineByName("rhino")
            val result = engine.eval(expression)
            if (result is Int) result.toDouble() else result as Double
        } catch (e: Exception) {
            Double.NaN
        }
    }
}