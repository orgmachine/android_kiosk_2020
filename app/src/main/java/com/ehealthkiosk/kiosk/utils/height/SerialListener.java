package com.ehealthkiosk.kiosk.utils.height;

public interface SerialListener {
    void onSerialConnect();
    void onSerialConnectError(Exception e);
    void onSerialRead(byte[] data);
    void onSerialIoError(Exception e);
}
