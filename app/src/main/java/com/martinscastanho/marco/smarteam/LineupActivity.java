package com.martinscastanho.marco.smarteam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.martinscastanho.marco.smarteam.database.DataBase;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

public class LineupActivity extends AppCompatActivity {
    static DataBase db;
    static Integer teamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lineup);

        db = new DataBase(getApplicationContext());

        setTitle("Lineup");
        setLayout();
    }

    private void setLayout(){
        Intent intent = getIntent();
        teamId = intent.getIntExtra("teamId", -1);
        ArrayList<String> selectedPlayersNames = intent.getStringArrayListExtra("selectedPlayersNames");
        if(teamId == -1 || selectedPlayersNames == null){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("Selected Players", String.valueOf(selectedPlayersNames));

        // generate lineups and set the layout
        new GenerateLinuepsTask(this).execute(selectedPlayersNames.toArray(new String[0]));
    }

    private static class GenerateLinuepsTask extends AsyncTask<String, Void, LineupWrapper> {
        private WeakReference<LineupActivity> activityReference;

        // only retain a weak reference to the activity
        GenerateLinuepsTask(LineupActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected LineupWrapper doInBackground(String... selectedPlayersNames) {
            return new LineupGenerator(selectedPlayersNames).generate();
        }

        @Override
        protected void onPostExecute(LineupWrapper lineupWrapper) {
            super.onPostExecute(lineupWrapper);

            // get a reference to the activity if it is still there
            LineupActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.setLayoutScores(lineupWrapper.getHomeSideScore(), lineupWrapper.getAwaySideScore());
            activity.inflateSidesLists(lineupWrapper.getHomePlayerIds(), lineupWrapper.getAwayPlayerIds());
        }

        private class LineupGenerator {
            private int maxPlayersPerSide;
            private double tempScoreGap;
            ArrayList<Map<String, Integer>> selectedPlayers;

            private ArrayList<Map<String, Integer>> tempHomePlayers;
            private ArrayList<Map<String, Integer>> tempAwayPlayers;
            private ArrayList<Map<String, Integer>> tempBestSolutionHomePlayers;
            private ArrayList<Map<String, Integer>> tempBestSolutionAwayPlayers;

            LineupGenerator(String[] selectedPlayersNames) {
                selectedPlayers = db.getPlayersIdsAndScores(selectedPlayersNames, teamId);
                Log.d("Selected Players Ids", String.valueOf(selectedPlayers));
                maxPlayersPerSide = (selectedPlayers.size() / 2) + (selectedPlayers.size() % 2);
                // scoreGap between Sides is initialized with its highest possible value: 100*numPlayers
                tempScoreGap =  Integer.valueOf(100 * selectedPlayers.size()).doubleValue();

                tempBestSolutionHomePlayers = new ArrayList<>(maxPlayersPerSide);
                tempBestSolutionAwayPlayers = new ArrayList<>(maxPlayersPerSide);
                //+1 because partition adds before checking if it's possible
                tempHomePlayers = new ArrayList<>(maxPlayersPerSide + 1);
                tempAwayPlayers = new ArrayList<>(maxPlayersPerSide + 1);
            }

            private void partition(int iPlayer, double tempScoreHome, double tempScoreAway){
                if(tempHomePlayers.size() > maxPlayersPerSide || tempAwayPlayers.size() > maxPlayersPerSide){
                    // if we just added a player to an already full side, discard this solution and go back
                    return;
                }

                if(iPlayer == selectedPlayers.size()) {
                    // we are allocating the last player on the list
                    double newScoreGap = Math.abs(tempScoreHome - tempScoreAway);
                    if(newScoreGap < tempScoreGap) {
                        // if the solution we just found if better than the best so far
                        // (i.e. has a lower scoreGap between sides),
                        // then the new solution becomes the best so far
                        tempBestSolutionHomePlayers = new ArrayList<>(tempHomePlayers);
                        tempBestSolutionAwayPlayers = new ArrayList<>(tempAwayPlayers);
                        tempScoreGap = newScoreGap;
                    }
                    return;
                }

                // safe validation that we can retrieve the player score
                Integer playerScore = selectedPlayers.get(iPlayer).get("score");
                if(playerScore == null){
                    return;
                }

                // try adding this Player to the Home side
                tempHomePlayers.add(selectedPlayers.get(iPlayer));
                partition(iPlayer + 1, tempScoreHome + playerScore, tempScoreAway);
                if(tempScoreGap == 0.0){
                    // if the gap is 0, we found an optimal solution
                    return;
                }

                // revert adding this Player to the Home side
                tempHomePlayers.remove(tempHomePlayers.size() - 1);
                // and instead try adding it to the Away side
                tempAwayPlayers.add(selectedPlayers.get(iPlayer));
                partition(iPlayer + 1, tempScoreHome, tempScoreAway + playerScore);
                if(tempScoreGap == 0.0){
                    // if the gap is 0, we found an optimal solution
                    return;
                }

                // revert adding this Player to the Away side
                tempAwayPlayers.remove(tempAwayPlayers.size() - 1);
            }

            LineupWrapper generate(){
                partition(0, 0.0, 0.0);
                return new LineupWrapper(tempBestSolutionHomePlayers, tempBestSolutionAwayPlayers);
            }
        }
    }

    private static class LineupWrapper {
        ArrayList<Integer> homePlayerIds;
        ArrayList<Integer> awayPlayerIds;
        Integer homeSideScore;
        Integer awaySideScore;

        LineupWrapper(ArrayList<Map<String, Integer>> homePlayers, ArrayList<Map<String, Integer>> awayPlayers) {
            homeSideScore = 0;
            homePlayerIds = new ArrayList<>();
            for(Map<String, Integer> homePlayer : homePlayers){
                homePlayerIds.add(homePlayer.get("id"));
                homeSideScore += homePlayer.get("score");
            }

            awaySideScore = 0;
            awayPlayerIds = new ArrayList<>();
            for(Map<String, Integer> homePlayer : awayPlayers){
                awayPlayerIds.add(homePlayer.get("id"));
                awaySideScore += homePlayer.get("score");
            }
        }

        ArrayList<Integer> getHomePlayerIds() {
            return homePlayerIds;
        }

        ArrayList<Integer> getAwayPlayerIds() {
            return awayPlayerIds;
        }

        Integer getHomeSideScore() {
            return homeSideScore;
        }

        Integer getAwaySideScore() {
            return awaySideScore;
        }
    }

    private void setLayoutScores(int homeScore, int awayScore){
        TextView homeScoreTextView = findViewById(R.id.homeSideScore);
        TextView awayScoreTextView = findViewById(R.id.awaySideScore);

        homeScoreTextView.setText(String.format("%s", homeScore));
        awayScoreTextView.setText(String.format("%s", awayScore));
    }

    private void inflateSidesLists(ArrayList<Integer> homePlayersIds, ArrayList<Integer> awayPlayersIds){
        ListView homeSideListView = findViewById(R.id.homeSideListView);
        ListView awaySideListView = findViewById(R.id.awaySideListView);

        Log.d("homePlayersIds", String.valueOf(homePlayersIds));
        Log.d("awayPlayersIds", String.valueOf(awayPlayersIds));

        Cursor mCursor = db.getSideLineup(homePlayersIds);
        ListAdapter homeSideListAdapter = new SimpleCursorAdapter(this, R.layout.lineup_row_home, mCursor, new String[] {DataBase.Player.COLUMN_NAME_NAME, DataBase.Player.COLUMN_NAME_MATCHES, DataBase.Player.COLUMN_NAME_SCORE}, new int[]{R.id.playerNameRowHome, R.id.playerMatchesRowHome, R.id.playerScoreRowHome}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        mCursor = db.getSideLineup(awayPlayersIds);
        ListAdapter awaySideListAdapter = new SimpleCursorAdapter(this, R.layout.lineup_row_away, mCursor, new String[] {DataBase.Player.COLUMN_NAME_NAME, DataBase.Player.COLUMN_NAME_MATCHES, DataBase.Player.COLUMN_NAME_SCORE}, new int[]{R.id.playerNameRowAway, R.id.playerMatchesRowAway, R.id.playerScoreRowAway}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        homeSideListView.setAdapter(homeSideListAdapter);
        awaySideListView.setAdapter(awaySideListAdapter);
    }

    public void homeSideScoreLegend(View view) {
        Toast.makeText(LineupActivity.this, "Total Score of Home side", Toast.LENGTH_SHORT).show();
    }

    public void awaySideScoreLegend(View view) {
        Toast.makeText(LineupActivity.this, "Total Score of Away side", Toast.LENGTH_SHORT).show();
    }

    public void matchesHeaderLegend(View view){
        Toast.makeText(LineupActivity.this, "M: Matches played", Toast.LENGTH_SHORT).show();
    }

    public void scoreHeaderLegend(View view){
        Toast.makeText(LineupActivity.this, "S: Score", Toast.LENGTH_SHORT).show();
    }
}
