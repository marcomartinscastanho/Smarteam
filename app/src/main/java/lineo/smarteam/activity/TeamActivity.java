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
import android.widget.EditText;
import java.sql.SQLException;
import java.util.ArrayList;

import lineo.smarteam.MyApplication;
import lineo.smarteam.R;
import lineo.smarteam.exception.PlayerAlreadyExistsException;
import lineo.smarteam.exception.TeamNotFoundException;

public class TeamActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "TeamActivity";
    private Context context;

    private Button addResultButton;
    private Button deleteLastResultButton;
    private Button addPlayerButton;
    private Button deletePlayerButton;
    private Button generateLineupsButton;
    private Button rankingButton;
    private Button statisticsButton;

    private Integer teamId;
    private int selectedPlayer = -1;

    // player selection indexes
    final ArrayList<Integer> selectedPlayersIndexList = new ArrayList<>();
    final ArrayList<Integer> selectedWinnersIndexList = new ArrayList<>();
    final ArrayList<Integer> selectedLosersIndexList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        context=this;
        setLayout();
        setTeamNameOnActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        if(MyApplication.db.isPlayersEmptyByTeamId(teamId)){
            MyApplication.showToast(context, getResources().getString(R.string.toastNoPlayersInit));
        }
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
        Log.i(TAG, "onClick()");
        if(v.equals(addPlayerButton)){
            addPlayerButtonClick();
        }
        else if(v.equals(deletePlayerButton)){
            deletePlayerButtonClick();
        }
        else if(v.equals(addResultButton)){
            insertResultButtonClick();
        }
        else if(v.equals(deleteLastResultButton)) {
            deleteLastResultButtonClick();
        }
        else if(v.equals(generateLineupsButton)){
            generateLineupsButtonClick();
        }
        else if(v.equals(rankingButton)){
            rankingButtonClick();
        }
        else if(v.equals(statisticsButton)){
            statisticsButtonClick();
        }
    }

    private void addPlayerButtonClick() {
        Log.i(TAG, "addPlayerButtonClick()");

        AlertDialog.Builder addPlayerBuilder = new AlertDialog.Builder(context);
        addPlayerBuilder.setTitle(getResources().getString(R.string.dialogAddPlayerTitle));
        addPlayerBuilder.setCancelable(false);
        EditText editTextAddPlayer = new EditText(this);
        addPlayerBuilder.setView(editTextAddPlayer);
        addPlayerBuilder.setCancelable(true);
        addPlayerBuilder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }
        );
        addPlayerBuilder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }
        );
        AlertDialog addDialog = addPlayerBuilder.create();
        addDialog.show();
        Button okButton = addDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setOnClickListener(new TeamActivity.AddPlayerDialogListener(addDialog, editTextAddPlayer));
    }

    private void deletePlayerButtonClick() {
        Log.i(TAG, "deletePlayerButtonClick()");
        selectedPlayer = -1;

        if(MyApplication.db.isPlayersEmptyByTeamId(teamId)){
            MyApplication.showToast(context, getResources().getString(R.string.toastNoPlayersToDelete));
            return;
        }
        ArrayList<String> playersNamesList = MyApplication.db.getPlayersNamesByTeamId(teamId);
        final CharSequence[] choiceList = playersNamesList.toArray(new CharSequence[playersNamesList.size()]);
        AlertDialog.Builder deletePlayerBuilder = new AlertDialog.Builder(context);
        deletePlayerBuilder.setTitle(getResources().getString(R.string.dialogPlayerToDelete));
        deletePlayerBuilder.setSingleChoiceItems(choiceList, selectedPlayer, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedPlayer = which;
            }
        });
        deletePlayerBuilder.setCancelable(true);
        deletePlayerBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Which value=" + which);
                Log.d(TAG, "Selected value=" + selectedPlayer);
                if(selectedPlayer<0)
                    return;
                // Are you sure you want to delete?
                AlertDialog.Builder builderAreYouSure = new AlertDialog.Builder(context);
                builderAreYouSure.setTitle(getResources().getString(R.string.dialogAreYouSureDeletePlayerPrefix) + choiceList[selectedPlayer] + getResources().getString(R.string.dialogAreYouSureDeletePlayerSuffix));
                builderAreYouSure.setCancelable(true);
                builderAreYouSure.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "deleteButtonClick() - Deleting player " + choiceList[selectedPlayer]);
                        MyApplication.db.beginTransaction();
                        try{
                            MyApplication.db.deletePlayerByNameAndTeamId(choiceList[selectedPlayer].toString(), teamId);
                            MyApplication.db.setTransactionSuccessful();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Log.wtf(TAG, "SqlException");
                        } finally {
                            Log.d(TAG, "deleteButtonClick() - end Transaction");
                            MyApplication.db.endTransaction();
                        }
                        MyApplication.showToast(context, String.format("%s%s%s", getResources().getString(R.string.toastSuccessfullyDeletedPlayerPrefix), choiceList[selectedPlayer], getResources().getString(R.string.toastSuccessfullyDeletedPlayerSuffix)));
                    }
                });
                builderAreYouSure.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog areYouSureDialog = builderAreYouSure.create();
                areYouSureDialog.show();
            }
        });
        deletePlayerBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog deleteDialog = deletePlayerBuilder.create();
        deleteDialog.show();
    }

    private void insertResultButtonClick() {
        Log.i(TAG, "insertResultButtonClick()");
        if(MyApplication.db.getPlayersCountByTeamId(teamId) < getResources().getInteger(R.integer.minPlayersPerMatch)){
            MyApplication.showToast(context, getResources().getString(R.string.toastNotEnoughPlayersInTeam)+getResources().getInteger(R.integer.minPlayersPerMatch));
            return;
        }
        AlertDialog.Builder builderResult = new AlertDialog.Builder(context);
        builderResult.setTitle(getResources().getString(R.string.dialogInsertResult));
        builderResult.setCancelable(true);
        builderResult.setPositiveButton(R.string.draw, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                insertResultDraw();
            }
        });
        builderResult.setNegativeButton(R.string.winDefeat, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                insertResultWinDefeat();
            }
        });
        AlertDialog dialogResult = builderResult.create();
        dialogResult.show();
    }

    private void insertResultDraw() {
        Log.i(TAG, "insertResultDraw()");
        int playersCount = MyApplication.db.getPlayersCountByTeamId(teamId);
        final CharSequence[] choiceList = MyApplication.db.getPlayersNamesByTeamId(teamId).toArray(new CharSequence[playersCount]);
        boolean[] isSelectedArray = new boolean[playersCount];
        for(int i=0; i< playersCount; ++i)
            isSelectedArray[i] = false;
        AlertDialog.Builder builderDraw = new AlertDialog.Builder(context);
        builderDraw.setTitle(getResources().getString(R.string.dialogSelectPlayersDraw));
        builderDraw.setMultiChoiceItems(choiceList, isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    selectedPlayersIndexList.add(which);
                } else if (selectedPlayersIndexList.contains(which)) {
                    selectedPlayersIndexList.remove(Integer.valueOf(which));
                }
            }
        });
        builderDraw.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builderDraw.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedPlayersIndexList.clear();
            }
        });
        AlertDialog dialogDraw = builderDraw.create();
        dialogDraw.show();
        Button okButton = dialogDraw.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setOnClickListener(new InsertResultDrawDialogListener(dialogDraw));
    }

    private void insertResultWinDefeat() {
        Log.i(TAG, "insertResultWinDefeat()");
    }

    private void deleteLastResultButtonClick() {

    }

    private void generateLineupsButtonClick() {

    }

    private void rankingButtonClick() {

    }

    private void statisticsButtonClick() {

    }

    public class AddPlayerDialogListener implements View.OnClickListener {
        private final Dialog dialog;
        private final EditText editTextAddPlayer;
        AddPlayerDialogListener(Dialog dialog, EditText editText) {
            this.dialog = dialog;
            this.editTextAddPlayer = editText;
        }
        @Override
        public void onClick(View v) {
            CharSequence selectedPlayerName = editTextAddPlayer.getText();
            if(validate(selectedPlayerName.toString())){
                dialog.dismiss();
            }
        }

        boolean validate(String name){
            if(name.length()<getResources().getInteger(R.integer.minCharsPlayerName)){
                MyApplication.showToast(context, getResources().getString(R.string.toastPlayerNameTooShort));
                return false;
            }
            if(name.length()>getResources().getInteger(R.integer.maxCharsPlayerName)){
                MyApplication.showToast(context, getResources().getString(R.string.toastPlayerNameTooLong));
                return false;
            }
            try {
                MyApplication.db.insertPlayer(name, teamId);
            } catch (PlayerAlreadyExistsException e) {
                MyApplication.showToast(context, getResources().getString(R.string.toastPlayerAlreadyExists));
                return false;
            }
            return true;
        }
    }

    public class InsertResultDrawDialogListener implements View.OnClickListener {
        private final Dialog dialog;
        InsertResultDrawDialogListener(Dialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {
            if(!isSelectionValid())
                return;
            AlertDialog.Builder builderAreYouSure = new AlertDialog.Builder(context);
            builderAreYouSure.setTitle(getResources().getString(R.string.dialogSelectPlayersDrawAreYouSurePrefix)+selectedPlayersIndexList.size()+getResources().getString(R.string.dialogSelectPlayersDrawAreYouSureSuffix));
            builderAreYouSure.setCancelable(false);
            builderAreYouSure.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogAreYouSure, int which) {
                    dialog.dismiss();
                    if(insertResult(MyApplication.ResultType.Draw)){
                        rankingButtonClick();
                    }
                    else{
                        MyApplication.showToast(context, getResources().getString(R.string.toastFailedToAddResult));
                    }
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

    private boolean insertResult(ArrayList<Integer> selectionIndexes, MyApplication.ResultType resultType){
        Integer matchday;
        try {
            matchday = teamsDb.getNumMatchesById(teamId) +1;
        } catch (TeamNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        ArrayList<Integer> playersIds =  playersDb.getPlayersIdsByTeamId(teamId);
        for(Integer playerIndexList : selectionIndexes){
            try {
                resultsDb.insertIndividualResult(playersIds.get(playerIndexList), teamId, matchday, resultType);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void setLayout(){
        setContentView(R.layout.activity_team);
        addResultButton = (Button) findViewById(R.id.team_buttonInsertResult);
        deleteLastResultButton = (Button) findViewById(R.id.team_buttonDeleteLastResult);
        addPlayerButton = (Button) findViewById(R.id.team_buttonInsertPlayer);
        deletePlayerButton = (Button) findViewById(R.id.team_buttonDeletePlayer);
        generateLineupsButton = (Button) findViewById(R.id.team_buttonGenerateLineups);
        rankingButton = (Button) findViewById(R.id.team_buttonRanking);
        statisticsButton = (Button) findViewById(R.id.team_buttonStatistics);
        addResultButton.setOnClickListener(this);
        deleteLastResultButton.setOnClickListener(this);
        addPlayerButton.setOnClickListener(this);
        deletePlayerButton.setOnClickListener(this);
        generateLineupsButton.setOnClickListener(this);
        rankingButton.setOnClickListener(this);
        statisticsButton.setOnClickListener(this);
    }

    private void setTeamNameOnActionBar(){
        Intent intent = getIntent();
        String teamName = intent.getStringExtra("teamName");
        this.teamId = intent.getIntExtra("teamId", -1);
        if(teamId==-1){
            Log.wtf(TAG, "onCreate() failed to pass teamId to TeamActivity");
            MyApplication.showToast(context, getResources().getString(R.string.toastFailedToLoadTeam));
            finish();
        }
        ActionBar ab = getActionBar();
        if (ab != null)
            ab.setTitle(String.format("\t%s", teamName));
    }
}
