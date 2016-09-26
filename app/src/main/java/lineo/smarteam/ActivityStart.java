package lineo.smarteam;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class ActivityStart extends AppCompatActivity {
    private static final String TAG = "Smarteam Test";
    private static Context mContext;

    // Buttons
    private Button loadButton;
    private Button createButton;
    private Button deleteButton;
    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }
}
