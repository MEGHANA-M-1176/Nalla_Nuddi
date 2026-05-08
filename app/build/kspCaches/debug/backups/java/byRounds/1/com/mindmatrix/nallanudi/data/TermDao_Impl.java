package com.mindmatrix.nallanudi.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class TermDao_Impl implements TermDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Term> __insertionAdapterOfTerm;

  private final EntityDeletionOrUpdateAdapter<Term> __updateAdapterOfTerm;

  public TermDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTerm = new EntityInsertionAdapter<Term>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `terms` (`id`,`englishTerm`,`kannadaExplanation`,`example`,`subject`,`isSaved`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Term entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getEnglishTerm());
        statement.bindString(3, entity.getKannadaExplanation());
        statement.bindString(4, entity.getExample());
        statement.bindString(5, entity.getSubject());
        final int _tmp = entity.isSaved() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
    this.__updateAdapterOfTerm = new EntityDeletionOrUpdateAdapter<Term>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `terms` SET `id` = ?,`englishTerm` = ?,`kannadaExplanation` = ?,`example` = ?,`subject` = ?,`isSaved` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Term entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getEnglishTerm());
        statement.bindString(3, entity.getKannadaExplanation());
        statement.bindString(4, entity.getExample());
        statement.bindString(5, entity.getSubject());
        final int _tmp = entity.isSaved() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public Object insertTerms(final List<Term> terms, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTerm.insert(terms);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTerm(final Term term, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTerm.handle(term);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Term>> getAllTerms() {
    final String _sql = "SELECT * FROM terms";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"terms"}, new Callable<List<Term>>() {
      @Override
      @NonNull
      public List<Term> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEnglishTerm = CursorUtil.getColumnIndexOrThrow(_cursor, "englishTerm");
          final int _cursorIndexOfKannadaExplanation = CursorUtil.getColumnIndexOrThrow(_cursor, "kannadaExplanation");
          final int _cursorIndexOfExample = CursorUtil.getColumnIndexOrThrow(_cursor, "example");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfIsSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaved");
          final List<Term> _result = new ArrayList<Term>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Term _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpEnglishTerm;
            _tmpEnglishTerm = _cursor.getString(_cursorIndexOfEnglishTerm);
            final String _tmpKannadaExplanation;
            _tmpKannadaExplanation = _cursor.getString(_cursorIndexOfKannadaExplanation);
            final String _tmpExample;
            _tmpExample = _cursor.getString(_cursorIndexOfExample);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final boolean _tmpIsSaved;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSaved);
            _tmpIsSaved = _tmp != 0;
            _item = new Term(_tmpId,_tmpEnglishTerm,_tmpKannadaExplanation,_tmpExample,_tmpSubject,_tmpIsSaved);
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
  public Flow<List<Term>> getTermsBySubject(final String subject) {
    final String _sql = "SELECT * FROM terms WHERE subject = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, subject);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"terms"}, new Callable<List<Term>>() {
      @Override
      @NonNull
      public List<Term> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEnglishTerm = CursorUtil.getColumnIndexOrThrow(_cursor, "englishTerm");
          final int _cursorIndexOfKannadaExplanation = CursorUtil.getColumnIndexOrThrow(_cursor, "kannadaExplanation");
          final int _cursorIndexOfExample = CursorUtil.getColumnIndexOrThrow(_cursor, "example");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfIsSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaved");
          final List<Term> _result = new ArrayList<Term>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Term _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpEnglishTerm;
            _tmpEnglishTerm = _cursor.getString(_cursorIndexOfEnglishTerm);
            final String _tmpKannadaExplanation;
            _tmpKannadaExplanation = _cursor.getString(_cursorIndexOfKannadaExplanation);
            final String _tmpExample;
            _tmpExample = _cursor.getString(_cursorIndexOfExample);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final boolean _tmpIsSaved;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSaved);
            _tmpIsSaved = _tmp != 0;
            _item = new Term(_tmpId,_tmpEnglishTerm,_tmpKannadaExplanation,_tmpExample,_tmpSubject,_tmpIsSaved);
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
  public Flow<List<Term>> searchTerms(final String query) {
    final String _sql = "SELECT * FROM terms WHERE englishTerm LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"terms"}, new Callable<List<Term>>() {
      @Override
      @NonNull
      public List<Term> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEnglishTerm = CursorUtil.getColumnIndexOrThrow(_cursor, "englishTerm");
          final int _cursorIndexOfKannadaExplanation = CursorUtil.getColumnIndexOrThrow(_cursor, "kannadaExplanation");
          final int _cursorIndexOfExample = CursorUtil.getColumnIndexOrThrow(_cursor, "example");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfIsSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaved");
          final List<Term> _result = new ArrayList<Term>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Term _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpEnglishTerm;
            _tmpEnglishTerm = _cursor.getString(_cursorIndexOfEnglishTerm);
            final String _tmpKannadaExplanation;
            _tmpKannadaExplanation = _cursor.getString(_cursorIndexOfKannadaExplanation);
            final String _tmpExample;
            _tmpExample = _cursor.getString(_cursorIndexOfExample);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final boolean _tmpIsSaved;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSaved);
            _tmpIsSaved = _tmp != 0;
            _item = new Term(_tmpId,_tmpEnglishTerm,_tmpKannadaExplanation,_tmpExample,_tmpSubject,_tmpIsSaved);
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
  public Flow<List<Term>> getSavedTerms() {
    final String _sql = "SELECT * FROM terms WHERE isSaved = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"terms"}, new Callable<List<Term>>() {
      @Override
      @NonNull
      public List<Term> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEnglishTerm = CursorUtil.getColumnIndexOrThrow(_cursor, "englishTerm");
          final int _cursorIndexOfKannadaExplanation = CursorUtil.getColumnIndexOrThrow(_cursor, "kannadaExplanation");
          final int _cursorIndexOfExample = CursorUtil.getColumnIndexOrThrow(_cursor, "example");
          final int _cursorIndexOfSubject = CursorUtil.getColumnIndexOrThrow(_cursor, "subject");
          final int _cursorIndexOfIsSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaved");
          final List<Term> _result = new ArrayList<Term>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Term _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpEnglishTerm;
            _tmpEnglishTerm = _cursor.getString(_cursorIndexOfEnglishTerm);
            final String _tmpKannadaExplanation;
            _tmpKannadaExplanation = _cursor.getString(_cursorIndexOfKannadaExplanation);
            final String _tmpExample;
            _tmpExample = _cursor.getString(_cursorIndexOfExample);
            final String _tmpSubject;
            _tmpSubject = _cursor.getString(_cursorIndexOfSubject);
            final boolean _tmpIsSaved;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSaved);
            _tmpIsSaved = _tmp != 0;
            _item = new Term(_tmpId,_tmpEnglishTerm,_tmpKannadaExplanation,_tmpExample,_tmpSubject,_tmpIsSaved);
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
  public Object getTermCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM terms";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
