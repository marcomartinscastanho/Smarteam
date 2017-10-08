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
import lineo.smarteam.exception.PlayerNotFoundException;
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
        renameTeamButton = findViewById(R.id.edit_button_rename_team);
        addPlayerButton = findViewById(R.id.edit_button_add_player);
        renamePlayerButton = findViewById(R.id.edit_button_rename_player);
        deletePlayerButton = findViewById(R.id.edit_button_delete_player);
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
            ab.setTitle(String.format("%s: " + getResources().getString(R.string.title_activity_edit), name));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
        setTeamNameOnActionBar(teamName);
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
        editTextRenameTeam.setText(teamName);
        editTextRenameTeam.setSelection(teamName.length());
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

    private class RenameTeamDialogListener implements View.OnClickListener {
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

    private class AddPlayerDialogListener implements View.OnClickListener {
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
    //Log.i(TAG, "deleteButtonClick()");
        selectedPlayer = -1;
        if(MyApplication.db.isPlayersEmptyByTeamId(teamId)){
            MyApplication.showToast(context, getResources().getString(R.string.toastNoPlayers));
            return;
        }
        ArrayList<String> playersNamesList = MyApplication.db.getPlayersNamesByTeamId(teamId);
        final CharSequence[] choiceList = playersNamesList.toArray(new CharSequence[playersNamesList.size()]);
        AlertDialog.Builder renamePlayerBuilder = new AlertDialog.Builder(context);
        renamePlayerBuilder.setTitle(getResources().getString(R.string.dialogPlayerToRename));
        renamePlayerBuilder.setItems(choiceList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedPlayer = which;
                Log.d(TAG, "selectedPlayer value=" + selectedPlayer);
                if(selectedPlayer<0){
                    return;
                }
                Integer playerId = null;
                try {
                    playerId = MyApplication.db.getPlayerIdByNameAndTeamId(choiceList[selectedPlayer].toString(), teamId);
                } catch (PlayerNotFoundException e) {
                    e.printStackTrace();
                    Log.wtf(TAG, "RenamePlayerDialogListener.validate() did not find player "+choiceList[selectedPlayer].toString());
                }
                AlertDialog.Builder renamePlayerBuilder = new AlertDialog.Builder(context);
                renamePlayerBuilder.setTitle(getResources().getString(R.string.dialogAddPlayerTitle));
                renamePlayerBuilder.setCancelable(false);
                EditText editTextRenamePlayer = new EditText(context);
                editTextRenamePlayer.setText(choiceList[selectedPlayer].toString());
                editTextRenamePlayer.setSelection(choiceList[selectedPlayer].toString().length());
                renamePlayerBuilder.setView(editTextRenamePlayer);
                renamePlayerBuilder.setCancelable(true);
                renamePlayerBuilder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }
                );
                renamePlayerBuilder.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }
                );
                AlertDialog createDialog = renamePlayerBuilder.create();
                createDialog.show();
                Button okButton = createDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                okButton.setOnClickListener(new EditActivity.RenamePlayerDialogListener(createDialog, editTextRenamePlayer, playerId));
            }
        });
        renamePlayerBuilder.setCancelable(true);
        AlertDialog deleteDialog = renamePlayerBuilder.create();
        deleteDialog.show();
    }

    private class RenamePlayerDialogListener implements View.OnClickListener {
        private final Dialog dialog;
        private final EditText editTextRenamePlayer;
        private final Integer playerId;
        RenamePlayerDialogListener(Dialog dialog, EditText editText, Integer playerId) {
            this.dialog = dialog;
            this.editTextRenamePlayer = editText;
            this.playerId = playerId;
        }
        @Override
        public void onClick(View v) {
            CharSequence selectedPlayerName = editTextRenamePlayer.getText();
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
                MyApplication.db.setPlayerNameById(playerId, teamId, name);
            } catch (PlayerAlreadyExistsException e) {
                MyApplication.showToast(context, getResources().getString(R.string.toastPlayerAlreadyExists));
                return false;
            } catch (PlayerNotFoundException e) {
                e.printStackTrace();
                Log.wtf(TAG, "RenamePlayerDialogListener.validate() did not find player "+playerId);
            }
            return true;
        }
    }

    private void deletePlayerButtonClick() {
        //Log.i(TAG, "deletePlayerButtonClick()");
        selectedPlayer = -1;
        if(MyApplication.db.isPlayersEmptyByTeamId(teamId)){
            MyApplication.showToast(context, getResources().getString(R.string.toastNoPlayers));
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
