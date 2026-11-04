package com.artha.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.artha.database.converter.RoomTypeConverters;
import com.artha.database.entity.BudgetCategoryCrossRef;
import com.artha.database.entity.BudgetEntity;
import com.artha.model.BudgetPeriod;
import java.lang.Class;
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
public final class BudgetDao_Impl implements BudgetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BudgetEntity> __insertionAdapterOfBudgetEntity;

  private final RoomTypeConverters __roomTypeConverters = new RoomTypeConverters();

  private final EntityInsertionAdapter<BudgetCategoryCrossRef> __insertionAdapterOfBudgetCategoryCrossRef;

  private final SharedSQLiteStatement __preparedStmtOfClearBudgetCategories;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBudget;

  public BudgetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBudgetEntity = new EntityInsertionAdapter<BudgetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `budgets` (`id`,`name`,`amount_minor`,`currency`,`period_type`,`start_date`,`end_date`,`rollover_enabled`,`envelope_mode`,`alert_threshold_percentage`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BudgetEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getAmountMinor());
        statement.bindString(4, entity.getCurrency());
        final String _tmp = __roomTypeConverters.fromBudgetPeriod(entity.getPeriodType());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp);
        }
        statement.bindLong(6, entity.getStartDate());
        statement.bindLong(7, entity.getEndDate());
        final int _tmp_1 = entity.getRolloverEnabled() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        final int _tmp_2 = entity.getEnvelopeMode() ? 1 : 0;
        statement.bindLong(9, _tmp_2);
        statement.bindLong(10, entity.getAlertThresholdPercentage());
      }
    };
    this.__insertionAdapterOfBudgetCategoryCrossRef = new EntityInsertionAdapter<BudgetCategoryCrossRef>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `budget_categories` (`budget_id`,`category_id`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BudgetCategoryCrossRef entity) {
        statement.bindString(1, entity.getBudgetId());
        statement.bindString(2, entity.getCategoryId());
      }
    };
    this.__preparedStmtOfClearBudgetCategories = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM budget_categories WHERE budget_id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteBudget = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM budgets WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertBudget(final BudgetEntity budget,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBudgetEntity.insert(budget);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertBudgetCategories(final List<BudgetCategoryCrossRef> crossRefs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBudgetCategoryCrossRef.insert(crossRefs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearBudgetCategories(final String budgetId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearBudgetCategories.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, budgetId);
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
          __preparedStmtOfClearBudgetCategories.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBudget(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBudget.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfDeleteBudget.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BudgetEntity>> getAllBudgetsFlow() {
    final String _sql = "SELECT `budgets`.`id` AS `id`, `budgets`.`name` AS `name`, `budgets`.`amount_minor` AS `amount_minor`, `budgets`.`currency` AS `currency`, `budgets`.`period_type` AS `period_type`, `budgets`.`start_date` AS `start_date`, `budgets`.`end_date` AS `end_date`, `budgets`.`rollover_enabled` AS `rollover_enabled`, `budgets`.`envelope_mode` AS `envelope_mode`, `budgets`.`alert_threshold_percentage` AS `alert_threshold_percentage` FROM budgets ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"budgets"}, new Callable<List<BudgetEntity>>() {
      @Override
      @NonNull
      public List<BudgetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfAmountMinor = 2;
          final int _cursorIndexOfCurrency = 3;
          final int _cursorIndexOfPeriodType = 4;
          final int _cursorIndexOfStartDate = 5;
          final int _cursorIndexOfEndDate = 6;
          final int _cursorIndexOfRolloverEnabled = 7;
          final int _cursorIndexOfEnvelopeMode = 8;
          final int _cursorIndexOfAlertThresholdPercentage = 9;
          final List<BudgetEntity> _result = new ArrayList<BudgetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BudgetEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final BudgetPeriod _tmpPeriodType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPeriodType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPeriodType);
            }
            final BudgetPeriod _tmp_1 = __roomTypeConverters.toBudgetPeriod(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.BudgetPeriod', but it was NULL.");
            } else {
              _tmpPeriodType = _tmp_1;
            }
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final boolean _tmpRolloverEnabled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfRolloverEnabled);
            _tmpRolloverEnabled = _tmp_2 != 0;
            final boolean _tmpEnvelopeMode;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfEnvelopeMode);
            _tmpEnvelopeMode = _tmp_3 != 0;
            final int _tmpAlertThresholdPercentage;
            _tmpAlertThresholdPercentage = _cursor.getInt(_cursorIndexOfAlertThresholdPercentage);
            _item = new BudgetEntity(_tmpId,_tmpName,_tmpAmountMinor,_tmpCurrency,_tmpPeriodType,_tmpStartDate,_tmpEndDate,_tmpRolloverEnabled,_tmpEnvelopeMode,_tmpAlertThresholdPercentage);
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
  public Object getBudgetById(final String id,
      final Continuation<? super BudgetEntity> $completion) {
    final String _sql = "SELECT * FROM budgets WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BudgetEntity>() {
      @Override
      @Nullable
      public BudgetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfPeriodType = CursorUtil.getColumnIndexOrThrow(_cursor, "period_type");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "start_date");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "end_date");
          final int _cursorIndexOfRolloverEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "rollover_enabled");
          final int _cursorIndexOfEnvelopeMode = CursorUtil.getColumnIndexOrThrow(_cursor, "envelope_mode");
          final int _cursorIndexOfAlertThresholdPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "alert_threshold_percentage");
          final BudgetEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final BudgetPeriod _tmpPeriodType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPeriodType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPeriodType);
            }
            final BudgetPeriod _tmp_1 = __roomTypeConverters.toBudgetPeriod(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.BudgetPeriod', but it was NULL.");
            } else {
              _tmpPeriodType = _tmp_1;
            }
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final boolean _tmpRolloverEnabled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfRolloverEnabled);
            _tmpRolloverEnabled = _tmp_2 != 0;
            final boolean _tmpEnvelopeMode;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfEnvelopeMode);
            _tmpEnvelopeMode = _tmp_3 != 0;
            final int _tmpAlertThresholdPercentage;
            _tmpAlertThresholdPercentage = _cursor.getInt(_cursorIndexOfAlertThresholdPercentage);
            _result = new BudgetEntity(_tmpId,_tmpName,_tmpAmountMinor,_tmpCurrency,_tmpPeriodType,_tmpStartDate,_tmpEndDate,_tmpRolloverEnabled,_tmpEnvelopeMode,_tmpAlertThresholdPercentage);
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
