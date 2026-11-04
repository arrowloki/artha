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

enum class PaymentMethod {
    CASH,
    UPI,
    NET_BANKING,
    DEBIT_CARD,
    CREDIT_CARD,
    WALLET,
    CHEQUE,
    OTHER
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
