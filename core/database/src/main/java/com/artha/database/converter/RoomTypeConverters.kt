package com.artha.database.converter

import androidx.room.TypeConverter
import com.artha.model.AccountType
import com.artha.model.BudgetPeriod
import com.artha.model.DebtType
import com.artha.model.NumberGroupingStyle
import com.artha.model.PaymentMethod
import com.artha.model.RecurrenceFrequency
import com.artha.model.TransactionStatus
import com.artha.model.TransactionType
import com.artha.model.WorthItStatus

class RoomTypeConverters {

    @TypeConverter
    fun fromAccountType(value: AccountType?): String? = value?.name

    @TypeConverter
    fun toAccountType(value: String?): AccountType? = value?.let { enumValueOf<AccountType>(it) }

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod?): String? = value?.name

    @TypeConverter
    fun toPaymentMethod(value: String?): PaymentMethod? = value?.let { enumValueOf<PaymentMethod>(it) }

    @TypeConverter
    fun fromTransactionType(value: TransactionType?): String? = value?.name

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? = value?.let { enumValueOf<TransactionType>(it) }

    @TypeConverter
    fun fromTransactionStatus(value: TransactionStatus?): String? = value?.name

    @TypeConverter
    fun toTransactionStatus(value: String?): TransactionStatus? = value?.let { enumValueOf<TransactionStatus>(it) }

    @TypeConverter
    fun fromWorthItStatus(value: WorthItStatus?): String? = value?.name

    @TypeConverter
    fun toWorthItStatus(value: String?): WorthItStatus? = value?.let { enumValueOf<WorthItStatus>(it) }

    @TypeConverter
    fun fromBudgetPeriod(value: BudgetPeriod?): String? = value?.name

    @TypeConverter
    fun toBudgetPeriod(value: String?): BudgetPeriod? = value?.let { enumValueOf<BudgetPeriod>(it) }

    @TypeConverter
    fun fromRecurrenceFrequency(value: RecurrenceFrequency?): String? = value?.name

    @TypeConverter
    fun toRecurrenceFrequency(value: String?): RecurrenceFrequency? = value?.let { enumValueOf<RecurrenceFrequency>(it) }

    @TypeConverter
    fun fromDebtType(value: DebtType?): String? = value?.name

    @TypeConverter
    fun toDebtType(value: String?): DebtType? = value?.let { enumValueOf<DebtType>(it) }

    @TypeConverter
    fun fromNumberGroupingStyle(value: NumberGroupingStyle?): String? = value?.name

    @TypeConverter
    fun toNumberGroupingStyle(value: String?): NumberGroupingStyle? = value?.let { enumValueOf<NumberGroupingStyle>(it) }
}
