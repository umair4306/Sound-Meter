package com.digitalappsacademy.soundmeter;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {
    ArrayList<Entry> yVals;
    boolean refreshed = false;
    Speedometer speedometer;
    //    public static Typeface tf;
    LineChart mChart;
    TextView minVal;
    TextView maxVal;
    TextView mmVal;
    TextView curVal;
    long currentTime = 0;
    long savedTime = 0;
    boolean isChart = false;
    boolean isMoney = false;
    /* Decibel */
    private boolean bListener = true;
    private boolean isThreadRun = true;
    private Thread thread;
    float volume = 10000;
    int refresh = 0;
    private MyMediaRecorder mRecorder;
    ImageView menufile;
    private MaxAdView MRECAdview;

    LinearLayout changeBackgoud;
    CardView changeLinearBackgroud;

    boolean changeTheme = true;
    private static final int MICE_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;



    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DecimalFormat df1 = new DecimalFormat("####.0");
            if (msg.what == 1) {
                if (!isChart) {
                    initChart();
                    return;
                }
                speedometer.refresh();
                minVal.setText(df1.format(World.minDB));
                mmVal.setText(df1.format((World.minDB + World.maxDB) / 2));
                maxVal.setText(df1.format(World.maxDB));
                curVal.setText(df1.format(World.dbCount));
                updateData(World.dbCount, 0);
                if (refresh == 1) {
                    long now = new Date().getTime();
                    now = now - currentTime;
                    now = now / 1000;
                    refresh = 0;
                } else {
                    refresh++;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//        }
        setContentView(R.layout.activity_main);






        if (Build.VERSION.SDK_INT >= 23) {
            checkMultiplePermissions();
        }

        changeBackgoud = findViewById(R.id.changeBackgroud);
        changeLinearBackgroud = findViewById(R.id.changeLinear);

//        tf = Typeface.createFromAsset(this.getAssets(), "fonts/Let_s go Digital Regular.ttf");
        minVal = (TextView) findViewById(R.id.minval);
//        minVal.setTypeface(tf);
        mmVal = (TextView) findViewById(R.id.mmval);
//        mmVal.setTypeface(tf);
        maxVal = (TextView) findViewById(R.id.maxval);
//        maxVal.setTypeface(tf);
        curVal = (TextView) findViewById(R.id.curval);
//        curVal.setTypeface(tf);

        menufile = (ImageView) findViewById(R.id.menu);

        menufile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });
        createMrecAd();


        speedometer = (Speedometer) findViewById(R.id.speed);
        mRecorder = new MyMediaRecorder();
    }

    private void updateData(float val, long time) {
        if (mChart == null) {
            return;
        }
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            LineDataSet set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            Entry entry = new Entry(savedTime, val);
            set1.addEntry(entry);
            if (set1.getEntryCount() > 200) {
                set1.removeFirst();
                set1.setDrawFilled(true);
            }
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();
            savedTime++;
        }
    }

    private void initChart() {
        if (mChart != null) {
            if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {
                savedTime++;
                isChart = true;
            }
        } else {
            currentTime = new Date().getTime();
            mChart = (LineChart) findViewById(R.id.chart1);
            mChart.setViewPortOffsets(50, 20, 5, 60);

            // no description text
//            mChart.setDescription(new Description());
            // enable touch gestures
            mChart.setTouchEnabled(true);
            // enable scaling and dragging
            mChart.setDragEnabled(false);
            mChart.setScaleEnabled(true);
            // if disabled, scaling can be done on x- and y-axis separately
            mChart.setPinchZoom(false);
            mChart.setDrawGridBackground(false);
            //mChart.setMaxHighlightDistance(400);
            XAxis x = mChart.getXAxis();
            x.setLabelCount(8, false);
            x.setEnabled(true);
//            x.setTypeface(tf);
            x.setTextColor(Color.GREEN);
            x.setPosition(XAxis.XAxisPosition.BOTTOM);
            x.setDrawGridLines(false);
            x.setAxisLineColor(Color.GREEN);
            YAxis y = mChart.getAxisLeft();
            y.setLabelCount(6, false);
            y.setTextColor(Color.GREEN);
//            y.setTypeface(tf);
            y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            y.setDrawGridLines(false);
            y.setAxisLineColor(Color.GREEN);
            y.setAxisMinValue(0);
            y.setAxisMaxValue(120);
            mChart.getAxisRight().setEnabled(true);
            yVals = new ArrayList<Entry>();
            yVals.add(new Entry(0, 0));
            LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
//            set1.setValueTypeface(tf);
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.02f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setCircleColor(Color.GREEN);
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.GREEN);
            set1.setFillColor(Color.GREEN);
            set1.setFillAlpha(255);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new DefaultFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });
            LineData data;
            if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {
                data = mChart.getLineData();
                data.clearValues();
                data.removeDataSet(0);
                data.addDataSet(set1);
            } else {
                data = new LineData(set1);
            }

            data.setValueTextSize(9f);
            data.setDrawValues(false);
            mChart.setData(data);
            mChart.getLegend().setEnabled(false);
            mChart.animateXY(2000, 2000);
            // dont forget to refresh the drawing
            mChart.invalidate();
            isChart = true;
        }

    }

    /* Sub-chant analysis */
    private void startListenAudio() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isThreadRun) {
                    try {
                        if (bListener) {
                            volume = mRecorder.getMaxAmplitude();  //Get the sound pressure value
                            if (volume > 0 && volume < 1000000) {
                                World.setDbCount(20 * (float) (Math.log10(volume)));  //Change the sound pressure value to the decibel value
                                // Update with thread
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            }
                        }
                        if (refreshed) {
                            Thread.sleep(1200);
                            refreshed = false;
                        } else {
                            Thread.sleep(200);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        bListener = false;
                    }
                }
            }
        });
        thread.start();
    }

    public void startRecord(File fFile) {
        try {
            mRecorder.setMyRecAudioFile(fFile);
            if (mRecorder.startRecorder()) {
                startListenAudio();
            } else {
                Toast.makeText(this, getString(R.string.activity_recStartErr), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.activity_recBusyErr), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {

                    case R.id.refreshbutton:
                        refreshed = true;
                        World.minDB = 100;
                        World.dbCount = 0;
                        World.lastDbCount = 0;
                        World.maxDB = 0;
                        initChart();
                        break;

                    case R.id.switch_theme:
                        if (changeTheme) {
                            changeBackgoud.setBackgroundColor(getResources().getColor(R.color.white));
                            changeLinearBackgroud.setCardBackgroundColor(getResources().getColor(R.color.internal_velocimeter));
                            changeTheme = false;

                        } else {
                            changeBackgoud.setBackgroundResource(R.drawable.bg_page);
                            changeLinearBackgroud.setCardBackgroundColor(getResources().getColor(R.color.white));
                            changeTheme = true;


                        }
                        break;
                    case R.id.infobutton:

                        InfoDialog.Builder builder = new InfoDialog.Builder(MainActivity.this);
                        builder.setMessage(getString(R.string.activity_infobull));
                        builder.setTitle(getString(R.string.activity_infotitle));
                        builder.setNegativeButton(getString(R.string.activity_infobutton),
                                new android.content.DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                        break;


                    case R.id.Rate_us:
                        Uri uri = Uri.parse(Constant.Rate_us_Link + SplashScreen.PACKAGE_NAME); // missing 'http://' will cause crashed
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        break;

                    case R.id.Privacy_Policy:
                        Uri uri1 = Uri.parse(Constant.Policy_Link); // missing 'http://' will cause crashed
                        Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
                        startActivity(intent1);
                        break;

                    case R.id.AppInfo:
                        startActivity(new Intent(MainActivity.this, AppInfo.class));
                        break;

                    case R.id.More_Apps:

                        Uri uri2 = Uri.parse(Constant.More_Apps_Link); // missing 'http://' will cause crashed
                        Intent intent2 = new Intent(Intent.ACTION_VIEW, uri2);
                        startActivity(intent2);
                        break;


                }

                return false;
            }
        });

        popup.show();

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

        FrameLayout layout = (FrameLayout) findViewById(R.id.mrec);
        layout.addView(MRECAdview);
        MRECAdview.loadAd();
        MRECAdview.startAutoRefresh();

    }


    @Override
    protected void onResume() {
        super.onResume();
        File file = FileUtil.createFile("temp.amr");
        if (file != null) {
            startRecord(file);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.activity_recFileErr), Toast.LENGTH_LONG).show();
        }
        bListener = true;
    }

    /**
     * Stop recording
     */
    @Override
    protected void onPause() {
        super.onPause();
        bListener = false;
        mRecorder.delete(); //Stop recording and delete the recording file
        thread = null;
        isChart = false;
    }

    @Override
    protected void onDestroy() {
        if (thread != null) {
            isThreadRun = false;
            thread = null;
        }
        mRecorder.delete();
        super.onDestroy();
    }


    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        } else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkMultiplePermissions() {

        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionsNeeded = new ArrayList<String>();
            List<String> permissionsList = new ArrayList<String>();

            if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO)) {
                permissionsNeeded.add("GPS");
            }

            if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionsNeeded.add("Read Storage");
            }

            if (permissionsList.size() > 0) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return;
            }
        }
    }



    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= 23)

            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);

                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MICE_PERMISSION_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(MainActivity.this, "Mice Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Mice Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApplication.isFirstTime = true;
    }
}
