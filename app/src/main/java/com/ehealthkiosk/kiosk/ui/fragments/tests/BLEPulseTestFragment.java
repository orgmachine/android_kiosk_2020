package com.ehealthkiosk.kiosk.ui.fragments.tests;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.MessageEvent;
import com.ehealthkiosk.kiosk.ui.fragments.BaseDeviceFragment;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.ehealthkiosk.kiosk.utils.pulse.BluetoothChatService;
import com.ehealthkiosk.kiosk.utils.pulse.CallBack;
import com.ehealthkiosk.kiosk.utils.pulse.ICallBack;
import com.ehealthkiosk.kiosk.utils.pulse.MtBuf;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
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

public class BLEPulseTestFragment extends BaseDeviceFragment implements ICallBack {

    @BindView(R.id.img_instruction)
    ImageView imgInstruction;
    @BindView(R.id.tv_instruction)
    TextView tvInstruction;

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
    @BindView(R.id.txt_setup)
    TextView tvSetup;
    @BindView(R.id.layout_setup_pulse)
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
    @BindView(R.id.button_cancel)
    Button buttonCancel;
    @BindView(R.id.progress)
    ProgressBar progressBar;


    BluetoothDevice device;
    ConnectionMonitor monitor;

    BluetoothAdapter btAdapt;
    boolean running = true;
    boolean isReading = false;
    private boolean stopReading = false;
    Thread pulseReaderThread;
    private BluetoothChatService mChatService;
    CharacteristicSubscription subscription;
    Connection mConnection;

    int nEntries = 0;
    int spo2_total = 0;
    int pulse_total = 0;

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_test_pulse_ble;
    }

    private void init() {
        if(getActivity() != null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
            Bundle bundle = new Bundle();
            bundle.putString("name", "weight");
            firebaseAnalytics.logEvent( "pulse_fragment_opened", bundle);
        }


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstruction();
                stopReading = true;
                if(subscription != null & subscription.isStarted()){
                    subscription.stop();
                }
            }
        });
        showInstruction();


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        if (SharedPrefUtils.getPulse(getActivity()) != null
                && !SharedPrefUtils.getPulse(getActivity()).equals("")) {
            showReading(SharedPrefUtils.getPulse(getActivity()), SharedPrefUtils.getSp02(getActivity()));
        }

    }

    private void connectDevice(){
        showSetup();
        device = Neatle.getDevice(DeviceIdPrefHelper.getDeviceAddress(mActivity, Constants.PULSE));

        monitor = Neatle.createConnectionMonitor(mActivity, device);
        monitor.setKeepAlive(true);

        monitor.setOnConnectionStateListener(new ConnectionStateListener() {
            @Override
            public void onConnectionStateChanged(Connection connection, int newState) {
                Log.d("pranav", "connection state: " + newState);
                tvSetup.setText("Connection State: " + newState);

                if(connection.isConnected()){
                    // The device has connected
                    mConnection = connection;
                    device = connection.getDevice();
                    tvSetup.setText("Connection successful!");
                    Log.d("pranav", "connection is done");
                    setupSubscription();
                    showMeasuring();
                }
            }
        });
        monitor.start();
    }

    private void setupSubscription() {

        UUID batteryService = UUID.fromString("CDEACB80-5235-4C07-8846-93A37EE6B86D");
        final UUID batteryCharacteristic = UUID.fromString("CDEACB81-5235-4C07-8846-93A37EE6B86D");

        tvSetup.setText("Setting up subscribtion with the device..");

        subscription =
                Neatle.createSubscription(mActivity, device, batteryService, batteryCharacteristic);
        subscription.setOnCharacteristicsChangedListener(new CharacteristicsChangedListener() {
            @Override
            public void onCharacteristicChanged(CommandResult change) {
                if (change.wasSuccessful()) {
                    if(change.getValue().length == 4) {

                        int pulse =  change.getValue()[1] & 0xFF;
                        int spo2 =  change.getValue()[2] & 0xFF;
                        Log.d("pranav", "" + pulse + " - " + spo2);

                        if (spo2 < 101) {
                            nEntries += 1;
                            spo2_total += spo2;
                            pulse_total += pulse;
                            try {
                                progressBar.setProgress(nEntries);
                            }catch(Exception e){

                            }
                        }
                        if(nEntries == 10){
                            String p = "" + pulse_total/10;
                            String s = "" + spo2_total/10;
                            SharedPrefUtils.setPulse(mActivity, p);
                            SharedPrefUtils.setSp02(mActivity, s);
                            mActivity.setSourceMap(Constants.PULSE, Constants.READING_DEVICE);
                            showReading(p, s);
                            subscription.stop();
                        }
                    }

//                    }
                }
            }
        });
        subscription.start();
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
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 4));
                break;
            case R.id.btn_continue:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 4));
                break;
            case R.id.btn_retry:
//                showMeasuring();
//                fetchReading();
                if(mConnection!=null && mConnection.isConnected()){
                    mConnection.disconnect();
                }
                if(monitor != null){
                    monitor.stop();
                }
                if(subscription != null){
                    subscription.stop();
                }
                showInstruction();
                break;

        }
    }

    void openManualDialog(final Context mContext) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = mActivity.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_manual_pulse, viewGroup, false);
        Button btnEnter = dialogView.findViewById(R.id.btn_enter);
        final EditText etPulse = dialogView.findViewById(R.id.et_pulse);
        final EditText etsats = dialogView.findViewById(R.id.et_sats);

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

                if (TextUtils.isEmpty(etPulse.getText().toString())) {
                    Toast.makeText(mContext, "Please Enter Pulse", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(etsats.getText().toString())) {
                    Toast.makeText(mContext, "Please Enter SpO2 value", Toast.LENGTH_SHORT).show();
                    return;
                }


                double weight = Double.parseDouble(etPulse.getText().toString());
                double spo2 = Double.parseDouble(etsats.getText().toString());

                if (weight <= 0) {
                    Toast.makeText(mContext, "Invalid Pulse", Toast.LENGTH_SHORT).show();
                    return;
                } if (spo2 <= 0 || spo2 > 100) {
                    Toast.makeText(mContext, "Invalid O2 sats", Toast.LENGTH_SHORT).show();
                    return;
                }


                final String pulseStr = etPulse.getText().toString();
                final String o2str = etsats.getText().toString();
                SharedPrefUtils.setPulse(mActivity, pulseStr);
                SharedPrefUtils.setSp02(mActivity, o2str);


                alertDialog.dismiss();

                mActivity.setSourceMap(Constants.PULSE, Constants.READING_MANUAL);
                showReading(pulseStr, o2str);


//                if (Common_Utils.isNotNullOrEmpty(Sp02Str)){
//                    tvReading1.setText(Sp02Str + " % Sp02");
//                }else{
//                    tvReading1.setText("");
//                }


            }
        });
    }

    void showReading(String pulse, String oxygenLevel) {
        try {
            layoutSetupHeight.setVisibility(View.INVISIBLE);
            layoutBtnHeight.setVisibility(View.INVISIBLE);
            layoutInstruction.setVisibility(View.INVISIBLE);
            layoutMeasuringHeight.setVisibility(View.INVISIBLE);
            layoutReadingHeight.setVisibility(View.VISIBLE);

            tvReading.setText(pulse + " BPM");
            if (oxygenLevel != null) {
                tvReading1.setVisibility(View.VISIBLE);
                tvReading1.setText(oxygenLevel + "% Sp02");
            } else {
                tvReading1.setVisibility(View.GONE);
            }
        }catch (Exception e){

        }
    }

    void showMeasuring() {
        try {
            progressBar.setProgress(0);
            isReading = true;
            pulse_total = nEntries = spo2_total = 0;
            layoutSetupHeight.setVisibility(View.INVISIBLE);
            layoutBtnHeight.setVisibility(View.INVISIBLE);
            layoutInstruction.setVisibility(View.INVISIBLE);
            layoutMeasuringHeight.setVisibility(View.VISIBLE);
            layoutReadingHeight.setVisibility(View.INVISIBLE);
        }catch (Exception e){

        }


    }

    void showInstruction() {
        try {
            isReading = false;
            layoutSetupHeight.setVisibility(View.INVISIBLE);
            layoutBtnHeight.setVisibility(View.VISIBLE);
            layoutInstruction.setVisibility(View.VISIBLE);
            layoutMeasuringHeight.setVisibility(View.INVISIBLE);
            layoutReadingHeight.setVisibility(View.INVISIBLE);
        }catch (Exception e){

        }


    }

    void showSetup() {
        isReading = false;
        layoutSetupHeight.setVisibility(View.VISIBLE);
        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
        layoutReadingHeight.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onDestroy() {
        //mActivity.unregisterReceiver(searchDevices);
        super.onDestroy();
        //android.os.Process.killProcess(android.os.Process.myPid());

    }

    @Override
    public void onStop() {
        super.onStop();
        running = false;
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

    @Override
    public void call() {

    }

}
