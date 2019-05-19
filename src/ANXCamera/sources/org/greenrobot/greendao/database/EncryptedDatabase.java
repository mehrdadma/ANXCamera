package org.greenrobot.greendao.database;

import android.database.Cursor;
import android.database.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

public class EncryptedDatabase implements Database {
    private final SQLiteDatabase delegate;

    public EncryptedDatabase(SQLiteDatabase sQLiteDatabase) {
        this.delegate = sQLiteDatabase;
    }

    public void beginTransaction() {
        this.delegate.beginTransaction();
    }

    public void close() {
        this.delegate.close();
    }

    public DatabaseStatement compileStatement(String str) {
        return new EncryptedDatabaseStatement(this.delegate.compileStatement(str));
    }

    public void endTransaction() {
        this.delegate.endTransaction();
    }

    public void execSQL(String str) throws SQLException {
        this.delegate.execSQL(str);
    }

    public void execSQL(String str, Object[] objArr) throws SQLException {
        this.delegate.execSQL(str, objArr);
    }

    public Object getRawDatabase() {
        return this.delegate;
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return this.delegate;
    }

    public boolean inTransaction() {
        return this.delegate.inTransaction();
    }

    public boolean isDbLockedByCurrentThread() {
        return this.delegate.isDbLockedByCurrentThread();
    }

    public Cursor rawQuery(String str, String[] strArr) {
        return this.delegate.rawQuery(str, strArr);
    }

    public void setTransactionSuccessful() {
        this.delegate.setTransactionSuccessful();
    }
}
