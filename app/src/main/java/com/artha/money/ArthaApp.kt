package com.artha.money

import android.app.Application
import com.artha.data.preferences.UserPreferencesRepository
import com.artha.data.repository.AccountRepository
import com.artha.data.repository.CategoryRepository
import com.artha.data.repository.TransactionRepository
import com.artha.database.ArthaDatabase
import com.artha.database.entity.AccountEntity
import com.artha.model.AccountType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

class ArthaApp : Application() {

    lateinit var database: ArthaDatabase
        private set

    lateinit var accountRepository: AccountRepository
        private set

    lateinit var categoryRepository: CategoryRepository
        private set

    lateinit var transactionRepository: TransactionRepository
        private set

    lateinit var preferencesRepository: UserPreferencesRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize encrypted Room database
        database = ArthaDatabase.getInstance(this, useEncryption = true)

        // Initialize repositories
        accountRepository = AccountRepository(database)
        categoryRepository = CategoryRepository(database)
        transactionRepository = TransactionRepository(database)
        preferencesRepository = UserPreferencesRepository(this)

        // Asynchronously seed default categories & starter accounts if empty
        CoroutineScope(Dispatchers.IO).launch {
            categoryRepository.seedDefaultCategoriesIfEmpty()
            seedStarterAccountsIfEmpty()
        }
    }

    private suspend fun seedStarterAccountsIfEmpty() {
        val accounts = accountRepository.getActiveAccountsFlow().firstOrNull()
        if (accounts.isNullOrEmpty()) {
            val starterAccounts = listOf(
                AccountEntity(
                    id = "acc_primary_bank",
                    name = "Main Savings Bank",
                    type = AccountType.BANK,
                    currency = "INR",
                    openingBalanceMinor = 4500000L, // ₹45,000.00
                    currentBalanceMinor = 4500000L,
                    colorHex = "#2196F3"
                ),
                AccountEntity(
                    id = "acc_cash_wallet",
                    name = "Physical Cash",
                    type = AccountType.CASH,
                    currency = "INR",
                    openingBalanceMinor = 350000L, // ₹3,500.00
                    currentBalanceMinor = 350000L,
                    colorHex = "#4CAF50"
                ),
                AccountEntity(
                    id = "acc_primary_card",
                    name = "Rewards Credit Card",
                    type = AccountType.CREDIT_CARD,
                    currency = "INR",
                    openingBalanceMinor = 1450000L, // ₹14,500.00 outstanding
                    currentBalanceMinor = 1450000L,
                    creditLimitMinor = 15000000L, // ₹1,50,000.00 limit
                    billingDay = 15,
                    dueDay = 5,
                    colorHex = "#E91E63"
                )
            )
            starterAccounts.forEach { accountRepository.createAccount(it) }
        }
    }

    companion object {
        lateinit var instance: ArthaApp
            private set
    }
}
