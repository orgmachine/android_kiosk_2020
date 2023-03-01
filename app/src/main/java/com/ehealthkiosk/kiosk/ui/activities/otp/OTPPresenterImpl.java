package com.ehealthkiosk.kiosk.ui.activities.otp;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.login.LoginParam;
import com.ehealthkiosk.kiosk.model.otp.OTPParam;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import static com.ehealthkiosk.kiosk.HealthKioskApp.context;


public class OTPPresenterImpl implements OTPPresenter, OTPInteractor.OTPViewListener {

    private OTPView otpView;
    private OTPInteractor otpInteractor;

    public OTPPresenterImpl(OTPView otpView) {
        this.otpView = otpView;
        this.otpInteractor = new OTPInteractorImpl();
    }

    @Override
    public void verifyOTP(OTPParam otpValueParam) {
        if (!Common_Utils.isNetworkAvailable()) {
            Common_Utils.showToast(context.getResources().getString(R.string.no_internet));
            return;
        }


        if(otpView!=null)
            otpView.showProgress();

        otpInteractor.verifyOTP(otpValueParam,this);
    }

    @Override
    public void regenerateOTP(String from, LoginParam loginParam) {
        if(otpView!=null)
            otpView.showProgress();

        otpInteractor.regenerateOTP(from,loginParam,this);
    }

    @Override
    public void onSuccess(String msg) {
        if(otpView!=null)
            otpView.showOTPSuccess(msg);
    }

    @Override
    public void onError(String msg) {
        if(otpView!=null)
            otpView.showOTPError(msg);
    }


}
