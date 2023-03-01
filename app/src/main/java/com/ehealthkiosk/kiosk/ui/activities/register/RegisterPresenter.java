package com.ehealthkiosk.kiosk.ui.activities.register;

import com.ehealthkiosk.kiosk.model.login.LoginParam;
import com.ehealthkiosk.kiosk.model.otp.OTPParam;
import com.ehealthkiosk.kiosk.model.register.RegisterParam;

public interface RegisterPresenter {

    void register(RegisterParam registerParam);
    void addProfile(RegisterParam registerParam);

}
