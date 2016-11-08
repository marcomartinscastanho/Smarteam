package lineo.smarteam;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class MyApplication extends Application {
    private static final String TAG = "AppGlobal";
    private static Toast toast;

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static void showToast(Context context, String string){
        if(toast == null){
            toast = new Toast(context);
        }
        else{
            toast.cancel();
        }
        toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
        toast.show();
    }
}
