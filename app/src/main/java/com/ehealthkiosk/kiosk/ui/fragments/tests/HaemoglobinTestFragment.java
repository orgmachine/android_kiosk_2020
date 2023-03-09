package com.ehealthkiosk.kiosk.ui.fragments.tests;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
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
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

public class HaemoglobinTestFragment extends BaseDeviceFragment {

    private static DecimalFormat TWO_DECIMAL_PLACES = new DecimalFormat(".##");
    LinearLayout linearLayout;
    UsbDeviceConnection connection;
    private boolean running = false;

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
    @BindView(R.id.btn_manual1)
    Button btnManual1;
    @BindView(R.id.btn_cancel)
    Button cancel;

    public static final String TAG = "USB";
    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    private static int TIMEOUT = 100*1000;
    private boolean forceClaim = true;
    UsbDevice device;
    PendingIntent permissionIntent;
    private byte[] bytes = new byte[144];
    UsbManager usbManager;
    private boolean hasPermission = false;
    private boolean pendingReading = false;
    private boolean isSettingUp = false;
    private boolean deviceConnected = true;
    private Thread thread;


    @Override
    protected int getLayout() {
        return R.layout.fragment_hb_test;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getActivity() != null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
            Bundle bundle = new Bundle();
            bundle.putString("name", "weight");
            firebaseAnalytics.logEvent( "pulse_fragment_opened", bundle);
        }

            linearLayout = view.findViewById(R.id.parent_layout);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    device = null;
                    running = false;

                    showInstruction();
                }
            });

            showInstruction();
            searchForDevice();

        if (SharedPrefUtils.getHb(mActivity) != null && !SharedPrefUtils.getHb(mActivity).equals("")) {
            showReading(SharedPrefUtils.getHb(getActivity()) + " g/dl");

        }


    }

    private void setDevicePermissons() {



        if (device == null) {
//            openSnackBar();
            Toast.makeText(mActivity, getResources().getString(R.string.invalid_usb_toast), Toast.LENGTH_SHORT).show();
        } else {
            permissionIntent = PendingIntent.getBroadcast(mActivity, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            mActivity.registerReceiver(usbReceiver, filter);
            usbManager.requestPermission(device, permissionIntent);
        }
    }


    private void searchForDevice(){
        usbManager = (UsbManager) mActivity.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Log.d("TAG", deviceList.toString());
        for (HashMap.Entry<String, UsbDevice> entry : deviceList.entrySet()) {
            if (entry.getValue().getProductId() == 12304) {
                device = entry.getValue();
                break;
            }
        }
    }
//    public void openSnackBar(){
//        Snackbar snackbar = Snackbar
//                .make(linearLayout, "Message is deleted", Snackbar.LENGTH_LONG)
//                .setAction("UNDO", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Snackbar snackbar1 = Snackbar.make(linearLayout, "Message is restored!", Snackbar.LENGTH_SHORT);
//                        snackbar1.show();
//                    }
//                });
//
//        snackbar.show();
//    }

    void showReading(String reading) {
        tvReading.setText(reading);
        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
        layoutReadingHeight.setVisibility(View.VISIBLE);
    }

    void showMeasuring() {


        layoutBtnHeight.setVisibility(View.INVISIBLE);
        layoutInstruction.setVisibility(View.INVISIBLE);
        layoutMeasuringHeight.setVisibility(View.VISIBLE);
        layoutReadingHeight.setVisibility(View.INVISIBLE);


    }

    void showInstruction() {
        try {
            layoutBtnHeight.setVisibility(View.VISIBLE);
            layoutInstruction.setVisibility(View.VISIBLE);
            layoutMeasuringHeight.setVisibility(View.INVISIBLE);
            layoutReadingHeight.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.d("TAG", "Device was already set, retry cta");

        }
    }

    @OnClick({R.id.btn_start, R.id.btn_manual, R.id.btn_skip, R.id.btn_continue, R.id.btn_manual1, R.id.btn_retry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                Common_Utils.enableBluetooth();
                Log.d("TAG", "Calling take reading");
                takeReading();

                Log.d("TAG", "set pending reading as true");
                pendingReading = true;
                break;
            case R.id.btn_manual1:
            case R.id.btn_manual:
                openManualDialog(mActivity);
                break;
            case R.id.btn_skip:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 6));
                break;
            case R.id.btn_continue:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 6));
                break;
            case R.id.btn_retry:
                pendingReading = false;
                showInstruction();
                break;

        }
    }

    private void ignorePastReadings(UsbEndpoint endpoint) {
        Log.d("TAG", "Trying to ignore past reading");
        while (connection.bulkTransfer(endpoint, bytes, bytes.length, 100) > 0) {
            //do in another thread){
            Log.d(TAG, "Ignoring previous reaind");
        }
    }

    private boolean takeReading() {

        if (device == null) {

            Log.d("TAG", "device was null so searching for it");
            searchForDevice();
        }

        if (device == null){
            Toast.makeText(mActivity, getResources().getString(R.string.reconnect_usb_toast),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!hasPermission){
//            Toast.makeText(mActivity, "Please allow usb permission", Toast.LENGTH_SHORT).show();
            setDevicePermissons();
            return false;
        }

        Log.d("TAG", "showing measuring icon");
        showMeasuring();

        Log.d("TAG", "preparing to read");
        UsbInterface intf = device.getInterface(0);
        UsbEndpoint endpoint = intf.getEndpoint(0);

        Log.d("TAG", "found an endpoint");
        connection = usbManager.openDevice(device);
        if(connection == null){
            Toast.makeText(mActivity, getResources().getString(R.string.reconnect_usb_toast), Toast.LENGTH_SHORT).show();
            hasPermission = false;
            pendingReading = false;
            deviceConnected = false;
            showInstruction();
            return false;
        }
        connection.claimInterface(intf, forceClaim);

        Log.d("TAG", "connection established");
        ignorePastReadings(endpoint);

        Log.d("TAG", "waiting for reading now");
        thread = new Thread(){

            @Override
            public void run() {


                    Log.d("TAG", "Inside the tread");
                    connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT); //do in another thread

                    Log.d("TAG", new String(bytes));
                    double hb = 0.0;
                    boolean hbdone = false;
                    int flag = -1;
                    for (int i = 0; i < bytes.length; i++) {
                        if (hbdone) break;
                        int d = (int) bytes[i];
                        if (d == 5) {
                            break;
                        }
                        if (d == 13) {
                            flag = 0;
                            hb = 0.0;
                        }
                        if (flag == 1 && d == 44) {
                            flag = 2;
                            hb = hb / 10;
                        }
                        if (flag == 1 && d - 47 > 0) {
                            hb = hb * 10 + d - 48;
                        }
                        if (flag == 0 && d == 44) {
                            flag = 1;
                        }
                    }
                    final String reading = hb + " g/dL";
                    if(running)
                        SharedPrefUtils.setHb(mActivity, String.valueOf(hb));

                    if (getActivity() != null && running) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mActivity.setSourceMap(Constants.HEMOGLOBIN, Constants.READING_DEVICE);
                                showReading(reading);
                            }
                        });
                    }
                }
            };
        running = true;
        thread.start();
        return true;

    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        hasPermission = true;
                        if(pendingReading){
                            takeReading();
                            pendingReading = false;
                        }
                    } else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    void openManualDialog(final Context mContext) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = mActivity.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(mActivity).inflate(R.layout.haemoglobin_test_dialog, viewGroup, false);
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
                    Toast.makeText(mContext, getResources().getString(R.string.enter_value_check),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                double hbInp = Double.parseDouble(etWeight.getText().toString());

                if (hbInp <= 0 || hbInp > 20) {
                    Toast.makeText(mContext, getResources().getString(R.string.invalid_value),
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                final String hbValue = etWeight.getText().toString();



                alertDialog.dismiss();
                mActivity.setSourceMap(Constants.HEMOGLOBIN, Constants.READING_MANUAL);
                showReading(hbValue);
                SharedPrefUtils.setHb(mActivity, String.valueOf(hbInp));
                tvReading.setText(TWO_DECIMAL_PLACES.format(hbInp) + "  g/dL");
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 6));

            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        running = false;
        try {
            Log.d("TAG", "onStop called");
            mActivity.unregisterReceiver(usbReceiver);
            thread.interrupt();
        }catch (Exception exc){

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        try {
            Log.d("TAG", "onDestroy called");
            mActivity.unregisterReceiver(usbReceiver);
            thread.interrupt();
        }catch (Exception exc){

        }
    }
}
