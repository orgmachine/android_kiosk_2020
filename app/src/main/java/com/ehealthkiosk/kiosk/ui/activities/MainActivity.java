package com.ehealthkiosk.kiosk.ui.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.MessageEvent;
import com.ehealthkiosk.kiosk.ui.adapters.TabPagerAdapter;
import com.ehealthkiosk.kiosk.ui.fragments.DermascopeTestFragment;
import com.ehealthkiosk.kiosk.ui.fragments.DeviceAddressFragment;
import com.ehealthkiosk.kiosk.ui.fragments.tests.BLEPulseTestFragment;
import com.ehealthkiosk.kiosk.ui.fragments.tests.BloodPressureNewTestFragment;
import com.ehealthkiosk.kiosk.ui.fragments.tests.BloodPressureTestFragment;
import com.ehealthkiosk.kiosk.ui.fragments.tests.GlucoseTestFragment;
import com.ehealthkiosk.kiosk.ui.fragments.tests.HaemoglobinTestFragment;
import com.ehealthkiosk.kiosk.ui.fragments.tests.HeightTestFragment;
import com.ehealthkiosk.kiosk.ui.fragments.tests.PulseTestFragment;
import com.ehealthkiosk.kiosk.ui.fragments.tests.TemperatureTestFragment;
import com.ehealthkiosk.kiosk.ui.fragments.tests.WeightTestFragment;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.shuhart.stepview.StepView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    public static final String TEST_TITLE = "test_title";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.step_view)
    StepView stepView;

    String reportType;

    private TabPagerAdapter mPagerAdapter;
    private HashMap<String, String> readingSourceMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        Common_Utils.disableBluetooth();
//        Common_Utils.enableBluetooth();


        showSystemUI();

        SharedPrefUtils.clearSP(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        reportType = getIntent().getExtras().getString(Constants.REPORT_TYPE);


        toolbarTitle.setText("YoloHealth");
        if(getIntent().hasExtra(TEST_TITLE)){
            toolbarTitle.setText(getIntent().getStringExtra(TEST_TITLE));
        }

        List<String> stepsBeanList = new ArrayList<>();
        if (getIntent() != null && getIntent().getExtras().getBoolean(Constants.IS_BASIC_TEST)) {
            stepsBeanList.add(getResources().getString(R.string.height_text));
            stepsBeanList.add(getResources().getString(R.string.weight_text));
            stepsBeanList.add(getResources().getString(R.string.bp_text));
            stepsBeanList.add(getResources().getString(R.string.pulse_text));
            stepsBeanList.add(getResources().getString(R.string.temp_text));
        } else if (getIntent() != null && getIntent().getExtras().getBoolean(Constants.IS_WELLNESS_TEST)) {
            stepsBeanList.add(getResources().getString(R.string.height_text));
            stepsBeanList.add(getResources().getString(R.string.weight_text));
            stepsBeanList.add(getResources().getString(R.string.bp_text));
            stepsBeanList.add(getResources().getString(R.string.pulse_text));
            stepsBeanList.add(getResources().getString(R.string.glucose_text));
            stepsBeanList.add(getResources().getString(R.string.hemoglobin_text));
            stepsBeanList.add(getResources().getString(R.string.temp_text));
        } else if (getIntent() != null && getIntent().getExtras().getBoolean(Constants.IS_DERMA_TEST)) {
            stepsBeanList.add(getResources().getString(R.string.derma_text));
        } else if (getIntent() != null && getIntent().getExtras().getBoolean(Constants.IS_AUTO_TEST)) {
            stepsBeanList.add(getResources().getString(R.string.autoscope_text));
        }
        stepView.setSteps(stepsBeanList);

//        stepView.setOnStepClickListener(new StepView.OnStepClickListener() {
//            @Override
//            public void onStepClick(int step) {

//            }
//        });


        // openUserDetailsDialog();

        stepView.setOnStepClickListener(new StepView.OnStepClickListener() {
            @Override
            public void onStepClick(int step) {
                stepView.done(false);
                stepView.go(step, true);
                goToStep(step);
            }
        });

        goToStep(0);
    }

    public void setSourceMap(String key, String val){
        readingSourceMap.put(key, val);
    }

    public String getSourceMap(){
        Log.d("TAG", readingSourceMap.toString());
        return readingSourceMap.toString();
    }

    private void switchFragment(Fragment fragment){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void goToStep(int step) {
        boolean isWellness = getIntent() != null && getIntent().getExtras().getBoolean(Constants.IS_WELLNESS_TEST);
        boolean isDerma = getIntent() != null && getIntent().getExtras().getBoolean(Constants.IS_DERMA_TEST);
        boolean isAutoscope = getIntent() != null && getIntent().getExtras().getBoolean(Constants.IS_AUTO_TEST);
        Log.d("TAG", "switching to " + step );
        switch(step){
            case 0:
                if(isDerma || isAutoscope)
                    switchFragment(getDermaFragment());
                else switchFragment(getHeightTestFragment());
                break;
            case 1:
                switchFragment(getWeightTestFragment());
                break;
            case 2:
                switchFragment(getBloodPressureFragment());

                break;
            case 3:
                switchFragment(getPulseFragment());
                break;
            case 4:
                if(isWellness)
                    switchFragment(getGlucoseFragment());
                else switchFragment(getTemperatureFragment());
                break;
            case 5:
                switchFragment(getHaemoglobinFragment());
                break;
            case 6:
                switchFragment(getTemperatureFragment());
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);

        MenuItem item1 = menu.findItem(R.id.action_logout);
        item1.setVisible(false);

        MenuItem item2 = menu.findItem(R.id.action_profile);
        item2.setVisible(false);

        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    public void hideSteps(){
        stepView.setVisibility(View.GONE);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_home:
                backPressClick();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backPressClick();
    }

    void backPressClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

        builder.setTitle(null);
        builder.setMessage(getResources().getString(R.string.exit_text_text));
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.show();
    }

    public Fragment getWeightTestFragment() {

        Fragment fragment;
        fragment = new WeightTestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    public Fragment getHeightTestFragment() {

        Fragment fragment;
        fragment = new HeightTestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    public Fragment getDermaFragment() {
        Fragment fragment;
        fragment = new DermascopeTestFragment();
        Bundle bundle = new Bundle();

        if(reportType.equals(Constants.REPORT_TYPE_DERMA)){
            bundle.putString(Constants.REPORT_TYPE, Constants.REPORT_TYPE_DERMA);
        }else if(reportType.equals(Constants.REPORT_TYPE_AUTOSCOPE)){
            bundle.putString(Constants.REPORT_TYPE, Constants.REPORT_TYPE_AUTOSCOPE);
        }
        fragment.setArguments(bundle);

        return fragment;
    }

    public Fragment getBloodPressureFragment() {

        Fragment fragment;
        if(DeviceIdPrefHelper.getDeviceAddress(this, Constants.BP_DEVICE_TYPE).equals(DeviceAddressFragment.BP_BIOS)) {
            fragment = new BloodPressureNewTestFragment();
        }else{
            fragment = new BloodPressureTestFragment();
        }
        Bundle args = new Bundle();
        //args.putString(Constants.KEY_ABOUT_US_CATEGORY, Constants.AGREEMENT_TYPE_ABOUT_US);
        fragment.setArguments(args);

        return fragment;
    }

    public Fragment getPulseFragment() {
        Fragment fragment;

        if(DeviceIdPrefHelper.getDeviceAddress(this, Constants.PULSE_DEVICE_TYPE).equals(DeviceAddressFragment.PULSE_BPL)) {
            fragment = new BLEPulseTestFragment();
        }else{
            fragment = new PulseTestFragment();
        }
        Bundle args = new Bundle();
        //args.putString(Constants.KEY_ABOUT_US_CATEGORY, Constants.AGREEMENT_TYPE_TERMS);
        fragment.setArguments(args);

        return fragment;
    }

    public Fragment getGlucoseFragment() {
        Fragment fragment;
        fragment = new GlucoseTestFragment();
        Bundle args = new Bundle();
        //args.putString(Constants.KEY_ABOUT_US_CATEGORY, Constants.AGREEMENT_TYPE_TERMS);
        fragment.setArguments(args);

        return fragment;
    }

    public Fragment getHaemoglobinFragment() {
        Fragment fragment;
        fragment = new HaemoglobinTestFragment();
        Bundle args = new Bundle();
        //args.putString(Constants.KEY_ABOUT_US_CATEGORY, Constants.AGREEMENT_TYPE_TERMS);
        fragment.setArguments(args);

        return fragment;
    }

    public Fragment getTemperatureFragment() {
        Fragment fragment;
        fragment = new TemperatureTestFragment();
        Bundle args = new Bundle();
        //args.putString(Constants.KEY_ABOUT_US_CATEGORY, Constants.AGREEMENT_TYPE_TERMS);
        fragment.setArguments(args);

        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        Integer position;
        switch (event.type) {
            case MessageEvent.EVENT_CHANGE_TEST:
                position = (Integer) event.data;
                stepView.go(position, true);
                goToStep(position);
                break;
            case MessageEvent.EVENT_DONE_TEST:
                stepView.done(true);

                break;
        }
    }

}
