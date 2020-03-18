package com.martinscastanho.marco.smarteam.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.provider.BaseColumns;

import com.martinscastanho.marco.smarteam.helpers.CoefficientsFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DataBase {
    private static SQLiteDatabase db;
    private CoefficientsFactory coefficientsFactory;

    public DataBase(Context context) {
        new GetDataBaseTask().execute(new DataBaseHelper(context));
        coefficientsFactory = new CoefficientsFactory();
    }

    /*****************************   SQL CREATE AND DELETE STATEMENTS    **************************/

    private static final String SQL_CREATE_TABLE_TEAMS =
            "CREATE TABLE " + Team.TABLE_NAME + " (" + Team._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + Team.COLUMN_NAME_NAME + " TEXT," + Team.COLUMN_NAME_NUM_MATCHES + " INTEGER,"
                    + Team.COLUMN_NAME_LAST_MATCH_DATE + " DATETIME," + Team.COLUMN_NAME_IS_SCORE_UPDATED + " INTEGER,"
                    + Team.COLUMN_NAME_UPDATE_DATE + " DATETIME,"
                    + " CONSTRAINT" + " TEAM_NAME_UNIQUE" + " UNIQUE" + " (" + Team.COLUMN_NAME_NAME + ")" +")";
    private static final String SQL_DELETE_TABLE_TEAMS = "DROP TABLE IF EXISTS " + Team.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_PLAYERS =
            "CREATE TABLE " + Player.TABLE_NAME + " (" + Player._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + Player.COLUMN_NAME_NAME + " TEXT," + Player.COLUMN_NAME_TEAM_ID + " TEXT,"
                    + Player.COLUMN_NAME_WINS + " INTEGER," + Player.COLUMN_NAME_DRAWS + " INTEGER,"
                    + Player.COLUMN_NAME_DEFEATS + " INTEGER," + Player.COLUMN_NAME_MATCHES + " INTEGER,"
                    + Player.COLUMN_NAME_MATCHES_AFTER_DEBUT + " INTEGER," + Player.COLUMN_NAME_WIN_PERCENTAGE + " INTEGER,"
                    + Player.COLUMN_NAME_SCORE + " INTEGER," + Player.COLUMN_NAME_UPDATE_DATE + " DATETIME,"
                    + " UNIQUE" + "(" + Player.COLUMN_NAME_NAME + "," + Player.COLUMN_NAME_TEAM_ID + "))";
    private static final String SQL_DELETE_TABLE_PLAYERS = "DROP TABLE IF EXISTS " + Player.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_INDIVIDUAL_RESULTS =
            "CREATE TABLE " + Result.TABLE_NAME + " (" + Result.COLUMN_NAME_PLAYER_ID + " INTEGER,"
                    + Result.COLUMN_NAME_TEAM_ID + " INTEGER," + Result.COLUMN_NAME_MATCHDAY + " INTEGER,"
                    + Result.COLUMN_NAME_RESULT + " TEXT," + Result.COLUMN_NAME_MATCHDAY_DATE + " DATETIME,"
                    + " PRIMARY KEY (" + Result.COLUMN_NAME_PLAYER_ID + "," + Result.COLUMN_NAME_MATCHDAY + ")" + ")";
    private static final String SQL_DELETE_TABLE_INDIVIDUAL_RESULTS = "DROP TABLE IF EXISTS " + Result.TABLE_NAME;

    /****************************    DEFINITION OF TABLES SCHEMAS    ******************************/

    public static class Team implements BaseColumns {
        static final String TABLE_NAME = "team";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_NUM_MATCHES = "num_matches";
        static final String COLUMN_NAME_LAST_MATCH_DATE = "last_match";
        static final String COLUMN_NAME_IS_SCORE_UPDATED = "is_score_updated";
        static final String COLUMN_NAME_UPDATE_DATE = "update_date";
    }

    public static class Player implements BaseColumns {
        static final String TABLE_NAME = "player";
        public static final String COLUMN_NAME_NAME = "NAME";
        static final String COLUMN_NAME_TEAM_ID = "TEAM_ID";
        static final String COLUMN_NAME_WINS = "WINS";
        static final String COLUMN_NAME_DRAWS = "DRAWS";
        static final String COLUMN_NAME_DEFEATS = "DEFEATS";
        public static final String COLUMN_NAME_MATCHES = "MATCHES";
        static final String COLUMN_NAME_MATCHES_AFTER_DEBUT = "MATCHES_AFTER_DEBUT";
        static final String COLUMN_NAME_WIN_PERCENTAGE = "WIN_PERCENTAGE";
        public static final String COLUMN_NAME_SCORE = "SCORE";
        static final String COLUMN_NAME_UPDATE_DATE = "UPDATE_DATE";

        public static final String PLAYERS_RANKING_POSITION = "_id";
    }

    public static class Result implements BaseColumns {
        static final String TABLE_NAME = "INDIVIDUAL_RESULTS";
        static final String COLUMN_NAME_PLAYER_ID = "PLAYER_ID";
        static final String COLUMN_NAME_TEAM_ID = "TEAM_ID";
        static final String COLUMN_NAME_MATCHDAY = "MATCHDAY";
        static final String COLUMN_NAME_RESULT = "RESULT";
        static final String COLUMN_NAME_MATCHDAY_DATE = "MATCHDAY_DATE";
    }

    /********************************    HELPERS    ***********************************************/

    public class DataBaseHelper extends SQLiteOpenHelper {
        static final int DATABASE_VERSION = 1;
        static final String DATABASE_NAME = "Smarteam.db";

        DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TABLE_TEAMS);
            db.execSQL(SQL_CREATE_TABLE_PLAYERS);
            db.execSQL(SQL_CREATE_TABLE_INDIVIDUAL_RESULTS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_TABLE_TEAMS);
            db.execSQL(SQL_DELETE_TABLE_PLAYERS);
            db.execSQL(SQL_DELETE_TABLE_INDIVIDUAL_RESULTS);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public static class GetDataBaseTask extends AsyncTask<DataBaseHelper, Void, SQLiteDatabase> {
        @Override
        protected SQLiteDatabase doInBackground(DataBaseHelper... dataBaseHelpers) {
            return dataBaseHelpers[0].getWritableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase sqLiteDatabase) {
            db = sqLiteDatabase;
        }
    }

    /******************************    QUERY METHODS    *******************************************/
    // TEAMS

    public void insertTeam(String name) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(Team.COLUMN_NAME_NAME, name);
        values.put(Team.COLUMN_NAME_NUM_MATCHES, 0);
        long tsLong = System.currentTimeMillis() / 1000;
        values.put(Team.COLUMN_NAME_UPDATE_DATE, Long.toString(tsLong));
        values.put(Team.COLUMN_NAME_IS_SCORE_UPDATED, "1");
        db.insertOrThrow(Team.TABLE_NAME, null, values);
    }

    public ArrayList<String> getTeamsNames(){
        ArrayList<String> teamList = new ArrayList<>();
        String[] projection = {Team.COLUMN_NAME_NAME};
        Cursor c = db.query(Team.TABLE_NAME, projection, null, null, null, null, null);
        if(c.moveToFirst()) {
            do {
                teamList.add(c.getString(c.getColumnIndexOrThrow(Team.COLUMN_NAME_NAME)));
            }
            while(c.moveToNext());
        }
        c.close();
        return teamList;
    }

    public Integer getTeamId(String name){
        String[] projection = {Team._ID};
        String selection = Team.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = {name};
        Cursor c = db.query(Team.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            int teamId = c.getInt(c.getColumnIndexOrThrow(Team._ID));
            c.close();
            return teamId;
        } else {
            c.close();
            return null;
        }
    }

    public void deleteTeam(String name){
        int teamId = getTeamId(name);

        db.beginTransaction();
        try{
            // delete team
            String selection = Team.COLUMN_NAME_NAME + " = ?";
            String[] selectionArgs = {name};
            db.delete(Team.TABLE_NAME, selection, selectionArgs);

            // delete team players
            deleteTeamPlayers(teamId);

            // delete team matches
            deleteTeamResults(teamId);

            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    private void deleteTeamPlayers(Integer teamId){
        String selection = Player.COLUMN_NAME_TEAM_ID + " = ?";
        String[] selectionArgs = {teamId.toString()};
        db.delete(Player.TABLE_NAME, selection, selectionArgs);
    }

    private void deleteTeamResults(Integer teamId){
        String selection = Result.COLUMN_NAME_TEAM_ID + " = ?";
        String[] selectionArgs = {teamId.toString()};
        db.delete(Result.TABLE_NAME, selection, selectionArgs);
    }

    private Integer getTeamNumMatches(Integer teamId) {
        String[] projection = {Team.COLUMN_NAME_NUM_MATCHES};
        String selection = Team._ID + " = ?";
        String[] selectionArgs = {teamId.toString()};
        Cursor c = db.query(Team.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer numMatches = c.getInt(c.getColumnIndexOrThrow(Team.COLUMN_NAME_NUM_MATCHES));
            c.close();
            return numMatches;
        } else {
            c.close();
            throw new SQLException();
        }
    }

    private void setTeamNumMatches(Integer teamId, Integer numMatches) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(Team.COLUMN_NAME_NUM_MATCHES, numMatches);
        String selection = Team._ID + " = ?";
        String[] selectionArgs = { teamId.toString() };
        int count = db.update(Team.TABLE_NAME, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private void setTeamScoresUpdated(Integer teamId) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(Team.COLUMN_NAME_IS_SCORE_UPDATED, 1);
        String selection = Team._ID + " = ?";
        String[] selectionArgs = { teamId.toString() };
        int count = db.update(Team.TABLE_NAME, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private void setTeamLastMatchDateById(Integer teamId) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(Team.COLUMN_NAME_UPDATE_DATE, System.currentTimeMillis() / 1000);
        String selection = Team._ID + " = ?";
        String[] selectionArgs = { teamId.toString() };
        int count = db.update(Team.TABLE_NAME, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private void incrementTeamMatches(Integer teamId) {
        setTeamNumMatches(teamId, getTeamNumMatches(teamId) + 1);
    }

    // PLAYER
    public void insertPlayer(String name, Integer teamId) {
        ContentValues values = new ContentValues();
        values.put(Player.COLUMN_NAME_NAME, name);
        values.put(Player.COLUMN_NAME_TEAM_ID, teamId);
        values.put(Player.COLUMN_NAME_WINS, 0);
        values.put(Player.COLUMN_NAME_DRAWS, 0);
        values.put(Player.COLUMN_NAME_DEFEATS, 0);
        values.put(Player.COLUMN_NAME_MATCHES, 0);
        values.put(Player.COLUMN_NAME_MATCHES_AFTER_DEBUT, 0);
        values.put(Player.COLUMN_NAME_WIN_PERCENTAGE, 0);
        values.put(Player.COLUMN_NAME_SCORE, 0.5);
        long tsLong = System.currentTimeMillis() / 1000;
        values.put(Player.COLUMN_NAME_UPDATE_DATE, Long.toString(tsLong));
        db.insertOrThrow(Player.TABLE_NAME, null, values);
    }

    public ArrayList<String> getPlayersNames(Integer teamId){
        ArrayList<String> playerList = new ArrayList<>();
        String[] projection = {Player.COLUMN_NAME_NAME};
        String selection = Player.COLUMN_NAME_TEAM_ID + " = ?";
        String[] selectionArgs = {teamId.toString()};
        String sortOrder = Player.COLUMN_NAME_NAME + " ASC";
        Cursor c = db.query(Player.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        if(c.moveToFirst()) {
            do {
                playerList.add(c.getString(c.getColumnIndexOrThrow(Player.COLUMN_NAME_NAME)));
            }
            while(c.moveToNext());
        }
        c.close();
        return playerList;
    }

    private ArrayList<Integer> getPlayersIds(Integer teamId){
        ArrayList<Integer> playerIdList = new ArrayList<>();
        String[] projection = {Player._ID};
        String selection = Player.COLUMN_NAME_TEAM_ID + " = ?";
        String[] selectionArgs = {teamId.toString()};
        Cursor c = db.query(Player.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if(c.moveToFirst()) {
            do {
                playerIdList.add(c.getInt(c.getColumnIndexOrThrow(Player._ID)));
            }
            while(c.moveToNext());
        }
        c.close();
        return playerIdList;
    }

    private Integer getPlayerId(String name){
        String[] projection = {Player._ID};
        String selection = Player.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = {name};
        Cursor c = db.query(Player.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            int playerId = c.getInt(c.getColumnIndexOrThrow(Player._ID));
            c.close();
            return playerId;
        } else {
            c.close();
            throw new SQLException();
        }
    }

    private ArrayList<Integer> getPlayersIdsFromNames(ArrayList<String> playersNames){
        ArrayList<Integer> ids = new ArrayList<>();
        if(playersNames != null){
            for(String playerName : playersNames){
                ids.add(getPlayerId(playerName));
            }
        }
        return ids;
    }

    private Integer getAbsenceStreak(Integer playerId){
        ArrayList<String> playerResults = getPlayerResults(playerId);
        Collections.reverse(playerResults);

        int streak = 1; // the current one, not stored yet
        for(String result : playerResults){ // looping from newest to oldest
            if(result.equals(ResultType.Win.toString()) ||
                    result.equals(ResultType.Draw.toString()) ||
                    result.equals(ResultType.Defeat.toString())){
                break;
            }
            streak++;
        }

        return streak;
    }

    private Integer getPlayerWins(Integer playerId) throws SQLException {
        String[] projection = {Player.COLUMN_NAME_WINS};
        String selection = Player._ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        Cursor c = db.query(Player.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer wins = c.getInt(c.getColumnIndexOrThrow(Player.COLUMN_NAME_WINS));
            c.close();
            return wins;
        } else {
            c.close();
            throw new SQLException();
        }
    }

    private Integer getPlayerDraws(Integer playerId) throws SQLException {
        String[] projection = {Player.COLUMN_NAME_DRAWS};
        String selection = Player._ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        Cursor c = db.query(Player.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer draws = c.getInt(c.getColumnIndexOrThrow(Player.COLUMN_NAME_DRAWS));
            c.close();
            return draws;
        } else {
            c.close();
            throw new SQLException();
        }
    }

    private Integer getPlayerDefeats(Integer playerId) throws SQLException {
        String[] projection = {Player.COLUMN_NAME_DEFEATS};
        String selection = Player._ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        Cursor c = db.query(Player.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer defeats = c.getInt(c.getColumnIndexOrThrow(Player.COLUMN_NAME_DEFEATS));
            c.close();
            return defeats;
        } else {
            c.close();
            throw new SQLException();
        }
    }

    private Integer getPlayerMatches(Integer playerId) throws SQLException {
        String[] projection = {Player.COLUMN_NAME_MATCHES};
        String selection = Player._ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        Cursor c = db.query(Player.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer matches = c.getInt(c.getColumnIndexOrThrow(Player.COLUMN_NAME_MATCHES));
            c.close();
            return matches;
        } else {
            c.close();
            throw new SQLException();
        }
    }

    private Integer getPlayerMatchesAfterDebut(Integer playerId) throws SQLException {
        String[] projection = {Player.COLUMN_NAME_MATCHES_AFTER_DEBUT};
        String selection = Player._ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        Cursor c = db.query(Player.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer matchesAfterDebut = c.getInt(c.getColumnIndexOrThrow(Player.COLUMN_NAME_MATCHES_AFTER_DEBUT));
            c.close();
            return matchesAfterDebut;
        } else {
            c.close();
            throw new SQLException();
        }
    }

    private void setPlayerWins(Integer playerId, Integer wins) {
        ContentValues values = new ContentValues();
        values.put(Player.COLUMN_NAME_WINS, wins);
        String selection = Player._ID + " = ?";
        String[] selectionArgs = { playerId.toString() };
        int count = db.update(Player.TABLE_NAME, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private void setPlayerDraws(Integer playerId, Integer draws)  {
        ContentValues values = new ContentValues();
        values.put(Player.COLUMN_NAME_DRAWS, draws);
        String selection = Player._ID + " = ?";
        String[] selectionArgs = { playerId.toString() };
        int count = db.update(Player.TABLE_NAME, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private void setPlayerDefeats(Integer playerId, Integer defeats) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(Player.COLUMN_NAME_DEFEATS, defeats);
        String selection = Player._ID + " = ?";
        String[] selectionArgs = { playerId.toString() };
        int count = db.update(Player.TABLE_NAME, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private void setPlayerMatches(Integer playerId, Integer matches) {
        ContentValues values = new ContentValues();
        values.put(Player.COLUMN_NAME_MATCHES, matches);
        String selection = Player._ID + " = ?";
        String[] selectionArgs = { playerId.toString() };
        int count = db.update(Player.TABLE_NAME, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private void setPlayerMatchesAfterDebut(Integer playerId, Integer matchesAfterDebut) {
        ContentValues values = new ContentValues();
        values.put(Player.COLUMN_NAME_MATCHES_AFTER_DEBUT, matchesAfterDebut);
        String selection = Player._ID + " = ?";
        String[] selectionArgs = { playerId.toString() };
        int count = db.update(Player.TABLE_NAME, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private void setPlayerWinPercentage(Integer playerId, Double winPercentage) {
        ContentValues values = new ContentValues();
        values.put(Player.COLUMN_NAME_WIN_PERCENTAGE, winPercentage);
        String selection = Player._ID + " = ?";
        String[] selectionArgs = { playerId.toString() };
        int count = db.update(Player.TABLE_NAME, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private void setPlayerScore(Integer playerId, Double score) {
        ContentValues values = new ContentValues();
        values.put(Player.COLUMN_NAME_SCORE, score);
        String selection = Player._ID + " = ?";
        String[] selectionArgs = { playerId.toString() };
        int count = db.update(Player.TABLE_NAME, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private void setPlayerUpdateDate(Integer playerId, Long date) {
        ContentValues values = new ContentValues();
        values.put(Player.COLUMN_NAME_UPDATE_DATE, date);
        String selection = Player._ID + " = ?";
        String[] selectionArgs = { playerId.toString() };
        int count = db.update(Player.TABLE_NAME, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private void incrementPlayerWins(Integer playerId) {
        setPlayerWins(playerId, getPlayerWins(playerId) + 1);
    }

    private void incrementPlayerDraws(Integer playerId) {
        setPlayerDraws(playerId, getPlayerDraws(playerId) + 1);
    }

    private void incrementPlayerDefeats(Integer playerId) {
        setPlayerDefeats(playerId, getPlayerDefeats(playerId) + 1);
    }

    private void incrementPlayerNumMatches(Integer playerId) {
        setPlayerMatches(playerId, getPlayerMatches(playerId) + 1);
    }

    private void incrementPlayerMatchesAfterDebut(Integer playerId) {
        setPlayerMatchesAfterDebut(playerId, getPlayerMatchesAfterDebut(playerId) + 1);
    }

    private Double calculatePlayerWinPercentage(Integer playerId){
        Integer numWins = getPlayerWins(playerId);
        Integer numMatches = getPlayerMatches(playerId);
        return numWins.doubleValue() / numMatches.doubleValue();
    }

    private void updatePlayerWinPercentage(Integer playerId) {
        setPlayerWinPercentage(playerId, calculatePlayerWinPercentage(playerId));
    }

    private void updatePlayerScore(Integer playerId) {
        Integer matchesAfterDebut = getPlayerMatchesAfterDebut(playerId);
        ArrayList<Double> weightingCoefficients = coefficientsFactory.get(matchesAfterDebut);
        ArrayList<Integer> results = playerIndividualResultsAsInt(getPlayerResults(playerId));
        int iCoefficient = 0;
        double score = 0.0;
        for(Integer res : results){
            score += res.doubleValue() * weightingCoefficients.get(iCoefficient);
            ++iCoefficient;
        }
        setPlayerScore(playerId, normalizeScore(score));
    }

    private void updatePlayerUpdateDateNow(Integer playerId) {
        setPlayerUpdateDate(playerId, System.currentTimeMillis() / 1000);
    }

    private void updatePlayerStats(Integer playerId, ResultType resultType){
        if(resultType == ResultType.Win){
            incrementPlayerWins(playerId);
        }
        else if(resultType == ResultType.Draw){
            incrementPlayerDraws(playerId);
        }
        else if(resultType == ResultType.Defeat){
            incrementPlayerDefeats(playerId);
        }

        if(resultType == ResultType.Win ||
           resultType == ResultType.Draw ||
           resultType == ResultType.Defeat){
            incrementPlayerNumMatches(playerId);
            updatePlayerWinPercentage(playerId);
        }

        updatePlayerScore(playerId);
        updatePlayerUpdateDateNow(playerId);
        incrementPlayerMatchesAfterDebut(playerId);
    }

    private boolean hasNeverPlayedBefore(Integer playerId){
        String[] projection = {Player.COLUMN_NAME_MATCHES};
        String selection = Player._ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        Cursor c = db.query(Player.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            int numMatches = c.getInt(c.getColumnIndexOrThrow(Player.COLUMN_NAME_MATCHES));
            c.close();
            return numMatches == 0;
        } else {
            c.close();
            return false;
        }
    }

    public Cursor getRankingByTeamId(Integer teamId){
        String query = "SELECT " + Player.COLUMN_NAME_NAME + ", CAST(100*" + Player.COLUMN_NAME_SCORE + " AS INTEGER) AS "+ Player.COLUMN_NAME_SCORE +", " + Player.COLUMN_NAME_MATCHES + " ,"
                + " 1+(SELECT COUNT(*) FROM " + Player.TABLE_NAME + " B WHERE A." + Player.COLUMN_NAME_SCORE + " < B." + Player.COLUMN_NAME_SCORE + " AND B." + Player.COLUMN_NAME_TEAM_ID + " = ?) AS " + Player.PLAYERS_RANKING_POSITION
                + " FROM " + Player.TABLE_NAME + " A WHERE A." + Player.COLUMN_NAME_TEAM_ID + " = ? ORDER BY " + Player.COLUMN_NAME_SCORE + " DESC ";
        String[] selectionArgs = {teamId.toString(), teamId.toString()};
        return db.rawQuery(query, selectionArgs);
    }

    // RESULTS
    public void addMatch(Integer teamId, ArrayList<String> drawPlayersNames, ArrayList<String> winnersNames, ArrayList<String> losersNames){
        int matchday = getTeamNumMatches(teamId) + 1;
        ArrayList<Integer> drawPlayers = getPlayersIdsFromNames(drawPlayersNames);
        ArrayList<Integer> winners = getPlayersIdsFromNames(winnersNames);
        ArrayList<Integer> losers = getPlayersIdsFromNames(losersNames);
        db.beginTransaction();
        try {
            for(int playerId : getPlayersIds(teamId)){
                ResultType resultType = getResultType(playerId, drawPlayers, winners, losers);
                if(resultType == ResultType.Absence && hasNeverPlayedBefore(playerId)){
                    // still hasn't played
                    continue;
                }

                insertIndividualResult(playerId, teamId, matchday, resultType);
                updatePlayerStats(playerId, resultType);
            }

            incrementTeamMatches(teamId);
            setTeamLastMatchDateById(teamId);
            setTeamScoresUpdated(teamId);

            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    private ArrayList<String> getPlayerResults(Integer playerId){
        ArrayList<String> playerResults = new ArrayList<>();
        String[] projection = {Result.COLUMN_NAME_RESULT};
        String selection = Result.COLUMN_NAME_PLAYER_ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        String sortOrder = Result.COLUMN_NAME_MATCHDAY + " ASC";
        Cursor c = db.query(Result.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        if (c.moveToFirst()){
            do{
                playerResults.add(c.getString(c.getColumnIndexOrThrow(Result.COLUMN_NAME_RESULT)));
            }while(c.moveToNext());
        }
        c.close();
        return playerResults;
    }

    private void insertIndividualResult(Integer playerId, Integer teamId, Integer matchday, ResultType result) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(Result.COLUMN_NAME_PLAYER_ID, playerId);
        values.put(Result.COLUMN_NAME_TEAM_ID, teamId);
        values.put(Result.COLUMN_NAME_MATCHDAY, matchday);
        values.put(Result.COLUMN_NAME_RESULT, result.toString());
        long tsLong = System.currentTimeMillis() / 1000;
        values.put(Result.COLUMN_NAME_MATCHDAY_DATE, tsLong);
        db.insertOrThrow(Result.TABLE_NAME, null, values);
    }

    // HELPERS
    enum ResultType {
        Win("W"),
        Draw("D"),
        Defeat("L"),
        Absence("-"),   //short or any
        MediumAbsence("+"),
        LongAbsence("*");

        ResultType(String r){
        }
    }

    private ResultType getAbsenceType(Integer playerId){
        int absenceStreak = getAbsenceStreak(playerId);
        if(absenceStreak > 10){
            return ResultType.LongAbsence;
        }
        if(absenceStreak > 3){
            return ResultType.MediumAbsence;
        }
        return ResultType.Absence;
    }

    private ResultType getResultType(Integer playerId, ArrayList<Integer> drawPlayers, ArrayList<Integer> winners, ArrayList<Integer> losers){
        if(winners.contains(playerId)){
            return ResultType.Win;
        }
        else if(drawPlayers.contains(playerId)){
            return ResultType.Draw;
        }
        else if(losers.contains(playerId)){
            return ResultType.Defeat;
        }
        else {
            //absent
            return getAbsenceType(playerId);
        }
    }

    private double normalizeScore(Double score){
        Double KEY_PREF_DEFEAT_SCORE = -2.0;
        Double KEY_PREF_WIN_SCORE = 2.0;

        score -= KEY_PREF_DEFEAT_SCORE;
        score /= (KEY_PREF_WIN_SCORE - KEY_PREF_DEFEAT_SCORE);
        return score;
    }

    private ArrayList<Integer> playerIndividualResultsAsInt(ArrayList<String> resultsAsString){
        ArrayList<Integer> resInteger = new ArrayList<>();
        for(String res : resultsAsString)
            resInteger.add(mapResultToInt().get(res));
        return resInteger;
    }

    private Map<String, Integer> mapResultToInt(){
        Map<String, Integer> map = new HashMap<>();
        map.put(ResultType.Win.toString(), 2);
        map.put(ResultType.Draw.toString(), 1);
        map.put(ResultType.Defeat.toString(), -2);
        map.put(ResultType.Absence.toString(), 0);
        map.put(ResultType.MediumAbsence.toString(), -1);
        map.put(ResultType.LongAbsence.toString(), -2);

        return map;
    }
}
