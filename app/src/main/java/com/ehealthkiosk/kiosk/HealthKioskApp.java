package com.ehealthkiosk.kiosk;

import android.app.Application;
import android.content.Context;

import cn.com.bodivis.scalelib.BleManager;
import com.quickblox.auth.session.QBSettings;

import net.danlew.android.joda.JodaTimeAndroid;

public class HealthKioskApp extends Application {

    public static Context context;
    private static HealthKioskApp sInstance;

    static final String APPLICATION_ID = "80293";
    static final String AUTH_KEY = "tPWasabEppO8jpT";
    static final String AUTH_SECRET = "xVHuJFqghs3ESsB";
    static final String ACCOUNT_KEY = "K5vdM8zVB2R2MsGfT3NH";

    public static HealthKioskApp getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        context = this;
        BleManager.getInstance().init(this);


        QBSettings.getInstance().init(getApplicationContext(), APPLICATION_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

        JodaTimeAndroid.init(this);


    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}