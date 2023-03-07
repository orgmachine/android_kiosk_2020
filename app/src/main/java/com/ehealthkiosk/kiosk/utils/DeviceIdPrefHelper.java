package com.ehealthkiosk.kiosk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class DeviceIdPrefHelper {


    public static String putString(Context ctx, String key, String value) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
        return key;
    }


    public static String getkioskId(Context ctx, String kioskId) {

        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(kioskId, "");
    }


    public static void setkioskId(Context ctx, String kioskId, String device_address) {
        kioskId = Constants.KIOSK_ID;

        Log.d("SPD", "setting " + kioskId + " to " + device_address);
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(kioskId, device_address);
        editor.commit();
    }

    public static boolean getWifiPrinter(Context ctx, Boolean isChecked) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getBoolean(Constants.IS_WIFI_PRINTER, false);
    }


    public static void setWifiPrinter(Context ctx, Boolean isChecked) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.IS_WIFI_PRINTER, isChecked);
        editor.commit();
    }

    public static boolean isLoginSaved(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getBoolean(Constants.LOGIN_PASSWORD_SAVED, false);
    }


    public static void saveLogin(Context ctx, Boolean isChecked) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.LOGIN_PASSWORD_SAVED, isChecked);
        editor.commit();
    }


    public static boolean getRaspberryPi(Context ctx, Boolean isChecked) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getBoolean(Constants.IS_RASPBERRY_PI, false);
    }


    public static void setRaspberryPi(Context ctx, Boolean isChecked) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.IS_RASPBERRY_PI, isChecked);
        editor.commit();
    }



    public static boolean getrailwayEmployee(Context ctx, Boolean isChecked) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getBoolean("Checked", false);
    }


    public static void setrailwayEmployee(Context ctx, Boolean isChecked) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("Checked", isChecked);
        editor.commit();
    }

    public static String getUserEmail(Context ctx, String userEmail) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(userEmail, "");
    }


    public static void setUserEmail(Context ctx, String userEmail, String userEmail_inp) {
        Log.d("SPD", "setting " + userEmail + " to " + userEmail_inp);
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(userEmail, userEmail_inp);
        editor.commit();
    }

    public static String getQbId(Context ctx, String qb_id) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(qb_id, "");
    }


    public static void setQbId(Context ctx, String qb_id_value) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.QB_ID, qb_id_value);
        editor.commit();
    }


    public static String getQblogin(Context ctx, String qb_login) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(qb_login, "");
    }


    public static void setQblogin(Context ctx, String qb_login_value) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.QB_LOGIN, qb_login_value);
        editor.commit();
    }

    public static String getQbpassword(Context ctx, String qb_password) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(qb_password, "");
    }


    public static void setQbpassword(Context ctx, String qb_password_value) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.QB_PASSWORD, qb_password_value);
        editor.commit();
    }



    public static void setDefaultHeight(Context ctx, String default_height, String device_value) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(default_height, device_value);
        editor.commit();
    }

    public static String getDefaultHeight(Context ctx, String default_height) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(default_height, "");
    }

    public static void setTestBill(Context ctx, String test_price, String price_value) {

        Log.d("SPD", "setting " + test_price + " to " + price_value);
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(test_price, price_value);
        editor.commit();
    }

    public static String getTestBill(Context ctx, String default_height) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(default_height, "");
    }

    public static String getAdminPassword(Context ctx, String settings_password) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(settings_password, "");
    }

    public static String getIssuedBy(Context ctx, String issued_by) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(issued_by, "");
    }

    public static String getExpiry(Context ctx, String expiry) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(expiry, "");
    }



    public static void setTestType(Context ctx, String test_type, String test_value) {

        Log.d("SPD", "setting " + test_type + " to " + test_value);
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(test_type, test_value);
        editor.commit();
    }

    public static String getTestType(Context ctx, String test_type) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(test_type, "");
    }


    public static void setBgTestType(Context ctx, String bg_test_type, String bg_test_value) {

        Log.d("SPD", "setting " + bg_test_type + " to " + bg_test_value);
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(bg_test_type, bg_test_value);
        editor.commit();
    }

    public static String getBgTestType(Context ctx, String bg_test_type) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(bg_test_type, "");
    }


    public static void setDeviceAddress(Context ctx, String device_name, String device_address) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(device_name, device_address);
        editor.commit();
    }

    public static String getDeviceAddress(Context ctx, String device_name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(device_name, "");
    }

    public static void setIssuedDate(Context ctx, String issued_date, String issued_inp_value) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(issued_date, issued_inp_value);
        editor.commit();
    }

    public static String getIssuedDate(Context ctx, String issued_date) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(issued_date, "");
    }

    public static void setExpiryDate(Context ctx, String expiry_date, String expiry_inp_value) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(expiry_date, expiry_inp_value);
        editor.commit();
    }

    public static String getExpiryDate(Context ctx, String expiry_date) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(expiry_date, "");
    }

    public static void setPrintUrl(Context ctx, String print_url, String url_value) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(print_url, url_value);
        editor.commit();
    }

    public static String getPrintUrl(Context ctx, String url_value) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.DEVICE_PREF, MODE_PRIVATE);
        return prefs.getString(url_value, "");
    }

    public static void clearAll(Context ctx){
        SharedPreferences.Editor editor = ctx.getSharedPreferences(Constants.DEVICE_PREF, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }


}
