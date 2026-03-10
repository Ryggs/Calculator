package com.ryggs.kmpcalc

import kotlin.test.Test
import kotlin.test.assertTrue

class IosCalculatorTest {

    private val calculator = Calculator()

    @Test
    fun testBasicExpression() {
        val result = calculator.evaluateExpression("5+5")
        assertTrue(result == 10.0, "5+5 should equal 10")
    }
}
