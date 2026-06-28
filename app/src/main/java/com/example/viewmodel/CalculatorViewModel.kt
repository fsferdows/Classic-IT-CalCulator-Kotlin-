package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.HistoryItem
import com.example.data.HistoryRepository
import com.example.logic.CalculatorEngine
import com.example.ui.theme.CalculatorTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = HistoryRepository(db.historyDao())

    // History Flow from Room
    val historyState: StateFlow<List<HistoryItem>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI States
    var expression by mutableStateOf("")
        private set

    var result by mutableStateOf("0")
        private set

    var isDegreeMode by mutableStateOf(true)
        private set

    var activeTheme by mutableStateOf(CalculatorTheme.CLASSIC_GOLD)
        private set

    var memoryValue by mutableStateOf(0.0)
        private set

    var activeScreen by mutableStateOf("Calculator") // Calculator, Converter, Graphing

    var isProUser by mutableStateOf(false)
        private set

    fun upgradeToPro() {
        isProUser = true
    }

    fun downgradeToFree() {
        isProUser = false
    }

    // Graphing States
    var selectedGraphEquations by mutableStateOf(listOf("sin(x)", "x^2", "cos(x)"))
        private set
    var graphInputText by mutableStateOf("")

    // Converter States
    var converterType by mutableStateOf("Length") // Length, Weight, Temperature
    var converterFromUnit by mutableStateOf("m")
    var converterToUnit by mutableStateOf("km")
    var converterInput by mutableStateOf("1")
    var converterResult by mutableStateOf("0.001")

    // Input actions for calculator
    fun onButtonClick(label: String) {
        when (label) {
            "AC" -> {
                expression = ""
                result = "0"
            }
            "DEL" -> {
                if (expression.isNotEmpty()) {
                    expression = expression.dropLast(1)
                }
                calculateRealtimeResult()
            }
            "=" -> {
                evaluateExpression()
            }
            "DEG", "RAD" -> {
                isDegreeMode = !isDegreeMode
                calculateRealtimeResult()
            }
            "MC" -> {
                memoryValue = 0.0
            }
            "MR" -> {
                expression += formatDouble(memoryValue)
                calculateRealtimeResult()
            }
            "M+" -> {
                val currentVal = result.toDoubleOrNull() ?: 0.0
                memoryValue += currentVal
            }
            "M-" -> {
                val currentVal = result.toDoubleOrNull() ?: 0.0
                memoryValue -= currentVal
            }
            "sin", "cos", "tan", "asin", "acos", "atan", "log", "ln" -> {
                expression += "$label("
                calculateRealtimeResult()
            }
            "√" -> {
                expression += "√("
                calculateRealtimeResult()
            }
            "∛" -> {
                expression += "∛("
                calculateRealtimeResult()
            }
            "x²" -> {
                expression += "^2"
                calculateRealtimeResult()
            }
            "x³" -> {
                expression += "^3"
                calculateRealtimeResult()
            }
            "x^y" -> {
                expression += "^"
                calculateRealtimeResult()
            }
            "1/x" -> {
                expression += "^(-1)"
                calculateRealtimeResult()
            }
            "10^x" -> {
                expression += "10^"
                calculateRealtimeResult()
            }
            "e^x" -> {
                expression += "e^"
                calculateRealtimeResult()
            }
            "abs" -> {
                expression += "abs("
                calculateRealtimeResult()
            }
            else -> {
                expression += label
                calculateRealtimeResult()
            }
        }
    }

    private fun calculateRealtimeResult() {
        val trimmed = expression.trim()
        if (trimmed.isEmpty()) {
            result = "0"
            return
        }

        // Drop trailing operators/parentheses for silent on-the-fly preview
        var cleanExpr = trimmed
        while (cleanExpr.isNotEmpty() && (
            cleanExpr.endsWith("+") || 
            cleanExpr.endsWith("-") || 
            cleanExpr.endsWith("×") || 
            cleanExpr.endsWith("÷") || 
            cleanExpr.endsWith("%") || 
            cleanExpr.endsWith("^") || 
            cleanExpr.endsWith("(")
        )) {
            cleanExpr = cleanExpr.dropLast(1).trim()
        }

        if (cleanExpr.isEmpty()) return

        try {
            val evalResult = CalculatorEngine.evaluate(cleanExpr, isDegreeMode)
            if (!evalResult.isNaN() && !evalResult.isInfinite()) {
                result = formatDouble(evalResult)
            }
        } catch (e: Exception) {
            // Keep the previous result or do not update result if intermediate formula is not yet parseable
        }
    }

    private fun evaluateExpression() {
        if (expression.trim().isEmpty()) return

        viewModelScope.launch {
            try {
                val evalResult = CalculatorEngine.evaluate(expression, isDegreeMode)
                if (evalResult.isNaN()) {
                    result = "Math Error"
                } else if (evalResult.isInfinite()) {
                    result = "Math Error"
                } else {
                    result = formatDouble(evalResult)
                    repository.addResult(expression, result)
                }
            } catch (e: ArithmeticException) {
                result = "Math Error"
            } catch (e: Exception) {
                result = "Syntax Error"
            }
        }
    }

    fun useHistoryItem(item: HistoryItem) {
        expression = item.expression
        result = item.result
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun changeTheme(theme: CalculatorTheme) {
        activeTheme = theme
    }

    fun switchScreen(screen: String) {
        activeScreen = screen
    }

    // Graphing Functions
    fun addEquation(eqn: String) {
        val clean = eqn.trim().lowercase(Locale.ROOT)
        if (clean.isNotEmpty() && !selectedGraphEquations.contains(clean)) {
            selectedGraphEquations = selectedGraphEquations + clean
            graphInputText = ""
        }
    }

    fun removeEquation(eqn: String) {
        selectedGraphEquations = selectedGraphEquations.filter { it != eqn }
    }

    // Converter Functions
    fun updateConverterType(type: String) {
        converterType = type
        when (type) {
            "Length" -> {
                converterFromUnit = "m"
                converterToUnit = "km"
            }
            "Weight" -> {
                converterFromUnit = "kg"
                converterToUnit = "g"
            }
            "Temperature" -> {
                converterFromUnit = "°C"
                converterToUnit = "°F"
            }
        }
        performConversion()
    }

    fun updateConverterUnits(from: String, to: String) {
        converterFromUnit = from
        converterToUnit = to
        performConversion()
    }

    fun updateConverterInput(input: String) {
        // Keep input sanitized to numeric decimal values
        val sanitized = input.filter { it.isDigit() || it == '.' || it == '-' }
        converterInput = sanitized
        performConversion()
    }

    private fun performConversion() {
        val inputVal = converterInput.toDoubleOrNull() ?: 0.0
        val convertedVal = when (converterType) {
            "Length" -> convertLength(inputVal, converterFromUnit, converterToUnit)
            "Weight" -> convertWeight(inputVal, converterFromUnit, converterToUnit)
            "Temperature" -> convertTemperature(inputVal, converterFromUnit, converterToUnit)
            else -> inputVal
        }
        converterResult = formatDouble(convertedVal)
    }

    private fun convertLength(value: Double, from: String, to: String): Double {
        // Base unit: Meters
        val meters = when (from) {
            "m" -> value
            "km" -> value * 1000.0
            "cm" -> value / 100.0
            "mm" -> value / 1000.0
            "in" -> value * 0.0254
            "ft" -> value * 0.3048
            else -> value
        }
        return when (to) {
            "m" -> meters
            "km" -> meters / 1000.0
            "cm" -> meters * 100.0
            "mm" -> meters * 1000.0
            "in" -> meters / 0.0254
            "ft" -> meters / 0.3048
            else -> meters
        }
    }

    private fun convertWeight(value: Double, from: String, to: String): Double {
        // Base unit: Grams
        val grams = when (from) {
            "g" -> value
            "kg" -> value * 1000.0
            "lbs" -> value * 453.59237
            "oz" -> value * 28.34952
            else -> value
        }
        return when (to) {
            "g" -> grams
            "kg" -> grams / 1000.0
            "lbs" -> grams / 453.59237
            "oz" -> grams / 28.34952
            else -> grams
        }
    }

    private fun convertTemperature(value: Double, from: String, to: String): Double {
        if (from == to) return value
        val celsius = when (from) {
            "°C" -> value
            "°F" -> (value - 32.0) * 5.0 / 9.0
            "K" -> value - 273.15
            else -> value
        }
        return when (to) {
            "°C" -> celsius
            "°F" -> celsius * 9.0 / 5.0 + 32.0
            "K" -> celsius + 273.15
            else -> celsius
        }
    }

    // Formatter utility to display clean numbers without trailing zeroes (e.g. 5.0 -> 5)
    private fun formatDouble(d: Double): String {
        if (d.isInfinite()) return "Math Error"
        if (d.isNaN()) return "Math Error"
        if (d == 0.0) return "0"

        // scientific notation for very large/small numbers
        val absVal = kotlin.math.abs(d)
        if (absVal >= 1e12 || (absVal in 1e-6..1e-15 && absVal > 0)) {
            return String.format(Locale.US, "%.6e", d)
        }

        return if (d == d.toLong().toDouble()) {
            d.toLong().toString()
        } else {
            val formatted = String.format(Locale.US, "%.10f", d)
            // Trim trailing zeros and decimal point
            var end = formatted.length - 1
            while (end > 0 && formatted[end] == '0') {
                end--
            }
            if (formatted[end] == '.') {
                end--
            }
            formatted.substring(0, end + 1)
        }
    }

    // Helper to evaluate arbitrary string graphs
    fun evaluateGraphY(equation: String, xVal: Double): Double {
        return try {
            val substituted = substituteVariable(equation, xVal)
            // Graphing always evaluates trigonometric functions using standard Radians
            CalculatorEngine.evaluate(substituted, isDegreeMode = false)
        } catch (e: Exception) {
            Double.NaN
        }
    }

    private fun substituteVariable(expr: String, xValue: Double): String {
        val sb = StringBuilder()
        var i = 0
        while (i < expr.length) {
            val char = expr[i]
            if (char == 'x' || char == 'X') {
                val prevIsLetter = i > 0 && expr[i - 1].isLetter()
                val nextIsLetter = i < expr.length - 1 && expr[i + 1].isLetter()
                if (!prevIsLetter && !nextIsLetter) {
                    sb.append("($xValue)")
                } else {
                    sb.append(char)
                }
            } else {
                sb.append(char)
            }
            i++
        }
        return sb.toString()
    }
}

class CalculatorViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculatorViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
