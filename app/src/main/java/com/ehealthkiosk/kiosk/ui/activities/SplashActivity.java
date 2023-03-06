package com.ehealthkiosk.kiosk.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.ui.activities.login.LoginActivity;
import com.ehealthkiosk.kiosk.ui.activities.selectprofile.SelectProfileActivity;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";
    private String mSource;
    private static final long SPLASH_TIME_OUT = 5000; /* Splash period in millis */


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

//        startLockTask();
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                finish();
                return;
            }
        }

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                init();
            }
        }, SPLASH_TIME_OUT);

    }

    private void init() {
        splashViewFinish();
    }

    public void splashViewFinish() {

        Intent intent;

        //Log.d(TAG, "apikey: " + SharedPrefUtils.getApiKey(this));


        if (SharedPrefUtils.getAppState(this) == Constants.STATE_INITIAL) {
            Intent i = new Intent(this, WelcomeActivity.class);
            startActivity(i);
        } else if (SharedPrefUtils.getToken(this) != null && !SharedPrefUtils.getToken(this).equalsIgnoreCase("")) {
            startApp();
        }

        finish();

    }

    private void startApp() {
        Intent intent = new Intent(this, SelectProfileActivity.class);
        startActivity(intent);
    }
}
