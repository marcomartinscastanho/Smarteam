package com.martinscastanho.marco.smarteam;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.help){
            // TODO: show help page
        }

        return super.onOptionsItemSelected(item);
    }

    public void openTeam(View view){
        final ArrayList<String> teamList = db.getTeamsNames();
        if(teamList.isEmpty()){
            Toast.makeText(MainActivity.this, R.string.toast_no_teams, Toast.LENGTH_SHORT).show();
            return;
        }

        final String[] choiceList = teamList.toArray(new String[0]);
        AlertDialog.Builder openTeamAlert = new AlertDialog.Builder(this);
        openTeamAlert.setTitle(getResources().getString(R.string.openTeamDialog));
        openTeamAlert.setItems(choiceList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                    Toast.makeText(MainActivity.this, R.string.toast_name_too_short, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(teamName.length() > getResources().getInteger(R.integer.max_name_length)){
                    Toast.makeText(MainActivity.this, R.string.toast_name_too_long, Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                    db.insertTeam(teamName);
                    callTeamActivity(teamName);
                }
                catch(SQLException e){
                    Toast.makeText(MainActivity.this, R.string.toast_invalid_name, Toast.LENGTH_SHORT).show();
                }
            }
        });
        newTeamAlert.show();
    }

    public void deleteTeam(View view){
        final ArrayList<String> teamList = db.getTeamsNames();
        if(teamList.isEmpty()){
            Toast.makeText(MainActivity.this, R.string.toast_no_teams, Toast.LENGTH_SHORT).show();
            return;
        }

        final String[] choiceList = teamList.toArray(new String[0]);
        AlertDialog.Builder deleteTeamAlert = new AlertDialog.Builder(this);
        deleteTeamAlert.setTitle(getResources().getString(R.string.deleteTeamDialog));
        deleteTeamAlert.setItems(choiceList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String teamName = teamList.get(which);
                AlertDialog.Builder confirmDeleteAlert = new AlertDialog.Builder(MainActivity.this);
                confirmDeleteAlert.setTitle(String.format("%s %s?", getResources().getString(R.string.dialog_delete_team_confirm), teamName));
                confirmDeleteAlert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteTeam(teamName);
                        Toast.makeText(MainActivity.this, String.format("%s %s %s", getResources().getString(R.string.toast_team_deleted_prefix), teamName, getResources().getString(R.string.toast_team_deleted_suffix)), Toast.LENGTH_SHORT).show();
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
