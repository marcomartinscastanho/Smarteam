package lineo.smarteam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

import lineo.smarteam.R;
import lineo.smarteam.db.exception.PlayerAlreadyExistsException;
import lineo.smarteam.db.exception.PlayerNotFoundException;

/**
 * Created by marco on 25/10/2016.
 * Adapter for DB Table Players
 */
public class Players {
    public static final String TABLE_NAME = "TEAMS";
    public static final String COLUMN_NAME_ID = "PLAYER_ID";
    public static final String COLUMN_NAME_NAME = "NAME";
    public static final String COLUMN_NAME_TEAM = "TEAM_ID";
    public static final String COLUMN_NAME_WINS = "WINS";
    public static final String COLUMN_NAME_DRAWS = "DRAWS";
    public static final String COLUMN_NAME_DEFEATS = "DEFEATS";
    public static final String COLUMN_NAME_MATCHES = "MATCHES";
    public static final String COLUMN_NAME_MATCHES_AFTER_DEBUT = "MATCHES_AFTER_DEBUT";
    public static final String COLUMN_NAME_WIN_PERCENTAGE = "WIN_PERCENTAGE";
    public static final String COLUMN_NAME_SCORE = "SCORE";
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

    public Players(Context context) {
        this.context = context;
    }

    public Players open() throws SQLException {
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insertPlayer(String name, Integer teamId) throws SQLException {
        if (checkPlayerExistsByName(name, teamId)) {
            throw new PlayerAlreadyExistsException();
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_NAME, name);
        values.put(COLUMN_NAME_TEAM, teamId);
        values.put(COLUMN_NAME_WINS, 0);
        values.put(COLUMN_NAME_DRAWS, 0);
        values.put(COLUMN_NAME_DEFEATS, 0);
        values.put(COLUMN_NAME_MATCHES, 0);
        values.put(COLUMN_NAME_MATCHES_AFTER_DEBUT, 0);
        values.put(COLUMN_NAME_WIN_PERCENTAGE, R.integer.defInitialWinPercentage);
        values.put(COLUMN_NAME_SCORE, R.integer.defInitialScore);
        Long tsLong = System.currentTimeMillis() / 1000;
        values.put(COLUMN_NAME_UPDATE_DATE, tsLong.toString());
        return db.insertOrThrow(TABLE_NAME, null, values);
    }

    public boolean checkPlayerExistsByName(String name, Integer teamId) {
        String[] projection = {COLUMN_NAME_ID};
        String selection = COLUMN_NAME_NAME + " = ? AND " + COLUMN_NAME_TEAM + " = ?";
        String[] selectionArgs = {name, String.format("%d", teamId)};
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        return c.moveToFirst();
    }

    public Integer getWinsById(Integer id) throws PlayerNotFoundException {
        String[] projection = {COLUMN_NAME_WINS};
        String selection = COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {String.format("%d", id)};
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer value = c.getInt(c.getColumnIndexOrThrow(COLUMN_NAME_WINS));
            c.close();
            return value;
        } else {
            c.close();
            throw new PlayerNotFoundException();
        }
    }

    public Integer getDrawsById(Integer id) throws PlayerNotFoundException {
        String[] projection = {COLUMN_NAME_DRAWS};
        String selection = COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {String.format("%d", id)};
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer value = c.getInt(c.getColumnIndexOrThrow(COLUMN_NAME_DRAWS));
            c.close();
            return value;
        } else {
            c.close();
            throw new PlayerNotFoundException();
        }
    }

    public Integer getDefeatsById(Integer id) throws PlayerNotFoundException {
        String[] projection = {COLUMN_NAME_DEFEATS};
        String selection = COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {String.format("%d", id)};
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer value = c.getInt(c.getColumnIndexOrThrow(COLUMN_NAME_DEFEATS));
            c.close();
            return value;
        } else {
            c.close();
            throw new PlayerNotFoundException();
        }
    }

    public Integer getMatchesById(Integer id) throws PlayerNotFoundException {
        String[] projection = {COLUMN_NAME_MATCHES};
        String selection = COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {String.format("%d", id)};
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer value = c.getInt(c.getColumnIndexOrThrow(COLUMN_NAME_MATCHES));
            c.close();
            return value;
        } else {
            c.close();
            throw new PlayerNotFoundException();
        }
    }

    public Integer getMatchesAfterDebutById(Integer id) throws PlayerNotFoundException {
        String[] projection = {COLUMN_NAME_MATCHES_AFTER_DEBUT};
        String selection = COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {String.format("%d", id)};
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer value = c.getInt(c.getColumnIndexOrThrow(COLUMN_NAME_MATCHES_AFTER_DEBUT));
            c.close();
            return value;
        } else {
            c.close();
            throw new PlayerNotFoundException();
        }
    }

    public Double getWinPercentageById(Integer id) throws PlayerNotFoundException {
        String[] projection = {COLUMN_NAME_WIN_PERCENTAGE};
        String selection = COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {String.format("%d", id)};
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Double value = c.getDouble(c.getColumnIndexOrThrow(COLUMN_NAME_WIN_PERCENTAGE));
            c.close();
            return value;
        } else {
            c.close();
            throw new PlayerNotFoundException();
        }
    }

    public Double getScoreById(Integer id) throws PlayerNotFoundException {
        String[] projection = {COLUMN_NAME_SCORE};
        String selection = COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {String.format("%d", id)};
        Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Double value = c.getDouble(c.getColumnIndexOrThrow(COLUMN_NAME_SCORE));
            c.close();
            return value;
        } else {
            c.close();
            throw new PlayerNotFoundException();
        }
    }
    //TODO: getAllPlayersByTeam
}
