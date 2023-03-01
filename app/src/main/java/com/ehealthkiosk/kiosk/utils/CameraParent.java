package com.ehealthkiosk.kiosk.utils;

public interface CameraParent {
    public void sendImageChat(String imageUrl, String imgType, String Description);
    public void showCamera();
    public void closeCamera();
}
