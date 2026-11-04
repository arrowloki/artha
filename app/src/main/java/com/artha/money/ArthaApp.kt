package com.artha.money

import android.app.Application
import com.artha.data.preferences.UserPreferencesRepository
import com.artha.data.repository.AccountRepository
import com.artha.data.repository.TransactionRepository
import com.artha.database.ArthaDatabase

class ArthaApp : Application() {

    lateinit var database: ArthaDatabase
        private set

    lateinit var accountRepository: AccountRepository
        private set

    lateinit var transactionRepository: TransactionRepository
        private set

    lateinit var preferencesRepository: UserPreferencesRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize encrypted Room database
        database = ArthaDatabase.getInstance(this, useEncryption = true)

        // Initialize repositories
        accountRepository = AccountRepository(database)
        transactionRepository = TransactionRepository(database)
        preferencesRepository = UserPreferencesRepository(this)
    }

    companion object {
        lateinit var instance: ArthaApp
            private set
    }
}
