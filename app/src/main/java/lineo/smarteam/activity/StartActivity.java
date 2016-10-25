package lineo.smarteam.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import lineo.smarteam.R;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "Smarteam Test";
    private static Context mContext;

    //TODO: Create Splash Screen http://stackoverflow.com/questions/5486789/how-do-i-make-a-splash-screen
    // This screen will be active while DB is being loaded

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }
}
