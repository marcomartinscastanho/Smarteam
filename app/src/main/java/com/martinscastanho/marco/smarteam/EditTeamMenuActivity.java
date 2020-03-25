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
        setTitle(R.string.title_edit_team);

        db = new DataBase(getApplicationContext());
    }

    public void renameTeam(View view){
        AlertDialog.Builder renameTeamAlert = new AlertDialog.Builder(this);
        final EditText renameTeamEditText = new EditText(this);
        renameTeamAlert.setTitle(getResources().getString(R.string.renameTeamDialog));
        renameTeamAlert.setView(renameTeamEditText);
        renameTeamAlert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String teamName = renameTeamEditText.getText().toString();

                if(teamName.length() < getResources().getInteger(R.integer.min_name_length)){
                    Toast.makeText(EditTeamMenuActivity.this, R.string.toast_name_too_short, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(teamName.length() > getResources().getInteger(R.integer.max_name_length)){
                    Toast.makeText(EditTeamMenuActivity.this, R.string.toast_name_too_long, Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                    db.updateTeamName(teamId, teamName);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("teamName", teamName);
                    setResult(Activity.RESULT_OK, resultIntent);

                    Toast.makeText(EditTeamMenuActivity.this, R.string.toast_team_name_updated, Toast.LENGTH_SHORT).show();

                }
                catch(SQLException e){
                    Toast.makeText(EditTeamMenuActivity.this, R.string.toast_invalid_name, Toast.LENGTH_SHORT).show();
                }
            }
        });
        renameTeamAlert.show();
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
                    Toast.makeText(EditTeamMenuActivity.this, R.string.toast_name_too_short, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(playerName.length() > getResources().getInteger(R.integer.max_name_length)){
                    Toast.makeText(EditTeamMenuActivity.this, R.string.toast_name_too_long, Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                    db.insertPlayer(playerName, teamId);
                    Toast.makeText(EditTeamMenuActivity.this, R.string.toast_player_added, Toast.LENGTH_SHORT).show();
                }
                catch(SQLException e){
                    Toast.makeText(EditTeamMenuActivity.this, R.string.toast_invalid_name, Toast.LENGTH_SHORT).show();
                }
            }
        });
        newPlayerAlert.show();
    }

    public void renamePlayer(View view){
        final ArrayList<String> playerList = db.getPlayersNames(teamId);
        if(playerList.isEmpty()){
            Toast.makeText(EditTeamMenuActivity.this, R.string.toast_no_players, Toast.LENGTH_SHORT).show();
            return;
        }

        final String[] choiceList = playerList.toArray(new String[0]);
        AlertDialog.Builder renamePlayerAlert = new AlertDialog.Builder(this);
        renamePlayerAlert.setTitle(getResources().getString(R.string.selectPlayerDialog));
        renamePlayerAlert.setItems(choiceList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("Rename Player", Integer.toString(which));
                final String playerName = playerList.get(which);

                AlertDialog.Builder renamePlayerAlert = new AlertDialog.Builder(EditTeamMenuActivity.this);
                final EditText renamePlayerEditText = new EditText(EditTeamMenuActivity.this);
                renamePlayerAlert.setTitle(getResources().getString(R.string.renamePlayerDialog));
                renamePlayerAlert.setView(renamePlayerEditText);
                renamePlayerAlert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPlayerName = renamePlayerEditText.getText().toString();

                        if(newPlayerName.length() < getResources().getInteger(R.integer.min_name_length)){
                            Toast.makeText(EditTeamMenuActivity.this, R.string.toast_name_too_short, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(newPlayerName.length() > getResources().getInteger(R.integer.max_name_length)){
                            Toast.makeText(EditTeamMenuActivity.this, R.string.toast_name_too_long, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try{
                            db.updatePlayerName(db.getPlayerId(playerName, teamId), newPlayerName);
                            Toast.makeText(EditTeamMenuActivity.this, R.string.toast_player_name_updated, Toast.LENGTH_SHORT).show();
                        }
                        catch(SQLException e){
                            Toast.makeText(EditTeamMenuActivity.this, R.string.toast_invalid_name, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                renamePlayerAlert.show();
            }
        });
        renamePlayerAlert.show();
    }

    public void deletePlayer(View view){
        final ArrayList<String> playerList = db.getPlayersNames(teamId);
        if(playerList.isEmpty()){
            Toast.makeText(EditTeamMenuActivity.this, R.string.toast_no_players, Toast.LENGTH_SHORT).show();
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
                confirmDeleteAlert.setTitle(String.format("%s %s?", getResources().getString(R.string.dialog_delete_player_confirm), playerName));
                confirmDeleteAlert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deletePlayer(db.getPlayerId(playerName, teamId));
                        Toast.makeText(EditTeamMenuActivity.this, String.format("%s %s!", playerName, getResources().getString(R.string.toast_team_deleted_suffix)), Toast.LENGTH_SHORT).show();
                    }
                });
                confirmDeleteAlert.setNegativeButton(android.R.string.no, null);
                confirmDeleteAlert.show();
            }
        });
        deletePlayerAlert.show();
    }
}
