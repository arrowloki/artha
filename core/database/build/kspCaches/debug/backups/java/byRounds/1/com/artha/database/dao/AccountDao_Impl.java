package com.artha.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.artha.database.entity.AccountEntity;
import com.artha.model.AccountType;
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
public final class AccountDao_Impl implements AccountDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AccountEntity> __insertionAdapterOfAccountEntity;

  private final RoomTypeConverters __roomTypeConverters = new RoomTypeConverters();

  private final EntityDeletionOrUpdateAdapter<AccountEntity> __deletionAdapterOfAccountEntity;

  private final EntityDeletionOrUpdateAdapter<AccountEntity> __updateAdapterOfAccountEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAccountBalance;

  public AccountDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAccountEntity = new EntityInsertionAdapter<AccountEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `accounts` (`id`,`name`,`type`,`currency`,`opening_balance_minor`,`current_balance_minor`,`credit_limit_minor`,`billing_day`,`due_day`,`grace_period_days`,`apr_percentage`,`is_archived`,`color_hex`,`icon_name`,`sort_order`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AccountEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        final String _tmp = __roomTypeConverters.fromAccountType(entity.getType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        statement.bindString(4, entity.getCurrency());
        statement.bindLong(5, entity.getOpeningBalanceMinor());
        statement.bindLong(6, entity.getCurrentBalanceMinor());
        statement.bindLong(7, entity.getCreditLimitMinor());
        statement.bindLong(8, entity.getBillingDay());
        statement.bindLong(9, entity.getDueDay());
        statement.bindLong(10, entity.getGracePeriodDays());
        statement.bindDouble(11, entity.getAprPercentage());
        final int _tmp_1 = entity.isArchived() ? 1 : 0;
        statement.bindLong(12, _tmp_1);
        statement.bindString(13, entity.getColorHex());
        statement.bindString(14, entity.getIconName());
        statement.bindLong(15, entity.getSortOrder());
        statement.bindLong(16, entity.getCreatedAt());
        statement.bindLong(17, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfAccountEntity = new EntityDeletionOrUpdateAdapter<AccountEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `accounts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AccountEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfAccountEntity = new EntityDeletionOrUpdateAdapter<AccountEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `accounts` SET `id` = ?,`name` = ?,`type` = ?,`currency` = ?,`opening_balance_minor` = ?,`current_balance_minor` = ?,`credit_limit_minor` = ?,`billing_day` = ?,`due_day` = ?,`grace_period_days` = ?,`apr_percentage` = ?,`is_archived` = ?,`color_hex` = ?,`icon_name` = ?,`sort_order` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AccountEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        final String _tmp = __roomTypeConverters.fromAccountType(entity.getType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        statement.bindString(4, entity.getCurrency());
        statement.bindLong(5, entity.getOpeningBalanceMinor());
        statement.bindLong(6, entity.getCurrentBalanceMinor());
        statement.bindLong(7, entity.getCreditLimitMinor());
        statement.bindLong(8, entity.getBillingDay());
        statement.bindLong(9, entity.getDueDay());
        statement.bindLong(10, entity.getGracePeriodDays());
        statement.bindDouble(11, entity.getAprPercentage());
        final int _tmp_1 = entity.isArchived() ? 1 : 0;
        statement.bindLong(12, _tmp_1);
        statement.bindString(13, entity.getColorHex());
        statement.bindString(14, entity.getIconName());
        statement.bindLong(15, entity.getSortOrder());
        statement.bindLong(16, entity.getCreatedAt());
        statement.bindLong(17, entity.getUpdatedAt());
        statement.bindString(18, entity.getId());
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
  public Object insertAccount(final AccountEntity account,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAccountEntity.insert(account);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAccounts(final List<AccountEntity> accounts,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAccountEntity.insert(accounts);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAccount(final AccountEntity account,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAccountEntity.handle(account);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAccount(final AccountEntity account,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAccountEntity.handle(account);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
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
  public Flow<List<AccountEntity>> getActiveAccountsFlow() {
    final String _sql = "SELECT `accounts`.`id` AS `id`, `accounts`.`name` AS `name`, `accounts`.`type` AS `type`, `accounts`.`currency` AS `currency`, `accounts`.`opening_balance_minor` AS `opening_balance_minor`, `accounts`.`current_balance_minor` AS `current_balance_minor`, `accounts`.`credit_limit_minor` AS `credit_limit_minor`, `accounts`.`billing_day` AS `billing_day`, `accounts`.`due_day` AS `due_day`, `accounts`.`grace_period_days` AS `grace_period_days`, `accounts`.`apr_percentage` AS `apr_percentage`, `accounts`.`is_archived` AS `is_archived`, `accounts`.`color_hex` AS `color_hex`, `accounts`.`icon_name` AS `icon_name`, `accounts`.`sort_order` AS `sort_order`, `accounts`.`created_at` AS `created_at`, `accounts`.`updated_at` AS `updated_at` FROM accounts WHERE is_archived = 0 ORDER BY sort_order ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"accounts"}, new Callable<List<AccountEntity>>() {
      @Override
      @NonNull
      public List<AccountEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfCurrency = 3;
          final int _cursorIndexOfOpeningBalanceMinor = 4;
          final int _cursorIndexOfCurrentBalanceMinor = 5;
          final int _cursorIndexOfCreditLimitMinor = 6;
          final int _cursorIndexOfBillingDay = 7;
          final int _cursorIndexOfDueDay = 8;
          final int _cursorIndexOfGracePeriodDays = 9;
          final int _cursorIndexOfAprPercentage = 10;
          final int _cursorIndexOfIsArchived = 11;
          final int _cursorIndexOfColorHex = 12;
          final int _cursorIndexOfIconName = 13;
          final int _cursorIndexOfSortOrder = 14;
          final int _cursorIndexOfCreatedAt = 15;
          final int _cursorIndexOfUpdatedAt = 16;
          final List<AccountEntity> _result = new ArrayList<AccountEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AccountEntity _item;
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
            _item = new AccountEntity(_tmpId,_tmpName,_tmpType,_tmpCurrency,_tmpOpeningBalanceMinor,_tmpCurrentBalanceMinor,_tmpCreditLimitMinor,_tmpBillingDay,_tmpDueDay,_tmpGracePeriodDays,_tmpAprPercentage,_tmpIsArchived,_tmpColorHex,_tmpIconName,_tmpSortOrder,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<AccountEntity>> getAllAccountsFlow() {
    final String _sql = "SELECT `accounts`.`id` AS `id`, `accounts`.`name` AS `name`, `accounts`.`type` AS `type`, `accounts`.`currency` AS `currency`, `accounts`.`opening_balance_minor` AS `opening_balance_minor`, `accounts`.`current_balance_minor` AS `current_balance_minor`, `accounts`.`credit_limit_minor` AS `credit_limit_minor`, `accounts`.`billing_day` AS `billing_day`, `accounts`.`due_day` AS `due_day`, `accounts`.`grace_period_days` AS `grace_period_days`, `accounts`.`apr_percentage` AS `apr_percentage`, `accounts`.`is_archived` AS `is_archived`, `accounts`.`color_hex` AS `color_hex`, `accounts`.`icon_name` AS `icon_name`, `accounts`.`sort_order` AS `sort_order`, `accounts`.`created_at` AS `created_at`, `accounts`.`updated_at` AS `updated_at` FROM accounts ORDER BY sort_order ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"accounts"}, new Callable<List<AccountEntity>>() {
      @Override
      @NonNull
      public List<AccountEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfCurrency = 3;
          final int _cursorIndexOfOpeningBalanceMinor = 4;
          final int _cursorIndexOfCurrentBalanceMinor = 5;
          final int _cursorIndexOfCreditLimitMinor = 6;
          final int _cursorIndexOfBillingDay = 7;
          final int _cursorIndexOfDueDay = 8;
          final int _cursorIndexOfGracePeriodDays = 9;
          final int _cursorIndexOfAprPercentage = 10;
          final int _cursorIndexOfIsArchived = 11;
          final int _cursorIndexOfColorHex = 12;
          final int _cursorIndexOfIconName = 13;
          final int _cursorIndexOfSortOrder = 14;
          final int _cursorIndexOfCreatedAt = 15;
          final int _cursorIndexOfUpdatedAt = 16;
          final List<AccountEntity> _result = new ArrayList<AccountEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AccountEntity _item;
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
            _item = new AccountEntity(_tmpId,_tmpName,_tmpType,_tmpCurrency,_tmpOpeningBalanceMinor,_tmpCurrentBalanceMinor,_tmpCreditLimitMinor,_tmpBillingDay,_tmpDueDay,_tmpGracePeriodDays,_tmpAprPercentage,_tmpIsArchived,_tmpColorHex,_tmpIconName,_tmpSortOrder,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getAccountById(final String id,
      final Continuation<? super AccountEntity> $completion) {
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
  public Flow<AccountEntity> getAccountFlow(final String id) {
    final String _sql = "SELECT * FROM accounts WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"accounts"}, new Callable<AccountEntity>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTotalBalance(final String currency,
      final Continuation<? super Long> $completion) {
    final String _sql = "SELECT SUM(current_balance_minor) FROM accounts WHERE is_archived = 0 AND currency = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, currency);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
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
