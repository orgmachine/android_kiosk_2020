package com.ehealthkiosk.kiosk.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.TestTypes;
import com.ehealthkiosk.kiosk.ui.activities.consult.ConsultationsActivity;
import com.ehealthkiosk.kiosk.ui.activities.reportlist.ReportsListActivity;
import com.ehealthkiosk.kiosk.ui.activities.selectprofile.SelectProfileActivity;
import com.ehealthkiosk.kiosk.ui.adapters.TestTypesAdapter;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestTypesActivity extends BaseActivity implements TestTypesAdapter.ItemListener {

    ArrayList<TestTypes> arrayList;
    @BindView(R.id.test_types_recyclerView)
    RecyclerView testTypesRecyclerView;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.top_bar)
    Toolbar toolbar;
    @BindView(R.id.btn_report)
    Button btnReport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_types);
        ButterKnife.bind(this);

        SharedPrefUtils.clearSP(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        showSystemUI();

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarTitle.setText("Choose Your Options");

        String profile_id = SharedPrefUtils.getProfileId(HealthKioskApp.getAppContext());

        if (profile_id.equalsIgnoreCase("guest")) {
            btnReport.setVisibility(View.INVISIBLE);
        } else {
            btnReport.setVisibility(View.VISIBLE);
        }

        arrayList = new ArrayList();
        arrayList.add(new TestTypes(Constants.TEST_TYPE_BASIC, "Basic Test", R.drawable.basic_test, "#20ca8b"));
        arrayList.add(new TestTypes(Constants.TEST_TYPE_WELLNESS, "Wellness Test", R.drawable.wellness_test, "#ff7b7b"));
        arrayList.add(new TestTypes(Constants.CONSULT_DOCTOR, "Consult Doctor", R.drawable.consult_doctor, "#ff7b7b"));
        arrayList.add(new TestTypes(Constants.TEST_TYPE_DERMA, "Dermascope", R.drawable.dermascope, "#ff7b7b"));
        arrayList.add(new TestTypes(Constants.TEST_TYPE_AUTOSCOPE, "Otoscope", R.drawable.otoscope_1, "#ff7b7b"));
        arrayList.add(new TestTypes(Constants.TEST_TYPE_STETHESCOPE, "Stethoscope", R.drawable.stethoscope_1, "#ff7b7b"));

        arrayList.add(new TestTypes(Constants.TEST_TYPE_ECG, "ECG Test", R.drawable.heart, "#ff7b7b"));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        testTypesRecyclerView.setLayoutManager(gridLayoutManager);

//        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, 500);
//        testTypesRecyclerView.setLayoutManager(layoutManager);

        TestTypesAdapter adapter = new TestTypesAdapter(this, arrayList, this);
        testTypesRecyclerView.setAdapter(adapter);


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
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

    @Override
    public void onItemClick(TestTypes item, int position) {

        if (DeviceIdPrefHelper.getDefaultHeight(this, Constants.DEFAULT_HEIGHT) != null &&
                !DeviceIdPrefHelper.getDefaultHeight(this, Constants.DEFAULT_HEIGHT).equals("")) {
            if (item != null) {
                if (item.testId.equals(Constants.TEST_TYPE_BASIC)) {
                    String price = DeviceIdPrefHelper.getTestBill(this, Constants.BASIC_HEALTH_BILL);
                    DeviceIdPrefHelper.setTestType(this, Constants.TEST_TYPE, "Basic Health");
                    if (DeviceIdPrefHelper.getrailwayEmployee(this, true)) {
                        itemClick(item, position);

                    } else {
                        paymentDialog(price, item, position);

                    }
                } else if (item.testId.equals(Constants.TEST_TYPE_WELLNESS)) {
                    DeviceIdPrefHelper.setTestType(this, Constants.TEST_TYPE, "Wellness Test");
                    String price = DeviceIdPrefHelper.getTestBill(this, Constants.WELLNESS_TEST_BILL);
                    paymentDialog(price, item, position);
                } else if(item.testId.equals(Constants.TEST_TYPE_DERMA)){
                    itemClick(item, position);
                }else if(item.testId.equals(Constants.TEST_TYPE_AUTOSCOPE)){
                    itemClick(item, position);
                } else if (item.testId.equals(Constants.CONSULT_DOCTOR)) {
                    itemClick(item, position);
                } else if (item.testId.equals(Constants.TEST_TYPE_ECG)) {
                    itemClick(item, position);
                } else if (item.testId.equals(Constants.TEST_TYPE_STETHESCOPE)) {
                    itemClick(item, position);
                } else {
                    Toast.makeText(TestTypesActivity.this,
                            "This test is coming soon", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(TestTypesActivity.this,
                    "Cannot measure without default height," +
                            " please provide default height in device settings", Toast.LENGTH_LONG).show();
        }
    }

    public void paymentDialog(String paymentAmout, TestTypes item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setMessage("Have you collected " + paymentAmout + " billed for this test?").setTitle("Payment Section")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        itemClick(item, position);
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        Toast.makeText(TestTypesActivity.this,
                                "Please finish the payment process to the begin test", Toast.LENGTH_LONG).show();

                        dialog.cancel();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();


    }

    public void itemClick(TestTypes item, int position) {
        if (item.testId.equals(Constants.TEST_TYPE_BASIC)) {
            if (Common_Utils.isLocationEnabled()) {
                Intent i = new Intent(TestTypesActivity.this, MainActivity.class);
                i.putExtra(Constants.IS_BASIC_TEST, true);
                i.putExtra(MainActivity.TEST_TITLE, "Basic Test");
                startActivity(i);
            } else {
                Common_Utils.displayPromptForEnablingGPS(TestTypesActivity.this, "Please turn on your location to proceed further.");
            }

        } else if (item.testId.equals(Constants.TEST_TYPE_WELLNESS)) {
            Intent i = new Intent(TestTypesActivity.this, MainActivity.class);
            i.putExtra(Constants.IS_WELLNESS_TEST, true);
            i.putExtra(MainActivity.TEST_TITLE, "Wellness Test");
            startActivity(i);

        } else if(item.testId.equals(Constants.TEST_TYPE_DERMA)){
            Intent i = new Intent(TestTypesActivity.this, MainActivity.class);
            i.putExtra(Constants.IS_DERMA_TEST, true);
            i.putExtra(Constants.REPORT_TYPE, Constants.REPORT_TYPE_DERMA);
            i.putExtra(MainActivity.TEST_TITLE, "Dermascope Test");

            startActivity(i);

        }
        else if(item.testId.equals(Constants.TEST_TYPE_AUTOSCOPE)){
            Intent i = new Intent(TestTypesActivity.this, MainActivity.class);
            i.putExtra(Constants.IS_AUTO_TEST, true);
            i.putExtra(Constants.REPORT_TYPE, Constants.REPORT_TYPE_AUTOSCOPE);
            i.putExtra(MainActivity.TEST_TITLE, "Otoscope Test");
            startActivity(i);

        }else if (item.testId.equals(Constants.CONSULT_DOCTOR)) {

            Intent i = new Intent(TestTypesActivity.this, ConsultationsActivity.class);
            startActivity(i);
        } else if (item.testId.equals(Constants.TEST_TYPE_STETHESCOPE)) {

            Intent i = new Intent(TestTypesActivity.this, StethescopeActivity.class);
            startActivity(i);
        }else if (item.testId.equals(Constants.TEST_TYPE_ECG)) {

            Intent i = new Intent(TestTypesActivity.this, ECGTestActivity.class);
            startActivity(i);
        } else {
                Toast.makeText(TestTypesActivity.this, "This test is coming soon", Toast.LENGTH_LONG).show();
            }

        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);

        MenuItem item1 = menu.findItem(R.id.action_logout);
        item1.setVisible(true);

        MenuItem item2 = menu.findItem(R.id.action_profile);
        item2.setVisible(true);
        if (SharedPrefUtils.getProfileId(this).equalsIgnoreCase("guest")) {
            item2.setVisible(false);
        }


        MenuItem item3 = menu.findItem(R.id.action_home);
        item3.setVisible(false);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                Intent intent = new Intent(getApplicationContext(), SelectProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                logoutAlert();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage("Do you want to logout").setTitle("Logout")
                .setCancelable(false)
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPrefUtils.setAppState(HealthKioskApp.getAppContext(), Constants.STATE_INITIAL);
                        SharedPrefUtils.setToken(HealthKioskApp.getAppContext(), null);
                        SharedPrefUtils.setProfileId(HealthKioskApp.getAppContext(), null);
                        SharedPrefUtils.setProfile(HealthKioskApp.getAppContext(), null);
                        SharedPrefUtils.setPhoneCode(HealthKioskApp.getAppContext(), 91);
                        SharedPrefUtils.setPhoneNo(HealthKioskApp.getAppContext(), null);
                        DeviceIdPrefHelper.setrailwayEmployee(TestTypesActivity.this, false);
                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();

    }

    @OnClick(R.id.btn_report)
    public void onViewClicked() {
        Intent i = new Intent(this, ReportsListActivity.class);
        startActivity(i);
        finish();

    }

    public void openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {


                //throw new ActivityNotFoundException();
            }
            i.setAction(Intent.ACTION_SEND);
            i.putExtra(Constants.SEND_PROFILE_ID, SharedPrefUtils.getProfileId(this));
            i.putExtra(Constants.SEND_KIOSK_TOKEN, SharedPrefUtils.getToken(this));
            i.putExtra(Constants.SEND_QB_ID, DeviceIdPrefHelper.getQbId(this, Constants.QB_ID));
            i.putExtra(Constants.SEND_QB_LOGIN, DeviceIdPrefHelper.getQblogin(this, Constants.QB_LOGIN));
            i.putExtra(Constants.SEND_QB_PASSWORD, DeviceIdPrefHelper.getQbpassword(this, Constants.QB_PASSWORD));
            startActivity(i);

        } catch (ActivityNotFoundException e) {

        }
    }

}
