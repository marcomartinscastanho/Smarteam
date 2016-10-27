package lineo.smarteam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by marco on 24/10/2016.
 * Adapter for DB Table Configurations
 */
public class Configurations {
    static final String TABLE_NAME = "CONFIGURATIONS";
    static final String COLUMN_NAME_ATTRIBUTE = "ATTRIBUTE";
    static final String COLUMN_NAME_VALUE = "VALUE";
    static final String COLUMN_NAME_UPDATE_DATE = "UPDATE_DATE";

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private final Context context;

    private static class DbHelper extends SQLiteOpenHelper {
        DbHelper(Context context) {
            super(context, DataBaseAdapter.DATABASE_NAME, null, DataBaseAdapter.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public Configurations(Context context) {
        this.context = context;
    }

    public Configurations open() throws SQLException {
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insertConfiguration(String attribute, String value) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_ATTRIBUTE, attribute);
        values.put(COLUMN_NAME_VALUE, value);
        Long tsLong = System.currentTimeMillis() / 1000;
        values.put(COLUMN_NAME_UPDATE_DATE, tsLong.toString());
        return db.insert(TABLE_NAME, null, values);
    }

    public String getConfigurationValue(String attribute) {
        String[] projection = {COLUMN_NAME_VALUE};
        String selection = COLUMN_NAME_ATTRIBUTE + " = ?";
        String[] selectionArgs = {attribute};
        String value = null;
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            value = c.getString(c.getColumnIndexOrThrow(COLUMN_NAME_VALUE));
        }
        c.close();
        return value;
    }

    public boolean updateConfiguration(String attribute, String newValue) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_VALUE, newValue);
        Long tsLong = System.currentTimeMillis() / 1000;
        values.put(COLUMN_NAME_UPDATE_DATE, tsLong.toString());
        String selection = COLUMN_NAME_ATTRIBUTE + " LIKE ?";
        String[] selectionArgs = {attribute};
        return db.update(TABLE_NAME, values, selection, selectionArgs) > 0;
    }

    public boolean deleteConfiguration(String attribute) {   //might never be used
        String selection = COLUMN_NAME_ATTRIBUTE + " LIKE ?";
        String[] selectionArgs = {attribute};
        return db.delete(TABLE_NAME, selection, selectionArgs) > 0;
    }
}
