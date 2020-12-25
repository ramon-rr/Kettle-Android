package com.example.kettle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

/**
 * Kettle Main Class Entry point to Frags
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //Intent intent = new Intent(this , MapsActivity.class);
        Intent intent = new Intent(this , LogInActivity.class);
        startActivity(intent);
    }
}