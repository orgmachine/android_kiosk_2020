package com.ehealthkiosk.kiosk.ui.activities;

import android.util.Log;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.api.ErrorUtils;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.commonresponse.Status;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingsInteractorImpl implements SettingsInteractor {

    private String TAG = SettingsInteractorImpl.class.getSimpleName();

    @Override
    public void settings(final KioskIdData kioskIdData, final SettingsViewListener listener) {
        String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);

        // Mock login. I'm creating a handler to delay the answer a couple of seconds


        Call<SettingsDataResponse> call = RestClient.getClient().getSettings(kioskId, kioskIdData);

        call.enqueue(new Callback<SettingsDataResponse>() {
            @Override
            public void onResponse(Call<SettingsDataResponse> call, Response<SettingsDataResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().getResult() == 1) {
                        SettingsDataResponse settingsDataResponse = response.body();
                        Status status = settingsDataResponse.getStatus();
                        if (RestClient.getStatus(status)) {
                            listener.onSuccess(settingsDataResponse, status.getMessage());
                        } else {
                            listener.onError(status.getMessage());
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
                    Common_Utils.showToast(response.body().getStatus().getMessage());
                } else {
                    listener.onError(ErrorUtils.parseError(response));
                    Common_Utils.hideProgress();
                    Log.d("TAG", response.code() + "");
                }
            }

            @Override
            public void onFailure(Call<SettingsDataResponse> call, Throwable t) {
                listener.onError(ErrorUtils.ErrorMessage);
                Log.d("TAG", t.getLocalizedMessage());
            }
        });

    }

}