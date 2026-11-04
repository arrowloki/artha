package com.artha.domain

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Pattern

data class ReceiptLineItem(
    val description: String,
    val amountMinor: Long
)

data class ParsedReceipt(
    val merchantName: String? = null,
    val totalMinor: Long? = null,
    val taxMinor: Long? = null,
    val dateMillis: Long? = null,
    val currencyCode: String = "INR",
    val lineItems: List<ReceiptLineItem> = emptyList(),
    val rawText: String = ""
)

/**
 * On-device Receipt OCR Text Analyzer.
 * Parses lines of text from receipt images to accurately extract merchant,
 * total amount, taxes (GST/VAT), date, and line items without network calls.
 */
object ReceiptOcrExtractor {

    private val TOTAL_REGEX = Pattern.compile(
        "\\b(?:grand\\s+total|net\\s+payable|net\\s+amount|amount\\s+due|balance\\s+due|final\\s+amount|(?<!sub)total)[^0-9]*([0-9]+(?:\\.[0-9]{1,2})?)",
        Pattern.CASE_INSENSITIVE
    )

    private val TAX_REGEX = Pattern.compile(
        "(?:tax|gst|cgst|sgst|vat|service\\s+tax)[^0-9]*([0-9]+(?:\\.[0-9]{1,2})?)",
        Pattern.CASE_INSENSITIVE
    )

    private val DATE_PATTERNS = listOf(
        Pair(Pattern.compile("\\b(\\d{2})[/.-](\\d{2})[/.-](\\d{4})\\b"), SimpleDateFormat("dd/MM/yyyy", Locale.US)),
        Pair(Pattern.compile("\\b(\\d{4})[/.-](\\d{2})[/.-](\\d{2})\\b"), SimpleDateFormat("yyyy/MM/dd", Locale.US)),
        Pair(Pattern.compile("\\b(\\d{2})[\\s-]([a-zA-Z]{3})[\\s-](\\d{4})\\b"), SimpleDateFormat("dd-MMM-yyyy", Locale.US))
    )

    fun extract(rawText: String): ParsedReceipt {
        if (rawText.isBlank()) return ParsedReceipt()

        val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }

        var merchant: String? = null
        var totalMinor: Long? = null
        var taxMinor: Long? = null
        var dateMillis: Long? = null
        val lineItems = mutableListOf<ReceiptLineItem>()

        // 1. Merchant name is typically in the first 1-3 lines (excluding invoice/order labels)
        for (line in lines.take(4)) {
            val lower = line.lowercase(Locale.ROOT)
            if (!lower.contains("invoice") && !lower.contains("receipt") && !lower.contains("tax") && !lower.contains("bill") && line.length >= 3) {
                merchant = line
                break
            }
        }

        // 2. Scan lines for Total & Tax
        for (line in lines) {
            if (totalMinor == null) {
                val totalMatcher = TOTAL_REGEX.matcher(line)
                if (totalMatcher.find()) {
                    val amtStr = totalMatcher.group(1)
                    val dbl = amtStr?.toDoubleOrNull()
                    if (dbl != null) {
                        totalMinor = (dbl * 100).toLong()
                    }
                }
            }

            if (taxMinor == null) {
                val taxMatcher = TAX_REGEX.matcher(line)
                if (taxMatcher.find()) {
                    val amtStr = taxMatcher.group(1)
                    val dbl = amtStr?.toDoubleOrNull()
                    if (dbl != null) {
                        taxMinor = (dbl * 100).toLong()
                    }
                }
            }

            // Date match
            if (dateMillis == null) {
                for ((pattern, df) in DATE_PATTERNS) {
                    val dm = pattern.matcher(line)
                    if (dm.find()) {
                        val matchedStr = dm.group(0)?.replace(".", "/")?.replace("-", "/")
                        if (matchedStr != null) {
                            runCatching {
                                dateMillis = df.parse(matchedStr)?.time
                            }
                        }
                    }
                }
            }

            // Line items: look for "Item Description ... Price"
            val itemMatcher = Pattern.compile("^([a-zA-Z0-9\\s&'-]+?)\\s+(\\d+\\.\\d{2})$").matcher(line)
            if (itemMatcher.find()) {
                val desc = itemMatcher.group(1)?.trim()
                val priceStr = itemMatcher.group(2)
                val price = priceStr?.toDoubleOrNull()
                if (desc != null && price != null) {
                    val lowerDesc = desc.lowercase(Locale.ROOT)
                    val isSummaryWord = lowerDesc.contains("total") ||
                        lowerDesc.contains("subtotal") ||
                        lowerDesc.contains("tax") ||
                        lowerDesc.contains("gst") ||
                        lowerDesc.contains("vat") ||
                        lowerDesc.contains("discount") ||
                        lowerDesc.contains("tip") ||
                        lowerDesc.contains("round")
                    if (!isSummaryWord) {
                        lineItems.add(ReceiptLineItem(desc, (price * 100).toLong()))
                    }
                }
            }
        }

        return ParsedReceipt(
            merchantName = merchant,
            totalMinor = totalMinor,
            taxMinor = taxMinor,
            dateMillis = dateMillis,
            lineItems = lineItems,
            rawText = rawText
        )
    }
}
