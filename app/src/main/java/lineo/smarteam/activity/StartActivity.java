package lineo.smarteam.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import lineo.smarteam.R;

public class StartActivity extends Activity {
    private static final String TAG = "StartActivity";
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }
}
