package com.digitalappsacademy.soundmeter;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAppOpenAd;
import com.applovin.sdk.AppLovinSdk;

public class MyApplication extends Application implements LifecycleEventObserver, MaxAdListener {
    public static MaxAppOpenAd appOpenAd;
    public static Context context;

    public static boolean isFirstTime = true;


    @Override
    public void onCreate() {
        super.onCreate();

        isFirstTime = true;
        AppLovinSdk.initializeSdk(getApplicationContext());

        context = getApplicationContext();
        appOpenAd = new MaxAppOpenAd(Constant.APP_OPEN_KEY, getApplicationContext());
        appOpenAd.setListener(this);


        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        appOpenAd.loadAd();

    }


    public static void showAdIfReady() {
        if (appOpenAd == null || !AppLovinSdk.getInstance(context).isInitialized()) return;
        if (appOpenAd.isReady()) {
            //progressBar.setVisibility(View.GONE);
            appOpenAd.showAd();

        }
    }

    @Override
    public void onAdLoaded(MaxAd ad) {

    }

    @Override
    public void onAdDisplayed(MaxAd ad) {

    }

    @Override
    public void onAdHidden(MaxAd ad) {
        //  appOpenAd.loadAd();
    }

    @Override
    public void onAdClicked(MaxAd ad) {

    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {

    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {

    }


    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        Log.d("debussa", event.getTargetState() + " true ");

        if (event.getTargetState().equals(Lifecycle.State.RESUMED)) {
            if (isFirstTime) {
                Log.d("debuss", event.getTargetState() + " true ");

                isFirstTime = false;
            } else {
                Log.d("debuss", event.getTargetState() + " ");
                showAdIfReady();
            }

        } else if (event.getTargetState().equals(Lifecycle.State.CREATED)) {
            if (!appOpenAd.isReady()) {
                //progressBar.setVisibility(View.GONE);
                appOpenAd.loadAd();

            }

        }

    }


}
