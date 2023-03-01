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

public class BloodPressureReportFragment extends BaseFragment {

    @BindView(R.id.tv_systolic_value)
    TextView tvSystolicValue;
    @BindView(R.id.tv_diastolic_value)
    TextView tvDiastolicValue;
    @BindView(R.id.tv_diastolic_typ)
    TextView tvDiastolicTyp;
    @BindView(R.id.tv_systolic_type)
    TextView tvSystolicType;
    @BindView(R.id.img_systolic)
    ImageButton imgSystolic;
    @BindView(R.id.img_diastolic)
    ImageButton imgDiastolic;
    Unbinder unbinder;

    GenerateReportData generateReportData;

    @Override
    protected int getLayout() {
        return R.layout.fragment_report_bloodpressure;
    }

    private void init() {

        generateReportData = getArguments().getParcelable(Constants.REPORT_DATA);

        GradientDrawable shape1 =  new GradientDrawable();
        shape1.setCornerRadius( 100 );

        GradientDrawable shape2 =  new GradientDrawable();
        shape2.setCornerRadius( 100 );


        if (Common_Utils.isNotNullOrEmpty(generateReportData.getReport().getSystolic().getInference())){
            shape1.setColor(Color.parseColor(generateReportData.getReport().getSystolic().getColor()));
            tvSystolicType.setText(generateReportData.getReport().getSystolic().getInference());
            tvSystolicType.setBackground(shape1);
            tvSystolicValue.setText(generateReportData.getReport().getSystolic().getValue() + " mmhg");
        }else{
            tvSystolicValue.setText("-");
        }

        if (Common_Utils.isNotNullOrEmpty(generateReportData.getReport().getDiastolic().getInference())){

            shape2.setColor(Color.parseColor(generateReportData.getReport().getDiastolic().getColor()));
            tvDiastolicTyp.setText(generateReportData.getReport().getDiastolic().getInference());
            tvDiastolicTyp.setBackground(shape2);
            tvDiastolicValue.setText(generateReportData.getReport().getDiastolic().getValue() + " mmhg");
        }else{
            tvDiastolicValue.setText("-");
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }


    @OnClick({R.id.img_systolic, R.id.img_diastolic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_systolic:
                break;
            case R.id.img_diastolic:
                break;
        }
    }
}
