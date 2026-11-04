package com.artha.data.repository

import com.artha.database.ArthaDatabase
import com.artha.database.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

class AccountRepository(private val db: ArthaDatabase) {

    private val accountDao = db.accountDao()

    fun getActiveAccountsFlow(): Flow<List<AccountEntity>> = accountDao.getActiveAccountsFlow()

    fun getAllAccountsFlow(): Flow<List<AccountEntity>> = accountDao.getAllAccountsFlow()

    fun getAccountFlow(id: String): Flow<AccountEntity?> = accountDao.getAccountFlow(id)

    suspend fun getAccountById(id: String): AccountEntity? = accountDao.getAccountById(id)

    suspend fun createAccount(account: AccountEntity) {
        accountDao.insertAccount(account)
    }

    suspend fun updateAccount(account: AccountEntity) {
        accountDao.updateAccount(account.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun archiveAccount(id: String) {
        val existing = accountDao.getAccountById(id) ?: return
        accountDao.updateAccount(existing.copy(isArchived = true, updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteAccount(account: AccountEntity) {
        accountDao.deleteAccount(account)
    }

    suspend fun getTotalBalance(currency: String): Long {
        return accountDao.getTotalBalance(currency) ?: 0L
    }
}
