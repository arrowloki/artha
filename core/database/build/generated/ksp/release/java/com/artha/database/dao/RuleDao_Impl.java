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
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.artha.database.entity.RuleEntity;
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
public final class RuleDao_Impl implements RuleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RuleEntity> __insertionAdapterOfRuleEntity;

  private final EntityDeletionOrUpdateAdapter<RuleEntity> __updateAdapterOfRuleEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRule;

  public RuleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRuleEntity = new EntityInsertionAdapter<RuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `rules` (`id`,`name`,`condition_field`,`condition_operator`,`condition_value`,`action_field`,`action_value`,`priority`,`is_active`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RuleEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getConditionField());
        statement.bindString(4, entity.getConditionOperator());
        statement.bindString(5, entity.getConditionValue());
        statement.bindString(6, entity.getActionField());
        statement.bindString(7, entity.getActionValue());
        statement.bindLong(8, entity.getPriority());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(9, _tmp);
      }
    };
    this.__updateAdapterOfRuleEntity = new EntityDeletionOrUpdateAdapter<RuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `rules` SET `id` = ?,`name` = ?,`condition_field` = ?,`condition_operator` = ?,`condition_value` = ?,`action_field` = ?,`action_value` = ?,`priority` = ?,`is_active` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RuleEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getConditionField());
        statement.bindString(4, entity.getConditionOperator());
        statement.bindString(5, entity.getConditionValue());
        statement.bindString(6, entity.getActionField());
        statement.bindString(7, entity.getActionValue());
        statement.bindLong(8, entity.getPriority());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindString(10, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteRule = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM rules WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertRule(final RuleEntity rule, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRuleEntity.insert(rule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRule(final RuleEntity rule, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRuleEntity.handle(rule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRule(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRule.acquire();
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
          __preparedStmtOfDeleteRule.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getActiveRules(final Continuation<? super List<RuleEntity>> $completion) {
    final String _sql = "SELECT `rules`.`id` AS `id`, `rules`.`name` AS `name`, `rules`.`condition_field` AS `condition_field`, `rules`.`condition_operator` AS `condition_operator`, `rules`.`condition_value` AS `condition_value`, `rules`.`action_field` AS `action_field`, `rules`.`action_value` AS `action_value`, `rules`.`priority` AS `priority`, `rules`.`is_active` AS `is_active` FROM rules WHERE is_active = 1 ORDER BY priority ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RuleEntity>>() {
      @Override
      @NonNull
      public List<RuleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfConditionField = 2;
          final int _cursorIndexOfConditionOperator = 3;
          final int _cursorIndexOfConditionValue = 4;
          final int _cursorIndexOfActionField = 5;
          final int _cursorIndexOfActionValue = 6;
          final int _cursorIndexOfPriority = 7;
          final int _cursorIndexOfIsActive = 8;
          final List<RuleEntity> _result = new ArrayList<RuleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RuleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpConditionField;
            _tmpConditionField = _cursor.getString(_cursorIndexOfConditionField);
            final String _tmpConditionOperator;
            _tmpConditionOperator = _cursor.getString(_cursorIndexOfConditionOperator);
            final String _tmpConditionValue;
            _tmpConditionValue = _cursor.getString(_cursorIndexOfConditionValue);
            final String _tmpActionField;
            _tmpActionField = _cursor.getString(_cursorIndexOfActionField);
            final String _tmpActionValue;
            _tmpActionValue = _cursor.getString(_cursorIndexOfActionValue);
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _item = new RuleEntity(_tmpId,_tmpName,_tmpConditionField,_tmpConditionOperator,_tmpConditionValue,_tmpActionField,_tmpActionValue,_tmpPriority,_tmpIsActive);
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
  public Flow<List<RuleEntity>> getAllRulesFlow() {
    final String _sql = "SELECT `rules`.`id` AS `id`, `rules`.`name` AS `name`, `rules`.`condition_field` AS `condition_field`, `rules`.`condition_operator` AS `condition_operator`, `rules`.`condition_value` AS `condition_value`, `rules`.`action_field` AS `action_field`, `rules`.`action_value` AS `action_value`, `rules`.`priority` AS `priority`, `rules`.`is_active` AS `is_active` FROM rules ORDER BY priority ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rules"}, new Callable<List<RuleEntity>>() {
      @Override
      @NonNull
      public List<RuleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfConditionField = 2;
          final int _cursorIndexOfConditionOperator = 3;
          final int _cursorIndexOfConditionValue = 4;
          final int _cursorIndexOfActionField = 5;
          final int _cursorIndexOfActionValue = 6;
          final int _cursorIndexOfPriority = 7;
          final int _cursorIndexOfIsActive = 8;
          final List<RuleEntity> _result = new ArrayList<RuleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RuleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpConditionField;
            _tmpConditionField = _cursor.getString(_cursorIndexOfConditionField);
            final String _tmpConditionOperator;
            _tmpConditionOperator = _cursor.getString(_cursorIndexOfConditionOperator);
            final String _tmpConditionValue;
            _tmpConditionValue = _cursor.getString(_cursorIndexOfConditionValue);
            final String _tmpActionField;
            _tmpActionField = _cursor.getString(_cursorIndexOfActionField);
            final String _tmpActionValue;
            _tmpActionValue = _cursor.getString(_cursorIndexOfActionValue);
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _item = new RuleEntity(_tmpId,_tmpName,_tmpConditionField,_tmpConditionOperator,_tmpConditionValue,_tmpActionField,_tmpActionValue,_tmpPriority,_tmpIsActive);
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
