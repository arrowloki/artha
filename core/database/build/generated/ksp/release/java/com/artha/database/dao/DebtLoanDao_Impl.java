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
import com.artha.database.entity.DebtLoanEntity;
import com.artha.database.entity.DebtPaymentEntity;
import com.artha.model.DebtType;
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
public final class DebtLoanDao_Impl implements DebtLoanDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DebtLoanEntity> __insertionAdapterOfDebtLoanEntity;

  private final RoomTypeConverters __roomTypeConverters = new RoomTypeConverters();

  private final EntityInsertionAdapter<DebtPaymentEntity> __insertionAdapterOfDebtPaymentEntity;

  private final EntityDeletionOrUpdateAdapter<DebtLoanEntity> __updateAdapterOfDebtLoanEntity;

  private final SharedSQLiteStatement __preparedStmtOfReduceRemainingDebt;

  public DebtLoanDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDebtLoanEntity = new EntityInsertionAdapter<DebtLoanEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `debts_loans` (`id`,`person_or_institution`,`type`,`principal_minor`,`remaining_minor`,`interest_rate_apr`,`tenure_months`,`emi_amount_minor`,`emi_due_day`,`start_date`,`end_date`,`notes`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtLoanEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getPersonOrInstitution());
        final String _tmp = __roomTypeConverters.fromDebtType(entity.getType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        statement.bindLong(4, entity.getPrincipalMinor());
        statement.bindLong(5, entity.getRemainingMinor());
        statement.bindDouble(6, entity.getInterestRateApr());
        statement.bindLong(7, entity.getTenureMonths());
        statement.bindLong(8, entity.getEmiAmountMinor());
        statement.bindLong(9, entity.getEmiDueDay());
        statement.bindLong(10, entity.getStartDate());
        if (entity.getEndDate() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getEndDate());
        }
        statement.bindString(12, entity.getNotes());
      }
    };
    this.__insertionAdapterOfDebtPaymentEntity = new EntityInsertionAdapter<DebtPaymentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `debt_payments` (`id`,`debt_id`,`transaction_id`,`amount_minor`,`principal_component_minor`,`interest_component_minor`,`payment_date`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtPaymentEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDebtId());
        if (entity.getTransactionId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getTransactionId());
        }
        statement.bindLong(4, entity.getAmountMinor());
        statement.bindLong(5, entity.getPrincipalComponentMinor());
        statement.bindLong(6, entity.getInterestComponentMinor());
        statement.bindLong(7, entity.getPaymentDate());
      }
    };
    this.__updateAdapterOfDebtLoanEntity = new EntityDeletionOrUpdateAdapter<DebtLoanEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `debts_loans` SET `id` = ?,`person_or_institution` = ?,`type` = ?,`principal_minor` = ?,`remaining_minor` = ?,`interest_rate_apr` = ?,`tenure_months` = ?,`emi_amount_minor` = ?,`emi_due_day` = ?,`start_date` = ?,`end_date` = ?,`notes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtLoanEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getPersonOrInstitution());
        final String _tmp = __roomTypeConverters.fromDebtType(entity.getType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        statement.bindLong(4, entity.getPrincipalMinor());
        statement.bindLong(5, entity.getRemainingMinor());
        statement.bindDouble(6, entity.getInterestRateApr());
        statement.bindLong(7, entity.getTenureMonths());
        statement.bindLong(8, entity.getEmiAmountMinor());
        statement.bindLong(9, entity.getEmiDueDay());
        statement.bindLong(10, entity.getStartDate());
        if (entity.getEndDate() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getEndDate());
        }
        statement.bindString(12, entity.getNotes());
        statement.bindString(13, entity.getId());
      }
    };
    this.__preparedStmtOfReduceRemainingDebt = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE debts_loans SET remaining_minor = remaining_minor - ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertDebt(final DebtLoanEntity debt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDebtLoanEntity.insert(debt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPayment(final DebtPaymentEntity payment,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDebtPaymentEntity.insert(payment);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDebt(final DebtLoanEntity debt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDebtLoanEntity.handle(debt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object recordDebtPayment(final DebtPaymentEntity payment,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> DebtLoanDao.DefaultImpls.recordDebtPayment(DebtLoanDao_Impl.this, payment, __cont), $completion);
  }

  @Override
  public Object reduceRemainingDebt(final String id, final long principalReduction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfReduceRemainingDebt.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, principalReduction);
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
          __preparedStmtOfReduceRemainingDebt.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DebtLoanEntity>> getAllDebtsFlow() {
    final String _sql = "SELECT `debts_loans`.`id` AS `id`, `debts_loans`.`person_or_institution` AS `person_or_institution`, `debts_loans`.`type` AS `type`, `debts_loans`.`principal_minor` AS `principal_minor`, `debts_loans`.`remaining_minor` AS `remaining_minor`, `debts_loans`.`interest_rate_apr` AS `interest_rate_apr`, `debts_loans`.`tenure_months` AS `tenure_months`, `debts_loans`.`emi_amount_minor` AS `emi_amount_minor`, `debts_loans`.`emi_due_day` AS `emi_due_day`, `debts_loans`.`start_date` AS `start_date`, `debts_loans`.`end_date` AS `end_date`, `debts_loans`.`notes` AS `notes` FROM debts_loans ORDER BY start_date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"debts_loans"}, new Callable<List<DebtLoanEntity>>() {
      @Override
      @NonNull
      public List<DebtLoanEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfPersonOrInstitution = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfPrincipalMinor = 3;
          final int _cursorIndexOfRemainingMinor = 4;
          final int _cursorIndexOfInterestRateApr = 5;
          final int _cursorIndexOfTenureMonths = 6;
          final int _cursorIndexOfEmiAmountMinor = 7;
          final int _cursorIndexOfEmiDueDay = 8;
          final int _cursorIndexOfStartDate = 9;
          final int _cursorIndexOfEndDate = 10;
          final int _cursorIndexOfNotes = 11;
          final List<DebtLoanEntity> _result = new ArrayList<DebtLoanEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DebtLoanEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPersonOrInstitution;
            _tmpPersonOrInstitution = _cursor.getString(_cursorIndexOfPersonOrInstitution);
            final DebtType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            final DebtType _tmp_1 = __roomTypeConverters.toDebtType(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.DebtType', but it was NULL.");
            } else {
              _tmpType = _tmp_1;
            }
            final long _tmpPrincipalMinor;
            _tmpPrincipalMinor = _cursor.getLong(_cursorIndexOfPrincipalMinor);
            final long _tmpRemainingMinor;
            _tmpRemainingMinor = _cursor.getLong(_cursorIndexOfRemainingMinor);
            final double _tmpInterestRateApr;
            _tmpInterestRateApr = _cursor.getDouble(_cursorIndexOfInterestRateApr);
            final int _tmpTenureMonths;
            _tmpTenureMonths = _cursor.getInt(_cursorIndexOfTenureMonths);
            final long _tmpEmiAmountMinor;
            _tmpEmiAmountMinor = _cursor.getLong(_cursorIndexOfEmiAmountMinor);
            final int _tmpEmiDueDay;
            _tmpEmiDueDay = _cursor.getInt(_cursorIndexOfEmiDueDay);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final Long _tmpEndDate;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            }
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _item = new DebtLoanEntity(_tmpId,_tmpPersonOrInstitution,_tmpType,_tmpPrincipalMinor,_tmpRemainingMinor,_tmpInterestRateApr,_tmpTenureMonths,_tmpEmiAmountMinor,_tmpEmiDueDay,_tmpStartDate,_tmpEndDate,_tmpNotes);
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
  public Object getDebtById(final String id,
      final Continuation<? super DebtLoanEntity> $completion) {
    final String _sql = "SELECT * FROM debts_loans WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DebtLoanEntity>() {
      @Override
      @Nullable
      public DebtLoanEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPersonOrInstitution = CursorUtil.getColumnIndexOrThrow(_cursor, "person_or_institution");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPrincipalMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "principal_minor");
          final int _cursorIndexOfRemainingMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "remaining_minor");
          final int _cursorIndexOfInterestRateApr = CursorUtil.getColumnIndexOrThrow(_cursor, "interest_rate_apr");
          final int _cursorIndexOfTenureMonths = CursorUtil.getColumnIndexOrThrow(_cursor, "tenure_months");
          final int _cursorIndexOfEmiAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "emi_amount_minor");
          final int _cursorIndexOfEmiDueDay = CursorUtil.getColumnIndexOrThrow(_cursor, "emi_due_day");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "start_date");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "end_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final DebtLoanEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPersonOrInstitution;
            _tmpPersonOrInstitution = _cursor.getString(_cursorIndexOfPersonOrInstitution);
            final DebtType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            final DebtType _tmp_1 = __roomTypeConverters.toDebtType(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.artha.model.DebtType', but it was NULL.");
            } else {
              _tmpType = _tmp_1;
            }
            final long _tmpPrincipalMinor;
            _tmpPrincipalMinor = _cursor.getLong(_cursorIndexOfPrincipalMinor);
            final long _tmpRemainingMinor;
            _tmpRemainingMinor = _cursor.getLong(_cursorIndexOfRemainingMinor);
            final double _tmpInterestRateApr;
            _tmpInterestRateApr = _cursor.getDouble(_cursorIndexOfInterestRateApr);
            final int _tmpTenureMonths;
            _tmpTenureMonths = _cursor.getInt(_cursorIndexOfTenureMonths);
            final long _tmpEmiAmountMinor;
            _tmpEmiAmountMinor = _cursor.getLong(_cursorIndexOfEmiAmountMinor);
            final int _tmpEmiDueDay;
            _tmpEmiDueDay = _cursor.getInt(_cursorIndexOfEmiDueDay);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final Long _tmpEndDate;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            }
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _result = new DebtLoanEntity(_tmpId,_tmpPersonOrInstitution,_tmpType,_tmpPrincipalMinor,_tmpRemainingMinor,_tmpInterestRateApr,_tmpTenureMonths,_tmpEmiAmountMinor,_tmpEmiDueDay,_tmpStartDate,_tmpEndDate,_tmpNotes);
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
  public Flow<List<DebtPaymentEntity>> getPaymentsForDebtFlow(final String debtId) {
    final String _sql = "SELECT * FROM debt_payments WHERE debt_id = ? ORDER BY payment_date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, debtId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"debt_payments"}, new Callable<List<DebtPaymentEntity>>() {
      @Override
      @NonNull
      public List<DebtPaymentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDebtId = CursorUtil.getColumnIndexOrThrow(_cursor, "debt_id");
          final int _cursorIndexOfTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "transaction_id");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_minor");
          final int _cursorIndexOfPrincipalComponentMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "principal_component_minor");
          final int _cursorIndexOfInterestComponentMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "interest_component_minor");
          final int _cursorIndexOfPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "payment_date");
          final List<DebtPaymentEntity> _result = new ArrayList<DebtPaymentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DebtPaymentEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDebtId;
            _tmpDebtId = _cursor.getString(_cursorIndexOfDebtId);
            final String _tmpTransactionId;
            if (_cursor.isNull(_cursorIndexOfTransactionId)) {
              _tmpTransactionId = null;
            } else {
              _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId);
            }
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final long _tmpPrincipalComponentMinor;
            _tmpPrincipalComponentMinor = _cursor.getLong(_cursorIndexOfPrincipalComponentMinor);
            final long _tmpInterestComponentMinor;
            _tmpInterestComponentMinor = _cursor.getLong(_cursorIndexOfInterestComponentMinor);
            final long _tmpPaymentDate;
            _tmpPaymentDate = _cursor.getLong(_cursorIndexOfPaymentDate);
            _item = new DebtPaymentEntity(_tmpId,_tmpDebtId,_tmpTransactionId,_tmpAmountMinor,_tmpPrincipalComponentMinor,_tmpInterestComponentMinor,_tmpPaymentDate);
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
