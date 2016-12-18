package lineo.smarteam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

import lineo.smarteam.R;

/**
 * Created by marco on 26/09/2016.
 * Adapter for the whole DB
 */
public class DataBaseAdapter {
    private DbHelper dbHelper;

    // If you change the database schema, you must increment the database version.
    private static final String TAG = "DataBaseAdapter";
    static final int DATABASE_VERSION = 3;
    static final String DATABASE_NAME = "Smarteam.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DATETIME_TYPE = " DATETIME";
    private static final String UNIQUE = " UNIQUE";
    private static final String CONSTRAINT = " CONSTRAINT";
    private static final String COMMA_SEP = ",";

    // CREATE AND DELETE QUERIES
    private static final String SQL_CREATE_TABLE_CONFIGURATIONS =
            "CREATE TABLE " + DataBase.CONFIGURATIONS_TABLE + " (" + DataBase.CONFIGURATIONS_COLUMN_ATTRIBUTE + " TEXT PRIMARY KEY,"
                    + DataBase.CONFIGURATIONS_COLUMN_VALUE + TEXT_TYPE + COMMA_SEP + DataBase.CONFIGURATIONS_COLUMN_UPDATE_DATE + DATETIME_TYPE + ")";
    private static final String SQL_DELETE_TABLE_CONFIGURATIONS = "DROP TABLE IF EXISTS " + DataBase.CONFIGURATIONS_TABLE;
    private static final String SQL_CREATE_TABLE_TEAMS =
            "CREATE TABLE " + DataBase.TEAMS_TABLE + " (" + DataBase.TEAMS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + DataBase.TEAMS_COLUMN_NAME + TEXT_TYPE + COMMA_SEP + DataBase.TEAMS_COLUMN_NUM_MATCHES + INTEGER_TYPE + COMMA_SEP
                    + DataBase.TEAMS_COLUMN_LAST_MATCH_DATE + DATETIME_TYPE + COMMA_SEP + DataBase.TEAMS_COLUMN_IS_SCORE_UPDATED + INTEGER_TYPE + COMMA_SEP
                    + DataBase.TEAMS_COLUMN_UPDATE_DATE + DATETIME_TYPE + COMMA_SEP
                    + CONSTRAINT + " TEAM_NAME_UNIQUE" + UNIQUE + " (" + DataBase.TEAMS_COLUMN_NAME + ")" +")";
    private static final String SQL_DELETE_TABLE_TEAMS = "DROP TABLE IF EXISTS " + DataBase.TEAMS_TABLE;
    private static final String SQL_CREATE_TABLE_PLAYERS =
            "CREATE TABLE " + DataBase.PLAYERS_TABLE + " (" + DataBase.PLAYERS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + DataBase.PLAYERS_COLUMN_NAME + TEXT_TYPE + COMMA_SEP + DataBase.PLAYERS_COLUMN_TEAM + INTEGER_TYPE + COMMA_SEP
                    + DataBase.PLAYERS_COLUMN_WINS + INTEGER_TYPE + COMMA_SEP + DataBase.PLAYERS_COLUMN_DRAWS + INTEGER_TYPE + COMMA_SEP
                    + DataBase.PLAYERS_COLUMN_DEFEATS + INTEGER_TYPE + COMMA_SEP + DataBase.PLAYERS_COLUMN_MATCHES + INTEGER_TYPE + COMMA_SEP
                    + DataBase.PLAYERS_COLUMN_MATCHES_AFTER_DEBUT + INTEGER_TYPE + COMMA_SEP + DataBase.PLAYERS_COLUMN_WIN_PERCENTAGE + INTEGER_TYPE + COMMA_SEP
                    + DataBase.PLAYERS_COLUMN_SCORE + INTEGER_TYPE + COMMA_SEP + DataBase.PLAYERS_COLUMN_UPDATE_DATE + DATETIME_TYPE + COMMA_SEP
                    + UNIQUE+"(" + DataBase.PLAYERS_COLUMN_NAME + COMMA_SEP + DataBase.PLAYERS_COLUMN_TEAM + "))";
    private static final String SQL_DELETE_TABLE_PLAYERS = "DROP TABLE IF EXISTS " + DataBase.PLAYERS_TABLE;
    private static final String SQL_CREATE_TABLE_INDIVIDUAL_RESULTS =
            "CREATE TABLE " + DataBase.INDIVIDUAL_RESULTS_TABLE + " (" + DataBase.INDIVIDUAL_RESULTS_COLUMN_PLAYER_ID + INTEGER_TYPE + COMMA_SEP
                    + DataBase.INDIVIDUAL_RESULTS_COLUMN_TEAM_ID + INTEGER_TYPE + COMMA_SEP + DataBase.INDIVIDUAL_RESULTS_COLUMN_MATCHDAY + INTEGER_TYPE + COMMA_SEP
                    + DataBase.INDIVIDUAL_RESULTS_COLUMN_RESULT + TEXT_TYPE + COMMA_SEP + DataBase.INDIVIDUAL_RESULTS_COLUMN_MATCHDAY_DATE + DATETIME_TYPE + COMMA_SEP
                    + " PRIMARY KEY (" + DataBase.INDIVIDUAL_RESULTS_COLUMN_PLAYER_ID + COMMA_SEP + DataBase.INDIVIDUAL_RESULTS_COLUMN_MATCHDAY + ")" + ")";
    private static final String SQL_DELETE_TABLE_INDIVIDUAL_RESULTS = "DROP TABLE IF EXISTS " + DataBase.INDIVIDUAL_RESULTS_TABLE;

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
}
