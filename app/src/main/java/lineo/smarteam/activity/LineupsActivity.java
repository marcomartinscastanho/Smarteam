package lineo.smarteam.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;

import java.util.ArrayList;

import lineo.smarteam.MyApplication;
import lineo.smarteam.R;
import lineo.smarteam.ShareAction;
import lineo.smarteam.db.DataBase;
import lineo.smarteam.exception.TeamNotFoundException;

public class LineupsActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "LineupsActivity";
    private Context context;
    private ShareAction shareAction;

    private Integer teamId;
    private ArrayList<Integer> selectedPlayersIndexList = new ArrayList<>();

    private String[] lineupHome;
    private String[] lineupAway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        context=this;
        getIntentExtras();
        setActionBarTitle();
        //setContentView(R.layout.loading);
        generateLineups(); //FIXME: this might take a while, screen cannot be empty in the meanwhile
        //TODO: use the same logic as in the Splash Screen
        setLayout();

    }

    private void setLayout(){
        //findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        setContentView(R.layout.activity_lineups);
        ListView listHome = findViewById(R.id.listViewTeamHome);
        ListView listAway = findViewById(R.id.listViewTeamAway);
        listHome.setAdapter(new ArrayAdapter<>(context, R.layout.lineups_line_black, lineupHome));
        listAway.setAdapter(new ArrayAdapter<>(context, R.layout.lineups_line_white, lineupAway));
        //TODO: put both lists centered vertically

    }

    private void getIntentExtras(){
        Intent intent = getIntent();
        this.teamId = intent.getIntExtra("teamId", -1);
        if(teamId==-1){
            Log.wtf(TAG, "onCreate() failed to pass teamId to "+TAG);
            MyApplication.showToast(context, getResources().getString(R.string.toastFailedToLoadTeam));
            finish();
        }
        this.selectedPlayersIndexList = intent.getIntegerArrayListExtra("selectedPlayersIndexList");
        if(selectedPlayersIndexList == null || selectedPlayersIndexList.isEmpty()){
            Log.wtf(TAG, "onCreate() failed to pass selectedPlayersIndexList to "+TAG);
            MyApplication.showToast(context, getResources().getString(R.string.toastFailedToLoadTeam));
            finish();
        }
    }

    private void setActionBarTitle(){
        String teamName = null;
        try {
            teamName = MyApplication.db.getTeamNameById(teamId);
        } catch (TeamNotFoundException e) {
            e.printStackTrace();
            Log.wtf(TAG, "onCreate() did not find team "+teamId);
        }
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setTitle(String.format(getResources().getString(R.string.title_activity_lineups) + " : %s", teamName));
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        MenuItem item = menu.findItem(R.id.item_share);
        shareAction = new ShareAction((ShareActionProvider) item.getActionProvider(), this);
        return true;
    }

    private void generateLineups() {
        class Player{
            private Integer playerId;
            private String playerName;
            private Double score;

            Player(Integer playerId, String playerName, Double score){
                this.playerId = playerId;
                this.playerName = playerName;
                this.score = score;
            }

            public Integer getId() {
                return playerId;
            }

            public String getName() {
                return playerName;
            }

            Double getScore() {
                return score;
            }
        }

        class LineupGenerator{
            private Integer maxPlayersPerTeam;
            private Double provisionalDiff;
            private ArrayList<Player> selectedPlayers;
            private ArrayList<Player> p1;
            private ArrayList<Player> p2;
            private ArrayList<Player> res1;
            private ArrayList<Player> res2;

            private LineupGenerator(ArrayList<Integer> selectedPlayersIndexList){
                maxPlayersPerTeam = (selectedPlayersIndexList.size()+1)/2;
                provisionalDiff =  Integer.valueOf(100*selectedPlayersIndexList.size()).doubleValue();
                selectedPlayers = new ArrayList<>(selectedPlayersIndexList.size());
                Cursor c = MyApplication.db.getLineupInfoByTeamId(teamId);
                Integer cursorCount=0;
                if(c.moveToFirst()){
                    do{
                        if(selectedPlayersIndexList.contains(cursorCount)){
                            selectedPlayers.add(new Player(c.getInt(c.getColumnIndexOrThrow(DataBase.PLAYERS_COLUMN_ID)), c.getString(c.getColumnIndexOrThrow(DataBase.PLAYERS_COLUMN_NAME)), c.getDouble(c.getColumnIndexOrThrow(DataBase.PLAYERS_COLUMN_SCORE))));
                        }
                        ++cursorCount;
                    }
                    while(c.moveToNext());
                }
                p1 = new ArrayList<>(maxPlayersPerTeam+1);
                p2 = new ArrayList<>(maxPlayersPerTeam+1);
                res1 = new ArrayList<>(maxPlayersPerTeam);
                res2 = new ArrayList<>(maxPlayersPerTeam);
            }

            private void partition(Integer i, Double sum1, Double sum2){
                if(p1.size()>maxPlayersPerTeam)
                    return;
                if(p2.size()>maxPlayersPerTeam)
                    return;
                if(i.equals(selectedPlayers.size())){
                    Double newDiff = Math.abs(sum1 - sum2);
                    if(newDiff < provisionalDiff){
                        res1 = new ArrayList<>(p1);
                        res2 = new ArrayList<>(p2);
                        provisionalDiff = newDiff;
                    }
                    return;
                }
                p1.add(selectedPlayers.get(i));
                partition(i+1, sum1 + selectedPlayers.get(i).getScore(), sum2);
                if(provisionalDiff.equals(0.0))
                    return;
                p1.remove(p1.size()-1);
                p2.add(selectedPlayers.get(i));
                partition(i+1, sum1, sum2 + selectedPlayers.get(i).getScore());
                if(provisionalDiff.equals(0.0))
                    return;
                p2.remove(p2.size()-1);
            }
        }

        LineupGenerator lineupGen = new LineupGenerator(selectedPlayersIndexList);
        lineupGen.partition(0, 0.0, 0.0);   //Magic Happens Here
        lineupHome = new String[lineupGen.res1.size()];
        lineupAway = new String[lineupGen.res2.size()];
        Double strengthHome=0.0;
        Double strengthAway=0.0;
        Integer i=0;
        for(Player p : lineupGen.res1){
            lineupHome[i] = p.getName();
            strengthHome += p.getScore()*100;
            ++i;
        }
        i=0;
        for(Player p : lineupGen.res2){
            lineupAway[i] = p.getName();
            strengthAway += p.getScore()*100;
            ++i;
        }
        Log.i(TAG, "generateLineups() "+strengthHome+ " vs "+strengthAway);
    }
}
