package com.martinscastanho.marco.smarteam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TeamHelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_help);

        setTitle(R.string.title_help);

        WebView webView = findViewById(R.id.helpWebView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/help.html");
    }
}
