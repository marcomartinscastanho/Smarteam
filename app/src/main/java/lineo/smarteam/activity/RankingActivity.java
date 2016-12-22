package lineo.smarteam.activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import lineo.smarteam.MyApplication;
import lineo.smarteam.R;
import lineo.smarteam.db.DataBase;
import lineo.smarteam.exception.TeamNotFoundException;


public class RankingActivity extends ListActivity {
    private static final String TAG = "RankingActivity";
    Context context;
    Integer teamId;
    String teamName;
    Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        context=this;
        getTeamIdFromIntent();
        mCursor= MyApplication.db.getRankingByTeamId(teamId);
        ListAdapter listAdapter = new SimpleCursorAdapter(context, R.layout.ranking_line, mCursor, new String[] {DataBase.PLAYERS_RANKING_POSITION, DataBase.PLAYERS_COLUMN_NAME, DataBase.PLAYERS_COLUMN_SCORE}, new int[]{R.id.ranking_position, R.id.ranking_name, R.id.ranking_score}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        setListAdapter(listAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getTeamIdFromIntent(){
        Intent intent = getIntent();
        this.teamId = intent.getIntExtra("teamId", -1);
        if(teamId==-1){
            Log.wtf(TAG, "onCreate() failed to pass teamId to TeamActivity");
            MyApplication.showToast(context, getResources().getString(R.string.toastFailedToLoadTeam));
            finish();
        }
        try {
            teamName = MyApplication.db.getTeamNameById(teamId);
        } catch (TeamNotFoundException e) {
            e.printStackTrace();
            Log.wtf(TAG, "onCreate() did not find team "+teamId);
        }
    }
}
