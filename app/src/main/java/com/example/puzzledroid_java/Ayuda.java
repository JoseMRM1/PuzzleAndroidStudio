package com.example.puzzledroid_java;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class Ayuda extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);

        WebView web = findViewById(R.id.AyudaWebView);
        web.loadUrl("file:///android_asset/Ayuda.html");
    }
}