package com.ehealthkiosk.kiosk.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;


public class KioskDeviceFragment extends Fragment implements SettingsView {

    TextInputEditText kioskIdInput, issuedInpText, expiryInpText;
    MaterialButton saveIdButton, logoutButton;
    private SettingsPresenter settingsPresenter;
    public String macAddress;
    TextView issuedBy, expiryDate;

    void openPassworDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_settings_password, viewGroup, false);
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
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adminpassword.getText().toString().equals(DeviceIdPrefHelper.getAdminPassword(getActivity(), Constants.ADMIN_PASSWORD))) {
                    logout();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Password Invalid!/nPlease enter a valid password", Toast.LENGTH_LONG).show();
                }
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kiosk_device, container, false);

        kioskIdInput = view.findViewById(R.id.kiosk_id_inp_text);
        saveIdButton = view.findViewById(R.id.btn_login);
        issuedBy = view.findViewById(R.id.issued_date_inp);
        expiryDate = view.findViewById(R.id.expiry_date_inp);
        logoutButton = view.findViewById(R.id.btn_logout);


        settingsPresenter = new SettingsPresenterImpl(this);

        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        macAddress = wInfo.getMacAddress();
        Log.d("MAC", macAddress);






        if (DeviceIdPrefHelper.getkioskId(getActivity(), Constants.KIOSK_ID) != null &&
                !DeviceIdPrefHelper.getkioskId(getActivity(), Constants.KIOSK_ID).equals("") &&
                DeviceIdPrefHelper.getkioskId(getActivity(), Constants.ISSUED_BY) != null &&
                !DeviceIdPrefHelper.getkioskId(getActivity(), Constants.ISSUED_BY).equals("") &&
                DeviceIdPrefHelper.getkioskId(getActivity(), Constants.EXPIRY_DATE) != null &&
                !DeviceIdPrefHelper.getkioskId(getActivity(), Constants.EXPIRY_DATE).equals("")) {

            kioskIdInput.setEnabled(false);
            String kioskDeviceId = DeviceIdPrefHelper.getkioskId(getActivity(), Constants.KIOSK_ID);
            kioskIdInput.setText(kioskDeviceId);
            issuedBy.setText(DeviceIdPrefHelper.getIssuedBy(getActivity(), Constants.ISSUED_BY));
            expiryDate.setText(DeviceIdPrefHelper.getExpiry(getActivity(), Constants.EXPIRY_DATE));
            Constants.KIOSK_ID_PRESENT = false;
            saveIdButton.setVisibility(View.INVISIBLE);
            logoutButton.setVisibility(View.VISIBLE);

        } else {
            kioskIdInput.setEnabled(true);
            kioskIdInput.setText("");
            saveIdButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.INVISIBLE);

        }

        saveIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceIdPrefHelper.setkioskId(getActivity(), Constants.KIOSK_ID, kioskIdInput.getText().toString());
                kioskIdInput.setText(DeviceIdPrefHelper.getkioskId(getActivity(), Constants.KIOSK_ID));
                Constants.KIOSK_ID_PRESENT = true;
                KioskIdData kioskIdData = new KioskIdData();
                kioskIdData.setMacAddress("yolo_abcd");
                settingsPresenter.settings(kioskIdData);


            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPassworDialog();
            }
        });


        return view;

    }

    public void logout(){
        SharedPrefUtils.clearAll(getActivity());
        DeviceIdPrefHelper.clearAll(getActivity());
        Intent i = new Intent(getActivity(), WelcomeActivity.class);
        startActivity(i);
        getActivity().finish();
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

            kioskIdInput.setText(DeviceIdPrefHelper.getkioskId(getActivity(), Constants.KIOSK_ID));
            issuedBy.setText(DeviceIdPrefHelper.getIssuedBy(getActivity(), Constants.ISSUED_BY));
            expiryDate.setText(DeviceIdPrefHelper.getExpiry(getActivity(), Constants.EXPIRY_DATE));
            Toast.makeText(getActivity(), getResources().getString(R.string.kiosk_registered), Toast.LENGTH_LONG).show();


            Intent i = new Intent(getActivity(), WelcomeActivity.class);
            startActivity(i);
        }

    }

    @Override
    public void showError(String errMsg) {
        Common_Utils.responseCodePromp(getActivity().findViewById(android.R.id.content), errMsg);

    }
}
