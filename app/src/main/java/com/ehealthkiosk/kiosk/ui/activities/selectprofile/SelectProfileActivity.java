package com.ehealthkiosk.kiosk.ui.activities.selectprofile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.profilelist.ProfileData;
import com.ehealthkiosk.kiosk.model.profilelist.ProfilesItem;
import com.ehealthkiosk.kiosk.ui.activities.BaseActivity;
import com.ehealthkiosk.kiosk.ui.activities.TestTypesActivity;
import com.ehealthkiosk.kiosk.ui.activities.WelcomeActivity;
import com.ehealthkiosk.kiosk.ui.activities.register.RegisterActivity;
import com.ehealthkiosk.kiosk.ui.adapters.ProfilesAdapter;
import com.ehealthkiosk.kiosk.ui.interfaces.OnItemClickListener;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.ehealthkiosk.kiosk.widgets.EmptyView.ProgressRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectProfileActivity extends BaseActivity implements OnItemClickListener, SelectProfileView, SwipeRefreshLayout.OnRefreshListener {

    List<ProfilesItem> profileList;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progress_empty)
    ProgressRelativeLayout progressEmpty;
    @BindString(R.string.retry)
    String strRetry;
    @BindView(R.id.profiles_recyclerView)
    RecyclerView profilesRecyclerView;

    @BindView(R.id.top_bar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    private SelectProfilePresenter mainPresenter;
    ProfilesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile);
        ButterKnife.bind(this);

        SharedPrefUtils.clearSP(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        showSystemUI();

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarTitle.setText("YoloHealth");


        profileList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        profilesRecyclerView.setLayoutManager(gridLayoutManager);


        mAdapter = new ProfilesAdapter(this, profileList);
        mAdapter.setOnItemClickListener(this);
        profilesRecyclerView.setAdapter(mAdapter);
        setupSwipeRefreshLayout();
        mainPresenter = new SelectProfilePresenterImpl(this);
        mainPresenter.getProfileList();
    }

    private void setupSwipeRefreshLayout(){
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.white));
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

    }

//    private void openPasswordDialog() {
//        SettingsPasswordDialog.display(getSupportFragmentManager());
//    }


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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        MenuItem item = menu.findItem(R.id.action_home);
        item.setVisible(false);

        MenuItem item1 = menu.findItem(R.id.action_profile);
        item1.setVisible(false);

        MenuItem item3 = menu.findItem(R.id.action_settings);
        item3.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logoutAlert();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle);

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
                        DeviceIdPrefHelper.setrailwayEmployee(SelectProfileActivity.this, false);
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

    @Override
    public void onItemClick(int position, View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.cardView:
                ProfilesItem profilesItem = profileList.get(position);
                SharedPrefUtils.setProfileId(HealthKioskApp.getAppContext(), String.valueOf(profilesItem.getId()));
                SharedPrefUtils.setProfile(HealthKioskApp.getAppContext(), profilesItem);
                String userEmail = profilesItem.getEmail();
                DeviceIdPrefHelper.setQbId(this, profilesItem.getQbId());
                DeviceIdPrefHelper.setQblogin(this, profilesItem.getQbLogin());
                DeviceIdPrefHelper.setQbpassword(this, profilesItem.getQbPassword());



                DeviceIdPrefHelper.setUserEmail(this, Constants.USER_EMAIL, userEmail);
                Log.d("RESPONSE", String.valueOf(profilesItem.getRailwayEmployeeCheck()));
                if (profilesItem.getRailwayEmployeeCheck()) {
                    DeviceIdPrefHelper.setrailwayEmployee(this, true);
                } else {
                    DeviceIdPrefHelper.setrailwayEmployee(this, false);
                }

                i = new Intent(this, TestTypesActivity.class);
                this.startActivity(i);
                finish();
                break;
            case R.id.addCardView:
                i = new Intent(this, RegisterActivity.class);
                i.putExtra(Constants.FROM, Constants.ADD_PROFILE);
                startActivityForResult(i, 3);
                finish();
                break;

        }

    }

    @Override
    public void showProgress() {
        Common_Utils.showProgress(this);
    }

    @Override
    public void showSuccess(ProfileData profileListData, String msg) {

//        if (getIntent().getStringExtra(Constants.FROM) != null &&
//                getIntent().getStringExtra(Constants.FROM).equalsIgnoreCase("Register")) {
//            Intent i = new Intent(this, TestTypesActivity.class);
//            this.startActivity(i);
//        }

        swipeRefreshLayout.setRefreshing(false);

        if (profileListData.getProfiles().size() > 0) {
            progressEmpty.showContent();
        } else {
            progressEmpty.showContent();
        }

        if (this.profileList != null && !this.profileList.isEmpty()) {
            this.profileList.clear();
        }

        this.profileList.addAll(profileListData.getProfiles());

        mAdapter.notifyDataSetChanged();


    }

    private View.OnClickListener retryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            mainPresenter.getProfileList();
        }
    };

    @Override
    public void showError(String msg) {
        swipeRefreshLayout.setRefreshing(false);
        Common_Utils.hideProgress();
        progressEmpty.showError(R.drawable.ic_home_illustration, msg, "", strRetry, retryClickListener, getVisibleView());
    }

    @Override
    public void onRefresh() {
        mainPresenter.getProfileList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == 3) {
            mainPresenter.getProfileList();
            Intent i = new Intent(this, TestTypesActivity.class);
            this.startActivity(i);
        }
    }
}
