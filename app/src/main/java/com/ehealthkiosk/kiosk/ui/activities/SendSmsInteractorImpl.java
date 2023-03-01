package com.ehealthkiosk.kiosk.ui.activities;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.SendEmailPojo;
import com.ehealthkiosk.kiosk.model.SendEmailResponse;
import com.ehealthkiosk.kiosk.model.SendSMSPojo;
import com.ehealthkiosk.kiosk.model.commonresponse.Status;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SendSmsInteractorImpl implements SendSmsInteractor {
    String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());
    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);


    @Override
    public void sendMessage(final SendSMSPojo sendEmailPojo, final SendMessageListener listener) {


        Call<SendEmailResponse> call = RestClient.getClient().getSms(token, kioskId, sendEmailPojo);

        call.enqueue(new Callback<SendEmailResponse>() {
            @Override
            public void onResponse(Call<SendEmailResponse> call, Response<SendEmailResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().getResult() == 1) {
                        SendEmailResponse sendEmailResponse = response.body();
                        Status status = sendEmailResponse.getStatus();
                        if (RestClient.getStatus(status)) {
                            listener.onMessageSuccess(status.getMessage());
                        } else {
                            listener.onMessageError(status.getMessage());
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
                }
            }

            @Override
            public void onFailure(Call<SendEmailResponse> call, Throwable t) {
                listener.onMessageSuccess(t.getLocalizedMessage());
                Common_Utils.hideProgress();
            }
        });

    }

}