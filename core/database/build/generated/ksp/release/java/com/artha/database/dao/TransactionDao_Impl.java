package com.artha.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.artha.database.converter.RoomTypeConverters;
import com.artha.database.entity.AccountEntity;
import com.artha.database.entity.AccountMonthlyAggregateEntity;
import com.artha.database.entity.CategoryMonthlyAggregateEntity;
import com.artha.database.entity.MonthlyAggregateEntity;
import com.artha.database.entity.TransactionEntity;
import com.artha.database.entity.TransactionFtsEntity;
import com.artha.model.AccountType;
import com.artha.model.PaymentMethod;
import com.artha.model.TransactionStatus;
import com.artha.model.TransactionType;
import com.artha.model.WorthItStatus;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TransactionDao_Impl extends TransactionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TransactionEntity> __insertionAdapterOfTransactionEntity;

  private final RoomTypeConverters __roomTypeConverters = new RoomTypeConverters();

  private final EntityInsertionAdapter<TransactionFtsEntity> __insertionAdapterOfTransactionFtsEntity;

  private final EntityInsertionAdapter<MonthlyAggregateEntity> __insertionAdapterOfMonthlyAggregateEntity;

  private final EntityInsertionAdapter<CategoryMonthlyAggregateEntity> __insertionAdapterOfCategoryMonthlyAggregateEntity;

  private final EntityInsertionAdapter<AccountMonthlyAggregateEntity> __insertionAdapterOfAccountMonthlyAggregateEntity;

  private final EntityDeletionOrUpdateAdapter<TransactionEntity> __deletionAdapterOfTransactionEntity;

  private final EntityDeletionOrUpdateAdapter<TransactionEntity> __updateAdapterOfTransactionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteFts;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAccountBalance;

  public TransactionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTransactionEntity = new EntityInsertionAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `transactions` (`id`,`account_id`,`to_account_id`,`category_id`,`amount_minor`,`currency`,`exchange_rate_to_base`,`amount_in_base_minor`,`type`,`payment_method`,`timestamp`,`note`,`is_excluded_from_budget`,`is_excluded_from_reports`,`is_reconciled`,`status`,`receipt_image_path`,`recurring_rule_id`,`parent_transaction_id`,`transfer_counterpart_id`,`latitude`,`longitude`,`location_name`,`worth_it_status`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getAccountId());
        if (entity.getToAccountId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getToAccountId());
        }
        if (entity.getCategoryId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCategoryId());
        }
        statement.bindLong(5, entity.getAmountMinor());
        statement.bindString(6, entity.getCurrency());
        statement.bindDouble(7, entity.getExchangeRateToBase());
        statement.bindLong(8, entity.getAmountInBaseMinor());
        final String _tmp = __roomTypeConverters.fromTransactionType(entity.getType());
        if (_tmp == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, _tmp);
        }
        final String _tmp_1 = __roomTypeConverters.fromPaymentMethod(entity.getPaymentMethod());
        if (_tmp_1 == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, _tmp_1);
        }
        statement.bindLong(11, entity.getTimestamp());
        statement.bindString(12, entity.getNote());
        final int _tmp_2 = entity.isExcludedFromBudget() ? 1 : 0;
        statement.bindLong(13, _tmp_2);
        final int _tmp_3 = entity.isExcludedFromReports() ? 1 : 0;
        statement.bindLong(14, _tmp_3);
        final int _tmp_4 = entity.isReconciled() ? 1 : 0;
        statement.bindLong(15, _tmp_4);
        final String _tmp_5 = __roomTypeConverters.fromTransactionStatus(entity.getStatus());
        if (_tmp_5 == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, _tmp_5);
        }
        if (entity.getReceiptImagePath() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getReceiptImagePath());
        }
        if (entity.getRecurringRuleId() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getRecurringRuleId());
        }
        if (entity.getParentTransactionId() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getParentTransactionId());
        }
        if (entity.getTransferCounterpartId() == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.getTransferCounterpartId());
        }
        if (entity.getLatitude() == null) {
          statement.bindNull(21);
        } else {
          statement.bindDouble(21, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(22);
        } else {
          statement.bindDouble(22, entity.getLongitude());
        }
        if (entity.getLocationName() == null) {
          statement.bindNull(23);
        } else {
          statement.bindString(23, entity.getLocationName());
        }
        final String _tmp_6 = __roomTypeConverters.fromWorthItStatus(entity.getWorthItStatus());
        if (_tmp_6 == null) {
          statement.bindNull(24);
        } else {
          statement.bindString(24, _tmp_6);
        }
        statement.bindLong(25, entity.getCreatedAt());
        statement.bindLong(26, entity.getUpdatedAt());
      }
    };
    this.__insertionAdapterOfTransactionFtsEntity = new EntityInsertionAdapter<TransactionFtsEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `transactions_fts` (`rowid`,`transaction_id`,`note`,`searchable_text`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionFtsEntity entity) {
        statement.bindLong(1, entity.getRowid());
        statement.bindString(2, entity.getTransactionId());
        statement.bindString(3, entity.getNote());
        statement.bindString(4, entity.getSearchableText());
      }
    };
    this.__insertionAdapterOfMonthlyAggregateEntity = new EntityInsertionAdapter<MonthlyAggregateEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `monthly_aggregates` (`year_month`,`currency`,`total_income_minor`,`total_expense_minor`,`net_savings_minor`,`transaction_count`,`updated_at`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MonthlyAggregateEntity entity) {
        statement.bindString(1, entity.getYearMonth());
        statement.bindString(2, entity.getCurrency());
        statement.bindLong(3, entity.getTotalIncomeMinor());
        statement.bindLong(4, entity.getTotalExpenseMinor());
        statement.bindLong(5, entity.getNetSavingsMinor());
        statement.bindLong(6, entity.getTransactionCount());
        statement.bindLong(7, entity.getUpdatedAt());
      }
    };
    this.__insertionAdapterOfCategoryMonthlyAggregateEntity = new EntityInsertionAdapter<CategoryMonthlyAggregateEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `category_monthly_aggregates` (`year_month`,`category_id`,`currency`,`total_amount_minor`,`transaction_count`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CategoryMonthlyAggregateEntity entity) {
        statement.bindString(1, entity.getYearMonth());
        statement.bindString(2, entity.getCategoryId());
        statement.bindString(3, entity.getCurrency());
        statement.bindLong(4, entity.getTotalAmountMinor());
        statement.bindLong(5, entity.getTransactionCount());
      }
    };
    this.__insertionAdapterOfAccountMonthlyAggregateEntity = new EntityInsertionAdapter<AccountMonthlyAggregateEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `account_monthly_aggregates` (`year_month`,`account_id`,`starting_balance_minor`,`ending_balance_minor`,`total_inflow_minor`,`total_outflow_minor`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AccountMonthlyAggregateEntity entity) {
        statement.bindString(1, entity.getYearMonth());
        statement.bindString(2, entity.getAccountId());
        statement.bindLong(3, entity.getStartingBalanceMinor());
        statement.bindLong(4, entity.getEndingBalanceMinor());
        statement.bindLong(5, entity.getTotalInflowMinor());
        statement.bindLong(6, entity.getTotalOutflowMinor());
      }
    };
    this.__deletionAdapterOfTransactionEntity = new EntityDeletionOrUpdateAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `transactions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfTransactionEntity = new EntityDeletionOrUpdateAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `transactions` SET `id` = ?,`account_id` = ?,`to_account_id` = ?,`category_id` = ?,`amount_minor` = ?,`currency` = ?,`exchange_rate_to_base` = ?,`amount_in_base_minor` = ?,`type` = ?,`payment_method` = ?,`timestamp` = ?,`note` = ?,`is_excluded_from_budget` = ?,`is_excluded_from_reports` = ?,`is_reconciled` = ?,`status` = ?,`receipt_image_path` = ?,`recurring_rule_id` = ?,`parent_transaction_id` = ?,`transfer_counterpart_id` = ?,`latitude` = ?,`longitude` = ?,`location_name` = ?,`worth_it_status` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getAccountId());
        if (entity.getToAccountId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getToAccountId());
        }
        if (entity.getCategoryId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCategoryId());
        }
        statement.bindLong(5, entity.getAmountMinor());
        statement.bindString(6, entity.getCurrency());
        statement.bindDouble(7, entity.getExchangeRateToBase());
        statement.bindLong(8, entity.getAmountInBaseMinor());
        final String _tmp = __roomTypeConverters.fromTransactionType(entity.getType());
        if (_tmp == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, _tmp);
        }
        final String _tmp_1 = __roomTypeConverters.fromPaymentMethod(entity.getPaymentMethod());
        if (_tmp_1 == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, _tmp_1);
        }
        statement.bindLong(11, entity.getTimestamp());
        statement.bindString(12, entity.getNote());
        final int _tmp_2 = entity.isExcludedFromBudget() ? 1 : 0;
        statement.bindLong(13, _tmp_2);
        final int _tmp_3 = entity.isExcludedFromReports() ? 1 : 0;
        statement.bindLong(14, _tmp_3);
        final int _tmp_4 = entity.isReconciled() ? 1 : 0;
        statement.bindLong(15, _tmp_4);
        final String _tmp_5 = __roomTypeConverters.fromTransactionStatus(entity.getStatus());
        if (_tmp_5 == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, _tmp_5);
        }
        if (entity.getReceiptImagePath() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getReceiptImagePath());
        }
        if (entity.getRecurringRuleId() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getRecurringRuleId());
        }
        if (entity.getParentTransactionId() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getParentTransactionId());
        }
        if (entity.getTransferCounterpartId() == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.getTransferCounterpartId());
        }
        if (entity.getLatitude() == null) {
          statement.bindNull(21);
        } else {
          statement.bindDouble(21, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(22);
        } else {
          statement.bindDouble(22, entity.getLongitude());
        }
        if (entity.getLocationName() == null) {
          statement.bindNull(23);
        } else {
          statement.bindString(23, entity.getLocationName());
        }
        final String _tmp_6 = __roomTypeConverters.fromWorthItStatus(entity.getWorthItStatus());
        if (_tmp_6 == null) {
          statement.bindNull(24);
        } else {
          statement.bindString(24, _tmp_6);
        }
        statement.bindLong(25, entity.getCreatedAt());
        statement.bindLong(26, entity.getUpdatedAt());
        statement.bindString(27, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteFts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM transactions_fts WHERE transaction_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateAccountBalance = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE accounts SET current_balance_minor = current_balance_minor + ?, updated_at = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertTransactionRaw(final TransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTransactionEntity.insert(transaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertFts(final TransactionFtsEntity fts,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTransactionFtsEntity.insert(fts);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertMonthlyAggregate(final MonthlyAggregateEntity aggregate,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMonthlyAggregateEntity.insert(aggregate);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertCategoryMonthlyAggregate(final CategoryMonthlyAggregateEntity aggregate,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCategoryMonthlyAggregateEntity.insert(aggregate);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertAccountMonthlyAggregate(final AccountMonthlyAggregateEntity aggregate,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAccountMonthlyAggregateEntity.insert(aggregate);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTransactionRaw(final TransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTransactionEntity.handle(transaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTransactionRaw(final TransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTransactionEntity.handle(transaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertTransactionWithAggregates(final TransactionEntity tx,
      final String searchableText, final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> TransactionDao_Impl.super.insertTransactionWithAggregates(tx, searchableText, __cont), $completion);
  }

  @Override
  public Object deleteFts(final String transactionId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteFts.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, transactionId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteFts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAccountBalance(final String id, final long deltaMinor, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAccountBalance.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, deltaMinor);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 3;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateAccountBalance.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getTransactionById(final String id,
      final Continuation<? super TransactionEntity> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TransactionEntity>() {
      @Override
      @Nullable
      public TransactionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfToAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "to_account_id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfExchangeRateToBase = CursorUtil.getColumnIndexOrThrow(_cursor, "exchange_rate_to_base");
          final int _cursorIndexOfAmountInBaseMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_in_base_minor");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "payment_method");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfIsExcludedFromBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "is_excluded_from_budget");
          final int _cursorIndexOfIsExcludedFromReports = CursorUtil.getColumnIndexOrThrow(_cursor, "is_excluded_from_reports");
          final int _cursorIndexOfIsReconciled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_reconciled");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReceiptImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "receipt_image_path");
          final int _cursorIndexOfRecurringRuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "recurring_rule_id");
          final int _cursorIndexOfParentTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "parent_transaction_id");
          final int _cursorIndexOfTransferCounterpartId = CursorUtil.getColumnIndexOrThrow(_cursor, "transfer_counterpart_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfLocationName = CursorUtil.getColumnIndexOrThrow(_cursor, "location_name");
          final int _cursorIndexOfWorthItStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "worth_it_status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final TransactionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpAccountId;
            _tmpAccountId = _cursor.getString(_cursorIndexOfAccountId);
            final String _tmpToAccountId;
            if (_cursor.isNull(_cursorIndexOfToAccountId)) {
              _tmpToAccountId = null;
            } else {
              _tmpToAccountId = _cursor.getString(_cursorIndexOfToAccountId);
            }
            final String _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId);
            }
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final double _tmpExchangeRateToBase;
            _tmpExchangeRateToBase = _cursor.getDouble(_cursorIndexOfExchangeRateToBase);
            final long _tmpAmountInBaseMinor;
            _tmpAmountInBaseMinor = _cursor.getLong(_cursorIndexOfAmountInBaseMinor);
            final TransactionType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            final TransactionType _tmp_1 = __roomTypeConverters.toTransactionType(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.TransactionType', but it was NULL.");
            } else {
              _tmpType = _tmp_1;
            }
            final PaymentMethod _tmpPaymentMethod;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfPaymentMethod)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfPaymentMethod);
            }
            final PaymentMethod _tmp_3 = __roomTypeConverters.toPaymentMethod(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.PaymentMethod', but it was NULL.");
            } else {
              _tmpPaymentMethod = _tmp_3;
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final boolean _tmpIsExcludedFromBudget;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsExcludedFromBudget);
            _tmpIsExcludedFromBudget = _tmp_4 != 0;
            final boolean _tmpIsExcludedFromReports;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfIsExcludedFromReports);
            _tmpIsExcludedFromReports = _tmp_5 != 0;
            final boolean _tmpIsReconciled;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfIsReconciled);
            _tmpIsReconciled = _tmp_6 != 0;
            final TransactionStatus _tmpStatus;
            final String _tmp_7;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getString(_cursorIndexOfStatus);
            }
            final TransactionStatus _tmp_8 = __roomTypeConverters.toTransactionStatus(_tmp_7);
            if (_tmp_8 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.TransactionStatus', but it was NULL.");
            } else {
              _tmpStatus = _tmp_8;
            }
            final String _tmpReceiptImagePath;
            if (_cursor.isNull(_cursorIndexOfReceiptImagePath)) {
              _tmpReceiptImagePath = null;
            } else {
              _tmpReceiptImagePath = _cursor.getString(_cursorIndexOfReceiptImagePath);
            }
            final String _tmpRecurringRuleId;
            if (_cursor.isNull(_cursorIndexOfRecurringRuleId)) {
              _tmpRecurringRuleId = null;
            } else {
              _tmpRecurringRuleId = _cursor.getString(_cursorIndexOfRecurringRuleId);
            }
            final String _tmpParentTransactionId;
            if (_cursor.isNull(_cursorIndexOfParentTransactionId)) {
              _tmpParentTransactionId = null;
            } else {
              _tmpParentTransactionId = _cursor.getString(_cursorIndexOfParentTransactionId);
            }
            final String _tmpTransferCounterpartId;
            if (_cursor.isNull(_cursorIndexOfTransferCounterpartId)) {
              _tmpTransferCounterpartId = null;
            } else {
              _tmpTransferCounterpartId = _cursor.getString(_cursorIndexOfTransferCounterpartId);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpLocationName;
            if (_cursor.isNull(_cursorIndexOfLocationName)) {
              _tmpLocationName = null;
            } else {
              _tmpLocationName = _cursor.getString(_cursorIndexOfLocationName);
            }
            final WorthItStatus _tmpWorthItStatus;
            final String _tmp_9;
            if (_cursor.isNull(_cursorIndexOfWorthItStatus)) {
              _tmp_9 = null;
            } else {
              _tmp_9 = _cursor.getString(_cursorIndexOfWorthItStatus);
            }
            final WorthItStatus _tmp_10 = __roomTypeConverters.toWorthItStatus(_tmp_9);
            if (_tmp_10 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.WorthItStatus', but it was NULL.");
            } else {
              _tmpWorthItStatus = _tmp_10;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new TransactionEntity(_tmpId,_tmpAccountId,_tmpToAccountId,_tmpCategoryId,_tmpAmountMinor,_tmpCurrency,_tmpExchangeRateToBase,_tmpAmountInBaseMinor,_tmpType,_tmpPaymentMethod,_tmpTimestamp,_tmpNote,_tmpIsExcludedFromBudget,_tmpIsExcludedFromReports,_tmpIsReconciled,_tmpStatus,_tmpReceiptImagePath,_tmpRecurringRuleId,_tmpParentTransactionId,_tmpTransferCounterpartId,_tmpLatitude,_tmpLongitude,_tmpLocationName,_tmpWorthItStatus,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TransactionEntity>> getRecentTransactionsFlow(final int limit,
      final int offset) {
    final String _sql = "SELECT * FROM transactions ORDER BY timestamp DESC LIMIT ? OFFSET ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    _argIndex = 2;
    _statement.bindLong(_argIndex, offset);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfToAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "to_account_id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfExchangeRateToBase = CursorUtil.getColumnIndexOrThrow(_cursor, "exchange_rate_to_base");
          final int _cursorIndexOfAmountInBaseMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_in_base_minor");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "payment_method");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfIsExcludedFromBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "is_excluded_from_budget");
          final int _cursorIndexOfIsExcludedFromReports = CursorUtil.getColumnIndexOrThrow(_cursor, "is_excluded_from_reports");
          final int _cursorIndexOfIsReconciled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_reconciled");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReceiptImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "receipt_image_path");
          final int _cursorIndexOfRecurringRuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "recurring_rule_id");
          final int _cursorIndexOfParentTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "parent_transaction_id");
          final int _cursorIndexOfTransferCounterpartId = CursorUtil.getColumnIndexOrThrow(_cursor, "transfer_counterpart_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfLocationName = CursorUtil.getColumnIndexOrThrow(_cursor, "location_name");
          final int _cursorIndexOfWorthItStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "worth_it_status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpAccountId;
            _tmpAccountId = _cursor.getString(_cursorIndexOfAccountId);
            final String _tmpToAccountId;
            if (_cursor.isNull(_cursorIndexOfToAccountId)) {
              _tmpToAccountId = null;
            } else {
              _tmpToAccountId = _cursor.getString(_cursorIndexOfToAccountId);
            }
            final String _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId);
            }
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final double _tmpExchangeRateToBase;
            _tmpExchangeRateToBase = _cursor.getDouble(_cursorIndexOfExchangeRateToBase);
            final long _tmpAmountInBaseMinor;
            _tmpAmountInBaseMinor = _cursor.getLong(_cursorIndexOfAmountInBaseMinor);
            final TransactionType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            final TransactionType _tmp_1 = __roomTypeConverters.toTransactionType(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.TransactionType', but it was NULL.");
            } else {
              _tmpType = _tmp_1;
            }
            final PaymentMethod _tmpPaymentMethod;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfPaymentMethod)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfPaymentMethod);
            }
            final PaymentMethod _tmp_3 = __roomTypeConverters.toPaymentMethod(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.PaymentMethod', but it was NULL.");
            } else {
              _tmpPaymentMethod = _tmp_3;
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final boolean _tmpIsExcludedFromBudget;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsExcludedFromBudget);
            _tmpIsExcludedFromBudget = _tmp_4 != 0;
            final boolean _tmpIsExcludedFromReports;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfIsExcludedFromReports);
            _tmpIsExcludedFromReports = _tmp_5 != 0;
            final boolean _tmpIsReconciled;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfIsReconciled);
            _tmpIsReconciled = _tmp_6 != 0;
            final TransactionStatus _tmpStatus;
            final String _tmp_7;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getString(_cursorIndexOfStatus);
            }
            final TransactionStatus _tmp_8 = __roomTypeConverters.toTransactionStatus(_tmp_7);
            if (_tmp_8 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.TransactionStatus', but it was NULL.");
            } else {
              _tmpStatus = _tmp_8;
            }
            final String _tmpReceiptImagePath;
            if (_cursor.isNull(_cursorIndexOfReceiptImagePath)) {
              _tmpReceiptImagePath = null;
            } else {
              _tmpReceiptImagePath = _cursor.getString(_cursorIndexOfReceiptImagePath);
            }
            final String _tmpRecurringRuleId;
            if (_cursor.isNull(_cursorIndexOfRecurringRuleId)) {
              _tmpRecurringRuleId = null;
            } else {
              _tmpRecurringRuleId = _cursor.getString(_cursorIndexOfRecurringRuleId);
            }
            final String _tmpParentTransactionId;
            if (_cursor.isNull(_cursorIndexOfParentTransactionId)) {
              _tmpParentTransactionId = null;
            } else {
              _tmpParentTransactionId = _cursor.getString(_cursorIndexOfParentTransactionId);
            }
            final String _tmpTransferCounterpartId;
            if (_cursor.isNull(_cursorIndexOfTransferCounterpartId)) {
              _tmpTransferCounterpartId = null;
            } else {
              _tmpTransferCounterpartId = _cursor.getString(_cursorIndexOfTransferCounterpartId);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpLocationName;
            if (_cursor.isNull(_cursorIndexOfLocationName)) {
              _tmpLocationName = null;
            } else {
              _tmpLocationName = _cursor.getString(_cursorIndexOfLocationName);
            }
            final WorthItStatus _tmpWorthItStatus;
            final String _tmp_9;
            if (_cursor.isNull(_cursorIndexOfWorthItStatus)) {
              _tmp_9 = null;
            } else {
              _tmp_9 = _cursor.getString(_cursorIndexOfWorthItStatus);
            }
            final WorthItStatus _tmp_10 = __roomTypeConverters.toWorthItStatus(_tmp_9);
            if (_tmp_10 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.WorthItStatus', but it was NULL.");
            } else {
              _tmpWorthItStatus = _tmp_10;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpToAccountId,_tmpCategoryId,_tmpAmountMinor,_tmpCurrency,_tmpExchangeRateToBase,_tmpAmountInBaseMinor,_tmpType,_tmpPaymentMethod,_tmpTimestamp,_tmpNote,_tmpIsExcludedFromBudget,_tmpIsExcludedFromReports,_tmpIsReconciled,_tmpStatus,_tmpReceiptImagePath,_tmpRecurringRuleId,_tmpParentTransactionId,_tmpTransferCounterpartId,_tmpLatitude,_tmpLongitude,_tmpLocationName,_tmpWorthItStatus,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TransactionEntity>> getTransactionsByAccountFlow(final String accountId) {
    final String _sql = "SELECT * FROM transactions WHERE account_id = ? OR to_account_id = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, accountId);
    _argIndex = 2;
    _statement.bindString(_argIndex, accountId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfToAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "to_account_id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfExchangeRateToBase = CursorUtil.getColumnIndexOrThrow(_cursor, "exchange_rate_to_base");
          final int _cursorIndexOfAmountInBaseMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_in_base_minor");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "payment_method");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfIsExcludedFromBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "is_excluded_from_budget");
          final int _cursorIndexOfIsExcludedFromReports = CursorUtil.getColumnIndexOrThrow(_cursor, "is_excluded_from_reports");
          final int _cursorIndexOfIsReconciled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_reconciled");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReceiptImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "receipt_image_path");
          final int _cursorIndexOfRecurringRuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "recurring_rule_id");
          final int _cursorIndexOfParentTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "parent_transaction_id");
          final int _cursorIndexOfTransferCounterpartId = CursorUtil.getColumnIndexOrThrow(_cursor, "transfer_counterpart_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfLocationName = CursorUtil.getColumnIndexOrThrow(_cursor, "location_name");
          final int _cursorIndexOfWorthItStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "worth_it_status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpAccountId;
            _tmpAccountId = _cursor.getString(_cursorIndexOfAccountId);
            final String _tmpToAccountId;
            if (_cursor.isNull(_cursorIndexOfToAccountId)) {
              _tmpToAccountId = null;
            } else {
              _tmpToAccountId = _cursor.getString(_cursorIndexOfToAccountId);
            }
            final String _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId);
            }
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final double _tmpExchangeRateToBase;
            _tmpExchangeRateToBase = _cursor.getDouble(_cursorIndexOfExchangeRateToBase);
            final long _tmpAmountInBaseMinor;
            _tmpAmountInBaseMinor = _cursor.getLong(_cursorIndexOfAmountInBaseMinor);
            final TransactionType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            final TransactionType _tmp_1 = __roomTypeConverters.toTransactionType(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.TransactionType', but it was NULL.");
            } else {
              _tmpType = _tmp_1;
            }
            final PaymentMethod _tmpPaymentMethod;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfPaymentMethod)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfPaymentMethod);
            }
            final PaymentMethod _tmp_3 = __roomTypeConverters.toPaymentMethod(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.PaymentMethod', but it was NULL.");
            } else {
              _tmpPaymentMethod = _tmp_3;
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final boolean _tmpIsExcludedFromBudget;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsExcludedFromBudget);
            _tmpIsExcludedFromBudget = _tmp_4 != 0;
            final boolean _tmpIsExcludedFromReports;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfIsExcludedFromReports);
            _tmpIsExcludedFromReports = _tmp_5 != 0;
            final boolean _tmpIsReconciled;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfIsReconciled);
            _tmpIsReconciled = _tmp_6 != 0;
            final TransactionStatus _tmpStatus;
            final String _tmp_7;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getString(_cursorIndexOfStatus);
            }
            final TransactionStatus _tmp_8 = __roomTypeConverters.toTransactionStatus(_tmp_7);
            if (_tmp_8 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.TransactionStatus', but it was NULL.");
            } else {
              _tmpStatus = _tmp_8;
            }
            final String _tmpReceiptImagePath;
            if (_cursor.isNull(_cursorIndexOfReceiptImagePath)) {
              _tmpReceiptImagePath = null;
            } else {
              _tmpReceiptImagePath = _cursor.getString(_cursorIndexOfReceiptImagePath);
            }
            final String _tmpRecurringRuleId;
            if (_cursor.isNull(_cursorIndexOfRecurringRuleId)) {
              _tmpRecurringRuleId = null;
            } else {
              _tmpRecurringRuleId = _cursor.getString(_cursorIndexOfRecurringRuleId);
            }
            final String _tmpParentTransactionId;
            if (_cursor.isNull(_cursorIndexOfParentTransactionId)) {
              _tmpParentTransactionId = null;
            } else {
              _tmpParentTransactionId = _cursor.getString(_cursorIndexOfParentTransactionId);
            }
            final String _tmpTransferCounterpartId;
            if (_cursor.isNull(_cursorIndexOfTransferCounterpartId)) {
              _tmpTransferCounterpartId = null;
            } else {
              _tmpTransferCounterpartId = _cursor.getString(_cursorIndexOfTransferCounterpartId);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpLocationName;
            if (_cursor.isNull(_cursorIndexOfLocationName)) {
              _tmpLocationName = null;
            } else {
              _tmpLocationName = _cursor.getString(_cursorIndexOfLocationName);
            }
            final WorthItStatus _tmpWorthItStatus;
            final String _tmp_9;
            if (_cursor.isNull(_cursorIndexOfWorthItStatus)) {
              _tmp_9 = null;
            } else {
              _tmp_9 = _cursor.getString(_cursorIndexOfWorthItStatus);
            }
            final WorthItStatus _tmp_10 = __roomTypeConverters.toWorthItStatus(_tmp_9);
            if (_tmp_10 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.WorthItStatus', but it was NULL.");
            } else {
              _tmpWorthItStatus = _tmp_10;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpToAccountId,_tmpCategoryId,_tmpAmountMinor,_tmpCurrency,_tmpExchangeRateToBase,_tmpAmountInBaseMinor,_tmpType,_tmpPaymentMethod,_tmpTimestamp,_tmpNote,_tmpIsExcludedFromBudget,_tmpIsExcludedFromReports,_tmpIsReconciled,_tmpStatus,_tmpReceiptImagePath,_tmpRecurringRuleId,_tmpParentTransactionId,_tmpTransferCounterpartId,_tmpLatitude,_tmpLongitude,_tmpLocationName,_tmpWorthItStatus,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TransactionEntity>> getTransactionsBetweenFlow(final long startTime,
      final long endTime) {
    final String _sql = "SELECT * FROM transactions WHERE timestamp >= ? AND timestamp <= ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfToAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "to_account_id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfExchangeRateToBase = CursorUtil.getColumnIndexOrThrow(_cursor, "exchange_rate_to_base");
          final int _cursorIndexOfAmountInBaseMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_in_base_minor");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "payment_method");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfIsExcludedFromBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "is_excluded_from_budget");
          final int _cursorIndexOfIsExcludedFromReports = CursorUtil.getColumnIndexOrThrow(_cursor, "is_excluded_from_reports");
          final int _cursorIndexOfIsReconciled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_reconciled");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReceiptImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "receipt_image_path");
          final int _cursorIndexOfRecurringRuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "recurring_rule_id");
          final int _cursorIndexOfParentTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "parent_transaction_id");
          final int _cursorIndexOfTransferCounterpartId = CursorUtil.getColumnIndexOrThrow(_cursor, "transfer_counterpart_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfLocationName = CursorUtil.getColumnIndexOrThrow(_cursor, "location_name");
          final int _cursorIndexOfWorthItStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "worth_it_status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpAccountId;
            _tmpAccountId = _cursor.getString(_cursorIndexOfAccountId);
            final String _tmpToAccountId;
            if (_cursor.isNull(_cursorIndexOfToAccountId)) {
              _tmpToAccountId = null;
            } else {
              _tmpToAccountId = _cursor.getString(_cursorIndexOfToAccountId);
            }
            final String _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId);
            }
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final double _tmpExchangeRateToBase;
            _tmpExchangeRateToBase = _cursor.getDouble(_cursorIndexOfExchangeRateToBase);
            final long _tmpAmountInBaseMinor;
            _tmpAmountInBaseMinor = _cursor.getLong(_cursorIndexOfAmountInBaseMinor);
            final TransactionType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            final TransactionType _tmp_1 = __roomTypeConverters.toTransactionType(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.TransactionType', but it was NULL.");
            } else {
              _tmpType = _tmp_1;
            }
            final PaymentMethod _tmpPaymentMethod;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfPaymentMethod)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfPaymentMethod);
            }
            final PaymentMethod _tmp_3 = __roomTypeConverters.toPaymentMethod(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.PaymentMethod', but it was NULL.");
            } else {
              _tmpPaymentMethod = _tmp_3;
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final boolean _tmpIsExcludedFromBudget;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsExcludedFromBudget);
            _tmpIsExcludedFromBudget = _tmp_4 != 0;
            final boolean _tmpIsExcludedFromReports;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfIsExcludedFromReports);
            _tmpIsExcludedFromReports = _tmp_5 != 0;
            final boolean _tmpIsReconciled;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfIsReconciled);
            _tmpIsReconciled = _tmp_6 != 0;
            final TransactionStatus _tmpStatus;
            final String _tmp_7;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getString(_cursorIndexOfStatus);
            }
            final TransactionStatus _tmp_8 = __roomTypeConverters.toTransactionStatus(_tmp_7);
            if (_tmp_8 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.TransactionStatus', but it was NULL.");
            } else {
              _tmpStatus = _tmp_8;
            }
            final String _tmpReceiptImagePath;
            if (_cursor.isNull(_cursorIndexOfReceiptImagePath)) {
              _tmpReceiptImagePath = null;
            } else {
              _tmpReceiptImagePath = _cursor.getString(_cursorIndexOfReceiptImagePath);
            }
            final String _tmpRecurringRuleId;
            if (_cursor.isNull(_cursorIndexOfRecurringRuleId)) {
              _tmpRecurringRuleId = null;
            } else {
              _tmpRecurringRuleId = _cursor.getString(_cursorIndexOfRecurringRuleId);
            }
            final String _tmpParentTransactionId;
            if (_cursor.isNull(_cursorIndexOfParentTransactionId)) {
              _tmpParentTransactionId = null;
            } else {
              _tmpParentTransactionId = _cursor.getString(_cursorIndexOfParentTransactionId);
            }
            final String _tmpTransferCounterpartId;
            if (_cursor.isNull(_cursorIndexOfTransferCounterpartId)) {
              _tmpTransferCounterpartId = null;
            } else {
              _tmpTransferCounterpartId = _cursor.getString(_cursorIndexOfTransferCounterpartId);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpLocationName;
            if (_cursor.isNull(_cursorIndexOfLocationName)) {
              _tmpLocationName = null;
            } else {
              _tmpLocationName = _cursor.getString(_cursorIndexOfLocationName);
            }
            final WorthItStatus _tmpWorthItStatus;
            final String _tmp_9;
            if (_cursor.isNull(_cursorIndexOfWorthItStatus)) {
              _tmp_9 = null;
            } else {
              _tmp_9 = _cursor.getString(_cursorIndexOfWorthItStatus);
            }
            final WorthItStatus _tmp_10 = __roomTypeConverters.toWorthItStatus(_tmp_9);
            if (_tmp_10 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.WorthItStatus', but it was NULL.");
            } else {
              _tmpWorthItStatus = _tmp_10;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpToAccountId,_tmpCategoryId,_tmpAmountMinor,_tmpCurrency,_tmpExchangeRateToBase,_tmpAmountInBaseMinor,_tmpType,_tmpPaymentMethod,_tmpTimestamp,_tmpNote,_tmpIsExcludedFromBudget,_tmpIsExcludedFromReports,_tmpIsReconciled,_tmpStatus,_tmpReceiptImagePath,_tmpRecurringRuleId,_tmpParentTransactionId,_tmpTransferCounterpartId,_tmpLatitude,_tmpLongitude,_tmpLocationName,_tmpWorthItStatus,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TransactionEntity>> getTransactionsByCategoryFlow(final String categoryId) {
    final String _sql = "SELECT * FROM transactions WHERE category_id = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, categoryId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfToAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "to_account_id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfExchangeRateToBase = CursorUtil.getColumnIndexOrThrow(_cursor, "exchange_rate_to_base");
          final int _cursorIndexOfAmountInBaseMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_in_base_minor");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "payment_method");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfIsExcludedFromBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "is_excluded_from_budget");
          final int _cursorIndexOfIsExcludedFromReports = CursorUtil.getColumnIndexOrThrow(_cursor, "is_excluded_from_reports");
          final int _cursorIndexOfIsReconciled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_reconciled");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReceiptImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "receipt_image_path");
          final int _cursorIndexOfRecurringRuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "recurring_rule_id");
          final int _cursorIndexOfParentTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "parent_transaction_id");
          final int _cursorIndexOfTransferCounterpartId = CursorUtil.getColumnIndexOrThrow(_cursor, "transfer_counterpart_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfLocationName = CursorUtil.getColumnIndexOrThrow(_cursor, "location_name");
          final int _cursorIndexOfWorthItStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "worth_it_status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpAccountId;
            _tmpAccountId = _cursor.getString(_cursorIndexOfAccountId);
            final String _tmpToAccountId;
            if (_cursor.isNull(_cursorIndexOfToAccountId)) {
              _tmpToAccountId = null;
            } else {
              _tmpToAccountId = _cursor.getString(_cursorIndexOfToAccountId);
            }
            final String _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId);
            }
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final double _tmpExchangeRateToBase;
            _tmpExchangeRateToBase = _cursor.getDouble(_cursorIndexOfExchangeRateToBase);
            final long _tmpAmountInBaseMinor;
            _tmpAmountInBaseMinor = _cursor.getLong(_cursorIndexOfAmountInBaseMinor);
            final TransactionType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            final TransactionType _tmp_1 = __roomTypeConverters.toTransactionType(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.TransactionType', but it was NULL.");
            } else {
              _tmpType = _tmp_1;
            }
            final PaymentMethod _tmpPaymentMethod;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfPaymentMethod)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfPaymentMethod);
            }
            final PaymentMethod _tmp_3 = __roomTypeConverters.toPaymentMethod(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.PaymentMethod', but it was NULL.");
            } else {
              _tmpPaymentMethod = _tmp_3;
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final boolean _tmpIsExcludedFromBudget;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsExcludedFromBudget);
            _tmpIsExcludedFromBudget = _tmp_4 != 0;
            final boolean _tmpIsExcludedFromReports;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfIsExcludedFromReports);
            _tmpIsExcludedFromReports = _tmp_5 != 0;
            final boolean _tmpIsReconciled;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfIsReconciled);
            _tmpIsReconciled = _tmp_6 != 0;
            final TransactionStatus _tmpStatus;
            final String _tmp_7;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getString(_cursorIndexOfStatus);
            }
            final TransactionStatus _tmp_8 = __roomTypeConverters.toTransactionStatus(_tmp_7);
            if (_tmp_8 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.TransactionStatus', but it was NULL.");
            } else {
              _tmpStatus = _tmp_8;
            }
            final String _tmpReceiptImagePath;
            if (_cursor.isNull(_cursorIndexOfReceiptImagePath)) {
              _tmpReceiptImagePath = null;
            } else {
              _tmpReceiptImagePath = _cursor.getString(_cursorIndexOfReceiptImagePath);
            }
            final String _tmpRecurringRuleId;
            if (_cursor.isNull(_cursorIndexOfRecurringRuleId)) {
              _tmpRecurringRuleId = null;
            } else {
              _tmpRecurringRuleId = _cursor.getString(_cursorIndexOfRecurringRuleId);
            }
            final String _tmpParentTransactionId;
            if (_cursor.isNull(_cursorIndexOfParentTransactionId)) {
              _tmpParentTransactionId = null;
            } else {
              _tmpParentTransactionId = _cursor.getString(_cursorIndexOfParentTransactionId);
            }
            final String _tmpTransferCounterpartId;
            if (_cursor.isNull(_cursorIndexOfTransferCounterpartId)) {
              _tmpTransferCounterpartId = null;
            } else {
              _tmpTransferCounterpartId = _cursor.getString(_cursorIndexOfTransferCounterpartId);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpLocationName;
            if (_cursor.isNull(_cursorIndexOfLocationName)) {
              _tmpLocationName = null;
            } else {
              _tmpLocationName = _cursor.getString(_cursorIndexOfLocationName);
            }
            final WorthItStatus _tmpWorthItStatus;
            final String _tmp_9;
            if (_cursor.isNull(_cursorIndexOfWorthItStatus)) {
              _tmp_9 = null;
            } else {
              _tmp_9 = _cursor.getString(_cursorIndexOfWorthItStatus);
            }
            final WorthItStatus _tmp_10 = __roomTypeConverters.toWorthItStatus(_tmp_9);
            if (_tmp_10 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.WorthItStatus', but it was NULL.");
            } else {
              _tmpWorthItStatus = _tmp_10;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpToAccountId,_tmpCategoryId,_tmpAmountMinor,_tmpCurrency,_tmpExchangeRateToBase,_tmpAmountInBaseMinor,_tmpType,_tmpPaymentMethod,_tmpTimestamp,_tmpNote,_tmpIsExcludedFromBudget,_tmpIsExcludedFromReports,_tmpIsReconciled,_tmpStatus,_tmpReceiptImagePath,_tmpRecurringRuleId,_tmpParentTransactionId,_tmpTransferCounterpartId,_tmpLatitude,_tmpLongitude,_tmpLocationName,_tmpWorthItStatus,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object searchTransactions(final String query, final int limit,
      final Continuation<? super List<TransactionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT t.* FROM transactions t\n"
            + "        JOIN transactions_fts fts ON t.id = fts.transaction_id\n"
            + "        WHERE transactions_fts MATCH ?\n"
            + "        ORDER BY t.timestamp DESC\n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfToAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "to_account_id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfExchangeRateToBase = CursorUtil.getColumnIndexOrThrow(_cursor, "exchange_rate_to_base");
          final int _cursorIndexOfAmountInBaseMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_in_base_minor");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "payment_method");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfIsExcludedFromBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "is_excluded_from_budget");
          final int _cursorIndexOfIsExcludedFromReports = CursorUtil.getColumnIndexOrThrow(_cursor, "is_excluded_from_reports");
          final int _cursorIndexOfIsReconciled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_reconciled");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReceiptImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "receipt_image_path");
          final int _cursorIndexOfRecurringRuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "recurring_rule_id");
          final int _cursorIndexOfParentTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "parent_transaction_id");
          final int _cursorIndexOfTransferCounterpartId = CursorUtil.getColumnIndexOrThrow(_cursor, "transfer_counterpart_id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfLocationName = CursorUtil.getColumnIndexOrThrow(_cursor, "location_name");
          final int _cursorIndexOfWorthItStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "worth_it_status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpAccountId;
            _tmpAccountId = _cursor.getString(_cursorIndexOfAccountId);
            final String _tmpToAccountId;
            if (_cursor.isNull(_cursorIndexOfToAccountId)) {
              _tmpToAccountId = null;
            } else {
              _tmpToAccountId = _cursor.getString(_cursorIndexOfToAccountId);
            }
            final String _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId);
            }
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final double _tmpExchangeRateToBase;
            _tmpExchangeRateToBase = _cursor.getDouble(_cursorIndexOfExchangeRateToBase);
            final long _tmpAmountInBaseMinor;
            _tmpAmountInBaseMinor = _cursor.getLong(_cursorIndexOfAmountInBaseMinor);
            final TransactionType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            final TransactionType _tmp_1 = __roomTypeConverters.toTransactionType(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.TransactionType', but it was NULL.");
            } else {
              _tmpType = _tmp_1;
            }
            final PaymentMethod _tmpPaymentMethod;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfPaymentMethod)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfPaymentMethod);
            }
            final PaymentMethod _tmp_3 = __roomTypeConverters.toPaymentMethod(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.PaymentMethod', but it was NULL.");
            } else {
              _tmpPaymentMethod = _tmp_3;
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final boolean _tmpIsExcludedFromBudget;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsExcludedFromBudget);
            _tmpIsExcludedFromBudget = _tmp_4 != 0;
            final boolean _tmpIsExcludedFromReports;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfIsExcludedFromReports);
            _tmpIsExcludedFromReports = _tmp_5 != 0;
            final boolean _tmpIsReconciled;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfIsReconciled);
            _tmpIsReconciled = _tmp_6 != 0;
            final TransactionStatus _tmpStatus;
            final String _tmp_7;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _cursor.getString(_cursorIndexOfStatus);
            }
            final TransactionStatus _tmp_8 = __roomTypeConverters.toTransactionStatus(_tmp_7);
            if (_tmp_8 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.TransactionStatus', but it was NULL.");
            } else {
              _tmpStatus = _tmp_8;
            }
            final String _tmpReceiptImagePath;
            if (_cursor.isNull(_cursorIndexOfReceiptImagePath)) {
              _tmpReceiptImagePath = null;
            } else {
              _tmpReceiptImagePath = _cursor.getString(_cursorIndexOfReceiptImagePath);
            }
            final String _tmpRecurringRuleId;
            if (_cursor.isNull(_cursorIndexOfRecurringRuleId)) {
              _tmpRecurringRuleId = null;
            } else {
              _tmpRecurringRuleId = _cursor.getString(_cursorIndexOfRecurringRuleId);
            }
            final String _tmpParentTransactionId;
            if (_cursor.isNull(_cursorIndexOfParentTransactionId)) {
              _tmpParentTransactionId = null;
            } else {
              _tmpParentTransactionId = _cursor.getString(_cursorIndexOfParentTransactionId);
            }
            final String _tmpTransferCounterpartId;
            if (_cursor.isNull(_cursorIndexOfTransferCounterpartId)) {
              _tmpTransferCounterpartId = null;
            } else {
              _tmpTransferCounterpartId = _cursor.getString(_cursorIndexOfTransferCounterpartId);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpLocationName;
            if (_cursor.isNull(_cursorIndexOfLocationName)) {
              _tmpLocationName = null;
            } else {
              _tmpLocationName = _cursor.getString(_cursorIndexOfLocationName);
            }
            final WorthItStatus _tmpWorthItStatus;
            final String _tmp_9;
            if (_cursor.isNull(_cursorIndexOfWorthItStatus)) {
              _tmp_9 = null;
            } else {
              _tmp_9 = _cursor.getString(_cursorIndexOfWorthItStatus);
            }
            final WorthItStatus _tmp_10 = __roomTypeConverters.toWorthItStatus(_tmp_9);
            if (_tmp_10 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.WorthItStatus', but it was NULL.");
            } else {
              _tmpWorthItStatus = _tmp_10;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpToAccountId,_tmpCategoryId,_tmpAmountMinor,_tmpCurrency,_tmpExchangeRateToBase,_tmpAmountInBaseMinor,_tmpType,_tmpPaymentMethod,_tmpTimestamp,_tmpNote,_tmpIsExcludedFromBudget,_tmpIsExcludedFromReports,_tmpIsReconciled,_tmpStatus,_tmpReceiptImagePath,_tmpRecurringRuleId,_tmpParentTransactionId,_tmpTransferCounterpartId,_tmpLatitude,_tmpLongitude,_tmpLocationName,_tmpWorthItStatus,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAccount(final String id, final Continuation<? super AccountEntity> $completion) {
    final String _sql = "SELECT * FROM accounts WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AccountEntity>() {
      @Override
      @Nullable
      public AccountEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfOpeningBalanceMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "opening_balance_minor");
          final int _cursorIndexOfCurrentBalanceMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "current_balance_minor");
          final int _cursorIndexOfCreditLimitMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "credit_limit_minor");
          final int _cursorIndexOfBillingDay = CursorUtil.getColumnIndexOrThrow(_cursor, "billing_day");
          final int _cursorIndexOfDueDay = CursorUtil.getColumnIndexOrThrow(_cursor, "due_day");
          final int _cursorIndexOfGracePeriodDays = CursorUtil.getColumnIndexOrThrow(_cursor, "grace_period_days");
          final int _cursorIndexOfAprPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "apr_percentage");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfIconName = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_name");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sort_order");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final AccountEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final AccountType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            final AccountType _tmp_1 = __roomTypeConverters.toAccountType(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.AccountType', but it was NULL.");
            } else {
              _tmpType = _tmp_1;
            }
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final long _tmpOpeningBalanceMinor;
            _tmpOpeningBalanceMinor = _cursor.getLong(_cursorIndexOfOpeningBalanceMinor);
            final long _tmpCurrentBalanceMinor;
            _tmpCurrentBalanceMinor = _cursor.getLong(_cursorIndexOfCurrentBalanceMinor);
            final long _tmpCreditLimitMinor;
            _tmpCreditLimitMinor = _cursor.getLong(_cursorIndexOfCreditLimitMinor);
            final int _tmpBillingDay;
            _tmpBillingDay = _cursor.getInt(_cursorIndexOfBillingDay);
            final int _tmpDueDay;
            _tmpDueDay = _cursor.getInt(_cursorIndexOfDueDay);
            final int _tmpGracePeriodDays;
            _tmpGracePeriodDays = _cursor.getInt(_cursorIndexOfGracePeriodDays);
            final double _tmpAprPercentage;
            _tmpAprPercentage = _cursor.getDouble(_cursorIndexOfAprPercentage);
            final boolean _tmpIsArchived;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp_2 != 0;
            final String _tmpColorHex;
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
            final String _tmpIconName;
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new AccountEntity(_tmpId,_tmpName,_tmpType,_tmpCurrency,_tmpOpeningBalanceMinor,_tmpCurrentBalanceMinor,_tmpCreditLimitMinor,_tmpBillingDay,_tmpDueDay,_tmpGracePeriodDays,_tmpAprPercentage,_tmpIsArchived,_tmpColorHex,_tmpIconName,_tmpSortOrder,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getMonthlyAggregate(final String yearMonth, final String currency,
      final Continuation<? super MonthlyAggregateEntity> $completion) {
    final String _sql = "SELECT * FROM monthly_aggregates WHERE year_month = ? AND currency = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, yearMonth);
    _argIndex = 2;
    _statement.bindString(_argIndex, currency);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MonthlyAggregateEntity>() {
      @Override
      @Nullable
      public MonthlyAggregateEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfYearMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "year_month");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfTotalIncomeMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "total_income_minor");
          final int _cursorIndexOfTotalExpenseMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "total_expense_minor");
          final int _cursorIndexOfNetSavingsMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "net_savings_minor");
          final int _cursorIndexOfTransactionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "transaction_count");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final MonthlyAggregateEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpYearMonth;
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final long _tmpTotalIncomeMinor;
            _tmpTotalIncomeMinor = _cursor.getLong(_cursorIndexOfTotalIncomeMinor);
            final long _tmpTotalExpenseMinor;
            _tmpTotalExpenseMinor = _cursor.getLong(_cursorIndexOfTotalExpenseMinor);
            final long _tmpNetSavingsMinor;
            _tmpNetSavingsMinor = _cursor.getLong(_cursorIndexOfNetSavingsMinor);
            final int _tmpTransactionCount;
            _tmpTransactionCount = _cursor.getInt(_cursorIndexOfTransactionCount);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new MonthlyAggregateEntity(_tmpYearMonth,_tmpCurrency,_tmpTotalIncomeMinor,_tmpTotalExpenseMinor,_tmpNetSavingsMinor,_tmpTransactionCount,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCategoryMonthlyAggregate(final String yearMonth, final String categoryId,
      final String currency,
      final Continuation<? super CategoryMonthlyAggregateEntity> $completion) {
    final String _sql = "SELECT * FROM category_monthly_aggregates WHERE year_month = ? AND category_id = ? AND currency = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, yearMonth);
    _argIndex = 2;
    _statement.bindString(_argIndex, categoryId);
    _argIndex = 3;
    _statement.bindString(_argIndex, currency);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CategoryMonthlyAggregateEntity>() {
      @Override
      @Nullable
      public CategoryMonthlyAggregateEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfYearMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "year_month");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfTotalAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "total_amount_minor");
          final int _cursorIndexOfTransactionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "transaction_count");
          final CategoryMonthlyAggregateEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpYearMonth;
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth);
            final String _tmpCategoryId;
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final long _tmpTotalAmountMinor;
            _tmpTotalAmountMinor = _cursor.getLong(_cursorIndexOfTotalAmountMinor);
            final int _tmpTransactionCount;
            _tmpTransactionCount = _cursor.getInt(_cursorIndexOfTransactionCount);
            _result = new CategoryMonthlyAggregateEntity(_tmpYearMonth,_tmpCategoryId,_tmpCurrency,_tmpTotalAmountMinor,_tmpTransactionCount);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAccountMonthlyAggregate(final String yearMonth, final String accountId,
      final Continuation<? super AccountMonthlyAggregateEntity> $completion) {
    final String _sql = "SELECT * FROM account_monthly_aggregates WHERE year_month = ? AND account_id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, yearMonth);
    _argIndex = 2;
    _statement.bindString(_argIndex, accountId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AccountMonthlyAggregateEntity>() {
      @Override
      @Nullable
      public AccountMonthlyAggregateEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfYearMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "year_month");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfStartingBalanceMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "starting_balance_minor");
          final int _cursorIndexOfEndingBalanceMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "ending_balance_minor");
          final int _cursorIndexOfTotalInflowMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "total_inflow_minor");
          final int _cursorIndexOfTotalOutflowMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "total_outflow_minor");
          final AccountMonthlyAggregateEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpYearMonth;
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth);
            final String _tmpAccountId;
            _tmpAccountId = _cursor.getString(_cursorIndexOfAccountId);
            final long _tmpStartingBalanceMinor;
            _tmpStartingBalanceMinor = _cursor.getLong(_cursorIndexOfStartingBalanceMinor);
            final long _tmpEndingBalanceMinor;
            _tmpEndingBalanceMinor = _cursor.getLong(_cursorIndexOfEndingBalanceMinor);
            final long _tmpTotalInflowMinor;
            _tmpTotalInflowMinor = _cursor.getLong(_cursorIndexOfTotalInflowMinor);
            final long _tmpTotalOutflowMinor;
            _tmpTotalOutflowMinor = _cursor.getLong(_cursorIndexOfTotalOutflowMinor);
            _result = new AccountMonthlyAggregateEntity(_tmpYearMonth,_tmpAccountId,_tmpStartingBalanceMinor,_tmpEndingBalanceMinor,_tmpTotalInflowMinor,_tmpTotalOutflowMinor);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
