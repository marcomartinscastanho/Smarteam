package lineo.smarteam.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import lineo.smarteam.MyApplication;
import lineo.smarteam.R;
import lineo.smarteam.exception.TeamNotFoundException;

public class TeamActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "TeamActivity";
    private Context context;

    private Button resultsButton;
    private Button rankingButton;
    private Button lineupsButton;
    private Button statisticsButton;
    private Button editButton;

    final ArrayList<Integer> selectedPlayersIndexList = new ArrayList<>();

    private Integer teamId;
    private String teamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        context=this;
        setLayout();
        getTeamIdFromIntent();
        checkMinPlayers();
    }

    private void setLayout(){
        setContentView(R.layout.activity_team);
        resultsButton = (Button) findViewById(R.id.team_buttonResults);
        rankingButton = (Button) findViewById(R.id.team_buttonRanking);
        lineupsButton = (Button) findViewById(R.id.team_buttonLineups);
        statisticsButton = (Button) findViewById(R.id.team_buttonStatistics);
        editButton = (Button) findViewById(R.id.team_buttonEdit);
        resultsButton.setOnClickListener(this);
        rankingButton.setOnClickListener(this);
        lineupsButton.setOnClickListener(this);
        statisticsButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
    }

    private void getTeamIdFromIntent(){
        Intent intent = getIntent();
        teamName = intent.getStringExtra("teamName");
        this.teamId = intent.getIntExtra("teamId", -1);
        if(teamId==-1){
            Log.wtf(TAG, "onCreate() failed to pass teamId to TeamActivity");
            MyApplication.showToast(context, getResources().getString(R.string.toastFailedToLoadTeam));
            finish();
        }
    }

    private void setTeamNameOnActionBar(String name){
        ActionBar ab = getActionBar();
        if (ab != null)
            ab.setTitle(String.format("%s", name));
    }

    private void checkMinPlayers(){
        if(MyApplication.db.getPlayersCountByTeamId(teamId) < getResources().getInteger(R.integer.minPlayersPerMatch))
            callEditActivity();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart()");
        try {
            teamName=MyApplication.db.getTeamNameById(teamId);
        } catch (TeamNotFoundException e) {
            e.printStackTrace();
            Log.wtf(TAG, "onRestart() did not find team "+teamId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
        setTeamNameOnActionBar(teamName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        //Log.i(TAG, "onClick()");
        if(v.equals(resultsButton)){
            resultsButtonClick();
        }
        else if(v.equals(rankingButton)){
            rankingButtonClick();
        }
        else if(v.equals(lineupsButton)){
            lineupsButtonClick();
        }
        else if(v.equals(statisticsButton)){
            statisticsButtonClick();
        }
        else if(v.equals(editButton)){
            editButtonClick();
        }
    }

    private void resultsButtonClick() {
        callResultsActivity();
    }

    private void rankingButtonClick() {
        if(MyApplication.db.isPlayersEmptyByTeamId(teamId)){
            MyApplication.showToast(context, getResources().getString(R.string.toastNoPlayers));
            return;
        }
        callRankingActivity();
    }

    private void lineupsButtonClick() {
        Log.i(TAG, "lineupsButtonClick()");
        if(MyApplication.db.getPlayersCountByTeamId(teamId) < getResources().getInteger(R.integer.minPlayersPerMatch)){
            MyApplication.showToast(context, getResources().getString(R.string.toastNotEnoughPlayersInTeam)+getResources().getInteger(R.integer.minPlayersPerMatch));
            return;
        }
        int playersCount = MyApplication.db.getPlayersCountByTeamId(teamId);
        final CharSequence[] choiceList = MyApplication.db.getPlayersNamesByTeamId(teamId).toArray(new CharSequence[playersCount]);
        boolean[] isSelectedArray = new boolean[playersCount];
        for(int i=0; i< playersCount; ++i)
            isSelectedArray[i] = false;
        AlertDialog.Builder builderLineup = new AlertDialog.Builder(context);
        builderLineup.setTitle(getResources().getString(R.string.dialogSelectPlayersDraw));
        builderLineup.setMultiChoiceItems(choiceList, isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    selectedPlayersIndexList.add(which);
                } else if (selectedPlayersIndexList.contains(which)) {
                    selectedPlayersIndexList.remove(Integer.valueOf(which));
                }
            }
        });
        builderLineup.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builderLineup.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedPlayersIndexList.clear();
            }
        });
        AlertDialog dialogLineup = builderLineup.create();
        dialogLineup.show();
        Button okButton = dialogLineup.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setOnClickListener(new LineupsDialogListener(dialogLineup));
    }

    private class LineupsDialogListener implements View.OnClickListener {
        private final Dialog dialog;
        LineupsDialogListener(Dialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {
            if(!isSelectionValid())
                return;
            AlertDialog.Builder builderAreYouSure = new AlertDialog.Builder(context);
            builderAreYouSure.setTitle(getResources().getString(R.string.dialogSelectPlayersLineupsAreYouSurePrefix)+selectedPlayersIndexList.size()+getResources().getString(R.string.dialogSelectPlayersLineupsAreYouSureSuffix));
            builderAreYouSure.setCancelable(false);
            builderAreYouSure.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogAreYouSure, int which) {
                    dialog.dismiss();
                    callLineupsActivity();
                    selectedPlayersIndexList.clear();
                }
            });
            builderAreYouSure.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialogAreYouSure = builderAreYouSure.create();
            dialogAreYouSure.show();
        }

        private boolean isSelectionValid(){
            if(selectedPlayersIndexList.size() < getResources().getInteger(R.integer.minPlayersPerMatch)){
                MyApplication.showToast(context, getResources().getString(R.string.toastNotEnoughPlayersSelected)+getResources().getInteger(R.integer.minPlayersPerMatch));
                return false;
            }
            if(selectedPlayersIndexList.size() > getResources().getInteger(R.integer.maxPlayersPerMatch)){
                MyApplication.showToast(context, getResources().getString(R.string.toastTooManyPlayersSelected)+getResources().getInteger(R.integer.maxPlayersPerMatch));
                return false;
            }
            return true;
        }
    }

    private void statisticsButtonClick() {
        if(MyApplication.db.isPlayersEmptyByTeamId(teamId)){
            MyApplication.showToast(context, getResources().getString(R.string.toastNoPlayers));
            return;
        }
        if(!MyApplication.db.hasTeamPlayedAnyMatch(teamId)){
            MyApplication.showToast(context, getResources().getString(R.string.toastNoMatches));
            return;
        }
        callStatisticsActivity();
    }

    private void editButtonClick() {
        callEditActivity();
    }

    public void callResultsActivity(){
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("teamId", teamId);
        startActivity(intent);
    }

    public void callEditActivity(){
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("teamId", teamId);
        startActivity(intent);
    }

    public void callRankingActivity(){
        Intent intent = new Intent(this, RankingActivity.class);
        intent.putExtra("teamId", teamId);
        startActivity(intent);
    }

    private void callLineupsActivity() {
        Intent intent = new Intent(this, LineupsActivity.class);
        intent.putExtra("teamId", teamId);
        intent.putExtra("selectedPlayersIndexList", selectedPlayersIndexList);
        startActivity(intent);
    }

    public void callStatisticsActivity(){
        Intent intent = new Intent(this, StatisticsActivity.class);
        intent.putExtra("teamId", teamId);
        startActivity(intent);
    }
}
