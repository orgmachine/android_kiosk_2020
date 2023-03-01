package com.ehealthkiosk.kiosk.ui.fragments.reports;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.generatereport.GenerateReportData;
import com.ehealthkiosk.kiosk.ui.fragments.BaseFragment;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PulseReportFragment extends BaseFragment {

    @BindView(R.id.tv_pulse_value)
    TextView tvPulseValue;
    @BindView(R.id.tv_pulse_type)
    TextView tvPulseType;
    @BindView(R.id.img_pulse)
    ImageButton imgPulse;
    @BindView(R.id.scrollViewMain)
    ScrollView scrollViewMain;
    Unbinder unbinder;
    @BindView(R.id.tv_spo2_value)
    TextView tvSpo2Value;
    @BindView(R.id.tv_spo2_type)
    TextView tvSpo2Type;
    @BindView(R.id.img_sp02)
    ImageButton imgSp02;
    Unbinder unbinder1;

    GenerateReportData generateReportData;

    @Override
    protected int getLayout() {
        return R.layout.fragment_report_pulse;
    }

    private void init() {

        generateReportData = getArguments().getParcelable(Constants.REPORT_DATA);

        GradientDrawable shape1 = new GradientDrawable();
        shape1.setCornerRadius(100);

        GradientDrawable shape2 = new GradientDrawable();
        shape2.setCornerRadius(100);


        if (Common_Utils.isNotNullOrEmpty(generateReportData.getReport().getPulse().getInference())) {
            shape1.setColor(Color.parseColor(generateReportData.getReport().getPulse().getColor()));
            tvPulseType.setText(generateReportData.getReport().getPulse().getInference());

            tvPulseType.setBackground(shape1);
            tvPulseValue.setText(generateReportData.getReport().getPulse().getValue() + " bpm");
        } else {
            tvPulseValue.setText("-");
        }

        if (Common_Utils.isNotNullOrEmpty(generateReportData.getReport().getOxygen_sat().getInference())) {
            shape2.setColor(Color.parseColor(generateReportData.getReport().getOxygen_sat().getColor()));
            tvSpo2Type.setText(generateReportData.getReport().getOxygen_sat().getInference());

            tvSpo2Type.setBackground(shape2);
            tvSpo2Value.setText(generateReportData.getReport().getOxygen_sat().getValue() + " %");
        } else {
            tvSpo2Value.setText("-");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }


    @OnClick(R.id.img_pulse)
    public void onViewClicked() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }
}
