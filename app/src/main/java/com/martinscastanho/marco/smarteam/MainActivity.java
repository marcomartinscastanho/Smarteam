package com.martinscastanho.marco.smarteam;

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

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DataBase(getApplicationContext());
    }

    public void openTeam(View view){
        final ArrayList<String> teamList = db.getTeamsNames();
        if(teamList.isEmpty()){
            Toast.makeText(MainActivity.this, "Not teams to show", Toast.LENGTH_SHORT).show();
            return;
        }

        final String[] choiceList = teamList.toArray(new String[0]);
        AlertDialog.Builder openTeamAlert = new AlertDialog.Builder(this);
        openTeamAlert.setTitle(getResources().getString(R.string.openTeamDialog));
        openTeamAlert.setItems(choiceList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("Open Team", Integer.toString(which));
                String teamName = teamList.get(which);
                callTeamActivity(teamName);
            }
        });
        openTeamAlert.show();
    }

    public void newTeam(View view){
        AlertDialog.Builder newTeamAlert = new AlertDialog.Builder(this);
        final EditText newTeamEditText = new EditText(this);
        newTeamAlert.setTitle(getResources().getString(R.string.newTeamDialog));
        newTeamAlert.setView(newTeamEditText);
        newTeamAlert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String teamName = newTeamEditText.getText().toString();

                if(teamName.length() < getResources().getInteger(R.integer.min_name_length)){
                    Toast.makeText(MainActivity.this, "Name is too short", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(teamName.length() > getResources().getInteger(R.integer.max_name_length)){
                    Toast.makeText(MainActivity.this, "Name is too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                    db.insertTeam(teamName);
                    callTeamActivity(teamName);
                }
                catch(SQLException e){
                    Toast.makeText(MainActivity.this, "Invalid Team Name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        newTeamAlert.show();
    }

    public void deleteTeam(View view){
        final ArrayList<String> teamList = db.getTeamsNames();
        if(teamList.isEmpty()){
            Toast.makeText(MainActivity.this, "There are no teams", Toast.LENGTH_SHORT).show();
            return;
        }

        final String[] choiceList = teamList.toArray(new String[0]);
        AlertDialog.Builder deleteTeamAlert = new AlertDialog.Builder(this);
        deleteTeamAlert.setTitle(getResources().getString(R.string.deleteTeamDialog));
        deleteTeamAlert.setItems(choiceList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("Delete Team", Integer.toString(which));
                final String teamName = teamList.get(which);
                AlertDialog.Builder confirmDeleteAlert = new AlertDialog.Builder(MainActivity.this);
                confirmDeleteAlert.setTitle("Are you sure you want to delete team " + teamName + "?");
                confirmDeleteAlert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteTeam(teamName);
                        Toast.makeText(MainActivity.this, String.format("Team %s deleted!", teamName), Toast.LENGTH_SHORT).show();
                    }
                });
                confirmDeleteAlert.setNegativeButton(android.R.string.no, null);
                confirmDeleteAlert.show();
            }
        });
        deleteTeamAlert.show();
    }

    public void callTeamActivity(String teamName){
        Intent teamMenuIntent = new Intent(getApplicationContext(), TeamMenuActivity.class);
        teamMenuIntent.putExtra("teamName", teamName);
        startActivity(teamMenuIntent);
    }
}
