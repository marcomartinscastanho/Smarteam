package com.martinscastanho.marco.smarteam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class StatisticActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
    }

    public void headerLegend(View view){
        Map<String, String> toasts = new HashMap<>();
        toasts.put("M", "M: Matches played");
        toasts.put("S", "S: Score");

        String header = String.valueOf(((TextView) view).getText());
        Toast.makeText(StatisticActivity.this, toasts.get(header), Toast.LENGTH_SHORT).show();
    }
}
