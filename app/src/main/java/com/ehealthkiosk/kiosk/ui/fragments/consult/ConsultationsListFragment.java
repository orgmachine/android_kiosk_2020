package com.ehealthkiosk.kiosk.ui.fragments.consult;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.Consultation;
import com.ehealthkiosk.kiosk.model.commonresponse.Base;
import com.ehealthkiosk.kiosk.model.consult.Appointment;
import com.ehealthkiosk.kiosk.model.consult.Doctor;
import com.ehealthkiosk.kiosk.model.consult.DoctorStatus;
import com.ehealthkiosk.kiosk.model.consult.requests.DoctorStatusAPI;
import com.ehealthkiosk.kiosk.ui.activities.consult.AppointmentDetailActivity;
import com.ehealthkiosk.kiosk.ui.activities.consult.ChatActivity;
import com.ehealthkiosk.kiosk.ui.activities.consult.UpdateAppointmentActivity;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.ehealthkiosk.kiosk.widgets.VerticalTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultationsListFragment extends Fragment {

    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);
    String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());

    RecyclerView rvConsultations;
    TextView noResults;
    ConsultationAdapter mAdapter;
    List<Appointment> consultations = new ArrayList<>();
    boolean isUpcoming = false;

    public ConsultationsListFragment(boolean upcoming, List<Appointment> appointments) {
        isUpcoming = upcoming;
        consultations = appointments;
    }

    public ConsultationsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_consultations_list, container, false);
        rvConsultations = v.findViewById(R.id.consultations_list);
        noResults = v.findViewById(R.id.no_result_tv);

        mAdapter = new ConsultationAdapter(consultations, getActivity());
        rvConsultations.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvConsultations.setAdapter(mAdapter);
        rvConsultations.addItemDecoration(new DividerItemDecoration(rvConsultations.getContext(), DividerItemDecoration.VERTICAL));
        if (consultations.isEmpty()){
            Log.d("pranav", "empty view");
            rvConsultations.setVisibility(View.INVISIBLE);
            noResults.setVisibility(View.VISIBLE);
        } else {
            rvConsultations.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.INVISIBLE);
        }
        return v;
    }

    class ConsultationAdapter extends RecyclerView.Adapter<ConsultationAdapter.MyViewHolder> {
        private List<Appointment> consultations;
        private Context mContext;

        class MyViewHolder extends RecyclerView.ViewHolder {
            public View view, doctorStatus;
            public VerticalTextView appointmentId;
            public TextView status, doctorName, patientName, description, date, profile;
            public Button details, reschedule, join;
            public ImageView image;

            public MyViewHolder(View v) {
                super(v);
                view = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public ConsultationAdapter(List<Appointment> data, Context c) {
            consultations = data;
            mContext = c;
        }

        @Override
        public ConsultationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_consultation, parent, false);
            MyViewHolder vh = new MyViewHolder(v);

            vh.status = v.findViewById(R.id.status);
            vh.doctorStatus = v.findViewById(R.id.doc_status);
            vh.details = v.findViewById(R.id.see_details);
            vh.reschedule = v.findViewById(R.id.reschedule);
            vh.doctorName = v.findViewById(R.id.doctor_name);
            vh.patientName = v.findViewById(R.id.patient_name);
            vh.description = v.findViewById(R.id.doctor_description);
            vh.date = v.findViewById(R.id.datetime);
            vh.join = v.findViewById(R.id.join_now);
            vh.appointmentId = v.findViewById(R.id.appointment_id);
            vh.image = v.findViewById(R.id.profile_image);
            vh.profile = v.findViewById(R.id.see_profile);
            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Appointment c = consultations.get(position);
            holder.doctorName.setText(c.getDoctor().getFirstName() + " " + c.getDoctor().getLastName());
            holder.description.setText(c.getDoctor().getDescription());
            holder.date.setText(c.getStrDatetime());
            holder.patientName.setText("For " + c.getPatientName());
            holder.appointmentId.setText(c.getAppointmentId().substring(0, 6));
            holder.status.setText(c.getAppointmentStr() + " Appointment");
            Glide.with(getActivity())
                    .load(c.getDoctor().getProfilePic())
                    .circleCrop()
                    .into(holder.image);
            holder.details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, AppointmentDetailActivity.class);
                    i.putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_ID, c.getId());
                    mContext.startActivity(i);
                }
            });

            if(c.getDoctor().isAvailable()){
                holder.doctorStatus.setBackground(mContext.getDrawable(R.drawable.available));
            }else{
                holder.doctorStatus.setBackground(mContext.getDrawable(R.drawable.busy));
            }

            holder.join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!Common_Utils.isNetworkAvailable()){
                        Common_Utils.showToast("Internet Connection Not Available!");
                        return;
                    }

                    checkStatusAndCall(c);

                }
            });
            holder.details.setVisibility(View.GONE);
            holder.join.setVisibility(View.GONE);

            if(c.hasStarted()){
                holder.join.setVisibility(View.VISIBLE);
            }else{
                holder.details.setVisibility(View.VISIBLE);
            }

            holder.profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Doctor doc = c.getDoctor();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
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

                    dialogNext.setVisibility(View.GONE);


                    Glide.with(getActivity()).load(doc.getProfilePic()).circleCrop().into(docImage);
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
                    dialogNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i;
                            if (c.hasStarted())
                                i = new Intent(mContext, ChatActivity.class);
                            else i = new Intent(mContext, AppointmentDetailActivity.class);
                            i.putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_ID, c.getId());
                            mContext.startActivity(i);
                            alertDialog.dismiss();
                        }
                    });


                    alertDialog.show();
                }
            });

            if(c.getStatus().equals("missed")){
                holder.status.setText("Missed");
                holder.status.setTextColor(getActivity().getResources().getColor(R.color.consult_button_red));
                holder.details.setVisibility(View.GONE);
                holder.join.setVisibility(View.GONE);
                holder.reschedule.setVisibility(View.VISIBLE);

                holder.reschedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, UpdateAppointmentActivity.class);
                        i.putExtra(UpdateAppointmentActivity.EXTRA_APPOINTMENT_ID, c.getId());
                        i.putExtra(UpdateAppointmentActivity.EXTRA_DOCTOR_ID, c.getDoctor().getId());
                        mContext.startActivity(i);
                    }
                });
            } else {
                holder.status.setTextColor(Color.parseColor("#707070"));
                holder.reschedule.setVisibility(View.GONE);

            }
        }

        private void checkStatusAndCall(Appointment c) {
            Common_Utils.showProgress(mContext, true);

            Call<Base<DoctorStatus>> call = RestClient.getClient().getDoctorStatus(token, kioskId, new DoctorStatusAPI(c.getDoctor().getId()));
            call.enqueue(new Callback<Base<DoctorStatus>>() {
                @Override
                public void onResponse(Call<Base<DoctorStatus>> call, Response<Base<DoctorStatus>> response) {
                    Common_Utils.hideProgress();
                    if(response.body().getData().isAvailable()){

                        Intent i = new Intent(mContext, ChatActivity.class);
                        i.putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_ID, c.getId());
                        mContext.startActivity(i);
                    }else{

                        Common_Utils.showToast("Doctor is not available for calling.");
                    }
                }

                @Override
                public void onFailure(Call<Base<DoctorStatus>> call, Throwable t) {
                    Common_Utils.hideProgress();
                    Common_Utils.showToast("Doctor is not available for calling.");
                }
            });


        }

        @Override
        public int getItemCount() {
            return consultations.size();
        }
    }


}
