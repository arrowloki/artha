package com.artha.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artha.data.repository.AccountRepository
import com.artha.data.repository.CategoryRepository
import com.artha.data.repository.TransactionRepository
import com.artha.database.entity.AccountEntity
import com.artha.database.entity.CategoryEntity
import com.artha.database.entity.TransactionEntity
import com.artha.model.CurrencyConfig
import com.artha.model.Money
import com.artha.model.PaymentMethod
import com.artha.model.TransactionType
import com.artha.ui.components.MathExpressionEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID

data class SplitInput(
    val categoryId: String,
    val amountMinor: Long,
    val note: String = ""
)

data class TransactionFilterState(
    val query: String = "",
    val type: TransactionType? = null,
    val accountId: String? = null,
    val categoryId: String? = null,
    val startDate: Long? = null,
    val endDate: Long? = null
)

class TransactionsViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository? = null
) : ViewModel() {

    val accounts: StateFlow<List<AccountEntity>> = accountRepository.getActiveAccountsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val categories: StateFlow<List<CategoryEntity>> = (categoryRepository?.getActiveCategoriesFlow()
        ?: kotlinx.coroutines.flow.flowOf(emptyList()))
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _filterState = MutableStateFlow(TransactionFilterState())
    val filterState: StateFlow<TransactionFilterState> = _filterState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<TransactionEntity>?>(null)

    // Transactions combined with filters
    val transactions: StateFlow<List<TransactionEntity>> = combine(
        transactionRepository.getRecentTransactionsFlow(limit = 200),
        _filterState,
        _searchResults
    ) { recent, filter, search ->
        if (search != null) {
            return@combine applyFilter(search, filter)
        }
        applyFilter(recent, filter)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Undo buffer for last deleted transaction
    private var lastDeletedTransaction: TransactionEntity? = null
    private val _showUndoSnackbar = MutableStateFlow(false)
    val showUndoSnackbar: StateFlow<Boolean> = _showUndoSnackbar.asStateFlow()

    // Fast Add UI State
    private val _addExpression = MutableStateFlow("")
    val addExpression: StateFlow<String> = _addExpression.asStateFlow()

    private val _addEvaluatedAmountMinor = MutableStateFlow<Long?>(null)
    val addEvaluatedAmountMinor: StateFlow<Long?> = _addEvaluatedAmountMinor.asStateFlow()

    private val _addType = MutableStateFlow(TransactionType.EXPENSE)
    val addType: StateFlow<TransactionType> = _addType.asStateFlow()

    private val _addAccountId = MutableStateFlow<String?>(null)
    val addAccountId: StateFlow<String?> = _addAccountId.asStateFlow()

    private val _addToAccountId = MutableStateFlow<String?>(null)
    val addToAccountId: StateFlow<String?> = _addToAccountId.asStateFlow()

    private val _addCategoryId = MutableStateFlow<String?>(null)
    val addCategoryId: StateFlow<String?> = _addCategoryId.asStateFlow()

    private val _addPaymentMethod = MutableStateFlow(PaymentMethod.UPI)
    val addPaymentMethod: StateFlow<PaymentMethod> = _addPaymentMethod.asStateFlow()

    private val _addNote = MutableStateFlow("")
    val addNote: StateFlow<String> = _addNote.asStateFlow()

    private val _addTimestamp = MutableStateFlow(System.currentTimeMillis())
    val addTimestamp: StateFlow<Long> = _addTimestamp.asStateFlow()

    private val _addSplits = MutableStateFlow<List<SplitInput>>(emptyList())
    val addSplits: StateFlow<List<SplitInput>> = _addSplits.asStateFlow()

    // Fast Add Keypad Handlers
    fun onKeypadDigit(digit: String) {
        val current = _addExpression.value
        val updated = current + digit
        _addExpression.value = updated
        evaluateExpression(updated)
    }

    fun onKeypadOperator(operator: String) {
        val current = _addExpression.value
        if (current.isNotEmpty() && current.last() !in "+-*/") {
            val updated = current + operator
            _addExpression.value = updated
        }
    }

    fun onKeypadBackspace() {
        val current = _addExpression.value
        if (current.isNotEmpty()) {
            val updated = current.dropLast(1)
            _addExpression.value = updated
            evaluateExpression(updated)
        }
    }

    fun onKeypadClear() {
        _addExpression.value = ""
        _addEvaluatedAmountMinor.value = null
    }

    fun onKeypadEvaluate() {
        val current = _addExpression.value
        val evaluated = MathExpressionEvaluator.evaluate(current)
        if (evaluated != null) {
            val plain = evaluated.stripTrailingZeros().toPlainString()
            _addExpression.value = plain
            evaluateExpression(plain)
        }
    }

    private fun evaluateExpression(expr: String) {
        val evaluated = MathExpressionEvaluator.evaluate(expr)
        if (evaluated != null && evaluated > BigDecimal.ZERO) {
            val currency = accounts.value.firstOrNull { it.id == _addAccountId.value }?.currency ?: "INR"
            val config = CurrencyConfig.forCode(currency)
            val minor = evaluated.movePointRight(config.decimalPlaces).toLong()
            _addEvaluatedAmountMinor.value = minor
        } else {
            _addEvaluatedAmountMinor.value = null
        }
    }

    fun setAddType(type: TransactionType) {
        _addType.value = type
    }

    fun setAddAccount(accountId: String) {
        _addAccountId.value = accountId
        evaluateExpression(_addExpression.value)
    }

    fun setAddToAccount(toAccountId: String) {
        _addToAccountId.value = toAccountId
    }

    fun setAddCategory(categoryId: String) {
        _addCategoryId.value = categoryId
    }

    fun setAddPaymentMethod(method: PaymentMethod) {
        _addPaymentMethod.value = method
    }

    fun setAddNote(note: String) {
        _addNote.value = note
    }

    fun setAddTimestamp(timestamp: Long) {
        _addTimestamp.value = timestamp
    }

    fun setSplits(splits: List<SplitInput>) {
        _addSplits.value = splits
    }

    /**
     * Fast Save: Under 50ms, zero main-thread blocking, atomic aggregates update.
     */
    fun saveFastTransaction(onSaved: () -> Unit) {
        val amountMinor = _addEvaluatedAmountMinor.value ?: return
        val currentAccounts = accounts.value
        val accountId = _addAccountId.value ?: currentAccounts.firstOrNull()?.id ?: return
        val account = currentAccounts.firstOrNull { it.id == accountId } ?: return

        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
            accountId = accountId,
            toAccountId = if (_addType.value == TransactionType.TRANSFER) _addToAccountId.value else null,
            categoryId = if (_addType.value != TransactionType.TRANSFER) _addCategoryId.value else null,
            amountMinor = amountMinor,
            currency = account.currency,
            type = _addType.value,
            paymentMethod = _addPaymentMethod.value,
            timestamp = _addTimestamp.value,
            note = _addNote.value.trim()
        )

        viewModelScope.launch {
            transactionRepository.recordTransaction(tx, "${tx.note} ${account.name}")
            // Reset Fast Add form
            _addExpression.value = ""
            _addEvaluatedAmountMinor.value = null
            _addNote.value = ""
            _addSplits.value = emptyList()
            onSaved()
        }
    }

    fun duplicateTransaction(tx: TransactionEntity) {
        val newTx = tx.copy(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            transactionRepository.recordTransaction(newTx)
        }
    }

    fun deleteTransaction(tx: TransactionEntity) {
        lastDeletedTransaction = tx
        _showUndoSnackbar.value = true
        viewModelScope.launch {
            transactionRepository.deleteTransaction(tx)
        }
    }

    fun undoLastDelete() {
        val tx = lastDeletedTransaction ?: return
        viewModelScope.launch {
            transactionRepository.recordTransaction(tx)
            lastDeletedTransaction = null
            _showUndoSnackbar.value = false
        }
    }

    fun dismissUndoSnackbar() {
        _showUndoSnackbar.value = false
        lastDeletedTransaction = null
    }

    // Search and Filter controls
    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(query = query)
        if (query.isBlank()) {
            _searchResults.value = null
        } else {
            viewModelScope.launch {
                val results = transactionRepository.searchTransactions("$query*")
                _searchResults.value = results
            }
        }
    }

    fun updateTypeFilter(type: TransactionType?) {
        _filterState.value = _filterState.value.copy(type = type)
    }

    fun updateAccountFilter(accountId: String?) {
        _filterState.value = _filterState.value.copy(accountId = accountId)
    }

    fun clearFilters() {
        _filterState.value = TransactionFilterState()
        _searchResults.value = null
    }

    private fun applyFilter(list: List<TransactionEntity>, filter: TransactionFilterState): List<TransactionEntity> {
        return list.filter { tx ->
            val matchType = filter.type == null || tx.type == filter.type
            val matchAcc = filter.accountId == null || tx.accountId == filter.accountId || tx.toAccountId == filter.accountId
            val matchCat = filter.categoryId == null || tx.categoryId == filter.categoryId
            val matchStart = filter.startDate == null || tx.timestamp >= filter.startDate
            val matchEnd = filter.endDate == null || tx.timestamp <= filter.endDate
            matchType && matchAcc && matchCat && matchStart && matchEnd
        }
    }
}
