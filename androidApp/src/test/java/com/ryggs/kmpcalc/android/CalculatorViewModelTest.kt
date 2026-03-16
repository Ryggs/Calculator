package com.ryggs.kmpcalc.android

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests for CalculatorEngine — the pure-Kotlin state machine that backs the ViewModel.
 * No Android or Compose runtime required; runs as a plain JVM unit test.
 */
class CalculatorEngineTest {

    private lateinit var engine: CalculatorEngine

    @Before
    fun setup() {
        engine = CalculatorEngine()
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private fun press(vararg buttons: String) = buttons.forEach { engine.onButtonClick(it) }

    // -------------------------------------------------------------------------
    // evaluate() — result becomes editable expression
    // -------------------------------------------------------------------------

    @Test
    fun `after equals result is in expression and isResult is true`() {
        press("5", "+", "2", "=")
        assertEquals("7", engine.expression)
        assertEquals("", engine.result)
        assertTrue(engine.isResult)
    }

    @Test
    fun `after equals backspace trims result digits`() {
        press("1", "2", "+", "3", "=")   // 15
        press("⌫")
        assertEquals("1", engine.expression)
        assertFalse(engine.isResult)
    }

    @Test
    fun `after equals backspace on single digit clears expression`() {
        press("5", "+", "2", "=")
        press("⌫")
        assertEquals("", engine.expression)
    }

    @Test
    fun `after equals typing a digit starts fresh`() {
        press("5", "+", "2", "=")
        press("9")
        assertEquals("9", engine.expression)
        assertFalse(engine.isResult)
    }

    @Test
    fun `after equals operator continues from result`() {
        press("5", "+", "2", "=")   // 7
        press("+")
        assertEquals("7+", engine.expression)
        assertFalse(engine.isResult)
    }

    @Test
    fun `after equals operator then digit gives correct result`() {
        press("5", "+", "2", "=")   // 7
        press("×", "3", "=")        // 7×3 = 21
        assertEquals("21", engine.expression)
    }

    // -------------------------------------------------------------------------
    // evaluate() — repeated = re-applies last operator and operand
    // -------------------------------------------------------------------------

    @Test
    fun `repeated equals applies last operation once`() {
        press("5", "+", "2", "=")   // 7
        press("=")                   // 9
        assertEquals("9", engine.expression)
    }

    @Test
    fun `repeated equals continues incrementing`() {
        press("5", "+", "2", "=")   // 7
        press("=")                   // 9
        press("=")                   // 11
        assertEquals("11", engine.expression)
    }

    @Test
    fun `repeated equals with multiplication`() {
        press("3", "×", "2", "=")   // 6
        press("=")                   // 12
        press("=")                   // 24
        assertEquals("24", engine.expression)
    }

    // -------------------------------------------------------------------------
    // toggleSign() — parenthesis format, no closing paren
    // -------------------------------------------------------------------------

    @Test
    fun `toggle sign on empty shows open paren prefix`() {
        press("+/-")
        assertEquals("(-", engine.expression)
    }

    @Test
    fun `toggle sign on open prefix clears to empty`() {
        press("+/-")
        press("+/-")
        assertEquals("", engine.expression)
    }

    @Test
    fun `toggle sign wraps digit in open paren`() {
        press("8")
        press("+/-")
        assertEquals("(-8", engine.expression)
    }

    @Test
    fun `toggle sign unwraps open-paren digit back to digit`() {
        press("8")
        press("+/-")
        press("+/-")
        assertEquals("8", engine.expression)
    }

    @Test
    fun `toggle sign mid-expression wraps last operand`() {
        press("3", "+", "2")
        press("+/-")
        assertEquals("3+(-2", engine.expression)
    }

    @Test
    fun `toggle sign mid-expression unwraps last operand`() {
        press("3", "+", "2")
        press("+/-")  // 3+(-2
        press("+/-")  // 3+2
        assertEquals("3+2", engine.expression)
    }

    @Test
    fun `toggle sign on result value wraps it`() {
        press("5", "+", "2", "=")   // expression = "7", isResult = true
        press("+/-")
        assertEquals("(-7", engine.expression)
        assertFalse(engine.isResult)
    }

    // -------------------------------------------------------------------------
    // Operator blocked while expression ends with (-
    // -------------------------------------------------------------------------

    @Test
    fun `operator is ignored when expression ends with open paren prefix`() {
        press("+/-")  // (-
        press("+")
        assertEquals("(-", engine.expression)
    }

    // -------------------------------------------------------------------------
    // Evaluation of open-paren negative numbers
    // -------------------------------------------------------------------------

    @Test
    fun `negative number via toggle evaluates correctly`() {
        press("+/-", "5", "=")      // (-5 = -5
        assertEquals("-5", engine.expression)
    }

    @Test
    fun `negative plus positive evaluates to zero`() {
        press("+/-", "1", "+", "1", "=")   // (-1+1 = 0
        assertEquals("0", engine.expression)
    }

    @Test
    fun `positive plus negative evaluates correctly`() {
        press("5", "+", "3")
        press("+/-")               // 5+(-3
        press("=")
        assertEquals("2", engine.expression)
    }

    @Test
    fun `negative times positive evaluates correctly`() {
        press("4", "×", "3")
        press("+/-")               // 4×(-3
        press("=")
        assertEquals("-12", engine.expression)
    }

    // -------------------------------------------------------------------------
    // AC clears everything
    // -------------------------------------------------------------------------

    @Test
    fun `AC clears expression result and flags`() {
        press("5", "+", "2", "=")
        press("AC")
        assertEquals("", engine.expression)
        assertEquals("", engine.result)
        assertFalse(engine.isResult)
    }

    // -------------------------------------------------------------------------
    // Basic arithmetic
    // -------------------------------------------------------------------------

    @Test
    fun `multiplication button works`() {
        press("6", "×", "7", "=")
        assertEquals("42", engine.expression)
    }

    @Test
    fun `division result is correct`() {
        press("1", "0", "÷", "4", "=")
        assertEquals("2.5", engine.expression)
    }

    @Test
    fun `chained operators use latest operator`() {
        press("5", "+")
        press("−")          // replace + with −
        press("2", "=")     // 5−2 = 3
        assertEquals("3", engine.expression)
    }

    @Test
    fun `subtraction result is correct`() {
        press("1", "0", "−", "3", "=")
        assertEquals("7", engine.expression)
    }

    @Test
    fun `decimal input works`() {
        press("1", ".", "5", "+", "1", ".", "5", "=")
        assertEquals("3", engine.expression)
    }

    @Test
    fun `divide by zero returns Error`() {
        press("5", "÷", "0", "=")
        assertEquals("Error", engine.expression)
        assertTrue(engine.isResult)
    }
}
