package lineo.smarteam.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import lineo.smarteam.MyApplication;
import lineo.smarteam.R;
import lineo.smarteam.exception.TeamNotFoundException;

public class ResultsActivity extends Activity  implements View.OnClickListener {
    private static final String TAG = "ResultsActivity";
    private Context context;

    private Button addButton;
    private Button viewButton;
    private Button editButton;
    private Button deleteButton;

    // player selection indexes
    final ArrayList<Integer> selectedPlayersIndexList = new ArrayList<>();
    final ArrayList<Integer> selectedWinnersIndexList = new ArrayList<>();
    final ArrayList<Integer> selectedLosersIndexList = new ArrayList<>();

    private Integer teamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        context=this;
        setLayout();
        setActionBarTitle();
    }

    private void setLayout(){
        setContentView(R.layout.activity_results);
        addButton = (Button) findViewById(R.id.results_button_add);
        viewButton = (Button) findViewById(R.id.results_button_View);
        editButton = (Button) findViewById(R.id.results_button_Edit);
        deleteButton = (Button) findViewById(R.id.results_button_delete);
        addButton.setOnClickListener(this);
        viewButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }

    private void setActionBarTitle(){
        Intent intent = getIntent();
        this.teamId = intent.getIntExtra("teamId", -1);
        if(teamId==-1){
            Log.wtf(TAG, "onCreate() failed to pass teamId to "+TAG);
            MyApplication.showToast(context, getResources().getString(R.string.toastFailedToLoadTeam));
            finish();
        }
        String teamName = null;
        try {
            teamName = MyApplication.db.getTeamNameById(teamId);
        } catch (TeamNotFoundException e) {
            e.printStackTrace();
            Log.wtf(TAG, "onCreate() did not find team "+teamId);
        }
        ActionBar ab = getActionBar();
        if (ab != null)
            ab.setTitle(String.format(getResources().getString(R.string.title_activity_results)+" : %s", teamName));
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
        if(v.equals(addButton)){
            insertResultButtonClick();
        }
        else if(v.equals(viewButton)) {
            deleteLastResultButtonClick();
        }
        else if(v.equals(editButton)) {
            deleteLastResultButtonClick();
        }
        else if(v.equals(deleteButton)) {
            deleteLastResultButtonClick();
        }
    }

    private void insertResultButtonClick() {
        Log.i(TAG, "insertResultButtonClick()");
        if(MyApplication.db.getPlayersCountByTeamId(teamId) < getResources().getInteger(R.integer.minPlayersPerMatch)){
            MyApplication.showToast(context, getResources().getString(R.string.toastNotEnoughPlayersInTeam)+getResources().getInteger(R.integer.minPlayersPerMatch));
            return;
        }
        AlertDialog.Builder builderResult = new AlertDialog.Builder(context);
        builderResult.setTitle(getResources().getString(R.string.dialogInsertResult));
        builderResult.setCancelable(true);
        builderResult.setPositiveButton(R.string.draw, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                insertResultDraw();
            }
        });
        builderResult.setNegativeButton(R.string.winDefeat, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                insertResultWinDefeat();
            }
        });
        AlertDialog dialogResult = builderResult.create();
        dialogResult.show();
    }

    private void insertResultDraw() {
        Log.i(TAG, "insertResultDraw()");
        int playersCount = MyApplication.db.getPlayersCountByTeamId(teamId);
        final CharSequence[] choiceList = MyApplication.db.getPlayersNamesByTeamId(teamId).toArray(new CharSequence[playersCount]);
        boolean[] isSelectedArray = new boolean[playersCount];
        for(int i=0; i< playersCount; ++i)
            isSelectedArray[i] = false;
        AlertDialog.Builder builderDraw = new AlertDialog.Builder(context);
        builderDraw.setTitle(getResources().getString(R.string.dialogSelectPlayersDraw));
        builderDraw.setMultiChoiceItems(choiceList, isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    selectedPlayersIndexList.add(which);
                } else if (selectedPlayersIndexList.contains(which)) {
                    selectedPlayersIndexList.remove(Integer.valueOf(which));
                }
            }
        });
        builderDraw.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builderDraw.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedPlayersIndexList.clear();
            }
        });
        AlertDialog dialogDraw = builderDraw.create();
        dialogDraw.show();
        Button okButton = dialogDraw.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setOnClickListener(new ResultsActivity.InsertResultDrawDialogListener(dialogDraw));
    }

    public class InsertResultDrawDialogListener implements View.OnClickListener {
        private final Dialog dialog;
        InsertResultDrawDialogListener(Dialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {
            if(!isSelectionValid())
                return;
            AlertDialog.Builder builderAreYouSure = new AlertDialog.Builder(context);
            builderAreYouSure.setTitle(getResources().getString(R.string.dialogSelectPlayersDrawAreYouSurePrefix)+selectedPlayersIndexList.size()+getResources().getString(R.string.dialogSelectPlayersDrawAreYouSureSuffix));
            builderAreYouSure.setCancelable(false);
            builderAreYouSure.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogAreYouSure, int which) {
                    dialog.dismiss();
                    if(MyApplication.db.insertResult(teamId, selectedWinnersIndexList, selectedLosersIndexList, MyApplication.ResultType.Draw)){
                        goToRanking();
                    }
                    else{
                        MyApplication.showToast(context, getResources().getString(R.string.toastFailedToAddResult));
                    }
                    selectedPlayersIndexList.clear();
                }
            });
            builderAreYouSure.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialogAreYouSure = builderAreYouSure.create();
            dialogAreYouSure.show();
        }

        private boolean isSelectionValid(){
            if(selectedPlayersIndexList.size() < getResources().getInteger(R.integer.minPlayersPerMatch)){
                MyApplication.showToast(context, getResources().getString(R.string.toastNotEnoughPlayersSelected)+getResources().getInteger(R.integer.minPlayersPerMatch));
                return false;
            }
            if(selectedPlayersIndexList.size() > getResources().getInteger(R.integer.maxPlayersPerMatch)){
                MyApplication.showToast(context, getResources().getString(R.string.toastTooManyPlayersSelected)+getResources().getInteger(R.integer.maxPlayersPerMatch));
                return false;
            }
            return true;
        }
    }


    private void insertResultWinDefeat() {
        Log.i(TAG, "insertResultWinDefeat()");
        int playersCount = MyApplication.db.getPlayersCountByTeamId(teamId);
        final CharSequence[] choiceList = MyApplication.db.getPlayersNamesByTeamId(teamId).toArray(new CharSequence[playersCount]);
        boolean[] isSelectedArray = new boolean[playersCount];
        for(int i=0; i< playersCount; ++i)
            isSelectedArray[i] = false;
        AlertDialog.Builder builderWin = new AlertDialog.Builder(context);
        builderWin.setTitle(getResources().getString(R.string.dialogSelectPlayersWin));
        builderWin.setMultiChoiceItems(choiceList, isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    selectedWinnersIndexList.add(which);
                } else if (selectedWinnersIndexList.contains(which)) {
                    selectedWinnersIndexList.remove(Integer.valueOf(which));
                }
            }
        });
        builderWin.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builderWin.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedWinnersIndexList.clear();
            }
        });
        AlertDialog dialogWin = builderWin.create();
        dialogWin.show();
        Button okButton = dialogWin.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setOnClickListener(new ResultsActivity.InsertResultWinDialogListener(dialogWin));
    }

    private class InsertResultWinDialogListener implements View.OnClickListener {
        private final Dialog dialogWin;
        InsertResultWinDialogListener(AlertDialog dialog) {
            this.dialogWin = dialog;
        }
        @Override
        public void onClick(View v) {
            if(!isSelectionValid())
                return;
            int playersCount = MyApplication.db.getPlayersCountByTeamId(teamId) - selectedWinnersIndexList.size();
            final CharSequence[] choiceList = new CharSequence[playersCount];
            ArrayList<String> playersList = MyApplication.db.getPlayersNamesByTeamId(teamId);
            int j=0;
            for(int i=0; i < playersList.size() && j<playersCount; ++i){
                if(selectedWinnersIndexList.contains(i))
                    continue;

                choiceList[j] = playersList.get(i);
                ++j;
            }
            boolean[] isSelectedArray = new boolean[playersCount];
            for(int i=0; i< playersCount; ++i)
                isSelectedArray[i] = false;
            AlertDialog.Builder builderDefeat = new AlertDialog.Builder(context);
            builderDefeat.setTitle(getResources().getString(R.string.dialogSelectPlayersDefeat));
            builderDefeat.setMultiChoiceItems(choiceList, isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked) {
                        selectedLosersIndexList.add(which);
                    } else if (selectedLosersIndexList.contains(which)) {
                        selectedLosersIndexList.remove(Integer.valueOf(which));
                    }
                }
            });
            builderDefeat.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builderDefeat.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedLosersIndexList.clear();
                }
            });
            AlertDialog dialogDefeat = builderDefeat.create();
            dialogDefeat.show();
            Button okButton = dialogDefeat.getButton(DialogInterface.BUTTON_POSITIVE);
            okButton.setOnClickListener(new InsertResultDefeatDialogListener(dialogDefeat));
        }

        private boolean isSelectionValid(){
            if(selectedWinnersIndexList.size() < getResources().getInteger(R.integer.minPlayersPerMatch)/2){
                MyApplication.showToast(context, getResources().getString(R.string.toastNotEnoughPlayersSelected)+getResources().getInteger(R.integer.minPlayersPerMatch));
                return false;
            }
            if(selectedWinnersIndexList.size() > getResources().getInteger(R.integer.maxPlayersPerMatch)/2){
                MyApplication.showToast(context, getResources().getString(R.string.toastTooManyPlayersSelected)+getResources().getInteger(R.integer.maxPlayersPerMatch));
                return false;
            }
            return true;
        }

        private class InsertResultDefeatDialogListener implements View.OnClickListener {
            private final Dialog dialogDefeat;
            InsertResultDefeatDialogListener(AlertDialog dialog) {
                this.dialogDefeat = dialog;
            }
            @Override
            public void onClick(View v) {
                if(!isSelectionValid())
                    return;
                final int winners = selectedWinnersIndexList.size();
                final int losers = selectedLosersIndexList.size();
                AlertDialog.Builder builderAreYouSure = new AlertDialog.Builder(context);
                builderAreYouSure.setTitle(getResources().getString(R.string.dialogSelectPlayersWinLoseAreYouSurePrefix)+winners+" vs "+losers+getResources().getString(R.string.dialogSelectPlayersWinLoseAreYouSureSuffix));
                builderAreYouSure.setCancelable(true);
                builderAreYouSure.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogAreYouSure, int which) {
                        dialogDefeat.dismiss();
                        dialogWin.dismiss();
                        if(MyApplication.db.insertResult(teamId, selectedWinnersIndexList, selectedLosersIndexList, MyApplication.ResultType.Win)){
                            goToRanking();
                        }
                        else{
                            MyApplication.showToast(context, getResources().getString(R.string.toastFailedToAddResult));
                        }
                        selectedWinnersIndexList.clear();
                        selectedLosersIndexList.clear();
                    }
                });
                builderAreYouSure.setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialogAreYouSure = builderAreYouSure.create();
                dialogAreYouSure.show();
            }

            private boolean isSelectionValid(){
                if(selectedLosersIndexList.size() < getResources().getInteger(R.integer.minPlayersPerMatch)/2){
                    MyApplication.showToast(context, getResources().getString(R.string.toastNotEnoughPlayersSelected)+getResources().getInteger(R.integer.minPlayersPerMatch));
                    return false;
                }
                if(selectedLosersIndexList.size() > getResources().getInteger(R.integer.maxPlayersPerMatch)/2){
                    MyApplication.showToast(context, getResources().getString(R.string.toastTooManyPlayersSelected)+getResources().getInteger(R.integer.maxPlayersPerMatch));
                    return false;
                }
                if(Math.abs(selectedWinnersIndexList.size() - selectedLosersIndexList.size()) > 1){
                    MyApplication.showToast(context, getResources().getString(R.string.toastTeamsUnbalanced));
                    return false;
                }
                return true;
            }
        }
    }

    private void goToRanking(){

    }

    private void deleteLastResultButtonClick() {

    }
}
