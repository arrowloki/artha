package com.artha.data.repository

import com.artha.database.ArthaDatabase
import com.artha.database.entity.AccountEntity
import com.artha.database.entity.CategoryEntity
import com.artha.database.entity.RuleEntity
import com.artha.database.entity.TransactionEntity
import com.artha.domain.DataTransferEngine
import com.artha.model.AccountType
import com.artha.model.PaymentMethod
import com.artha.model.TransactionStatus
import com.artha.model.TransactionType
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class DataTransferRepository(
    private val db: ArthaDatabase,
    private val transactionRepository: TransactionRepository
) {

    private val accountDao = db.accountDao()
    private val categoryDao = db.categoryDao()
    private val transactionDao = db.transactionDao()
    private val ruleDao = db.ruleDao()

    suspend fun exportAllTransactionsCsv(): String {
        val txs = transactionDao.getAllTransactions()
        val accounts = accountDao.getAllAccounts().associate { it.id to it.name }
        val categories = categoryDao.getAllCategories().associate { it.id to it.name }
        return DataTransferEngine.exportTransactionsCsv(txs, accounts, categories)
    }

    suspend fun exportFullJsonBackup(): String {
        val root = JSONObject()
        root.put("app", "Artha")
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())

        // Accounts
        val accountsArray = JSONArray()
        for (acc in accountDao.getAllAccounts()) {
            val obj = JSONObject()
            obj.put("id", acc.id)
            obj.put("name", acc.name)
            obj.put("type", acc.type.name)
            obj.put("currency", acc.currency)
            obj.put("currentBalanceMinor", acc.currentBalanceMinor)
            obj.put("isArchived", acc.isArchived)
            accountsArray.put(obj)
        }
        root.put("accounts", accountsArray)

        // Categories
        val categoriesArray = JSONArray()
        for (cat in categoryDao.getAllCategories()) {
            val obj = JSONObject()
            obj.put("id", cat.id)
            obj.put("name", cat.name)
            obj.put("type", cat.type)
            obj.put("colorHex", cat.colorHex)
            obj.put("iconName", cat.iconName)
            categoriesArray.put(obj)
        }
        root.put("categories", categoriesArray)

        // Transactions
        val txsArray = JSONArray()
        for (tx in transactionDao.getAllTransactions()) {
            val obj = JSONObject()
            obj.put("id", tx.id)
            obj.put("accountId", tx.accountId)
            obj.put("toAccountId", tx.toAccountId)
            obj.put("categoryId", tx.categoryId)
            obj.put("amountMinor", tx.amountMinor)
            obj.put("currency", tx.currency)
            obj.put("type", tx.type.name)
            obj.put("paymentMethod", tx.paymentMethod.name)
            obj.put("timestamp", tx.timestamp)
            obj.put("note", tx.note)
            obj.put("isReconciled", tx.isReconciled)
            txsArray.put(obj)
        }
        root.put("transactions", txsArray)

        // Rules
        val rulesArray = JSONArray()
        for (rule in ruleDao.getAllRules()) {
            val obj = JSONObject()
            obj.put("id", rule.id)
            obj.put("name", rule.name)
            obj.put("conditionField", rule.conditionField)
            obj.put("conditionOperator", rule.conditionOperator)
            obj.put("conditionValue", rule.conditionValue)
            obj.put("actionField", rule.actionField)
            obj.put("actionValue", rule.actionValue)
            obj.put("isActive", rule.isActive)
            rulesArray.put(obj)
        }
        root.put("rules", rulesArray)

        return root.toString(2)
    }

    suspend fun importBankStatementCsv(
        csvContent: String,
        accountId: String,
        defaultCategoryId: String? = null
    ): Int {
        val parsedRows = DataTransferEngine.parseBankCsv(csvContent)
        if (parsedRows.isEmpty()) return 0

        val account = accountDao.getAccountById(accountId) ?: return 0

        for (row in parsedRows) {
            val tx = TransactionEntity(
                id = UUID.randomUUID().toString(),
                accountId = accountId,
                categoryId = defaultCategoryId,
                amountMinor = row.amountMinor,
                currency = account.currency,
                type = row.type,
                paymentMethod = if (row.description.contains("UPI", ignoreCase = true)) PaymentMethod.UPI else PaymentMethod.NET_BANKING,
                timestamp = row.timestamp,
                note = row.description,
                status = TransactionStatus.COMPLETED
            )
            transactionRepository.recordTransaction(tx, row.description)
        }

        return parsedRows.size
    }

    suspend fun importJsonBackup(jsonContent: String): Int {
        val root = JSONObject(jsonContent)
        var count = 0

        // Restore Categories
        if (root.has("categories")) {
            val catsArr = root.getJSONArray("categories")
            for (i in 0 until catsArr.length()) {
                val o = catsArr.getJSONObject(i)
                val cat = CategoryEntity(
                    id = o.getString("id"),
                    name = o.getString("name"),
                    type = o.optString("type", "EXPENSE"),
                    colorHex = o.optString("colorHex", "#FF9800"),
                    iconName = o.optString("iconName", "category")
                )
                categoryDao.insertCategory(cat)
                count++
            }
        }

        // Restore Accounts
        if (root.has("accounts")) {
            val accsArr = root.getJSONArray("accounts")
            for (i in 0 until accsArr.length()) {
                val o = accsArr.getJSONObject(i)
                val acc = AccountEntity(
                    id = o.getString("id"),
                    name = o.getString("name"),
                    type = runCatching { AccountType.valueOf(o.getString("type")) }.getOrDefault(AccountType.BANK),
                    currency = o.optString("currency", "INR"),
                    currentBalanceMinor = o.optLong("currentBalanceMinor", 0L),
                    isArchived = o.optBoolean("isArchived", false)
                )
                accountDao.insertAccount(acc)
                count++
            }
        }

        // Restore Rules
        if (root.has("rules")) {
            val rulesArr = root.getJSONArray("rules")
            for (i in 0 until rulesArr.length()) {
                val o = rulesArr.getJSONObject(i)
                val rule = RuleEntity(
                    id = o.getString("id"),
                    name = o.getString("name"),
                    conditionField = o.getString("conditionField"),
                    conditionOperator = o.getString("conditionOperator"),
                    conditionValue = o.getString("conditionValue"),
                    actionField = o.getString("actionField"),
                    actionValue = o.getString("actionValue"),
                    isActive = o.optBoolean("isActive", true)
                )
                ruleDao.insertRule(rule)
                count++
            }
        }

        return count
    }
}
