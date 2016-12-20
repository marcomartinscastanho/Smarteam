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
import lineo.smarteam.exception.TeamAlreadyExistsException;
import lineo.smarteam.exception.TeamNotFoundException;


public class EditActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "EditActivity";
    private Context context;

    private Button renameTeamButton;
    private Button addPlayerButton;
    private Button renamePlayerButton;
    private Button deletePlayerButton;

    private Integer teamId;
    private String teamName;
    private Integer selectedPlayer = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        context=this;
        setLayout();
        getTeamIdFromIntent();
    }

    private void setLayout(){
        setContentView(R.layout.activity_edit);
        renameTeamButton = (Button) findViewById(R.id.edit_button_rename_team);
        addPlayerButton = (Button) findViewById(R.id.edit_button_add_player);
        renamePlayerButton = (Button) findViewById(R.id.edit_button_rename_player);
        deletePlayerButton = (Button) findViewById(R.id.edit_button_delete_player);
        renameTeamButton.setOnClickListener(this);
        addPlayerButton.setOnClickListener(this);
        renamePlayerButton.setOnClickListener(this);
        deletePlayerButton.setOnClickListener(this);
    }

    private void getTeamIdFromIntent(){
        Intent intent = getIntent();
        this.teamId = intent.getIntExtra("teamId", -1);
        if(teamId==-1){
            Log.wtf(TAG, "onCreate() failed to pass teamId to TeamActivity");
            MyApplication.showToast(context, getResources().getString(R.string.toastFailedToLoadTeam));
            finish();
        }
        try {
            teamName = MyApplication.db.getTeamNameById(teamId);
        } catch (TeamNotFoundException e) {
            e.printStackTrace();
            Log.wtf(TAG, "onCreate() did not find team "+teamId);
        }
    }

    private void setTeamNameOnActionBar(String name){
        ActionBar ab = getActionBar();
        if (ab != null)
            ab.setTitle(String.format("\tEdit : %s", teamName));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
        setTeamNameOnActionBar(teamName);
        checkMinPlayers();
    }

    private void checkMinPlayers(){
        Integer minPlayers = getResources().getInteger(R.integer.minPlayersPerMatch);
        Integer numPlayers = MyApplication.db.getPlayersCountByTeamId(teamId);
        if(numPlayers < minPlayers){
            MyApplication.showLongToast(context, getResources().getString(R.string.toastNoPlayersInitPrefix)+(minPlayers-numPlayers)+getResources().getString(R.string.toastNoPlayersInitSuffix));
            addPlayerButtonClick();
        }
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
        if(v.equals(renameTeamButton)){
            renameTeamButtonClick();
        }
        else if(v.equals(addPlayerButton)){
            addPlayerButtonClick();
        }
        else if(v.equals(renamePlayerButton)){
            renamePlayerButtonClick();
        }
        else if(v.equals(deletePlayerButton)){
            deletePlayerButtonClick();
        }
    }

    private void renameTeamButtonClick() {
        Log.i(TAG, "renameTeamButtonClick()");
        AlertDialog.Builder renameTeamBuilder = new AlertDialog.Builder(context);
        renameTeamBuilder.setTitle(getResources().getString(R.string.dialogCreateTeamTitle));
        renameTeamBuilder.setCancelable(false);

        EditText editTextRenameTeam = new EditText(this);
        renameTeamBuilder.setView(editTextRenameTeam);
        renameTeamBuilder.setCancelable(true);
        renameTeamBuilder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }
        );
        renameTeamBuilder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }
        );
        AlertDialog createDialog = renameTeamBuilder.create();
        createDialog.show();
        Button okButton = createDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setOnClickListener(new EditActivity.RenameTeamDialogListener(createDialog, editTextRenameTeam));
    }

    public class RenameTeamDialogListener implements View.OnClickListener {
        private final Dialog dialog;
        private final EditText editTextRenameTeam;
        RenameTeamDialogListener(Dialog dialog, EditText editText) {
            this.dialog = dialog;
            this.editTextRenameTeam = editText;
        }
        @Override
        public void onClick(View v) {
            CharSequence selectedTeamName = editTextRenameTeam.getText();
            if(validate(selectedTeamName.toString())){
                dialog.dismiss();
            }
        }

        boolean validate(String name){
            if(name.length()<getResources().getInteger(R.integer.minCharsTeamName)){    // validate min length
                MyApplication.showToast(context, getResources().getString(R.string.toastTeamNameTooShort));
                return false;
            }
            if(name.length()>getResources().getInteger(R.integer.maxCharsTeamName)){    // validate max length
                MyApplication.showToast(context, getResources().getString(R.string.toastTeamNameTooLong));
                return false;
            }
            try {
                if(name.equals(MyApplication.db.getTeamNameById(teamId))){     // validate current name
                    MyApplication.showToast(context, getResources().getString(R.string.toastTeamNameTooLong));
                    return false;
                }
            } catch (TeamNotFoundException e) {
                e.printStackTrace();
                Log.wtf(TAG, "RenameTeamDialogListener.validate() did not find team "+teamId);
                return false;
            }
            try {
                MyApplication.db.setTeamNameById(teamId, name);
            } catch (TeamAlreadyExistsException e) {
                MyApplication.showToast(context, getResources().getString(R.string.toastTeamAlreadyExists));
                return false;
            } catch (TeamNotFoundException e) {
                e.printStackTrace();
                Log.wtf(TAG, "RenameTeamDialogListener.validate() did not find team "+teamId);
                return false;
            }
            teamName=name;
            setTeamNameOnActionBar(teamName);
            return true;
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
        okButton.setOnClickListener(new EditActivity.AddPlayerDialogListener(addDialog, editTextAddPlayer));
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

    private void renamePlayerButtonClick() {

    }

    private void deletePlayerButtonClick() {
        //Log.i(TAG, "deletePlayerButtonClick()");
        selectedPlayer = -1;
        if(MyApplication.db.isPlayersEmptyByTeamId(teamId)){
            MyApplication.showToast(context, getResources().getString(R.string.toastNoPlayersToDelete));
            return;
        }
        ArrayList<String> playersNamesList = MyApplication.db.getPlayersNamesByTeamId(teamId);
        final CharSequence[] choiceList = playersNamesList.toArray(new CharSequence[playersNamesList.size()]);
        AlertDialog.Builder deletePlayerBuilder = new AlertDialog.Builder(context);
        deletePlayerBuilder.setTitle(getResources().getString(R.string.dialogPlayerToDelete));
        deletePlayerBuilder.setItems(choiceList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedPlayer = which;
                Log.d(TAG, "selectedPlayer value=" + selectedPlayer);
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
        deletePlayerBuilder.setCancelable(true);
        AlertDialog deleteDialog = deletePlayerBuilder.create();
        deleteDialog.show();
    }
}
