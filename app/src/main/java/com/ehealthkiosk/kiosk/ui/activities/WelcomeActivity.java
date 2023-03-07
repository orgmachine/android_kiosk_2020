package com.ehealthkiosk.kiosk.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.ui.activities.login.LoginActivity;
import com.ehealthkiosk.kiosk.ui.activities.register.RegisterActivity;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SyncService;
import com.ehealthkiosk.kiosk.utils.PermissionDialogView;
import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

public class WelcomeActivity extends BaseActivity {

    ComponentName myServiceComponent;

//    @BindView(R.id.top_bar)
//    Toolbar toolbar;

    @BindView(R.id.attendant_app)
    TextView attendatApp;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
//        if (toolbar != null) {
//            setSupportActionBar(toolbar);
//        }

//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        showSystemUI();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        askLocationPermission();

        launchTestService();

        if (DeviceIdPrefHelper.getkioskId(this, Constants.KIOSK_ID) != null
                && DeviceIdPrefHelper.getkioskId(this, Constants.KIOSK_ID).equals("")) {
            openSetKioskDialog(false);
        } else if (DeviceIdPrefHelper.getkioskId(this, Constants.DEFAULT_HEIGHT) != null
                && DeviceIdPrefHelper.getkioskId(this, Constants.DEFAULT_HEIGHT).equals("")) {
            openSetKioskDialog(true);
        }

        attendatApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openApp(WelcomeActivity.this, "com.example.yolohealth_attendant_app");
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            hideSystemUI();
//        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void askLocationPermission() {

        askCompactPermissions(new String[]{
                PermissionUtils.Manifest_ACCESS_COARSE_LOCATION,
                PermissionUtils.Manifest_CAMERA,
                PermissionUtils.Manifest_RECORD_AUDIO,
                PermissionUtils.Manifest_READ_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE

        }, new PermissionResult() {

            @Override
            public void permissionGranted() {

            }

            @Override
            public void permissionDenied() {
                PermissionDialogView.gotoSettingsDialog(WelcomeActivity.this, PermissionDialogView.LOCATION_PERMISSION);
            }

            @Override
            public void permissionForeverDenied() {
                PermissionDialogView.gotoSettingsDialog(WelcomeActivity.this, PermissionDialogView.LOCATION_PERMISSION);
            }
        });
    }


    public void openSetKioskDialog(boolean setupHeight) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = getResources().getString(R.string.kiosk_id_check);
        String title = getResources().getString(R.string.kiosk_id_title);
        if(setupHeight){
            msg = getResources().getString(R.string.default_height_check);
            title = getResources().getString(R.string.default_height_check_title);
        }
        builder.setMessage(msg).setTitle(title)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(WelcomeActivity.this, SettingsTabActivity.class);
                        i.putExtra(Constants.FROM, Constants.NEW_USER);
                        startActivity(i);
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(WelcomeActivity.this,
                                getResources().getString(R.string.kiosk_id_toast), Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }


    @OnClick({R.id.btn_new_user, R.id.btn_existing_user})
    public void onViewClicked(View view) {
        Intent i = null;



        switch (view.getId()) {
            case R.id.btn_new_user:
                if (DeviceIdPrefHelper.getkioskId(this, Constants.KIOSK_ID) != null
                        && DeviceIdPrefHelper.getkioskId(this, Constants.KIOSK_ID).equals("")) {
                    openSetKioskDialog(false);
                } else if (DeviceIdPrefHelper.getkioskId(this, Constants.DEFAULT_HEIGHT) != null
                        && DeviceIdPrefHelper.getkioskId(this, Constants.DEFAULT_HEIGHT).equals("")) {
                    openSetKioskDialog(true);
                } else {
                    i = new Intent(this, RegisterActivity.class);
                    i.putExtra(Constants.FROM, Constants.NEW_USER);
                    startActivity(i);
                }
                break;
            case R.id.btn_existing_user:
                if (DeviceIdPrefHelper.getkioskId(this, Constants.KIOSK_ID) != null
                        && DeviceIdPrefHelper.getkioskId(this, Constants.KIOSK_ID).equals("")) {
                    openSetKioskDialog(false);
                } else if (DeviceIdPrefHelper.getkioskId(this, Constants.DEFAULT_HEIGHT) != null
                        && DeviceIdPrefHelper.getkioskId(this, Constants.DEFAULT_HEIGHT).equals("")) {
                    openSetKioskDialog(true);
                } else {
                    i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                }
                break;
        }
    }

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
                //throw new ActivityNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            i.setType("text/plain");

            Intent shareIntent = Intent.createChooser(i, null);
            context.startActivity(shareIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        MenuItem item = menu.findItem(R.id.action_home);
        item.setVisible(false);

        MenuItem item1 = menu.findItem(R.id.action_profile);
        item1.setVisible(false);

        MenuItem item2 = menu.findItem(R.id.action_logout);
        item2.setVisible(false);

        MenuItem item3 = menu.findItem(R.id.action_settings);
        item3.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsTabActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void launchTestService() {
        startService(new Intent(getApplicationContext(), SyncService.class));
        Toast.makeText(this, "service initiated", Toast.LENGTH_SHORT).show();

    }


}
