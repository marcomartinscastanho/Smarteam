package lineo.smarteam.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import lineo.smarteam.MyApplication;
import lineo.smarteam.R;

public class TeamActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "TeamActivity";
    private Context context;

    private Button resultsButton;
    private Button rankingButton;
    private Button lineupsButton;
    private Button statisticsButton;
    private Button editButton;

    private Integer teamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        context=this;
        setLayout();
        setTeamNameOnActionBar();
    }

    private void setLayout(){
        setContentView(R.layout.activity_team);
        resultsButton = (Button) findViewById(R.id.team_buttonResults);
        rankingButton = (Button) findViewById(R.id.team_buttonRanking);
        lineupsButton = (Button) findViewById(R.id.team_buttonLineups);
        statisticsButton = (Button) findViewById(R.id.team_buttonStatistics);
        editButton = (Button) findViewById(R.id.team_buttonEdit);
        resultsButton.setOnClickListener(this);
        rankingButton.setOnClickListener(this);
        lineupsButton.setOnClickListener(this);
        statisticsButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
    }

    private void setTeamNameOnActionBar(){
        Intent intent = getIntent();
        String teamName = intent.getStringExtra("teamName");
        this.teamId = intent.getIntExtra("teamId", -1);
        if(teamId==-1){
            Log.wtf(TAG, "onCreate() failed to pass teamId to TeamActivity");
            MyApplication.showToast(context, getResources().getString(R.string.toastFailedToLoadTeam));
            finish();
        }
        ActionBar ab = getActionBar();
        if (ab != null)
            ab.setTitle(String.format("\t%s", teamName));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        if(MyApplication.db.isPlayersEmptyByTeamId(teamId)){
            MyApplication.showToast(context, getResources().getString(R.string.toastNoPlayersInit));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
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

    @Override
    public void onClick(View v) {
        //Log.i(TAG, "onClick()");
        if(v.equals(resultsButton)){
            resultsButtonClick();
        }
        else if(v.equals(rankingButton)){
            rankingButtonClick();
        }
        else if(v.equals(lineupsButton)){
            lineupsButtonClick();
        }
        else if(v.equals(statisticsButton)){
            statisticsButtonClick();
        }
        else if(v.equals(editButton)){
            editButtonClick();
        }
    }

    private void resultsButtonClick() {
        callResultsActivity();
    }

    private void rankingButtonClick() {

    }

    private void lineupsButtonClick() {

    }

    private void statisticsButtonClick() {

    }

    private void editButtonClick() {
        callEditActivity();
    }

    public void callResultsActivity(){
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("teamId", teamId);
        startActivity(intent);
    }

    public void callEditActivity(){
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("teamId", teamId);
        startActivity(intent);
    }
}
