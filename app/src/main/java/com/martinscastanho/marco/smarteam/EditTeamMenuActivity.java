package com.martinscastanho.marco.smarteam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.martinscastanho.marco.smarteam.database.DataBase;

import java.util.ArrayList;

public class EditTeamMenuActivity extends AppCompatActivity {
    int teamId;
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team_menu);

        Intent intent = getIntent();
        teamId = intent.getIntExtra("teamId", -1);
        if(teamId < 0){
            finish();
        }
        setTitle("Edit Menu");

        db = new DataBase(getApplicationContext());
    }

    public void renameTeam(View view){
        AlertDialog.Builder newTeamAlert = new AlertDialog.Builder(this);
        final EditText newTeamEditText = new EditText(this);
        newTeamAlert.setTitle(getResources().getString(R.string.newTeamDialog));
        newTeamAlert.setView(newTeamEditText);
        newTeamAlert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String teamName = newTeamEditText.getText().toString();

                if(teamName.length() < getResources().getInteger(R.integer.min_name_length)){
                    Toast.makeText(EditTeamMenuActivity.this, "Name is too short", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(teamName.length() > getResources().getInteger(R.integer.max_name_length)){
                    Toast.makeText(EditTeamMenuActivity.this, "Name is too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                    db.updateTeamName(teamId, teamName);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("teamName", teamName);
                    setResult(Activity.RESULT_OK, resultIntent);

                    Toast.makeText(EditTeamMenuActivity.this, "Team name updated!", Toast.LENGTH_SHORT).show();

                }
                catch(SQLException e){
                    Toast.makeText(EditTeamMenuActivity.this, "Invalid Team Name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        newTeamAlert.show();
    }

    public void addPlayer(View view){
        AlertDialog.Builder newPlayerAlert = new AlertDialog.Builder(this);
        final EditText newPlayerEditText = new EditText(this);
        newPlayerAlert.setTitle(getResources().getString(R.string.newPlayerDialog));
        newPlayerAlert.setView(newPlayerEditText);
        newPlayerAlert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playerName = newPlayerEditText.getText().toString();

                if(playerName.length() < getResources().getInteger(R.integer.min_name_length)){
                    Toast.makeText(EditTeamMenuActivity.this, "Name is too short", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(playerName.length() > getResources().getInteger(R.integer.max_name_length)){
                    Toast.makeText(EditTeamMenuActivity.this, "Name is too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                    db.insertPlayer(playerName, teamId);
                    Toast.makeText(EditTeamMenuActivity.this, "Player added!", Toast.LENGTH_SHORT).show();
                }
                catch(SQLException e){
                    Toast.makeText(EditTeamMenuActivity.this, "Invalid Player name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        newPlayerAlert.show();
    }

    public void renamePlayer(View view){
        // TODO: this
    }

    public void deletePlayer(View view){
        final ArrayList<String> playerList = db.getPlayersNames(teamId);
        if(playerList.isEmpty()){
            Toast.makeText(EditTeamMenuActivity.this, "The team has no players", Toast.LENGTH_SHORT).show();
            return;
        }

        final String[] choiceList = playerList.toArray(new String[0]);
        AlertDialog.Builder deletePlayerAlert = new AlertDialog.Builder(this);
        deletePlayerAlert.setTitle(getResources().getString(R.string.deletePlayerDialog));
        deletePlayerAlert.setItems(choiceList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("Delete Player", Integer.toString(which));
                final String playerName = playerList.get(which);
                AlertDialog.Builder confirmDeleteAlert = new AlertDialog.Builder(EditTeamMenuActivity.this);
                confirmDeleteAlert.setTitle("Are you sure you want to delete " + playerName + "?");
                confirmDeleteAlert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deletePlayer(db.getPlayerId(playerName, teamId));
                        Toast.makeText(EditTeamMenuActivity.this, String.format("%s deleted!", playerName), Toast.LENGTH_SHORT).show();
                    }
                });
                confirmDeleteAlert.setNegativeButton(android.R.string.no, null);
                confirmDeleteAlert.show();
            }
        });
        deletePlayerAlert.show();
    }
}
