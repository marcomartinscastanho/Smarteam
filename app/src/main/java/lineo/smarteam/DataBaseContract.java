package lineo.smarteam;

import android.provider.BaseColumns;

/**
 * Created by marco on 26/09/2016.
 */
public final class DataBaseContract {
    private DataBaseContract() {}

    public static class Configurations implements BaseColumns{
        public static final String TABLE_NAME = "CONFIGURATIONS";
        public static final String COLUMN_NAME_ATTRIBUTE = "ATTRIBUTE";
        public static final String COLUMN_NAME_VALUE = "VALUE";
    }
    public static class Teams implements BaseColumns{
        public static final String TABLE_NAME = "TEAMS";
        public static final String COLUMN_NAME_ID = "TEAM_ID";
        public static final String COLUMN_NAME_NAME = "NAME";
        public static final String COLUMN_NAME_NUM_MATCHES = "NUM_MATCHES";
        public static final String COLUMN_NAME_LAST_MATCH_DATE = "LAST_MATCH";
    }
    public static class Players implements BaseColumns{
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
    }
    public static class IndividualResults implements BaseColumns{
        public static final String TABLE_NAME = "INDIVIDUAL_RESULTS";
        public static final String COLUMN_NAME_PLAYER_ID = "PLAYER_ID";
        public static final String COLUMN_NAME_TEAM_ID = "TEAM_ID";
        public static final String COLUMN_NAME_MATCHDAY = "MATCHDAY";
        public static final String COLUMN_NAME_RESULT = "RESULT";
        public static final String COLUMN_NAME_MATCHDAY_DATE = "MATCHDAY_DATE";
    }
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_TABLE_CONFIGURATIONS =
            "CREATE TABLE " +Configurations.TABLE_NAME +" (" +Configurations.COLUMN_NAME_ATTRIBUTE +" TEXT PRIMARY KEY," +Configurations.COLUMN_NAME_VALUE+TEXT_TYPE +")";
    private static final String SQL_DELETE_TABLE_CONFIGURATIONS =
            "DROP TABLE IF EXISTS " +Configurations.TABLE_NAME;
    private static final String SQL_CREATE_TABLE_TEAMS =
            "CREATE TABLE " +Teams.TABLE_NAME +" (" +Teams.COLUMN_NAME_ID +" INTEGER PRIMARY KEY," +Teams.COLUMN_NAME_NAME+TEXT_TYPE+COMMA_SEP
                    +Teams.COLUMN_NAME_NUM_MATCHES+INTEGER_TYPE+COMMA_SEP +Teams.COLUMN_NAME_LAST_MATCH_DATE+INTEGER_TYPE +")";
    private static final String SQL_DELETE_TABLE_TEAMS =
            "DROP TABLE IF EXISTS " +Teams.TABLE_NAME;
    private static final String SQL_CREATE_TABLE_PLAYERS =
            "CREATE TABLE " +Players.TABLE_NAME +" (" +Players.COLUMN_NAME_ID +" INTEGER PRIMARY KEY," +Players.COLUMN_NAME_NAME+TEXT_TYPE+COMMA_SEP
                    +Players.COLUMN_NAME_TEAM+INTEGER_TYPE+COMMA_SEP +Players.COLUMN_NAME_WINS+INTEGER_TYPE+COMMA_SEP +Players.COLUMN_NAME_DRAWS+INTEGER_TYPE+COMMA_SEP
                    +Players.COLUMN_NAME_DEFEATS+INTEGER_TYPE+COMMA_SEP +Players.COLUMN_NAME_MATCHES+INTEGER_TYPE+COMMA_SEP +Players.COLUMN_NAME_MATCHES_AFTER_DEBUT+INTEGER_TYPE+COMMA_SEP
                    +Players.COLUMN_NAME_WIN_PERCENTAGE+INTEGER_TYPE+COMMA_SEP +Players.COLUMN_NAME_SCORE+INTEGER_TYPE +")";
    private static final String SQL_DELETE_TABLE_PLAYERS =
            "DROP TABLE IF EXISTS " +Players.TABLE_NAME;
    private static final String SQL_CREATE_TABLE_INDIVIDUAL_RESULTS =
            "CREATE TABLE " +IndividualResults.TABLE_NAME +" (" +IndividualResults.COLUMN_NAME_PLAYER_ID+INTEGER_TYPE+COMMA_SEP
                    +IndividualResults.COLUMN_NAME_TEAM_ID+INTEGER_TYPE+COMMA_SEP +IndividualResults.COLUMN_NAME_MATCHDAY+INTEGER_TYPE+COMMA_SEP
                    +IndividualResults.COLUMN_NAME_RESULT+TEXT_TYPE+COMMA_SEP +IndividualResults.COLUMN_NAME_MATCHDAY_DATE+INTEGER_TYPE+COMMA_SEP
                    +" PRIMARY KEY (" +IndividualResults.COLUMN_NAME_PLAYER_ID+COMMA_SEP +IndividualResults.COLUMN_NAME_MATCHDAY +")" +")";
    private static final String SQL_DELETE_TABLE_INDIVIDUAL_RESULTS =
            "DROP TABLE IF EXISTS " +IndividualResults.TABLE_NAME;
}
