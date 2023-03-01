package com.ehealthkiosk.kiosk.ui.fragments.consult;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ehealthkiosk.kiosk.model.consult.requests.CreateAppointmentAPI;
import com.ehealthkiosk.kiosk.ui.activities.consult.NewAppointmentActivity;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentFinalizeFragment extends Fragment {

    @BindView(R.id.profile_image)
    CircleImageView image;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.date1)
    TextView date1;
    @BindView(R.id.date2)
    TextView date2;
    @BindView(R.id.fees)
    TextView fees;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.reason)
    EditText reason;

    Doctor doctor;
    NewAppointmentActivity parent;


    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);

    public AppointmentFinalizeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void callCreateAppointmentAPI() {
        Common_Utils.showProgress(getActivity(), true);
        submit.setEnabled(false);
        String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());
        CreateAppointmentAPI params = new CreateAppointmentAPI(
                doctor.getId(),
                Integer.valueOf(SharedPrefUtils.getProfileId(parent)),
                parent.getSelectedDateForAPI(),
                parent.getStartTime(),
                parent.getEndTIme(),
                reason.getText().toString()
        );

        Call<Base> call = RestClient.getClient().createAppointment(
                token,
                kioskId,
                params
        );
        call.enqueue(new Callback<Base>() {
            @Override
            public void onResponse(Call<Base> call, Response<Base> response) {
                Toast.makeText(parent, "Your appointment was created!", Toast.LENGTH_SHORT).show();

                // show non dismissable dialog
                showCustomDialog();
                Common_Utils.hideProgress();
submit.setEnabled(false);
//                parent.goToAppointments();
            }

            @Override
            public void onFailure(Call<Base> call, Throwable t) {

                Common_Utils.hideProgress();
                submit.setEnabled(true);
            }
        });

    }

    public void showCustomDialog(){
        ViewGroup viewGroup = parent.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(parent).inflate(R.layout.dialog_payment, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);

        alertDialog.show();
        alertDialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.goToAppointments();
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("pranav", "finalize view create called");
        View v = inflater.inflate(R.layout.fragment_appointment_finalize, container, false);
        ButterKnife.bind(this, v);

        parent = (NewAppointmentActivity)getActivity();
        doctor = parent.getDoctor();

        Glide.with(parent)
                .load(doctor.getProfilePic())
                .circleCrop()
                .into(image);

        name.setText(doctor.getName());
        description.setText(doctor.getDescription());
        date1.setText("At " + parent.getSelectedTime() + " on");
        date2.setText(parent.getSelectedDate());
        fees.setText("₹ " + doctor.getFees());
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reason.getText().toString().equals("")){
                    Toast.makeText(parent, "Please fill a reason", Toast.LENGTH_SHORT).show();
                }else {
                    callCreateAppointmentAPI();
                }
            }
        });
        return v;
    }

}
