package com.ehealthkiosk.kiosk.ui.activities.register;

import com.ehealthkiosk.kiosk.model.login.LoginParam;
import com.ehealthkiosk.kiosk.model.otp.OTPParam;
import com.ehealthkiosk.kiosk.model.register.RegisterParam;
import com.ehealthkiosk.kiosk.ui.activities.otp.OTPInteractor;

public interface RegisterInteractor {

    interface RegisterViewListener{
        void onSuccess(String msg);
        void onError(String msg);
    }

    void register(RegisterParam registerParam, RegisterInteractor.RegisterViewListener changeListener);
    void addProfile(RegisterParam registerParam, RegisterInteractor.RegisterViewListener changeListener);
}
