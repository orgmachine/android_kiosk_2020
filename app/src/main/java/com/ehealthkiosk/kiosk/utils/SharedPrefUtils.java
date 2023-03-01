package com.ehealthkiosk.kiosk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ehealthkiosk.kiosk.model.profilelist.ProfilesItem;
import com.google.gson.Gson;

public class SharedPrefUtils {

    public static int getPhoneCode(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PERSONAL_PREF, Context.MODE_PRIVATE);
        return prefs.getInt(Constants.PHONE_CODE, 91);
    }


    public static void setPhoneCode(Context ctx, int code) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PERSONAL_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.PHONE_CODE, code);
        editor.commit();
    }

    public static String getPhoneNo(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PERSONAL_PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.PHONE_NO, "");
    }


    public static void setPhoneNo(Context ctx, String profile_id) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PERSONAL_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PHONE_NO, profile_id);
        editor.commit();
    }



    public static String getProfileId(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PERSONAL_PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.PROFILE_ID, "");
    }


    public static void setProfileId(Context ctx, String profile_id) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PERSONAL_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PROFILE_ID, profile_id);
        editor.commit();
    }

    public static ProfilesItem getProfile(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PERSONAL_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(Constants.PROFILE, "");
        ProfilesItem obj = gson.fromJson(json, ProfilesItem.class);
        return obj;
    }


    public static void setProfile(Context ctx, ProfilesItem profilesItem) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PERSONAL_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(profilesItem);
        if (profilesItem != null){
            editor.putString(Constants.PROFILE, json);
        }else{
            editor.putString(Constants.PROFILE, null);
        }

        editor.commit();
    }

    public static String getToken(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PERSONAL_PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.TOKEN, "");
    }


    public static void setToken(Context ctx, String token) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PERSONAL_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.TOKEN, token);
        editor.commit();
    }

    public static int getReferenceHeight(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PERSONAL_PREF, Context.MODE_PRIVATE);
        return prefs.getInt(Constants.REFERENCEHEIGHT, 244);
    }


    public static void setReferenceHeight(Context ctx, int referenceHeight) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PERSONAL_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.REFERENCEHEIGHT, referenceHeight);
        editor.commit();
    }

    public static int getAppState(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PERSONAL_PREF, Context.MODE_PRIVATE);
        return prefs.getInt(Constants.STATE, Constants.STATE_INITIAL);
    }


    public static void setAppState(Context ctx, int state) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PERSONAL_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.STATE, state);
        editor.commit();
    }

    public static void clearSP(Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().commit();
    }
    public static void setHeightFT(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.MANUAL_HEIGHT_FT,name);
        editor.commit();
    }

    public static String getHeightFT(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.MANUAL_HEIGHT_FT, "");
    }

    public static void setHeightInch(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.MANUAL_HEIGHT_INCH,name);
        editor.commit();
    }

    public static String getHeightInch(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.MANUAL_HEIGHT_INCH, "");
    }

    public static void setWeight(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.MANUAL_WEIGHT,name);
        editor.commit();
    }

    public static String getWeight(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.MANUAL_WEIGHT, "");
    }

    public static void setBMI(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.BMI,name);
        editor.commit();
    }

    public static String getBMI(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.BMI, "");
    }

    public static void setHydration(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.HYDRATION,name);
        editor.commit();
    }

    public static String getHydration(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.HYDRATION, "");
    }

    public static void setFat(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.FAT,name);
        editor.commit();
    }

    public static String getFAT(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.FAT, "");
    }

    public static void setBonemass(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.BONEMASS,name);
        editor.commit();
    }

    public static String getBonemass(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.BONEMASS, "");
    }

    public static void setObesity(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.OBESITY, name);
        editor.commit();
    }

    public static String getObesity(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.OBESITY, "");
    }

    public static void setProtein(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PROTEIN, name);
        editor.commit();
    }

    public static String getProtein(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.PROTEIN, "");
    }

    public static void setVFI(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.VFI, name);
        editor.commit();
    }

    public static String getVFI(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.VFI, "");
    }

    public static void setBMR(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.BMR, name);
        editor.commit();
    }

    public static String getBMR(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.BMR, "");
    }

    public static void setBodyAge(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.BODY_AGE, name);
        editor.commit();
    }

    public static String getBodyAge(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.BODY_AGE, "");
    }


    public static void setMuscle(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.MUSCLE,name);
        editor.commit();
    }

    public static String getMuscle(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.MUSCLE, "");
    }

    public static void setDiastolic(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.MANUAL_DIASTOLIC,name);
        editor.commit();
    }

    public static String getDiastolic(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.MANUAL_DIASTOLIC, "");
    }

    public static void setSystolic(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.MANUAL_SYSTOLIC,name);
        editor.commit();
    }

    public static String getSystolic(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.MANUAL_SYSTOLIC, "");
    }

    public static void setPulse(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.MANUAL_PULSE,name);
        editor.commit();
    }

    public static String getPulse(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.MANUAL_PULSE, "");
    }

    public static void setSp02(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.MANUAL_Sp02,name);
        editor.commit();
    }

    public static String getSp02(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.MANUAL_Sp02, "");
    }

    public static void setTemp(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.MANUAL_TEMPERATURE,name);
        editor.commit();
    }

    public static String getTemp(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.MANUAL_TEMPERATURE, "");
    }

    public static void setGlucose(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.MANUAL_GLUCOSE, name);
        editor.commit();
    }

    public static String getGlucose(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.MANUAL_GLUCOSE, "");
    }

    public static void setHb(Context ctx, String name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.MANUAL_HB, name);
        editor.commit();
    }

    public static String getHb(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(Constants.MANUAL_HB, "");
    }


    public static void setDeviceAddress(Context ctx, String device_name, String device_address) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(device_name, device_address);
        editor.commit();
    }

    public static String getDeviceAddress(Context ctx, String device_name) {
        SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        return prefs.getString(device_name, "");
    }

    public static void clearAll(Context ctx){
        SharedPreferences.Editor editor = ctx.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

}
