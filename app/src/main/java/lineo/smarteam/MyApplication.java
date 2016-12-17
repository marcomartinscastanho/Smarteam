package lineo.smarteam;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import lineo.smarteam.db.DataBase;

public class MyApplication extends Application {
    private static Toast toast;
    public static DataBase db;

    public enum ResultType {
        Win("W"),
        Draw("D"),
        Defeat("L"),
        Absence("-"),   //short or any
        MediumAbsence("+"),
        LongAbsence("*");

        private final String res;
        ResultType(String r){
            res = r;
        }
        public boolean equals(String otherRes){
            return otherRes != null && res.equals(otherRes);
        }
        public String toString(){
            return this.res;
        }
    }

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
