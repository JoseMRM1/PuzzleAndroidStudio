package com.example.puzzledroid_java.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.puzzledroid_java.utils.AppConstants;

public class BDHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "Puntuacion";
    private static final String LEVEL = "level";
    private static final String PLAYER = "player";
    private static final String DATE = "date";
    private static final String SECONDS = "seconds";
    public static int DATABASE_VERSION = 1;
    private static BDHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;


    private BDHelper(Context context) {
        super(context, AppConstants.DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = this.getWritableDatabase();
    }

    public static BDHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new BDHelper(context);
        }
        if (!dbHelper.sqLiteDatabase.isOpen()) {
            dbHelper.sqLiteDatabase = dbHelper.getWritableDatabase();
        }

        return dbHelper;
    }

    public SQLiteDatabase getSqLiteDb() {
        return sqLiteDatabase;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME + "("
                + LEVEL + " INT,"
                + PLAYER + " VARCHAR,"
                + DATE + " VARCHAR,"
                + SECONDS + " DOUBLE"
                + ")";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }


    public long insert(String tableName, ContentValues contentValues) {
        return sqLiteDatabase.insert(tableName, null, contentValues);
    }


    public int update(String table, ContentValues values, String whereClause,
                      String[] whereArgs) {
        return sqLiteDatabase.update(table, values, whereClause, whereArgs);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        return sqLiteDatabase.delete(table, whereClause, whereArgs);
    }


    public Cursor select(String table, String[] columns, String selection,
                         String[] selectionArgs, String groupBy, String having,
                         String orderBy) {
        return sqLiteDatabase.query(table, columns, selection, selectionArgs,
                groupBy, having, orderBy);
    }

    public void closeDb() {
        sqLiteDatabase.close();
    }
}
