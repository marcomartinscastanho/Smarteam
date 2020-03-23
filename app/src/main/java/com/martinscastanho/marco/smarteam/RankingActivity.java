package com.martinscastanho.marco.smarteam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.martinscastanho.marco.smarteam.database.DataBase;

import java.util.HashMap;
import java.util.Map;

public class RankingActivity extends AppCompatActivity {
    int teamId;
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        db = new DataBase(getApplicationContext());

        getIntentExtras();
        setTitle("Ranking");
        setLayout();
    }

    private void getIntentExtras() {
        Intent intent = getIntent();
        teamId = intent.getIntExtra("teamId", -1);
        if(teamId < 0){
            finish();
        }
    }

    private void setLayout(){
        Cursor mCursor = db.getRankingByTeamId(teamId);
        ListView userListView = findViewById(R.id.rankingListView);
        ListAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.ranking_row, mCursor, new String[] {DataBase.Player.PLAYERS_RANKING_POSITION, DataBase.Player.COLUMN_NAME_NAME, DataBase.Player.COLUMN_NAME_MATCHES, DataBase.Player.COLUMN_NAME_SCORE}, new int[]{R.id.rankingPlayerRankRow, R.id.rankingPlayerNameRow, R.id.rankingPlayerMatchesRow, R.id.rankingPlayerScoreRow}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        userListView.setAdapter(listAdapter);
    }

    public void headerLegend(View view){
        Map<String, String> toasts = new HashMap<>();
        toasts.put("M", "M: Matches played");
        toasts.put("S", "S: Score");

        String header = String.valueOf(((TextView) view).getText());
        Toast.makeText(RankingActivity.this, toasts.get(header), Toast.LENGTH_SHORT).show();
    }
}
