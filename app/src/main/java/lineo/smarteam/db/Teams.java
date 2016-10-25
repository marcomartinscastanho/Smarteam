package lineo.smarteam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

import lineo.smarteam.db.exception.TeamAlreadyExistsException;
import lineo.smarteam.db.exception.TeamNotFoundException;

/**
 * Created by marco on 24/10/2016.
 * Adapter for DB Table Teams
 */
public class Teams {
    public static final String TABLE_NAME = "TEAMS";
    public static final String COLUMN_NAME_ID = "TEAM_ID";
    public static final String COLUMN_NAME_NAME = "NAME";
    public static final String COLUMN_NAME_NUM_MATCHES = "NUM_MATCHES";
    public static final String COLUMN_NAME_LAST_MATCH_DATE = "LAST_MATCH";
    public static final String COLUMN_NAME_UPDATE_DATE = "UPDATE_DATE";

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

    public Teams(Context context) {
        this.context = context;
    }

    public Teams open() throws SQLException {
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insertTeam(String name) throws TeamAlreadyExistsException {
        if (checkTeamExistsByName(name)) {
            throw new TeamAlreadyExistsException();
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_NAME, name);
        values.put(COLUMN_NAME_NUM_MATCHES, 0);
        Long tsLong = System.currentTimeMillis() / 1000;
        values.put(COLUMN_NAME_UPDATE_DATE, tsLong.toString());
        return db.insertOrThrow(TABLE_NAME, null, values);
    }

    public boolean checkTeamExistsByName(String name) {
        String[] projection = {COLUMN_NAME_ID};
        String selection = COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = {name};
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        return c.moveToFirst();
    }

    public Integer getNumMatchesById(Integer id) throws TeamNotFoundException {
        String[] projection = {COLUMN_NAME_NUM_MATCHES};
        String selection = COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {String.format("%d", id)};
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer value = c.getInt(c.getColumnIndexOrThrow(COLUMN_NAME_NUM_MATCHES));
            c.close();
            return value;
        } else {
            c.close();
            throw new TeamNotFoundException();
        }
    }

    public Long getLastMatchDateById(Integer id) throws TeamNotFoundException {
        String[] projection = {COLUMN_NAME_LAST_MATCH_DATE};
        String selection = COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {String.format("%d", id)};
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Long value = c.getLong(c.getColumnIndexOrThrow(COLUMN_NAME_LAST_MATCH_DATE));
            c.close();
            return value;
        } else {
            c.close();
            throw new TeamNotFoundException();
        }
    }

    public Integer getIdByName(String name) throws TeamNotFoundException {
        String[] projection = {COLUMN_NAME_ID};
        String selection = COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = {name};
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer value = c.getInt(c.getColumnIndexOrThrow(COLUMN_NAME_ID));
            c.close();
            return value;
        } else {
            c.close();
            throw new TeamNotFoundException();
        }
    }
    //TODO: getAllTeams
}
