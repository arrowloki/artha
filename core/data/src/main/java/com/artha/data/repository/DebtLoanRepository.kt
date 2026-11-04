package com.artha.data.repository

import com.artha.database.ArthaDatabase
import com.artha.database.entity.DebtLoanEntity
import com.artha.database.entity.DebtPaymentEntity
import kotlinx.coroutines.flow.Flow

class DebtLoanRepository(private val db: ArthaDatabase) {

    private val debtDao = db.debtLoanDao()

    fun getAllDebtsFlow(): Flow<List<DebtLoanEntity>> = debtDao.getAllDebtsFlow()

    suspend fun getDebtById(id: String): DebtLoanEntity? = debtDao.getDebtById(id)

    fun getPaymentsForDebtFlow(debtId: String): Flow<List<DebtPaymentEntity>> = debtDao.getPaymentsForDebtFlow(debtId)

    suspend fun saveDebt(debt: DebtLoanEntity) {
        debtDao.insertDebt(debt)
    }

    suspend fun updateDebt(debt: DebtLoanEntity) {
        debtDao.updateDebt(debt)
    }

    suspend fun recordPayment(payment: DebtPaymentEntity) {
        debtDao.recordDebtPayment(payment)
    }
}
