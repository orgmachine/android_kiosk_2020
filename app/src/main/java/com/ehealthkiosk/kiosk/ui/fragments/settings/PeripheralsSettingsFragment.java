package com.ehealthkiosk.kiosk.ui.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatSpinner;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.ui.fragments.BaseFragment;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Min;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PeripheralsSettingsFragment extends BaseFragment implements Validator.ValidationListener {

    @BindView(R.id.spinner_checkup)
    AppCompatSpinner spinnerCheckup;
    @BindView(R.id.spinner_device)
    AppCompatSpinner spinnerDevice;
    @BindView(R.id.spinner_units)
    AppCompatSpinner spinnerUnits;

    ArrayList<String> checkupArray = new ArrayList<String>();
    ArrayList<String> deviceArray = new ArrayList<String>();
    ArrayList<String> unitsArray = new ArrayList<String>();

    @Min(value = 180, message = "Reference height should be greater than 180 cm")
    @NotEmpty(message = "Please enter valid reference height")
    @BindView(R.id.et_reference_height)
    EditText etReferenceHeight;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_reset_bluetooth)
    Button btnResetBluetooth;
    Unbinder unbinder;
    Validator mValidator;

    @Override
    protected int getLayout() {
        return R.layout.fragment_settings_peripherals;
    }

    private void init() {

        checkupArray.add("Checkup 1");
        checkupArray.add("Checkup 2");
        checkupArray.add("Checkup 3");

        deviceArray.add("Device 1");
        deviceArray.add("Device 2");
        deviceArray.add("Device 3");

        unitsArray.add("Unit 1");
        unitsArray.add("Unit 2");
        unitsArray.add("Unit 3");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, checkupArray);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, deviceArray);
        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, unitsArray);

        // Drop down layout style - list view with radio button
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // attaching data adapter to spinner
        spinnerCheckup.setAdapter(dataAdapter1);
        spinnerDevice.setAdapter(dataAdapter2);
        spinnerUnits.setAdapter(dataAdapter3);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        etReferenceHeight.setText(""+SharedPrefUtils.getReferenceHeight(HealthKioskApp.getAppContext()));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_save, R.id.btn_reset_bluetooth})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                String height = etReferenceHeight.getText().toString();
                SharedPrefUtils.setReferenceHeight(HealthKioskApp.getAppContext(),Integer.parseInt(height));
                Common_Utils.showToast("Settings saved successfully");
                break;
            case R.id.btn_reset_bluetooth:
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Common_Utils.showError(getActivity(), errors);
    }
}
