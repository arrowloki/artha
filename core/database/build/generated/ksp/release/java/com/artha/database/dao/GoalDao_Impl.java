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
import com.artha.database.entity.GoalEntity;
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
public final class GoalDao_Impl implements GoalDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GoalEntity> __insertionAdapterOfGoalEntity;

  private final EntityDeletionOrUpdateAdapter<GoalEntity> __updateAdapterOfGoalEntity;

  private final SharedSQLiteStatement __preparedStmtOfContributeToGoal;

  private final SharedSQLiteStatement __preparedStmtOfDeleteGoal;

  public GoalDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGoalEntity = new EntityInsertionAdapter<GoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `goals` (`id`,`name`,`target_amount_minor`,`saved_amount_minor`,`currency`,`target_date`,`account_id`,`icon_name`,`color_hex`,`is_reached`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GoalEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getTargetAmountMinor());
        statement.bindLong(4, entity.getSavedAmountMinor());
        statement.bindString(5, entity.getCurrency());
        statement.bindLong(6, entity.getTargetDate());
        if (entity.getAccountId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getAccountId());
        }
        statement.bindString(8, entity.getIconName());
        statement.bindString(9, entity.getColorHex());
        final int _tmp = entity.isReached() ? 1 : 0;
        statement.bindLong(10, _tmp);
      }
    };
    this.__updateAdapterOfGoalEntity = new EntityDeletionOrUpdateAdapter<GoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `goals` SET `id` = ?,`name` = ?,`target_amount_minor` = ?,`saved_amount_minor` = ?,`currency` = ?,`target_date` = ?,`account_id` = ?,`icon_name` = ?,`color_hex` = ?,`is_reached` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GoalEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getTargetAmountMinor());
        statement.bindLong(4, entity.getSavedAmountMinor());
        statement.bindString(5, entity.getCurrency());
        statement.bindLong(6, entity.getTargetDate());
        if (entity.getAccountId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getAccountId());
        }
        statement.bindString(8, entity.getIconName());
        statement.bindString(9, entity.getColorHex());
        final int _tmp = entity.isReached() ? 1 : 0;
        statement.bindLong(10, _tmp);
        statement.bindString(11, entity.getId());
      }
    };
    this.__preparedStmtOfContributeToGoal = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE goals SET saved_amount_minor = saved_amount_minor + ?, is_reached = (saved_amount_minor + ? >= target_amount_minor) WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteGoal = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM goals WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertGoal(final GoalEntity goal, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfGoalEntity.insert(goal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateGoal(final GoalEntity goal, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfGoalEntity.handle(goal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object contributeToGoal(final String id, final long deltaMinor,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfContributeToGoal.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, deltaMinor);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, deltaMinor);
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
          __preparedStmtOfContributeToGoal.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteGoal(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteGoal.acquire();
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
          __preparedStmtOfDeleteGoal.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<GoalEntity>> getAllGoalsFlow() {
    final String _sql = "SELECT `goals`.`id` AS `id`, `goals`.`name` AS `name`, `goals`.`target_amount_minor` AS `target_amount_minor`, `goals`.`saved_amount_minor` AS `saved_amount_minor`, `goals`.`currency` AS `currency`, `goals`.`target_date` AS `target_date`, `goals`.`account_id` AS `account_id`, `goals`.`icon_name` AS `icon_name`, `goals`.`color_hex` AS `color_hex`, `goals`.`is_reached` AS `is_reached` FROM goals ORDER BY is_reached ASC, target_date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"goals"}, new Callable<List<GoalEntity>>() {
      @Override
      @NonNull
      public List<GoalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfTargetAmountMinor = 2;
          final int _cursorIndexOfSavedAmountMinor = 3;
          final int _cursorIndexOfCurrency = 4;
          final int _cursorIndexOfTargetDate = 5;
          final int _cursorIndexOfAccountId = 6;
          final int _cursorIndexOfIconName = 7;
          final int _cursorIndexOfColorHex = 8;
          final int _cursorIndexOfIsReached = 9;
          final List<GoalEntity> _result = new ArrayList<GoalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GoalEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpTargetAmountMinor;
            _tmpTargetAmountMinor = _cursor.getLong(_cursorIndexOfTargetAmountMinor);
            final long _tmpSavedAmountMinor;
            _tmpSavedAmountMinor = _cursor.getLong(_cursorIndexOfSavedAmountMinor);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final long _tmpTargetDate;
            _tmpTargetDate = _cursor.getLong(_cursorIndexOfTargetDate);
            final String _tmpAccountId;
            if (_cursor.isNull(_cursorIndexOfAccountId)) {
              _tmpAccountId = null;
            } else {
              _tmpAccountId = _cursor.getString(_cursorIndexOfAccountId);
            }
            final String _tmpIconName;
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            final String _tmpColorHex;
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
            final boolean _tmpIsReached;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReached);
            _tmpIsReached = _tmp != 0;
            _item = new GoalEntity(_tmpId,_tmpName,_tmpTargetAmountMinor,_tmpSavedAmountMinor,_tmpCurrency,_tmpTargetDate,_tmpAccountId,_tmpIconName,_tmpColorHex,_tmpIsReached);
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
