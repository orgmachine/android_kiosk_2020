package com.ehealthkiosk.kiosk.ui.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.ui.activities.KioskIdData;
import com.ehealthkiosk.kiosk.ui.activities.SettingsDataResponse;
import com.ehealthkiosk.kiosk.ui.activities.SettingsDeviceId;
import com.ehealthkiosk.kiosk.ui.activities.SettingsPresenter;
import com.ehealthkiosk.kiosk.ui.activities.SettingsPresenterImpl;
import com.ehealthkiosk.kiosk.ui.activities.SettingsView;
import com.ehealthkiosk.kiosk.ui.activities.WelcomeActivity;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.google.android.material.button.MaterialButton;
import com.vise.baseble.ViseBle;
import com.vise.baseble.core.DeviceMirror;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
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


public class DeviceAddressFragment extends Fragment implements SettingsView {

    private boolean is_connected = false;
    protected Activity mActivity;

    public static final String BP_CONTEC = "Contec";
    public static final String BP_BIOS = "Bios BP";

    public static final String PULSE_CONTEC = "Contec";
    public static final String PULSE_BPL = "BPL";

    private DeviceMirror mDeviceMirror;
    /*
    * This code will be refractored later
    * */
    @BindView(R.id.device_one_inp_text)
    EditText device_1_address;
    @BindView(R.id.device_2_inp_text)
    EditText device_2_address;
    @BindView(R.id.device_3_inp_text)
    EditText device_3_address;
    @BindView(R.id.device_4_inp_text)
    EditText device_4_address;
    @BindView(R.id.device_5_inp_text)
    EditText device_5_address;
    @BindView(R.id.device_6_inp_text)
    EditText device_6_address;
    @BindView(R.id.device_7_inp_text)
    EditText device_7_address;
    @BindView(R.id.print_url_inp_text)
    EditText printUrlInpText;
    @BindView(R.id.measure_height)
    MaterialButton measureHeight;
    @BindView(R.id.container_layout)
    LinearLayout containerLayout;
    @BindView(R.id.access_denied_container)
    LinearLayout accessDeniedContainer;
    @BindView(R.id.retry_password)
    MaterialButton retryPassword;
    @BindView(R.id.sync_button)
    MaterialButton syncButton;
    @BindView(R.id.update_btn)
    Button updateBtn;
    @BindView(R.id.is_wifi)
    RadioButton isWifi;
    @BindView(R.id.is_raspberry_pi)
    RadioButton isRaspberryPi;
    @BindView(R.id.bp_spinner)
    Spinner bpSpinner;
    @BindView(R.id.pulse_spinner)
    Spinner pulseSpinner;


//    private ArrayList<String> devices = new ArrayList<>();

    private BluetoothDevice device;
    private ConnectionMonitor monitor;
    private CharacteristicSubscription subscription;
    private SettingsPresenter settingsPresenter;
    private Connection mConnection;

    String selectedBPDevice = BP_CONTEC;
    String selectedPulseDevice = PULSE_CONTEC;

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

        ViseBle.getInstance().init(getActivity());

        Log.d("TAG", "setup successful");

    }

    @OnClick(R.id.device_settings_save)
    public void saveSettings(View v){
        if(device_7_address.getText().toString().equals("")){
            Toast.makeText(getActivity(), getResources().getString(R.string.default_height), Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getActivity(), getResources().getString(R.string.settings_added), Toast.LENGTH_SHORT).show();
        DeviceIdPrefHelper.setDeviceAddress(getActivity(), Constants.PULSE, device_1_address.getText().toString());
        DeviceIdPrefHelper.setDeviceAddress(getActivity(), Constants.TEMPERATURE, device_2_address.getText().toString());
        DeviceIdPrefHelper.setDeviceAddress(getActivity(), Constants.BP, device_3_address.getText().toString());
        DeviceIdPrefHelper.setDeviceAddress(getActivity(), Constants.WEIGHT, device_4_address.getText().toString());
        DeviceIdPrefHelper.setDeviceAddress(getActivity(), Constants.HEIGHT, device_5_address.getText().toString());
        DeviceIdPrefHelper.setDeviceAddress(getActivity(), Constants.GLUECOSE, device_6_address.getText().toString());
        DeviceIdPrefHelper.setDefaultHeight(getActivity(), Constants.DEFAULT_HEIGHT, device_7_address.getText().toString());
        DeviceIdPrefHelper.setDefaultHeight(getActivity(), Constants.PRINT_URL, printUrlInpText.getText().toString());


        DeviceIdPrefHelper.setDeviceAddress(getActivity(), Constants.BP_DEVICE_TYPE, selectedBPDevice);
        DeviceIdPrefHelper.setDeviceAddress(getActivity(), Constants.PULSE_DEVICE_TYPE, selectedPulseDevice);

        Intent i = new Intent(getActivity(), WelcomeActivity.class);
        startActivity(i);
    }

    public void show_saved_settings(){
        device_1_address.setText(DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.PULSE));
        device_2_address.setText(DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.TEMPERATURE));
        device_3_address.setText(DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.BP));
        device_4_address.setText(DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.WEIGHT));
        device_5_address.setText(DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.HEIGHT));
        device_6_address.setText(DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.GLUECOSE));
        device_7_address.setText(DeviceIdPrefHelper.getDefaultHeight(getActivity(), Constants.DEFAULT_HEIGHT));
        printUrlInpText.setText(DeviceIdPrefHelper.getPrintUrl(getActivity(), Constants.PRINT_URL));
        if(DeviceIdPrefHelper.getWifiPrinter(getActivity(), false)){
            isWifi.setChecked(true);
        }else{
            isRaspberryPi.setChecked(true);
        }

        if(DeviceIdPrefHelper.getDeviceAddress(mActivity, Constants.BP_DEVICE_TYPE).equals(BP_BIOS)) {
            bpSpinner.setSelection(1);
        }
        if(DeviceIdPrefHelper.getDeviceAddress(mActivity, Constants.PULSE_DEVICE_TYPE).equals(PULSE_BPL)) {
            pulseSpinner.setSelection(1);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_device_address, container, false);
        ButterKnife.bind(this, view);
        containerLayout.setVisibility(View.GONE);

        List<String> categories = new ArrayList<String>();
        categories.add(BP_CONTEC);
        categories.add(BP_BIOS);
        List<String> pulseCategories = new ArrayList<String>();
        pulseCategories.add(PULSE_CONTEC);
        pulseCategories.add(PULSE_BPL);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        bpSpinner.setAdapter(dataAdapter);
        bpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBPDevice = categories.get(position);
                if(!selectedBPDevice.equals(DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.BP_DEVICE_TYPE)))
                device_3_address.setText("");
                else device_3_address.setText(DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.BP));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterPulse = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, pulseCategories);

        // Drop down layout style - list view with radio button
        dataAdapterPulse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        pulseSpinner.setAdapter(dataAdapterPulse);
        pulseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPulseDevice = pulseCategories.get(position);

                if(!selectedPulseDevice.equals(DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.PULSE_DEVICE_TYPE)))
                    device_1_address.setText("");
                else device_1_address.setText(DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.PULSE));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        isWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DeviceIdPrefHelper.setWifiPrinter(getActivity(), isChecked);
            }
        });

        isRaspberryPi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DeviceIdPrefHelper.setRaspberryPi(getActivity(), isChecked);
            }
        });



        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DeviceIdPrefHelper.getkioskId(getActivity(), Constants.KIOSK_ID) != null &&
                        !DeviceIdPrefHelper.getkioskId(getActivity(), Constants.KIOSK_ID).equals("")) {
                    settingsPresenter = new SettingsPresenterImpl(DeviceAddressFragment.this);
                    KioskIdData kioskIdData = new KioskIdData();
                    kioskIdData.setMacAddress("yolo_abcd");
                    settingsPresenter.settings(kioskIdData);
                }
            }
        });


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://stgapi.yolohealth.co.in/update"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        measureHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common_Utils.enableBluetooth();
                takeReading();

            }
        });

        retryPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPassworDialog();
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    void openPassworDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = mActivity.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_settings_password, viewGroup, false);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnEnter = dialogView.findViewById(R.id.btn_enter);
        ImageView cancelDialog = dialogView.findViewById(R.id.dialog_cancel);
        EditText adminpassword = dialogView.findViewById(R.id.et_password);
        String adminPasswordInp = adminpassword.getText().toString();
        adminpassword.setText(adminPasswordInp);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                accessDeniedContainer.setVisibility(View.VISIBLE);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                accessDeniedContainer.setVisibility(View.VISIBLE);
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adminpassword.getText().toString().equals(DeviceIdPrefHelper.getAdminPassword(getActivity(), Constants.ADMIN_PASSWORD))) {
                    setup();
                    containerLayout.setVisibility(View.VISIBLE);
                    accessDeniedContainer.setVisibility(View.GONE);
                    show_saved_settings();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Password Invalid!/nPlease enter a valid password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            containerLayout.setVisibility(View.GONE);
            openPassworDialog();
            connectToDevice();
            show_saved_settings();
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

    private void takeReading() {
        Log.d("TAG", String.valueOf(is_connected));
        if (is_connected) {

            sendCommand("F");

        }
    }


    private void connectToDevice() {
        try {

            String deviceAddress = DeviceIdPrefHelper.getDeviceAddress(mActivity, Constants.HEIGHT);
            device = Neatle.getDevice(deviceAddress);

            monitor = Neatle.createConnectionMonitor(mActivity, device);
            monitor.setKeepAlive(true);

            monitor.setOnConnectionStateListener(new ConnectionStateListener() {
                @Override
                public void onConnectionStateChanged(Connection connection, int newState) {
                    Log.d("TAG", "New State " + newState);
                    if (connection.isConnected()) {
                        mConnection = connection;
                        Log.d("TAG", "device is connected now");
                        measureHeight.setEnabled(true);
                        is_connected = true;
                        if (subscription != null) {
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
                    } else if (connection.isConnecting()) {

                        is_connected = false;

                        Log.d("TAG", "trying to connect now");
                        measureHeight.setEnabled(false);
                    } else {

                        is_connected = false;
                        Log.d("TAG", "device disconnected");
                        measureHeight.setEnabled(false);
                    }
                }
            });
            monitor.start();
        }catch (Exception ex){
            Toast.makeText(getActivity(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }


    private void parseReading(CommandResult change){
        byte[] data = change.getValue();
        if (data.length > 1) {
            String value = new String(data);
            String inp = value.replace(":", "");
            Log.d("TAG", inp);
            if (inp.contains("OK")) {

                Log.d("TAG", "OK call");
                return;
            }
            if (inp.contains("Er")) {

                Log.d("TAG", "error");
                sendCommand("F");
                return;
            }
            String[] separated = inp.split("m");
            String dataValue = separated[0];


            Float meters = Float.parseFloat(dataValue);

            Float resultValue = meters;
            if (resultValue < 0) {

                Log.d("TAG", "result was less than 0");
                sendCommand("F");
            } else {
                Toast.makeText(mActivity, "Reading " + resultValue.toString(), Toast.LENGTH_SHORT).show();

                device_7_address.setText(resultValue.toString());

            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if(mConnection !=null && mConnection.isConnected()) {
            sendCommand("C");
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
    public void showProgress() {
        Common_Utils.showProgress(getActivity());
    }

    @Override
    public void showSuccess(SettingsDataResponse settingsDataResponse, String msg) {
        if (settingsDataResponse != null) {
            Common_Utils.hideProgress();
            List<SettingsDeviceId> deviceIds = settingsDataResponse.getData().getSettings();
            for (int i = 0; i < deviceIds.size(); i++) {
                DeviceIdPrefHelper.putString(getActivity(), deviceIds.get(i).getKey(), deviceIds.get(i).getValue());
            }
            show_saved_settings();

            Toast.makeText(getActivity(), getResources().getString(R.string.data_synced), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void showError(String errMsg) {
        Common_Utils.responseCodePromp(getActivity().findViewById(android.R.id.content), errMsg);
    }
}
