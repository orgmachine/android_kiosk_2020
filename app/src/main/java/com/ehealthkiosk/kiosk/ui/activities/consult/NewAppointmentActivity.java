package com.ehealthkiosk.kiosk.ui.activities.consult;
import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.consult.Doctor;
import com.ehealthkiosk.kiosk.ui.activities.TestTypesActivity;
import com.ehealthkiosk.kiosk.ui.fragments.consult.AppointmentFinalizeFragment;
import com.ehealthkiosk.kiosk.ui.fragments.consult.ConsultationsListFragment;
import com.ehealthkiosk.kiosk.ui.fragments.consult.DoctorListFragment;
import com.ehealthkiosk.kiosk.ui.fragments.consult.DoctorSlotsFragment;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.ehealthkiosk.kiosk.widgets.NoSwipeViewPager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewAppointmentActivity extends AppCompatActivity implements View.OnClickListener {

    String patientName = SharedPrefUtils.getProfile(HealthKioskApp.getAppContext()).getName();

    @BindView(R.id.frame)
    FrameLayout frameLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView title;
    @BindView(R.id.step1)
    LinearLayout step1;
    @BindView(R.id.step2)
    LinearLayout step2;
    @BindView(R.id.step3)
    LinearLayout step3;

    int currentStep = 1;

    Doctor selectedDoctor;

    DoctorListFragment doctorListFragment;
    DoctorSlotsFragment slotsFragment;
    AppointmentFinalizeFragment appointmentFinalizeFragment;

    String selectedDate, selectedTime, selectedDateForAPI, startTime, endTIme;

    public String getSelectedDateForAPI() {
        return selectedDateForAPI;
    }

    public void setSelectedDateForAPI(String selectedDateForAPI) {
        this.selectedDateForAPI = selectedDateForAPI;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTIme() {
        return endTIme;
    }

    public void setEndTIme(String endTIme) {
        this.endTIme = endTIme;
    }

    private void makeStepInActive(LinearLayout ll){
        TextView number = ll.findViewWithTag("number");
        TextView title = ll.findViewWithTag("title");

        number.setBackground(getResources().getDrawable(R.drawable.light_gray_circle));
        title.setTextColor(Color.parseColor("#a7a7a7"));
    }
    private void makeStepActive(LinearLayout ll){
        TextView number = ll.findViewWithTag("number");
        TextView title = ll.findViewWithTag("title");

        number.setBackground(getResources().getDrawable(R.drawable.blue_circle));
        title.setTextColor(Color.parseColor("#707070"));
    }

    public Doctor getDoctor(){
        return selectedDoctor;
    }


    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId() == R.id.action_home) {
            Intent i = new Intent(this, TestTypesActivity.class);
            startActivity(i);
            finish();
        } else {
            switch (currentStep) {
                case 1:
                    Intent i = new Intent(this, ConsultationsActivity.class);
                    startActivity(i);
                    finish();
                    break;
                case 2:
                    makeStepInActive(step2);
                    setFragment(doctorListFragment);
                    currentStep = 1;
                    break;
                case 3:
                    makeStepInActive(step3);
                    setFragment(slotsFragment);
                    currentStep = 2;
            }
        }

        return true;
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
    public void onBackPressed() {
        Log.d("pranav", "current step: " + currentStep);
        switch (currentStep) {
            case 1:
                Intent i = new Intent(this, ConsultationsActivity.class);
                startActivity(i);
                finish();
                break;
            case 2:
                makeStepInActive(step2);
                setFragment(doctorListFragment);
                currentStep = 1;
                break;
            case 3:
                makeStepInActive(step3);
                setFragment(slotsFragment);
                currentStep = 2;
        }
    }

    void setFragment(Fragment f){
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.frame, f);
        t.commit();
    }

    public void goToAppointments(){
        Intent i = new Intent(this, ConsultationsActivity.class);
        startActivity(i);
    }

    public void goToSlots(Doctor d){
        currentStep = 2;
        selectedDoctor = d;
        makeStepActive(step2);
        setFragment(slotsFragment);
    }

    public void goToFinalize(String date, String time){
        currentStep = 3;
        Log.d("pranav", selectedDate + " " + selectedTime);
        selectedDate = date;
        selectedTime = time;
        Log.d("pranav", selectedDate + " " + selectedTime);
        makeStepActive(step3);
        setFragment(appointmentFinalizeFragment);
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title.setText("New Appointment for " + patientName);

        doctorListFragment = new DoctorListFragment();
        slotsFragment = new DoctorSlotsFragment();
        appointmentFinalizeFragment = new AppointmentFinalizeFragment();
        setFragment(doctorListFragment);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        Log.d("pranav", ""+dpHeight+","+dpWidth);
        makeStepInActive(step2);
        makeStepInActive(step3);

        step1.setOnClickListener(this);
        step2.setOnClickListener(this);
        step3.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.step1:
                currentStep = 1;
                makeStepInActive(step2);
                makeStepInActive(step3);
                makeStepActive(step1);
                setFragment(doctorListFragment);
                return;
            case R.id.step2:
                currentStep = 2;
                if(selectedDoctor == null)return;
                makeStepInActive(step3);
                makeStepActive(step2);
                setFragment(slotsFragment);
                return;
            case R.id.step3:
                currentStep = 3;
                if(selectedTime == null)return;
                makeStepActive(step3);
                setFragment(appointmentFinalizeFragment);
                return;
        }
    }
}
