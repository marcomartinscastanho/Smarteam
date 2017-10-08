package lineo.smarteam.activity;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ShareActionProvider;
import android.widget.SimpleCursorAdapter;

import lineo.smarteam.MyApplication;
import lineo.smarteam.R;
import lineo.smarteam.ShareAction;
import lineo.smarteam.exception.TeamNotFoundException;


public class ResultsActivity extends ListActivity {
    private static final String TAG = "ResultsActivity";
    private Context context;
    private Integer teamId;
    private String teamName;
    private ShareAction shareAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        context=this;
        getTeamIdFromIntent();
        setActionBarTitle();
        Cursor mCursor = MyApplication.db.getPrintableResultsByTeamId(teamId);
        ListAdapter listAdapter = new SimpleCursorAdapter(context, R.layout.result_layout, mCursor, new String[] {"MATCHDAY", "DATE", "WIN", "DRAW", "DEFEAT"},
                new int[]{R.id.result_layout_matchday, R.id.result_layout_date, R.id.result_layout_win, R.id.result_layout_draw, R.id.result_layout_defeat}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        setListAdapter(listAdapter);
    }

    private void setActionBarTitle(){
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setTitle(String.format("%s: " + getResources().getString(R.string.title_activity_results), teamName));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_share: {
                Log.i(TAG, "Share Button clicked!");
                shareAction.share();
                return true;
            }
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        MenuItem item = menu.findItem(R.id.item_share);
        shareAction = new ShareAction((ShareActionProvider) item.getActionProvider(), this);
        return true;
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
