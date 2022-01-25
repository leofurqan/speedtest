package com.example.speedtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RelativeLayout;

import com.droidnet.DroidListener;
import com.droidnet.DroidNet;
import com.google.android.material.snackbar.Snackbar;

public class SplashActivity extends AppCompatActivity{

    RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        DroidNet.init(this);

        mainLayout = findViewById(R.id.layout_main);
        main();
    }

    private void main() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}