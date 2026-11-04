package com.artha.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.artha.database.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts WHERE is_archived = 0 ORDER BY sort_order ASC, name ASC")
    fun getActiveAccountsFlow(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts ORDER BY sort_order ASC, name ASC")
    fun getAllAccountsFlow(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: String): AccountEntity?

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    fun getAccountFlow(id: String): Flow<AccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<AccountEntity>)

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Query("UPDATE accounts SET current_balance_minor = current_balance_minor + :deltaMinor, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateAccountBalance(id: String, deltaMinor: Long, updatedAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteAccount(account: AccountEntity)

    @Query("SELECT SUM(current_balance_minor) FROM accounts WHERE is_archived = 0 AND currency = :currency")
    suspend fun getTotalBalance(currency: String): Long?
}
