package com.artha.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs

/**
 * Immutable Money representation storing all currency amounts as 64-bit integers (minor units).
 * Float and Double are never used for internal money arithmetic.
 *
 * Examples:
 * - 10000 INR = ₹100.00
 * - 499 USD = $4.99
 * - 500 JPY = ¥500 (0 decimal places)
 */
data class Money(
    val amountMinor: Long,
    val currency: String = "INR"
) : Comparable<Money> {

    val config: CurrencyConfig
        get() = CurrencyConfig.forCode(currency)

    val isZero: Boolean get() = amountMinor == 0L
    val isPositive: Boolean get() = amountMinor > 0L
    val isNegative: Boolean get() = amountMinor < 0L

    operator fun plus(other: Money): Money {
        requireSameCurrency(other)
        return copy(amountMinor = Math.addExact(this.amountMinor, other.amountMinor))
    }

    operator fun minus(other: Money): Money {
        requireSameCurrency(other)
        return copy(amountMinor = Math.subtractExact(this.amountMinor, other.amountMinor))
    }

    operator fun unaryMinus(): Money {
        return copy(amountMinor = Math.negateExact(this.amountMinor))
    }

    /**
     * Exact integer multiplication.
     */
    operator fun times(multiplier: Long): Money {
        return copy(amountMinor = Math.multiplyExact(this.amountMinor, multiplier))
    }

    /**
     * Scaling with Banker's Rounding (RoundingMode.HALF_EVEN).
     */
    operator fun times(factor: BigDecimal): Money {
        val scaled = BigDecimal(this.amountMinor)
            .multiply(factor)
            .setScale(0, RoundingMode.HALF_EVEN)
        return copy(amountMinor = scaled.longValueExact())
    }

    /**
     * Exact division with Banker's Rounding (RoundingMode.HALF_EVEN).
     */
    operator fun div(divisor: Long): Money {
        require(divisor != 0L) { "Cannot divide money by zero" }
        val divided = BigDecimal(this.amountMinor)
            .divide(BigDecimal(divisor), 0, RoundingMode.HALF_EVEN)
        return copy(amountMinor = divided.longValueExact())
    }

    /**
     * Split an amount into [parts] without losing a single minor unit (cent/paisa).
     * Any non-zero remainder R is distributed 1 unit at a time to the first R parts.
     * Guaranteed: parts.sumOf { it.amountMinor } == this.amountMinor
     */
    fun split(parts: Int): List<Money> {
        require(parts > 0) { "Parts count must be positive, was $parts" }
        if (parts == 1) return listOf(this)

        val basePart = amountMinor / parts
        val remainder = amountMinor % parts

        val result = ArrayList<Money>(parts)
        val absRemainder = abs(remainder)
        val remainderSign = if (remainder >= 0) 1L else -1L

        for (i in 0 until parts) {
            val partValue = if (i < absRemainder) {
                basePart + remainderSign
            } else {
                basePart
            }
            result.add(copy(amountMinor = partValue))
        }

        return result
    }

    /**
     * Proportional allocation by weight ratios with zero minor unit loss.
     * Guaranteed: allocations.sumOf { it.amountMinor } == this.amountMinor
     */
    fun allocate(ratios: List<Int>): List<Money> {
        require(ratios.isNotEmpty()) { "Ratios must not be empty" }
        require(ratios.all { it >= 0 }) { "All ratios must be non-negative" }
        val totalWeight = ratios.sum().toLong()
        require(totalWeight > 0L) { "Sum of ratios must be greater than zero" }

        var remainder = amountMinor
        val results = ArrayList<Money>(ratios.size)

        for (weight in ratios) {
            val share = (amountMinor * weight) / totalWeight
            results.add(copy(amountMinor = share))
            remainder -= share
        }

        // Distribute remainder 1 minor unit per share
        var i = 0
        val remainderSign = if (remainder >= 0) 1L else -1L
        while (remainder != 0L) {
            val index = i % ratios.size
            if (ratios[index] > 0) {
                val current = results[index]
                results[index] = current.copy(amountMinor = current.amountMinor + remainderSign)
                remainder -= remainderSign
            }
            i++
        }

        return results
    }

    /**
     * Returns the major unit value as BigDecimal (e.g. 150.50).
     */
    fun toMajorBigDecimal(): BigDecimal {
        val decimals = config.decimalPlaces
        return BigDecimal(amountMinor).movePointLeft(decimals)
    }

    /**
     * Formats amount according to locale and grouping rules (Indian Lakh/Crore or Western Million/Billion).
     */
    fun format(
        style: NumberGroupingStyle = NumberGroupingStyle.AUTO,
        includeSymbol: Boolean = true
    ): String {
        val decimals = config.decimalPlaces
        val symbol = if (includeSymbol) config.symbol else ""
        val isNeg = amountMinor < 0
        val absMinor = abs(amountMinor)

        val effectiveStyle = if (style == NumberGroupingStyle.AUTO) {
            config.preferredGrouping
        } else {
            style
        }

        val majorPart = if (decimals > 0) {
            val divisor = Math.pow(10.0, decimals.toDouble()).toLong()
            absMinor / divisor
        } else {
            absMinor
        }

        val minorPart = if (decimals > 0) {
            val divisor = Math.pow(10.0, decimals.toDouble()).toLong()
            absMinor % divisor
        } else {
            0L
        }

        val groupedMajor = when (effectiveStyle) {
            NumberGroupingStyle.INDIAN_LAKH_CRORE -> formatIndianGrouping(majorPart)
            else -> formatWesternGrouping(majorPart)
        }

        val formattedNumber = if (decimals > 0) {
            val minorStr = minorPart.toString().padStart(decimals, '0')
            "$groupedMajor.$minorStr"
        } else {
            groupedMajor
        }

        val prefix = if (isNeg) "-" else ""
        return if (symbol.isNotEmpty()) {
            "$prefix$symbol$formattedNumber"
        } else {
            "$prefix$formattedNumber"
        }
    }

    override fun compareTo(other: Money): Int {
        requireSameCurrency(other)
        return this.amountMinor.compareTo(other.amountMinor)
    }

    override fun toString(): String {
        return format()
    }

    private fun requireSameCurrency(other: Money) {
        require(this.currency.equals(other.currency, ignoreCase = true)) {
            "Currency mismatch: ${this.currency} vs ${other.currency}"
        }
    }

    companion object {
        fun zero(currency: String = "INR"): Money = Money(0L, currency)

        fun fromMajor(major: BigDecimal, currency: String = "INR"): Money {
            val cfg = CurrencyConfig.forCode(currency)
            val minor = major.movePointRight(cfg.decimalPlaces).setScale(0, RoundingMode.HALF_EVEN).longValueExact()
            return Money(minor, currency)
        }

        fun fromMajor(major: Double, currency: String = "INR"): Money {
            return fromMajor(BigDecimal.valueOf(major), currency)
        }

        fun fromMajor(major: Long, currency: String = "INR"): Money {
            val cfg = CurrencyConfig.forCode(currency)
            val factor = Math.pow(10.0, cfg.decimalPlaces.toDouble()).toLong()
            return Money(Math.multiplyExact(major, factor), currency)
        }

        private fun formatWesternGrouping(number: Long): String {
            val s = number.toString()
            val len = s.length
            if (len <= 3) return s
            val sb = java.lang.StringBuilder()
            for (i in 0 until len) {
                if (i > 0 && (len - i) % 3 == 0) {
                    sb.append(',')
                }
                sb.append(s[i])
            }
            return sb.toString()
        }

        private fun formatIndianGrouping(number: Long): String {
            val s = number.toString()
            val len = s.length
            if (len <= 3) return s

            // In Indian grouping, the last 3 digits are grouped, then groups of 2 digits
            val lastThree = s.substring(len - 3)
            val rest = s.substring(0, len - 3)

            val sb = java.lang.StringBuilder()
            val restLen = rest.length
            for (i in 0 until restLen) {
                if (i > 0 && (restLen - i) % 2 == 0) {
                    sb.append(',')
                }
                sb.append(rest[i])
            }
            sb.append(',').append(lastThree)
            return sb.toString()
        }
    }
}
