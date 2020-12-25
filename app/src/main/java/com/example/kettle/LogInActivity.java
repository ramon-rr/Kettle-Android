package com.example.kettle;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kettle.ui.fragments.SignInFragment;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;

public class LogInActivity extends AppCompatActivity {

    private SignInFragment siFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        siFragment = new SignInFragment();

        System.out.println("XD");
    }
}
