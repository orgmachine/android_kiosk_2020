package com.ehealthkiosk.kiosk.ui.activities.consult;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.consult.Doctor;
import com.ehealthkiosk.kiosk.ui.activities.TestTypesActivity;
import com.ehealthkiosk.kiosk.ui.fragments.consult.AppointmentFinalizeFragment;
import com.ehealthkiosk.kiosk.ui.fragments.consult.DoctorListFragment;
import com.ehealthkiosk.kiosk.ui.fragments.consult.DoctorSlotsFragment;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateAppointmentActivity extends AppCompatActivity {

    String patientName = SharedPrefUtils.getProfile(HealthKioskApp.getAppContext()).getName();
    public static final String EXTRA_APPOINTMENT_ID = "appointment_id";
    public static final String EXTRA_DOCTOR_ID = "doc_id";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView title;

    int appointment_id;

    DoctorSlotsFragment slotsFragment;



    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, ConsultationsActivity.class);
        startActivity(i);
        finish();

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
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_home) {
            Intent i = new Intent(this, TestTypesActivity.class);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(this, ConsultationsActivity.class);
            startActivity(i);
            finish();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_appointment);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title.setText("Reschedule Appointment for " + patientName);

        appointment_id = getIntent().getIntExtra(EXTRA_APPOINTMENT_ID, 0);
        int doc_id = getIntent().getIntExtra(EXTRA_DOCTOR_ID, 0);
        slotsFragment = new DoctorSlotsFragment(doc_id, true, appointment_id);

        setFragment(slotsFragment);

    }

}
