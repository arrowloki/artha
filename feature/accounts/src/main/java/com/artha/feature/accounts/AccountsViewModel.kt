package com.artha.feature.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artha.data.repository.AccountRepository
import com.artha.data.repository.TransactionRepository
import com.artha.database.entity.AccountEntity
import com.artha.database.entity.TransactionEntity
import com.artha.domain.AccountBalanceState
import com.artha.domain.SingleCountCreditCardRule
import com.artha.model.AccountType
import com.artha.model.Money
import com.artha.model.PaymentMethod
import com.artha.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class NetWorthSummary(
    val totalAssets: Money,
    val totalLiabilities: Money,
    val netWorth: Money
)

class AccountsViewModel(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val singleCountRule = SingleCountCreditCardRule()

    val accounts: StateFlow<List<AccountEntity>> = accountRepository.getActiveAccountsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val assetAccounts: StateFlow<List<AccountEntity>> = accounts.map { list ->
        list.filterNot { it.type.isLiability }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val liabilityAccounts: StateFlow<List<AccountEntity>> = accounts.map { list ->
        list.filter { it.type.isLiability }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val netWorthSummary: StateFlow<NetWorthSummary> = accounts.map { list ->
        val currency = list.firstOrNull()?.currency ?: "INR"
        val balanceStates = list.map {
            AccountBalanceState(
                accountId = it.id,
                name = it.name,
                type = it.type,
                balanceMinor = it.currentBalanceMinor,
                currency = it.currency,
                creditLimitMinor = it.creditLimitMinor
            )
        }
        val totalAssetsMinor = balanceStates.filterNot { it.type.isLiability }.sumOf { it.balanceMinor }
        val totalLiabilitiesMinor = balanceStates.filter { it.type.isLiability }.sumOf { it.balanceMinor }
        val netWorth = singleCountRule.calculateNetWorth(balanceStates, currency)

        NetWorthSummary(
            totalAssets = Money(totalAssetsMinor, currency),
            totalLiabilities = Money(totalLiabilitiesMinor, currency),
            netWorth = netWorth
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        NetWorthSummary(Money.zero(), Money.zero(), Money.zero())
    )

    fun createAccount(
        name: String,
        type: AccountType,
        currency: String = "INR",
        openingBalanceMinor: Long = 0L,
        creditLimitMinor: Long = 0L,
        billingDay: Int = 1,
        dueDay: Int = 20
    ) {
        val account = AccountEntity(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            type = type,
            currency = currency,
            openingBalanceMinor = openingBalanceMinor,
            currentBalanceMinor = openingBalanceMinor,
            creditLimitMinor = creditLimitMinor,
            billingDay = billingDay,
            dueDay = dueDay,
            colorHex = when (type) {
                AccountType.BANK -> "#2196F3"
                AccountType.CREDIT_CARD -> "#E91E63"
                AccountType.CASH -> "#4CAF50"
                AccountType.WALLET -> "#9C27B0"
                AccountType.INVESTMENT -> "#FF9800"
                AccountType.LOAN -> "#F44336"
                else -> "#607D8B"
            }
        )
        viewModelScope.launch {
            accountRepository.createAccount(account)
        }
    }

    /**
     * Dedicated Pay Credit Card Bill Flow:
     * Creates an atomic TRANSFER from Bank/Asset account to Credit Card liability.
     * Enforces the Single-Count Credit Card rule: never counted as an expense!
     */
    fun payCreditCardBill(
        cardAccount: AccountEntity,
        fromAccountId: String,
        amountMinor: Long,
        onSuccess: () -> Unit
    ) {
        val transferTx = TransactionEntity(
            id = UUID.randomUUID().toString(),
            accountId = fromAccountId, // Bank
            toAccountId = cardAccount.id, // Card
            categoryId = null,
            amountMinor = amountMinor,
            currency = cardAccount.currency,
            type = TransactionType.TRANSFER,
            paymentMethod = PaymentMethod.NET_BANKING,
            timestamp = System.currentTimeMillis(),
            note = "Bill payment for ${cardAccount.name}"
        )

        viewModelScope.launch {
            transactionRepository.recordTransaction(transferTx, "Card payment transfer bill")
            onSuccess()
        }
    }

    /**
     * Reconcile Support: fetch transactions for specific account
     */
    fun getTransactionsForAccountFlow(accountId: String) =
        transactionRepository.getTransactionsByAccountFlow(accountId)

    /**
     * Toggle transaction reconciliation status against bank/card statement
     */
    fun toggleReconciled(tx: TransactionEntity) {
        viewModelScope.launch {
            transactionRepository.updateReconciledStatus(tx.id, !tx.isReconciled)
        }
    }
}
