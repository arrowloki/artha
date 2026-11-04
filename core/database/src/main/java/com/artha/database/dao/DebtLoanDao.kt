package com.artha.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.artha.database.entity.DebtLoanEntity
import com.artha.database.entity.DebtPaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtLoanDao {

    @Query("SELECT * FROM debts_loans ORDER BY start_date DESC")
    fun getAllDebtsFlow(): Flow<List<DebtLoanEntity>>

    @Query("SELECT * FROM debts_loans WHERE id = :id LIMIT 1")
    suspend fun getDebtById(id: String): DebtLoanEntity?

    @Query("SELECT * FROM debt_payments WHERE debt_id = :debtId ORDER BY payment_date DESC")
    fun getPaymentsForDebtFlow(debtId: String): Flow<List<DebtPaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtLoanEntity)

    @Update
    suspend fun updateDebt(debt: DebtLoanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: DebtPaymentEntity)

    @Query("UPDATE debts_loans SET remaining_minor = remaining_minor - :principalReduction WHERE id = :id")
    suspend fun reduceRemainingDebt(id: String, principalReduction: Long)

    @Transaction
    suspend fun recordDebtPayment(payment: DebtPaymentEntity) {
        insertPayment(payment)
        reduceRemainingDebt(payment.debtId, payment.principalComponentMinor)
    }
}
