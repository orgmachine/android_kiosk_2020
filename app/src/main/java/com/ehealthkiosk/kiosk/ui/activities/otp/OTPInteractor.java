package com.ehealthkiosk.kiosk.ui.activities.otp;

import com.ehealthkiosk.kiosk.model.login.LoginParam;
import com.ehealthkiosk.kiosk.model.otp.OTPParam;


public interface OTPInteractor {
    interface OTPViewListener{
        void onSuccess(String msg);
        void onError(String msg);
    }

    void verifyOTP(OTPParam otpValueParam, OTPViewListener changeListener);
    void regenerateOTP(String from, LoginParam loginParam, OTPViewListener changeListener);

}
