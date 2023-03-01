package com.ehealthkiosk.kiosk.ui.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestInterface;
import com.ehealthkiosk.kiosk.model.SetServerCheckPojo;
import com.ehealthkiosk.kiosk.model.commonresponse.Status;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.google.android.material.button.MaterialButton;

import cn.com.bodivis.scalelib.BuildConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DiagnosisFragment extends Fragment {

    TextView appVersion, deviceConfig, serverCheck;
    MaterialButton printerCheck;
    ToggleButton bluetoothToggle, wifiToggle;
    BluetoothAdapter mBluetoothAdapter;
    NetworkInfo mWifi;
    WifiManager wifiManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diagnosis, container, false);

        appVersion = view.findViewById(R.id.app_version_value);
        wifiToggle = view.findViewById(R.id.wifi_toggle);
        serverCheck = view.findViewById(R.id.text_server_check);
        bluetoothToggle = view.findViewById(R.id.bluetooth_toggle);
        deviceConfig = view.findViewById(R.id.text_config_check);
        printerCheck = view.findViewById(R.id.btn_printer_check);


//        Common_Utils.callPrintAPI(Constants.BASIC_PRINT_CHECK_URL, getActivity());

        Integer versioncode = BuildConfig.VERSION_CODE;
        appVersion.setText(versioncode.toString());

        checkWifi();
        checkServer();

        if (checkDeviceConfig() == true) {
            deviceConfig.setText("CONFIGURED");
        } else {
            deviceConfig.setText("NOT CONFIGURED");
        }


        bluetoothCheck();

        bluetoothToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    bluetoothToggle.setHighlightColor(getResources().getColor(R.color.colorPrimary));
                    mBluetoothAdapter.enable();
                } else {
                    mBluetoothAdapter.disable();
                }
            }
        });


        wifiToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    wifiManager.setWifiEnabled(true);
                    wifiToggle.setHighlightColor(getResources().getColor(R.color.colorPrimary));
                    wifiToggle.setText("ON");
                } else {
                    wifiManager.disconnect();
                    wifiManager.setWifiEnabled(false);
                    wifiToggle.setHighlightColor(getResources().getColor(R.color.colorPrimary));
                    wifiToggle.setText("OFF");
                }
            }
        });


        printerCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common_Utils.callPrintAPI(Constants.PRINT_STATUS_CHECK_URL, getActivity());
            }
        });


        return view;

    }

    private void checkWifi() {
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        wifiToggle.setChecked(true);
        wifiToggle.setText("ON");
        wifiToggle.setHighlightColor(getResources().getColor(R.color.colorPrimary));
    }

    private void bluetoothCheck() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else if (!mBluetoothAdapter.isEnabled()) {
            bluetoothToggle.setText("OFF");
        } else {
            bluetoothToggle.setChecked(true);
            bluetoothToggle.setText("ON");
            bluetoothToggle.setHighlightColor(getResources().getColor(R.color.colorPrimary));

        }
    }

    private void checkServer() {
        Common_Utils.showProgress(getActivity());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestInterface service = retrofit.create(RestInterface.class);

        SetServerCheckPojo setServerCheckPojo = new SetServerCheckPojo();
        setServerCheckPojo.setAppVersion(1);
        Call<Status> call = service.getServerStatus(setServerCheckPojo);
        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                serverCheck.setText("SERVER AVAILABLE");
                Common_Utils.hideProgress();
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                serverCheck.setText("SERVER UNAVAILABLE");
                Common_Utils.hideProgress();
            }
        });
    }

    private boolean checkDeviceConfig() {
        if (DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.HEIGHT) != "" &&
                DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.WEIGHT) != "" &&
                DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.GLUECOSE) != "" &&
                DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.PULSE) != "" &&
                DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.TEMPERATURE) != "" &&
                DeviceIdPrefHelper.getDefaultHeight(getActivity(), Constants.DEFAULT_HEIGHT) != "" &&
                DeviceIdPrefHelper.getDeviceAddress(getActivity(), Constants.BP) != "" &&
                DeviceIdPrefHelper.getPrintUrl(getActivity(), Constants.PRINT_URL) != "") {

            return true;
        } else {
            return false;
        }
    }


}
