package com.artha.model

/**
 * Currency metadata supporting decimal places, symbol, flag, and default grouping rules.
 */
data class CurrencyConfig(
    val code: String,
    val name: String,
    val symbol: String,
    val flag: String,
    val decimalPlaces: Int = 2,
    val preferredGrouping: NumberGroupingStyle = NumberGroupingStyle.WESTERN_MILLION_BILLION
) {
    companion object {
        val INR = CurrencyConfig(
            code = "INR",
            name = "Indian Rupee",
            symbol = "₹",
            flag = "🇮🇳",
            decimalPlaces = 2,
            preferredGrouping = NumberGroupingStyle.INDIAN_LAKH_CRORE
        )

        val USD = CurrencyConfig(
            code = "USD",
            name = "US Dollar",
            symbol = "$",
            flag = "🇺🇸",
            decimalPlaces = 2,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val EUR = CurrencyConfig(
            code = "EUR",
            name = "Euro",
            symbol = "€",
            flag = "🇪🇺",
            decimalPlaces = 2,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val GBP = CurrencyConfig(
            code = "GBP",
            name = "British Pound",
            symbol = "£",
            flag = "🇬🇧",
            decimalPlaces = 2,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val AED = CurrencyConfig(
            code = "AED",
            name = "UAE Dirham",
            symbol = "د.إ",
            flag = "🇦🇪",
            decimalPlaces = 2,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val SGD = CurrencyConfig(
            code = "SGD",
            name = "Singapore Dollar",
            symbol = "S$",
            flag = "🇸🇬",
            decimalPlaces = 2,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val CAD = CurrencyConfig(
            code = "CAD",
            name = "Canadian Dollar",
            symbol = "CA$",
            flag = "🇨🇦",
            decimalPlaces = 2,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val AUD = CurrencyConfig(
            code = "AUD",
            name = "Australian Dollar",
            symbol = "A$",
            flag = "🇦🇺",
            decimalPlaces = 2,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val JPY = CurrencyConfig(
            code = "JPY",
            name = "Japanese Yen",
            symbol = "¥",
            flag = "🇯🇵",
            decimalPlaces = 0,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val CHF = CurrencyConfig(
            code = "CHF",
            name = "Swiss Franc",
            symbol = "CHF",
            flag = "🇨🇭",
            decimalPlaces = 2,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val SAR = CurrencyConfig(
            code = "SAR",
            name = "Saudi Riyal",
            symbol = "﷼",
            flag = "🇸🇦",
            decimalPlaces = 2,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val CNY = CurrencyConfig(
            code = "CNY",
            name = "Chinese Yuan",
            symbol = "¥",
            flag = "🇨🇳",
            decimalPlaces = 2,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val KWD = CurrencyConfig(
            code = "KWD",
            name = "Kuwaiti Dinar",
            symbol = "KD",
            flag = "🇰🇼",
            decimalPlaces = 3,
            preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
        )

        val ALL_CURRENCIES = listOf(
            INR, USD, EUR, GBP, AED, SGD, CAD, AUD, JPY, CHF, SAR, CNY, KWD
        )

        private val REGISTRY = ALL_CURRENCIES.associateBy { it.code }

        fun forCode(code: String): CurrencyConfig {
            val upper = code.uppercase()
            return REGISTRY[upper] ?: CurrencyConfig(
                code = upper,
                name = upper,
                symbol = upper,
                flag = "🌐",
                decimalPlaces = 2,
                preferredGrouping = NumberGroupingStyle.WESTERN_MILLION_BILLION
            )
        }
    }
}
