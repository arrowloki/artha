package com.artha.model

import java.util.Locale

/**
 * Currency metadata supporting decimal places, symbol, and default grouping rules.
 */
data class CurrencyConfig(
    val code: String,
    val symbol: String,
    val decimalPlaces: Int = 2,
    val preferredGrouping: NumberGroupingStyle = NumberGroupingStyle.WESTERN_MILLION_BILLION
) {
    companion object {
        val INR = CurrencyConfig(
            code = "INR",
            symbol = "₹",
            decimalPlaces = 2,
            preferredGrouping = NumberGroupingStyle.INDIAN_LAKH_CRORE
        )

        val USD = CurrencyConfig(
            code = "USD",
            symbol = "$",
            decimalPlaces = 2,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val EUR = CurrencyConfig(
            code = "EUR",
            symbol = "€",
            decimalPlaces = 2,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val GBP = CurrencyConfig(
            code = "GBP",
            symbol = "£",
            decimalPlaces = 2,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val JPY = CurrencyConfig(
            code = "JPY",
            symbol = "¥",
            decimalPlaces = 0,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val KWD = CurrencyConfig(
            code = "KWD",
            symbol = "KD",
            decimalPlaces = 3,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        private val REGISTRY = mapOf(
            "INR" to INR,
            "USD" to USD,
            "EUR" to EUR,
            "GBP" to GBP,
            "JPY" to JPY,
            "KWD" to KWD
        )

        fun forCode(code: String): CurrencyConfig {
            val upper = code.uppercase()
            return REGISTRY[upper] ?: CurrencyConfig(
                code = upper,
                symbol = upper,
                decimalPlaces = 2,
                preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
            )
        }
    }
}
