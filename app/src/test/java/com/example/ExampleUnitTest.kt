package com.example

import com.example.logic.CalculatorEngine
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4.0, CalculatorEngine.evaluate("2+2", isDegreeMode = false), 1e-9)
    }

    @Test
    fun basic_arithmetic() {
        assertEquals(14.0, CalculatorEngine.evaluate("2 + 3 * 4", isDegreeMode = false), 1e-9)
        assertEquals(4.0, CalculatorEngine.evaluate("10 / 2 - 1", isDegreeMode = false), 1e-9)
        assertEquals(2.0, CalculatorEngine.evaluate("5 % 3", isDegreeMode = false), 1e-9)
    }

    @Test
    fun trig_functions() {
        // Degree Mode
        assertEquals(0.5, CalculatorEngine.evaluate("sin(30)", isDegreeMode = true), 1e-9)
        assertEquals(1.0, CalculatorEngine.evaluate("cos(0)", isDegreeMode = true), 1e-9)
        assertEquals(1.0, CalculatorEngine.evaluate("tan(45)", isDegreeMode = true), 1e-9)

        // Radian Mode
        assertEquals(1.0, CalculatorEngine.evaluate("sin(pi/2)", isDegreeMode = false), 1e-9)
        assertEquals(0.5, CalculatorEngine.evaluate("cos(pi/3)", isDegreeMode = false), 1e-9)
    }

    @Test
    fun logarithms_and_roots() {
        assertEquals(2.0, CalculatorEngine.evaluate("log(100)", isDegreeMode = false), 1e-9)
        assertEquals(1.0, CalculatorEngine.evaluate("ln(e)", isDegreeMode = false), 1e-9)
        assertEquals(4.0, CalculatorEngine.evaluate("√(16)", isDegreeMode = false), 1e-9)
        assertEquals(3.0, CalculatorEngine.evaluate("∛(27)", isDegreeMode = false), 1e-9)
    }

    @Test
    fun powers_and_factorials() {
        assertEquals(8.0, CalculatorEngine.evaluate("2^3", isDegreeMode = false), 1e-9)
        assertEquals(120.0, CalculatorEngine.evaluate("5!", isDegreeMode = false), 1e-9)
        assertEquals(1.0, CalculatorEngine.evaluate("0!", isDegreeMode = false), 1e-9)
    }

    @Test
    fun implicit_multiplication() {
        assertEquals(2.0 * Math.PI, CalculatorEngine.evaluate("2pi", isDegreeMode = false), 1e-9)
        assertEquals(14.0, CalculatorEngine.evaluate("2(3+4)", isDegreeMode = false), 1e-9)
        assertEquals(Math.E * Math.PI, CalculatorEngine.evaluate("e pi", isDegreeMode = false), 1e-9)
    }
}
