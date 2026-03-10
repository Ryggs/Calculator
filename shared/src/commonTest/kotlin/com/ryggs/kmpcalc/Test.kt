package com.ryggs.kmpcalc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalculatorTest {

    private val calculator = Calculator()

    @Test
    fun testAddition() {
        val result = calculator.evaluateExpression("2+3")
        assertEquals(5.0, result)
    }

    @Test
    fun testSubtraction() {
        val result = calculator.evaluateExpression("10-4")
        assertEquals(6.0, result)
    }

    @Test
    fun testMultiplication() {
        val result = calculator.evaluateExpression("3*7")
        assertEquals(21.0, result)
    }

    @Test
    fun testDivision() {
        val result = calculator.evaluateExpression("20/4")
        assertEquals(5.0, result)
    }

    @Test
    fun testInvalidExpression() {
        val result = calculator.evaluateExpression("abc")
        assertTrue(result.isNaN())
    }
}
