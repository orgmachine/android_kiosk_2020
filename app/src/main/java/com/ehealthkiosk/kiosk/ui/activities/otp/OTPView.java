package com.ehealthkiosk.kiosk.ui.activities.otp;

public interface OTPView {
    void showProgress();
    void showOTPSuccess(String msg);
    void showOTPError(String msg);
}
