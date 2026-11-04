package com.artha.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.artha.database.entity.AccountMonthlyAggregateEntity;
import com.artha.database.entity.CategoryMonthlyAggregateEntity;
import com.artha.database.entity.MonthlyAggregateEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class AggregateDao_Impl implements AggregateDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MonthlyAggregateEntity> __insertionAdapterOfMonthlyAggregateEntity;

  private final EntityInsertionAdapter<CategoryMonthlyAggregateEntity> __insertionAdapterOfCategoryMonthlyAggregateEntity;

  private final EntityInsertionAdapter<AccountMonthlyAggregateEntity> __insertionAdapterOfAccountMonthlyAggregateEntity;

  public AggregateDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
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
  public Flow<List<MonthlyAggregateEntity>> getMonthlyAggregatesFlow(final String currency) {
    final String _sql = "SELECT * FROM monthly_aggregates WHERE currency = ? ORDER BY year_month DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, currency);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"monthly_aggregates"}, new Callable<List<MonthlyAggregateEntity>>() {
      @Override
      @NonNull
      public List<MonthlyAggregateEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfYearMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "year_month");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfTotalIncomeMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "total_income_minor");
          final int _cursorIndexOfTotalExpenseMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "total_expense_minor");
          final int _cursorIndexOfNetSavingsMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "net_savings_minor");
          final int _cursorIndexOfTransactionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "transaction_count");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<MonthlyAggregateEntity> _result = new ArrayList<MonthlyAggregateEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MonthlyAggregateEntity _item;
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
            _item = new MonthlyAggregateEntity(_tmpYearMonth,_tmpCurrency,_tmpTotalIncomeMinor,_tmpTotalExpenseMinor,_tmpNetSavingsMinor,_tmpTransactionCount,_tmpUpdatedAt);
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
  public Flow<List<CategoryMonthlyAggregateEntity>> getCategoryMonthlyAggregatesFlow(
      final String yearMonth, final String currency) {
    final String _sql = "SELECT * FROM category_monthly_aggregates WHERE year_month = ? AND currency = ? ORDER BY total_amount_minor DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, yearMonth);
    _argIndex = 2;
    _statement.bindString(_argIndex, currency);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"category_monthly_aggregates"}, new Callable<List<CategoryMonthlyAggregateEntity>>() {
      @Override
      @NonNull
      public List<CategoryMonthlyAggregateEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfYearMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "year_month");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfTotalAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "total_amount_minor");
          final int _cursorIndexOfTransactionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "transaction_count");
          final List<CategoryMonthlyAggregateEntity> _result = new ArrayList<CategoryMonthlyAggregateEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CategoryMonthlyAggregateEntity _item;
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
            _item = new CategoryMonthlyAggregateEntity(_tmpYearMonth,_tmpCategoryId,_tmpCurrency,_tmpTotalAmountMinor,_tmpTransactionCount);
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
  public Flow<List<AccountMonthlyAggregateEntity>> getAccountMonthlyAggregatesFlow(
      final String yearMonth) {
    final String _sql = "SELECT * FROM account_monthly_aggregates WHERE year_month = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, yearMonth);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"account_monthly_aggregates"}, new Callable<List<AccountMonthlyAggregateEntity>>() {
      @Override
      @NonNull
      public List<AccountMonthlyAggregateEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfYearMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "year_month");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfStartingBalanceMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "starting_balance_minor");
          final int _cursorIndexOfEndingBalanceMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "ending_balance_minor");
          final int _cursorIndexOfTotalInflowMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "total_inflow_minor");
          final int _cursorIndexOfTotalOutflowMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "total_outflow_minor");
          final List<AccountMonthlyAggregateEntity> _result = new ArrayList<AccountMonthlyAggregateEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AccountMonthlyAggregateEntity _item;
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
            _item = new AccountMonthlyAggregateEntity(_tmpYearMonth,_tmpAccountId,_tmpStartingBalanceMinor,_tmpEndingBalanceMinor,_tmpTotalInflowMinor,_tmpTotalOutflowMinor);
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
