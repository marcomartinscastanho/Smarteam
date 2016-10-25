package lineo.smarteam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ActivitySplash extends Activity {
    private static final String TAG = "ActivitySplash";
    private static Context mContext;
    AppGlobal appGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mContext = this;
        appGlobal = (AppGlobal) ((Activity) mContext).getApplication();
        appGlobal.initialiseDbHelper(mContext);

        // ALL CONFIGURATIONS MUST BE APPLIED HERE

        //in the end, move to ActivityStart
        Intent mainIntent = new Intent(ActivitySplash.this, ActivityStart.class);
        ActivitySplash.this.startActivity(mainIntent);
        ActivitySplash.this.finish();
    }
}
