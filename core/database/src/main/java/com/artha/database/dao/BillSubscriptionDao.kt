package com.artha.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.artha.database.entity.BillSubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BillSubscriptionDao {

    @Query("SELECT * FROM bills_subscriptions WHERE is_active = 1 ORDER BY next_due_date ASC")
    fun getActiveBillsFlow(): Flow<List<BillSubscriptionEntity>>

    @Query("SELECT * FROM bills_subscriptions ORDER BY next_due_date ASC")
    fun getAllBillsFlow(): Flow<List<BillSubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillSubscriptionEntity)

    @Update
    suspend fun updateBill(bill: BillSubscriptionEntity)

    @Query("UPDATE bills_subscriptions SET last_paid_date = :paidDate, next_due_date = :nextDueDate WHERE id = :id")
    suspend fun markBillPaid(id: String, paidDate: Long, nextDueDate: Long)

    @Query("DELETE FROM bills_subscriptions WHERE id = :id")
    suspend fun deleteBill(id: String)
}
