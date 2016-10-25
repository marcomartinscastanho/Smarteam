package lineo.smarteam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

import lineo.smarteam.exception.IndividualResultAlreadyExistsException;
import lineo.smarteam.exception.IndividualResultNotFoundException;

/**
 * Created by marco on 25/10/2016.
 * Adapter for DB Table IndividualResults
 */
public class IndividualResults {
    public static final String TABLE_NAME = "INDIVIDUAL_RESULTS";
    public static final String COLUMN_NAME_PLAYER_ID = "PLAYER_ID";
    public static final String COLUMN_NAME_TEAM_ID = "TEAM_ID";
    public static final String COLUMN_NAME_MATCHDAY = "MATCHDAY";
    public static final String COLUMN_NAME_RESULT = "RESULT";
    public static final String COLUMN_NAME_MATCHDAY_DATE = "MATCHDAY_DATE";
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

    public IndividualResults(Context context) {
        this.context = context;
    }

    public IndividualResults open() throws SQLException {
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insertIndividualResult(Integer playerId, Integer teamId, Integer matchday, String result, Integer date) throws SQLException {
        if (checkIndividualResultExists(playerId, teamId, matchday)) {
            throw new IndividualResultAlreadyExistsException();
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_PLAYER_ID, playerId);
        values.put(COLUMN_NAME_TEAM_ID, teamId);
        values.put(COLUMN_NAME_MATCHDAY, matchday);
        values.put(COLUMN_NAME_RESULT, result);
        values.put(COLUMN_NAME_MATCHDAY_DATE, date);
        Long tsLong = System.currentTimeMillis() / 1000;
        values.put(COLUMN_NAME_UPDATE_DATE, tsLong.toString());
        return db.insertOrThrow(TABLE_NAME, null, values);
    }

    public boolean checkIndividualResultExists(Integer playerId, Integer teamId, Integer matchday) {
        String[] projection = {COLUMN_NAME_RESULT};
        String selection = COLUMN_NAME_PLAYER_ID + " = ? AND " + COLUMN_NAME_TEAM_ID + " = ? AND " + COLUMN_NAME_MATCHDAY + " = ?";
        String[] selectionArgs = {String.format("%d", playerId), String.format("%d", teamId), String.format("%d", matchday)};
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        return c.moveToFirst();
    }

    public String getResult(Integer playerId, Integer teamId, Integer matchday) throws IndividualResultNotFoundException {
        String[] projection = {COLUMN_NAME_RESULT};
        String selection = COLUMN_NAME_PLAYER_ID + " = ? AND " + COLUMN_NAME_TEAM_ID + " = ? AND " + COLUMN_NAME_MATCHDAY + " = ?";
        String[] selectionArgs = {String.format("%d", playerId), String.format("%d", teamId), String.format("%d", matchday)};
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            String value = c.getString(c.getColumnIndexOrThrow(COLUMN_NAME_RESULT));
            c.close();
            return value;
        } else {
            c.close();
            throw new IndividualResultNotFoundException();
        }
    }
}
