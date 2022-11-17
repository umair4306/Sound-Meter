package com.digitalappsacademy.soundmeter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;

public class OnboardingScreen extends AppCompatActivity {

    ImageView imageView;
    TextView heading, description;
    int currentPosition = 1;
    Button next_button;
    private MaxAdView MRECAdview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screen);


        imageView = findViewById(R.id.imageView);
        heading = findViewById(R.id.title_text);
        description = findViewById(R.id.slider_desc);
        createMrecAd();

        if (currentPosition == 1) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.soundmeter));
            heading.setText("Sound Meter");
            description.setText("Get the real time frequecny in DB");
            currentPosition++;

        }

        next_button = findViewById(R.id.next_button);

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPosition == 2) {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.swithteme));
                    heading.setText("Switch Theme");
                    description.setText("Set the dark or light theme according to your preferences");
                    currentPosition++;

                } else if (currentPosition == 3) {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.graph));
                    heading.setText("Real Time Graph");
                    description.setText("Watch real time graph of sound around you");
                    next_button.setText("Finish");
                    next_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(OnboardingScreen.this, MainActivity.class));
                            finish();
                        }
                    });
                }
            }
        });
    }


    private void createMrecAd() {
        MRECAdview = new MaxAdView(Constant.MREC_ADD_KEY, MaxAdFormat.MREC, this);
        MRECAdview.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.d("onAdLoaded", "onAdLoaded: ");
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                Log.d("onAdLoaded", "onAdDisplayed: ");
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                Log.d("onAdLoaded", "onAdHidden: ");
            }

            @Override
            public void onAdClicked(MaxAd ad) {
                Log.d("onAdLoaded", "onAdClicked: ");
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Log.d("onAdLoaded", "onAdLoadFailed: ");
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

                Log.d("onAdLoaded", "onAdDisplayFailed: ");
            }
        });

        int width = AppLovinSdkUtils.dpToPx(this, 300);
        int height = AppLovinSdkUtils.dpToPx(this, 250);
        MRECAdview.setLayoutParams(new FrameLayout.LayoutParams(width, height, Gravity.CENTER));

        MRECAdview.setBackgroundColor(Color.WHITE);

        FrameLayout layout = findViewById(R.id.mrec);
        layout.addView(MRECAdview);
        MRECAdview.loadAd();
        MRECAdview.startAutoRefresh();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApplication.isFirstTime = true;
    }

}