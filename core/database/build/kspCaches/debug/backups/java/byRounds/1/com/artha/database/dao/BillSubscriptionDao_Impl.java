package com.artha.database.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.artha.database.converter.RoomTypeConverters;
import com.artha.database.entity.BillSubscriptionEntity;
import com.artha.model.RecurrenceFrequency;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Long;
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
public final class BillSubscriptionDao_Impl implements BillSubscriptionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BillSubscriptionEntity> __insertionAdapterOfBillSubscriptionEntity;

  private final RoomTypeConverters __roomTypeConverters = new RoomTypeConverters();

  private final EntityDeletionOrUpdateAdapter<BillSubscriptionEntity> __updateAdapterOfBillSubscriptionEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkBillPaid;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBill;

  public BillSubscriptionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBillSubscriptionEntity = new EntityInsertionAdapter<BillSubscriptionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `bills_subscriptions` (`id`,`name`,`amount_minor`,`currency`,`category_id`,`account_id`,`frequency`,`due_day`,`trial_end_date`,`cancellation_url`,`is_auto_paid`,`is_active`,`last_paid_date`,`next_due_date`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BillSubscriptionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getAmountMinor());
        statement.bindString(4, entity.getCurrency());
        if (entity.getCategoryId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCategoryId());
        }
        statement.bindString(6, entity.getAccountId());
        final String _tmp = __roomTypeConverters.fromRecurrenceFrequency(entity.getFrequency());
        if (_tmp == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp);
        }
        statement.bindLong(8, entity.getDueDay());
        if (entity.getTrialEndDate() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getTrialEndDate());
        }
        if (entity.getCancellationUrl() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getCancellationUrl());
        }
        final int _tmp_1 = entity.isAutoPaid() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
        final int _tmp_2 = entity.isActive() ? 1 : 0;
        statement.bindLong(12, _tmp_2);
        if (entity.getLastPaidDate() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getLastPaidDate());
        }
        statement.bindLong(14, entity.getNextDueDate());
      }
    };
    this.__updateAdapterOfBillSubscriptionEntity = new EntityDeletionOrUpdateAdapter<BillSubscriptionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `bills_subscriptions` SET `id` = ?,`name` = ?,`amount_minor` = ?,`currency` = ?,`category_id` = ?,`account_id` = ?,`frequency` = ?,`due_day` = ?,`trial_end_date` = ?,`cancellation_url` = ?,`is_auto_paid` = ?,`is_active` = ?,`last_paid_date` = ?,`next_due_date` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BillSubscriptionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getAmountMinor());
        statement.bindString(4, entity.getCurrency());
        if (entity.getCategoryId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCategoryId());
        }
        statement.bindString(6, entity.getAccountId());
        final String _tmp = __roomTypeConverters.fromRecurrenceFrequency(entity.getFrequency());
        if (_tmp == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp);
        }
        statement.bindLong(8, entity.getDueDay());
        if (entity.getTrialEndDate() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getTrialEndDate());
        }
        if (entity.getCancellationUrl() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getCancellationUrl());
        }
        final int _tmp_1 = entity.isAutoPaid() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
        final int _tmp_2 = entity.isActive() ? 1 : 0;
        statement.bindLong(12, _tmp_2);
        if (entity.getLastPaidDate() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getLastPaidDate());
        }
        statement.bindLong(14, entity.getNextDueDate());
        statement.bindString(15, entity.getId());
      }
    };
    this.__preparedStmtOfMarkBillPaid = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE bills_subscriptions SET last_paid_date = ?, next_due_date = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteBill = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bills_subscriptions WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertBill(final BillSubscriptionEntity bill,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBillSubscriptionEntity.insert(bill);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBill(final BillSubscriptionEntity bill,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBillSubscriptionEntity.handle(bill);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markBillPaid(final String id, final long paidDate, final long nextDueDate,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkBillPaid.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, paidDate);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, nextDueDate);
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
          __preparedStmtOfMarkBillPaid.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBill(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBill.acquire();
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
          __preparedStmtOfDeleteBill.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BillSubscriptionEntity>> getActiveBillsFlow() {
    final String _sql = "SELECT `bills_subscriptions`.`id` AS `id`, `bills_subscriptions`.`name` AS `name`, `bills_subscriptions`.`amount_minor` AS `amount_minor`, `bills_subscriptions`.`currency` AS `currency`, `bills_subscriptions`.`category_id` AS `category_id`, `bills_subscriptions`.`account_id` AS `account_id`, `bills_subscriptions`.`frequency` AS `frequency`, `bills_subscriptions`.`due_day` AS `due_day`, `bills_subscriptions`.`trial_end_date` AS `trial_end_date`, `bills_subscriptions`.`cancellation_url` AS `cancellation_url`, `bills_subscriptions`.`is_auto_paid` AS `is_auto_paid`, `bills_subscriptions`.`is_active` AS `is_active`, `bills_subscriptions`.`last_paid_date` AS `last_paid_date`, `bills_subscriptions`.`next_due_date` AS `next_due_date` FROM bills_subscriptions WHERE is_active = 1 ORDER BY next_due_date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bills_subscriptions"}, new Callable<List<BillSubscriptionEntity>>() {
      @Override
      @NonNull
      public List<BillSubscriptionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfAmountMinor = 2;
          final int _cursorIndexOfCurrency = 3;
          final int _cursorIndexOfCategoryId = 4;
          final int _cursorIndexOfAccountId = 5;
          final int _cursorIndexOfFrequency = 6;
          final int _cursorIndexOfDueDay = 7;
          final int _cursorIndexOfTrialEndDate = 8;
          final int _cursorIndexOfCancellationUrl = 9;
          final int _cursorIndexOfIsAutoPaid = 10;
          final int _cursorIndexOfIsActive = 11;
          final int _cursorIndexOfLastPaidDate = 12;
          final int _cursorIndexOfNextDueDate = 13;
          final List<BillSubscriptionEntity> _result = new ArrayList<BillSubscriptionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BillSubscriptionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final String _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId);
            }
            final String _tmpAccountId;
            _tmpAccountId = _cursor.getString(_cursorIndexOfAccountId);
            final RecurrenceFrequency _tmpFrequency;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfFrequency)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFrequency);
            }
            final RecurrenceFrequency _tmp_1 = __roomTypeConverters.toRecurrenceFrequency(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.RecurrenceFrequency', but it was NULL.");
            } else {
              _tmpFrequency = _tmp_1;
            }
            final int _tmpDueDay;
            _tmpDueDay = _cursor.getInt(_cursorIndexOfDueDay);
            final Long _tmpTrialEndDate;
            if (_cursor.isNull(_cursorIndexOfTrialEndDate)) {
              _tmpTrialEndDate = null;
            } else {
              _tmpTrialEndDate = _cursor.getLong(_cursorIndexOfTrialEndDate);
            }
            final String _tmpCancellationUrl;
            if (_cursor.isNull(_cursorIndexOfCancellationUrl)) {
              _tmpCancellationUrl = null;
            } else {
              _tmpCancellationUrl = _cursor.getString(_cursorIndexOfCancellationUrl);
            }
            final boolean _tmpIsAutoPaid;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsAutoPaid);
            _tmpIsAutoPaid = _tmp_2 != 0;
            final boolean _tmpIsActive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_3 != 0;
            final Long _tmpLastPaidDate;
            if (_cursor.isNull(_cursorIndexOfLastPaidDate)) {
              _tmpLastPaidDate = null;
            } else {
              _tmpLastPaidDate = _cursor.getLong(_cursorIndexOfLastPaidDate);
            }
            final long _tmpNextDueDate;
            _tmpNextDueDate = _cursor.getLong(_cursorIndexOfNextDueDate);
            _item = new BillSubscriptionEntity(_tmpId,_tmpName,_tmpAmountMinor,_tmpCurrency,_tmpCategoryId,_tmpAccountId,_tmpFrequency,_tmpDueDay,_tmpTrialEndDate,_tmpCancellationUrl,_tmpIsAutoPaid,_tmpIsActive,_tmpLastPaidDate,_tmpNextDueDate);
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
  public Flow<List<BillSubscriptionEntity>> getAllBillsFlow() {
    final String _sql = "SELECT `bills_subscriptions`.`id` AS `id`, `bills_subscriptions`.`name` AS `name`, `bills_subscriptions`.`amount_minor` AS `amount_minor`, `bills_subscriptions`.`currency` AS `currency`, `bills_subscriptions`.`category_id` AS `category_id`, `bills_subscriptions`.`account_id` AS `account_id`, `bills_subscriptions`.`frequency` AS `frequency`, `bills_subscriptions`.`due_day` AS `due_day`, `bills_subscriptions`.`trial_end_date` AS `trial_end_date`, `bills_subscriptions`.`cancellation_url` AS `cancellation_url`, `bills_subscriptions`.`is_auto_paid` AS `is_auto_paid`, `bills_subscriptions`.`is_active` AS `is_active`, `bills_subscriptions`.`last_paid_date` AS `last_paid_date`, `bills_subscriptions`.`next_due_date` AS `next_due_date` FROM bills_subscriptions ORDER BY next_due_date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bills_subscriptions"}, new Callable<List<BillSubscriptionEntity>>() {
      @Override
      @NonNull
      public List<BillSubscriptionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfAmountMinor = 2;
          final int _cursorIndexOfCurrency = 3;
          final int _cursorIndexOfCategoryId = 4;
          final int _cursorIndexOfAccountId = 5;
          final int _cursorIndexOfFrequency = 6;
          final int _cursorIndexOfDueDay = 7;
          final int _cursorIndexOfTrialEndDate = 8;
          final int _cursorIndexOfCancellationUrl = 9;
          final int _cursorIndexOfIsAutoPaid = 10;
          final int _cursorIndexOfIsActive = 11;
          final int _cursorIndexOfLastPaidDate = 12;
          final int _cursorIndexOfNextDueDate = 13;
          final List<BillSubscriptionEntity> _result = new ArrayList<BillSubscriptionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BillSubscriptionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final String _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId);
            }
            final String _tmpAccountId;
            _tmpAccountId = _cursor.getString(_cursorIndexOfAccountId);
            final RecurrenceFrequency _tmpFrequency;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfFrequency)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFrequency);
            }
            final RecurrenceFrequency _tmp_1 = __roomTypeConverters.toRecurrenceFrequency(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.RecurrenceFrequency', but it was NULL.");
            } else {
              _tmpFrequency = _tmp_1;
            }
            final int _tmpDueDay;
            _tmpDueDay = _cursor.getInt(_cursorIndexOfDueDay);
            final Long _tmpTrialEndDate;
            if (_cursor.isNull(_cursorIndexOfTrialEndDate)) {
              _tmpTrialEndDate = null;
            } else {
              _tmpTrialEndDate = _cursor.getLong(_cursorIndexOfTrialEndDate);
            }
            final String _tmpCancellationUrl;
            if (_cursor.isNull(_cursorIndexOfCancellationUrl)) {
              _tmpCancellationUrl = null;
            } else {
              _tmpCancellationUrl = _cursor.getString(_cursorIndexOfCancellationUrl);
            }
            final boolean _tmpIsAutoPaid;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsAutoPaid);
            _tmpIsAutoPaid = _tmp_2 != 0;
            final boolean _tmpIsActive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_3 != 0;
            final Long _tmpLastPaidDate;
            if (_cursor.isNull(_cursorIndexOfLastPaidDate)) {
              _tmpLastPaidDate = null;
            } else {
              _tmpLastPaidDate = _cursor.getLong(_cursorIndexOfLastPaidDate);
            }
            final long _tmpNextDueDate;
            _tmpNextDueDate = _cursor.getLong(_cursorIndexOfNextDueDate);
            _item = new BillSubscriptionEntity(_tmpId,_tmpName,_tmpAmountMinor,_tmpCurrency,_tmpCategoryId,_tmpAccountId,_tmpFrequency,_tmpDueDay,_tmpTrialEndDate,_tmpCancellationUrl,_tmpIsAutoPaid,_tmpIsActive,_tmpLastPaidDate,_tmpNextDueDate);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
