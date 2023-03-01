package com.ehealthkiosk.kiosk.ui.activities.register;

import android.content.Context;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.api.ErrorUtils;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.register.RegisterParam;
import com.ehealthkiosk.kiosk.model.register.RegisterResponse;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterInteractorImpl implements RegisterInteractor {
    Context context;

    @Override
    public void register(RegisterParam registerParam, final RegisterViewListener changeListener) {
        // create part for file (photo, video, ...)
        MultipartBody.Part body = RestClient.prepareFilePart("photo", registerParam.getPhoto());
        String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);


        Call<RegisterResponse> registerResponseCall = RestClient.getClient().register(kioskId,
                requestRegisterParamsBody(registerParam), body);
        registerResponseCall.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().getResult() == 1) {
                        RegisterResponse registerResponse = response.body();
                        if (RestClient.getStatus(registerResponse.getStatus())) {
                            SharedPrefUtils.setAppState(HealthKioskApp.getAppContext(), Constants.STATE_REGISTERED);
                            SharedPrefUtils.setToken(HealthKioskApp.getAppContext(), registerResponse.getData().getToken());
                            SharedPrefUtils.setProfile(HealthKioskApp.getAppContext(), registerResponse.getData().getProfile());
                            SharedPrefUtils.setProfileId(HealthKioskApp.getAppContext(), String.valueOf(registerResponse.getData().getProfile().getId()));
                            changeListener.onSuccess("");
                        } else {
                            changeListener.onError(response.body().getStatus().getMessage());
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
                    Common_Utils.showToast(response.body().getStatus().getMessage());
                    Common_Utils.hideProgress();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                changeListener.onError("");

            }
        });
    }

    @Override
    public void addProfile(final RegisterParam registerParam, final RegisterViewListener changeListener) {
        // create part for file (photo, video, ...)
        String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());
        String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);

        MultipartBody.Part body = RestClient.prepareFilePart("photo", registerParam.getPhoto());

        Call<RegisterResponse> registerResponseCall = RestClient.getClient().addProfile(token, kioskId,
                requestRegisterParamsBody(registerParam), body);
        registerResponseCall.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    RegisterResponse registerResponse = response.body();
                    if (RestClient.getStatus(registerResponse.getStatus())) {
                        SharedPrefUtils.setProfile(HealthKioskApp.getAppContext(), registerResponse.getData().getProfile());
                        SharedPrefUtils.setProfileId(HealthKioskApp.getAppContext(), String.valueOf(registerResponse.getData().getProfile().getId()));
                        DeviceIdPrefHelper.setUserEmail(HealthKioskApp.getAppContext(), Constants.USER_EMAIL, registerResponse.getData().getProfile().getEmail());
                        changeListener.onSuccess(registerResponse.getStatus().getMessage());
                    } else {
                        changeListener.onError(registerResponse.getStatus().getMessage());
                    }
                } else {
                    changeListener.onError(ErrorUtils.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                changeListener.onError(ErrorUtils.ErrorMessage);

            }
        });

    }

    private Map<String, RequestBody> requestRegisterParamsBody(RegisterParam registerParam) {
        HashMap<String, RequestBody> comment = new HashMap<>();
        comment.put("name", RestClient.createPartFromString(registerParam.getName()));
        comment.put("mobile", RestClient.createPartFromString(registerParam.getMobile()));
        comment.put("date_of_birth", RestClient.createPartFromString(registerParam.getAge()));
        comment.put("email", RestClient.createPartFromString(registerParam.getEmail()));
        comment.put("gender", RestClient.createPartFromString(registerParam.getGender()));
        comment.put("is_railway_employee", RestClient.createPartFromString(registerParam.getRailwayEmployeeCheck().toString()));

        return comment;

    }
}
