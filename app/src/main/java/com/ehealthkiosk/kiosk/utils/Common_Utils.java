package com.ehealthkiosk.kiosk.utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestInterface;
import com.ehealthkiosk.kiosk.model.PrintParam;
import com.ehealthkiosk.kiosk.model.commonresponse.Status;
import com.ehealthkiosk.kiosk.widgets.MaterialProgress.TransparentProgressDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.ValidationError;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.ehealthkiosk.kiosk.HealthKioskApp.context;


public class Common_Utils {

    public static TransparentProgressDialog progressDialog;
    public static ProgressDialog pd;
    private static final String TAG = Common_Utils.class.getSimpleName();
    public static Toast toast;
    private static long lastClickTime = 0;


    public static boolean isNotNullOrEmpty(String string) {

        if (string != null
                && !string.equalsIgnoreCase("null")
                && !string.isEmpty()
                //&& !str.contains("null")
                && !string.equalsIgnoreCase("")
                && !string.equalsIgnoreCase(" ")) {

            return true;
        } else {
            return false;
        }
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) HealthKioskApp.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static float dp2px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static float sp2px(Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics());
    }

    public static float px2dp(Context context, float px) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static SpannableString getUnderline(Context context, String str_temp) {

        TextPaint tp = new TextPaint();

        tp.linkColor = context.getResources().getColor(R.color.colorAccent);
        UnderlineSpan us = new UnderlineSpan();
        us.updateDrawState(tp);

        SpannableString content = new SpannableString(str_temp);
        content.setSpan(us, 0, str_temp.length(), 0);

        return content;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (!isNotNullOrEmpty(target.toString())) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /**
     * Method to convert MM to MMM
     */
    public static String getMonthName(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    public static Date stringToDate(String dtStart) {
        //  String dtStart = "2010-10-15T09:27:37Z";
        Date dateFinal = null;
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
        try {
            dateFinal = format.parse(dtStart);
            //  System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFinal;
    }

    public static Date stringToDateTime24Hrs(Date dtStart) {
        //  String dtStart = "2010-10-15T09:27:37Z";
        Date dateFinal = null;
        // SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
        SimpleDateFormat format1 = new SimpleDateFormat("MM-dd-yyyy hh:mm aa");

        try {
            //  dateFinal = format.parse(dtStart);
            //  dateFinal = format1.parse(dateFinal.toString());
            String date = format1.format(dtStart);
            dateFinal = format1.parse(date);
            //  System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFinal;
    }


    public static Date stringToDatee(String dtStart) {
        //  String dtStart = "2010-10-15T09:27:37Z";
        Date dateFinal = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dateFinal = format.parse(dtStart);
            //  System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFinal;
    }

    public static Date stringToDateValidation(String dtStart) {
        //  String dtStart = "2010-10-15T09:27:37Z";
        Date dateFinal = null;
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        try {
            dateFinal = format.parse(dtStart);
            //  System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFinal;
    }

    public static String formateDate(String dateString) throws ParseException {
        // String dateString = "2015-07-16 17:07:21";
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");

        // use SimpleDateFormat to define how to PARSE the INPUT
        Date date = sdf.parse(dateString);

        // at this point you have a Date-Object with the value of
        // 1437059241000 milliseconds
        // It doesn't have a format in the way you think

        // use SimpleDateFormat to define how to FORMAT the OUTPUT
        // System.out.println("+++1 " + sdf.format(date));

        sdf = new SimpleDateFormat("dd/MM/yyyy");
        //  System.out.println("+++1 2" + sdf.format(date));
        return sdf.format(date);
        // ....
    }

    public static String parseServerDate(String dateString) throws ParseException {
        // String dateString = "2015-07-16 17:07:21";
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");

        // use SimpleDateFormat to define how to PARSE the INPUT
        Date date = sdf.parse(dateString);

        // at this point you have a Date-Object with the value of
        // 1437059241000 milliseconds
        // It doesn't have a format in the way you think

        // use SimpleDateFormat to define how to FORMAT the OUTPUT
        //  System.out.println("+++1 " + sdf.format(date));

        sdf = new SimpleDateFormat("MM/dd/yyyy");
        //  System.out.println("+++1 2" + sdf.format(date));
        return sdf.format(date);
        // ....
    }

    public static String formateMM_DD_YYYY(String dateString) throws ParseException {
        // String dateString = "2015-07-16 17:07:21";
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm a");

        // use SimpleDateFormat to define how to PARSE the INPUT
        Date date = sdf.parse(dateString);

        // at this point you have a Date-Object with the value of
        // 1437059241000 milliseconds
        // It doesn't have a format in the way you think

        // use SimpleDateFormat to define how to FORMAT the OUTPUT
        // System.out.println("+++1 " + sdf.format(date));

        sdf = new SimpleDateFormat("MM-dd-yyyy");
        //  System.out.println("+++1 2" + sdf.format(date));
        return sdf.format(date);
        // ....
    }

    public static String formateServerDate(String dateString) throws ParseException {
        // String dateString = "2015-07-16 17:07:21";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // use SimpleDateFormat to define how to PARSE the INPUT
        Date date = sdf.parse(dateString);

        // at this point you have a Date-Object with the value of
        // 1437059241000 milliseconds
        // It doesn't have a format in the way you think

        // use SimpleDateFormat to define how to FORMAT the OUTPUT
        //  System.out.println("+++1 " + sdf.format(date));

        sdf = new SimpleDateFormat("MM/dd/yyyy");
        //  System.out.println("+++1 2" + sdf.format(date));
        return sdf.format(date);
        // ....
    }


    public static String getTimeFromDate(String dateString) throws ParseException {
        // String dateString = "2015-07-16 17:07:21";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");

        // use SimpleDateFormat to define how to PARSE the INPUT
        Date date = sdf.parse(dateString);

        // at this point you have a Date-Object with the value of
        // 1437059241000 milliseconds
        // It doesn't have a format in the way you think

        // use SimpleDateFormat to define how to FORMAT the OUTPUT
        // System.out.println("+++1 " + sdf.format(date));

        sdf = new SimpleDateFormat("HH:mm:ss a");
        // System.out.println("+++1 2" + sdf.format(date));
        return sdf.format(date);
        // ....
    }

    public static Calendar getCalendar(String dates) {
        SimpleDateFormat sdf1 = new SimpleDateFormat();
        sdf1.applyPattern("MM-dd-yyyy");
        Date date = null;
        // System.out.println("++++ date " + calendarEventsArrayList.get(j));
        try {
            date = sdf1.parse(dates);
        } catch (ParseException e) {
            System.out.println("++++ e " + e.getMessage());
            e.printStackTrace();
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }

    public static String parseSelectedDate(String dates) {


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // use SimpleDateFormat to define how to PARSE the INPUT
        Date date = null;
        try {
            date = sdf.parse(dates);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // at this point you have a Date-Object with the value of
        // 1437059241000 milliseconds
        // It doesn't have a format in the way you think

        // use SimpleDateFormat to define how to FORMAT the OUTPUT
        // System.out.println("+++1 " + sdf.format(date));

        sdf = new SimpleDateFormat("MM-dd-yyyy");
        // System.out.println("+++1 2" + sdf.format(date));
        return sdf.format(date);

    }
/*
    public static Calendar getCalendarDate(Date dates){
        SimpleDateFormat sdf1 = new SimpleDateFormat();
        sdf1.applyPattern("yyyy-MM-dd");
        java.util.Date date = null;
        //System.out.println("++++ date " + calendarEventsArrayList.get(j).getStart_date());
        try {
            date = sdf1.parse(dates.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }*/

    public static long getStringToLong(String startDate) {
        // System.out.println("+++++++ startDate " + startDate);
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        long milliseconds = 0;
        try {
            Date d = f.parse(startDate);
            milliseconds = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }

    public static String getFirstAndLastDayOfMonth(String formattedDate, int yearpart, int monthPart, int dateDay) {
        Calendar cal = Calendar.getInstance();
        cal.set(yearpart, monthPart, dateDay);
        // System.out.println(" ++++ Calendar.DATE " + Calendar.DATE);
        int res = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        // System.out.println("Today's Date = " + cal.getTime());
        //  System.out.println("Last Date of the current month = " + res);

        String[] split = formattedDate.split("/");

        return split[0] + "/" + res + "/" + split[2];

    }

    public static boolean pathValidation(String path) {
        if (path.contains("http://") || path.contains("https://")) {
            return true;
        } else {
            return false;
        }
    }


    public static void getCircularImageFromServer(ImageView imageview, String url, int drawble) {

       /* if (!isNotNullOrEmpty(url)) {
            imageview.setImageResource(drawble);
            return;
        }

        if (!pathValidation(url)) {
            url = RestClient.SERVER_APIURL_IMAGE + url;
        }

        //System.out.println(("image " + url);
        RequestOptions options = new RequestOptions()
                .placeholder(drawble)
                .error(drawble)
                .centerCrop()
                .dontAnimate()
                .priority(Priority.HIGH);

        Glide.with(AthleteFoundryApp.getAppContext())
                .load(url)
                .apply(options)
                .into(imageview);*/

    }

    public static void getCircularImageFromServer(ImageView imageview, String url, Drawable drawble) {

        if (!isNotNullOrEmpty(url)) {
            imageview.setImageDrawable(drawble);
            return;
        }

        //System.out.println(("image " + url);
        RequestOptions options = new RequestOptions()
                .placeholder(drawble)
                .error(drawble)
                .centerCrop()
                .dontAnimate()
                .priority(Priority.HIGH);

        Glide.with(HealthKioskApp.getAppContext())
                .load(url)
                .apply(options)
                .into(imageview);

    }

   /* public static Drawable getTextDrawable(String text) {
        if (!Common_Utils.isNotNullOrEmpty(text))
            text = "";//AppConstant.App_Name;

        TextDrawable drawable = TextDrawable.builder().beginConfig()
                .toUpperCase()
                .width(160)  // width in px
                .height(160) // height in px
                .endConfig()
                .buildRound(getTagName(text), AthleteFoundryApp.getAppContext().getResources().getColor(R.color.primary));

        return drawable;
    }*/

    public static String getTagName(String x) {
        String tempText = "";
        if (isNotNullOrEmpty(x)) {
            String[] myName = x.split(" ");
            for (int i = 0; i < (myName.length > 2 ? 2 : myName.length); i++) {
                String s = myName[i];
                tempText = tempText + String.valueOf(s.charAt(0));
            }
        }
        return tempText;
    }

    public static String getFullName(String fname, String sname) {
        String name = "";
        if (Common_Utils.isNotNullOrEmpty(fname) &&
                Common_Utils.isNotNullOrEmpty(sname)) {
            name = fname + " " + sname;
        } else if (Common_Utils.isNotNullOrEmpty(fname)) {
            name = fname;
        }

        return Common_Utils.toCapitalise(name);
    }

    public static String getTimeData(String hr, String min) {
        String name = "";
        if (Common_Utils.isNotNullOrEmpty(hr) &&
                !hr.equalsIgnoreCase("0") &&
                Common_Utils.isNotNullOrEmpty(min) &&
                !min.equalsIgnoreCase("0")) {
            name = hr + "Hr " + min + "Min";
        } else if (Common_Utils.isNotNullOrEmpty(hr) &&
                !hr.equalsIgnoreCase("0")) {
            name = hr + "Hr";
        } else if (Common_Utils.isNotNullOrEmpty(min) &&
                !min.equalsIgnoreCase("0")) {
            name = min + "Min";
        }

        return name;
    }

    public static String toCamelCase(final String init) {
        if (!isNotNullOrEmpty(init))
            return "";

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }

    public static String toCapitalise(String string) {
        if (!isNotNullOrEmpty(string))
            return "";

        string = removeUnderline(string);

        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public static String removeUnderline(String string) {
        if (isNotNullOrEmpty(string))
            return string.replaceAll("_", " ");
        else
            return "";
    }


    public static boolean isNotFrequentClick() {

        if (SystemClock.elapsedRealtime() - lastClickTime < 500) {
            return false;
        }

        lastClickTime = SystemClock.elapsedRealtime();
        return true;
    }


    public static long getLongDate(String startdate) {
        try {
            String dateString = "30/09/2014";
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm a");
            Date date = sdf.parse(startdate);
            // System.out.println("+++++ test date " + date);
            return date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getLongAllDayDate(String startdate) {
        try {
            String dateString = "30/09/2014";
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            Date date = sdf.parse(startdate);

            return date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //sdfDate.setTimeZone(getTimeZone());
        Calendar c = Calendar.getInstance();
        //  System.out.println("Current time => " + c.getTime());
        String strDate = sdfDate.format(c.getTime());
        return strDate;
    }

    public static String getDeviceName() {
        String deviceName = Build.MODEL;
        return deviceName;
    }

    public static String getDeviceVersion() {
        String deviceName = Build.VERSION.RELEASE;
        return deviceName;
    }

    @SuppressLint("MissingPermission")
    public static String getDeviceID() {
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits

        return m_szDevIDShort;
    }

    public static String getAppVersion(Context context) {
        String result = "";

        try {
            result = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }


    public static void showToast(String data) {

        try {
            if (!Common_Utils.isNotNullOrEmpty(data))
                return;

            if (toast == null) {
                toast = Toast.makeText(HealthKioskApp.getAppContext(), data, Toast.LENGTH_SHORT);
            }

            if (!toast.getView().isShown()) {
                toast.setText(data);
                toast.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showProgress(Context context) {
        //System.out.println(("show");
        if (progressDialog != null && progressDialog.isShowing())
            hideProgress();

        progressDialog = new TransparentProgressDialog(context);
        progressDialog.show();
    }

    public static void showProgress(Context context, boolean cantDismiss) {
        //System.out.println(("show");
        if (progressDialog != null && progressDialog.isShowing())
            hideProgress();

        progressDialog = new TransparentProgressDialog(context);
        if(cantDismiss){
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    public static void hideProgress() {
        //System.out.println(("hide");
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSelection(EditText editText) {
        int length = editText.getText().length();
        editText.setSelection(length);
    }

    public static void showError(Activity activity, List<ValidationError> errors) {
        boolean isFirst = false;
        EditText editText;
        for (ValidationError error : errors) {
            View view = error.getView();
            ViewParent viewParent = error.getView().getParent().getParent();
            String message = error.getCollatedErrorMessage(context);
            if (!isFirst) {
                if (view instanceof EditText) {
                    editText = ((EditText) view);
                    Common_Utils.setSelection(editText);
                    editText.requestFocus();
                    isFirst = true;
                }
            }

            if (viewParent instanceof TextInputLayout) {
                ((TextInputLayout) viewParent).setError(message);
                ((TextInputLayout) viewParent).setErrorEnabled(true);

            } else {
                Common_Utils.showToast(message);
            }
        }
    }

    public static void getCircularImageFromLocal(ImageView imageview, String url, int drawble) {

        if (!isNotNullOrEmpty(url)) {
            imageview.setImageResource(drawble);
            return;
        }

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(drawble)
                .error(drawble)
                .dontAnimate()
                .priority(Priority.HIGH);

        Glide.with(HealthKioskApp.getAppContext())
                .load(url)
                .apply(options)
                .into(imageview);

    }

    public static double convertFeetandInchesToCentimeter(String feet, String inches) {
        double heightInFeet = 0;
        double heightInInches = 0;
        try {
            if (feet != null && feet.trim().length() != 0) {
                heightInFeet = Double.parseDouble(feet);
            }
            if (inches != null && inches.trim().length() != 0) {
                heightInInches = Double.parseDouble(inches);
            }
        } catch (NumberFormatException nfe) {

        }
        return (heightInFeet * 30.48) + (heightInInches * 2.54);
    }

    public static String centimeterToFeet(String centemeter) {
        int feetPart = 0;
        int inchesPart = 0;
        if (!TextUtils.isEmpty(centemeter)) {
            double dCentimeter = Double.valueOf(centemeter);
            feetPart = (int) Math.floor((dCentimeter / 2.54) / 12);
            System.out.println((dCentimeter / 2.54) - (feetPart * 12));
            inchesPart = (int) Math.ceil((dCentimeter / 2.54) - (feetPart * 12));
        }
        return String.format("%d", feetPart);
    }

    public static String centimeterToInch(String centemeter) {
        int feetPart = 0;
        int inchesPart = 0;
        if (!TextUtils.isEmpty(centemeter)) {
            double dCentimeter = Double.valueOf(centemeter);
            feetPart = (int) Math.floor((dCentimeter / 2.54) / 12);
            System.out.println((dCentimeter / 2.54) - (feetPart * 12));
            inchesPart = (int) Math.ceil((dCentimeter / 2.54) - (feetPart * 12));
        }
        return String.format(" %d", inchesPart);
    }

    final static double KG_TO_LBS = 2.20462;
    final static double M_TO_IN = 39.3701;


    public static double metricFormula(double m, double kg) {
        double result = 0;

        result = kg / (Math.pow(m, 2));

        return result;
    }


    public static double imperialFormula(double m, double kg) {
        double result = 0;
        // convert kg to lbs and m to in
        double lbs = Math.round(kg * KG_TO_LBS);
        double in = Math.round((m * M_TO_IN) * 100);

        result = (lbs / (Math.pow((in / 100), 2))) * 703;

        return result;
    }


    public static void DownloadFile(String fileURL, File directory) {
        try {

            FileOutputStream f = new FileOutputStream(directory);
            URL u = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            InputStream in = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getReportsStoragePath() {
        String filePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + HealthKioskApp.getAppContext().getResources().getString(R.string.app_name)
                + File.separator + "Media"
                + File.separator + "Documents"
                + File.separator + "Reports";

        return filePath;
    }

    //method to enable bluetooth
    public static void enableBluetooth() {
        Log.d("TAG", "will try to enable bluetooth");
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();

            Log.d("TAG", "bluetooth enabled");
        }
    }

    //method to disable bluetooth
    public static void disableBluetooth() {
        Log.d("TAG", "will try to disable bluetooth");
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();

            Log.d("TAG", "bluetooth disabled");
        }
    }

    public static boolean isLocationEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
// This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
// This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }

    public static void displayPromptForEnablingGPS(final Activity activity, String msg) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = msg;

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    public static void setProgressDialog(Context mContext) {
        ProgressDialog pd = new ProgressDialog(mContext);
        pd.setMessage("Please wait while device is connecting.....");
        pd.show();
    }

    public static void dismissProgressDialog(Context mContext) {
        try {
            if (pd != null) {
                pd.dismiss();
                pd = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void responseCodePromp(View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static void callPrintAPI(String url, Context context) {
        Common_Utils.showProgress(context);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DeviceIdPrefHelper.getPrintUrl(context, Constants.PRINT_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestInterface service = retrofit.create(RestInterface.class);

        PrintParam pp = new PrintParam();
        pp.setUrl(url);
        Call<Status> call = service.print(pp);
        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                Toast.makeText(context, context.getResources().getString(R.string.print_job_success), Toast.LENGTH_SHORT).show();
                Common_Utils.hideProgress();
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.print_job_fail), Toast.LENGTH_SHORT).show();
                Common_Utils.hideProgress();
            }
        });

    }

    public static void openLinkInBrowser(Context context, String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    public static void downloadFile(Context context, String url){
        Uri uri = Uri.parse(url);
        DownloadManager.Request r = new DownloadManager.Request(uri);

        String path = uri.getPath();
        String filename = path.substring(path.lastIndexOf("/")+1);


        String root = Environment.getExternalStorageDirectory().toString();

        r.setDestinationInExternalPublicDir(root, filename);
        r.allowScanningByMediaScanner();
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(r);
    }


    public static void playAudio(String filePath) {
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(filePath);
            mp.prepare();
            mp.setLooping(false);
            mp.start();


        } catch (IOException e) {
            Log.e("pranav", "prepare() failed" + e.toString());
        }
    }
    public static void playAudio(String filePath, MediaPlayer mp) {
        try {
            mp.setDataSource(filePath);
            mp.prepare();
            mp.setLooping(false);
            mp.start();


        } catch (IOException e) {
            Log.e("pranav", "prepare() failed" + e.toString());
        }
    }
    public static void playAudio(String filePath, SeekBar seekBar, Button button) {
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(filePath);

            button.setEnabled(false);
            button.setText("Loading..");

            Log.d("pranav", "calling prepare");
            mp.prepare();
            mp.setLooping(false);

            Log.d("pranav", " prepare completed");
            mp.start();

            mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    Log.d("pranav", "buffering - " +  percent);
                }
            });

            seekBar.setVisibility(View.VISIBLE);
            button.setText("Playing");

            seekBar.setMax(mp.getDuration());
            Handler mSeekbarUpdateHandler = new Handler();

            Runnable mUpdateSeekbar = new Runnable() {
                @Override
                public void run() {
                    seekBar.setProgress(mp.getCurrentPosition());
                    mSeekbarUpdateHandler.postDelayed(this, 50);

                }
            };


            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                    button.setText("Play");
                    button.setEnabled(true);
                    seekBar.setVisibility(View.INVISIBLE);
                }
            });


            mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);


        } catch (IOException e) {
            Log.e("pranav", "prepare() failed" + e.toString());
        }
    }

    public static void playAudio(String filePath, SeekBar seekBar, Button button, MediaPlayer mp) {
        try {
            mp.setDataSource(filePath);

            button.setText("Loading..");

            Log.d("pranav", "calling prepare");
            mp.prepare();
            mp.setLooping(false);

            Log.d("pranav", " prepare completed");
            mp.start();

            mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    Log.d("pranav", "buffering - " +  percent);
                }
            });

            seekBar.setVisibility(View.VISIBLE);
            button.setText("Stop");

            seekBar.setMax(mp.getDuration());


        } catch (IOException e) {
            Log.e("pranav", "prepare() failed" + e.toString());
        }
    }

    public static String saveImage(Bitmap finalBitmap) {
        Log.d("pranav", "saving");
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/derma");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".png";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 30, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static String getTimeString(int millis){
        int seconds = millis / 1000;
        if(seconds < 60){
            return "" + seconds + "s";
        }else{
            return "" + seconds / 60 + "m " + seconds % 60 + "s";
        }
    }
}

