package com.creative.routetracker;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.creative.routetracker.appdata.MydApplication;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if (MydApplication.getInstance().getPrefManger().getUserProfile() != null) {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                    return;
                }else{
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                    return;
                }
            }
        }, 3000);
    }
}
