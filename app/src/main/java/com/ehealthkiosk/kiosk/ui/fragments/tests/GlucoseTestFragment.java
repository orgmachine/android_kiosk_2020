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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.MessageEvent;
import com.ehealthkiosk.kiosk.ui.fragments.BaseDeviceFragment;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.vise.baseble.ViseBle;
import com.vise.baseble.callback.IBleCallback;
import com.vise.baseble.callback.IConnectCallback;
import com.vise.baseble.common.PropertyType;
import com.vise.baseble.core.BluetoothGattChannel;
import com.vise.baseble.core.DeviceMirror;
import com.vise.baseble.exception.BleException;
import com.vise.baseble.model.BluetoothLeDevice;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import si.inova.neatle.Neatle;

public class GlucoseTestFragment extends BaseDeviceFragment {

    private static DecimalFormat TWO_DECIMAL_PLACES = new DecimalFormat(".##");
    private boolean running = true;
    @BindView(R.id.tv_reading)
    TextView tvReading;

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
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_manual1)
    Button btnManual1;
    Boolean isTestTypeChecked = false;
    Boolean isFromManual = false;

    @Override
    protected int getLayout() {
        return R.layout.fragment_glucose_test;
    }


    private void setup() {
        ViseBle.config()
                .setScanTimeout(-1)
                .setConnectTimeout(10 * 1000)
                .setOperateTimeout(5 * 1000)
                .setConnectRetryCount(3)
                .setConnectRetryInterval(1000)
                .setOperateRetryCount(3)
                .setOperateRetryInterval(1000)
                .setMaxConnectCount(3);

        ViseBle.getInstance().init(mActivity);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstruction();
            }
        });


    }

    private void takeReading() {
        String deviceMac = DeviceIdPrefHelper.getDeviceAddress(mActivity, Constants.GLUECOSE);
        ViseBle.getInstance().connectByMac(deviceMac, new IConnectCallback() {
            @Override
            public void onConnectSuccess(DeviceMirror deviceMirror) {
                Log.d("TAG", "connection successful");
                setListener(deviceMirror);
            }

            @Override
            public void onConnectFailure(BleException exception) {

            }

            @Override
            public void onDisconnect(boolean isActive) {

            }
        });
    }

    void showReading(double reading) {
//        tvReading.setText(reading);
        try {
            layoutBtnHeight.setVisibility(View.INVISIBLE);
            layoutInstruction.setVisibility(View.INVISIBLE);
            layoutMeasuringHeight.setVisibility(View.INVISIBLE);
            layoutReadingHeight.setVisibility(View.VISIBLE);

            tvReading.setText(TWO_DECIMAL_PLACES.format(reading) + " mg/dl");
        } catch (Exception e) {

        }

    }

    void showMeasuring() {
        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.VISIBLE);
        layoutReadingHeight.setVisibility(View.INVISIBLE);
    }

    void showInstruction() {

        layoutBtnHeight.setVisibility(View.VISIBLE);
        layoutInstruction.setVisibility(View.VISIBLE);
        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
        layoutReadingHeight.setVisibility(View.INVISIBLE);

    }

    private void setListener(final DeviceMirror deviceMirror) {

        UUID serviceUUID = Neatle.createUUID(0xFFE0);
        UUID characteristicUUID = Neatle.createUUID(0xFFE4);
        UUID des = Neatle.createUUID(0x000E);

        BluetoothGattChannel bluetoothGattChannel = new BluetoothGattChannel.Builder()
                .setBluetoothGatt(deviceMirror.getBluetoothGatt())
                .setPropertyType(PropertyType.PROPERTY_NOTIFY)
                .setServiceUUID(serviceUUID)
                .setCharacteristicUUID(characteristicUUID)
                .setDescriptorUUID(des)
                .builder();
        deviceMirror.bindChannel(new IBleCallback() {
            @Override
            public void onSuccess(byte[] data, BluetoothGattChannel bluetoothGattChannel, BluetoothLeDevice bluetoothLeDevice) {
                Log.d("TAG", "bind success");
                getUserData(deviceMirror);

                deviceMirror.setNotifyListener(bluetoothGattChannel.getGattInfoKey(), new IBleCallback() {
                    @Override
                    public void onSuccess(final byte[] data, BluetoothGattChannel bluetoothGattChannel, BluetoothLeDevice bluetoothLeDevice) {
                        Log.d("TAG", "received notification");
                        mActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                               // if(!running) return;

                                int a = data[13] & 0xFF;
                                int b = data[14] & 0xFF;
                                try {

                                    mActivity.setSourceMap(Constants.GLUECOSE, Constants.READING_DEVICE);
                                    showReading(Double.parseDouble(String.valueOf(a * 255 + b)));
                                    SharedPrefUtils.setGlucose(mActivity, String.valueOf(a * 255 + b));
                                }catch(Exception exc){

                                }
//                                bgTestTypeCheck();
                            }
                        });

                    }

                    @Override
                    public void onFailure(BleException exception) {
                        Log.d("TAG", "bind failure");
                    }
                });
            }

            @Override
            public void onFailure(BleException exception) {

            }
        }, bluetoothGattChannel);
        deviceMirror.registerNotify(false);

        Log.d("TAG", "listener is now set");
    }

//    private void bgTestTypeCheck() {
//
//    }

    private void getUserData(DeviceMirror deviceMirror) {
        UUID serviceUUID = Neatle.createUUID(0xFFE5);
        UUID characteristicUUID = Neatle.createUUID(0xFFE9);
        UUID descriptorUUID = Neatle.createUUID(0x0013);
        BluetoothGattChannel bluetoothGattChannel = new BluetoothGattChannel.Builder()
                .setBluetoothGatt(deviceMirror.getBluetoothGatt())
                .setPropertyType(PropertyType.PROPERTY_WRITE)
                .setServiceUUID(serviceUUID)
                .setCharacteristicUUID(characteristicUUID)
                .setDescriptorUUID(descriptorUUID)
                .builder();

        byte[] d = {
                (byte) 0x80, (byte) 0x0F, (byte) 0xF0, (byte) 0x02,
                (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };

        deviceMirror.bindChannel(new IBleCallback() {
            @Override
            public void onSuccess(byte[] data, BluetoothGattChannel bluetoothGattChannel, BluetoothLeDevice bluetoothLeDevice) {

            }

            @Override
            public void onFailure(BleException exception) {
                Log.d("TAG", exception.toString());
            }
        }, bluetoothGattChannel);

        deviceMirror.writeData(d);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getActivity() != null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
            Bundle bundle = new Bundle();
            bundle.putString("name", "weight");
            firebaseAnalytics.logEvent( "glucose_fragment_opened", bundle);
        }
        showInstruction();
        setup();
        if (SharedPrefUtils.getGlucose(mActivity) != null
                && !SharedPrefUtils.getGlucose(mActivity).equals("")) {
            showReading(Double.parseDouble(SharedPrefUtils.getGlucose(mActivity)));
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        running = false;
        Log.d("TAG", "weight stop called");
        try {
            ViseBle.getInstance().clear();
        } catch (Exception exc) {

        }
        Log.d("TAG", "device disconnected on OnPause");

        Log.d("TAG", "height stop called");
    }


    @OnClick({R.id.btn_start, R.id.btn_manual, R.id.btn_skip, R.id.btn_continue, R.id.btn_manual1, R.id.btn_retry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                isFromManual = false;
                openTestTypesDialog();
                Common_Utils.enableBluetooth();
                break;
            case R.id.btn_manual1:
            case R.id.btn_manual:
                openTestTypesDialog();
                isFromManual = true;
                break;
            case R.id.btn_skip:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 5));
                break;
            case R.id.btn_continue:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 5));
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
        View dialogView = LayoutInflater.from(mActivity).inflate(R.layout.glucose_test_dialog, viewGroup, false);
        Button btnEnter = dialogView.findViewById(R.id.btn_enter);
        final EditText etWeight = dialogView.findViewById(R.id.et_kg);

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

                if (TextUtils.isEmpty(etWeight.getText().toString())) {
                    Toast.makeText(mContext, getResources().getString(R.string.enter_glucose_toast),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                double glucoseInp = Double.parseDouble(etWeight.getText().toString());
//                bgTestTypeCheck();

                if (glucoseInp <= 0) {
                    Toast.makeText(mContext, getResources().getString(R.string.invalid_glucose_toast), Toast.LENGTH_SHORT).show();
                    return;
                }


                final String glucoseValue = etWeight.getText().toString();


                SharedPrefUtils.setGlucose(mActivity, TWO_DECIMAL_PLACES.format(glucoseInp));


                alertDialog.dismiss();

                mActivity.setSourceMap(Constants.GLUECOSE, Constants.READING_MANUAL);
                showReading(Double.parseDouble(glucoseValue));

            }
        });
    }

    void openTestTypesDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = mActivity.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_glucose_test_type, viewGroup, false);
        Button btnSubmit = dialogView.findViewById(R.id.btn_submit);
        RadioButton fasting = dialogView.findViewById(R.id.bg_fasting_type);
        RadioButton random = dialogView.findViewById(R.id.bg_random_type);
        RadioButton postPrandial = dialogView.findViewById(R.id.bg_post_prandial_type);


        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (random.isChecked()) {
                    isTestTypeChecked = true;
                    DeviceIdPrefHelper.setBgTestType(mActivity, Constants.BG_TEST_TYPE, Constants.BG_RANDOM_TEST);
                } else if (fasting.isChecked()) {
                    isTestTypeChecked = true;
                    DeviceIdPrefHelper.setBgTestType(mActivity, Constants.BG_TEST_TYPE, Constants.BG_FASTING_TEST);
                } else if (postPrandial.isChecked()) {
                    isTestTypeChecked = true;
                    DeviceIdPrefHelper.setBgTestType(mActivity, Constants.BG_TEST_TYPE, Constants.BG_POST_PRANDIAL_TEST);
                } else {
                    isTestTypeChecked = true;
                }
                if (isTestTypeChecked && isFromManual) {
                    alertDialog.dismiss();
                    openManualDialog(mActivity);
                } else if (isTestTypeChecked) {
                    alertDialog.dismiss();
                    takeReading();
                    showMeasuring();
                }
            }
        });

    }


}
