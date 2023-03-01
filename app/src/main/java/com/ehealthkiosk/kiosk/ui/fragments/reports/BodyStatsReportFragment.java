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
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BodyStatsReportFragment extends BaseFragment {

    @BindView(R.id.tv_bmi_value)
    TextView tvBmiValue;
    @BindView(R.id.tv_hydration_value)
    TextView tvHydrationValue;
    @BindView(R.id.tv_fat_value)
    TextView tvFatValue;
    @BindView(R.id.tv_bonemass_value)
    TextView tvBonemassValue;
    @BindView(R.id.tv_muscle_value)
    TextView tvMuscleValue;
    @BindView(R.id.tv_bmi_type)
    TextView tvBmiType;
    @BindView(R.id.tv_hydration_type)
    TextView tvHydrationType;
    @BindView(R.id.tv_fat_type)
    TextView tvFatType;
    @BindView(R.id.tv_bonemass_type)
    TextView tvBonemassType;
    @BindView(R.id.tv_muscle_type)
    TextView tvMuscleType;
    @BindView(R.id.img_bmi)
    ImageButton imgBmi;
    @BindView(R.id.img_hydration)
    ImageButton imgHydration;
    @BindView(R.id.img_fat)
    ImageButton imgFat;
    @BindView(R.id.img_bonemass)
    ImageButton imgBonemass;
    @BindView(R.id.img_muscle)
    ImageButton imgMuscle;

    final static double KG_TO_LBS = 2.20462;
    final static double M_TO_IN = 39.3701;

    @BindView(R.id.tv_bmi)
    TextView tvBmi;
    @BindView(R.id.tv_hydration)
    TextView tvHydration;
    @BindView(R.id.tv_fat)
    TextView tvFat;
    @BindView(R.id.tv_bonemass)
    TextView tvBonemass;
    @BindView(R.id.tv_muscle)
    TextView tvMuscle;
    @BindView(R.id.scrollViewMain)
    ScrollView scrollViewMain;
    Unbinder unbinder;

    GenerateReportData generateReportData;

    private static DecimalFormat TWO_DECIMAL_PLACES = new DecimalFormat(".##");
    @BindView(R.id.tv_height)
    TextView tvHeight;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.tv_height_value)
    TextView tvHeightValue;
    @BindView(R.id.tv_weight_value)
    TextView tvWeightValue;
    @BindView(R.id.tv_height_type)
    TextView tvHeightType;
    @BindView(R.id.tv_weight_type)
    TextView tvWeightType;
    @BindView(R.id.img_height)
    ImageButton imgHeight;
    @BindView(R.id.img_weight)
    ImageButton imgWeight;

    @Override
    protected int getLayout() {
        return R.layout.fragment_report_bodystats;
    }

    private void init() {

        generateReportData = getArguments().getParcelable(Constants.REPORT_DATA);

        GradientDrawable shape1 = new GradientDrawable();
        shape1.setCornerRadius(100);

        GradientDrawable shape2 = new GradientDrawable();
        shape2.setCornerRadius(100);

        GradientDrawable shape3 = new GradientDrawable();
        shape3.setCornerRadius(100);

        GradientDrawable shape4 = new GradientDrawable();
        shape4.setCornerRadius(100);

        GradientDrawable shape5 = new GradientDrawable();
        shape5.setCornerRadius(100);

        String heightFt = Common_Utils.centimeterToFeet(String.valueOf(generateReportData.getHeight()));
        String heightInch = Common_Utils.centimeterToInch(String.valueOf(generateReportData.getHeight()));

        //String heightFt = SharedPrefUtils.getHeightFT(getActivity());
        //String heightInch = SharedPrefUtils.getHeightInch(getActivity());


        String weight = String.valueOf(generateReportData.getWeight());//SharedPrefUtils.getWeight(getActivity());

        if (Common_Utils.isNotNullOrEmpty(weight)) {
            tvWeightValue.setText(weight + " Kg");
        }else{
            tvWeightValue.setText("-");
        }

        if (Common_Utils.isNotNullOrEmpty(heightFt) && Common_Utils.isNotNullOrEmpty(heightInch)) {

            tvHeightValue.setText(heightFt + " Feet " + heightInch + " inch");
        }else{
            tvHeightValue.setText("-");
        }


        if (Common_Utils.isNotNullOrEmpty(generateReportData.getReport().getBmi().getInference())) {
            shape1.setColor(Color.parseColor(generateReportData.getReport().getBmi().getColor()));
            tvBmiType.setText(generateReportData.getReport().getBmi().getInference());

            tvBmiType.setBackground(shape1);
            tvBmiValue.setText(TWO_DECIMAL_PLACES.format(generateReportData.getReport().getBmi().getValue()) + " Kg/m2");

        } else {
            tvBmiValue.setText("-");
        }

        if (Common_Utils.isNotNullOrEmpty(generateReportData.getReport().getHydration().getInference())) {
            shape2.setColor(Color.parseColor(generateReportData.getReport().getHydration().getColor()));
            tvHydrationType.setText(generateReportData.getReport().getHydration().getInference());

            tvHydrationType.setBackground(shape2);
            tvHydrationValue.setText(TWO_DECIMAL_PLACES.format(generateReportData.getReport().getHydration().getValue()) + " %");
        } else {
            tvHydrationValue.setText("-");
        }

        if (Common_Utils.isNotNullOrEmpty(generateReportData.getReport().getFat().getInference())) {
            shape3.setColor(Color.parseColor(generateReportData.getReport().getFat().getColor()));
            tvFatType.setText(generateReportData.getReport().getFat().getInference());

            tvFatType.setBackground(shape3);
            tvFatValue.setText(TWO_DECIMAL_PLACES.format(generateReportData.getReport().getFat().getValue()) + " %");
        } else {
            tvFatValue.setText("-");
        }

        if (Common_Utils.isNotNullOrEmpty(generateReportData.getReport().getBonemass().getInference())) {
            shape4.setColor(Color.parseColor(generateReportData.getReport().getBonemass().getColor()));
            tvBonemassType.setText(generateReportData.getReport().getBonemass().getInference());

            tvBonemassType.setBackground(shape4);
            tvBonemassValue.setText(TWO_DECIMAL_PLACES.format(generateReportData.getReport().getBonemass().getValue()) + " %");
        } else {
            tvBonemassValue.setText("-");
        }

        if (Common_Utils.isNotNullOrEmpty(generateReportData.getReport().getMuscle().getInference())) {
            shape5.setColor(Color.parseColor(generateReportData.getReport().getMuscle().getColor()));
            tvMuscleType.setText(generateReportData.getReport().getMuscle().getInference());

            tvMuscleType.setBackground(shape5);
            tvMuscleValue.setText(TWO_DECIMAL_PLACES.format(generateReportData.getReport().getMuscle().getValue()) + " %");
        } else {
            tvMuscleValue.setText("-");
        }

        showHideAutomaticGeneratedReport(!Common_Utils.isNotNullOrEmpty(generateReportData.getReport().getHydration().getInference()));
    }

    void showHideAutomaticGeneratedReport(boolean isGone) {
        if (isGone) {
            tvHydration.setVisibility(View.GONE);
            tvFat.setVisibility(View.GONE);
            tvBonemass.setVisibility(View.GONE);
            tvMuscle.setVisibility(View.GONE);

            tvHydrationValue.setVisibility(View.GONE);
            tvFatValue.setVisibility(View.GONE);
            tvBonemassValue.setVisibility(View.GONE);
            tvMuscleValue.setVisibility(View.GONE);

            tvHydrationType.setVisibility(View.GONE);
            tvFatType.setVisibility(View.GONE);
            tvBonemassType.setVisibility(View.GONE);
            tvMuscleType.setVisibility(View.GONE);

            imgHydration.setVisibility(View.GONE);
            imgFat.setVisibility(View.GONE);
            imgBonemass.setVisibility(View.GONE);
            imgMuscle.setVisibility(View.GONE);
        } else {
            tvHydration.setVisibility(View.VISIBLE);
            tvFat.setVisibility(View.VISIBLE);
            tvBonemass.setVisibility(View.VISIBLE);
            tvMuscle.setVisibility(View.VISIBLE);

            tvHydrationValue.setVisibility(View.VISIBLE);
            tvFatValue.setVisibility(View.VISIBLE);
            tvBonemassValue.setVisibility(View.VISIBLE);
            tvMuscleValue.setVisibility(View.VISIBLE);

            tvHydrationType.setVisibility(View.VISIBLE);
            tvFatType.setVisibility(View.VISIBLE);
            tvBonemassType.setVisibility(View.VISIBLE);
            tvMuscleType.setVisibility(View.VISIBLE);

            imgHydration.setVisibility(View.VISIBLE);
            imgFat.setVisibility(View.VISIBLE);
            imgBonemass.setVisibility(View.VISIBLE);
            imgMuscle.setVisibility(View.VISIBLE);
        }
    }

    public static double metricFormula(double m, double kg) {
        double result = 0;

        result = kg / (Math.pow(m, 2));

        return result;
    }


    public static double imperialFormula(double m, double kg) {
        double result = 0;
        // convert kg to lbs and m to in
        double lbs = Math.round(kg * KG_TO_LBS);
        double in = Math.round((m * M_TO_IN) * 100);

        result = (lbs / (Math.pow((in / 100), 2))) * 703;

        return result;
    }

    // THE BMI CATEGORIES
    public static String getCategory(double result) {
        String category;
        if (result < 18) {
            category = "Underweight";
        } else if (result >= 18 && result <= 27) {
            category = "Normal";
        } else if (result > 27 && result <= 30) {
            category = "Overweight";
        } else {
            category = "Obese";
        }
        return category;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @OnClick({R.id.img_bmi, R.id.img_hydration, R.id.img_fat, R.id.img_bonemass, R.id.img_muscle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_bmi:
                break;
            case R.id.img_hydration:
                break;
            case R.id.img_fat:
                break;
            case R.id.img_bonemass:
                break;
            case R.id.img_muscle:
                break;
        }
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
}
