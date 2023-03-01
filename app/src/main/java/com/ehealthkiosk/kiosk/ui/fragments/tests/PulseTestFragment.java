package com.ehealthkiosk.kiosk.ui.fragments.tests;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
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
import com.ehealthkiosk.kiosk.utils.pulse.BluetoothChatService;
import com.ehealthkiosk.kiosk.utils.pulse.CallBack;
import com.ehealthkiosk.kiosk.utils.pulse.ICallBack;
import com.ehealthkiosk.kiosk.utils.pulse.MtBuf;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.greenrobot.eventbus.EventBus;

import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

public class PulseTestFragment extends BaseDeviceFragment implements ICallBack {

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
    @BindView(R.id.layout_setup_pulse)
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
    @BindView(R.id.button_cancel)
    Button buttonCancel;


    BluetoothAdapter btAdapt;
    boolean running = true;
    private BluetoothDevice device;
    private boolean stopReading = false;
    Thread pulseReaderThread;
    private BluetoothChatService mChatService;

    @Override
    public void onStart() {
        super.onStart();
        if (mChatService == null)
            setupChat();

    }

    private CallBack call;

    private void setupChat() {
        mChatService = new BluetoothChatService(mActivity, call);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_test_pulse;
    }

    private void init() {
        if(getActivity() != null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
            Bundle bundle = new Bundle();
            bundle.putString("name", "weight");
            firebaseAnalytics.logEvent( "pulse_fragment_opened", bundle);
        }

        call = new CallBack(this.mtBuf, this);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstruction();
                stopReading = true;
                pulseReaderThread.interrupt();
            }
        });
        showInstruction();


    }
    void fetchReading() {


        btAdapt = BluetoothAdapter.getDefaultAdapter();
        boolean deviceFound = false;

        Set<BluetoothDevice> devices = btAdapt.getBondedDevices();
        for (BluetoothDevice device : devices) {
            String sDeviceName = device.getAddress();
            Log.d("device_found", sDeviceName);
            if (sDeviceName.equals(DeviceIdPrefHelper.getDeviceAddress(mActivity, Constants.PULSE))) {
                deviceFound = true;
                btAdapt.cancelDiscovery();

                if (mChatService != null)
                    mChatService.stop();
                mtBuf.pulse = null;


                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int nTries = 20;

                while (mtBuf.pulse == null) {
                    if (stopReading) {
                        break;
                    }
                    // in this loop we try for 20 times to get the data

                    mChatService.start();

                    mChatService.connect(device);
                    Log.d("mt", "waiting for pulse data");

                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (nTries <= 0) {
                        break;
                    }
                    nTries--;
                }

                if (!Common_Utils.isNotNullOrEmpty(mtBuf.pulse) && running) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInstruction();
                            Toast.makeText(mActivity, getResources().getString(R.string.invalid_usb_toast), Toast.LENGTH_SHORT).show();

                        }
                    });
                    return;
                }

                if (!Common_Utils.isNotNullOrEmpty(mtBuf.spo2) && running) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInstruction();
                            Toast.makeText(mActivity, getResources().getString(R.string.reconnect_usb_toast), Toast.LENGTH_SHORT).show();

                        }
                    });
                    return;
                }

                SharedPrefUtils.setPulse(mActivity, mtBuf.pulse);
                SharedPrefUtils.setSp02(mActivity, mtBuf.spo2);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(running) {

                            mActivity.setSourceMap(Constants.PULSE, Constants.READING_DEVICE);
                            showReading(mtBuf.pulse, mtBuf.spo2);
                        }

                    }
                });
                break;
            }


        }
        if (!deviceFound && !stopReading && running) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showInstruction();
                    Toast.makeText(mActivity, getResources().getString(R.string.reconnect_usb_toast), Toast.LENGTH_SHORT).show();


                }
            });
        }

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        if (SharedPrefUtils.getPulse(getActivity()) != null
                && !SharedPrefUtils.getPulse(getActivity()).equals("")) {
            showReading(SharedPrefUtils.getPulse(getActivity()), SharedPrefUtils.getSp02(getActivity()));
        }

    }


    @OnClick({R.id.btn_start, R.id.btn_manual, R.id.btn_skip, R.id.btn_continue, R.id.btn_manual1, R.id.btn_retry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                Common_Utils.enableBluetooth();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(running)showMeasuring();
                    }
                });
                if (pulseReaderThread == null) {
                    pulseReaderThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            stopReading = false;

                            fetchReading();
                            pulseReaderThread = null;

                        }

                    });
                    pulseReaderThread.start();
                }

                break;
            case R.id.btn_manual1:
            case R.id.btn_manual:
                openManualDialog(mActivity);
                break;

            case R.id.btn_skip:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 4));
                break;
            case R.id.btn_continue:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 4));
                break;
            case R.id.btn_retry:
//                showMeasuring();
//                fetchReading();
                showInstruction();
                break;

        }
    }

    void openManualDialog(final Context mContext) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = mActivity.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_manual_pulse, viewGroup, false);
        Button btnEnter = dialogView.findViewById(R.id.btn_enter);
        final EditText etPulse = dialogView.findViewById(R.id.et_pulse);
        final EditText etsats = dialogView.findViewById(R.id.et_sats);

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

                if (TextUtils.isEmpty(etPulse.getText().toString())) {
                    Toast.makeText(mContext, "Please Enter Pulse", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(etsats.getText().toString())) {
                    Toast.makeText(mContext, "Please Enter SpO2 value", Toast.LENGTH_SHORT).show();
                    return;
                }


                double weight = Double.parseDouble(etPulse.getText().toString());
                double spo2 = Double.parseDouble(etsats.getText().toString());

                if (weight <= 0) {
                    Toast.makeText(mContext, "Invalid Pulse", Toast.LENGTH_SHORT).show();
                    return;
                } if (spo2 <= 0 || spo2 > 100) {
                    Toast.makeText(mContext, "Invalid O2 sats", Toast.LENGTH_SHORT).show();
                    return;
                }


                final String pulseStr = etPulse.getText().toString();
                final String o2str = etsats.getText().toString();
                SharedPrefUtils.setPulse(mActivity, pulseStr);
                SharedPrefUtils.setSp02(mActivity, o2str);


                alertDialog.dismiss();

                mActivity.setSourceMap(Constants.PULSE, Constants.READING_MANUAL);
                showReading(pulseStr, o2str);


//                if (Common_Utils.isNotNullOrEmpty(Sp02Str)){
//                    tvReading1.setText(Sp02Str + " % Sp02");
//                }else{
//                    tvReading1.setText("");
//                }


            }
        });
    }

    void showReading(String pulse, String oxygenLevel) {
        layoutSetupHeight.setVisibility(View.INVISIBLE);
        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
        layoutReadingHeight.setVisibility(View.VISIBLE);

        tvReading.setText(pulse + " BPM");
        if (oxygenLevel != null) {
            tvReading1.setVisibility(View.VISIBLE);
            tvReading1.setText(oxygenLevel + "% Sp02");
        } else {
            tvReading1.setVisibility(View.GONE);
        }
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


    }

    void showSetup() {
        layoutSetupHeight.setVisibility(View.VISIBLE);
        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
        layoutReadingHeight.setVisibility(View.INVISIBLE);
    }

    // required for pairing, this is a one time thing
    private BroadcastReceiver searchDevices = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("mt", action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() == null) {
                    return;
                }
                Log.d("mt", "found" + device.getName());
                if (device.getName() != null && device.getName().contains("SpO208")) {

                    btAdapt.cancelDiscovery();
                    if (mChatService != null)
                        mChatService.stop();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.d("mt", "connected to" + device.getName());
                    Toast.makeText(mActivity, getResources().getString(R.string.pairing_success),
                            Toast.LENGTH_SHORT).show();
                    mChatService.start();
                    mChatService.connect(device);

                }
            }
        }
    };






    public MtBuf mtBuf = new MtBuf();

    @Override
    public void onDestroy() {
        //mActivity.unregisterReceiver(searchDevices);
        super.onDestroy();
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onStop() {
        super.onStop();
        running = false;
        if (btAdapt != null) {
            btAdapt.cancelDiscovery();
        }
        if (mChatService != null)
            mChatService.stop();
    }

    @Override
    public void call() {

    }

}
