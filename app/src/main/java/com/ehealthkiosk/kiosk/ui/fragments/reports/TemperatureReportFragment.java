package com.ehealthkiosk.kiosk.ui.fragments.reports;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.generatereport.GenerateReportData;
import com.ehealthkiosk.kiosk.ui.fragments.BaseFragment;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TemperatureReportFragment extends BaseFragment {

    @BindView(R.id.tv_temp_value)
    TextView tvTempValue;
    @BindView(R.id.tv_temp_type)
    TextView tvTempType;
    @BindView(R.id.img_temp)
    ImageButton imgTemp;
    Unbinder unbinder;

    GenerateReportData generateReportData;

    @Override
    protected int getLayout() {
        return R.layout.fragment_report_temperature;
    }

    private void init() {
        generateReportData = getArguments().getParcelable(Constants.REPORT_DATA);

        GradientDrawable shape1 = new GradientDrawable();
        shape1.setCornerRadius(100);


        if (Common_Utils.isNotNullOrEmpty(generateReportData.getReport().getTemperature().getInference())) {

            shape1.setColor(Color.parseColor(generateReportData.getReport().getTemperature().getColor()));
            tvTempType.setText(generateReportData.getReport().getTemperature().getInference());

            tvTempType.setBackground(shape1);
            tvTempValue.setText(generateReportData.getReport().getTemperature().getValue() + " °F");
        } else {
            tvTempValue.setText("-");
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }


    @OnClick(R.id.img_temp)
    public void onViewClicked() {
    }
}
