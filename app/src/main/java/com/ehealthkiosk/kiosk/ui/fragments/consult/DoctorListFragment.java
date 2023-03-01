package com.ehealthkiosk.kiosk.ui.fragments.consult;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.Consultation;
import com.ehealthkiosk.kiosk.model.commonresponse.Base;
import com.ehealthkiosk.kiosk.model.consult.Doctor;
import com.ehealthkiosk.kiosk.model.consult.DoctorList;
import com.ehealthkiosk.kiosk.model.consult.requests.DoctorListAPI;
import com.ehealthkiosk.kiosk.ui.activities.consult.AppointmentDetailActivity;
import com.ehealthkiosk.kiosk.ui.activities.consult.ChatActivity;
import com.ehealthkiosk.kiosk.ui.activities.consult.NewAppointmentActivity;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorListFragment extends Fragment {

    RecyclerView rvConsultations;
    ConsultationAdapter mAdapter;
    EditText search;
    Spinner spinner;
    RelativeLayout searchButton;
    List<Doctor> doctors = new ArrayList<>();
    String filterType = "All";
    String filterQ = "";
    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);

    public DoctorListFragment() {
    }

    void fetchDoctors(){
        Common_Utils.showProgress(getActivity());
        String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());
        Call<Base<DoctorList>> call = RestClient.getClient().getDoctors(
                token, kioskId, new DoctorListAPI());
        call.enqueue(new Callback<Base<DoctorList>>() {
            @Override
            public void onResponse(Call<Base<DoctorList>> call, Response<Base<DoctorList>> response) {

                Common_Utils.hideProgress();
                Log.d("pranav", response.body().getData().toString());
                doctors = response.body().getData().getDoctors();
                rvConsultations.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvConsultations.addItemDecoration(new DividerItemDecoration(rvConsultations.getContext(), DividerItemDecoration.VERTICAL));
                refreshList();
                initSpinner();
            }

            @Override
            public void onFailure(Call<Base<DoctorList>> call, Throwable t) {

                Common_Utils.hideProgress();
            }
        });
    }

    private void initSpinner() {

        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("All");
        for(Doctor d: doctors) {
            if(!categories.contains(d.getDescription()))
            categories.add(d.getDescription());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_doctor_list, container, false);

        rvConsultations = v.findViewById(R.id.doctor_list);
        search = v.findViewById(R.id.search);
        searchButton = v.findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterQ = search.getText().toString();
                refreshList();
            }
        });

        spinner = v.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterType = parent.getAdapter().getItem(position).toString();
                refreshList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterType = "All";
                refreshList();
            }
        });

        fetchDoctors();
        return v;
    }

    private List<Doctor> getFilteredDoctors() {
        List<Doctor> filteredDoctors = new ArrayList<>();
        for (Doctor d : doctors) {
            if (d.getSearchString().toLowerCase().contains(filterQ.toLowerCase()) || filterQ.equals("")) {
                if(filterType.toLowerCase().equals(d.getDescription().toLowerCase()) || filterType.equals("All"))
                    filteredDoctors.add(d);
            }
        }

        return filteredDoctors;
    }

    private void refreshList(){
        mAdapter = new ConsultationAdapter(getFilteredDoctors(), getActivity());
        rvConsultations.setAdapter(mAdapter);
    }

    class ConsultationAdapter extends RecyclerView.Adapter<ConsultationAdapter.MyViewHolder> {
        private List<Doctor> doctors;
        private Activity mActivity;

        class MyViewHolder extends RecyclerView.ViewHolder {
            public View view;
            public Button next;
            public ImageView image;
            public TextView name, specialization, fees, profile, timings;

            public MyViewHolder(View v) {
                super(v);
                view = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public ConsultationAdapter(List<Doctor> data, Activity activity) {
            doctors = data;
            mActivity = activity;
        }

        @Override
        public ConsultationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_doctor, parent, false);

            MyViewHolder vh = new MyViewHolder(v);
            vh.next = v.findViewById(R.id.see_slots);
            vh.name = v.findViewById(R.id.name);
            vh.image = v.findViewById(R.id.profile_image);
            vh.specialization = v.findViewById(R.id.specialization);
            vh.fees = v.findViewById(R.id.fees);
            vh.profile = v.findViewById(R.id.see_profile);
            vh.timings = v.findViewById(R.id.doc_timings);
            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Doctor d = doctors.get(position);
            holder.next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("pranav", "Will go to doctor " + d.getName());
                    ((NewAppointmentActivity)mActivity).goToSlots(d);
                }
            });
            holder.name.setText(d.getFirstName() + " " + d.getLastName());
            Glide.with(getActivity())
                    .load(d.getProfilePic())
                    .circleCrop()
                    .into(holder.image);
            holder.fees.setText("₹ " + d.getFees());
            holder.specialization.setText(d.getDescription());
            holder.timings.setText(d.getAvailableTiming());

            holder.profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Doctor doc = d;
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
                            ((NewAppointmentActivity)mActivity).goToSlots(d);
                            alertDialog.dismiss();

                        }
                    });

                    alertDialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return doctors.size();
        }
    }

}
