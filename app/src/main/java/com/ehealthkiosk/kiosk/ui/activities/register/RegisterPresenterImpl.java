package com.ehealthkiosk.kiosk.ui.activities.register;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.register.RegisterParam;
import com.ehealthkiosk.kiosk.ui.activities.otp.OTPInteractor;
import com.ehealthkiosk.kiosk.ui.activities.otp.OTPInteractorImpl;
import com.ehealthkiosk.kiosk.ui.activities.otp.OTPView;
import com.ehealthkiosk.kiosk.utils.Common_Utils;

import static com.ehealthkiosk.kiosk.HealthKioskApp.context;

public class RegisterPresenterImpl implements RegisterPresenter, RegisterInteractor.RegisterViewListener {

    private RegisterView registerView;
    private RegisterInteractor registerInteractor;

    public RegisterPresenterImpl(RegisterView registerView) {
        this.registerView = registerView;
        this.registerInteractor = new RegisterInteractorImpl();
    }

    @Override
    public void onSuccess(String msg) {
        if(registerView!=null)
            registerView.showSuccess(msg);
    }

    @Override
    public void onError(String msg) {
        if(registerView!=null)
            registerView.showError(msg);
    }

    @Override
    public void register(RegisterParam registerParam) {
        if (!Common_Utils.isNetworkAvailable()) {
            Common_Utils.showToast(context.getResources().getString(R.string.no_internet));
            return;
        }


        if(registerView!=null)
            registerView.showProgress();

        registerInteractor.register(registerParam,this);

    }

    @Override
    public void addProfile(RegisterParam registerParam) {
        if (!Common_Utils.isNetworkAvailable()) {
            Common_Utils.showToast(context.getResources().getString(R.string.no_internet));
            return;
        }


        if(registerView!=null)
            registerView.showProgress();

        registerInteractor.addProfile(registerParam,this);
    }
}
