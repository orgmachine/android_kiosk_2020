package com.ehealthkiosk.kiosk.ui.activities.reportlist;


import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.commonresponse.Status;
import com.ehealthkiosk.kiosk.model.healthreports.ReportsParam;
import com.ehealthkiosk.kiosk.model.healthreports.ReportsResponse;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthReportsInteractorImpl implements HealthReportsInteractor {
    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);


    @Override
    public void getHealthReports(final ReportsParam reportsParam, final HealthReportsChangeListener listener, final String from) {
        String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());
        String profile_id = SharedPrefUtils.getProfileId(HealthKioskApp.getAppContext());
        reportsParam.setProfile_id(profile_id);
        Call<ReportsResponse> call = RestClient.getClient().getHealthReportList(token, kioskId, reportsParam);

        call.enqueue(new Callback<ReportsResponse>() {
            @Override
            public void onResponse(Call<ReportsResponse> call, Response<ReportsResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().getResult() == 1) {
                        ReportsResponse commonResponse = response.body();
                        Status status = commonResponse.getStatus();
                        if (RestClient.getStatus(status)) {
                            listener.onReportsList(commonResponse.getData().getReports(), reportsParam.getOffset(), from);
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
            public void onFailure(Call<ReportsResponse> call, Throwable t) {
                listener.onError(t.getLocalizedMessage());
            }
        });
    }
}
