package com.ehealthkiosk.kiosk.ui.activities.consult;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.Consultation;
import com.ehealthkiosk.kiosk.model.TestTypes;
import com.ehealthkiosk.kiosk.model.commonresponse.Base;
import com.ehealthkiosk.kiosk.model.consult.Appointment;
import com.ehealthkiosk.kiosk.model.consult.AppointmentDetail;
import com.ehealthkiosk.kiosk.model.consult.Doctor;
import com.ehealthkiosk.kiosk.model.consult.requests.GetAppointmentDetailsAPI;
import com.ehealthkiosk.kiosk.ui.activities.TestTypesActivity;
import com.ehealthkiosk.kiosk.ui.fragments.consult.CameraFragment;
import com.ehealthkiosk.kiosk.ui.fragments.consult.ChatFragment;
import com.ehealthkiosk.kiosk.ui.fragments.consult.ConsultationsListFragment;
import com.ehealthkiosk.kiosk.ui.fragments.consult.DoctorListFragment;
import com.ehealthkiosk.kiosk.ui.fragments.consult.DocumentsFragment;
import com.ehealthkiosk.kiosk.utils.CameraParent;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.google.android.material.tabs.TabLayout;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.auth.session.QBSessionParameters;


import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentDetailActivity extends AppCompatActivity implements CameraParent {
    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);
    String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());
    public static final String EXTRA_APPOINTMENT_ID = "appointment_id";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView title;
    @BindView(R.id.see_full_note)
    Button seeFull;
    @BindView(R.id.doctor_name)
    TextView doctorName;
    @BindView(R.id.doctor_description)
    TextView doctorDescription;
    @BindView(R.id.profile_image)
    CircleImageView image;
    @BindView(R.id.reason)
    TextView reason;
    @BindView(R.id.ad_viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.see_profile)
    TextView profile;
    @BindView(R.id.prev_appointment_list)
    RecyclerView rvPrevList;
    @BindView(R.id.prev_appointment_title)
    TextView prevAppointmentTitle;

    int appointmentId = 2;
    AppointmentDetail appointment;
    AppointmentDetailViewPagerAdapter adapter;
    PrevAppointmentAdapter prevAdapter;

    int selectedAppointment = -1;

    boolean isFullNote = false;


    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, ConsultationsActivity.class);
        startActivity(i);
        finish();
    }

    void fetchDetails(){
        Common_Utils.showProgress(this);
        Call<Base<AppointmentDetail>> call = RestClient.getClient().getAppointmentDetails(
                token, kioskId, new GetAppointmentDetailsAPI(appointmentId)
        );
        call.enqueue(new Callback<Base<AppointmentDetail>>() {
            @Override
            public void onResponse(Call<Base<AppointmentDetail>> call, Response<Base<AppointmentDetail>> response) {
                appointment = response.body().getData();
                setupViewPager();
                showDetails();
                prevAdapter = new PrevAppointmentAdapter(
                        AppointmentDetailActivity.this, appointment.getPrevAppointments(), selectedAppointment);
                rvPrevList.setAdapter(prevAdapter);
                Common_Utils.hideProgress();

            }

            @Override
            public void onFailure(Call<Base<AppointmentDetail>> call, Throwable t) {
                Common_Utils.hideProgress();

            }
        });
    }

    public void selectAppointment(int i){
        selectedAppointment = i;
        prevAdapter = new PrevAppointmentAdapter(
                AppointmentDetailActivity.this, appointment.getPrevAppointments(), selectedAppointment);
        rvPrevList.setAdapter(prevAdapter);
    }

    private void setupViewPager() {

        adapter = new AppointmentDetailViewPagerAdapter(
                getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);

    }

    private void showDetails() {
        doctorName.setText(appointment.getDoctor().getName());
        doctorDescription.setText(appointment.getDoctor().getDescription());
        if(appointment.getTitle().length() > 75){
            reason.setText(appointment.getTitle().substring(0,73) + "...");
            seeFull.setVisibility(View.VISIBLE);
        }else {
            reason.setText(appointment.getTitle());
        }
        Glide.with(this)
                .load(appointment.getDoctor().getProfilePic())
                .circleCrop()
                .into(image);

        title.setText(
                        "Appointment on "
                        + appointment.getDate()
                        + " at " + appointment.getStartTime()
                        + " (" + appointment.getAppointmentId() + ")"
        );
        seeFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFullNote){

                    reason.setText(appointment.getTitle().substring(0,73) + "...");
                    seeFull.setText("see full note");
                }else{

                    reason.setText(appointment.getTitle());
                    seeFull.setText("hide");
                }
                isFullNote = !isFullNote;
            }
        });

        prevAppointmentTitle.setText("Previous Appointments (" + appointment.getPrevAppointments().size() + ")");


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
        if(item.getItemId() == R.id.action_home) {
            Intent myIntent = new Intent(getApplicationContext(), TestTypesActivity.class);
            startActivityForResult(myIntent, 0);
            finish();
        }else if(item.getItemId() == android.R.id.home){
            Intent i = new Intent(this, ConsultationsActivity.class);
            startActivity(i);
            finish();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(getIntent().hasExtra(EXTRA_APPOINTMENT_ID)){
            appointmentId = getIntent().getIntExtra(EXTRA_APPOINTMENT_ID, 2);
        }


        rvPrevList.setLayoutManager(new LinearLayoutManager(this));


        fetchDetails();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Doctor doc = appointment.getDoctor();
                AlertDialog.Builder builder = new AlertDialog.Builder(AppointmentDetailActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_doctor, viewGroup, false);
                TextView docName, docDescription, docRegNumber, docFees, docQualifications, docExp, docLanguages, docAddress, docExpertise, dialogClose;
                Button dialogNext;
                ImageView docImage;
                docName = dialogView.findViewById(R.id.name);
                docDescription = dialogView.findViewById(R.id.description);
                docRegNumber = dialogView.findViewById(R.id.reg_number);
                docFees = dialogView.findViewById(R.id.fees);
                docQualifications = dialogView.findViewById(R.id.qualifications);
                docExp = dialogView.findViewById(R.id.experience);
                docExpertise = dialogView.findViewById(R.id.expertise);
                docLanguages = dialogView.findViewById(R.id.languages);
                docAddress = dialogView.findViewById(R.id.address);
                docImage = dialogView.findViewById(R.id.profile_image);
                dialogNext = dialogView.findViewById(R.id.next);
                dialogClose = dialogView.findViewById(R.id.close);

                Glide.with(AppointmentDetailActivity.this).load(doc.getProfilePic()).circleCrop().into(docImage);
                docName.setText(doc.getName());
                docQualifications.setText(doc.getQualifications());
                docExp.setText(doc.getExperience() + "yrs");
                docExpertise.setText(doc.getExpertise());
                docLanguages.setText(doc.getLanguages());
                docAddress.setText(doc.getAddress());
                docDescription.setText(doc.getDescription());
                docRegNumber.setText("Registration No: " + doc.getRegistrationNumber());
                docFees.setText("₹ " + doc.getFees() + " ");

                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();


                dialogClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                dialogNext.setVisibility(View.GONE);
                alertDialog.show();
            }
        });
    }

    @Override
    public void sendImageChat(String imageUrl, String imgType, String Description) {

    }

    @Override
    public void showCamera() {

    }

    @Override
    public void closeCamera() {

    }

    class AppointmentDetailViewPagerAdapter extends FragmentStatePagerAdapter {

        public AppointmentDetailViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0)
            {
                fragment = new ChatFragment(
                        appointment.getPatient(),
                        appointment.getDialogId(),
                        appointment.getDoctor().getFirstName(),
                        appointment.getId(),
                        !appointment.getStatus().equals("upcoming"),
                        true
                        );
            }
            if (position == 1)
            {
                fragment = new DocumentsFragment(appointment.getId());
//                fragment = new CameraFragment();
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
                title = "Chat";
            }
            else if (position == 1)
            {
                title = "Documents";
            }
            return title;
        }
    }

    class PrevAppointmentAdapter extends RecyclerView.Adapter<PrevAppointmentAdapter.MyViewHolder>{
        List<Appointment> appointments;
        Context mContext;
        int selected;

        public PrevAppointmentAdapter(Context c, List<Appointment> appointments, int selected){
            this.appointments = appointments;
            this.mContext = c;
            this.selected = selected;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_prev_ap, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            vh.appointmentNumber = v.findViewById(R.id.appointment_number);
            vh.appointmentId = v.findViewById(R.id.appointment_id);
            vh.time1 = v.findViewById(R.id.time1);
            vh.time2 = v.findViewById(R.id.time2);
            vh.nextButton = v.findViewById(R.id.next_button);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Appointment ap = appointments.get(position);
            holder.appointmentNumber.setText(ap.getAppointmentStr() + " Appointment");
            holder.appointmentId.setText(ap.getAppointmentId());
            String datetime = ap.getStrDatetime();
            holder.time1.setText(datetime.split(", ")[1]);
            holder.time2.setText(datetime.split(", ")[0] + " (15 mins)");
            if(selected == position){
                holder.nextButton.setBackground(mContext.getDrawable(R.drawable.gray_circle));
                holder.appointmentId.setTextColor(Color.parseColor("#0049ee"));
            } else {
                holder.nextButton.setBackground(mContext.getDrawable(R.drawable.blue_circle));
                holder.appointmentId.setTextColor(Color.parseColor("#e0dada"));
            }
            holder.nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AppointmentDetailActivity)mContext).selectAppointment(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return appointments.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            public View view;
            public TextView appointmentNumber, appointmentId, time1, time2;
            public RelativeLayout nextButton;

            public MyViewHolder(View v) {
                super(v);
                view = v;
            }
        }

    }
}
