package com.ehealthkiosk.kiosk.ui.fragments.consult;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.Consultation;
import com.ehealthkiosk.kiosk.model.commonresponse.Base;
import com.ehealthkiosk.kiosk.model.consult.DoctorList;
import com.ehealthkiosk.kiosk.model.consult.Slot;
import com.ehealthkiosk.kiosk.model.consult.SlotList;
import com.ehealthkiosk.kiosk.model.consult.requests.DoctorListAPI;
import com.ehealthkiosk.kiosk.model.consult.requests.SlotsAPI;
import com.ehealthkiosk.kiosk.model.consult.requests.UpdateAppointmentAPI;
import com.ehealthkiosk.kiosk.ui.activities.consult.NewAppointmentActivity;
import com.ehealthkiosk.kiosk.ui.activities.consult.UpdateAppointmentActivity;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorSlotsFragment extends Fragment {
    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);
    String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());


    RecyclerView rvSlots;
    SlotAdapter mAdapter;
    Calendar calendar;
    SimpleDateFormat dateFormat;

    TextView date, day;
    LinearLayout llNext, llPrev;
    Button bNext;

    ProgressBar pBar;

    int doc_id = -1;

    List<Slot> slots = new ArrayList<>();
    int dateOffset = 0;

    int selectedSlot = -1;

    boolean isUpdate = false;
    int appointmentId = -1;

    public DoctorSlotsFragment() {
    }

    public DoctorSlotsFragment(int doc_id, boolean isUpdate, int appointment_id) {
        this.doc_id = doc_id;
        this.isUpdate = isUpdate;
        this.appointmentId = appointment_id;
    }

    void refreshList(){
        mAdapter = new SlotAdapter(slots, selectedSlot, DoctorSlotsFragment.this);
        rvSlots.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSlots.setAdapter(mAdapter);
    }

    void selectSlot(int i){
        selectedSlot = i;
        refreshList();
    }

    String getSelectedSlot(){
        String group = slots.get((int)(selectedSlot/4)).getGroup();
        String hour = group.split(":")[0];
        String end = group.split(" ")[1];
        int i = selectedSlot % 4;
        String[] minutes = {":00 ", ":15 ", ":30 ", ":45 "};
        return hour + minutes[i] + end;
    }


    void next(){
        if(selectedSlot < 0){
            Toast.makeText(getActivity(), "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }
        String s = getDate("EEEE, dd MMM");
        Log.d("pranav", s + " " + getSelectedSlot());
        NewAppointmentActivity parent = (NewAppointmentActivity)getActivity();
        parent.setSelectedDateForAPI(getDate("yyyy-MM-dd"));
        parent.setStartTime(getSelectedSlot());
        parent.setEndTIme(getSelectedSlot());
        parent.goToFinalize(s, getSelectedSlot());
    }

    void fetchDoctors(){
        pBar.setVisibility(View.VISIBLE);
        rvSlots.setVisibility(View.INVISIBLE);
        String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());
        if( !isUpdate) {
            doc_id = ((NewAppointmentActivity) getActivity()).getDoctor().getId();
        }
        Call<Base<SlotList>> call = RestClient.getClient().getSlots(
                token, kioskId, new SlotsAPI(getDate("yyyy-MM-dd"), doc_id));
        call.enqueue(new Callback<Base<SlotList>>() {
            @Override
            public void onResponse(Call<Base<SlotList>> call, Response<Base<SlotList>> response) {
                Log.d("pranav", response.body().getData().toString());
                slots = response.body().getData().getSlots();
                pBar.setVisibility(View.INVISIBLE);
                rvSlots.setVisibility(View.VISIBLE);
                refreshList();
            }

            @Override
            public void onFailure(Call<Base<SlotList>> call, Throwable t) {
                Log.d("pranav", t.getMessage());
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_doctor_slots, container, false);
        rvSlots = v.findViewById(R.id.slot_list);
        rvSlots.addItemDecoration(new DividerItemDecoration(rvSlots.getContext(), DividerItemDecoration.VERTICAL));

        day = v.findViewById(R.id.day);
        date = v.findViewById(R.id.date);
        llNext = v.findViewById(R.id.next);
        llPrev = v.findViewById(R.id.prev);
        bNext = v.findViewById(R.id.b_next);
        pBar = v.findViewById(R.id.progressBar);

        setCurrentDate();


        llNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateOffset++;
                setCurrentDate();
            }
        });

        llPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateOffset--;
                setCurrentDate();
            }
        });


        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        if(isUpdate){
            bNext.setText("Confirm");
            bNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // call update API here
                    Call<Base> call = RestClient.getClient().updateAppointment(
                        token, kioskId,
                            new UpdateAppointmentAPI(
                                appointmentId, getDate("yyyy-MM-dd"), getSelectedSlot()
                            )
                    );

                    call.enqueue(new Callback<Base>() {
                        @Override
                        public void onResponse(Call<Base> call, Response<Base> response) {
                            Common_Utils.showToast("Appointment rescheduled!");
                            ((UpdateAppointmentActivity)getActivity()).goToAppointments();
                        }

                        @Override
                        public void onFailure(Call<Base> call, Throwable t) {

                        }
                    });
                }
            });

        }

        return v;
    }

    private String getDate(String format){
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dateOffset);
        dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(calendar.getTime());
    }

    private void setCurrentDate() {
        String s = getDate("MMM dd-EEEE");

        date.setText(s.split("-")[0]);
        day.setText(s.split("-")[1]);
        fetchDoctors();
    }

    class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.MyViewHolder> {

        List<Slot> slots;
        int selected;
        DoctorSlotsFragment parent;

        class MyViewHolder extends RecyclerView.ViewHolder {
            public View view;
            public TextView group;
            public List<TextView> slot_texts = new ArrayList<>();
            public List<RelativeLayout> slot_containers = new ArrayList<>();

            public MyViewHolder(View v) {
                super(v);
                view = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public SlotAdapter(List<Slot> slots, int selected, DoctorSlotsFragment parent) {
            this.slots = slots;
            this.selected = selected;
            this.parent = parent;
        }

        @Override
        public SlotAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_slot, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            vh.group = v.findViewById(R.id.group);

            vh.slot_texts = new ArrayList<>();
            vh.slot_containers = new ArrayList<>();

            int[] text_ids = {
                    R.id.slot_0_text, R.id.slot_1_text, R.id.slot_2_text, R.id.slot_3_text
            };

            int[] container_ids = {
                    R.id.slot_0_container, R.id.slot_1_container, R.id.slot_2_container,
                    R.id.slot_3_container
            };

            for(int i=0; i<4; i++){
                vh.slot_texts.add(v.findViewById(text_ids[i]));
                vh.slot_containers.add(v.findViewById(container_ids[i]));
            }

            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Slot s = slots.get(position);
            holder.group.setText(s.getGroup());
            String hour = s.getGroup().split(":")[0];
            String end = s.getGroup().split(" ")[1];
            holder.slot_texts.get(0).setText(hour + ":00 " + end);
            holder.slot_texts.get(1).setText(hour + ":15 " + end);
            holder.slot_texts.get(2).setText(hour + ":30 " + end);
            holder.slot_texts.get(3).setText(hour + ":45 " + end);

            for( int i=0; i<4; i++){
                final int j = i;
                RelativeLayout rl = holder.slot_containers.get(i);
                TextView tv = holder.slot_texts.get(i);
                if(s.getFree().size() > i && s.getFree().get(i)) {
                    rl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            parent.selectSlot(4 * position + j);
                        }
                    });
                    rl.setBackgroundColor(Color.parseColor("#ffffff"));
                    tv.setTextColor(Color.parseColor("#6f6f6f"));
                }else{
                    rl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    rl.setBackgroundColor(Color.parseColor("#e8e8e8"));
                    tv.setTextColor(Color.parseColor("#c9c4c4"));
                }

                if(this.selected == 4 * position + j){
                    holder.slot_texts.get(i).setTextColor(Color.parseColor("#0049ee"));
                    holder.slot_texts.get(i).setTypeface(holder.slot_texts.get(i).getTypeface(), Typeface.BOLD);
                    holder.slot_containers.get(i).setBackground(HealthKioskApp.getAppContext().getDrawable(R.drawable.btn_border_white));
                }
            }

        }

        @Override
        public int getItemCount() {
            return slots.size();
        }
    }

}
