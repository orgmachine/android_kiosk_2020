package com.ehealthkiosk.kiosk.ui.activities.otp;


import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.api.ErrorUtils;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.commonresponse.CommonResponse;
import com.ehealthkiosk.kiosk.model.login.LoginParam;
import com.ehealthkiosk.kiosk.model.otp.OTPParam;
import com.ehealthkiosk.kiosk.model.otp.OTPResponse;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OTPInteractorImpl implements OTPInteractor {

    private String TAG = OTPInteractorImpl.class.getSimpleName();
    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);



    @Override
    public void verifyOTP(OTPParam otpValueParam, final OTPViewListener changeListener) {
        Call<OTPResponse> validateOTP = RestClient.getClient().otp(kioskId, otpValueParam);
        validateOTP.enqueue(new Callback<OTPResponse>() {
            @Override
            public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().getResult() == 1) {
                        OTPResponse otpResponse = response.body();
                        if (RestClient.getStatus(otpResponse.getStatus())) {
                            SharedPrefUtils.setAppState(HealthKioskApp.getAppContext(), Constants.STATE_REGISTERED);
                            SharedPrefUtils.setToken(HealthKioskApp.getAppContext(), otpResponse.getData().getToken());
                            SharedPrefUtils.setReferenceHeight(HealthKioskApp.getAppContext(), 244);
                            changeListener.onSuccess(otpResponse.getStatus().getMessage());
                        } else {
                            changeListener.onError(otpResponse.getStatus().getMessage());
                        }
                    } else {
                        Common_Utils.hideProgress();
                        Common_Utils.showToast(response.body().getStatus().getMessage());
                    }
                } else if (response.code() == 500) {
                    Common_Utils.hideProgress();
                    Common_Utils.showToast(Constants.SERVER_ERROR);
                } else if (response.code() == 401) {
                    Common_Utils.hideProgress();
                    Common_Utils.showToast(Constants.SERVER_ERROR);
                } else {
                    Common_Utils.hideProgress();
                    Common_Utils.showToast(Constants.SERVER_ERROR);
                    Common_Utils.hideProgress();
                }
            }

            @Override
            public void onFailure(Call<OTPResponse> call, Throwable t) {
                changeListener.onError(t.getLocalizedMessage());

            }
        });
    }

    @Override
    public void regenerateOTP(String from, LoginParam param, final OTPViewListener changeListener) {

        Call<CommonResponse> regenrateOTP = RestClient.getClient().login(kioskId, param);

        regenrateOTP.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    CommonResponse commonResponseData = response.body();
                    if (RestClient.getStatus(commonResponseData.getStatus())) {
                        changeListener.onSuccess(commonResponseData.getStatus().getMessage());
                    } else {
                        changeListener.onError("");
                    }
                } else {
                    changeListener.onError(ErrorUtils.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                changeListener.onError(ErrorUtils.ErrorMessage);
            }
        });
    }

}
