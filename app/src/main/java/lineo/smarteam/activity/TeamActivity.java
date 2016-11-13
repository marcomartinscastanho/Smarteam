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
import lineo.smarteam.db.Players;
import lineo.smarteam.exception.PlayerAlreadyExistsException;
import lineo.smarteam.exception.PlayerNotFoundException;

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
    private Players playersDb;
    private int selectedPlayer = -1;

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

        playersDb = new Players(context);
        try {
            playersDb = playersDb.open();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.wtf(TAG, "onStart() Players DB failed to open");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        if(playersDb.isEmptyByTeamId(teamId)){
            MyApplication.showToast(context, getResources().getString(R.string.toastNoPlayersInit));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
        playersDb.close();
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
        if(v.equals(addResultButton)){
            Log.i(TAG, "onClick(View v) - Add Result button clicked");
            //insertResultButtonClick();
        }
        else if(v.equals(deleteLastResultButton)){
            Log.i(TAG, "onClick(View v) - Delete Last Result button clicked");
            //deleteLastResultButtonClick();
        }
        else if(v.equals(addPlayerButton)){
            Log.i(TAG, "onClick(View v) - Add Player button clicked");
            addPlayerButtonClick();
        }
        else if(v.equals(deletePlayerButton)){
            Log.i(TAG, "onClick(View v) - Delete Player button clicked");
            deletePlayerButtonClick();
        }
        else if(v.equals(generateLineupsButton)){
            Log.i(TAG, "onClick(View v) - Generate Lineups button clicked");
            //generateLineupsButtonClick();
        }
        else if(v.equals(rankingButton)){
            Log.i(TAG, "onClick(View v) - Ranking button clicked");
            //rankingButtonClick();
        }
        else if(v.equals(statisticsButton)){
            Log.i(TAG, "onClick(View v) - Statistics button clicked");
            //statisticsButtonClick();
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

        if(playersDb.isEmptyByTeamId(teamId)){
            MyApplication.showToast(context, getResources().getString(R.string.toastNoPlayersToDelete));
            return;
        }
        ArrayList<String> playersNamesList = playersDb.getPlayersNamesByTeamId(teamId);
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
                        try {
                            playersDb.deleteTeamByNameAndTeamId(choiceList[selectedPlayer].toString(), teamId);
                            MyApplication.showToast(context, String.format("%s%s%s", getResources().getString(R.string.toastSuccessfullyDeletedPlayerPrefix), choiceList[selectedPlayer], getResources().getString(R.string.toastSuccessfullyDeletedPlayerSuffix)));
                        } catch (PlayerNotFoundException e) {
                            e.printStackTrace();
                            MyApplication.showToast(context, String.format("%s%s%s", getResources().getString(R.string.toastFailedToDeletePlayerPrefix), choiceList[selectedPlayer], getResources().getString(R.string.toastFailedToDeletePlayerSuffix)));
                        }
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
                playersDb.insertPlayer(name, teamId);
            } catch (PlayerAlreadyExistsException e) {
                MyApplication.showToast(context, getResources().getString(R.string.toastPlayerAlreadyExists));
                return false;
            }
            return true;
        }
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
