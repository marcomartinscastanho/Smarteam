package lineo.smarteam.preference;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.widget.EditText;

public class MyEditTextPreference extends EditTextPreference {
    public MyEditTextPreference(Context context) {
        super(context);
    }
    public MyEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onClick() {
        super.onClick();
        EditText et = getEditText();
        et.setSelection(et.getText().length()); //Aligns cursor at the end of the text
    }
}
