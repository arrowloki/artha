package com.artha.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.FtsTableInfo;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.artha.database.dao.AccountDao;
import com.artha.database.dao.AccountDao_Impl;
import com.artha.database.dao.AggregateDao;
import com.artha.database.dao.AggregateDao_Impl;
import com.artha.database.dao.BillSubscriptionDao;
import com.artha.database.dao.BillSubscriptionDao_Impl;
import com.artha.database.dao.BudgetDao;
import com.artha.database.dao.BudgetDao_Impl;
import com.artha.database.dao.CategoryDao;
import com.artha.database.dao.CategoryDao_Impl;
import com.artha.database.dao.DebtLoanDao;
import com.artha.database.dao.DebtLoanDao_Impl;
import com.artha.database.dao.GoalDao;
import com.artha.database.dao.GoalDao_Impl;
import com.artha.database.dao.RecurringRuleDao;
import com.artha.database.dao.RecurringRuleDao_Impl;
import com.artha.database.dao.RuleDao;
import com.artha.database.dao.RuleDao_Impl;
import com.artha.database.dao.TransactionDao;
import com.artha.database.dao.TransactionDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ArthaDatabase_Impl extends ArthaDatabase {
  private volatile AccountDao _accountDao;

  private volatile CategoryDao _categoryDao;

  private volatile TransactionDao _transactionDao;

  private volatile AggregateDao _aggregateDao;

  private volatile BudgetDao _budgetDao;

  private volatile RecurringRuleDao _recurringRuleDao;

  private volatile BillSubscriptionDao _billSubscriptionDao;

  private volatile DebtLoanDao _debtLoanDao;

  private volatile GoalDao _goalDao;

  private volatile RuleDao _ruleDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `accounts` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `currency` TEXT NOT NULL, `opening_balance_minor` INTEGER NOT NULL, `current_balance_minor` INTEGER NOT NULL, `credit_limit_minor` INTEGER NOT NULL, `billing_day` INTEGER NOT NULL, `due_day` INTEGER NOT NULL, `grace_period_days` INTEGER NOT NULL, `apr_percentage` REAL NOT NULL, `is_archived` INTEGER NOT NULL, `color_hex` TEXT NOT NULL, `icon_name` TEXT NOT NULL, `sort_order` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_accounts_type_is_archived` ON `accounts` (`type`, `is_archived`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_accounts_sort_order` ON `accounts` (`sort_order`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`id` TEXT NOT NULL, `parent_id` TEXT, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `icon_name` TEXT NOT NULL, `color_hex` TEXT NOT NULL, `sort_order` INTEGER NOT NULL, `is_archived` INTEGER NOT NULL, `default_payment_method` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`parent_id`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_categories_parent_id` ON `categories` (`parent_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_categories_type_is_archived` ON `categories` (`type`, `is_archived`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_categories_sort_order` ON `categories` (`sort_order`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`id` TEXT NOT NULL, `account_id` TEXT NOT NULL, `to_account_id` TEXT, `category_id` TEXT, `amount_minor` INTEGER NOT NULL, `currency` TEXT NOT NULL, `exchange_rate_to_base` REAL NOT NULL, `amount_in_base_minor` INTEGER NOT NULL, `type` TEXT NOT NULL, `payment_method` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `note` TEXT NOT NULL, `is_excluded_from_budget` INTEGER NOT NULL, `is_excluded_from_reports` INTEGER NOT NULL, `is_reconciled` INTEGER NOT NULL, `status` TEXT NOT NULL, `receipt_image_path` TEXT, `recurring_rule_id` TEXT, `parent_transaction_id` TEXT, `transfer_counterpart_id` TEXT, `latitude` REAL, `longitude` REAL, `location_name` TEXT, `worth_it_status` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`account_id`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`to_account_id`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_timestamp` ON `transactions` (`timestamp`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_account_id_timestamp` ON `transactions` (`account_id`, `timestamp`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_to_account_id_timestamp` ON `transactions` (`to_account_id`, `timestamp`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_category_id_timestamp` ON `transactions` (`category_id`, `timestamp`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_type_timestamp` ON `transactions` (`type`, `timestamp`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `transaction_splits` (`id` TEXT NOT NULL, `transaction_id` TEXT NOT NULL, `category_id` TEXT NOT NULL, `amount_minor` INTEGER NOT NULL, `note` TEXT NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`transaction_id`) REFERENCES `transactions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transaction_splits_transaction_id` ON `transaction_splits` (`transaction_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transaction_splits_category_id` ON `transaction_splits` (`category_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tags` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `color_hex` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_tags_name` ON `tags` (`name`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `transaction_tags` (`transaction_id` TEXT NOT NULL, `tag_id` TEXT NOT NULL, PRIMARY KEY(`transaction_id`, `tag_id`), FOREIGN KEY(`transaction_id`) REFERENCES `transactions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`tag_id`) REFERENCES `tags`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transaction_tags_transaction_id` ON `transaction_tags` (`transaction_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transaction_tags_tag_id` ON `transaction_tags` (`tag_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `budgets` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `amount_minor` INTEGER NOT NULL, `currency` TEXT NOT NULL, `period_type` TEXT NOT NULL, `start_date` INTEGER NOT NULL, `end_date` INTEGER NOT NULL, `rollover_enabled` INTEGER NOT NULL, `envelope_mode` INTEGER NOT NULL, `alert_threshold_percentage` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_budgets_period_type_start_date_end_date` ON `budgets` (`period_type`, `start_date`, `end_date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `budget_categories` (`budget_id` TEXT NOT NULL, `category_id` TEXT NOT NULL, PRIMARY KEY(`budget_id`, `category_id`), FOREIGN KEY(`budget_id`) REFERENCES `budgets`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_budget_categories_budget_id` ON `budget_categories` (`budget_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_budget_categories_category_id` ON `budget_categories` (`category_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `recurring_rules` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `type` TEXT NOT NULL, `amount_minor` INTEGER NOT NULL, `currency` TEXT NOT NULL, `account_id` TEXT NOT NULL, `to_account_id` TEXT, `category_id` TEXT, `payment_method` TEXT NOT NULL, `frequency` TEXT NOT NULL, `interval` INTEGER NOT NULL, `next_due_date` INTEGER NOT NULL, `auto_apply` INTEGER NOT NULL, `reminder_days_before` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_recurring_rules_next_due_date` ON `recurring_rules` (`next_due_date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bills_subscriptions` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `amount_minor` INTEGER NOT NULL, `currency` TEXT NOT NULL, `category_id` TEXT, `account_id` TEXT NOT NULL, `frequency` TEXT NOT NULL, `due_day` INTEGER NOT NULL, `trial_end_date` INTEGER, `cancellation_url` TEXT, `is_auto_paid` INTEGER NOT NULL, `is_active` INTEGER NOT NULL, `last_paid_date` INTEGER, `next_due_date` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_bills_subscriptions_next_due_date` ON `bills_subscriptions` (`next_due_date`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_bills_subscriptions_is_active` ON `bills_subscriptions` (`is_active`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `debts_loans` (`id` TEXT NOT NULL, `person_or_institution` TEXT NOT NULL, `type` TEXT NOT NULL, `principal_minor` INTEGER NOT NULL, `remaining_minor` INTEGER NOT NULL, `interest_rate_apr` REAL NOT NULL, `tenure_months` INTEGER NOT NULL, `emi_amount_minor` INTEGER NOT NULL, `emi_due_day` INTEGER NOT NULL, `start_date` INTEGER NOT NULL, `end_date` INTEGER, `notes` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_debts_loans_type` ON `debts_loans` (`type`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `debt_payments` (`id` TEXT NOT NULL, `debt_id` TEXT NOT NULL, `transaction_id` TEXT, `amount_minor` INTEGER NOT NULL, `principal_component_minor` INTEGER NOT NULL, `interest_component_minor` INTEGER NOT NULL, `payment_date` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`debt_id`) REFERENCES `debts_loans`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`transaction_id`) REFERENCES `transactions`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_debt_payments_debt_id` ON `debt_payments` (`debt_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_debt_payments_transaction_id` ON `debt_payments` (`transaction_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `goals` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `target_amount_minor` INTEGER NOT NULL, `saved_amount_minor` INTEGER NOT NULL, `currency` TEXT NOT NULL, `target_date` INTEGER NOT NULL, `account_id` TEXT, `icon_name` TEXT NOT NULL, `color_hex` TEXT NOT NULL, `is_reached` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`account_id`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_goals_account_id` ON `goals` (`account_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_goals_is_reached` ON `goals` (`is_reached`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `rules` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `condition_field` TEXT NOT NULL, `condition_operator` TEXT NOT NULL, `condition_value` TEXT NOT NULL, `action_field` TEXT NOT NULL, `action_value` TEXT NOT NULL, `priority` INTEGER NOT NULL, `is_active` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_rules_is_active_priority` ON `rules` (`is_active`, `priority`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `exchange_rates` (`base_currency` TEXT NOT NULL, `target_currency` TEXT NOT NULL, `rate` REAL NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`base_currency`, `target_currency`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `monthly_aggregates` (`year_month` TEXT NOT NULL, `currency` TEXT NOT NULL, `total_income_minor` INTEGER NOT NULL, `total_expense_minor` INTEGER NOT NULL, `net_savings_minor` INTEGER NOT NULL, `transaction_count` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`year_month`, `currency`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `category_monthly_aggregates` (`year_month` TEXT NOT NULL, `category_id` TEXT NOT NULL, `currency` TEXT NOT NULL, `total_amount_minor` INTEGER NOT NULL, `transaction_count` INTEGER NOT NULL, PRIMARY KEY(`year_month`, `category_id`, `currency`), FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_category_monthly_aggregates_category_id` ON `category_monthly_aggregates` (`category_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `account_monthly_aggregates` (`year_month` TEXT NOT NULL, `account_id` TEXT NOT NULL, `starting_balance_minor` INTEGER NOT NULL, `ending_balance_minor` INTEGER NOT NULL, `total_inflow_minor` INTEGER NOT NULL, `total_outflow_minor` INTEGER NOT NULL, PRIMARY KEY(`year_month`, `account_id`), FOREIGN KEY(`account_id`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_account_monthly_aggregates_account_id` ON `account_monthly_aggregates` (`account_id`)");
        db.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `transactions_fts` USING FTS4(`transaction_id` TEXT NOT NULL, `note` TEXT NOT NULL, `searchable_text` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4b5e85484b88d238fde7e714cd7b4f16')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `accounts`");
        db.execSQL("DROP TABLE IF EXISTS `categories`");
        db.execSQL("DROP TABLE IF EXISTS `transactions`");
        db.execSQL("DROP TABLE IF EXISTS `transaction_splits`");
        db.execSQL("DROP TABLE IF EXISTS `tags`");
        db.execSQL("DROP TABLE IF EXISTS `transaction_tags`");
        db.execSQL("DROP TABLE IF EXISTS `budgets`");
        db.execSQL("DROP TABLE IF EXISTS `budget_categories`");
        db.execSQL("DROP TABLE IF EXISTS `recurring_rules`");
        db.execSQL("DROP TABLE IF EXISTS `bills_subscriptions`");
        db.execSQL("DROP TABLE IF EXISTS `debts_loans`");
        db.execSQL("DROP TABLE IF EXISTS `debt_payments`");
        db.execSQL("DROP TABLE IF EXISTS `goals`");
        db.execSQL("DROP TABLE IF EXISTS `rules`");
        db.execSQL("DROP TABLE IF EXISTS `exchange_rates`");
        db.execSQL("DROP TABLE IF EXISTS `monthly_aggregates`");
        db.execSQL("DROP TABLE IF EXISTS `category_monthly_aggregates`");
        db.execSQL("DROP TABLE IF EXISTS `account_monthly_aggregates`");
        db.execSQL("DROP TABLE IF EXISTS `transactions_fts`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsAccounts = new HashMap<String, TableInfo.Column>(17);
        _columnsAccounts.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("currency", new TableInfo.Column("currency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("opening_balance_minor", new TableInfo.Column("opening_balance_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("current_balance_minor", new TableInfo.Column("current_balance_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("credit_limit_minor", new TableInfo.Column("credit_limit_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("billing_day", new TableInfo.Column("billing_day", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("due_day", new TableInfo.Column("due_day", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("grace_period_days", new TableInfo.Column("grace_period_days", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("apr_percentage", new TableInfo.Column("apr_percentage", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("is_archived", new TableInfo.Column("is_archived", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("color_hex", new TableInfo.Column("color_hex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("icon_name", new TableInfo.Column("icon_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("sort_order", new TableInfo.Column("sort_order", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAccounts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAccounts = new HashSet<TableInfo.Index>(2);
        _indicesAccounts.add(new TableInfo.Index("index_accounts_type_is_archived", false, Arrays.asList("type", "is_archived"), Arrays.asList("ASC", "ASC")));
        _indicesAccounts.add(new TableInfo.Index("index_accounts_sort_order", false, Arrays.asList("sort_order"), Arrays.asList("ASC")));
        final TableInfo _infoAccounts = new TableInfo("accounts", _columnsAccounts, _foreignKeysAccounts, _indicesAccounts);
        final TableInfo _existingAccounts = TableInfo.read(db, "accounts");
        if (!_infoAccounts.equals(_existingAccounts)) {
          return new RoomOpenHelper.ValidationResult(false, "accounts(com.artha.database.entity.AccountEntity).\n"
                  + " Expected:\n" + _infoAccounts + "\n"
                  + " Found:\n" + _existingAccounts);
        }
        final HashMap<String, TableInfo.Column> _columnsCategories = new HashMap<String, TableInfo.Column>(9);
        _columnsCategories.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("parent_id", new TableInfo.Column("parent_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("icon_name", new TableInfo.Column("icon_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("color_hex", new TableInfo.Column("color_hex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("sort_order", new TableInfo.Column("sort_order", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("is_archived", new TableInfo.Column("is_archived", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("default_payment_method", new TableInfo.Column("default_payment_method", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCategories = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysCategories.add(new TableInfo.ForeignKey("categories", "SET NULL", "NO ACTION", Arrays.asList("parent_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesCategories = new HashSet<TableInfo.Index>(3);
        _indicesCategories.add(new TableInfo.Index("index_categories_parent_id", false, Arrays.asList("parent_id"), Arrays.asList("ASC")));
        _indicesCategories.add(new TableInfo.Index("index_categories_type_is_archived", false, Arrays.asList("type", "is_archived"), Arrays.asList("ASC", "ASC")));
        _indicesCategories.add(new TableInfo.Index("index_categories_sort_order", false, Arrays.asList("sort_order"), Arrays.asList("ASC")));
        final TableInfo _infoCategories = new TableInfo("categories", _columnsCategories, _foreignKeysCategories, _indicesCategories);
        final TableInfo _existingCategories = TableInfo.read(db, "categories");
        if (!_infoCategories.equals(_existingCategories)) {
          return new RoomOpenHelper.ValidationResult(false, "categories(com.artha.database.entity.CategoryEntity).\n"
                  + " Expected:\n" + _infoCategories + "\n"
                  + " Found:\n" + _existingCategories);
        }
        final HashMap<String, TableInfo.Column> _columnsTransactions = new HashMap<String, TableInfo.Column>(26);
        _columnsTransactions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("account_id", new TableInfo.Column("account_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("to_account_id", new TableInfo.Column("to_account_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("category_id", new TableInfo.Column("category_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("amount_minor", new TableInfo.Column("amount_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("currency", new TableInfo.Column("currency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("exchange_rate_to_base", new TableInfo.Column("exchange_rate_to_base", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("amount_in_base_minor", new TableInfo.Column("amount_in_base_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("payment_method", new TableInfo.Column("payment_method", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("is_excluded_from_budget", new TableInfo.Column("is_excluded_from_budget", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("is_excluded_from_reports", new TableInfo.Column("is_excluded_from_reports", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("is_reconciled", new TableInfo.Column("is_reconciled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("receipt_image_path", new TableInfo.Column("receipt_image_path", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("recurring_rule_id", new TableInfo.Column("recurring_rule_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("parent_transaction_id", new TableInfo.Column("parent_transaction_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("transfer_counterpart_id", new TableInfo.Column("transfer_counterpart_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("latitude", new TableInfo.Column("latitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("longitude", new TableInfo.Column("longitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("location_name", new TableInfo.Column("location_name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("worth_it_status", new TableInfo.Column("worth_it_status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTransactions = new HashSet<TableInfo.ForeignKey>(3);
        _foreignKeysTransactions.add(new TableInfo.ForeignKey("accounts", "RESTRICT", "NO ACTION", Arrays.asList("account_id"), Arrays.asList("id")));
        _foreignKeysTransactions.add(new TableInfo.ForeignKey("accounts", "RESTRICT", "NO ACTION", Arrays.asList("to_account_id"), Arrays.asList("id")));
        _foreignKeysTransactions.add(new TableInfo.ForeignKey("categories", "SET NULL", "NO ACTION", Arrays.asList("category_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTransactions = new HashSet<TableInfo.Index>(5);
        _indicesTransactions.add(new TableInfo.Index("index_transactions_timestamp", false, Arrays.asList("timestamp"), Arrays.asList("ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_account_id_timestamp", false, Arrays.asList("account_id", "timestamp"), Arrays.asList("ASC", "ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_to_account_id_timestamp", false, Arrays.asList("to_account_id", "timestamp"), Arrays.asList("ASC", "ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_category_id_timestamp", false, Arrays.asList("category_id", "timestamp"), Arrays.asList("ASC", "ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_type_timestamp", false, Arrays.asList("type", "timestamp"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoTransactions = new TableInfo("transactions", _columnsTransactions, _foreignKeysTransactions, _indicesTransactions);
        final TableInfo _existingTransactions = TableInfo.read(db, "transactions");
        if (!_infoTransactions.equals(_existingTransactions)) {
          return new RoomOpenHelper.ValidationResult(false, "transactions(com.artha.database.entity.TransactionEntity).\n"
                  + " Expected:\n" + _infoTransactions + "\n"
                  + " Found:\n" + _existingTransactions);
        }
        final HashMap<String, TableInfo.Column> _columnsTransactionSplits = new HashMap<String, TableInfo.Column>(5);
        _columnsTransactionSplits.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactionSplits.put("transaction_id", new TableInfo.Column("transaction_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactionSplits.put("category_id", new TableInfo.Column("category_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactionSplits.put("amount_minor", new TableInfo.Column("amount_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactionSplits.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTransactionSplits = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysTransactionSplits.add(new TableInfo.ForeignKey("transactions", "CASCADE", "NO ACTION", Arrays.asList("transaction_id"), Arrays.asList("id")));
        _foreignKeysTransactionSplits.add(new TableInfo.ForeignKey("categories", "RESTRICT", "NO ACTION", Arrays.asList("category_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTransactionSplits = new HashSet<TableInfo.Index>(2);
        _indicesTransactionSplits.add(new TableInfo.Index("index_transaction_splits_transaction_id", false, Arrays.asList("transaction_id"), Arrays.asList("ASC")));
        _indicesTransactionSplits.add(new TableInfo.Index("index_transaction_splits_category_id", false, Arrays.asList("category_id"), Arrays.asList("ASC")));
        final TableInfo _infoTransactionSplits = new TableInfo("transaction_splits", _columnsTransactionSplits, _foreignKeysTransactionSplits, _indicesTransactionSplits);
        final TableInfo _existingTransactionSplits = TableInfo.read(db, "transaction_splits");
        if (!_infoTransactionSplits.equals(_existingTransactionSplits)) {
          return new RoomOpenHelper.ValidationResult(false, "transaction_splits(com.artha.database.entity.TransactionSplitEntity).\n"
                  + " Expected:\n" + _infoTransactionSplits + "\n"
                  + " Found:\n" + _existingTransactionSplits);
        }
        final HashMap<String, TableInfo.Column> _columnsTags = new HashMap<String, TableInfo.Column>(3);
        _columnsTags.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("color_hex", new TableInfo.Column("color_hex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTags = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTags = new HashSet<TableInfo.Index>(1);
        _indicesTags.add(new TableInfo.Index("index_tags_name", true, Arrays.asList("name"), Arrays.asList("ASC")));
        final TableInfo _infoTags = new TableInfo("tags", _columnsTags, _foreignKeysTags, _indicesTags);
        final TableInfo _existingTags = TableInfo.read(db, "tags");
        if (!_infoTags.equals(_existingTags)) {
          return new RoomOpenHelper.ValidationResult(false, "tags(com.artha.database.entity.TagEntity).\n"
                  + " Expected:\n" + _infoTags + "\n"
                  + " Found:\n" + _existingTags);
        }
        final HashMap<String, TableInfo.Column> _columnsTransactionTags = new HashMap<String, TableInfo.Column>(2);
        _columnsTransactionTags.put("transaction_id", new TableInfo.Column("transaction_id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactionTags.put("tag_id", new TableInfo.Column("tag_id", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTransactionTags = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysTransactionTags.add(new TableInfo.ForeignKey("transactions", "CASCADE", "NO ACTION", Arrays.asList("transaction_id"), Arrays.asList("id")));
        _foreignKeysTransactionTags.add(new TableInfo.ForeignKey("tags", "CASCADE", "NO ACTION", Arrays.asList("tag_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTransactionTags = new HashSet<TableInfo.Index>(2);
        _indicesTransactionTags.add(new TableInfo.Index("index_transaction_tags_transaction_id", false, Arrays.asList("transaction_id"), Arrays.asList("ASC")));
        _indicesTransactionTags.add(new TableInfo.Index("index_transaction_tags_tag_id", false, Arrays.asList("tag_id"), Arrays.asList("ASC")));
        final TableInfo _infoTransactionTags = new TableInfo("transaction_tags", _columnsTransactionTags, _foreignKeysTransactionTags, _indicesTransactionTags);
        final TableInfo _existingTransactionTags = TableInfo.read(db, "transaction_tags");
        if (!_infoTransactionTags.equals(_existingTransactionTags)) {
          return new RoomOpenHelper.ValidationResult(false, "transaction_tags(com.artha.database.entity.TransactionTagCrossRef).\n"
                  + " Expected:\n" + _infoTransactionTags + "\n"
                  + " Found:\n" + _existingTransactionTags);
        }
        final HashMap<String, TableInfo.Column> _columnsBudgets = new HashMap<String, TableInfo.Column>(10);
        _columnsBudgets.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("amount_minor", new TableInfo.Column("amount_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("currency", new TableInfo.Column("currency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("period_type", new TableInfo.Column("period_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("start_date", new TableInfo.Column("start_date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("end_date", new TableInfo.Column("end_date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("rollover_enabled", new TableInfo.Column("rollover_enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("envelope_mode", new TableInfo.Column("envelope_mode", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("alert_threshold_percentage", new TableInfo.Column("alert_threshold_percentage", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBudgets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBudgets = new HashSet<TableInfo.Index>(1);
        _indicesBudgets.add(new TableInfo.Index("index_budgets_period_type_start_date_end_date", false, Arrays.asList("period_type", "start_date", "end_date"), Arrays.asList("ASC", "ASC", "ASC")));
        final TableInfo _infoBudgets = new TableInfo("budgets", _columnsBudgets, _foreignKeysBudgets, _indicesBudgets);
        final TableInfo _existingBudgets = TableInfo.read(db, "budgets");
        if (!_infoBudgets.equals(_existingBudgets)) {
          return new RoomOpenHelper.ValidationResult(false, "budgets(com.artha.database.entity.BudgetEntity).\n"
                  + " Expected:\n" + _infoBudgets + "\n"
                  + " Found:\n" + _existingBudgets);
        }
        final HashMap<String, TableInfo.Column> _columnsBudgetCategories = new HashMap<String, TableInfo.Column>(2);
        _columnsBudgetCategories.put("budget_id", new TableInfo.Column("budget_id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgetCategories.put("category_id", new TableInfo.Column("category_id", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBudgetCategories = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysBudgetCategories.add(new TableInfo.ForeignKey("budgets", "CASCADE", "NO ACTION", Arrays.asList("budget_id"), Arrays.asList("id")));
        _foreignKeysBudgetCategories.add(new TableInfo.ForeignKey("categories", "CASCADE", "NO ACTION", Arrays.asList("category_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesBudgetCategories = new HashSet<TableInfo.Index>(2);
        _indicesBudgetCategories.add(new TableInfo.Index("index_budget_categories_budget_id", false, Arrays.asList("budget_id"), Arrays.asList("ASC")));
        _indicesBudgetCategories.add(new TableInfo.Index("index_budget_categories_category_id", false, Arrays.asList("category_id"), Arrays.asList("ASC")));
        final TableInfo _infoBudgetCategories = new TableInfo("budget_categories", _columnsBudgetCategories, _foreignKeysBudgetCategories, _indicesBudgetCategories);
        final TableInfo _existingBudgetCategories = TableInfo.read(db, "budget_categories");
        if (!_infoBudgetCategories.equals(_existingBudgetCategories)) {
          return new RoomOpenHelper.ValidationResult(false, "budget_categories(com.artha.database.entity.BudgetCategoryCrossRef).\n"
                  + " Expected:\n" + _infoBudgetCategories + "\n"
                  + " Found:\n" + _existingBudgetCategories);
        }
        final HashMap<String, TableInfo.Column> _columnsRecurringRules = new HashMap<String, TableInfo.Column>(14);
        _columnsRecurringRules.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringRules.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringRules.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringRules.put("amount_minor", new TableInfo.Column("amount_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringRules.put("currency", new TableInfo.Column("currency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringRules.put("account_id", new TableInfo.Column("account_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringRules.put("to_account_id", new TableInfo.Column("to_account_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringRules.put("category_id", new TableInfo.Column("category_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringRules.put("payment_method", new TableInfo.Column("payment_method", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringRules.put("frequency", new TableInfo.Column("frequency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringRules.put("interval", new TableInfo.Column("interval", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringRules.put("next_due_date", new TableInfo.Column("next_due_date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringRules.put("auto_apply", new TableInfo.Column("auto_apply", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringRules.put("reminder_days_before", new TableInfo.Column("reminder_days_before", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecurringRules = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRecurringRules = new HashSet<TableInfo.Index>(1);
        _indicesRecurringRules.add(new TableInfo.Index("index_recurring_rules_next_due_date", false, Arrays.asList("next_due_date"), Arrays.asList("ASC")));
        final TableInfo _infoRecurringRules = new TableInfo("recurring_rules", _columnsRecurringRules, _foreignKeysRecurringRules, _indicesRecurringRules);
        final TableInfo _existingRecurringRules = TableInfo.read(db, "recurring_rules");
        if (!_infoRecurringRules.equals(_existingRecurringRules)) {
          return new RoomOpenHelper.ValidationResult(false, "recurring_rules(com.artha.database.entity.RecurringRuleEntity).\n"
                  + " Expected:\n" + _infoRecurringRules + "\n"
                  + " Found:\n" + _existingRecurringRules);
        }
        final HashMap<String, TableInfo.Column> _columnsBillsSubscriptions = new HashMap<String, TableInfo.Column>(14);
        _columnsBillsSubscriptions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillsSubscriptions.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillsSubscriptions.put("amount_minor", new TableInfo.Column("amount_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillsSubscriptions.put("currency", new TableInfo.Column("currency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillsSubscriptions.put("category_id", new TableInfo.Column("category_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillsSubscriptions.put("account_id", new TableInfo.Column("account_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillsSubscriptions.put("frequency", new TableInfo.Column("frequency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillsSubscriptions.put("due_day", new TableInfo.Column("due_day", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillsSubscriptions.put("trial_end_date", new TableInfo.Column("trial_end_date", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillsSubscriptions.put("cancellation_url", new TableInfo.Column("cancellation_url", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillsSubscriptions.put("is_auto_paid", new TableInfo.Column("is_auto_paid", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillsSubscriptions.put("is_active", new TableInfo.Column("is_active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillsSubscriptions.put("last_paid_date", new TableInfo.Column("last_paid_date", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillsSubscriptions.put("next_due_date", new TableInfo.Column("next_due_date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBillsSubscriptions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBillsSubscriptions = new HashSet<TableInfo.Index>(2);
        _indicesBillsSubscriptions.add(new TableInfo.Index("index_bills_subscriptions_next_due_date", false, Arrays.asList("next_due_date"), Arrays.asList("ASC")));
        _indicesBillsSubscriptions.add(new TableInfo.Index("index_bills_subscriptions_is_active", false, Arrays.asList("is_active"), Arrays.asList("ASC")));
        final TableInfo _infoBillsSubscriptions = new TableInfo("bills_subscriptions", _columnsBillsSubscriptions, _foreignKeysBillsSubscriptions, _indicesBillsSubscriptions);
        final TableInfo _existingBillsSubscriptions = TableInfo.read(db, "bills_subscriptions");
        if (!_infoBillsSubscriptions.equals(_existingBillsSubscriptions)) {
          return new RoomOpenHelper.ValidationResult(false, "bills_subscriptions(com.artha.database.entity.BillSubscriptionEntity).\n"
                  + " Expected:\n" + _infoBillsSubscriptions + "\n"
                  + " Found:\n" + _existingBillsSubscriptions);
        }
        final HashMap<String, TableInfo.Column> _columnsDebtsLoans = new HashMap<String, TableInfo.Column>(12);
        _columnsDebtsLoans.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtsLoans.put("person_or_institution", new TableInfo.Column("person_or_institution", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtsLoans.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtsLoans.put("principal_minor", new TableInfo.Column("principal_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtsLoans.put("remaining_minor", new TableInfo.Column("remaining_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtsLoans.put("interest_rate_apr", new TableInfo.Column("interest_rate_apr", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtsLoans.put("tenure_months", new TableInfo.Column("tenure_months", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtsLoans.put("emi_amount_minor", new TableInfo.Column("emi_amount_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtsLoans.put("emi_due_day", new TableInfo.Column("emi_due_day", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtsLoans.put("start_date", new TableInfo.Column("start_date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtsLoans.put("end_date", new TableInfo.Column("end_date", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtsLoans.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDebtsLoans = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDebtsLoans = new HashSet<TableInfo.Index>(1);
        _indicesDebtsLoans.add(new TableInfo.Index("index_debts_loans_type", false, Arrays.asList("type"), Arrays.asList("ASC")));
        final TableInfo _infoDebtsLoans = new TableInfo("debts_loans", _columnsDebtsLoans, _foreignKeysDebtsLoans, _indicesDebtsLoans);
        final TableInfo _existingDebtsLoans = TableInfo.read(db, "debts_loans");
        if (!_infoDebtsLoans.equals(_existingDebtsLoans)) {
          return new RoomOpenHelper.ValidationResult(false, "debts_loans(com.artha.database.entity.DebtLoanEntity).\n"
                  + " Expected:\n" + _infoDebtsLoans + "\n"
                  + " Found:\n" + _existingDebtsLoans);
        }
        final HashMap<String, TableInfo.Column> _columnsDebtPayments = new HashMap<String, TableInfo.Column>(7);
        _columnsDebtPayments.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("debt_id", new TableInfo.Column("debt_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("transaction_id", new TableInfo.Column("transaction_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("amount_minor", new TableInfo.Column("amount_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("principal_component_minor", new TableInfo.Column("principal_component_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("interest_component_minor", new TableInfo.Column("interest_component_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("payment_date", new TableInfo.Column("payment_date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDebtPayments = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysDebtPayments.add(new TableInfo.ForeignKey("debts_loans", "CASCADE", "NO ACTION", Arrays.asList("debt_id"), Arrays.asList("id")));
        _foreignKeysDebtPayments.add(new TableInfo.ForeignKey("transactions", "SET NULL", "NO ACTION", Arrays.asList("transaction_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesDebtPayments = new HashSet<TableInfo.Index>(2);
        _indicesDebtPayments.add(new TableInfo.Index("index_debt_payments_debt_id", false, Arrays.asList("debt_id"), Arrays.asList("ASC")));
        _indicesDebtPayments.add(new TableInfo.Index("index_debt_payments_transaction_id", false, Arrays.asList("transaction_id"), Arrays.asList("ASC")));
        final TableInfo _infoDebtPayments = new TableInfo("debt_payments", _columnsDebtPayments, _foreignKeysDebtPayments, _indicesDebtPayments);
        final TableInfo _existingDebtPayments = TableInfo.read(db, "debt_payments");
        if (!_infoDebtPayments.equals(_existingDebtPayments)) {
          return new RoomOpenHelper.ValidationResult(false, "debt_payments(com.artha.database.entity.DebtPaymentEntity).\n"
                  + " Expected:\n" + _infoDebtPayments + "\n"
                  + " Found:\n" + _existingDebtPayments);
        }
        final HashMap<String, TableInfo.Column> _columnsGoals = new HashMap<String, TableInfo.Column>(10);
        _columnsGoals.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("target_amount_minor", new TableInfo.Column("target_amount_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("saved_amount_minor", new TableInfo.Column("saved_amount_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("currency", new TableInfo.Column("currency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("target_date", new TableInfo.Column("target_date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("account_id", new TableInfo.Column("account_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("icon_name", new TableInfo.Column("icon_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("color_hex", new TableInfo.Column("color_hex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("is_reached", new TableInfo.Column("is_reached", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGoals = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysGoals.add(new TableInfo.ForeignKey("accounts", "SET NULL", "NO ACTION", Arrays.asList("account_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesGoals = new HashSet<TableInfo.Index>(2);
        _indicesGoals.add(new TableInfo.Index("index_goals_account_id", false, Arrays.asList("account_id"), Arrays.asList("ASC")));
        _indicesGoals.add(new TableInfo.Index("index_goals_is_reached", false, Arrays.asList("is_reached"), Arrays.asList("ASC")));
        final TableInfo _infoGoals = new TableInfo("goals", _columnsGoals, _foreignKeysGoals, _indicesGoals);
        final TableInfo _existingGoals = TableInfo.read(db, "goals");
        if (!_infoGoals.equals(_existingGoals)) {
          return new RoomOpenHelper.ValidationResult(false, "goals(com.artha.database.entity.GoalEntity).\n"
                  + " Expected:\n" + _infoGoals + "\n"
                  + " Found:\n" + _existingGoals);
        }
        final HashMap<String, TableInfo.Column> _columnsRules = new HashMap<String, TableInfo.Column>(9);
        _columnsRules.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRules.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRules.put("condition_field", new TableInfo.Column("condition_field", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRules.put("condition_operator", new TableInfo.Column("condition_operator", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRules.put("condition_value", new TableInfo.Column("condition_value", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRules.put("action_field", new TableInfo.Column("action_field", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRules.put("action_value", new TableInfo.Column("action_value", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRules.put("priority", new TableInfo.Column("priority", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRules.put("is_active", new TableInfo.Column("is_active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRules = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRules = new HashSet<TableInfo.Index>(1);
        _indicesRules.add(new TableInfo.Index("index_rules_is_active_priority", false, Arrays.asList("is_active", "priority"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoRules = new TableInfo("rules", _columnsRules, _foreignKeysRules, _indicesRules);
        final TableInfo _existingRules = TableInfo.read(db, "rules");
        if (!_infoRules.equals(_existingRules)) {
          return new RoomOpenHelper.ValidationResult(false, "rules(com.artha.database.entity.RuleEntity).\n"
                  + " Expected:\n" + _infoRules + "\n"
                  + " Found:\n" + _existingRules);
        }
        final HashMap<String, TableInfo.Column> _columnsExchangeRates = new HashMap<String, TableInfo.Column>(4);
        _columnsExchangeRates.put("base_currency", new TableInfo.Column("base_currency", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExchangeRates.put("target_currency", new TableInfo.Column("target_currency", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExchangeRates.put("rate", new TableInfo.Column("rate", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExchangeRates.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExchangeRates = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExchangeRates = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExchangeRates = new TableInfo("exchange_rates", _columnsExchangeRates, _foreignKeysExchangeRates, _indicesExchangeRates);
        final TableInfo _existingExchangeRates = TableInfo.read(db, "exchange_rates");
        if (!_infoExchangeRates.equals(_existingExchangeRates)) {
          return new RoomOpenHelper.ValidationResult(false, "exchange_rates(com.artha.database.entity.ExchangeRateEntity).\n"
                  + " Expected:\n" + _infoExchangeRates + "\n"
                  + " Found:\n" + _existingExchangeRates);
        }
        final HashMap<String, TableInfo.Column> _columnsMonthlyAggregates = new HashMap<String, TableInfo.Column>(7);
        _columnsMonthlyAggregates.put("year_month", new TableInfo.Column("year_month", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonthlyAggregates.put("currency", new TableInfo.Column("currency", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonthlyAggregates.put("total_income_minor", new TableInfo.Column("total_income_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonthlyAggregates.put("total_expense_minor", new TableInfo.Column("total_expense_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonthlyAggregates.put("net_savings_minor", new TableInfo.Column("net_savings_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonthlyAggregates.put("transaction_count", new TableInfo.Column("transaction_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonthlyAggregates.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMonthlyAggregates = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMonthlyAggregates = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMonthlyAggregates = new TableInfo("monthly_aggregates", _columnsMonthlyAggregates, _foreignKeysMonthlyAggregates, _indicesMonthlyAggregates);
        final TableInfo _existingMonthlyAggregates = TableInfo.read(db, "monthly_aggregates");
        if (!_infoMonthlyAggregates.equals(_existingMonthlyAggregates)) {
          return new RoomOpenHelper.ValidationResult(false, "monthly_aggregates(com.artha.database.entity.MonthlyAggregateEntity).\n"
                  + " Expected:\n" + _infoMonthlyAggregates + "\n"
                  + " Found:\n" + _existingMonthlyAggregates);
        }
        final HashMap<String, TableInfo.Column> _columnsCategoryMonthlyAggregates = new HashMap<String, TableInfo.Column>(5);
        _columnsCategoryMonthlyAggregates.put("year_month", new TableInfo.Column("year_month", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategoryMonthlyAggregates.put("category_id", new TableInfo.Column("category_id", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategoryMonthlyAggregates.put("currency", new TableInfo.Column("currency", "TEXT", true, 3, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategoryMonthlyAggregates.put("total_amount_minor", new TableInfo.Column("total_amount_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategoryMonthlyAggregates.put("transaction_count", new TableInfo.Column("transaction_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCategoryMonthlyAggregates = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysCategoryMonthlyAggregates.add(new TableInfo.ForeignKey("categories", "CASCADE", "NO ACTION", Arrays.asList("category_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesCategoryMonthlyAggregates = new HashSet<TableInfo.Index>(1);
        _indicesCategoryMonthlyAggregates.add(new TableInfo.Index("index_category_monthly_aggregates_category_id", false, Arrays.asList("category_id"), Arrays.asList("ASC")));
        final TableInfo _infoCategoryMonthlyAggregates = new TableInfo("category_monthly_aggregates", _columnsCategoryMonthlyAggregates, _foreignKeysCategoryMonthlyAggregates, _indicesCategoryMonthlyAggregates);
        final TableInfo _existingCategoryMonthlyAggregates = TableInfo.read(db, "category_monthly_aggregates");
        if (!_infoCategoryMonthlyAggregates.equals(_existingCategoryMonthlyAggregates)) {
          return new RoomOpenHelper.ValidationResult(false, "category_monthly_aggregates(com.artha.database.entity.CategoryMonthlyAggregateEntity).\n"
                  + " Expected:\n" + _infoCategoryMonthlyAggregates + "\n"
                  + " Found:\n" + _existingCategoryMonthlyAggregates);
        }
        final HashMap<String, TableInfo.Column> _columnsAccountMonthlyAggregates = new HashMap<String, TableInfo.Column>(6);
        _columnsAccountMonthlyAggregates.put("year_month", new TableInfo.Column("year_month", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccountMonthlyAggregates.put("account_id", new TableInfo.Column("account_id", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccountMonthlyAggregates.put("starting_balance_minor", new TableInfo.Column("starting_balance_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccountMonthlyAggregates.put("ending_balance_minor", new TableInfo.Column("ending_balance_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccountMonthlyAggregates.put("total_inflow_minor", new TableInfo.Column("total_inflow_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccountMonthlyAggregates.put("total_outflow_minor", new TableInfo.Column("total_outflow_minor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAccountMonthlyAggregates = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysAccountMonthlyAggregates.add(new TableInfo.ForeignKey("accounts", "CASCADE", "NO ACTION", Arrays.asList("account_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesAccountMonthlyAggregates = new HashSet<TableInfo.Index>(1);
        _indicesAccountMonthlyAggregates.add(new TableInfo.Index("index_account_monthly_aggregates_account_id", false, Arrays.asList("account_id"), Arrays.asList("ASC")));
        final TableInfo _infoAccountMonthlyAggregates = new TableInfo("account_monthly_aggregates", _columnsAccountMonthlyAggregates, _foreignKeysAccountMonthlyAggregates, _indicesAccountMonthlyAggregates);
        final TableInfo _existingAccountMonthlyAggregates = TableInfo.read(db, "account_monthly_aggregates");
        if (!_infoAccountMonthlyAggregates.equals(_existingAccountMonthlyAggregates)) {
          return new RoomOpenHelper.ValidationResult(false, "account_monthly_aggregates(com.artha.database.entity.AccountMonthlyAggregateEntity).\n"
                  + " Expected:\n" + _infoAccountMonthlyAggregates + "\n"
                  + " Found:\n" + _existingAccountMonthlyAggregates);
        }
        final HashSet<String> _columnsTransactionsFts = new HashSet<String>(4);
        _columnsTransactionsFts.add("transaction_id");
        _columnsTransactionsFts.add("note");
        _columnsTransactionsFts.add("searchable_text");
        final FtsTableInfo _infoTransactionsFts = new FtsTableInfo("transactions_fts", _columnsTransactionsFts, "CREATE VIRTUAL TABLE IF NOT EXISTS `transactions_fts` USING FTS4(`transaction_id` TEXT NOT NULL, `note` TEXT NOT NULL, `searchable_text` TEXT NOT NULL)");
        final FtsTableInfo _existingTransactionsFts = FtsTableInfo.read(db, "transactions_fts");
        if (!_infoTransactionsFts.equals(_existingTransactionsFts)) {
          return new RoomOpenHelper.ValidationResult(false, "transactions_fts(com.artha.database.entity.TransactionFtsEntity).\n"
                  + " Expected:\n" + _infoTransactionsFts + "\n"
                  + " Found:\n" + _existingTransactionsFts);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4b5e85484b88d238fde7e714cd7b4f16", "49f82cf0e99a95fffedd0db71af28ae6");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(1);
    _shadowTablesMap.put("transactions_fts", "transactions_fts_content");
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "accounts","categories","transactions","transaction_splits","tags","transaction_tags","budgets","budget_categories","recurring_rules","bills_subscriptions","debts_loans","debt_payments","goals","rules","exchange_rates","monthly_aggregates","category_monthly_aggregates","account_monthly_aggregates","transactions_fts");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `accounts`");
      _db.execSQL("DELETE FROM `categories`");
      _db.execSQL("DELETE FROM `transactions`");
      _db.execSQL("DELETE FROM `transaction_splits`");
      _db.execSQL("DELETE FROM `tags`");
      _db.execSQL("DELETE FROM `transaction_tags`");
      _db.execSQL("DELETE FROM `budgets`");
      _db.execSQL("DELETE FROM `budget_categories`");
      _db.execSQL("DELETE FROM `recurring_rules`");
      _db.execSQL("DELETE FROM `bills_subscriptions`");
      _db.execSQL("DELETE FROM `debts_loans`");
      _db.execSQL("DELETE FROM `debt_payments`");
      _db.execSQL("DELETE FROM `goals`");
      _db.execSQL("DELETE FROM `rules`");
      _db.execSQL("DELETE FROM `exchange_rates`");
      _db.execSQL("DELETE FROM `monthly_aggregates`");
      _db.execSQL("DELETE FROM `category_monthly_aggregates`");
      _db.execSQL("DELETE FROM `account_monthly_aggregates`");
      _db.execSQL("DELETE FROM `transactions_fts`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(AccountDao.class, AccountDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CategoryDao.class, CategoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TransactionDao.class, TransactionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AggregateDao.class, AggregateDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BudgetDao.class, BudgetDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RecurringRuleDao.class, RecurringRuleDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BillSubscriptionDao.class, BillSubscriptionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DebtLoanDao.class, DebtLoanDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GoalDao.class, GoalDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RuleDao.class, RuleDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public AccountDao accountDao() {
    if (_accountDao != null) {
      return _accountDao;
    } else {
      synchronized(this) {
        if(_accountDao == null) {
          _accountDao = new AccountDao_Impl(this);
        }
        return _accountDao;
      }
    }
  }

  @Override
  public CategoryDao categoryDao() {
    if (_categoryDao != null) {
      return _categoryDao;
    } else {
      synchronized(this) {
        if(_categoryDao == null) {
          _categoryDao = new CategoryDao_Impl(this);
        }
        return _categoryDao;
      }
    }
  }

  @Override
  public TransactionDao transactionDao() {
    if (_transactionDao != null) {
      return _transactionDao;
    } else {
      synchronized(this) {
        if(_transactionDao == null) {
          _transactionDao = new TransactionDao_Impl(this);
        }
        return _transactionDao;
      }
    }
  }

  @Override
  public AggregateDao aggregateDao() {
    if (_aggregateDao != null) {
      return _aggregateDao;
    } else {
      synchronized(this) {
        if(_aggregateDao == null) {
          _aggregateDao = new AggregateDao_Impl(this);
        }
        return _aggregateDao;
      }
    }
  }

  @Override
  public BudgetDao budgetDao() {
    if (_budgetDao != null) {
      return _budgetDao;
    } else {
      synchronized(this) {
        if(_budgetDao == null) {
          _budgetDao = new BudgetDao_Impl(this);
        }
        return _budgetDao;
      }
    }
  }

  @Override
  public RecurringRuleDao recurringRuleDao() {
    if (_recurringRuleDao != null) {
      return _recurringRuleDao;
    } else {
      synchronized(this) {
        if(_recurringRuleDao == null) {
          _recurringRuleDao = new RecurringRuleDao_Impl(this);
        }
        return _recurringRuleDao;
      }
    }
  }

  @Override
  public BillSubscriptionDao billSubscriptionDao() {
    if (_billSubscriptionDao != null) {
      return _billSubscriptionDao;
    } else {
      synchronized(this) {
        if(_billSubscriptionDao == null) {
          _billSubscriptionDao = new BillSubscriptionDao_Impl(this);
        }
        return _billSubscriptionDao;
      }
    }
  }

  @Override
  public DebtLoanDao debtLoanDao() {
    if (_debtLoanDao != null) {
      return _debtLoanDao;
    } else {
      synchronized(this) {
        if(_debtLoanDao == null) {
          _debtLoanDao = new DebtLoanDao_Impl(this);
        }
        return _debtLoanDao;
      }
    }
  }

  @Override
  public GoalDao goalDao() {
    if (_goalDao != null) {
      return _goalDao;
    } else {
      synchronized(this) {
        if(_goalDao == null) {
          _goalDao = new GoalDao_Impl(this);
        }
        return _goalDao;
      }
    }
  }

  @Override
  public RuleDao ruleDao() {
    if (_ruleDao != null) {
      return _ruleDao;
    } else {
      synchronized(this) {
        if(_ruleDao == null) {
          _ruleDao = new RuleDao_Impl(this);
        }
        return _ruleDao;
      }
    }
  }
}
