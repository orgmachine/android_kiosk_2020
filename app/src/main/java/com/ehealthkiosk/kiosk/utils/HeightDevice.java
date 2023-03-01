package com.ehealthkiosk.kiosk.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.vise.baseble.ViseBle;
import com.vise.baseble.callback.IBleCallback;
import com.vise.baseble.callback.IConnectCallback;
import com.vise.baseble.common.PropertyType;
import com.vise.baseble.core.BluetoothGattChannel;
import com.vise.baseble.core.DeviceMirror;
import com.vise.baseble.exception.BleException;
import com.vise.baseble.model.BluetoothLeDevice;

import java.security.Identity;
import java.util.UUID;

import si.inova.neatle.Neatle;

public class HeightDevice {
    private Context mContext;
    private DeviceMirror mDeviceMirror;
    private boolean is_connected;

    public void setup() {
        ViseBle.config()
                .setScanTimeout(-1)
                .setConnectTimeout(-1)
                .setOperateTimeout(5 * 1000)
                .setConnectRetryCount(3)
                .setConnectRetryInterval(1000)
                .setOperateRetryCount(3)
                .setOperateRetryInterval(1000)
                .setMaxConnectCount(3);

        ViseBle.getInstance().init(mContext);

        Log.d("TAG", "setup successful");

    }
    private static final HeightDevice mInstance = new HeightDevice();

    public static HeightDevice getInstance() {
        return mInstance;
    }

    public void setContext(Context context){
        mContext = context;
    }

    private HeightDevice() { }

    public void connectToDevice() {
        Log.d("TAG", "inside connect to device ");
        String deviceAddress = DeviceIdPrefHelper.getDeviceAddress(mContext, Constants.HEIGHT);

        Log.d("TAG", "connecting to" + deviceAddress);
        ViseBle.getInstance().connectByMac(deviceAddress, new IConnectCallback() {
            @Override
            public void onConnectSuccess(DeviceMirror deviceMirror) {
                Log.d("TAG", "connection successful");
                sendCommand("C");
                is_connected = true;
                mDeviceMirror = deviceMirror;
                setListener(deviceMirror);
            }

            @Override
            public void onConnectFailure(BleException exception) {
                Log.d("TAG", exception.toString());
                is_connected = false;
            }

            @Override
            public void onDisconnect(boolean isActive) {
                Log.d("TAG", "disconnected");
                is_connected = false;
            }
        });
    }

    public void takeReading() {
        Log.d("TAG", String.valueOf(is_connected));
        if (is_connected) {

            sendCommand("F");

        } else {
            Common_Utils.responseCodePromp(((Activity)mContext).getWindow().getDecorView().getRootView(), "Please wait till the device is connected");
        }

    }

    private void setListener(final DeviceMirror deviceMirror2) {

        UUID serviceUUID = Neatle.createUUID(0xFFE0);
        UUID characteristicUUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");
        BluetoothGattChannel bluetoothGattChannel = new BluetoothGattChannel.Builder()
                .setBluetoothGatt(mDeviceMirror.getBluetoothGatt())
                .setPropertyType(PropertyType.PROPERTY_NOTIFY)
                .setServiceUUID(serviceUUID)
                .setCharacteristicUUID(characteristicUUID)
                .builder();
        mDeviceMirror.bindChannel(new IBleCallback() {
            @Override
            public void onSuccess(byte[] data, BluetoothGattChannel bluetoothGattChannel, BluetoothLeDevice bluetoothLeDevice) {
                Log.d("TAG", "bind success");
                sendCommand("O");
                mDeviceMirror.setNotifyListener(bluetoothGattChannel.getGattInfoKey(), new IBleCallback() {
                    @Override
                    public void onSuccess(final byte[] data, BluetoothGattChannel bluetoothGattChannel, BluetoothLeDevice bluetoothLeDevice) {
                        Log.d("TAG", "received notification");

                    }

                    @Override
                    public void onFailure(BleException exception) {
                        Log.d("TAG", exception.toString());
                    }
                });
            }

            @Override
            public void onFailure(BleException exception) {

            }
        }, bluetoothGattChannel);
        mDeviceMirror.registerNotify(false);
        Log.d("TAG", "listener is now set");
    }

    private void sendCommand(String command) {
        Log.d("TAG", "sending " + command);
        UUID serviceUUID = Neatle.createUUID(0xFFE0);

        UUID characteristicUUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");
        BluetoothGattChannel bluetoothGattChannel = new BluetoothGattChannel.Builder()
                .setBluetoothGatt(mDeviceMirror.getBluetoothGatt())
                .setPropertyType(PropertyType.PROPERTY_WRITE)
                .setServiceUUID(serviceUUID)
                .setCharacteristicUUID(characteristicUUID)
                .builder();

        byte[] d = command.getBytes();

        mDeviceMirror.bindChannel(new IBleCallback() {
            @Override
            public void onSuccess(byte[] data, BluetoothGattChannel bluetoothGattChannel, BluetoothLeDevice bluetoothLeDevice) {
                Log.d("TAG", String.valueOf(data));
            }

            @Override
            public void onFailure(BleException exception) {
                Log.d("TAG", exception.toString());
            }
        }, bluetoothGattChannel);

        mDeviceMirror.writeData(d);

    }

    public void disconnect(){
        if (mDeviceMirror != null) {
            Log.d("TAG", "will try to close the laser now");
            sendCommand("C");

            mDeviceMirror.unregisterNotify(false);
            mDeviceMirror.close();

            mDeviceMirror.clear();

            is_connected = false;
            ViseBle.getInstance().clear();
        }
        Log.d("TAG", "device disconnected on OnPause");
    }


}