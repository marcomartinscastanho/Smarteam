package lineo.smarteam.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import lineo.smarteam.AppGlobal;
import lineo.smarteam.R;

public class SplashActivity extends Activity {
    private static final String TAG = "ActivitySplash";
    AppGlobal appGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //in the end, move to ActivityStart
        Intent mainIntent = new Intent(SplashActivity.this, StartActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        SplashActivity.this.finish();
    }

    protected class LoadDataBase extends AsyncTask<Context, Integer, String>{
        @Override
        protected String doInBackground(Context... params) {
            return null;
        }
    }


}
