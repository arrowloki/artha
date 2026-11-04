package com.artha.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.artha.database.converter.RoomTypeConverters;
import com.artha.database.entity.RecurringRuleEntity;
import com.artha.model.PaymentMethod;
import com.artha.model.RecurrenceFrequency;
import com.artha.model.TransactionType;
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
public final class RecurringRuleDao_Impl implements RecurringRuleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RecurringRuleEntity> __insertionAdapterOfRecurringRuleEntity;

  private final RoomTypeConverters __roomTypeConverters = new RoomTypeConverters();

  private final EntityDeletionOrUpdateAdapter<RecurringRuleEntity> __updateAdapterOfRecurringRuleEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateNextDueDate;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRecurringRule;

  public RecurringRuleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRecurringRuleEntity = new EntityInsertionAdapter<RecurringRuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `recurring_rules` (`id`,`title`,`type`,`amount_minor`,`currency`,`account_id`,`to_account_id`,`category_id`,`payment_method`,`frequency`,`interval`,`next_due_date`,`auto_apply`,`reminder_days_before`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecurringRuleEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        final String _tmp = __roomTypeConverters.fromTransactionType(entity.getType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        statement.bindLong(4, entity.getAmountMinor());
        statement.bindString(5, entity.getCurrency());
        statement.bindString(6, entity.getAccountId());
        if (entity.getToAccountId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getToAccountId());
        }
        if (entity.getCategoryId() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getCategoryId());
        }
        final String _tmp_1 = __roomTypeConverters.fromPaymentMethod(entity.getPaymentMethod());
        if (_tmp_1 == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, _tmp_1);
        }
        final String _tmp_2 = __roomTypeConverters.fromRecurrenceFrequency(entity.getFrequency());
        if (_tmp_2 == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, _tmp_2);
        }
        statement.bindLong(11, entity.getInterval());
        statement.bindLong(12, entity.getNextDueDate());
        final int _tmp_3 = entity.getAutoApply() ? 1 : 0;
        statement.bindLong(13, _tmp_3);
        statement.bindLong(14, entity.getReminderDaysBefore());
      }
    };
    this.__updateAdapterOfRecurringRuleEntity = new EntityDeletionOrUpdateAdapter<RecurringRuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `recurring_rules` SET `id` = ?,`title` = ?,`type` = ?,`amount_minor` = ?,`currency` = ?,`account_id` = ?,`to_account_id` = ?,`category_id` = ?,`payment_method` = ?,`frequency` = ?,`interval` = ?,`next_due_date` = ?,`auto_apply` = ?,`reminder_days_before` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecurringRuleEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        final String _tmp = __roomTypeConverters.fromTransactionType(entity.getType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        statement.bindLong(4, entity.getAmountMinor());
        statement.bindString(5, entity.getCurrency());
        statement.bindString(6, entity.getAccountId());
        if (entity.getToAccountId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getToAccountId());
        }
        if (entity.getCategoryId() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getCategoryId());
        }
        final String _tmp_1 = __roomTypeConverters.fromPaymentMethod(entity.getPaymentMethod());
        if (_tmp_1 == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, _tmp_1);
        }
        final String _tmp_2 = __roomTypeConverters.fromRecurrenceFrequency(entity.getFrequency());
        if (_tmp_2 == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, _tmp_2);
        }
        statement.bindLong(11, entity.getInterval());
        statement.bindLong(12, entity.getNextDueDate());
        final int _tmp_3 = entity.getAutoApply() ? 1 : 0;
        statement.bindLong(13, _tmp_3);
        statement.bindLong(14, entity.getReminderDaysBefore());
        statement.bindString(15, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateNextDueDate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE recurring_rules SET next_due_date = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteRecurringRule = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM recurring_rules WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertRecurringRule(final RecurringRuleEntity rule,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRecurringRuleEntity.insert(rule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRecurringRule(final RecurringRuleEntity rule,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRecurringRuleEntity.handle(rule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateNextDueDate(final String id, final long nextDueDate,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateNextDueDate.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, nextDueDate);
        _argIndex = 2;
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
          __preparedStmtOfUpdateNextDueDate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRecurringRule(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRecurringRule.acquire();
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
          __preparedStmtOfDeleteRecurringRule.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RecurringRuleEntity>> getAllRecurringRulesFlow() {
    final String _sql = "SELECT `recurring_rules`.`id` AS `id`, `recurring_rules`.`title` AS `title`, `recurring_rules`.`type` AS `type`, `recurring_rules`.`amount_minor` AS `amount_minor`, `recurring_rules`.`currency` AS `currency`, `recurring_rules`.`account_id` AS `account_id`, `recurring_rules`.`to_account_id` AS `to_account_id`, `recurring_rules`.`category_id` AS `category_id`, `recurring_rules`.`payment_method` AS `payment_method`, `recurring_rules`.`frequency` AS `frequency`, `recurring_rules`.`interval` AS `interval`, `recurring_rules`.`next_due_date` AS `next_due_date`, `recurring_rules`.`auto_apply` AS `auto_apply`, `recurring_rules`.`reminder_days_before` AS `reminder_days_before` FROM recurring_rules ORDER BY next_due_date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"recurring_rules"}, new Callable<List<RecurringRuleEntity>>() {
      @Override
      @NonNull
      public List<RecurringRuleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfTitle = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfAmountMinor = 3;
          final int _cursorIndexOfCurrency = 4;
          final int _cursorIndexOfAccountId = 5;
          final int _cursorIndexOfToAccountId = 6;
          final int _cursorIndexOfCategoryId = 7;
          final int _cursorIndexOfPaymentMethod = 8;
          final int _cursorIndexOfFrequency = 9;
          final int _cursorIndexOfInterval = 10;
          final int _cursorIndexOfNextDueDate = 11;
          final int _cursorIndexOfAutoApply = 12;
          final int _cursorIndexOfReminderDaysBefore = 13;
          final List<RecurringRuleEntity> _result = new ArrayList<RecurringRuleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RecurringRuleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
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
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
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
            final RecurrenceFrequency _tmpFrequency;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfFrequency)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfFrequency);
            }
            final RecurrenceFrequency _tmp_5 = __roomTypeConverters.toRecurrenceFrequency(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.RecurrenceFrequency', but it was NULL.");
            } else {
              _tmpFrequency = _tmp_5;
            }
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final long _tmpNextDueDate;
            _tmpNextDueDate = _cursor.getLong(_cursorIndexOfNextDueDate);
            final boolean _tmpAutoApply;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfAutoApply);
            _tmpAutoApply = _tmp_6 != 0;
            final int _tmpReminderDaysBefore;
            _tmpReminderDaysBefore = _cursor.getInt(_cursorIndexOfReminderDaysBefore);
            _item = new RecurringRuleEntity(_tmpId,_tmpTitle,_tmpType,_tmpAmountMinor,_tmpCurrency,_tmpAccountId,_tmpToAccountId,_tmpCategoryId,_tmpPaymentMethod,_tmpFrequency,_tmpInterval,_tmpNextDueDate,_tmpAutoApply,_tmpReminderDaysBefore);
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
  public Object getDueRecurringRules(final long nowTimestamp,
      final Continuation<? super List<RecurringRuleEntity>> $completion) {
    final String _sql = "SELECT * FROM recurring_rules WHERE next_due_date <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, nowTimestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RecurringRuleEntity>>() {
      @Override
      @NonNull
      public List<RecurringRuleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfToAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "to_account_id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "payment_method");
          final int _cursorIndexOfFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "frequency");
          final int _cursorIndexOfInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "interval");
          final int _cursorIndexOfNextDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "next_due_date");
          final int _cursorIndexOfAutoApply = CursorUtil.getColumnIndexOrThrow(_cursor, "auto_apply");
          final int _cursorIndexOfReminderDaysBefore = CursorUtil.getColumnIndexOrThrow(_cursor, "reminder_days_before");
          final List<RecurringRuleEntity> _result = new ArrayList<RecurringRuleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RecurringRuleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
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
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
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
            final RecurrenceFrequency _tmpFrequency;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfFrequency)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfFrequency);
            }
            final RecurrenceFrequency _tmp_5 = __roomTypeConverters.toRecurrenceFrequency(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.RecurrenceFrequency', but it was NULL.");
            } else {
              _tmpFrequency = _tmp_5;
            }
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final long _tmpNextDueDate;
            _tmpNextDueDate = _cursor.getLong(_cursorIndexOfNextDueDate);
            final boolean _tmpAutoApply;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfAutoApply);
            _tmpAutoApply = _tmp_6 != 0;
            final int _tmpReminderDaysBefore;
            _tmpReminderDaysBefore = _cursor.getInt(_cursorIndexOfReminderDaysBefore);
            _item = new RecurringRuleEntity(_tmpId,_tmpTitle,_tmpType,_tmpAmountMinor,_tmpCurrency,_tmpAccountId,_tmpToAccountId,_tmpCategoryId,_tmpPaymentMethod,_tmpFrequency,_tmpInterval,_tmpNextDueDate,_tmpAutoApply,_tmpReminderDaysBefore);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
