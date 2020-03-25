package com.martinscastanho.marco.smarteam;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.martinscastanho.marco.smarteam.database.DataBase;

import androidx.appcompat.app.AppCompatActivity;

public class RankingActivity extends AppCompatActivity {
    int teamId;
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        db = new DataBase(getApplicationContext());

        getIntentExtras();
        setTitle(R.string.title_ranking);
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
        Cursor mCursor = db.getRanking(teamId);
        ListView userListView = findViewById(R.id.rankingListView);
        ListAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.ranking_row, mCursor, new String[] {"_id", DataBase.Player.COLUMN_NAME, DataBase.Player.COLUMN_MATCHES, DataBase.Player.COLUMN_SCORE}, new int[]{R.id.rankingPlayerRankRow, R.id.rankingPlayerNameRow, R.id.rankingPlayerMatchesRow, R.id.rankingPlayerScoreRow}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        userListView.setAdapter(listAdapter);
    }

    public void matchesHeaderLegend(View view){
        Toast.makeText(RankingActivity.this, R.string.legend_matches, Toast.LENGTH_SHORT).show();
    }

    public void scoreHeaderLegend(View view){
        Toast.makeText(RankingActivity.this, R.string.legend_score, Toast.LENGTH_SHORT).show();
    }
}
