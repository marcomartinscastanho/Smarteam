package lineo.smarteam.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.sql.SQLException;
import java.util.ArrayList;
import lineo.smarteam.MyApplication;
import lineo.smarteam.R;
import lineo.smarteam.db.Teams;
import lineo.smarteam.exception.TeamAlreadyExistsException;

public class StartActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "StartActivity";
    private Context context;
    private MyApplication myApp;

    // Buttons
    private Button loadButton;
    private Button createButton;
    private Button deleteButton;
    private Button settingsButton;

    private Teams teamsDb;
    private ArrayList<String> teamNamesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        setLayout();

        context=this;
        myApp = ((MyApplication) ((Activity) context).getApplication());
        teamsDb = new Teams(context);
        try {
            teamsDb = teamsDb.open();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.wtf(TAG, "onCreate() Teams DB failed to open");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        teamNamesList = teamsDb.getTeamsNames();
        if(teamNamesList.isEmpty()){
            myApp.showToast(context, getResources().getString(R.string.toastNoTeams));
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(loadButton)){
            Log.i(TAG, "onClick() - Load");
            //loadButtonClick();
        }
        else if(v.equals(createButton)){
            Log.i(TAG, "onClick() - Create");
            createButtonClick();
        }
        else if(v.equals(deleteButton)){
            Log.i(TAG, "onClick() - Delete");
            //deleteButtonClick();
        }
        else if(v.equals(settingsButton)){
            Log.i(TAG, "onClick() - Settings");
            //settingsButtonClick();
        }
    }

    private void createButtonClick() {
        Log.i(TAG, "createButtonClick()");

        AlertDialog.Builder createBuilder = new AlertDialog.Builder(context);
        createBuilder.setTitle(getResources().getString(R.string.dialogCreateTeamTitle));
        createBuilder.setCancelable(false);

        EditText editTextCreateTeam = new EditText(this);
        createBuilder.setView(editTextCreateTeam);
        createBuilder.setCancelable(true);
        createBuilder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }
        );
        createBuilder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }
        );
        AlertDialog createDialog = createBuilder.create();
        createDialog.show();
        Button okButton = createDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setOnClickListener(new CreateTeamDialogListener(createDialog, editTextCreateTeam));
    }

    public class CreateTeamDialogListener implements View.OnClickListener {
        private final Dialog dialog;
        private final EditText editTextCreateTeam;
        CreateTeamDialogListener(Dialog dialog, EditText editText) {
            this.dialog = dialog;
            this.editTextCreateTeam = editText;
        }
        @Override
        public void onClick(View v) {
            CharSequence selectedTeamName = editTextCreateTeam.getText();
            if(validate(selectedTeamName.toString())){
                dialog.dismiss();
                Log.wtf(TAG, "onClick(View v) - Loading team " + selectedTeamName);
                callTeamActivity(selectedTeamName.toString());
            }
        }

        boolean validate(String name){
            if(name.length()<getResources().getInteger(R.integer.minCharsTeamName)){
                myApp.showToast(context, getResources().getString(R.string.toastTeamNameTooShort));
                return false;
            }
            if(name.length()>getResources().getInteger(R.integer.maxCharsTeamName)){
                myApp.showToast(context, getResources().getString(R.string.toastTeamNameTooLong));
                return false;
            }
            try {
                teamsDb.insertTeam(name);
            } catch (TeamAlreadyExistsException e) {
                myApp.showToast(context, getResources().getString(R.string.toastTeamAlreadyExists));
                return false;
            }
            return true;
        }
    }

    public void callTeamActivity(String teamId){/*
        Intent intent = new Intent(this, TeamMenuActivity.class);
        intent.putExtra("teamName", teamId);
        startActivity(intent);*/
    }

    private void setLayout(){
        setContentView(R.layout.activity_start);
        loadButton = (Button) findViewById(R.id.start_button_load);
        createButton = (Button) findViewById(R.id.start_button_create);
        deleteButton = (Button) findViewById(R.id.start_button_delete);
        settingsButton = (Button) findViewById(R.id.start_button_settings);
        loadButton.setOnClickListener(this);
        createButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
    }
}
