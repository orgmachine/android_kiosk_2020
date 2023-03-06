package com.ehealthkiosk.kiosk.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.ui.adapters.TabPagerAdapter;
import com.ehealthkiosk.kiosk.ui.fragments.DeviceAddressFragment;
import com.ehealthkiosk.kiosk.ui.fragments.DiagnosisFragment;
import com.ehealthkiosk.kiosk.ui.fragments.KioskDeviceFragment;
import com.ehealthkiosk.kiosk.ui.fragments.settings.FAQsSettingsFragment;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsTabActivity extends BaseActivity {

    private static final String TAG = "AboutUsActivity";
    @BindView(R.id.top_bar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.sliding_tabs)
    TabLayout slidingTabs;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private TabPagerAdapter mPagerAdapter;

    private int[] tabIcons = {
            R.drawable.kiosk,
            R.drawable.devices,
            R.drawable.diagnosis,
            R.drawable.faqs,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toolbarTitle.setText("Settings");
        showSystemUI();

        mPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(getKioskSettingsFragment(), "KIOSK");
        mPagerAdapter.addFragment(getPeripheralsSettingsFragment(), "DEVICES");
        mPagerAdapter.addFragment(getDiagnosisFragment(), "DIAGNOSIS");
        mPagerAdapter.addFragment(getFAQsSettingsFragment(), "FAQs");

        viewpager.setAdapter(mPagerAdapter);

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        slidingTabs.setupWithViewPager(viewpager);
        setupTabIcons();
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

    private void setupTabIcons() {
        slidingTabs.getTabAt(0).setIcon(tabIcons[0]);
        slidingTabs.getTabAt(1).setIcon(tabIcons[1]);
        slidingTabs.getTabAt(2).setIcon(tabIcons[2]);
        slidingTabs.getTabAt(3).setIcon(tabIcons[3]);

    }

    public Fragment getKioskSettingsFragment() {

        Fragment fragment;
        fragment = new KioskDeviceFragment();
        return fragment;
    }

    public Fragment getPeripheralsSettingsFragment() {
        Fragment fragment;
        fragment = new DeviceAddressFragment();
        return fragment;
    }

    public Fragment getDiagnosisFragment() {
        Fragment fragment;
        fragment = new DiagnosisFragment();
        return fragment;
    }

    public Fragment getFAQsSettingsFragment() {
        Fragment fragment;
        fragment = new FAQsSettingsFragment();
        return fragment;
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(SettingsTabActivity.this, WelcomeActivity.class);
        startActivity(i);
        finish();
    }
}
