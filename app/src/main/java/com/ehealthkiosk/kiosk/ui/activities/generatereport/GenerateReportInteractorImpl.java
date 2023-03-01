package com.ehealthkiosk.kiosk.ui.activities.generatereport;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.generatereport.GenerateReportParam;
import com.ehealthkiosk.kiosk.model.generatereport.GenerateReportResponse;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenerateReportInteractorImpl implements GenerateReportInteractor {
    @Override
    public void generateReport(GenerateReportParam generateReportParam, final GenerateReportViewListener changeListener) {
        String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());
        String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);


        Call<GenerateReportResponse> generateReportResponseCall = RestClient.getClient().generateReport(token, kioskId, generateReportParam);
        generateReportResponseCall.enqueue(new Callback<GenerateReportResponse>() {
            @Override
            public void onResponse(Call<GenerateReportResponse> call, Response<GenerateReportResponse> response) {
                System.out.println("Response ==== " + response.body());
                if (response.isSuccessful()) {
                    if (response.body().getStatus().getResult() == 1) {
                        GenerateReportResponse profileListResponse = response.body();
                        System.out.println("Response message==== " + profileListResponse.getStatus().getMessage());
                        if (RestClient.getStatus(profileListResponse.getStatus())) {
                            changeListener.onSuccess(profileListResponse.getData(), profileListResponse.getStatus().getMessage());
                        } else {
                            changeListener.onError(profileListResponse.getStatus().getMessage());
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
            public void onFailure(Call<GenerateReportResponse> call, Throwable t) {
                changeListener.onError(t.getLocalizedMessage());
            }
        });
    }
}
