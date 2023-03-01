package com.ehealthkiosk.kiosk.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

import com.ehealthkiosk.kiosk.R;


public class PermissionDialogView {

    public static int MEDIA_PERMISSION = 0;
    public static int LOCATION_PERMISSION = 1;
    public static int SMS_PERMISSION = 2;
    public static int PHONE_PERMISSION = 3;

    private static void goToSettings(Activity activity) {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(myAppSettings, 168);
    }

    public static void gotoSettingsDialog(final Activity activity, final int type) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, R.style.MyAlertDialogStyle);
        // set title
        String appName = Constants.APP_NAME;
        alertDialogBuilder.setTitle(appName);

        String strMSG = getPermissionMessage(type);

        // set dialog message
        alertDialogBuilder
                .setMessage(strMSG)
                .setPositiveButton(Constants.GO_TO_SETTINGS, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        if (Build.VERSION.SDK_INT > 22) {
                            goToSettings(activity);
                        }
                    }
                })
                .setNegativeButton(Constants.DIALOG_DISMISS, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                        if (type == 3) {
                            activity.finish();
                        }
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    private static String getPermissionMessage(int type) {
        switch (type) {
            case 0:
                return "Few permission required for media access.";
            case 1:
                return "Few permission required for location access.";
            case 2:
                return "Few permission required for sms access.";
            case 3:
                return "Few permission required to start the app.";
            default:
                return "Few permission required for start the app.";
        }

    }

}
