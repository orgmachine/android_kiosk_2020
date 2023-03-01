package com.ehealthkiosk.kiosk.ui.activities.otp;

import com.ehealthkiosk.kiosk.model.login.LoginParam;
import com.ehealthkiosk.kiosk.model.otp.OTPParam;

public interface OTPPresenter {
    void verifyOTP(OTPParam otpValueParam);
    void regenerateOTP(String from, LoginParam loginParam);
}
