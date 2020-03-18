package com.martinscastanho.marco.smarteam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.martinscastanho.marco.smarteam.database.DataBase;

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
        // TODO: this
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
        // TODO: this
    }
}
