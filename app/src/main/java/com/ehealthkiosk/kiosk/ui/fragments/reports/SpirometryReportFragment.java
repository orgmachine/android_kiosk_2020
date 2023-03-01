package com.ehealthkiosk.kiosk.ui.fragments.reports;

import android.os.Bundle;
import android.view.View;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.generatereport.GenerateReportData;
import com.ehealthkiosk.kiosk.ui.fragments.BaseFragment;
import com.ehealthkiosk.kiosk.utils.Constants;

public class SpirometryReportFragment extends BaseFragment {

    GenerateReportData generateReportData;
    @Override
    protected int getLayout() {
        return R.layout.fragment_report_spirometry;
    }

    private void init() {
        generateReportData = getArguments().getParcelable(Constants.REPORT_DATA);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }


}
