package com.ehealthkiosk.kiosk.ui.activities.consult;
import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.commonresponse.Base;
import com.ehealthkiosk.kiosk.model.consult.Appointment;
import com.ehealthkiosk.kiosk.model.consult.requests.GetAppointmentsAPI;
import com.ehealthkiosk.kiosk.ui.activities.MainActivity;
import com.ehealthkiosk.kiosk.ui.activities.TestTypesActivity;
import com.ehealthkiosk.kiosk.ui.fragments.consult.ConsultationsListFragment;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultationsActivity extends AppCompatActivity {
    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);
    String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());

    @BindView(R.id.consultations_viewpager)
    ViewPager vpConsultations;
    @BindView(R.id.new_appointment)
    Button newAppointment;
    @BindView(R.id.tab_layout)
    TabLayout tabs;
    ConsultationsViewPagerAdapter viewPagerAdapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    List<Appointment> ongoing = new ArrayList<>();
    List<Appointment> completed = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultations);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        newAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConsultationsActivity.this, NewAppointmentActivity.class);
                startActivity(i);
            }
        });

        fetchAppointments();

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

    public boolean onOptionsItemSelected(MenuItem item){

        Intent myIntent = new Intent(getApplicationContext(), TestTypesActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(getApplicationContext(), TestTypesActivity.class);
        startActivityForResult(myIntent, 0);
        finish();
    }

    void fetchAppointments(){
        if(!Common_Utils.isNetworkAvailable()){
            Common_Utils.showToast("Internet Connection Not Available!");
            Intent myIntent = new Intent(getApplicationContext(), TestTypesActivity.class);
            startActivityForResult(myIntent, 0);
            finish();
        }
        Common_Utils.showProgress(this);
        Call<Base<List<Appointment>>> call = RestClient.getClient().getAppointments(
                token,
                kioskId,
                new GetAppointmentsAPI(
                        true
                )
        );
        call.enqueue(new Callback<Base<List<Appointment>>>() {
            @Override
            public void onResponse(Call<Base<List<Appointment>>> call, Response<Base<List<Appointment>>> response) {
                for(Appointment a: response.body().getData()){
                    if(a.getStatus().equals("upcoming") || a.getStatus().equals("missed")){
                        ongoing.add(a);
                    }else{
                        completed.add(a);
                    }
                }

                viewPagerAdapter = new ConsultationsViewPagerAdapter(
                        getSupportFragmentManager(),
                        FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
                vpConsultations.setAdapter(viewPagerAdapter);
                tabs.setupWithViewPager(vpConsultations);
                Common_Utils.hideProgress();
            }

            @Override
            public void onFailure(Call<Base<List<Appointment>>> call, Throwable t) {

                Log.d("pranav", "got error in the API");
                Common_Utils.hideProgress();
            }
        });
    }

    class ConsultationsViewPagerAdapter extends FragmentStatePagerAdapter {

        public ConsultationsViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0)
            {
                fragment = new ConsultationsListFragment(false, ongoing);
            }
            else if (position == 1)
            {
                fragment = new ConsultationsListFragment(true, completed);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0)
            {
                title = "Ongoing";
            }
            else if (position == 1)
            {
                title = "Completed";
            }
            return title;
        }
    }

}
