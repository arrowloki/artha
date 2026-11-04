package com.artha.data.repository

import com.artha.database.ArthaDatabase
import com.artha.database.entity.BillSubscriptionEntity
import kotlinx.coroutines.flow.Flow

class BillSubscriptionRepository(private val db: ArthaDatabase) {

    private val billDao = db.billSubscriptionDao()

    fun getActiveBillsFlow(): Flow<List<BillSubscriptionEntity>> = billDao.getActiveBillsFlow()

    fun getAllBillsFlow(): Flow<List<BillSubscriptionEntity>> = billDao.getAllBillsFlow()

    suspend fun saveBill(bill: BillSubscriptionEntity) {
        billDao.insertBill(bill)
    }

    suspend fun updateBill(bill: BillSubscriptionEntity) {
        billDao.updateBill(bill)
    }

    suspend fun markBillPaid(id: String, paidDate: Long, nextDueDate: Long) {
        billDao.markBillPaid(id, paidDate, nextDueDate)
    }

    suspend fun deleteBill(id: String) {
        billDao.deleteBill(id)
    }
}
