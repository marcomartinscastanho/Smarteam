package lineo.smarteam.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

import lineo.smarteam.Coefficients;
import lineo.smarteam.MyApplication;
import lineo.smarteam.R;
import lineo.smarteam.exception.PlayerAlreadyExistsException;
import lineo.smarteam.exception.PlayerNotFoundException;
import lineo.smarteam.exception.TeamAlreadyExistsException;
import lineo.smarteam.exception.TeamNotFoundException;

/**
 * Created by marco on 15/12/2016.
 * Adapter for DB
 */

public class DataBase {
    private static final String TAG = "DataBase";
    static final String CONFIGURATIONS_TABLE = "CONFIGURATIONS";
    static final String CONFIGURATIONS_COLUMN_ATTRIBUTE = "ATTRIBUTE";
    static final String CONFIGURATIONS_COLUMN_VALUE = "VALUE";
    static final String CONFIGURATIONS_COLUMN_UPDATE_DATE = "UPDATE_DATE";
    static final String TEAMS_TABLE = "TEAMS";
    static final String TEAMS_COLUMN_ID = "TEAM_ID";
    static final String TEAMS_COLUMN_NAME = "NAME";
    static final String TEAMS_COLUMN_NUM_MATCHES = "NUM_MATCHES";
    static final String TEAMS_COLUMN_LAST_MATCH_DATE = "LAST_MATCH";
    static final String TEAMS_COLUMN_UPDATE_DATE = "UPDATE_DATE";
    static final String PLAYERS_TABLE = "PLAYERS";
    static final String PLAYERS_COLUMN_ID = "PLAYER_ID";
    static final String PLAYERS_COLUMN_NAME = "NAME";
    static final String PLAYERS_COLUMN_TEAM = "TEAM_ID";
    static final String PLAYERS_COLUMN_WINS = "WINS";
    static final String PLAYERS_COLUMN_DRAWS = "DRAWS";
    static final String PLAYERS_COLUMN_DEFEATS = "DEFEATS";
    static final String PLAYERS_COLUMN_MATCHES = "MATCHES";
    static final String PLAYERS_COLUMN_MATCHES_AFTER_DEBUT = "MATCHES_AFTER_DEBUT";
    static final String PLAYERS_COLUMN_WIN_PERCENTAGE = "WIN_PERCENTAGE";
    static final String PLAYERS_COLUMN_SCORE = "SCORE";
    static final String PLAYERS_COLUMN_UPDATE_DATE = "UPDATE_DATE";
    static final String INDIVIDUAL_RESULTS_TABLE = "INDIVIDUAL_RESULTS";
    static final String INDIVIDUAL_RESULTS_COLUMN_PLAYER_ID = "PLAYER_ID";
    static final String INDIVIDUAL_RESULTS_COLUMN_TEAM_ID = "TEAM_ID";
    static final String INDIVIDUAL_RESULTS_COLUMN_MATCHDAY = "MATCHDAY";
    static final String INDIVIDUAL_RESULTS_COLUMN_RESULT = "RESULT";
    static final String INDIVIDUAL_RESULTS_COLUMN_MATCHDAY_DATE = "MATCHDAY_DATE";
    static final String INDIVIDUAL_RESULTS_COLUMN_UPDATE_DATE = "UPDATE_DATE";

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private final Context context;

    private SharedPreferences sharedPreferences;
    private Coefficients coefficients;

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

    public DataBase(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        coefficients = new Coefficients(Double.parseDouble(sharedPreferences.getString("KEY_PREF_K", "1.1")));
    }

    public DataBase open() throws SQLException {
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void beginTransaction(){
        db.beginTransaction();
    }

    public void setTransactionSuccessful(){
        db.setTransactionSuccessful();
    }

    public void endTransaction(){
        db.endTransaction();
    }

    public void close() {
        dbHelper.close();
    }

    /*
     *   COEFFICIENTS
     */
    public void updateCoefficientsWeight(Double newWeight){
        coefficients = new Coefficients(newWeight);
    }

    /*
     *   TEAMS
     */
    public boolean isTeamsEmpty(){
        return getTeamsCount()==0;
    }

    private Integer getTeamsCount(){
        Cursor c = db.query(TEAMS_TABLE, null, null, null, null, null, null);
        Integer count = c.getCount();
        c.close();
        return count;
    }

    public ArrayList<String> getTeamsNames(){
        ArrayList<String> list = new ArrayList<>();
        String[] projection = {TEAMS_COLUMN_NAME};
        Cursor c = db.query(TEAMS_TABLE, projection, null, null, null, null, null);
        if (c.moveToFirst()){
            do{
                list.add(c.getString(c.getColumnIndexOrThrow(TEAMS_COLUMN_NAME)));
            }while(c.moveToNext());
        }
        c.close();
        return list;
    }

    public long deleteTeamByName(String name) throws TeamNotFoundException, PlayerNotFoundException {
        if (!checkTeamExistsByName(name)) {
            throw new TeamNotFoundException();
        }
        deleteAllPlayersByTeamId(getTeamIdByName(name));
        String selection = TEAMS_COLUMN_NAME + " = ?";
        String[] selectionArgs = {name};
        return db.delete(TEAMS_TABLE, selection, selectionArgs);
    }

    private boolean checkTeamExistsByName(String name) {
        String[] projection = {TEAMS_COLUMN_ID};
        String selection = TEAMS_COLUMN_NAME + " = ?";
        String[] selectionArgs = {name};
        Cursor c = db.query(TEAMS_TABLE, projection, selection, selectionArgs, null, null, null);
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public long insertTeam(String name) throws TeamAlreadyExistsException {
        if (checkTeamExistsByName(name)) {
            throw new TeamAlreadyExistsException();
        }
        ContentValues values = new ContentValues();
        values.put(TEAMS_COLUMN_NAME, name);
        values.put(TEAMS_COLUMN_NUM_MATCHES, 0);
        Long tsLong = System.currentTimeMillis() / 1000;
        values.put(TEAMS_COLUMN_UPDATE_DATE, tsLong.toString());
        return db.insertOrThrow(TEAMS_TABLE, null, values);
    }

    public Integer getTeamIdByName(String name) throws TeamNotFoundException {
        String[] projection = {TEAMS_COLUMN_ID};
        String selection = TEAMS_COLUMN_NAME + " = ?";
        String[] selectionArgs = {name};
        Cursor c = db.query(TEAMS_TABLE, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer value = c.getInt(c.getColumnIndexOrThrow(TEAMS_COLUMN_ID));
            c.close();
            return value;
        } else {
            c.close();
            throw new TeamNotFoundException();
        }
    }

    public Integer getTeamNumMatchesById(Integer id) throws TeamNotFoundException {
        String[] projection = {TEAMS_COLUMN_NUM_MATCHES};
        String selection = TEAMS_COLUMN_ID + " = ?";
        String[] selectionArgs = {id.toString()};
        Cursor c = db.query(TEAMS_TABLE, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer value = c.getInt(c.getColumnIndexOrThrow(TEAMS_COLUMN_NUM_MATCHES));
            c.close();
            return value;
        } else {
            c.close();
            throw new TeamNotFoundException();
        }
    }

    private void setTeamNumMatchesById(Integer teamId, Integer numMatches) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(TEAMS_COLUMN_NUM_MATCHES, numMatches);
        String selection = TEAMS_COLUMN_ID + " = ?";
        String[] selectionArgs = { teamId.toString() };
        int count = db.update(TEAMS_TABLE, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private void setTeamLastMatchDateById(Integer teamId) throws SQLException {
        ContentValues values = new ContentValues();
        Long tsLong = System.currentTimeMillis() / 1000;
        values.put(TEAMS_COLUMN_LAST_MATCH_DATE, tsLong);
        String selection = TEAMS_COLUMN_ID + " = ?";
        String[] selectionArgs = { teamId.toString() };
        int count = db.update(TEAMS_TABLE, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private void incrementTeamMatches(Integer teamId) throws SQLException {
        Integer matches = getTeamNumMatchesById(teamId);
        setTeamNumMatchesById(teamId, matches+1);
    }

    public void addTeamMatch(Integer teamId) throws SQLException {
        incrementTeamMatches(teamId);
        setTeamLastMatchDateById(teamId);
    }

    /*
     *   PLAYERS
     */
    public boolean isPlayersEmptyByTeamId(Integer teamId){
        return getPlayersCountByTeamId(teamId)==0;
    }

    public Integer getPlayersCountByTeamId(Integer teamId){
        String selection = PLAYERS_COLUMN_TEAM + " = ?";
        String[] selectionArgs = {teamId.toString()};
        Cursor c = db.query(PLAYERS_TABLE, null, selection, selectionArgs, null, null, null);
        Integer count = c.getCount();
        c.close();
        return count;
    }

    public ArrayList<String> getPlayersNamesByTeamId(Integer teamId){
        Log.i(TAG, "getPlayersNamesByTeamId()");
        ArrayList<String> list = new ArrayList<>();
        String[] projection = {PLAYERS_COLUMN_NAME};
        String selection = PLAYERS_COLUMN_TEAM + " = ?";
        String[] selectionArgs = {teamId.toString()};
        String sortOrder = PLAYERS_COLUMN_NAME + " ASC";
        Cursor c = db.query(PLAYERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        if (c.moveToFirst()){
            do{
                list.add(c.getString(c.getColumnIndexOrThrow(PLAYERS_COLUMN_NAME)));
            }while(c.moveToNext());
        }
        c.close();
        return list;
    }

    private Integer getPlayerIdByNameAndTeamId(String playerName, Integer teamId) throws PlayerNotFoundException {
        Log.i(TAG, "getPlayerIdByNameAndTeamId()");
        String[] projection = {PLAYERS_COLUMN_ID};
        String selection = PLAYERS_COLUMN_NAME + " = ? AND " + PLAYERS_COLUMN_TEAM + " = ?";
        String[] selectionArgs = {playerName, teamId.toString()};
        Cursor c = db.query(PLAYERS_TABLE, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer value = c.getInt(c.getColumnIndexOrThrow(PLAYERS_COLUMN_ID));
            c.close();
            return value;
        } else {
            c.close();
            throw new PlayerNotFoundException();
        }
    }

    public long deletePlayerByNameAndTeamId(String name, Integer teamId) throws PlayerNotFoundException {
        if (!checkPlayerExistsByName(name, teamId)) {
            throw new PlayerNotFoundException();
        }
        deleteAllIndividualResultsByPlayerIdAndTeamId(getPlayerIdByNameAndTeamId(name, teamId), teamId);
        String selection = PLAYERS_COLUMN_NAME + " = ? AND " + PLAYERS_COLUMN_TEAM + " = ?";
        String[] selectionArgs = {name, teamId.toString()};
        return db.delete(PLAYERS_TABLE, selection, selectionArgs);
    }

    private long deleteAllPlayersByTeamId(Integer teamId) {
        deleteAllIndividualResultsByTeamId(teamId);
        String selection = PLAYERS_COLUMN_TEAM + " = ?";
        String[] selectionArgs = {teamId.toString()};
        return db.delete(PLAYERS_TABLE, selection, selectionArgs);
    }

    private boolean checkPlayerExistsByName(String name, Integer teamId) {
        String[] projection = {PLAYERS_COLUMN_ID};
        String selection = PLAYERS_COLUMN_NAME + " = ? AND " + PLAYERS_COLUMN_TEAM + " = ?";
        String[] selectionArgs = {name, teamId.toString()};
        Cursor c = db.query(PLAYERS_TABLE, projection, selection, selectionArgs, null, null, null);
        boolean exist = c.moveToFirst();
        c.close();
        return exist;
    }

    public long insertPlayer(String name, Integer teamId) throws PlayerAlreadyExistsException {
        if (checkPlayerExistsByName(name, teamId)) {
            throw new PlayerAlreadyExistsException();
        }
        ContentValues values = new ContentValues();
        values.put(PLAYERS_COLUMN_NAME, name);
        values.put(PLAYERS_COLUMN_TEAM, teamId);
        values.put(PLAYERS_COLUMN_WINS, 0);
        values.put(PLAYERS_COLUMN_DRAWS, 0);
        values.put(PLAYERS_COLUMN_DEFEATS, 0);
        values.put(PLAYERS_COLUMN_MATCHES, 0);
        values.put(PLAYERS_COLUMN_MATCHES_AFTER_DEBUT, 0);
        values.put(PLAYERS_COLUMN_WIN_PERCENTAGE, context.getResources().getString(R.string.defInitialWinPercentage));
        values.put(PLAYERS_COLUMN_SCORE, context.getResources().getString(R.string.defInitialScore));
        Long tsLong = System.currentTimeMillis() / 1000;
        values.put(PLAYERS_COLUMN_UPDATE_DATE, tsLong.toString());
        return db.insertOrThrow(PLAYERS_TABLE, null, values);
    }

    public ArrayList<Integer> getPlayersIdsByTeamIdOrderByPlayerName(Integer teamId){
        Log.i(TAG, "getPlayersIdsByTeamIdOrderByPlayerName()");
        ArrayList<Integer> list = new ArrayList<>();
        String[] projection = {PLAYERS_COLUMN_ID};
        String selection = PLAYERS_COLUMN_TEAM + " = ?";
        String[] selectionArgs = {teamId.toString()};
        String sortOrder = PLAYERS_COLUMN_NAME + " ASC";
        Cursor c = db.query(PLAYERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        if (c.moveToFirst()){
            do{
                list.add(c.getInt(c.getColumnIndexOrThrow(PLAYERS_COLUMN_ID)));
            }while(c.moveToNext());
        }
        c.close();
        return list;
    }

    private Integer getPlayerWinsById(Integer playerId) throws PlayerNotFoundException {
        String[] projection = {PLAYERS_COLUMN_WINS};
        String selection = PLAYERS_COLUMN_ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        Cursor c = db.query(PLAYERS_TABLE, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer value = c.getInt(c.getColumnIndexOrThrow(PLAYERS_COLUMN_WINS));
            c.close();
            return value;
        } else {
            c.close();
            throw new PlayerNotFoundException();
        }
    }

    private void setPlayerWinsById(Integer playerId, Integer wins) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(PLAYERS_COLUMN_WINS, wins);
        String selection = PLAYERS_COLUMN_ID + " = ?";
        String[] selectionArgs = { playerId.toString() };
        int count = db.update(PLAYERS_TABLE, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private Integer getPlayerDrawsById(Integer playerId) throws PlayerNotFoundException {
        String[] projection = {PLAYERS_COLUMN_DRAWS};
        String selection = PLAYERS_COLUMN_ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        Cursor c = db.query(PLAYERS_TABLE, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer value = c.getInt(c.getColumnIndexOrThrow(PLAYERS_COLUMN_DRAWS));
            c.close();
            return value;
        } else {
            c.close();
            throw new PlayerNotFoundException();
        }
    }

    private void setPlayerDrawsById(Integer playerId, Integer draws) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(PLAYERS_COLUMN_DRAWS, draws);
        String selection = PLAYERS_COLUMN_ID + " = ?";
        String[] selectionArgs = { playerId.toString() };
        int count = db.update(PLAYERS_TABLE, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private Integer getPlayerDefeatsById(Integer playerId) throws PlayerNotFoundException {
        String[] projection = {PLAYERS_COLUMN_DEFEATS};
        String selection = PLAYERS_COLUMN_ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        Cursor c = db.query(PLAYERS_TABLE, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer value = c.getInt(c.getColumnIndexOrThrow(PLAYERS_COLUMN_DEFEATS));
            c.close();
            return value;
        } else {
            c.close();
            throw new PlayerNotFoundException();
        }
    }

    private void setPlayerDefeatsById(Integer playerId, Integer defeats) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(PLAYERS_COLUMN_DEFEATS, defeats);
        String selection = PLAYERS_COLUMN_ID + " = ?";
        String[] selectionArgs = { playerId.toString() };
        int count = db.update(PLAYERS_TABLE, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    public Integer getPlayerMatchesById(Integer playerId) throws PlayerNotFoundException {
        String[] projection = {PLAYERS_COLUMN_MATCHES};
        String selection = PLAYERS_COLUMN_ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        Cursor c = db.query(PLAYERS_TABLE, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer value = c.getInt(c.getColumnIndexOrThrow(PLAYERS_COLUMN_MATCHES));
            c.close();
            return value;
        } else {
            c.close();
            throw new PlayerNotFoundException();
        }
    }

    private void setPlayerMatchesById(Integer playerId, Integer matches) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(PLAYERS_COLUMN_MATCHES, matches);
        String selection = PLAYERS_COLUMN_ID + " = ?";
        String[] selectionArgs = { playerId.toString() };
        int count = db.update(PLAYERS_TABLE, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private Integer getPlayerMatchesAfterDebutById(Integer playerId) throws PlayerNotFoundException {
        String[] projection = {PLAYERS_COLUMN_MATCHES_AFTER_DEBUT};
        String selection = PLAYERS_COLUMN_ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        Cursor c = db.query(PLAYERS_TABLE, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer value = c.getInt(c.getColumnIndexOrThrow(PLAYERS_COLUMN_MATCHES_AFTER_DEBUT));
            c.close();
            return value;
        } else {
            c.close();
            throw new PlayerNotFoundException();
        }
    }

    private void setPlayerMatchesAfterDebutById(Integer playerId, Integer matchesAfterDebut) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(PLAYERS_COLUMN_MATCHES_AFTER_DEBUT, matchesAfterDebut);
        String selection = PLAYERS_COLUMN_ID + " = ?";
        String[] selectionArgs = { playerId.toString() };
        int count = db.update(PLAYERS_TABLE, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    public Double getPlayerWinPercentageById(Integer playerId) throws PlayerNotFoundException {
        String[] projection = {PLAYERS_COLUMN_WIN_PERCENTAGE};
        String selection = PLAYERS_COLUMN_ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        Cursor c = db.query(PLAYERS_TABLE, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Double value = c.getDouble(c.getColumnIndexOrThrow(PLAYERS_COLUMN_WIN_PERCENTAGE));
            c.close();
            return value;
        } else {
            c.close();
            throw new PlayerNotFoundException();
        }
    }

    private void setPlayerWinPercentageById(Integer playerId, Double winPercentage) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(PLAYERS_COLUMN_WIN_PERCENTAGE, winPercentage);
        String selection = PLAYERS_COLUMN_ID + " = ?";
        String[] selectionArgs = { playerId.toString() };
        int count = db.update(PLAYERS_TABLE, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    public Double getPlayerScoreById(Integer playerId) throws PlayerNotFoundException {
        String[] projection = {PLAYERS_COLUMN_SCORE};
        String selection = PLAYERS_COLUMN_ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        Cursor c = db.query(PLAYERS_TABLE, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            Double value = c.getDouble(c.getColumnIndexOrThrow(PLAYERS_COLUMN_SCORE));
            c.close();
            return value;
        } else {
            c.close();
            throw new PlayerNotFoundException();
        }
    }

    private void setPlayerScoreById(Integer playerId, Double score) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(PLAYERS_COLUMN_SCORE, score);
        String selection = PLAYERS_COLUMN_ID + " = ?";
        String[] selectionArgs = { playerId.toString() };
        int count = db.update(PLAYERS_TABLE, values, selection, selectionArgs);
        if(count <= 0)
            throw new SQLException();
    }

    private void incrementPlayerWins(Integer playerId) throws SQLException {
        Integer numWins = getPlayerWinsById(playerId);
        setPlayerWinsById(playerId, numWins+1);
    }

    private void incrementPlayerDraws(Integer playerId) throws SQLException {
        Integer numDraws = getPlayerDrawsById(playerId);
        setPlayerDrawsById(playerId, numDraws+1);
    }

    private void incrementPlayerDefeats(Integer playerId) throws SQLException {
        Integer numDefeats = getPlayerDefeatsById(playerId);
        setPlayerDefeatsById(playerId, numDefeats+1);
    }

    private void incrementPlayerMatches(Integer playerId) throws SQLException {
        Integer numMatches = getPlayerMatchesById(playerId);
        setPlayerMatchesById(playerId, numMatches+1);
    }

    private void incrementPlayerMatchesAfterDebut(Integer playerId) throws SQLException {
        Integer numMatchesAfterDebut = getPlayerMatchesAfterDebutById(playerId);
        setPlayerMatchesAfterDebutById(playerId, numMatchesAfterDebut+1);
    }

    private void calculatePlayerWinPercentage(Integer playerId) throws SQLException {
        Integer numWins = getPlayerWinsById(playerId);
        Integer numMatches = getPlayerMatchesById(playerId);
        Double winPercentage;
        if(numMatches==0){
            winPercentage=0.5;
        }
        else{
            winPercentage =  numWins.doubleValue() / numMatches.doubleValue();
        }
        setPlayerWinPercentageById(playerId, winPercentage);
    }

    private void calculatePlayerScore(Integer playerId) throws SQLException {
        Integer matchesAfterDebut = getPlayerMatchesAfterDebutById(playerId);
        if(matchesAfterDebut == 0)
            return;

        Double score=0.0;
        ArrayList<Double> matchCoefficients = coefficients.get(matchesAfterDebut-1);    //-1 because list starts in index 0
        ArrayList<Integer> results = getIntegerIndividualResultsByPlayerId(playerId);
        Integer c=0;
        for(Integer res : results){
            Log.d(TAG, "calculatePlayerScore() playerId: "+playerId+": "+res.doubleValue()+"*"+matchCoefficients.get(c));
            score += res.doubleValue()*matchCoefficients.get(c);
            ++c;
        }
        //normalize score
        score -= Double.parseDouble(sharedPreferences.getString("KEY_PREF_DEFEAT_SCORE", "-2"));
        score /= (Double.parseDouble(sharedPreferences.getString("KEY_PREF_WIN_SCORE", "2")) - Double.parseDouble(sharedPreferences.getString("KEY_PREF_DEFEAT_SCORE", "-2")));
        setPlayerScoreById(playerId, score);
    }

    public void addPlayerWin(Integer playerId) throws SQLException {
        incrementPlayerWins(playerId);
        incrementPlayerMatches(playerId);
        incrementPlayerMatchesAfterDebut(playerId);
        calculatePlayerWinPercentage(playerId);
        calculatePlayerScore(playerId);
        //TODO: Update Date
    }

    public void addPlayerDraw(Integer playerId) throws SQLException {
        incrementPlayerDraws(playerId);
        incrementPlayerMatches(playerId);
        incrementPlayerMatchesAfterDebut(playerId);
        calculatePlayerWinPercentage(playerId);
        calculatePlayerScore(playerId);
        //TODO: Update Date
    }

    public void addPlayerDefeat(Integer playerId) throws SQLException {
        incrementPlayerDefeats(playerId);
        incrementPlayerMatches(playerId);
        incrementPlayerMatchesAfterDebut(playerId);
        calculatePlayerWinPercentage(playerId);
        calculatePlayerScore(playerId);
        //TODO: Update Date
    }

    public void addPlayerAbsence(Integer playerId) throws SQLException {
        if(getPlayerMatchesById(playerId) == 0)
            return;
        incrementPlayerMatchesAfterDebut(playerId);
        calculatePlayerScore(playerId);
        //TODO: Update Date
    }

    /*
     *   INDIVIDUAL RESULTS
     */
    public long insertIndividualResult(Integer playerId, Integer teamId, Integer matchday, MyApplication.ResultType result) throws SQLException {
        if(getPlayerMatchesById(playerId) == 0 && result.equals(MyApplication.ResultType.Absence))  // if player hasn't debuted yet and this is another absence, do not add it
            return 0;
        String res = result.toString();
        if(result.equals(MyApplication.ResultType.Absence)){
            Integer absenceStreak = getAbsenceStreak(playerId)+1;   //+1 because this result is not yet in the DB
            Log.d(TAG, "insertIndividualResult() streak: "+absenceStreak);
            if(absenceStreak > Integer.parseInt(sharedPreferences.getString("KEY_PREF_MEDIUM_ABSENCE_DURATION", "3")))
                res = MyApplication.ResultType.MediumAbsence.toString();
            if(absenceStreak > Integer.parseInt(sharedPreferences.getString("KEY_PREF_LONG_ABSENCE_DURATION", "10")))
                res = MyApplication.ResultType.LongAbsence.toString();
        }
        ContentValues values = new ContentValues();
        values.put(INDIVIDUAL_RESULTS_COLUMN_PLAYER_ID, playerId);
        values.put(INDIVIDUAL_RESULTS_COLUMN_TEAM_ID, teamId);
        values.put(INDIVIDUAL_RESULTS_COLUMN_MATCHDAY, matchday);
        values.put(INDIVIDUAL_RESULTS_COLUMN_RESULT, res);
        Long tsLong = System.currentTimeMillis() / 1000;
        values.put(INDIVIDUAL_RESULTS_COLUMN_MATCHDAY_DATE, tsLong);
        values.put(INDIVIDUAL_RESULTS_COLUMN_UPDATE_DATE, tsLong);
        return db.insertOrThrow(INDIVIDUAL_RESULTS_TABLE, null, values);
    }

    private ArrayList<Integer> getIntegerIndividualResultsByPlayerId(Integer playerId){
        //Log.i(TAG, "getIntegerIndividualResultsByPlayerId()");
        ArrayList<Integer> resInteger = new ArrayList<>();
        ArrayList<String> resString = getStringIndividualResultsByPlayerId(playerId);
        for(String res : resString)
            resInteger.add(resultToInteger(res));
        return resInteger;
    }

    private ArrayList<String> getStringIndividualResultsByPlayerId(Integer playerId){
        //Log.i(TAG, "getStringIndividualResultsByPlayerId()");
        ArrayList<String> resString = new ArrayList<>();
        String[] projection = {INDIVIDUAL_RESULTS_COLUMN_RESULT};
        String selection = INDIVIDUAL_RESULTS_COLUMN_PLAYER_ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        String sortOrder = INDIVIDUAL_RESULTS_COLUMN_MATCHDAY + " ASC";
        Cursor c = db.query(INDIVIDUAL_RESULTS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        if (c.moveToFirst()){
            do{
                resString.add(c.getString(c.getColumnIndexOrThrow(INDIVIDUAL_RESULTS_COLUMN_RESULT)));
            }while(c.moveToNext());
        }
        c.close();
        return resString;
    }

    private long deleteAllIndividualResultsByPlayerIdAndTeamId(Integer playerId, Integer teamId) throws PlayerNotFoundException {
        String selection = INDIVIDUAL_RESULTS_COLUMN_PLAYER_ID + " = ? AND " + INDIVIDUAL_RESULTS_COLUMN_TEAM_ID + " = ?";
        String[] selectionArgs = {playerId.toString(), teamId.toString()};
        return db.delete(INDIVIDUAL_RESULTS_TABLE, selection, selectionArgs);
    }

    private long deleteAllIndividualResultsByTeamId(Integer teamId) {
        String selection = INDIVIDUAL_RESULTS_COLUMN_TEAM_ID + " = ?";
        String[] selectionArgs = {teamId.toString()};
        return db.delete(INDIVIDUAL_RESULTS_TABLE, selection, selectionArgs);
    }

    private Integer resultToInteger(String result){
        switch (result){
            case "W":
                return Integer.parseInt(sharedPreferences.getString("KEY_PREF_WIN_SCORE", "2"));
            case "D":
                return Integer.parseInt(sharedPreferences.getString("KEY_PREF_DRAW_SCORE", "1"));
            case "L":
                return Integer.parseInt(sharedPreferences.getString("KEY_PREF_DEFEAT_SCORE", "-2"));
            case "-":
            default:
                return Integer.parseInt(sharedPreferences.getString("KEY_PREF_SHORT_ABSENCE_SCORE", "0"));
            case "+":
                return Integer.parseInt(sharedPreferences.getString("KEY_PREF_MEDIUM_ABSENCE_SCORE", "-1"));
            case "*":
                return Integer.parseInt(sharedPreferences.getString("KEY_PREF_LONG_ABSENCE_SCORE", "-2"));
        }
    }

    private Integer getAbsenceStreak(Integer playerId) throws SQLException {
        Log.i(TAG, "getStringIndividualResultsByPlayerId()");
        ArrayList<String> resString = getStringIndividualResultsByPlayerId(playerId);
        String[] projection = {INDIVIDUAL_RESULTS_COLUMN_RESULT};
        String selection = INDIVIDUAL_RESULTS_COLUMN_PLAYER_ID + " = ?";
        String[] selectionArgs = {playerId.toString()};
        Cursor c = db.query(INDIVIDUAL_RESULTS_TABLE, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()){
            do{
                resString.add(c.getString(c.getColumnIndexOrThrow(INDIVIDUAL_RESULTS_COLUMN_RESULT)));
            }while(c.moveToNext());
        }
        c.close();
        Integer streak = 0;
        for(String res : resString){
            switch(res){
                case "W":
                case "D":
                case "L":
                    streak = 0;
                    break;
                case "-":
                case "+":
                case "*":
                default:
                    ++streak;
            }
        }
        return streak;
    }
}
