package lineo.smarteam.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import java.sql.SQLException;

import lineo.smarteam.MyApplication;
import lineo.smarteam.R;

public class SettingsActivity extends PreferenceActivity {
    private static final String TAG = "SettingsActivity";
    private static MyApplication myApp;
    private static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

        myApp = ((MyApplication) this.getApplication());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Respond to the action bar's Up/Home button
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private Preference resetDialogPreference;
        private Intent settingsIntent;
        private static final String KEY_PREF_WIN_SCORE = "KEY_PREF_WIN_SCORE";
        private static final String KEY_PREF_DRAW_SCORE = "KEY_PREF_DRAW_SCORE";
        private static final String KEY_PREF_DEFEAT_SCORE = "KEY_PREF_DEFEAT_SCORE";
        private static final String KEY_PREF_SHORT_ABSENCE_SCORE = "KEY_PREF_SHORT_ABSENCE_SCORE";
        private static final String KEY_PREF_MEDIUM_ABSENCE_SCORE = "KEY_PREF_MEDIUM_ABSENCE_SCORE";
        private static final String KEY_PREF_LONG_ABSENCE_SCORE = "KEY_PREF_LONG_ABSENCE_SCORE";
        private static final String KEY_PREF_MEDIUM_ABSENCE_DURATION = "KEY_PREF_MEDIUM_ABSENCE_DURATION";
        private static final String KEY_PREF_LONG_ABSENCE_DURATION = "KEY_PREF_LONG_ABSENCE_DURATION";
        private static final String KEY_PREF_K = "KEY_PREF_K";
        private static final String KEY_PREF_DEFAULT = "KEY_PREF_DEFAULT";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            setListeners();
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            setAllPreferencesSummaries();
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d(TAG, "onSharedPreferenceChanged() - key: "+key);
            String value = sharedPreferences.getString(key, "");
            setPreferenceSummary(key, value);
            try {
                MyApplication.db.setAllTeamScoresUpdated(false);
            } catch (SQLException e) {
                e.printStackTrace();
                Log.wtf(TAG, "setListenerWinScore() - failed to Un-Update Scores");
            }
        }

        public void setListeners() {
            setListenerWinScore();
            setListenerDrawScore();
            setListenerDefeatScore();
            setListenerShortAbsenceScore();
            setListenerMediumAbsenceScore();
            setListenerLongAbsenceScore();
            setListenerMediumAbsenceDuration();
            setListenerLongAbsenceDuration();
            setListenerK();
            setResetDefaultPreference();
        }

        /* Rules:
         * WIN_SCORE > DRAW_SCORE
         */
        private void setListenerWinScore() {
            findPreference(KEY_PREF_WIN_SCORE).setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            Log.i(TAG, "setListenerWinScore() - newValue=" + newValue.toString());
                            String nuValue = newValue.toString();
                            if(!complyBasicRules(nuValue))
                                return false;
                            if(!((Integer.parseInt(nuValue) > Integer.parseInt(sharedPreferences.getString(KEY_PREF_DRAW_SCORE, "1"))))){ //WIN_SCORE > DRAW_SCORE
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_points_win_not_larger_than_draw));
                                return false;
                            }
                            return true;
                        }
                    }
            );
        }

        /* Rules:
         * DRAW_SCORE >= SHORT_ABSENCE_SCORE
         * DRAW_SCORE > DEFEAT_SCORE
         * DRAW_SCORE >= 0
         * DRAW_SCORE < WIN_SCORE
         */
        private void setListenerDrawScore() {
            findPreference(KEY_PREF_DRAW_SCORE).setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            Log.i(TAG, "setListenerDrawScore() - newValue=" + newValue.toString());
                            String nuValue = newValue.toString();
                            if(!complyBasicRules(nuValue))
                                return false;
                            if(!((Integer.parseInt(nuValue) >= Integer.parseInt(sharedPreferences.getString(KEY_PREF_SHORT_ABSENCE_SCORE, "0"))))){ //DRAW_SCORE >= SHORT_ABSENCE_SCORE
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_points_draw_smaller_than_short_absence));
                                return false;
                            }
                            if(!((Integer.parseInt(nuValue) > Integer.parseInt(sharedPreferences.getString(KEY_PREF_DEFEAT_SCORE, "-2"))))){ //DRAW_SCORE > DEFEAT_SCORE
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_points_draw_not_larger_than_defeat));
                                return false;
                            }
                            if(!((Integer.parseInt(nuValue) >= 0))){ //DRAW_SCORE >= 0
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_points_draw_not_larger_than_zero));
                                return false;
                            }
                            if(!((Integer.parseInt(nuValue) < Integer.parseInt(sharedPreferences.getString(KEY_PREF_WIN_SCORE, "2"))))){ //DRAW_SCORE < WIN_SCORE
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_points_draw_not_smaller_than_win));
                                return false;
                            }
                            return true;
                        }
                    }
            );
        }

        /* Rules:
         * DEFEAT_SCORE < DRAW_SCORE
         * DEFEAT_SCORE <= 0
         */
        private void setListenerDefeatScore() {
            findPreference(KEY_PREF_DEFEAT_SCORE).setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            Log.i(TAG, "setListenerDefeatScore() - newValue=" + newValue.toString());
                            String nuValue = newValue.toString();
                            if(!complyBasicRules(nuValue))
                                return false;
                            if(!((Integer.parseInt(nuValue) < Integer.parseInt(sharedPreferences.getString(KEY_PREF_DRAW_SCORE, "1"))))){ //DEFEAT_SCORE < DRAW_SCORE
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_points_defeat_not_smaller_than_draw));
                                return false;
                            }
                            if(!((Integer.parseInt(nuValue) <= 0))){ //DEFEAT_SCORE <= 0
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_points_defeat_larger_than_zero));
                                return false;
                            }
                            return true;
                        }
                    }
            );
        }

        /* Rules:
         * SHORT_ABSENCE_SCORE >= MEDIUM_ABSENCE_SCORE
         * SHORT_ABSENCE_SCORE <= DRAW_SCORE
         */
        private void setListenerShortAbsenceScore() {
            findPreference(KEY_PREF_SHORT_ABSENCE_SCORE).setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            Log.i(TAG, "setListenerShortAbsenceScore() - newValue=" + newValue.toString());
                            String nuValue = newValue.toString();
                            if(!complyBasicRules(nuValue))
                                return false;
                            if(!((Integer.parseInt(nuValue) >= Integer.parseInt(sharedPreferences.getString(KEY_PREF_MEDIUM_ABSENCE_SCORE, "-1"))))){ //SHORT_ABSENCE_SCORE >= MEDIUM_ABSENCE_SCORE
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_points_short_absence_smaller_than_medium));
                                return false;
                            }
                            if(!((Integer.parseInt(nuValue) <= Integer.parseInt(sharedPreferences.getString(KEY_PREF_DRAW_SCORE, "1"))))){ //SHORT_ABSENCE_SCORE <= DRAW_SCORE
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_points_short_absence_larger_than_draw));
                                return false;
                            }
                            return true;
                        }
                    }
            );
        }

        /* Rules:
         * MEDIUM_ABSENCE_SCORE >= LONG_ABSENCE_SCORE
         * MEDIUM_ABSENCE_SCORE <= SHORT_ABSENCE_SCORE
         */
        private void setListenerMediumAbsenceScore() {
            findPreference(KEY_PREF_MEDIUM_ABSENCE_SCORE).setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            Log.i(TAG, "setListenerMediumAbsenceScore() - newValue=" + newValue.toString());
                            String nuValue = newValue.toString();
                            if(!complyBasicRules(nuValue))
                                return false;
                            if(!((Integer.parseInt(nuValue) >= Integer.parseInt(sharedPreferences.getString(KEY_PREF_LONG_ABSENCE_SCORE, "-2"))))){ //MEDIUM_ABSENCE_SCORE >= LONG_ABSENCE_SCORE
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_points_medium_absence_smaller_than_long));
                                return false;
                            }
                            if(!((Integer.parseInt(nuValue) <= Integer.parseInt(sharedPreferences.getString(KEY_PREF_SHORT_ABSENCE_SCORE, "0"))))){ //MEDIUM_ABSENCE_SCORE <= SHORT_ABSENCE_SCORE
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_points_medium_absence_larger_than_short));
                                return false;
                            }
                            return true;
                        }
                    }
            );
        }

        /* Rules:
         * LONG_ABSENCE_SCORE <= MEDIUM_ABSENCE_SCORE
         */
        private void setListenerLongAbsenceScore() {
            findPreference(KEY_PREF_LONG_ABSENCE_SCORE).setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            Log.i(TAG, "setListenerLongAbsenceScore() - newValue=" + newValue.toString());
                            String nuValue = newValue.toString();
                            if(!complyBasicRules(nuValue))
                                return false;
                            if(!((Integer.parseInt(nuValue) <= Integer.parseInt(sharedPreferences.getString(KEY_PREF_MEDIUM_ABSENCE_SCORE, "-1"))))){ //LONG_ABSENCE_SCORE <= MEDIUM_ABSENCE_SCORE
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_points_long_absence_larger_than_medium));
                                return false;
                            }
                            return true;
                        }
                    }
            );
        }

        /* Rules:
         * MEDIUM_ABSENCE_DURATION >= 0
         * MEDIUM_ABSENCE_DURATION <= LONG_ABSENCE_DURATION
         */
        private void setListenerMediumAbsenceDuration() {
            findPreference(KEY_PREF_MEDIUM_ABSENCE_DURATION).setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            Log.i(TAG, "setListenerMediumAbsenceDuration() - newValue=" + newValue.toString());
                            String nuValue = newValue.toString();
                            if(!complyBasicRules(nuValue))
                                return false;
                            if(!((Integer.parseInt(nuValue) >= 0))){ //MEDIUM_ABSENCE_DURATION >= 0
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_medium_absence_duration_smaller_than_zero));
                                return false;
                            }
                            if(!((Integer.parseInt(nuValue) <= Integer.parseInt(sharedPreferences.getString(KEY_PREF_LONG_ABSENCE_DURATION, "10"))))){ //MEDIUM_ABSENCE_DURATION <= LONG_ABSENCE_DURATION
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_medium_absence_duration_larger_than_long));
                                return false;
                            }
                            return true;
                        }
                    }
            );
        }

        /* Rules:
         * LONG_ABSENCE_DURATION >= MEDIUM_ABSENCE_DURATION
         */
        private void setListenerLongAbsenceDuration() {
            findPreference(KEY_PREF_LONG_ABSENCE_DURATION).setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            Log.i(TAG, "setListenerLongAbsenceDuration() - newValue=" + newValue.toString());
                            String nuValue = newValue.toString();
                            if(!complyBasicRules(nuValue))
                                return false;
                            if(!((Integer.parseInt(nuValue) >= Integer.parseInt(sharedPreferences.getString(KEY_PREF_MEDIUM_ABSENCE_DURATION, "3"))))){ //LONG_ABSENCE_DURATION >= MEDIUM_ABSENCE_DURATION
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_long_absence_duration_smaller_than_medium));
                                return false;
                            }
                            return true;
                        }
                    }
            );
        }

        /*
         * Rules:
         * K >= 1.0
         */
        private void setListenerK() {
            findPreference(KEY_PREF_K).setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            Log.i(TAG, "setListenerK() - newValue=" + newValue.toString());
                            String nuValue = newValue.toString();
                            if(!complyBasicRulesK(nuValue))
                                return false;
                            if(!((Double.parseDouble(nuValue) >= 1.0))){ //K > 1.0
                                if(getActivity() != null)
                                    MyApplication.showToast(getActivity().getApplicationContext(), getResources().getString(R.string.err_k_smaller_than_one));
                                return false;
                            }
                            MyApplication.db.updateCoefficientsWeight(Double.parseDouble(nuValue));
                            return true;
                        }
                    }
            );
        }

        private boolean complyBasicRules(String newValue) {
            return newValue != null && !newValue.isEmpty() && !((newValue.length() == 1) && (newValue.startsWith("-")));   //newValue is just an hyphen
        }

        private boolean complyBasicRulesK(String newValue) {
            return complyBasicRules(newValue) && !((newValue.length() == 1) && (newValue.startsWith(".")));   //newValue is just a dot
        }

        private void setAllPreferencesSummaries(){
            Log.d(TAG, "setAllPreferencesSummaries()");
            for(int i=0; i<getPreferenceScreen().getPreferenceCount(); ++i){
                Preference pref = getPreferenceScreen().getPreference(i);
                if(pref instanceof PreferenceCategory){
                    PreferenceCategory prefCat = (PreferenceCategory) pref;
                    for(int j=0; j<prefCat.getPreferenceCount(); ++j) {
                        String key = prefCat.getPreference(j).getKey();
                        setPreferenceSummary(key, sharedPreferences.getString(key, ""));
                    }
                } else{
                    String key = getPreferenceScreen().getPreference(i).getKey();
                    setPreferenceSummary(key, sharedPreferences.getString(key, ""));
                }
            }
        }

        private void setPreferenceSummary(String key, String value){
            //Log.d(TAG, "setPreferenceSummary() key: "+key+", value: "+value);
            switch (key){
                case KEY_PREF_WIN_SCORE:
                case KEY_PREF_DRAW_SCORE:
                case KEY_PREF_DEFEAT_SCORE:
                case KEY_PREF_SHORT_ABSENCE_SCORE:
                case KEY_PREF_MEDIUM_ABSENCE_SCORE:
                case KEY_PREF_LONG_ABSENCE_SCORE:
                case KEY_PREF_MEDIUM_ABSENCE_DURATION:
                    findPreference(key).setSummary(getResources().getString(R.string.pref_summary_medium_absence_duration)+": "+value);
                    break;
                case KEY_PREF_LONG_ABSENCE_DURATION:
                    findPreference(key).setSummary(getResources().getString(R.string.pref_summary_long_absence_duration)+": "+value);
                    break;
                case KEY_PREF_K:
                    findPreference(key).setSummary(getResources().getString(R.string.pref_summary_k)+": "+value);
                    break;
                default:
                    break;
            }
        }

        private void setResetDefaultPreference(){
            settingsIntent = getActivity().getIntent();
            resetDialogPreference = findPreference(KEY_PREF_DEFAULT);
            resetDialogPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    MyApplication.db.updateCoefficientsWeight(null);
                    try {
                        MyApplication.db.setAllTeamScoresUpdated(false);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Log.wtf(TAG, "setListenerWinScore() - failed to Un-Update Scores");
                    }
                    getActivity().overridePendingTransition(0, 0);
                    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    getActivity().finish();
                    getActivity().overridePendingTransition(0, 0);
                    getActivity().startActivity(settingsIntent);
                    return false;
                }
            });
        }
    }
}
