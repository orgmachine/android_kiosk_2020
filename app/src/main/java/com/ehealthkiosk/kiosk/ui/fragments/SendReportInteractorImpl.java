package com.ehealthkiosk.kiosk.ui.fragments;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.SendEmailPojo;
import com.ehealthkiosk.kiosk.model.SendEmailResponse;
import com.ehealthkiosk.kiosk.model.commonresponse.Status;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SendReportInteractorImpl implements SendReportInteractor {
    String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());
    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);


    @Override
    public void sendEmail(final SendEmailPojo sendEmailPojo, final SendReporterListener listener) {


        Call<SendEmailResponse> call = RestClient.getClient().getEmail(token, kioskId, sendEmailPojo);

        call.enqueue(new Callback<SendEmailResponse>() {
            @Override
            public void onResponse(Call<SendEmailResponse> call, Response<SendEmailResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().getResult() == 1) {
                        SendEmailResponse sendEmailResponse = response.body();
                        Status status = sendEmailResponse.getStatus();
                        if (RestClient.getStatus(status)) {
                            listener.onSuccess(status.getMessage());
                        } else {
                            listener.onError(status.getMessage());
                        }
                    } else {
                        Common_Utils.hideProgress();
                        Common_Utils.showToast(response.body().getStatus().getMessage());
                    }
                } else if (response.code() == 500) {
                    Common_Utils.showToast(Constants.SERVER_ERROR);
                } else if (response.code() == 401) {
                    Common_Utils.showToast(Constants.SERVER_ERROR);
                } else {
                    Common_Utils.showToast(Constants.SERVER_ERROR);
                    Common_Utils.hideProgress();
                }
            }

            @Override
            public void onFailure(Call<SendEmailResponse> call, Throwable t) {
                listener.onError(t.getLocalizedMessage());
                Common_Utils.hideProgress();
            }
        });

    }

}