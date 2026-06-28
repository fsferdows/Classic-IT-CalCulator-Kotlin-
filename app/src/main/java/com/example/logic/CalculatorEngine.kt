package com.example.logic

import kotlin.math.*

object CalculatorEngine {

    /**
     * Evaluates a mathematical expression string.
     * Supports addition, subtraction, multiplication, division, modulo, percentage,
     * parentheses, power, factorial, root, trigonometric and logarithmic functions.
     * [isDegreeMode] decides whether trig arguments are degrees or radians, and
     * whether inverse trig results are in degrees or radians.
     */
    fun evaluate(expression: String, isDegreeMode: Boolean): Double {
        val sanitized = preprocess(expression)
        return Parser(sanitized, isDegreeMode).parse()
    }

    // Preprocesses the string to make parsing robust
    private fun preprocess(expr: String): String {
        val temp = expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", "pi")
            .replace("mod", "%")
            .replace("√", "sqrt")
            .replace("∛", "cbrt")
            .replace("e^x", "exp") // Map e^x to custom exp function for easier parsing

        // 1. Convert spaces between two letters (e.g., "e pi") into '*'
        val processedSpaces = StringBuilder()
        var j = 0
        while (j < temp.length) {
            val char = temp[j]
            processedSpaces.append(char)
            if (char == ' ' && j > 0 && j < temp.length - 1) {
                val prevChar = temp[j - 1]
                val nextChar = temp[j + 1]
                if (prevChar.isLetter() && nextChar.isLetter()) {
                    processedSpaces.setLength(processedSpaces.length - 1)
                    processedSpaces.append('*')
                }
            }
            j++
        }

        // Now remove all remaining spaces
        val result = processedSpaces.toString().replace(" ", "")

        // 2. Insert implicit multiplication on the clean space-free string
        // e.g., 2pi -> 2*pi, 2(3+4) -> 2*(3+4), (2+3)(4) -> (2+3)*(4), pi e -> pi*e, etc.
        val sb = StringBuilder()
        var i = 0
        while (i < result.length) {
            val char = result[i]
            sb.append(char)
            if (i < result.length - 1) {
                val nextChar = result[i + 1]

                val currentIsDigitOrConstant = char.isDigit() || char == '.' || char == 'e' || (char == 'i' && i > 0 && result[i-1] == 'p') // 'e' or end of 'pi'
                val nextIsDigitOrConstant = nextChar.isDigit() || nextChar == '.' || nextChar == 'e' || (nextChar == 'p' && i < result.length - 2 && nextCharIndex(result, i+1, "pi"))

                val currentIsRightParen = char == ')' || char == '!'
                val nextIsLeftParen = nextChar == '(' || nextChar == 's' || nextChar == 'c' || nextChar == 't' || nextChar == 'l' || nextChar == 'a'

                // Scenarios for implicit multiplication:
                // 1. Digit/Constant followed by '(' or function
                if (currentIsDigitOrConstant && (nextChar == '(' || nextIsFunctionStart(result, i + 1))) {
                    sb.append('*')
                }
                // 2. ')' or '!' or '%' followed by Digit/Constant/Function/'('
                else if (currentIsRightParen && (nextChar.isDigit() || nextChar == '(' || nextChar == 'e' || nextChar == 'p' || nextIsFunctionStart(result, i + 1))) {
                    sb.append('*')
                }
                // 3. 'pi' followed by a digit (e.g. pi2 -> pi*2) or 'e' followed by digit
                else if (char == 'e' && nextChar.isDigit()) {
                    sb.append('*')
                }
                else if (char == 'i' && i > 0 && result[i-1] == 'p' && nextChar.isDigit()) {
                    sb.append('*')
                }
            }
            i++
        }
        return sb.toString()
    }

    private fun nextCharIndex(str: String, start: Int, match: String): Boolean {
        if (start + match.length <= str.length) {
            return str.substring(start, start + match.length) == match
        }
        return false
    }

    private fun nextIsFunctionStart(str: String, index: Int): Boolean {
        val sub = str.substring(index)
        return sub.startsWith("sin") || sub.startsWith("cos") || sub.startsWith("tan") ||
               sub.startsWith("asin") || sub.startsWith("acos") || sub.startsWith("atan") ||
               sub.startsWith("log") || sub.startsWith("ln") || sub.startsWith("sqrt") ||
               sub.startsWith("cbrt") || sub.startsWith("abs") || sub.startsWith("exp") ||
               sub.startsWith("pi")
    }

    private class Parser(private val input: String, private val isDegreeMode: Boolean) {
        private var pos = -1
        private var ch = ' '

        private fun nextChar() {
            ch = if (++pos < input.length) input[pos] else '\u0000'
        }

        private fun eat(charToEat: Char): Boolean {
            while (ch == ' ') nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < input.length) {
                throw IllegalArgumentException("Syntax Error at: " + input.substring(pos))
            }
            return x
        }

        // Handles + and -
        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                if (eat('+')) {
                    x += parseTerm() // addition
                } else if (eat('-')) {
                    x -= parseTerm() // subtraction
                } else {
                    return x
                }
            }
        }

        // Handles *, /, % (modulo), and percentage calculations
        private fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                if (eat('*')) {
                    x *= parseFactor() // multiplication
                } else if (eat('/')) {
                    val divisor = parseFactor()
                    if (divisor == 0.0) throw ArithmeticException("Math Error")
                    x /= divisor // division
                } else if (eat('%')) {
                    val modVal = parseFactor()
                    if (modVal == 0.0) throw ArithmeticException("Math Error")
                    x %= modVal // modulo
                } else {
                    return x
                }
            }
        }

        // Handles unary +/-, exponentiation, factorials, and percentages
        private fun parseFactor(): Double {
            if (eat('+')) return parseFactor() // unary plus
            if (eat('-')) return -parseFactor() // unary minus

            var x: Double
            val startPos = this.pos
            if (eat('(')) { // parentheses
                x = parseExpression()
                if (!eat(')')) throw IllegalArgumentException("Missing closed parenthesis")
            } else if (ch.isDigit() || ch == '.') { // numbers
                while (ch.isDigit() || ch == '.') nextChar()
                // Support scientific E notation (e.g., 1E5 or 2.3E-4)
                if (ch == 'E' || ch == 'e') {
                    nextChar()
                    if (ch == '+' || ch == '-') {
                        nextChar()
                    }
                    while (ch.isDigit()) nextChar()
                }
                val numStr = input.substring(startPos, pos)
                x = numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid Number: $numStr")
            } else if (ch.isLetter()) { // functions or constants
                while (ch.isLetter()) nextChar()
                val func = input.substring(startPos, pos)
                if (func == "pi") {
                    x = PI
                } else if (func == "e") {
                    x = E
                } else {
                    // It is a function, it should be followed by parentheses
                    var hasParentheses = eat('(')
                    val arg = parseExpression()
                    if (hasParentheses && !eat(')')) {
                        throw IllegalArgumentException("Missing matching parenthesis for function: $func")
                    }
                    x = when (func) {
                        "sin" -> if (isDegreeMode) sin(Math.toRadians(arg)) else sin(arg)
                        "cos" -> if (isDegreeMode) cos(Math.toRadians(arg)) else cos(arg)
                        "tan" -> {
                            val rad = if (isDegreeMode) Math.toRadians(arg) else arg
                            // Tan is undefined for 90, 270 degrees etc.
                            if (abs(cos(rad)) < 1e-15) throw ArithmeticException("Math Error")
                            tan(rad)
                        }
                        "asin" -> {
                            if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Math Error")
                            val radResult = asin(arg)
                            if (isDegreeMode) Math.toDegrees(radResult) else radResult
                        }
                        "acos" -> {
                            if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Math Error")
                            val radResult = acos(arg)
                            if (isDegreeMode) Math.toDegrees(radResult) else radResult
                        }
                        "atan" -> {
                            val radResult = atan(arg)
                            if (isDegreeMode) Math.toDegrees(radResult) else radResult
                        }
                        "log" -> {
                            if (arg <= 0) throw ArithmeticException("Math Error")
                            log10(arg)
                        }
                        "ln" -> {
                            if (arg <= 0) throw ArithmeticException("Math Error")
                            ln(arg)
                        }
                        "sqrt" -> {
                            if (arg < 0) throw ArithmeticException("Math Error")
                            sqrt(arg)
                        }
                        "cbrt" -> {
                            cbrt(arg)
                        }
                        "abs" -> {
                            abs(arg)
                        }
                        "exp" -> {
                            exp(arg)
                        }
                        else -> throw IllegalArgumentException("Unknown function: $func")
                    }
                }
            } else {
                throw IllegalArgumentException("Unexpected character: " + ch)
            }

            // Exponentiation (right associative power)
            if (eat('^')) {
                val exponent = parseFactor()
                // Handle negative base with non-integer exponent
                if (x < 0.0 && floor(exponent) != exponent) throw ArithmeticException("Math Error")
                x = x.pow(exponent)
            }

            // Postfix operators: Factorial (!) and Percent (%) as unary division by 100
            while (true) {
                if (eat('!')) {
                    x = factorial(x)
                } else if (ch == '%') {
                    // Only eat % as percentage if it is NOT followed by an operand term (which implies binary modulo)
                    val next = nextNonSpaceChar(pos + 1)
                    val isNextOperand = next.isDigit() || next == '.' || next == '(' || next.isLetter()
                    if (!isNextOperand) {
                        eat('%')
                        x /= 100.0
                    } else {
                        break
                    }
                } else {
                    break
                }
            }

            return x
        }

        private fun nextNonSpaceChar(index: Int): Char {
            var idx = index
            while (idx < input.length && input[idx] == ' ') {
                idx++
            }
            return if (idx < input.length) input[idx] else '\u0000'
        }

        private fun factorial(n: Double): Double {
            if (n < 0 || n != floor(n)) throw ArithmeticException("Math Error")
            if (n > 170) return Double.POSITIVE_INFINITY
            var res = 1.0
            for (i in 1..n.toInt()) {
                res *= i
            }
            return res
        }
    }
}
