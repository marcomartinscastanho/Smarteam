package com.martinscastanho.marco.smarteam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.martinscastanho.marco.smarteam.database.DataBase;

public class StatisticsMenuActivity extends AppCompatActivity {
    int teamId;
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statisctics_menu);

        db = new DataBase(getApplicationContext());

        getIntentExtras();
        setTitle("Statistics");
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
        Cursor mCursor = db.getStatistics(teamId, DataBase.Statistic.All);
        ListView userListView = findViewById(R.id.statisticsMenuListView);
        ListAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.statistics_menu_row, mCursor, new String[] {DataBase.Player.STATISTIC_NAME, DataBase.Player.COLUMN_NAME_NAME, DataBase.Player.STATISTIC_VALUE}, new int[]{R.id.statisticsMenuRowStatName, R.id.statisticsMenuRowPlayerName, R.id.statisticsMenuRowStatValue}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        userListView.setAdapter(listAdapter);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
