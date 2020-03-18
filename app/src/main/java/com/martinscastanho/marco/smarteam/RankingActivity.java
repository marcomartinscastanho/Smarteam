package com.martinscastanho.marco.smarteam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.martinscastanho.marco.smarteam.database.DataBase;

public class RankingActivity extends AppCompatActivity {
    int teamId;
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        Intent intent = getIntent();
        teamId = intent.getIntExtra("teamId", -1);
        if(teamId < 0){
            finish();
        }
        setTitle("Ranking");

        db = new DataBase(getApplicationContext());
        Cursor mCursor = db.getRankingByTeamId(teamId);
        ListView userListView = findViewById(R.id.rankingListView);
        ListAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.ranking_row, mCursor, new String[] {DataBase.Player.PLAYERS_RANKING_POSITION, DataBase.Player.COLUMN_NAME_NAME, DataBase.Player.COLUMN_NAME_MATCHES, DataBase.Player.COLUMN_NAME_SCORE}, new int[]{R.id.playerRank, R.id.playerName, R.id.playerMatches, R.id.playerScore}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        userListView.setAdapter(listAdapter);
    }
}
