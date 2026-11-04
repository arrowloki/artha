package com.artha.ui.components

import java.math.BigDecimal
import java.math.RoundingMode

object MathExpressionEvaluator {

    /**
     * Evaluates a mathematical string expression such as "120+45*2" into a BigDecimal.
     * Evaluates multiplication and division before addition and subtraction.
     * Returns null if expression is blank or invalid.
     */
    fun evaluate(expression: String): BigDecimal? {
        val clean = expression.replace(" ", "").replace(",", ".")
        if (clean.isBlank()) return null

        // Strip trailing operator for live preview (e.g. "120+" -> "120")
        var sanitized = clean
        while (sanitized.isNotEmpty() && (sanitized.last() in "+-*/")) {
            sanitized = sanitized.dropLast(1)
        }
        if (sanitized.isEmpty()) return null

        return try {
            parseAddSubtract(sanitized)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseAddSubtract(expr: String): BigDecimal {
        // Split by '+' and '-' while respecting signs
        val tokens = mutableListOf<String>()
        val ops = mutableListOf<Char>()
        val current = StringBuilder()

        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            if ((c == '+' || c == '-') && current.isNotEmpty() && current.last() !in "+-*/") {
                tokens.add(current.toString())
                ops.add(c)
                current.clear()
            } else {
                current.append(c)
            }
            i++
        }
        if (current.isNotEmpty()) {
            tokens.add(current.toString())
        }

        if (tokens.isEmpty()) return BigDecimal.ZERO

        var result = parseMultiplyDivide(tokens[0])
        for (j in ops.indices) {
            val nextVal = parseMultiplyDivide(tokens[j + 1])
            result = if (ops[j] == '+') {
                result.add(nextVal)
            } else {
                result.subtract(nextVal)
            }
        }
        return result
    }

    private fun parseMultiplyDivide(term: String): BigDecimal {
        val tokens = mutableListOf<String>()
        val ops = mutableListOf<Char>()
        val current = StringBuilder()

        var i = 0
        while (i < term.length) {
            val c = term[i]
            if ((c == '*' || c == '/') && current.isNotEmpty() && current.last() !in "*/") {
                tokens.add(current.toString())
                ops.add(c)
                current.clear()
            } else {
                current.append(c)
            }
            i++
        }
        if (current.isNotEmpty()) {
            tokens.add(current.toString())
        }

        if (tokens.isEmpty()) return BigDecimal.ZERO

        var result = BigDecimal(tokens[0])
        for (j in ops.indices) {
            val nextVal = BigDecimal(tokens[j + 1])
            result = if (ops[j] == '*') {
                result.multiply(nextVal)
            } else {
                if (nextVal.compareTo(BigDecimal.ZERO) == 0) {
                    BigDecimal.ZERO
                } else {
                    result.divide(nextVal, 6, RoundingMode.HALF_EVEN)
                }
            }
        }
        return result
    }
}
