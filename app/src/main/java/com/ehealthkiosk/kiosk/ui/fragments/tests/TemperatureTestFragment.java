package com.ehealthkiosk.kiosk.ui.fragments.tests;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.MessageEvent;
import com.ehealthkiosk.kiosk.model.generatereport.Checkup;
import com.ehealthkiosk.kiosk.model.generatereport.GenerateReportData;
import com.ehealthkiosk.kiosk.model.generatereport.GenerateReportParam;
import com.ehealthkiosk.kiosk.ui.activities.OfflineBasicReportActivity;
import com.ehealthkiosk.kiosk.ui.activities.PDFViewActivity;
import com.ehealthkiosk.kiosk.ui.activities.generatereport.GenerateReportPresenter;
import com.ehealthkiosk.kiosk.ui.activities.generatereport.GenerateReportPresenterImpl;
import com.ehealthkiosk.kiosk.ui.activities.generatereport.GenerateReportView;
import com.ehealthkiosk.kiosk.ui.fragments.BaseDeviceFragment;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import si.inova.neatle.Neatle;
import si.inova.neatle.operation.CharacteristicSubscription;
import si.inova.neatle.operation.CharacteristicsChangedListener;
import si.inova.neatle.operation.CommandResult;


public class TemperatureTestFragment extends BaseDeviceFragment implements GenerateReportView {


    BluetoothDevice device;
    boolean isReading = false;
    boolean running = true;
    @BindView(R.id.img_instruction)
    ImageView imgInstruction;
    @BindView(R.id.tv_instruction)
    TextView tvInstruction;

    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.btn_manual)
    Button btnManual;
    @BindView(R.id.btn_skip)
    Button btnSkip;
    @BindView(R.id.tv_reading)
    TextView tvReading;

    @BindView(R.id.layout_setup_temperature)
    LinearLayout layoutSetupTemperature;

    @BindView(R.id.layout_instruction)
    LinearLayout layoutInstruction;

    @BindView(R.id.layout_btn_height)
    LinearLayout layoutBtnHeight;

    @BindView(R.id.layout_reading_height)
    LinearLayout layoutReadingHeight;

    @BindView(R.id.layout_measuring_height)
    LinearLayout layoutMeasuringHeight;


    @BindView(R.id.btn_continue)
    Button btnContinue;
    @BindView(R.id.btn_manual1)
    Button btnManual1;
    @BindView(R.id.btn_retry)
    Button btnRetry;
    @BindView(R.id.btn_cancel)
    Button btnCancel;

    AlertDialog progressDialog;


    private CharacteristicSubscription subscription;
    private GenerateReportPresenter generateReportPresenter;
    private static DecimalFormat TWO_DECIMAL_PLACES = new DecimalFormat(".##");
    int i = 0;

    @Override
    protected int getLayout() {
        return R.layout.fragment_test_temperature;
    }

    private void init() {
        if(getActivity() != null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
            Bundle bundle = new Bundle();
            bundle.putString("name", "weight");
            firebaseAnalytics.logEvent( "temperature_fragment_opened", bundle);
        }
        generateReportPresenter = new GenerateReportPresenterImpl(this);
        showInstruction();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstruction();
                isReading = false;
                if (subscription !=null && subscription.isStarted()) {
                    Log.d("TAG", "stopping subscription");

                    subscription.stop();
                }

            }
        });

    }

//    void setupDevice(){
//
//        Log.d("Device",  Neatle.getDevice(device_addr).toString());
//        final ConnectionMonitor monitor =
//                Neatle.createConnectionMonitor(getActivity(), Neatle.getDevice(device_addr));
//        monitor.setKeepAlive(true);
//        monitor.setOnConnectionStateListener(new ConnectionStateListener() {
//            @Override
//            public void onConnectionStateChanged(Connection connection, int newState) {
//                System.out.println("State is "+newState);
//
//                if(connection.isConnected()){
//                    // The device has connected
//                    Log.d("Device", "Connected to the thermometer");
//
//                    isDeviceConnected = true;
//                    if (isRetry){
//                        takeReading();
//                    }else{
//                        showInstruction();
//                    }
//
//                    monitor.stop();
//
//                }
//                else if (connection.isConnecting()){
//
//                    Log.d("Device", "Connecting to the thermometer");
//                    System.out.println("Connecting to thermo");
//                    isDeviceConnected = false;
//                    if (i == 2){
//                        showInstruction();
//                    }else{
//                        showSetup();
//                    }
//
//                    i++;
//                } else {
//                    showInstruction();
//                }
//            }
//        });
//        monitor.start();
//
//    }

    void showReading(double reading) {

        layoutSetupTemperature.setVisibility(View.INVISIBLE);
        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
        layoutReadingHeight.setVisibility(View.VISIBLE);

        tvReading.setText(TWO_DECIMAL_PLACES.format(reading) + " °F");
    }

    void showMeasuring() {

        layoutSetupTemperature.setVisibility(View.INVISIBLE);
        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.VISIBLE);
        layoutReadingHeight.setVisibility(View.INVISIBLE);


    }

    void showInstruction() {
        try {
            layoutSetupTemperature.setVisibility(View.INVISIBLE);
            layoutBtnHeight.setVisibility(View.VISIBLE);
            layoutInstruction.setVisibility(View.VISIBLE);
            layoutMeasuringHeight.setVisibility(View.INVISIBLE);
            layoutReadingHeight.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.d("TAG", "Device was already set, retry cta");

        }
    }

    void showSetup() {
        layoutSetupTemperature.setVisibility(View.VISIBLE);
        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
        layoutReadingHeight.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        if(SharedPrefUtils.getTemp(getActivity()) != null
                && !SharedPrefUtils.getTemp(getActivity()).equals("")){
            showReading(Double.parseDouble(SharedPrefUtils.getTemp(getActivity())));

        }
    }

    void openManualDialog(final Context mContext) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_manual_temperature, viewGroup, false);
        Button btnEnter = dialogView.findViewById(R.id.btn_enter);
        final EditText etTemp = dialogView.findViewById(R.id.et_fahrenheit);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(etTemp.getText().toString())) {
                    Toast.makeText(mContext, getResources().getString(R.string.enter_temp_toast),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                double temp = Double.parseDouble(etTemp.getText().toString());

                if (temp <= 0) {
                    Toast.makeText(mContext, getResources().getString(R.string.invalid_temp_toast), Toast.LENGTH_SHORT).show();
                    return;
                }


                final String tempStr = etTemp.getText().toString();


                SharedPrefUtils.setTemp(getActivity(), TWO_DECIMAL_PLACES.format(temp));

                alertDialog.dismiss();

                mActivity.setSourceMap(Constants.TEMPERATURE, Constants.READING_MANUAL);
                showReading(temp);
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_DONE_TEST));
//                tvReading.setText(TWO_DECIMAL_PLACES.format(temp) + " °F");

            }
        });
    }


    @OnClick({R.id.btn_start, R.id.btn_manual, R.id.btn_skip, R.id.btn_continue, R.id.btn_manual1, R.id.btn_retry})
    public void onViewClicked(View view) {
        String profileId = SharedPrefUtils.getProfileId(getActivity());
        switch (view.getId()) {
            case R.id.btn_start:
                takeReading();

//                if (isDeviceConnected){
//                    takeReading();
//                }else{
//                    isRetry = true;
//                    setupDevice();
//                }

                break;
            case R.id.btn_retry:
//                isRetry = true;
//                if (isDeviceConnected){
//                    takeReading();
//                }else{
//                    setupDevice();
//                }
                showInstruction();
                if (subscription != null && subscription.isStarted())
                    subscription.stop();
                isReading = false;
                break;
            case R.id.btn_manual1:
            case R.id.btn_manual:
                openManualDialog(getActivity());
                break;


            case R.id.btn_skip:
                if (profileId.equalsIgnoreCase("guest")) {
                    Intent i = new Intent(getActivity(), OfflineBasicReportActivity.class);
                    startActivity(i);
                    getActivity().finish();
                } else {

                    String bmi = SharedPrefUtils.getBMI(getActivity());
                    String hydration = SharedPrefUtils.getHydration(getActivity());
                    String fat = SharedPrefUtils.getFAT(getActivity());
                    String bonemss = SharedPrefUtils.getBonemass(getActivity());
                    String obesity = SharedPrefUtils.getObesity(getActivity());
                    String protien = SharedPrefUtils.getProtein(getActivity());
                    String vfi = SharedPrefUtils.getVFI(getActivity());
                    String bmr = SharedPrefUtils.getBMR(getActivity());
                    String bodyAge = SharedPrefUtils.getBodyAge(getActivity());
                    String muscle = SharedPrefUtils.getMuscle(getActivity());
                    String systolic = SharedPrefUtils.getSystolic(getActivity());
                    String diastolic = SharedPrefUtils.getDiastolic(getActivity());
                    String temperature = SharedPrefUtils.getTemp(getActivity());
                    String pulse = SharedPrefUtils.getPulse(getActivity());
                    String oxygensat = SharedPrefUtils.getSp02(getActivity());
                    String heightFt = SharedPrefUtils.getHeightFT(getActivity());
                    String heightInch = SharedPrefUtils.getHeightInch(getActivity());

                    Log.d("READING", obesity + protien + vfi + bmr + bodyAge);


                    String weight = SharedPrefUtils.getWeight(getActivity());
                    double height = Common_Utils.convertFeetandInchesToCentimeter(heightFt, heightInch);


                    if (!Common_Utils.isNotNullOrEmpty(bmi)) {
                        double weightKg = Double.parseDouble(weight);
                        double meters = ((Double.parseDouble(heightFt) * 12) + Double.parseDouble(heightInch)) * 0.0254;


                        double metricFormula = Common_Utils.metricFormula(meters, weightKg);
                        bmi = TWO_DECIMAL_PLACES.format(metricFormula);
                    }

                    GenerateReportParam param = new GenerateReportParam();
                    param.setProfile_id(profileId);

                    Checkup checkup = new Checkup();
                    if (Common_Utils.isNotNullOrEmpty(bmi)) {
                        checkup.setBmi(bmi);
                    }
                    if (Common_Utils.isNotNullOrEmpty(hydration)) {
                        checkup.setHydration(hydration);
                    }
                    if (Common_Utils.isNotNullOrEmpty(fat)) {
                        checkup.setFat(fat);
                    }
                    if (Common_Utils.isNotNullOrEmpty(bonemss)) {
                        checkup.setBonemass(bonemss);
                    }
                    if (Common_Utils.isNotNullOrEmpty(muscle)) {
                        checkup.setMuscle(muscle);
                    }
                    if (Common_Utils.isNotNullOrEmpty(systolic)) {
                        checkup.setSystolic(systolic);
                    }
                    if (Common_Utils.isNotNullOrEmpty(diastolic)) {
                        checkup.setDiastolic(diastolic);
                    }
                    if (Common_Utils.isNotNullOrEmpty(temperature)) {
                        checkup.setTemperature(temperature);
                    }
                    if (Common_Utils.isNotNullOrEmpty(pulse)) {
                        checkup.setPulse(pulse);
                    }
                    if (Common_Utils.isNotNullOrEmpty(oxygensat)) {
                        checkup.setOxygen_sat(oxygensat);
                    }
                    if (Common_Utils.isNotNullOrEmpty(weight)) {
                        checkup.setWeight(weight);
                    }
                    if (Common_Utils.isNotNullOrEmpty(String.valueOf(height))) {
                        checkup.setHeight(String.valueOf(height));
                    }
                    if (Common_Utils.isNotNullOrEmpty(protien)) {
                        checkup.setProtien(protien);
                    }
                    if (Common_Utils.isNotNullOrEmpty(obesity)) {
                        checkup.setObesity(obesity);
                    }
                    if (Common_Utils.isNotNullOrEmpty(vfi)) {
                        checkup.setVfi(vfi);
                    }
                    if (Common_Utils.isNotNullOrEmpty(bmr)) {
                        checkup.setBmr(bmr);
                    }
                    if (Common_Utils.isNotNullOrEmpty(bodyAge)) {
                        checkup.setBodyage(bodyAge);
                    }



                    param.setCheckup(checkup);

                    generateReportPresenter.generateReport(param);

                    Log.d("READING", "report data" + param);
                }
                break;
            case R.id.btn_continue:
                if(subscription !=null && subscription.isStarted()){
                    subscription.stop();
                }
                if (SharedPrefUtils.getHeightFT(getActivity()).equals("") &&
                        SharedPrefUtils.getWeight(getActivity()).equals("")) {

                    Toast.makeText(getActivity(), R.string.empty_tests_case, Toast.LENGTH_SHORT).show();
                    /*Commenting this out temporary since this needs to be discusss*/

//                    Intent i = new Intent(getActivity(), OfflineBasicReportActivity.class);
//                    startActivity(i);
//                    getActivity().finish();
                } else {

                    String data_source = mActivity.getSourceMap();
                    String bmi = SharedPrefUtils.getBMI(getActivity());
                    String hydration = SharedPrefUtils.getHydration(getActivity());
                    String fat = SharedPrefUtils.getFAT(getActivity());
                    String bonemss = SharedPrefUtils.getBonemass(getActivity());
                    String muscle = SharedPrefUtils.getMuscle(getActivity());
                    String obesity = SharedPrefUtils.getObesity(getActivity());
                    String protien = SharedPrefUtils.getProtein(getActivity());
                    String vfi = SharedPrefUtils.getVFI(getActivity());
                    String bmr = SharedPrefUtils.getBMR(getActivity());
                    String bodyAge = SharedPrefUtils.getBodyAge(getActivity());
                    String systolic = SharedPrefUtils.getSystolic(getActivity());
                    String diastolic = SharedPrefUtils.getDiastolic(getActivity());
                    String temperature = SharedPrefUtils.getTemp(getActivity());
                    String pulse = SharedPrefUtils.getPulse(getActivity());
                    String oxygensat = SharedPrefUtils.getSp02(getActivity());
                    String heightFt = SharedPrefUtils.getHeightFT(getActivity());
                    String heightInch = SharedPrefUtils.getHeightInch(getActivity());
                    String weight = SharedPrefUtils.getWeight(getActivity());
                    String glucose = SharedPrefUtils.getGlucose(getActivity());
                    String haemoglobin = SharedPrefUtils.getHb(getActivity());


                    double height = Common_Utils.convertFeetandInchesToCentimeter(heightFt, heightInch);

                    if (!Common_Utils.isNotNullOrEmpty(bmi)) {
                        double weightKg = Double.parseDouble(weight);
                        double meters = ((Double.parseDouble(heightFt) * 12) + Double.parseDouble(heightInch)) * 0.0254;


                        double metricFormula = Common_Utils.metricFormula(meters, weightKg);
                        bmi = String.valueOf(metricFormula);
                    }



                    GenerateReportParam param = new GenerateReportParam();

                    param.setProfile_id(profileId);

                    Checkup checkup = new Checkup();
                    checkup.setData_source(data_source);

                    if (Common_Utils.isNotNullOrEmpty(bmi)) {
                        checkup.setBmi(bmi);
                    }
                    if (Common_Utils.isNotNullOrEmpty(hydration)) {
                        checkup.setHydration(hydration);
                    }
                    if (Common_Utils.isNotNullOrEmpty(fat)) {
                        checkup.setFat(fat);
                    }
                    if (Common_Utils.isNotNullOrEmpty(bonemss)) {
                        checkup.setBonemass(bonemss);
                    }
                    if (Common_Utils.isNotNullOrEmpty(muscle)) {
                        checkup.setMuscle(muscle);
                    }
                    if (Common_Utils.isNotNullOrEmpty(systolic)) {
                        checkup.setSystolic(systolic);
                    }
                    if (Common_Utils.isNotNullOrEmpty(diastolic)) {
                        checkup.setDiastolic(diastolic);
                    }
                    if (Common_Utils.isNotNullOrEmpty(temperature)) {
                        checkup.setTemperature(temperature);
                    }
                    if (Common_Utils.isNotNullOrEmpty(pulse)) {
                        checkup.setPulse(pulse);
                    }
                    if (Common_Utils.isNotNullOrEmpty(oxygensat)) {
                        checkup.setOxygen_sat(oxygensat);
                    }
                    if (Common_Utils.isNotNullOrEmpty(weight)) {
                        checkup.setWeight(weight);
                    }
                    if (Common_Utils.isNotNullOrEmpty(String.valueOf(height))) {
                        checkup.setHeight(String.valueOf(height));
                    }
                    if (Common_Utils.isNotNullOrEmpty(String.valueOf(glucose))) {
                        checkup.setGlucose(String.valueOf(glucose));
                    }
                    if (Common_Utils.isNotNullOrEmpty(String.valueOf(haemoglobin))) {
                        checkup.setHb(String.valueOf(haemoglobin));
                    }
                    if (Common_Utils.isNotNullOrEmpty(protien)) {
                        checkup.setProtien(protien);
                    }
                    if (Common_Utils.isNotNullOrEmpty(obesity)) {
                        checkup.setObesity(obesity);
                    }
                    if (Common_Utils.isNotNullOrEmpty(vfi)) {
                        checkup.setVfi(vfi);
                    }
                    if (Common_Utils.isNotNullOrEmpty(bmr)) {
                        checkup.setBmr(bmr);
                    }
                    if (Common_Utils.isNotNullOrEmpty(bodyAge)) {
                        checkup.setBodyage(bodyAge);
                    }

                    if (!DeviceIdPrefHelper.getBgTestType(getActivity(), Constants.BG_TEST_TYPE).equals("")) {
                        checkup.setBgTestType(DeviceIdPrefHelper.getBgTestType(getActivity(), Constants.BG_TEST_TYPE));
                    } else {
                        checkup.setBgTestType("");
                    }

                    if (DeviceIdPrefHelper.getTestType(getActivity(), Constants.TEST_TYPE).equals("Basic Health")) {
                        checkup.setReportType("Basic Health");
                    } else if (DeviceIdPrefHelper.getTestType(getActivity(), Constants.TEST_TYPE).equals("Wellness Test"))
                        checkup.setReportType("Wellness Test");
                    if (DeviceIdPrefHelper.getrailwayEmployee(getActivity(), true)) {
                        checkup.setIsFree(true);
                    } else {
                        checkup.setIsFree(false);
                    }


                    param.setCheckup(checkup);

                    generateReportPresenter.generateReport(param);
                }
                break;

        }
    }

    void takeReading() {

        showMeasuring();




        // this is the address of our thermometer
        final String device_addr = DeviceIdPrefHelper.getDeviceAddress(mActivity, Constants.TEMPERATURE);

        device = Neatle.getDevice(device_addr);
//
        UUID batteryService = UUID.fromString("CDEACB80-5235-4C07-8846-93A37EE6B86D");
        final List<UUID> batteryCharacteristic = new ArrayList<>();
        batteryCharacteristic.add(UUID.fromString("CDEACB81-5235-4C07-8846-93A37EE6B86D"));

////
////                    UUID batteryService = UUID.fromString("0000FF12-0000-1000-8000-00805F9B34FB");
////                    final List<UUID> batteryCharacteristic = new ArrayList<>();
////                    batteryCharacteristic.add(UUID.fromString("0000FF01-0000-1000-8000-00805F9B34FB"));
//
//
//        Operation operation = Neatle.createOperationBuilder(mActivity)
//                .read(batteryService, batteryCharacteristic.get(0))
//                .onFinished(new SimpleOperationObserver() {
//                    @Override
//                    public void onOperationFinished(Operation op, OperationResults results) {
//                        if(results.wasSuccessful()){
////                            int battery = results.getResult(batteryCharacteristic.get(0)).getValueAsInt();
//                            try {
//
//                                int a  = results.getResult(batteryCharacteristic.get(0)).getValue()[2] & 0xFF;
//                                int b  = results.getResult(batteryCharacteristic.get(0)).getValue()[3] & 0xFF;
//                                double temp = (a*256 + b)/100.0;
//                                // show the result on the UI
//
//                                double f = temp * 9/5 + 32;
//                                if (Common_Utils.isNotNullOrEmpty(String.valueOf(f))){
//                                    SharedPrefUtils.setTemp(mActivity,String.valueOf(f));
//                                    tvReading.setText(String.valueOf(f) + " °F");
//
//                                    showReading();
//                                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_DONE_TEST));
//                                }else{
//                                    showInstruction();
//                                }
//                            }catch (Exception e){
//                                Toast.makeText(mActivity,"There was some error in reading data. Please try again",Toast.LENGTH_SHORT).show();
//                                showInstruction();
//                            }
//                        }else {
//                            System.out.println("Operation failed! ");
//                        }
//                    }
//                })
//                .build(device);
//        operation.execute();


        subscription =
                Neatle.createSubscription(mActivity, device, batteryService, batteryCharacteristic.get(0));


        subscription.setOnCharacteristicsChangedListener(new CharacteristicsChangedListener() {
            @Override
            public void onCharacteristicChanged(CommandResult change) {
                if(!running) return;

                // this listens to change in the temperature reading.
                // when the thermometer takes a reading this method will autmatically be called

                Log.d("Device", "something changed");
                if (change.wasSuccessful() && isReading) {


                    try {
                        int a = change.getValue()[2] & 0xFF;
                        int b = change.getValue()[3] & 0xFF;
                        double temp = (a * 256 + b) / 100.0;
                        // show the result on the UI

                        double f = temp * 9 / 5 + 32;
                        if (Common_Utils.isNotNullOrEmpty(String.valueOf(f))) {
                            SharedPrefUtils.setTemp(mActivity, String.valueOf(f));
//                            tvReading.setText(TWO_DECIMAL_PLACES.format(f) + " °F");

                            mActivity.setSourceMap(Constants.TEMPERATURE, Constants.READING_DEVICE);
                            showReading(f);
                            dismissDialog();
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_DONE_TEST));
                        } else {
                            dismissDialog();
                            showInstruction();
                        }
                    } catch (Exception e) {
                        dismissDialog();
//                        Toast.makeText(HealthKioskApp.getAppContext(),"There was some error in reading data. Please try again",Toast.LENGTH_SHORT).show();
//                        showInstruction();
                    }


                } else {
                    dismissDialog();
                    showInstruction();

                }
            }
        });
        Log.d("TAG", "starting subscription");
        isReading = true;
        subscription.start();
    }

    @Override
    public void showProgress() {
        Common_Utils.showProgress(mActivity);
    }

    @Override
    public void showSuccess(GenerateReportData generateReportData, String msg) {
        DeviceIdPrefHelper.setrailwayEmployee(getActivity(), false);
        Log.d("TESTING", String.valueOf(DeviceIdPrefHelper.getrailwayEmployee(getActivity(), false)));
        Intent i = new Intent(mActivity, PDFViewActivity.class);
        i.putExtra(Constants.PDF_PATH, generateReportData.getPdf_link());
        i.putExtra(Constants.REPORT_DATA, generateReportData.getReportId());
        startActivity(i);
        mActivity.finish();
    }

    @Override
    public void showError(String msg) {
        Common_Utils.hideProgress();
        Common_Utils.responseCodePromp(getActivity().findViewById(android.R.id.content), msg);
    }

    void openConnectionDialog() {
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_progress_bar,
                viewGroup, false);

        ProgressBar progressBar = (ProgressBar) dialogView.findViewById(R.id.buffer_progressBar);
        progressBar.setProgress(20);
        progressBar.setSecondaryProgress(50);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        //finally creating the alert dialog and displaying it
        progressDialog = builder.create();
        progressDialog.show();

    }

    void dismissDialog() {
        //progressDialog.dismiss();
    }

    @Override
    public void onStop() {
        running = false;
        super.onStop();

        try{
            if (subscription != null)
                subscription.stop();
        }catch (Exception exc){

        }

    }

}
