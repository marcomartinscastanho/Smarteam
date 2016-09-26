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
}
