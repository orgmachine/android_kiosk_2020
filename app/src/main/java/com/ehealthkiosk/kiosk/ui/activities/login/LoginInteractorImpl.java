package com.ehealthkiosk.kiosk.ui.activities.login;

import android.util.Log;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.api.ErrorUtils;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.commonresponse.CommonResponse;
import com.ehealthkiosk.kiosk.model.commonresponse.Status;
import com.ehealthkiosk.kiosk.model.login.LoginParam;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginInteractorImpl implements LoginInteractor {

    private String TAG = LoginInteractorImpl.class.getSimpleName();
    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);

    @Override
    public void login(final LoginParam loginParam, final LoginViewListener listener) {
        // Mock login. I'm creating a handler to delay the answer a couple of seconds


        Call<CommonResponse> call = RestClient.getClient().login(kioskId, loginParam);

        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                Common_Utils.hideProgress();

                if (response.isSuccessful()) {
                    if (response.body().getStatus().getResult() == 1) {
                        CommonResponse commonResponse = response.body();
                        Status status = commonResponse.getStatus();

                        if (RestClient.getStatus(status)) {
                            listener.onSuccess(status.getMessage());
                            Common_Utils.hideProgress();
                        } else {
                            listener.onError(status.getMessage());
                            Common_Utils.hideProgress();
                        }
                    } else {
                        Common_Utils.showToast(response.body().getStatus().getMessage());
                        Common_Utils.hideProgress();

                    }
                } else if (response.code() == 500) {
                    Common_Utils.showToast(Constants.SERVER_ERROR);
                    Common_Utils.hideProgress();
                } else if (response.code() == 401) {
                    Common_Utils.showToast(Constants.SERVER_ERROR);
                    Common_Utils.hideProgress();
                } else {
                    Log.d("TAG", response.code() + "");
                    Common_Utils.showToast(Constants.SERVER_ERROR);

                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                listener.onError(ErrorUtils.ErrorMessage);
                Log.d("TAG", t.getLocalizedMessage());
            }
        });

    }

}