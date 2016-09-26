package lineo.smarteam;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by marco on 26/09/2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Smarteam.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    // QUERIES
    private static final String SQL_CREATE_TABLE_CONFIGURATIONS =
            "CREATE TABLE " +DataBaseContract.Configurations.TABLE_NAME +" (" +DataBaseContract.Configurations.COLUMN_NAME_ATTRIBUTE
                    +" TEXT PRIMARY KEY," +DataBaseContract.Configurations.COLUMN_NAME_VALUE+TEXT_TYPE +")";
    private static final String SQL_DELETE_TABLE_CONFIGURATIONS =
            "DROP TABLE IF EXISTS " +DataBaseContract.Configurations.TABLE_NAME;
    private static final String SQL_CREATE_TABLE_TEAMS =
            "CREATE TABLE " +DataBaseContract.Teams.TABLE_NAME +" (" +DataBaseContract.Teams.COLUMN_NAME_ID +" INTEGER PRIMARY KEY,"
                    +DataBaseContract.Teams.COLUMN_NAME_NAME+TEXT_TYPE+COMMA_SEP +DataBaseContract.Teams.COLUMN_NAME_NUM_MATCHES+INTEGER_TYPE+COMMA_SEP
                    +DataBaseContract.Teams.COLUMN_NAME_LAST_MATCH_DATE+INTEGER_TYPE +")";
    private static final String SQL_DELETE_TABLE_TEAMS =
            "DROP TABLE IF EXISTS " +DataBaseContract.Teams.TABLE_NAME;
    private static final String SQL_CREATE_TABLE_PLAYERS =
            "CREATE TABLE " +DataBaseContract.Players.TABLE_NAME +" (" +DataBaseContract.Players.COLUMN_NAME_ID +" INTEGER PRIMARY KEY,"
                    +DataBaseContract.Players.COLUMN_NAME_NAME+TEXT_TYPE+COMMA_SEP +DataBaseContract.Players.COLUMN_NAME_TEAM+INTEGER_TYPE+COMMA_SEP
                    +DataBaseContract.Players.COLUMN_NAME_WINS+INTEGER_TYPE+COMMA_SEP +DataBaseContract.Players.COLUMN_NAME_DRAWS+INTEGER_TYPE+COMMA_SEP
                    +DataBaseContract.Players.COLUMN_NAME_DEFEATS+INTEGER_TYPE+COMMA_SEP +DataBaseContract.Players.COLUMN_NAME_MATCHES+INTEGER_TYPE+COMMA_SEP
                    +DataBaseContract.Players.COLUMN_NAME_MATCHES_AFTER_DEBUT+INTEGER_TYPE+COMMA_SEP +DataBaseContract.Players.COLUMN_NAME_WIN_PERCENTAGE+INTEGER_TYPE+COMMA_SEP
                    +DataBaseContract.Players.COLUMN_NAME_SCORE+INTEGER_TYPE +")";
    private static final String SQL_DELETE_TABLE_PLAYERS =
            "DROP TABLE IF EXISTS " +DataBaseContract.Players.TABLE_NAME;
    private static final String SQL_CREATE_TABLE_INDIVIDUAL_RESULTS =
            "CREATE TABLE " +DataBaseContract.IndividualResults.TABLE_NAME +" (" +DataBaseContract.IndividualResults.COLUMN_NAME_PLAYER_ID+INTEGER_TYPE+COMMA_SEP
                    +DataBaseContract.IndividualResults.COLUMN_NAME_TEAM_ID+INTEGER_TYPE+COMMA_SEP +DataBaseContract.IndividualResults.COLUMN_NAME_MATCHDAY+INTEGER_TYPE+COMMA_SEP
                    +DataBaseContract.IndividualResults.COLUMN_NAME_RESULT+TEXT_TYPE+COMMA_SEP +DataBaseContract.IndividualResults.COLUMN_NAME_MATCHDAY_DATE+INTEGER_TYPE+COMMA_SEP
                    +" PRIMARY KEY (" +DataBaseContract.IndividualResults.COLUMN_NAME_PLAYER_ID+COMMA_SEP +DataBaseContract.IndividualResults.COLUMN_NAME_MATCHDAY +")" +")";
    private static final String SQL_DELETE_TABLE_INDIVIDUAL_RESULTS =
            "DROP TABLE IF EXISTS " +DataBaseContract.IndividualResults.TABLE_NAME;


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_CONFIGURATIONS);
        db.execSQL(SQL_CREATE_TABLE_TEAMS);
        db.execSQL(SQL_CREATE_TABLE_PLAYERS);
        db.execSQL(SQL_CREATE_TABLE_INDIVIDUAL_RESULTS);

        //TODO: insert default configuration values
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DataBaseContract.Configurations.COLUMN_NAME_ATTRIBUTE, "x");
        values.put(DataBaseContract.Configurations.COLUMN_NAME_VALUE, "y");
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DataBaseContract.Configurations.TABLE_NAME, null, values);
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
