package com.ehealthkiosk.kiosk.ui.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.ehealthkiosk.kiosk.BuildConfig;
import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.profilelist.ProfilesItem;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.ehealthkiosk.kiosk.utils.VersionUtils;
import com.ehealthkiosk.kiosk.widgets.SquareLinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ehealthkiosk.kiosk.HealthKioskApp.getAppContext;

public class OfflineBasicReportActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.top_bar)
    Toolbar toolbar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.btn_print)
    Button btnPrint;
    @BindView(R.id.card_view)
    CardView cardView;
    @BindView(R.id.tv_ft)
    TextView tvFt;
    @BindView(R.id.tv_inch)
    TextView tvInch;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.tv_bmi_value)
    TextView tvBmiValue;
    @BindView(R.id.tv_bmi_type)
    TextView tvBmiType;
    @BindView(R.id.ll_bmi)
    LinearLayout llBmi;
    @BindView(R.id.ll_bmi_border)
    SquareLinearLayout llBmiBorder;
    @BindView(R.id.tv_systolic_value)
    TextView tvSystolicValue;
    @BindView(R.id.tv_systolic_type)
    TextView tvSystolicType;
    @BindView(R.id.ll_systolic)
    LinearLayout llSystolic;
    @BindView(R.id.ll_systolic_border)
    SquareLinearLayout llSystolicBorder;
    @BindView(R.id.tv_fat_value)
    TextView tvFatValue;
    @BindView(R.id.tv_fat_type)
    TextView tvFatType;
    @BindView(R.id.ll_fat)
    LinearLayout llFat;
    @BindView(R.id.ll_fat_border)
    SquareLinearLayout llFatBorder;
    @BindView(R.id.tv_diastolic_value)
    TextView tvDiastolicValue;
    @BindView(R.id.tv_diastolic_type)
    TextView tvDiastolicType;
    @BindView(R.id.ll_diastolic)
    LinearLayout llDiastolic;
    @BindView(R.id.ll_diastolic_border)
    SquareLinearLayout llDiastolicBorder;
    @BindView(R.id.tv_bonemass_value)
    TextView tvBonemassValue;
    @BindView(R.id.tv_bonemass_type)
    TextView tvBonemassType;
    @BindView(R.id.ll_bonemass)
    LinearLayout llBonemass;
    @BindView(R.id.ll_bonemass_border)
    SquareLinearLayout llBonemassBorder;
    @BindView(R.id.tv_temp_value)
    TextView tvTempValue;
    @BindView(R.id.tv_temp_type)
    TextView tvTempType;
    @BindView(R.id.ll_temperature)
    LinearLayout llTemperature;
    @BindView(R.id.ll_temperature_border)
    SquareLinearLayout llTemperatureBorder;
    @BindView(R.id.tv_muscle_value)
    TextView tvMuscleValue;
    @BindView(R.id.tv_muscle_type)
    TextView tvMuscleType;
    @BindView(R.id.ll_muscle)
    LinearLayout llMuscle;
    @BindView(R.id.ll_muscle_border)
    SquareLinearLayout llMuscleBorder;
    @BindView(R.id.tv_pulse_value)
    TextView tvPulseValue;
    @BindView(R.id.tv_pulse_type)
    TextView tvPulseType;
    @BindView(R.id.ll_pulse)
    LinearLayout llPulse;
    @BindView(R.id.ll_pulse_border)
    SquareLinearLayout llPulseBorder;
    @BindView(R.id.tv_hydration_value)
    TextView tvHydrationValue;
    @BindView(R.id.tv_hydration_type)
    TextView tvHydrationType;
    @BindView(R.id.ll_hydration)
    LinearLayout llHydration;
    @BindView(R.id.ll_hydration_border)
    SquareLinearLayout llHydrationBorder;
    @BindView(R.id.tv_oxygensat_value)
    TextView tvOxygensatValue;
    @BindView(R.id.tv_oxygensat_type)
    TextView tvOxygensatType;
    @BindView(R.id.ll_oxygensat)
    LinearLayout llOxygensat;
    @BindView(R.id.ll_oxygensat_border)
    SquareLinearLayout llOxygensatBorder;

    private static DecimalFormat TWO_DECIMAL_PLACES = new DecimalFormat(".##");

    ProfilesItem profilesItem;
    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_basic_report);
        ButterKnife.bind(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarTitle.setText("Reports");

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);

        profilesItem = SharedPrefUtils.getProfile(HealthKioskApp.getAppContext());

        tvName.setText(profilesItem.getName());
        tvGender.setText(Common_Utils.toCamelCase(profilesItem.getGender()));
        tvAge.setText("" + profilesItem.getAge());

        tvDate.setText(formattedDate);

        String bmi = SharedPrefUtils.getBMI(this);
        String hydration = SharedPrefUtils.getHydration(this);
        String fat = SharedPrefUtils.getFAT(this);
        String bonemss = SharedPrefUtils.getBonemass(this);
        String muscle = SharedPrefUtils.getMuscle(this);
        String systolic = SharedPrefUtils.getSystolic(this);
        String diastolic = SharedPrefUtils.getDiastolic(this);
        String temperature = SharedPrefUtils.getTemp(this);
        String pulse = SharedPrefUtils.getPulse(this);
        String oxygensat = SharedPrefUtils.getSp02(this);
        String heightFt = SharedPrefUtils.getHeightFT(this);
        String heightInch = SharedPrefUtils.getHeightInch(this);


        String weight = SharedPrefUtils.getWeight(this);

        if (!Common_Utils.isNotNullOrEmpty(bmi)) {
            double weightKg = Double.parseDouble(weight);
            double meters = ((Double.parseDouble(heightFt) * 12) + Double.parseDouble(heightInch)) * 0.0254;


            double metricFormula = Common_Utils.metricFormula(meters, weightKg);
            bmi = TWO_DECIMAL_PLACES.format(metricFormula);
        }

        if (Common_Utils.isNotNullOrEmpty(bmi)) {
            tvBmiValue.setText(TWO_DECIMAL_PLACES.format(Double.parseDouble(bmi)));
            tvBmiType.setText(getBMICategory(Double.parseDouble(bmi)));
            if (getBMICategory(Double.parseDouble(bmi)).equalsIgnoreCase("Obese")) {
                GradientDrawable gd = (GradientDrawable) llBmiBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llBmi.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            } else if (getBMICategory(Double.parseDouble(bmi)).equalsIgnoreCase("Overweight")) {
                GradientDrawable gd = (GradientDrawable) llBmiBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.yellow_500));
                llBmi.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow_500));
            } else if (getBMICategory(Double.parseDouble(bmi)).equalsIgnoreCase("Normal")) {
                GradientDrawable gd = (GradientDrawable) llBmiBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.colorAccent));
                llBmi.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            } else if (getBMICategory(Double.parseDouble(bmi)).equalsIgnoreCase("Underweight")) {
                GradientDrawable gd = (GradientDrawable) llBmiBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llBmi.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            }

        } else {
            tvBmiValue.setText("-");
            tvBmiType.setText("");
            GradientDrawable gd = (GradientDrawable) llBmiBorder.getBackground().getCurrent();
            gd.setStroke(2, ContextCompat.getColor(this, R.color.grey_500));
            llBmi.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_500));
        }

        if (Common_Utils.isNotNullOrEmpty(fat)) {
            tvFatValue.setText(TWO_DECIMAL_PLACES.format(Double.parseDouble(fat)));
            tvFatType.setText(getFATCategory(Double.parseDouble(fat)));
            if (getFATCategory(Double.parseDouble(fat)).equalsIgnoreCase("Obese")) {
                GradientDrawable gd = (GradientDrawable) llFatBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llFat.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            } else if (getFATCategory(Double.parseDouble(fat)).equalsIgnoreCase("Overweight")) {
                GradientDrawable gd = (GradientDrawable) llFatBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.yellow_500));
                llFat.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow_500));
            } else if (getFATCategory(Double.parseDouble(fat)).equalsIgnoreCase("Acceptable")) {
                GradientDrawable gd = (GradientDrawable) llFatBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.yellow_500));
                llFat.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow_500));
            } else if (getFATCategory(Double.parseDouble(fat)).equalsIgnoreCase("Normal")) {
                GradientDrawable gd = (GradientDrawable) llFatBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.colorAccent));
                llFat.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            } else if (getFATCategory(Double.parseDouble(fat)).equalsIgnoreCase("Athletic")) {
                GradientDrawable gd = (GradientDrawable) llFatBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.blue_grey_500));
                llFat.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_grey_500));
            }

        } else {
            tvFatValue.setText("-");
            tvFatType.setText("");
            GradientDrawable gd = (GradientDrawable) llFatBorder.getBackground().getCurrent();
            gd.setStroke(2, ContextCompat.getColor(this, R.color.grey_500));
            llFat.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_500));
        }

        if (Common_Utils.isNotNullOrEmpty(hydration)) {
            tvHydrationValue.setText(TWO_DECIMAL_PLACES.format(Double.parseDouble(hydration)));
            tvHydrationType.setText(getHydrationCategory(Double.parseDouble(hydration)));
            if (getHydrationCategory(Double.parseDouble(hydration)).equalsIgnoreCase("Low")) {
                GradientDrawable gd = (GradientDrawable) llHydrationBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llHydration.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            } else if (getHydrationCategory(Double.parseDouble(hydration)).equalsIgnoreCase("Normal")) {
                GradientDrawable gd = (GradientDrawable) llHydrationBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.colorAccent));
                llHydration.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            } else if (getHydrationCategory(Double.parseDouble(hydration)).equalsIgnoreCase("Good")) {
                GradientDrawable gd = (GradientDrawable) llHydrationBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.blue_grey_500));
                llHydration.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_grey_500));
            }

        } else {
            tvHydrationType.setText("");
            tvHydrationValue.setText("-");
            GradientDrawable gd = (GradientDrawable) llHydrationBorder.getBackground().getCurrent();
            gd.setStroke(2, ContextCompat.getColor(this, R.color.grey_500));
            llHydration.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_500));
        }

        if (Common_Utils.isNotNullOrEmpty(muscle)) {
            tvMuscleValue.setText(TWO_DECIMAL_PLACES.format(Double.parseDouble(muscle)));
            tvMuscleType.setText(getMuscleCategory(Double.parseDouble(muscle)));
            if (getMuscleCategory(Double.parseDouble(muscle)).equalsIgnoreCase("Low")) {
                GradientDrawable gd = (GradientDrawable) llMuscleBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llMuscle.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            } else if (getMuscleCategory(Double.parseDouble(muscle)).equalsIgnoreCase("Normal")) {
                GradientDrawable gd = (GradientDrawable) llMuscleBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.colorAccent));
                llMuscle.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            } else if (getMuscleCategory(Double.parseDouble(muscle)).equalsIgnoreCase("Good")) {
                GradientDrawable gd = (GradientDrawable) llMuscleBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.blue_grey_500));
                llMuscle.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_grey_500));
            }

        } else {
            tvMuscleType.setText("");
            tvMuscleValue.setText("-");
            GradientDrawable gd = (GradientDrawable) llMuscleBorder.getBackground().getCurrent();
            gd.setStroke(2, ContextCompat.getColor(this, R.color.grey_500));
            llMuscle.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_500));
        }

        if (Common_Utils.isNotNullOrEmpty(bonemss)) {
            tvBonemassValue.setText(TWO_DECIMAL_PLACES.format(Double.parseDouble(bonemss)));
            tvBonemassType.setText(getBoneMassCategory(Double.parseDouble(bonemss)));
            if (getBoneMassCategory(Double.parseDouble(bonemss)).equalsIgnoreCase("Low")) {
                GradientDrawable gd = (GradientDrawable) llBonemassBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llBonemass.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            } else if (getBoneMassCategory(Double.parseDouble(bonemss)).equalsIgnoreCase("Normal")) {
                GradientDrawable gd = (GradientDrawable) llBonemassBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.colorAccent));
                llBonemass.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            } else if (getBoneMassCategory(Double.parseDouble(bonemss)).equalsIgnoreCase("Good")) {
                GradientDrawable gd = (GradientDrawable) llBonemassBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.blue_grey_500));
                llBonemass.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_grey_500));
            }

        } else {
            tvBonemassType.setText("");
            tvBonemassValue.setText("-");
            GradientDrawable gd = (GradientDrawable) llBonemassBorder.getBackground().getCurrent();
            gd.setStroke(2, ContextCompat.getColor(this, R.color.grey_500));
            llBonemass.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_500));
        }

        if (Common_Utils.isNotNullOrEmpty(systolic)) {
            tvSystolicValue.setText(""+Integer.parseInt(systolic));
            tvSystolicType.setText(getSysCategory(Integer.parseInt(systolic)));
            if (getSysCategory(Integer.parseInt(systolic)).equalsIgnoreCase("Low")) {
                GradientDrawable gd = (GradientDrawable) llSystolicBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llSystolic.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            } else if (getSysCategory(Integer.parseInt(systolic)).equalsIgnoreCase("Normal")) {
                GradientDrawable gd = (GradientDrawable) llSystolicBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.colorAccent));
                llSystolic.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            } else if (getSysCategory(Integer.parseInt(systolic)).equalsIgnoreCase("Elevated")) {
                GradientDrawable gd = (GradientDrawable) llSystolicBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.yellow_500));
                llSystolic.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow_500));
            } else if (getSysCategory(Integer.parseInt(systolic)).equalsIgnoreCase("Hypertension I")) {
                GradientDrawable gd = (GradientDrawable) llSystolicBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llSystolic.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            } else if (getSysCategory(Integer.parseInt(systolic)).equalsIgnoreCase("Hypertension II")) {
                GradientDrawable gd = (GradientDrawable) llSystolicBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llSystolic.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            }
        } else {
            tvSystolicType.setText("");
            tvSystolicValue.setText("-");
            GradientDrawable gd = (GradientDrawable) llSystolicBorder.getBackground().getCurrent();
            gd.setStroke(2, ContextCompat.getColor(this, R.color.grey_500));
            llSystolic.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_500));
        }
        if (Common_Utils.isNotNullOrEmpty(diastolic)) {
            tvDiastolicValue.setText(""+Integer.parseInt(diastolic));
            tvDiastolicType.setText(getDiaCategory(Integer.parseInt(diastolic)));
            if (getDiaCategory(Integer.parseInt(diastolic)).equalsIgnoreCase("Low")) {
                GradientDrawable gd = (GradientDrawable) llDiastolicBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llDiastolic.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            } else if (getDiaCategory(Integer.parseInt(diastolic)).equalsIgnoreCase("Normal")) {
                GradientDrawable gd = (GradientDrawable) llDiastolicBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.colorAccent));
                llDiastolic.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            } else if (getDiaCategory(Integer.parseInt(diastolic)).equalsIgnoreCase("Hypertension I")) {
                GradientDrawable gd = (GradientDrawable) llDiastolicBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llDiastolic.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            } else if (getDiaCategory(Integer.parseInt(diastolic)).equalsIgnoreCase("Hypertension II")) {
                GradientDrawable gd = (GradientDrawable) llDiastolicBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llDiastolic.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            }
        } else {
            tvDiastolicType.setText("");
            tvDiastolicValue.setText("-");
            GradientDrawable gd = (GradientDrawable) llDiastolicBorder.getBackground().getCurrent();
            gd.setStroke(2, ContextCompat.getColor(this, R.color.grey_500));
            llDiastolic.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_500));
        }
        if (Common_Utils.isNotNullOrEmpty(temperature)) {
            tvTempValue.setText(TWO_DECIMAL_PLACES.format(Double.parseDouble(temperature)));
            tvTempType.setText(getTempCategory(Double.parseDouble(temperature)));
            if (getTempCategory(Double.parseDouble(temperature)).equalsIgnoreCase("Low")) {
                GradientDrawable gd = (GradientDrawable) llTemperatureBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llTemperature.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            } else if (getTempCategory(Double.parseDouble(temperature)).equalsIgnoreCase("Normal")) {
                GradientDrawable gd = (GradientDrawable) llTemperatureBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.colorAccent));
                llTemperature.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            } else if (getTempCategory(Double.parseDouble(temperature)).equalsIgnoreCase("High")) {
                GradientDrawable gd = (GradientDrawable) llTemperatureBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llTemperature.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            }
        } else {
            tvTempType.setText("");
            tvTempValue.setText("-");
            GradientDrawable gd = (GradientDrawable) llTemperatureBorder.getBackground().getCurrent();
            gd.setStroke(2, ContextCompat.getColor(this, R.color.grey_500));
            llTemperature.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_500));
        }
        if (Common_Utils.isNotNullOrEmpty(pulse)) {
            tvPulseValue.setText(""+Integer.parseInt(pulse));
            tvPulseType.setText(getPulseCategory(Integer.parseInt(pulse)));
            if (getPulseCategory(Integer.parseInt(pulse)).equalsIgnoreCase("Low")) {
                GradientDrawable gd = (GradientDrawable) llPulseBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llPulse.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            } else if (getPulseCategory(Integer.parseInt(pulse)).equalsIgnoreCase("Normal")) {
                GradientDrawable gd = (GradientDrawable) llPulseBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.colorAccent));
                llPulse.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            } else if (getPulseCategory(Integer.parseInt(pulse)).equalsIgnoreCase("High")) {
                GradientDrawable gd = (GradientDrawable) llPulseBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llPulse.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            }
        } else {
            tvPulseValue.setText("-");
            tvPulseType.setText("");
            GradientDrawable gd = (GradientDrawable) llPulseBorder.getBackground().getCurrent();
            gd.setStroke(2, ContextCompat.getColor(this, R.color.grey_500));
            llPulse.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_500));
        }

        if (Common_Utils.isNotNullOrEmpty(oxygensat)) {
            tvOxygensatValue.setText(TWO_DECIMAL_PLACES.format(Double.parseDouble(oxygensat)));
            tvOxygensatType.setText(getOxygenCategory(Double.parseDouble(oxygensat)));
            if (getOxygenCategory(Double.parseDouble(oxygensat)).equalsIgnoreCase("Low")) {
                GradientDrawable gd = (GradientDrawable) llOxygensatBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llOxygensat.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            } else if (getOxygenCategory(Double.parseDouble(oxygensat)).equalsIgnoreCase("Normal")) {
                GradientDrawable gd = (GradientDrawable) llOxygensatBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.colorAccent));
                llOxygensat.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            } else if (getOxygenCategory(Double.parseDouble(oxygensat)).equalsIgnoreCase("High")) {
                GradientDrawable gd = (GradientDrawable) llOxygensatBorder.getBackground().getCurrent();
                gd.setStroke(2, ContextCompat.getColor(this, R.color.red_500));
                llOxygensat.setBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
            }
        } else {
            tvOxygensatValue.setText("-");
            tvOxygensatType.setText("");
            GradientDrawable gd = (GradientDrawable) llOxygensatBorder.getBackground().getCurrent();
            gd.setStroke(2, ContextCompat.getColor(this, R.color.grey_500));
            llOxygensat.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_500));
        }
        if (Common_Utils.isNotNullOrEmpty(weight)) {
            tvWeight.setText(weight);
        }
        if (Common_Utils.isNotNullOrEmpty(heightFt)) {
            tvFt.setText(heightFt);
        }
        if (Common_Utils.isNotNullOrEmpty(heightInch)) {
            tvInch.setText(heightInch);
        }

    }

    @OnClick(R.id.btn_print)
    public void onViewClicked() {
        takeScreenshot();
    }

    private void takeScreenshot() {
        Date now = new Date();
        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            String mPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + File.separator
                    + getResources().getString(R.string.app_name)
                    + File.separator
                    + "Media"
                    + File.separator + "Images" + File.separator + "OfflineReport" + File.separator + now + ".jpg";

            File dir = new File(mPath);
            if (!dir.exists())
                dir.mkdirs();

            String filename = "/" +now+".jpg";

            File nomedia = new File(dir, ".nomedia");
            try {
                nomedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }



            // image naming and path  to include sd card  appending name you choose for file
            //String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);

//            int totalHeight = scrollView.getChildAt(0).getHeight();
//            int totalWidth = scrollView.getChildAt(0).getWidth();
//            v1.layout(0, 0, totalWidth, totalHeight);
//            v1.buildDrawingCache(true);

            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(dir, filename);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        Uri uri = null;
        if (VersionUtils.isAfter24()) {
            uri = FileProvider.getUriForFile(getAppContext(),
                    BuildConfig.APPLICATION_ID + ".provider", imageFile);
        } else {
            uri = Uri.fromFile(imageFile);
        }

        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }


    // THE BMI CATEGORIES
    public String getBMICategory(double result) {
        String category = "";
        if (result < 18) {
            category = "Underweight";
        } else if (result >= 18 && result <= 27) {
            category = "Normal";
        } else if (result > 27 && result <= 30) {
            category = "Overweight";
        } else if (result > 30) {
            category = "Obese";
        }
        return category;
    }

    public String getFATCategory(double result) {
        String category = "";

        if (profilesItem.getGender().equalsIgnoreCase("female")) {

            if (result <= 15) {
                category = "Athletic";
            } else if (result > 15 && result <= 24) {
                category = "Normal";
            } else if (result > 24 && result <= 30) {
                category = "Acceptable";
            } else if (result > 30 && result <= 36) {
                category = "Overweight";
            } else if (result > 36) {
                category = "Obese";
            }
            return category;
        } else {


            if (result <= 10) {
                category = "Athletic";
            } else if (result > 10 && result <= 18) {
                category = "Normal";
            } else if (result > 18 && result <= 25) {
                category = "Acceptable";
            } else if (result > 25 && result <= 28) {
                category = "Overweight";
            } else if (result > 28) {
                category = "Obese";
            }
            return category;
        }

    }

    public String getBoneMassCategory(double result) {
        String category = "";
        if (profilesItem.getGender().equalsIgnoreCase("female")) {
            if (result < 3.6) {
                category = "Low";
            } else if (result >= 3.6 && result <= 4.8) {
                category = "Normal";
            } else if (result > 4.8) {
                category = "Good";
            }
            return category;
        } else {

            if (result < 3.4) {
                category = "Low";
            } else if (result >= 3.4 && result <= 5) {
                category = "Normal";
            } else if (result > 5) {
                category = "Good";
            }
            return category;
        }

    }

    public String getMuscleCategory(double result) {
        String category = "";
        if (profilesItem.getGender().equalsIgnoreCase("female")) {
            if (result < 65.2) {
                category = "Low";
            } else if (result >= 65.2 && result <= 81.4) {
                category = "Normal";
            } else if (result > 81.4) {
                category = "Good";
            }
            return category;

        } else {

            if (result < 70) {
                category = "Low";
            } else if (result >= 70 && result <= 86.6) {
                category = "Normal";
            } else if (result > 86.6) {
                category = "Good";
            }
            return category;
        }
    }

    public String getHydrationCategory(double result) {
        String category = "";
        if (profilesItem.getGender().equalsIgnoreCase("female")) {

            if (result < 45) {
                category = "Low";
            } else if (result >= 45 && result <= 60) {
                category = "Normal";
            } else if (result > 60) {
                category = "Good";
            }
            return category;

        } else {

            if (result < 50) {
                category = "Low";
            } else if (result >= 50 && result <= 65) {
                category = "Normal";
            } else if (result > 65) {
                category = "Good";
            }
            return category;
        }
    }

    // THE Sys CATEGORIES
    public static String getSysCategory(int result) {
        String category;
        if (result < 90) {
            category = "Low";
        } else if (result >= 90 && result <= 120) {
            category = "Normal";
        } else if (result > 120 && result < 130) {
            category = "Elevated";
        } else if (result >= 130 && result < 140) {
            category = "Hypertension I";
        } else {
            category = "Hypertension II";
        }
        return category;
    }

    // THE Dia CATEGORIES
    public static String getDiaCategory(int result) {
        String category;
        if (result < 60) {
            category = "Low";
        } else if (result >= 60 && result <= 80) {
            category = "Normal";
        } else if (result > 80 && result < 90) {
            category = "Hypertension I";
        } else {
            category = "Hypertension II";
        }
        return category;
    }

    // THE Temp CATEGORIES
    public static String getTempCategory(double result) {
        String category;
        if (result < 93) {
            category = "Low";
        } else if (result >= 93 && result <= 99) {
            category = "Normal";
        } else {
            category = "High";
        }
        return category;
    }

    // THE Pulse CATEGORIES
    public static String getPulseCategory(int result) {
        String category;
        if (result < 60) {
            category = "Low";
        } else if (result >= 60 && result <= 90) {
            category = "Normal";
        } else {
            category = "High";
        }
        return category;
    }

    // THE Oxygen CATEGORIES
    public static String getOxygenCategory(double result) {
        String category;
        if (result < 90) {
            category = "Low";
        } else if (result >= 90 && result <= 100) {
            category = "Normal";
        } else {
            category = "High";
        }
        return category;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);

        MenuItem item1 = menu.findItem(R.id.action_logout);
        item1.setVisible(false);

        MenuItem item2 = menu.findItem(R.id.action_profile);
        item2.setVisible(false);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
