package lineo.smarteam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

import lineo.smarteam.R;

/**
 * Created by marco on 26/09/2016.
 * Adapter for the whole DB
 */
public class DataBaseAdapter {
    private DbHelper dbHelper;

    // If you change the database schema, you must increment the database version.
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "Smarteam.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DATETIME_TYPE = " DATETIME";
    private static final String UNIQUE = " UNIQUE";
    private static final String COMMA_SEP = ",";

    // CREATE AND DELETE QUERIES
    private static final String SQL_CREATE_TABLE_CONFIGURATIONS =
            "CREATE TABLE " + Configurations.TABLE_NAME + " (" + Configurations.COLUMN_NAME_ATTRIBUTE + " TEXT PRIMARY KEY,"
                    + Configurations.COLUMN_NAME_VALUE + TEXT_TYPE + COMMA_SEP + Configurations.COLUMN_NAME_UPDATE_DATE + DATETIME_TYPE + ")";
    private static final String SQL_DELETE_TABLE_CONFIGURATIONS = "DROP TABLE IF EXISTS " + Configurations.TABLE_NAME;
    private static final String SQL_CREATE_TABLE_TEAMS =
            "CREATE TABLE " + Teams.TABLE_NAME + " (" + Teams.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + Teams.COLUMN_NAME_NAME + TEXT_TYPE + UNIQUE + COMMA_SEP + Teams.COLUMN_NAME_NUM_MATCHES + INTEGER_TYPE + COMMA_SEP
                    + Teams.COLUMN_NAME_LAST_MATCH_DATE + DATETIME_TYPE + COMMA_SEP + Teams.COLUMN_NAME_UPDATE_DATE + DATETIME_TYPE + ")";
    private static final String SQL_DELETE_TABLE_TEAMS = "DROP TABLE IF EXISTS " + Teams.TABLE_NAME;
    private static final String SQL_CREATE_TABLE_PLAYERS =
            "CREATE TABLE " + Players.TABLE_NAME + " (" + Players.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + Players.COLUMN_NAME_NAME + TEXT_TYPE + UNIQUE + COMMA_SEP + Players.COLUMN_NAME_TEAM + INTEGER_TYPE + COMMA_SEP
                    + Players.COLUMN_NAME_WINS + INTEGER_TYPE + COMMA_SEP + Players.COLUMN_NAME_DRAWS + INTEGER_TYPE + COMMA_SEP
                    + Players.COLUMN_NAME_DEFEATS + INTEGER_TYPE + COMMA_SEP + Players.COLUMN_NAME_MATCHES + INTEGER_TYPE + COMMA_SEP
                    + Players.COLUMN_NAME_MATCHES_AFTER_DEBUT + INTEGER_TYPE + COMMA_SEP + Players.COLUMN_NAME_WIN_PERCENTAGE + INTEGER_TYPE + COMMA_SEP
                    + Players.COLUMN_NAME_SCORE + INTEGER_TYPE + COMMA_SEP + Players.COLUMN_NAME_UPDATE_DATE + DATETIME_TYPE + ")";
    private static final String SQL_DELETE_TABLE_PLAYERS = "DROP TABLE IF EXISTS " + Players.TABLE_NAME;
    private static final String SQL_CREATE_TABLE_INDIVIDUAL_RESULTS =
            "CREATE TABLE " + IndividualResults.TABLE_NAME + " (" + IndividualResults.COLUMN_NAME_PLAYER_ID + INTEGER_TYPE + COMMA_SEP
                    + IndividualResults.COLUMN_NAME_TEAM_ID + INTEGER_TYPE + COMMA_SEP + IndividualResults.COLUMN_NAME_MATCHDAY + INTEGER_TYPE + COMMA_SEP
                    + IndividualResults.COLUMN_NAME_RESULT + TEXT_TYPE + COMMA_SEP + IndividualResults.COLUMN_NAME_MATCHDAY_DATE + DATETIME_TYPE + COMMA_SEP
                    + IndividualResults.COLUMN_NAME_UPDATE_DATE + DATETIME_TYPE + COMMA_SEP
                    + " PRIMARY KEY (" + IndividualResults.COLUMN_NAME_PLAYER_ID + COMMA_SEP + IndividualResults.COLUMN_NAME_MATCHDAY + ")" + ")";
    private static final String SQL_DELETE_TABLE_INDIVIDUAL_RESULTS = "DROP TABLE IF EXISTS " + IndividualResults.TABLE_NAME;

    public DataBaseAdapter(Context context) {
        dbHelper = new DbHelper(context);
    }

    private static class DbHelper extends SQLiteOpenHelper {
        DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TABLE_CONFIGURATIONS);
            db.execSQL(SQL_CREATE_TABLE_TEAMS);
            db.execSQL(SQL_CREATE_TABLE_PLAYERS);
            db.execSQL(SQL_CREATE_TABLE_INDIVIDUAL_RESULTS);
            ContentValues values = new ContentValues();
            values.put(Configurations.COLUMN_NAME_ATTRIBUTE, "VALUE_K");
            values.put(Configurations.COLUMN_NAME_VALUE, R.string.valueK);
            db.insert(Configurations.TABLE_NAME, null, values);
            values = new ContentValues();
            values.put(Configurations.COLUMN_NAME_ATTRIBUTE, "VALUE_COLOR");
            values.put(Configurations.COLUMN_NAME_VALUE, R.integer.valueColor);
            db.insert(Configurations.TABLE_NAME, null, values);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Not sure if this is safe
            db.execSQL(SQL_DELETE_TABLE_CONFIGURATIONS);
            db.execSQL(SQL_DELETE_TABLE_TEAMS);
            db.execSQL(SQL_DELETE_TABLE_PLAYERS);
            db.execSQL(SQL_DELETE_TABLE_INDIVIDUAL_RESULTS);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public DataBaseAdapter open() throws SQLException {
        dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }
}
