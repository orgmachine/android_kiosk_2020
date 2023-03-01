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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.MessageEvent;
import com.ehealthkiosk.kiosk.ui.activities.MainActivity;
import com.ehealthkiosk.kiosk.ui.fragments.BaseDeviceFragment;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.vise.baseble.ViseBle;
import com.vise.baseble.core.DeviceMirror;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
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
import si.inova.neatle.operation.Operation;
import si.inova.neatle.operation.OperationResults;
import si.inova.neatle.operation.SimpleOperationObserver;
import si.inova.neatle.source.ByteArrayInputSource;
import si.inova.neatle.source.InputSource;

public class HeightTestFragment extends BaseDeviceFragment {

    private boolean running = true;
    @BindView(R.id.img_instruction)
    ImageView imgInstruction;
    @BindView(R.id.tv_instruction)
    TextView tvInstruction;
    @BindView(R.id.error_height)
    TextView error;

    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.btn_manual)
    Button btnManual;
    @BindView(R.id.btn_skip)
    Button btnSkip;
    @BindView(R.id.tv_reading)
    TextView tvReading;

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    @BindView(R.id.layout_setup_height)
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


    private enum Connected {False, Pending, True}

    private List<Double> values = new ArrayList<>();

    private boolean initialStart = true;
    private Connected connected = Connected.False;
    private boolean is_connected = false;

    private DeviceMirror mDeviceMirror;

    private BluetoothDevice device;
    private ConnectionMonitor monitor;
    private CharacteristicSubscription subscription;

    private MainActivity mActivity;
    private Connection mConnection;

//    private String deviceAddress = "E8:EB:11:0D:E2:B8";

    @Override
    protected int getLayout() {
        return R.layout.fragment_test_height;
    }

    private void setup() {
        ViseBle.config()
                .setScanTimeout(-1)
                .setConnectTimeout(10 * 1000)
                .setOperateTimeout(5 * 1000)
                .setConnectRetryCount(3)
                .setConnectRetryInterval(1000)
                .setOperateRetryCount(3)
                .setOperateRetryInterval(1000)
                .setMaxConnectCount(3);

        ViseBle.getInstance().init(mActivity);

        Log.d("TAG", "setup successful");


    }

    private void init() {
        if(getActivity() != null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
            Bundle bundle = new Bundle();
            bundle.putString("name", "weight");
            firebaseAnalytics.logEvent( "height_fragment_opened", bundle);
        }
        setup();


        mActivity = (MainActivity) getActivity();
        showInstruction();


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstruction();
                values = new ArrayList<>();
            }
        });


        //Common_Utils.showProgress(mActivity);
        connectToDevice();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        if (SharedPrefUtils.getHeightFT(getActivity()) != null
                && !SharedPrefUtils.getHeightFT(getActivity()).equals("") &&
                SharedPrefUtils.getHeightInch(getActivity()) != null &&
                !SharedPrefUtils.getHeightInch(getActivity()).equals("")) {

            showReading(SharedPrefUtils.getHeightFT(getActivity()) + " Feet "
                    + SharedPrefUtils.getHeightInch(getActivity()) + " inch");
        }


    }


    @OnClick({R.id.btn_start, R.id.btn_manual, R.id.btn_skip, R.id.btn_continue, R.id.btn_manual1, R.id.btn_retry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
//                connectToDevice();
                takeReading();
                showMeasuring();
                break;
            case R.id.btn_manual1:
            case R.id.btn_manual:
                openManualDialog(mActivity);
                break;
            case R.id.btn_skip:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 1));
                break;
            case R.id.btn_continue:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 1));
                break;
            case R.id.btn_retry:
                if (is_connected) {
                    sendCommand("O");
                }
                showInstruction();
                values = new ArrayList<>();

                break;

        }
    }

    private void connectToDevice() {

        String deviceAddress = DeviceIdPrefHelper.getDeviceAddress(mActivity, Constants.HEIGHT);
        device = Neatle.getDevice(deviceAddress);

        monitor = Neatle.createConnectionMonitor(mActivity, device);
        monitor.setKeepAlive(true);

        monitor.setOnConnectionStateListener(new ConnectionStateListener() {
            @Override
            public void onConnectionStateChanged(Connection connection, int newState) {
                Log.d("TAG", "New State " + newState );
                mConnection = connection;

                if(connection.isConnected()){
                    Log.d("TAG", "device is connected now");
                    btnStart.setEnabled(true);
                    error.setVisibility(View.INVISIBLE);
                    is_connected = true;
                    if(subscription != null){
                        subscription.stop();
                    }

                    UUID batteryService = Neatle.createUUID(0xFFE0);
                    UUID characteristicUUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");
                    subscription =
                            Neatle.createSubscription(mActivity, device, batteryService, characteristicUUID);
                    subscription.setOnCharacteristicsChangedListener(new CharacteristicsChangedListener() {
                        @Override
                        public void onCharacteristicChanged(CommandResult change) {
                            if (change.wasSuccessful()) {
                                Log.d("TAG", "got a notification");
                                parseReading(change);
                            }
                        }
                    });
                    subscription.start();
                    Log.d("TAG", "subscription is now started");

                    sendCommand("O");

                    // The device has connected
                }
                else if(connection.isConnecting()){

                    is_connected = false;
                    error.setVisibility(View.VISIBLE);
                    Log.d("TAG", "trying to connect now");
                    btnStart.setEnabled(false);
                }else {

                    error.setVisibility(View.VISIBLE);
                    is_connected = false;
                    Log.d("TAG", "device disconnected");
                    btnStart.setEnabled(false);
                }
            }
        });
        monitor.start();
    }


    private void parseReading(CommandResult change){
        byte[] data = change.getValue();
        if (data.length > 1) {
            String value = new String(data);
            String inp = value.replace(":", "");
            if (inp.contains("OK")) {
                return;
            }
            if (inp.contains("Er")) {
                sendCommand("F");
                return;
            }
            String[] separated = inp.split("m");
            String dataValue = separated[0];


            Float meters = Float.parseFloat(dataValue);

            Double defaultReading = Double.valueOf(DeviceIdPrefHelper.getDefaultHeight(mActivity,
                    Constants.DEFAULT_HEIGHT));

            Double resultValue = defaultReading - meters;
            if (resultValue < 0) {
                sendCommand("F");
            } else {
                Integer footValue = (int) Math.floor((resultValue * 100 / 2.54) / 12);
                Integer inch = (int) ((resultValue * 100) / 2.54) % 12;
                String reading = footValue + " feet " + inch + " inch " + "( " + (int)(resultValue*100) + " cms )";

                SharedPrefUtils.setHeightFT(mActivity, String.valueOf(footValue));
                SharedPrefUtils.setHeightInch(mActivity, String.valueOf(inch));
                mActivity.setSourceMap(Constants.HEIGHT, Constants.READING_DEVICE);
                showReading(reading);

            }
        }
    }

    private void takeReading() {
        Log.d("TAG", String.valueOf(is_connected));
        if (is_connected) {

            sendCommand("F");

        }

    }



    private void sendCommand(String command) {
        Log.d("TAG", "sending " + command);
        UUID serviceToWrite = Neatle.createUUID(0xFFE0);
        final UUID characteristicToWrite = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");
        byte[] dataToWrite = command.getBytes();

        InputSource inputSource = new ByteArrayInputSource(dataToWrite);

        Operation operation = Neatle.createOperationBuilder(mActivity)
                .write(serviceToWrite, characteristicToWrite, inputSource)
                .onFinished(new SimpleOperationObserver() {
                    @Override
                    public void onOperationFinished(Operation op, OperationResults results) {
                        if (results.wasSuccessful()) {
                            System.out.println("Write was successful!");
                        } else {
                            System.out.println("Write failed! ");
                        }
                    }
                })
                .build(device);
        operation.execute();

    }

    void openManualDialog(final Context mContext) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = mActivity.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_manual_height, viewGroup, false);
        final EditText etHeightFt = dialogView.findViewById(R.id.et_feet);
        final EditText etHeightInch = dialogView.findViewById(R.id.et_inches);
        Button btnEnter = dialogView.findViewById(R.id.btn_enter);


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

                if (TextUtils.isEmpty(etHeightFt.getText().toString())) {
                    Toast.makeText(mContext, getResources().getString(R.string.enter_feet_toast), Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(etHeightInch.getText().toString())) {
                    Toast.makeText(mContext, getResources().getString(R.string.enter_inch_toast), Toast.LENGTH_SHORT).show();
                    return;
                }

                final int feet = Integer.parseInt(etHeightFt.getText().toString());
                final int inch = Integer.parseInt(etHeightInch.getText().toString());

                if (feet > 7 || (feet == 7 && inch > 0)) {
                    Toast.makeText(mContext, getResources().getString(R.string.invalid_height_toast), Toast.LENGTH_SHORT).show();
                    return;
                }


                if (inch > 11) {
                    Toast.makeText(mContext, getResources().getString(R.string.invalid_height_toast), Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPrefUtils.setHeightFT(mActivity, etHeightFt.getText().toString());
                SharedPrefUtils.setHeightInch(mActivity, etHeightInch.getText().toString());

                alertDialog.dismiss();
                mActivity.setSourceMap(Constants.HEIGHT, Constants.READING_MANUAL);
                showReading(feet + " Feet " + inch + " inch");


            }
        });


    }

    void showReading(String reading) {
        layoutSetupHeight.setVisibility(View.INVISIBLE);
        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
        layoutReadingHeight.setVisibility(View.VISIBLE);

        tvReading.setText(reading);
    }

    void showMeasuring() {

        layoutSetupHeight.setVisibility(View.INVISIBLE);
        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.VISIBLE);
        layoutReadingHeight.setVisibility(View.INVISIBLE);


    }

    void showInstruction() {
        layoutSetupHeight.setVisibility(View.INVISIBLE);
        layoutBtnHeight.setVisibility(View.VISIBLE);
        layoutInstruction.setVisibility(View.VISIBLE);
        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
        layoutReadingHeight.setVisibility(View.INVISIBLE);
    }

    void showSetup() {
        layoutSetupHeight.setVisibility(View.VISIBLE);
        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
        layoutReadingHeight.setVisibility(View.INVISIBLE);
    }



    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        if (SharedPrefUtils.getHeightFT(getActivity()) != null
                && !SharedPrefUtils.getHeightFT(getActivity()).equals("") &&
                SharedPrefUtils.getHeightInch(getActivity()) != null &&
                !SharedPrefUtils.getHeightInch(getActivity()).equals("")) {

            Log.d("TAG", SharedPrefUtils.getHeightFT(getActivity()) + " Feet "
                    + SharedPrefUtils.getHeightInch(getActivity()) + " inch");

            showReading(SharedPrefUtils.getHeightFT(getActivity()) + " Feet "
                    + SharedPrefUtils.getHeightInch(getActivity()) + " inch");
        }
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onDestroy() {
        Log.d("TAG", "height destroy called");
        super.onDestroy();
    }
    @Override
    public void onStop() {
        super.onStop();
        running = false;
        sendCommand("C");
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
}


