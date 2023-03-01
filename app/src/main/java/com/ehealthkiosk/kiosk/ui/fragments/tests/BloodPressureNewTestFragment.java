package com.ehealthkiosk.kiosk.ui.fragments.tests;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.contec.bp.code.base.ContecDevice;
import com.contec.bp.code.bean.ContecBluetoothType;
import com.contec.bp.code.callback.BluetoothSearchCallback;
import com.contec.bp.code.callback.CommunicateCallback;
import com.contec.bp.code.connect.ContecSdk;
import com.contec.bp.code.tools.Utils;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.MessageEvent;
import com.ehealthkiosk.kiosk.ui.fragments.BaseDeviceFragment;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import si.inova.neatle.Neatle;
import si.inova.neatle.monitor.Connection;
import si.inova.neatle.monitor.ConnectionMonitor;
import si.inova.neatle.monitor.ConnectionStateListener;
import si.inova.neatle.operation.CharacteristicSubscription;
import si.inova.neatle.operation.CharacteristicsChangedListener;
import si.inova.neatle.operation.CommandResult;

public class BloodPressureNewTestFragment extends BaseDeviceFragment {

    String TAG = "BloodPressureTestFragment";
    BluetoothDevice device;
    ConnectionMonitor monitor;
    Connection mConnection;
    CharacteristicSubscription subscription;

    boolean running = true;
    boolean isDeviceConnected = false;
    boolean isRetry = false;
    boolean isMeasuring = false;

    @BindView(R.id.img_instruction)
    ImageView imgInstruction;
    @BindView(R.id.tv_instruction)
    TextView tvInstruction;
    @BindView(R.id.measuring_text)
    TextView tvMeasuring;
    @BindView(R.id.txt_setup)
    TextView tvSetup;

    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.btn_manual)
    Button btnManual;
    @BindView(R.id.btn_skip)
    Button btnSkip;
    @BindView(R.id.tv_reading)
    TextView tvReading;
    @BindView(R.id.tv_reading1)
    TextView tvReading1;


    @BindView(R.id.layout_setup_bp)
    LinearLayout layoutSetupHeight;

    @BindView(R.id.layout_instruction)
    LinearLayout layoutInstruction;

    @BindView(R.id.layout_btn_height)
    LinearLayout layoutBtnHeight;

    @BindView(R.id.layout_reading_height)
    LinearLayout layoutReadingHeight;

    @BindView(R.id.layout_measuring_height)
    LinearLayout layoutMeasuringHeight;
    @BindView(R.id.btn_continue)
    Button btnContinue;
    @BindView(R.id.btn_manual1)
    Button btnManual1;
    @BindView(R.id.btn_retry)
    Button btnRetry;
    @BindView(R.id.btn_cancel)
    Button btnCancel;


    @Override
    protected int getLayout() {
        return R.layout.fragment_test_newbloodpressure;
    }

    private void init() {
        if(getActivity() != null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
            Bundle bundle = new Bundle();
            bundle.putString("name", "weight");
            firebaseAnalytics.logEvent( "bp_fragment_opened", bundle);
        }
//        showSetup();
//        sdk.startBluetoothSearch(bluetoothSearchCallback, 20000);
        showInstruction();



    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        running = false;

        super.onStop();
        clearConnection();
    }


    void showReading(String systollicReading, String diastollicReading) {
        try {
            isMeasuring = false;

            layoutSetupHeight.setVisibility(View.INVISIBLE);
            layoutBtnHeight.setVisibility(View.INVISIBLE);
            layoutInstruction.setVisibility(View.INVISIBLE);
            layoutMeasuringHeight.setVisibility(View.INVISIBLE);
            layoutReadingHeight.setVisibility(View.VISIBLE);

            tvReading.setText("Systolic " + systollicReading + " mmhg");
            tvReading1.setText("Diastolic " + diastollicReading + " mmhg");
        }catch(Exception e){

        }
    }

    void showMeasuring() {
        try {
            isMeasuring = true;
            tvMeasuring.setText("Measuring...");

            layoutSetupHeight.setVisibility(View.INVISIBLE);
            layoutBtnHeight.setVisibility(View.INVISIBLE);
            layoutInstruction.setVisibility(View.INVISIBLE);
            layoutMeasuringHeight.setVisibility(View.VISIBLE);
            layoutReadingHeight.setVisibility(View.INVISIBLE);
        }catch(Exception e){

        }



    }

    void showInstruction() {
        isMeasuring = false;
        layoutSetupHeight.setVisibility(View.INVISIBLE);
        layoutBtnHeight.setVisibility(View.VISIBLE);
        layoutInstruction.setVisibility(View.VISIBLE);
        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
        layoutReadingHeight.setVisibility(View.INVISIBLE);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstruction();
                if (isDeviceConnected) {
                    isDeviceConnected = false;
                } else {

                }

            }
        });
    }

    void showSetup() {
        isMeasuring = false;
        layoutSetupHeight.setVisibility(View.VISIBLE);
        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
        layoutReadingHeight.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        if (SharedPrefUtils.getSystolic(getActivity()) != null
                && !SharedPrefUtils.getSystolic(getActivity()).equals("") &&
                SharedPrefUtils.getDiastolic(getActivity()) != null
                && !SharedPrefUtils.getDiastolic(getActivity()).equals("")

        ) {
            showReading(SharedPrefUtils.getSystolic(getActivity()),
                    SharedPrefUtils.getDiastolic(getActivity()));
        }
    }

    private void connectDevice(){
        showSetup();
        device = Neatle.getDevice(DeviceIdPrefHelper.getDeviceAddress(mActivity, Constants.BP));

        monitor = Neatle.createConnectionMonitor(mActivity, device);
        monitor.setKeepAlive(true);

        monitor.setOnConnectionStateListener(new ConnectionStateListener() {
            @Override
            public void onConnectionStateChanged(Connection connection, int newState) {
                Log.d("pranav", "connection state: " + newState);

                if(connection.isConnected()){
                    mConnection = connection;
                    // The device has connected
                    device = connection.getDevice();
                    Log.d("pranav", "connection is done");
                    setupSubscription();
                    showMeasuring();
                }
            }
        });
        monitor.start();
    }

    private void setupSubscription() {

        UUID batteryService = UUID.fromString("0000FFF0-0000-1000-8000-00805F9B34FB");
        final UUID batteryCharacteristic = UUID.fromString("0000FFF1-0000-1000-8000-00805F9B34FB");

        subscription =
                Neatle.createSubscription(mActivity, device, batteryService, batteryCharacteristic);
        subscription.setOnCharacteristicsChangedListener(new CharacteristicsChangedListener() {
            @Override
            public void onCharacteristicChanged(CommandResult change) {
                if (change.wasSuccessful()) {
                    try {
                        if (change.getValue().length >= 4) {
                            Log.d("pranav", ((int) change.getValue()[2] == -4) + " - " + change.getValue()[3] + " - " + change.getValue()[4] + " - " + change.getValue()[5] + " - " + change.getValue()[6]);
//                    if(change.getValue().length == 4){
//                        tv.setText(String.valueOf((int)change.getValue()[1]) + " - " + String.valueOf((int)change.getValue()[2]));

                            if ((int) change.getValue()[2] == -4) {
                                int a = change.getValue()[3] & 0xFF;
                                int b = change.getValue()[4] & 0xFF;
                                mActivity.setSourceMap(Constants.BP, Constants.READING_DEVICE);
                                showReading("" + a, "" + b);
                                SharedPrefUtils.setSystolic(mActivity, "" + a);
                                SharedPrefUtils.setDiastolic(mActivity, "" + b);
                            } else if ((int) change.getValue()[2] == -5) {
                                int val = change.getValue()[4] & 0xFF;
                                tvMeasuring.setText(val + " mmhg");
                            }
                        }

//                    }
                    }catch (Exception e){

                    }
                }
            }
        });
        subscription.start();
    }

    void clearConnection(){
        if(mConnection!=null && mConnection.isConnected()){
            mConnection.disconnect();
        }
        if(monitor != null){
            monitor.stop();
        }
        if(subscription != null){
            subscription.stop();
        }
    }


    @OnClick({R.id.btn_start, R.id.btn_manual, R.id.btn_skip, R.id.btn_continue, R.id.btn_manual1, R.id.btn_retry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                connectDevice();
                break;
            case R.id.btn_manual1:
            case R.id.btn_manual:
                openManualDialog(mActivity);
                break;
            case R.id.btn_skip:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 3));
                break;
            case R.id.btn_continue:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 3));
                break;
            case R.id.btn_retry:
                clearConnection();
                showInstruction();
                break;

        }
    }


    void openManualDialog(final Context mContext) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = mActivity.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_manual_bloodpressure, viewGroup, false);
        Button btnEnter = dialogView.findViewById(R.id.btn_enter);
        final EditText sysET = dialogView.findViewById(R.id.et_systolic);
        final EditText diasysET = dialogView.findViewById(R.id.et_diastolic);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(sysET.getText().toString())) {
                    Toast.makeText(mContext, getResources().getString(R.string.enter_syst), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(diasysET.getText().toString())) {
                    Toast.makeText(mContext, getResources().getString(R.string.enter_diast), Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(sysET.getText().toString())) {
                    Toast.makeText(mContext, getResources().getString(R.string.enter_syst), Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(diasysET.getText().toString())) {
                    Toast.makeText(mContext, getResources().getString(R.string.enter_diast), Toast.LENGTH_SHORT).show();
                    return;
                }

                int diasys = Integer.parseInt(diasysET.getText().toString());
                int sys = Integer.parseInt(sysET.getText().toString());

                if (diasys <= 20 || diasys >= 200) {
                    Toast.makeText(mContext, getResources().getString(R.string.invalid_diast_toast), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (sys <= 0 || sys >= 200) {
                    Toast.makeText(mContext, getResources().getString(R.string.invalid_syst_toast), Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPrefUtils.setSystolic(mActivity, sysET.getText().toString());
                SharedPrefUtils.setDiastolic(mActivity, diasysET.getText().toString());

                alertDialog.dismiss();


                mActivity.setSourceMap(Constants.BP, Constants.READING_MANUAL);
                showReading(sysET.getText().toString(), diasysET.getText().toString());


            }
        });
    }


}
