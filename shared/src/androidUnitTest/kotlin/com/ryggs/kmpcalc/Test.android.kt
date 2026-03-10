package com.ryggs.kmpcalc

import org.junit.Assert.assertEquals
import org.junit.Test

class AndroidCalculatorTest {

    private val calculator = Calculator()

    @Test
    fun testExpressionWithPrecedence() {
        val result = calculator.evaluateExpression("2+3*4")
        assertEquals(14.0, result, 0.001)
    }

    @Test
    fun testPlatformName() {
        val platform = Platform()
        assert(platform.name.contains("Android"))
    }
}
