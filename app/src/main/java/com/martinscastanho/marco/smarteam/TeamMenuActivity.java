package com.martinscastanho.marco.smarteam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.martinscastanho.marco.smarteam.database.DataBase;

import java.util.ArrayList;
import java.util.Arrays;

public class TeamMenuActivity extends AppCompatActivity {
    Integer teamId;
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_menu);

        Intent intent = getIntent();
        String teamName = intent.getStringExtra("teamName");
        setTitle(teamName);

        db = new DataBase(getApplicationContext());
        teamId = db.getTeamId(teamName);
    }

    public void addResultButtonClick(View view){
        final ArrayList<String> playersList = db.getPlayersNames(teamId);
        if(playersList.isEmpty()){
            Toast.makeText(TeamMenuActivity.this, "Add at least 4 players", Toast.LENGTH_SHORT).show();
            return;
        }
        if(playersList.size() < 4){
            Toast.makeText(TeamMenuActivity.this, String.format("Add %s players more", 4 - playersList.size()), Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder resultAlert = new AlertDialog.Builder(this);
        resultAlert.setTitle(getResources().getString(R.string.resultTypeDialog));
        resultAlert.setPositiveButton(R.string.winDefeatResult, new WinDefeatResultClickListener());
        resultAlert.setNegativeButton(R.string.drawResult, new DrawResultClickListener());
        resultAlert.show();
    }

    private class DrawResultClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            final ArrayList<String> playersList = db.getPlayersNames(teamId);
            final String[] choiceList = playersList.toArray(new String[0]);
            final boolean[] isSelectedArray = new boolean[playersList.size()];

            AlertDialog.Builder drawAlert = new AlertDialog.Builder(TeamMenuActivity.this);
            drawAlert.setTitle(getResources().getString(R.string.drawPlayersSelectionTitle));
            drawAlert.setMultiChoiceItems(choiceList, isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if(getNumberSelected(isSelectedArray) >= 4){
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                    else{
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                }
            });
            drawAlert.setCancelable(false);
            drawAlert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("Final Selected Players", Arrays.toString(isSelectedArray));

                    int numPlayersSelected = getNumberSelected(isSelectedArray);

                    final ArrayList<String> selectedPlayers = new ArrayList<>();
                    for(int i=0; i<isSelectedArray.length; i++){
                        if(isSelectedArray[i])
                            selectedPlayers.add(playersList.get(i));
                    }

                    AlertDialog.Builder confirmResultAlert = new AlertDialog.Builder(TeamMenuActivity.this);
                    confirmResultAlert.setTitle(String.format("Confirm %svs%s Draw?", numPlayersSelected/2 + (numPlayersSelected%2), numPlayersSelected/2));
                    confirmResultAlert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.addMatch(teamId, selectedPlayers, null, null);
                            callRankingActivity();
                        }
                    });
                    confirmResultAlert.setNegativeButton(android.R.string.no, null);
                    confirmResultAlert.show();
                }
            });
            AlertDialog drawDialog = drawAlert.create();
            drawDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            });
            drawDialog.show();
        }
    }

    private class WinDefeatResultClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            final ArrayList<String> playersList = db.getPlayersNames(teamId);
            final String[] choiceWinnersList = playersList.toArray(new String[0]);
            final boolean[] isSelectedWinnersArray = new boolean[playersList.size()];

            AlertDialog.Builder winAlert = new AlertDialog.Builder(TeamMenuActivity.this);
            winAlert.setTitle(getResources().getString(R.string.winnersSelectionTitle));
            winAlert.setMultiChoiceItems(choiceWinnersList, isSelectedWinnersArray, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if(getNumberSelected(isSelectedWinnersArray) >= 2){
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                    else{
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                }
            });
            winAlert.setCancelable(false);
            winAlert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("Final Winners Selection", Arrays.toString(isSelectedWinnersArray));
                    final int numWinnersSelected = getNumberSelected(isSelectedWinnersArray);

                    // list of winners
                    final ArrayList<String> winners = new ArrayList<>();
                    for(int i=0; i<isSelectedWinnersArray.length; i++){
                        if(isSelectedWinnersArray[i])
                            winners.add(playersList.get(i));
                    }

                    // now do the same with remaining list of players, to ge the losers
                    final ArrayList<String> remainingPlayersList = new ArrayList<>();
                    for(int i=0; i<isSelectedWinnersArray.length; i++){
                        if(!isSelectedWinnersArray[i])
                            remainingPlayersList.add(playersList.get(i));
                    }
                    final String[] choiceLosersList = remainingPlayersList.toArray(new String[0]);
                    final boolean[] isSelectedLosersArray = new boolean[remainingPlayersList.size()];

                    AlertDialog.Builder defeatAlert = new AlertDialog.Builder(TeamMenuActivity.this);
                    defeatAlert.setTitle(getResources().getString(R.string.losersSelectionTitle));
                    defeatAlert.setMultiChoiceItems(choiceLosersList, isSelectedLosersArray, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if(getNumberSelected(isSelectedLosersArray) >= numWinnersSelected-1 && getNumberSelected(isSelectedLosersArray) <= numWinnersSelected+1){
                                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            }
                            else{
                                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            }
                        }
                    });
                    defeatAlert.setCancelable(false);
                    defeatAlert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("Final Losers Selection", Arrays.toString(isSelectedLosersArray));
                            final int numLosersSelected = getNumberSelected(isSelectedLosersArray);

                            final ArrayList<String> losers = new ArrayList<>();
                            for(int i=0; i<isSelectedLosersArray.length; i++){
                                if(isSelectedLosersArray[i])
                                    losers.add(remainingPlayersList.get(i));
                            }

                            AlertDialog.Builder confirmResultAlert = new AlertDialog.Builder(TeamMenuActivity.this);
                            confirmResultAlert.setTitle(String.format("Confirm %svs%s match?", numWinnersSelected, numLosersSelected));
                            confirmResultAlert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.addMatch(teamId,null, winners, losers);
                                    callRankingActivity();
                                }
                            });
                            confirmResultAlert.setNegativeButton(android.R.string.no, null);
                            confirmResultAlert.show();
                        }
                    });
                    AlertDialog defeatDialog = defeatAlert.create();
                    defeatDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                    });
                    defeatDialog.show();
                }
            });
            AlertDialog winDialog = winAlert.create();
            winDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            });
            winDialog.show();
        }
    }

    public void showRanking(View view){
        callRankingActivity();
    }

    public void generateLineup(View view){
        //TODO: this
    }

    public void showStatistics(View view){
        //TODO: this
    }

    public void editTeam(View view){
        Intent editTeamMenuIntent = new Intent(getApplicationContext(), EditTeamMenuActivity.class);
        editTeamMenuIntent.putExtra("teamId", teamId);
        startActivity(editTeamMenuIntent);
    }

    public void callRankingActivity(){
        Intent rankingMenuIntent = new Intent(getApplicationContext(), RankingActivity.class);
        rankingMenuIntent.putExtra("teamId", teamId);
        startActivity(rankingMenuIntent);
    }

    // HELPERS

    public static int getNumberSelected(boolean[] selectedArray){
        int numPlayersSelected = 0;
        for (boolean isSelected : selectedArray) {
            numPlayersSelected += (isSelected ? 1 : 0);
        }
        return numPlayersSelected;
    }

}
