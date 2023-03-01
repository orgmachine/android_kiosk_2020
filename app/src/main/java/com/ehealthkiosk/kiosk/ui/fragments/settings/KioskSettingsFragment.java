package com.ehealthkiosk.kiosk.ui.fragments.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.ui.fragments.BaseFragment;

import java.util.ArrayList;

import butterknife.BindView;

public class KioskSettingsFragment extends BaseFragment {


    @BindView(R.id.spinner_language)
    AppCompatSpinner spinnerLanguage;

    ArrayList<String> languageArray = new ArrayList<String>();



    @Override
    protected int getLayout() {
        return R.layout.fragment_settings_kiosk;
    }

    private void init() {
        languageArray.add("Language 1");
        languageArray.add("Language 2");
        languageArray.add("Language 3");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, languageArray);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // attaching data adapter to spinner
        spinnerLanguage.setAdapter(dataAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

}
