package com.artha.model

enum class AccountType(val isLiability: Boolean) {
    CASH(isLiability = false),
    BANK(isLiability = false),
    CREDIT_CARD(isLiability = true),
    WALLET(isLiability = false),
    SAVINGS(isLiability = false),
    INVESTMENT(isLiability = false),
    LOAN(isLiability = true),
    CUSTOM(isLiability = false)
}

enum class PaymentMethod(val displayName: String) {
    CASH("Cash"),
    UPI("UPI"),
    NET_BANKING("Net Banking"),
    DEBIT_CARD("Debit Card"),
    CREDIT_CARD("Credit Card"),
    WALLET("Wallet"),
    CHEQUE("Cheque"),
    OTHER("Other")
}

enum class TransactionType {
    EXPENSE,
    INCOME,
    TRANSFER
}

enum class TransactionStatus {
    COMPLETED,
    PENDING,
    DRAFT,
    CLEARED
}

enum class WorthItStatus {
    UNRATED,
    WORTH_IT,
    NOT_WORTH_IT,
    INDIFFERENT
}

enum class BudgetPeriod {
    WEEKLY,
    MONTHLY,
    CUSTOM
}

enum class RecurrenceFrequency {
    DAILY,
    WEEKLY,
    BIWEEKLY,
    MONTHLY,
    QUARTERLY,
    YEARLY,
    CUSTOM_CRON
}

enum class DebtType {
    LENT,
    BORROWED,
    EMI_LOAN
}

enum class NumberGroupingStyle {
    AUTO,
    INDIAN_LAKH_CRORE,
    WESTERN_MILLION_BILLION
}
