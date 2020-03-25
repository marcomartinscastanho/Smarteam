package com.martinscastanho.marco.smarteam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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

public class StatisticActivity extends AppCompatActivity {
    int teamId;
    DataBase db;
    DataBase.StatisticName statisticName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        db = new DataBase(getApplicationContext());

        getIntentExtras();
        setTitle(statisticName.toString());
        setLayout();
    }

    private void getIntentExtras() {
        Intent intent = getIntent();
        teamId = intent.getIntExtra("teamId", -1);
        if(teamId < 0){
            finish();
        }

        int iStatistic = intent.getIntExtra("iStatistic", -1);
        if(iStatistic < 0){
            finish();
        }
        else{
            statisticName = DataBase.StatisticName.valueOf(iStatistic);
            Log.d("statistic", statisticName.toString());
        }
    }

    private void setLayout(){
        // TODO: set the header depending on which statistic this is

        Cursor mCursor = db.getStatistic(teamId, statisticName, false);
        ListView userListView = findViewById(R.id.statisticListView);
        ListAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.ranking_row, mCursor, new String[] {DataBase.Player._ID, DataBase.Player.COLUMN_NAME, DataBase.Statistic.STATISTIC_VALUE}, new int[]{R.id.rankingPlayerRankRow, R.id.rankingPlayerNameRow, R.id.rankingPlayerScoreRow}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        userListView.setAdapter(listAdapter);
    }

    public void headerLegend(View view){
        Map<String, String> toasts = new HashMap<>();
        toasts.put("M", "M: Matches played");
        toasts.put("S", "S: Score");

        String header = String.valueOf(((TextView) view).getText());
        Toast.makeText(StatisticActivity.this, toasts.get(header), Toast.LENGTH_SHORT).show();
    }
}
