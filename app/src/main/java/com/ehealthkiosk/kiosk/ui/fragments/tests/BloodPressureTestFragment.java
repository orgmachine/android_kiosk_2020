package com.ehealthkiosk.kiosk.ui.fragments.tests;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.contec.bp.code.base.ContecDevice;
import com.contec.bp.code.bean.ContecBluetoothType;
import com.contec.bp.code.callback.BluetoothSearchCallback;
import com.contec.bp.code.callback.CommunicateCallback;
import com.contec.bp.code.connect.ContecSdk;
import com.contec.bp.code.tools.Utils;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.MessageEvent;
import com.ehealthkiosk.kiosk.ui.fragments.BaseDeviceFragment;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class BloodPressureTestFragment extends BaseDeviceFragment {

    String TAG = "BloodPressureTestFragment";
    ContecDevice myDevice;
    private ContecSdk sdk = new ContecSdk();
    boolean running = true;
    boolean isDeviceConnected = false;
    boolean isRetry = false;

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
    @BindView(R.id.tv_reading1)
    TextView tvReading1;


    @BindView(R.id.layout_setup_bp)
    LinearLayout layoutSetupHeight;

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


    @Override
    protected int getLayout() {
        return R.layout.fragment_test_bloodpressure;
    }

    private void init() {
        if(getActivity() != null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
            Bundle bundle = new Bundle();
            bundle.putString("name", "weight");
            firebaseAnalytics.logEvent( "bp_fragment_opened", bundle);
        }
//        showSetup();
        sdk.init(ContecBluetoothType.TYPE_FF, false);
//        sdk.startBluetoothSearch(bluetoothSearchCallback, 20000);
        showInstruction();



    }

    @Override
    public void onResume() {
        super.onResume();

        //sdk.startBluetoothSearch(bluetoothSearchCallback, 20000);
    }

    private void stopBluetooth() {
        try {
            sdk.stopCommunicate();
        } catch (Exception exc) {

        }
        try {
            sdk.stopBluetoothSearch();
        } catch (Exception exc) {

        }
    }

    @Override
    public void onStop() {
        running = false;
        try{
            sdk.stopCommunicate();
        }catch (Exception exc){

        }
        try{
            sdk.stopBluetoothSearch();
        }catch (Exception exc){

        }

        super.onStop();
    }


    void showReading(String systollicReading, String diastollicReading) {

        layoutSetupHeight.setVisibility(View.INVISIBLE);
        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
        layoutReadingHeight.setVisibility(View.VISIBLE);

        tvReading.setText("Systolic " + systollicReading + " mmhg");
        tvReading1.setText("Diastolic " + diastollicReading + " mmhg");
    }

    void showMeasuring() {

        layoutSetupHeight.setVisibility(View.INVISIBLE);
        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.VISIBLE);
        layoutReadingHeight.setVisibility(View.INVISIBLE);



    }

    void showInstruction() {
        layoutSetupHeight.setVisibility(View.INVISIBLE);
        layoutBtnHeight.setVisibility(View.VISIBLE);
        layoutInstruction.setVisibility(View.VISIBLE);
        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
        layoutReadingHeight.setVisibility(View.INVISIBLE);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstruction();
                if (isDeviceConnected) {
                    isDeviceConnected = false;
                    sdk.stopCommunicate();
                } else {
                    sdk.stopBluetoothSearch();
                }

            }
        });
    }

    void showSetup() {
        layoutSetupHeight.setVisibility(View.VISIBLE);
        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
        layoutReadingHeight.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        if (SharedPrefUtils.getSystolic(getActivity()) != null
                && !SharedPrefUtils.getSystolic(getActivity()).equals("") &&
                SharedPrefUtils.getDiastolic(getActivity()) != null
                && !SharedPrefUtils.getDiastolic(getActivity()).equals("")

        ) {
            showReading(SharedPrefUtils.getSystolic(getActivity()),
                    SharedPrefUtils.getDiastolic(getActivity()));
        }
    }


    @OnClick({R.id.btn_start, R.id.btn_manual, R.id.btn_skip, R.id.btn_continue, R.id.btn_manual1, R.id.btn_retry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                Common_Utils.enableBluetooth();
                if (isDeviceConnected) {
                    showMeasuring();
                    sdk.startCommunicate(mActivity, myDevice, communicateCallback);
                } else {
                    isRetry = true;
                    showMeasuring();
                    sdk.startBluetoothSearch(bluetoothSearchCallback, 20000);
                }

                break;
            case R.id.btn_manual1:
            case R.id.btn_manual:
                openManualDialog(mActivity);
                break;
            case R.id.btn_skip:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 3));
                break;
            case R.id.btn_continue:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 3));
                break;
            case R.id.btn_retry:
                showInstruction();
                break;

        }
    }


    void openManualDialog(final Context mContext) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = mActivity.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_manual_bloodpressure, viewGroup, false);
        Button btnEnter = dialogView.findViewById(R.id.btn_enter);
        final EditText sysET = dialogView.findViewById(R.id.et_systolic);
        final EditText diasysET = dialogView.findViewById(R.id.et_diastolic);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(sysET.getText().toString())) {
                    Toast.makeText(mContext, getResources().getString(R.string.enter_syst), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(diasysET.getText().toString())) {
                    Toast.makeText(mContext, getResources().getString(R.string.enter_diast), Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(sysET.getText().toString())) {
                    Toast.makeText(mContext, getResources().getString(R.string.enter_syst), Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(diasysET.getText().toString())) {
                    Toast.makeText(mContext, getResources().getString(R.string.enter_diast), Toast.LENGTH_SHORT).show();
                    return;
                }
                int diasys = Integer.parseInt(diasysET.getText().toString());
                int sys = Integer.parseInt(sysET.getText().toString());
                if (diasys <= 20 || diasys >= 200) {
                    Toast.makeText(mContext, getResources().getString(R.string.invalid_diast_toast), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (sys <= 0 || sys >= 200) {
                    Toast.makeText(mContext, getResources().getString(R.string.invalid_syst_toast), Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPrefUtils.setSystolic(mActivity, sysET.getText().toString());
                SharedPrefUtils.setDiastolic(mActivity, diasysET.getText().toString());
                alertDialog.dismiss();
                mActivity.setSourceMap(Constants.BP, Constants.READING_MANUAL);
                showReading(sysET.getText().toString(), diasysET.getText().toString());
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 3));
            }
        });
    }

    // this callback is for searching the device, this can be used as it is
    BluetoothSearchCallback bluetoothSearchCallback = new BluetoothSearchCallback() {

        @Override
        public void onSearchError(int errorCode) {
            System.out.println("onSearchError" + errorCode);
            isDeviceConnected = false;
            showInstruction();
        }

        @Override
        public void onContecDeviceFound(ContecDevice contectDevice) {
            System.out.println("onContecDeviceFound");
            if (contectDevice.getName() == null) {
                return;
            }
            System.out.println("onContecDeviceFound" + contectDevice.getName());

            if (contectDevice.getMac().equals(DeviceIdPrefHelper.getDeviceAddress(mActivity, Constants.BP))) {
                Log.e(TAG, Utils.bytesToHexString(contectDevice.getRecord()));
                sdk.stopBluetoothSearch();
                myDevice = contectDevice;
                isDeviceConnected = true;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        if(!running) return;
                        if (isRetry) {
                            showMeasuring();
                            sdk.startCommunicate(mActivity, myDevice, communicateCallback);
                        } else {
                            showInstruction();
                        }
                    }
                });


            }
        }

        @Override
        public void onSearchComplete() {
            System.out.println("onSearchComplete");

            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if(!running) return;
                    if (!isDeviceConnected) {
                        //sdk.startBluetoothSearch(bluetoothSearchCallback, 20000);
                        showInstruction();
                        Toast.makeText(mActivity, getResources().getString(R.string.invalid_device_toast), Toast.LENGTH_SHORT).show();


                    } else {
                        System.out.println("onSearchComplete Device Connected");
                    }

                }
            });
        }
    };


    // this is the callback when we get the actual data back from the device

    CommunicateCallback communicateCallback = new CommunicateCallback() {
        @Override
        public void onCommunicateSuccess(final String json) {
            Log.e(TAG, json);

            try {
                JSONArray jsonArray = new JSONObject(json).getJSONArray("BloodPressureData");

                final String systolicPressure, diastolicPressure, Pulse;
                JSONObject jsonObject = jsonArray.optJSONObject(jsonArray.length() - 1);
                systolicPressure = jsonObject.optString("SystolicPressure");
                diastolicPressure = jsonObject.optString("DiastolicPressure");
                Pulse = jsonObject.optString("PulseRate");

                if (mActivity == null)
                    return;
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if(!running) return;
                        //this is where you set the values back on the UI

                        mActivity.setSourceMap(Constants.BP, Constants.READING_DEVICE);
                        showReading(systolicPressure, diastolicPressure);
                        SharedPrefUtils.setSystolic(mActivity, systolicPressure);
                        SharedPrefUtils.setDiastolic(mActivity, diastolicPressure);

//                        tvReading.setText("Systolic " + systolicPressure + " mmhg");
//                        tvReading1.setText("Diastolic " + diastolicPressure + " mmhg");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if(!running) return;
                        showInstruction();
                        Log.d("TAG", "Error taking reading. Please try again");
//                        Toast.makeText(mActivity, "Error taking reading. Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        }

        @Override
        public void onCommunicateFailed(int errorCode) {
            System.out.println("onCommunicateFailed " + errorCode);
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if(!running) return;
                    showInstruction();
                    Log.d("TAG", "Error taking reading. Please try again");
                    Toast.makeText(mActivity, getResources().getString(R.string.device_error), Toast.LENGTH_SHORT).show();
                }
            });
//            Toast.makeText(mActivity, "Error communicating device. Please try again", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCommunicateProgress(int status) {
            System.out.println("onCommunicateProgress " + status);
        }
    };

}
