package lineo.smarteam.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import java.sql.SQLException;

import lineo.smarteam.db.DataBaseAdapter;

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LoadDataBase().execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    protected class LoadDataBase extends AsyncTask<Context, Integer, String>{
        @Override
        protected String doInBackground(Context... params) {
            try{
                new DataBaseAdapter(getApplicationContext()).open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent startIntent = new Intent(SplashActivity.this, StartActivity.class);
            startActivity(startIntent);
        }
    }
}
