package com.digitalappsacademy.soundmeter;



import static com.digitalappsacademy.soundmeter.MyApplication.showAdIfReady;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.ybq.android.spinkit.SpinKitView;

public class SplashScreen extends AppCompatActivity {

    private SpinKitView progressBar;
    private TextView adLoadingTv;

    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        progressBar = findViewById(R.id.spin_kit);
        adLoadingTv = findViewById(R.id.ad_loading_tv);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showAdIfReady();
                Intent intent = new Intent(SplashScreen.this, OnboardingScreen.class);
                startActivity(intent);
                finish();
            }
        }, 3000);

    }


}