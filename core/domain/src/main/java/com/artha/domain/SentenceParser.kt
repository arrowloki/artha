package com.artha.domain

import com.artha.database.entity.CategoryEntity
import com.artha.model.Money
import com.artha.model.PaymentMethod
import com.artha.model.TransactionType
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

/**
 * Result of natural-language sentence parsing.
 */
data class ParsedSentence(
    val rawText: String,
    val amountMinor: Long? = null,
    val currencyCode: String = "INR",
    val type: TransactionType = TransactionType.EXPENSE,
    val categoryId: String? = null,
    val categoryName: String? = null,
    val merchant: String? = null,
    val paymentMethod: PaymentMethod? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = "",
    val isValid: Boolean = amountMinor != null && amountMinor > 0
)

/**
 * Natural language sentence parser for 1-sentence transaction entry.
 * E.g.: "45 coffee at blue tokai card" -> amount=₹45, merchant="Blue Tokai", category="Coffee", method=CREDIT_CARD
 * E.g.: "₹250 groceries at nature basket upi" -> amount=₹250, merchant="Nature Basket", method=UPI
 * E.g.: "salary 85000 bank" -> amount=₹85,000, type=INCOME, method=NET_BANKING
 * E.g.: "2.5k petrol shell by card" -> amount=₹2,500, merchant="Shell", method=CREDIT_CARD
 */
object SentenceParser {

    private val AMOUNT_PATTERN = Pattern.compile(
        "(?:[₹$€£]|Rs\\.?|INR|USD|EUR)?\\s*(\\d+(?:\\.\\d{1,2})?)\\s*(k|K|L|l|lakh|lac|cr|crore|m|M)?\\b"
    )

    private val AT_MERCHANT_PATTERN = Pattern.compile(
        "\\b(?:at|to|from|in)\\s+([a-zA-Z0-9&'\\s]+?)(?=\\s+(?:by|via|with|using|cash|upi|card|yesterday|today|$))",
        Pattern.CASE_INSENSITIVE
    )

    private val INCOME_KEYWORDS = setOf(
        "salary", "income", "stipend", "dividend", "bonus", "refund", "cashback", "received", "earned", "freelance"
    )

    private val TRANSFER_KEYWORDS = setOf(
        "transfer", "transferred", "sent to account", "moved to", "atm withdrawal", "self transfer"
    )

    private val YESTERDAY_KEYWORDS = setOf("yesterday", "yday")

    /**
     * Parse natural language text into structured transaction fields.
     */
    fun parse(
        text: String,
        availableCategories: List<CategoryEntity> = emptyList(),
        defaultCurrency: String = "INR"
    ): ParsedSentence {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return ParsedSentence(rawText = text, currencyCode = defaultCurrency)
        }

        val lower = trimmed.lowercase(Locale.ROOT)

        // 1. Detect Currency
        var detectedCurrency = defaultCurrency
        when {
            trimmed.contains("$") || lower.contains("usd") -> detectedCurrency = "USD"
            trimmed.contains("€") || lower.contains("eur") -> detectedCurrency = "EUR"
            trimmed.contains("£") || lower.contains("gbp") -> detectedCurrency = "GBP"
            trimmed.contains("₹") || lower.contains("inr") || lower.contains("rs") -> detectedCurrency = "INR"
        }

        // 2. Parse Amount
        var amountMinor: Long? = null
        var amountMatchedStr: String? = null
        val matcher = AMOUNT_PATTERN.matcher(trimmed)
        if (matcher.find()) {
            val numStr = matcher.group(1)
            val suffix = matcher.group(2)?.lowercase(Locale.ROOT)
            val baseVal = numStr?.toDoubleOrNull()
            if (baseVal != null) {
                val multiplier = when (suffix) {
                    "k" -> 1_000.0
                    "l", "lakh", "lac" -> 100_000.0
                    "cr", "crore" -> 10_000_000.0
                    "m" -> 1_000_000.0
                    else -> 1.0
                }
                amountMinor = (baseVal * multiplier * 100).toLong()
                amountMatchedStr = matcher.group(0)
            }
        }

        // 3. Detect Transaction Type
        val type = when {
            TRANSFER_KEYWORDS.any { lower.contains(it) } -> TransactionType.TRANSFER
            INCOME_KEYWORDS.any { lower.contains(it) } -> TransactionType.INCOME
            else -> TransactionType.EXPENSE
        }

        // 4. Detect Payment Method
        val paymentMethod = when {
            lower.contains("upi") || lower.contains("gpay") || lower.contains("phonepe") || lower.contains("paytm") -> PaymentMethod.UPI
            lower.contains("cash") -> PaymentMethod.CASH
            lower.contains("credit card") || lower.contains("cc") -> PaymentMethod.CREDIT_CARD
            lower.contains("debit card") || lower.contains("dc") -> PaymentMethod.DEBIT_CARD
            lower.contains("card") -> PaymentMethod.CREDIT_CARD
            lower.contains("net banking") || lower.contains("netbanking") || lower.contains("bank") || lower.contains("neft") || lower.contains("imps") -> PaymentMethod.NET_BANKING
            lower.contains("wallet") -> PaymentMethod.WALLET
            lower.contains("cheque") || lower.contains("check") -> PaymentMethod.CHEQUE
            else -> null
        }

        // 5. Detect Date (Yesterday vs Today)
        val timestamp = if (YESTERDAY_KEYWORDS.any { lower.contains(it) }) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            cal.timeInMillis
        } else {
            System.currentTimeMillis()
        }

        // 6. Detect Merchant
        var merchant: String? = null
        val merchantMatcher = AT_MERCHANT_PATTERN.matcher(trimmed)
        if (merchantMatcher.find()) {
            val candidate = merchantMatcher.group(1)?.trim()
            if (!candidate.isNullOrBlank()) {
                merchant = candidate.split(" ").joinToString(" ") { word ->
                    word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                }
            }
        }

        // Known brand fallbacks if no "at/to" preposition used
        if (merchant == null) {
            val knownBrands = listOf(
                "Blue Tokai", "Starbucks", "Uber", "Ola", "Swiggy", "Zomato",
                "Amazon", "Flipkart", "Netflix", "Spotify", "Shell", "Nature's Basket",
                "Nature Basket", "Apple", "Google", "McDonalds", "Blinkit", "Zepto", "Instamart"
            )
            for (brand in knownBrands) {
                if (lower.contains(brand.lowercase(Locale.ROOT))) {
                    merchant = brand
                    break
                }
            }
        }

        // 7. Detect Category
        var matchedCategoryId: String? = null
        var matchedCategoryName: String? = null

        // Try exact match with available category entities first
        for (cat in availableCategories) {
            if (lower.contains(cat.name.lowercase(Locale.ROOT))) {
                matchedCategoryId = cat.id
                matchedCategoryName = cat.name
                break
            }
        }

        // Fallback keyword category association
        if (matchedCategoryId == null && matchedCategoryName == null) {
            val keywordCategoryMap = mapOf(
                "coffee" to "Coffee & Dining",
                "tea" to "Coffee & Dining",
                "cafe" to "Coffee & Dining",
                "lunch" to "Food & Dining",
                "dinner" to "Food & Dining",
                "breakfast" to "Food & Dining",
                "food" to "Food & Dining",
                "restaurant" to "Food & Dining",
                "groceries" to "Groceries",
                "grocery" to "Groceries",
                "supermarket" to "Groceries",
                "veggies" to "Groceries",
                "milk" to "Groceries",
                "fuel" to "Transport & Fuel",
                "petrol" to "Transport & Fuel",
                "diesel" to "Transport & Fuel",
                "uber" to "Transport & Fuel",
                "ola" to "Transport & Fuel",
                "metro" to "Transport & Fuel",
                "cab" to "Transport & Fuel",
                "shopping" to "Shopping",
                "clothes" to "Shopping",
                "shoes" to "Shopping",
                "rent" to "Bills & Rent",
                "electricity" to "Utilities",
                "wifi" to "Utilities",
                "broadband" to "Utilities",
                "movie" to "Entertainment",
                "movies" to "Entertainment",
                "salary" to "Salary",
                "stipend" to "Salary"
            )

            for ((kw, catName) in keywordCategoryMap) {
                if (lower.contains(kw)) {
                    matchedCategoryName = catName
                    val existing = availableCategories.find { it.name.equals(catName, ignoreCase = true) }
                    matchedCategoryId = existing?.id
                    break
                }
            }
        }

        // 8. Note: Clean remaining text or use full sentence
        var note = trimmed
        if (amountMatchedStr != null) {
            note = note.replace(amountMatchedStr, "")
        }
        note = note.replace(Regex("\\b(at|to|from|by|via|using|card|cash|upi|bank|yesterday|today)\\b", RegexOption.IGNORE_CASE), "")
            .replace(Regex("\\s+"), " ")
            .trim()

        if (note.isBlank()) {
            note = merchant ?: matchedCategoryName ?: "Quick entry"
        }

        return ParsedSentence(
            rawText = trimmed,
            amountMinor = amountMinor,
            currencyCode = detectedCurrency,
            type = type,
            categoryId = matchedCategoryId,
            categoryName = matchedCategoryName,
            merchant = merchant,
            paymentMethod = paymentMethod,
            timestamp = timestamp,
            note = note
        )
    }
}
