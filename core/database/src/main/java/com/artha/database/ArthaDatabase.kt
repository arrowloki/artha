package com.artha.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.artha.database.converter.RoomTypeConverters
import com.artha.database.dao.AccountDao
import com.artha.database.dao.AggregateDao
import com.artha.database.dao.BillSubscriptionDao
import com.artha.database.dao.BudgetDao
import com.artha.database.dao.CategoryDao
import com.artha.database.dao.DebtLoanDao
import com.artha.database.dao.GoalDao
import com.artha.database.dao.RecurringRuleDao
import com.artha.database.dao.RuleDao
import com.artha.database.dao.TransactionDao
import com.artha.database.entity.AccountEntity
import com.artha.database.entity.AccountMonthlyAggregateEntity
import com.artha.database.entity.BillSubscriptionEntity
import com.artha.database.entity.BudgetCategoryCrossRef
import com.artha.database.entity.BudgetEntity
import com.artha.database.entity.CategoryEntity
import com.artha.database.entity.CategoryMonthlyAggregateEntity
import com.artha.database.entity.DebtLoanEntity
import com.artha.database.entity.DebtPaymentEntity
import com.artha.database.entity.ExchangeRateEntity
import com.artha.database.entity.GoalEntity
import com.artha.database.entity.MonthlyAggregateEntity
import com.artha.database.entity.RecurringRuleEntity
import com.artha.database.entity.RuleEntity
import com.artha.database.entity.TagEntity
import com.artha.database.entity.TransactionEntity
import com.artha.database.entity.TransactionFtsEntity
import com.artha.database.entity.TransactionSplitEntity
import com.artha.database.entity.TransactionTagCrossRef
import com.artha.database.security.DatabaseKeyManager
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        TransactionSplitEntity::class,
        TagEntity::class,
        TransactionTagCrossRef::class,
        BudgetEntity::class,
        BudgetCategoryCrossRef::class,
        RecurringRuleEntity::class,
        BillSubscriptionEntity::class,
        DebtLoanEntity::class,
        DebtPaymentEntity::class,
        GoalEntity::class,
        RuleEntity::class,
        ExchangeRateEntity::class,
        MonthlyAggregateEntity::class,
        CategoryMonthlyAggregateEntity::class,
        AccountMonthlyAggregateEntity::class,
        TransactionFtsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomTypeConverters::class)
abstract class ArthaDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun aggregateDao(): AggregateDao
    abstract fun budgetDao(): BudgetDao
    abstract fun recurringRuleDao(): RecurringRuleDao
    abstract fun billSubscriptionDao(): BillSubscriptionDao
    abstract fun debtLoanDao(): DebtLoanDao
    abstract fun goalDao(): GoalDao
    abstract fun ruleDao(): RuleDao

    companion object {
        const val DATABASE_NAME = "artha_encrypted.db"

        @Volatile
        private var INSTANCE: ArthaDatabase? = null

        fun getInstance(context: Context, useEncryption: Boolean = true): ArthaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context.applicationContext, useEncryption).also { INSTANCE = it }
            }
        }

        fun buildDatabase(context: Context, useEncryption: Boolean = true): ArthaDatabase {
            val builder = Room.databaseBuilder(
                context,
                ArthaDatabase::class.java,
                DATABASE_NAME
            ).setJournalMode(JournalMode.WRITE_AHEAD_LOGGING)

            if (useEncryption) {
                val keyManager = DatabaseKeyManager(context)
                val passphrase = keyManager.getOrCreateDatabasePassphrase()
                val factory = SupportFactory(passphrase)
                builder.openHelperFactory(factory)
            }

            return builder.build()
        }

        fun buildInMemory(context: Context): ArthaDatabase {
            return Room.inMemoryDatabaseBuilder(
                context,
                ArthaDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}
