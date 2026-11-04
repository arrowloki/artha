package com.artha.domain

import com.artha.database.entity.TransactionEntity
import com.artha.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ParsedBankRow(
    val timestamp: Long,
    val amountMinor: Long,
    val type: TransactionType,
    val description: String,
    val balanceMinor: Long? = null
)

/**
 * High-performance, offline data import and export engine for CSV and bank statements.
 * Handles auto-detection of bank statement column formats (HDFC, ICICI, SBI, Chase, Standard Chartered).
 */
object DataTransferEngine {

    private val DATE_FORMATS = listOf(
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
        SimpleDateFormat("yyyy-MM-dd", Locale.US),
        SimpleDateFormat("dd/MM/yyyy", Locale.US),
        SimpleDateFormat("dd-MM-yyyy", Locale.US),
        SimpleDateFormat("MM/dd/yyyy", Locale.US),
        SimpleDateFormat("dd/MM/yy", Locale.US)
    )

    /**
     * Exports transactions into standard CSV format.
     */
    fun exportTransactionsCsv(
        transactions: List<TransactionEntity>,
        accountNames: Map<String, String>,
        categoryNames: Map<String, String>
    ): String {
        val sb = StringBuilder()
        sb.append("Date,Type,Amount,Currency,Account,Category,ToAccount,Note,PaymentMethod,Reconciled\n")

        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

        for (tx in transactions) {
            val dateStr = df.format(Date(tx.timestamp))
            val amountStr = String.format(Locale.US, "%.2f", tx.amountMinor / 100.0)
            val accName = escapeCsv(accountNames[tx.accountId] ?: tx.accountId)
            val catName = escapeCsv(categoryNames[tx.categoryId] ?: (tx.categoryId ?: ""))
            val toAccName = escapeCsv(if (tx.toAccountId != null) (accountNames[tx.toAccountId] ?: tx.toAccountId!!) else "")
            val noteStr = escapeCsv(tx.note)

            sb.append("$dateStr,${tx.type.name},$amountStr,${tx.currency},$accName,$catName,$toAccName,$noteStr,${tx.paymentMethod.name},${tx.isReconciled}\n")
        }

        return sb.toString()
    }

    /**
     * Auto-detects and parses bank CSV exports into structured transactions.
     */
    fun parseBankCsv(csvContent: String): List<ParsedBankRow> {
        val lines = csvContent.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()

        // 1. Locate header row
        var headerIndex = -1
        var dateCol = -1
        var descCol = -1
        var debitCol = -1
        var creditCol = -1
        var amountCol = -1
        var balanceCol = -1

        for (i in lines.indices.take(10)) {
            val tokens = parseCsvLine(lines[i]).map { it.lowercase(Locale.ROOT).trim() }
            for (col in tokens.indices) {
                val h = tokens[col]
                when {
                    dateCol == -1 && (h.contains("date") || h == "txn date" || h == "value date") -> dateCol = col
                    descCol == -1 && (h.contains("description") || h.contains("narration") || h.contains("particulars") || h.contains("remark") || h.contains("payee")) -> descCol = col
                    debitCol == -1 && (h.contains("debit") || h.contains("withdrawal") || h == "dr" || h.contains("dr amount")) -> debitCol = col
                    creditCol == -1 && (h.contains("credit") || h.contains("deposit") || h == "cr" || h.contains("cr amount")) -> creditCol = col
                    amountCol == -1 && (h == "amount" || h == "net amount") -> amountCol = col
                    balanceCol == -1 && (h.contains("balance") || h == "closing bal") -> balanceCol = col
                }
            }

            if (dateCol != -1 && descCol != -1 && (debitCol != -1 || creditCol != -1 || amountCol != -1)) {
                headerIndex = i
                break
            }
        }

        if (headerIndex == -1) return emptyList()

        val parsedRows = mutableListOf<ParsedBankRow>()

        for (i in (headerIndex + 1) until lines.size) {
            val tokens = parseCsvLine(lines[i])
            if (tokens.size <= maxOf(dateCol, descCol)) continue

            val dateStr = tokens.getOrNull(dateCol)?.trim() ?: continue
            val timestamp = parseDate(dateStr) ?: continue
            val desc = tokens.getOrNull(descCol)?.trim() ?: "Bank Transaction"

            var amountMinor = 0L
            var type = TransactionType.EXPENSE

            if (debitCol != -1 && creditCol != -1) {
                val debitStr = cleanAmountString(tokens.getOrNull(debitCol) ?: "")
                val creditStr = cleanAmountString(tokens.getOrNull(creditCol) ?: "")

                val debitVal = debitStr.toDoubleOrNull() ?: 0.0
                val creditVal = creditStr.toDoubleOrNull() ?: 0.0

                if (debitVal > 0.0) {
                    amountMinor = (debitVal * 100.0).toLong()
                    type = TransactionType.EXPENSE
                } else if (creditVal > 0.0) {
                    amountMinor = (creditVal * 100.0).toLong()
                    type = TransactionType.INCOME
                }
            } else if (amountCol != -1) {
                val amtStr = cleanAmountString(tokens.getOrNull(amountCol) ?: "")
                val amtVal = amtStr.toDoubleOrNull() ?: 0.0
                if (amtVal < 0.0) {
                    amountMinor = (Math.abs(amtVal) * 100.0).toLong()
                    type = TransactionType.EXPENSE
                } else {
                    amountMinor = (amtVal * 100.0).toLong()
                    type = TransactionType.INCOME
                }
            }

            val balanceStr = if (balanceCol != -1) cleanAmountString(tokens.getOrNull(balanceCol) ?: "") else ""
            val balanceVal = balanceStr.toDoubleOrNull()
            val balanceMinor = if (balanceVal != null) (balanceVal * 100.0).toLong() else null

            if (amountMinor > 0L) {
                parsedRows.add(
                    ParsedBankRow(
                        timestamp = timestamp,
                        amountMinor = amountMinor,
                        type = type,
                        description = desc,
                        balanceMinor = balanceMinor
                    )
                )
            }
        }

        return parsedRows
    }

    private fun cleanAmountString(raw: String): String {
        return raw.replace("₹", "")
            .replace("$", "")
            .replace("€", "")
            .replace("£", "")
            .replace(",", "")
            .replace("CR", "", ignoreCase = true)
            .replace("DR", "", ignoreCase = true)
            .trim()
    }

    private fun parseDate(str: String): Long? {
        val cleaned = str.trim()
        for (df in DATE_FORMATS) {
            val d = runCatching { df.parse(cleaned) }.getOrNull()
            if (d != null) return d.time
        }
        return null
    }

    /**
     * Splits a CSV line into tokens taking quoted strings with commas into account.
     */
    fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val cur = StringBuilder()
        var inQuotes = false

        for (c in line) {
            when (c) {
                '\"' -> inQuotes = !inQuotes
                ',' -> {
                    if (inQuotes) {
                        cur.append(c)
                    } else {
                        result.add(cur.toString().trim())
                        cur.clear()
                    }
                }
                else -> cur.append(c)
            }
        }
        result.add(cur.toString().trim())
        return result
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }
}
