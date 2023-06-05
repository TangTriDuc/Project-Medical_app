package com.example.covidapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

public class DoYourOwnTests extends AppCompatActivity {
    TextView textViewdata;

    Button Follow;
    String txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_your_own_tests);

        textViewdata = findViewById(R.id.textviewdata);

        Follow = findViewById(R.id.btnFollow);

        //Make function go back in action bar
        getSupportActionBar().setTitle("Do Your Own Test Covid");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Process btn Follow
        Follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data;
                InputStream inputStream = getResources().openRawResource(R.raw.steptestowncovid);
                try {
                    byte[] buffer = new byte[inputStream.available()];
                    while (inputStream.read(buffer) != -1)
                        txt = new String(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                textViewdata.setText(txt);

            }
        });
    }

    //Process ActionBack to go back in action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}