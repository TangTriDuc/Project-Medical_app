package com.example.covid19app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class SplashApp extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Process to configuring our window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash_app);

        // Process to call handler to run a task for specific time interval (load)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               startActivity(new Intent(SplashApp.this, MainActivity.class));
               finish();
            }
        }, 2000);

    }
}