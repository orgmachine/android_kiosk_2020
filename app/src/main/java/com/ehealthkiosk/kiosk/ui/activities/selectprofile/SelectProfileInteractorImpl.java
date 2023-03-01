package com.ehealthkiosk.kiosk.ui.activities.selectprofile;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.profilelist.ProfileListResponse;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectProfileInteractorImpl implements SelectProfileInteractor {
    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);

    @Override
    public void getProfileList(final SelectProfileViewListener changeListener) {

        String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());

        Call<ProfileListResponse> getProfileList = RestClient.getClient().getProfileList(token, kioskId);
        getProfileList.enqueue(new Callback<ProfileListResponse>() {
            @Override
            public void onResponse(Call<ProfileListResponse> call, Response<ProfileListResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().getResult() == 1) {
                        ProfileListResponse profileListResponse = response.body();
                        if (RestClient.getStatus(profileListResponse.getStatus())) {
                            changeListener.onSuccess(profileListResponse.getProfileData(), profileListResponse.getStatus().getMessage());
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
                    Common_Utils.showToast(Constants.SERVER_ERROR);
                    Common_Utils.hideProgress();
                }
            }

            @Override
            public void onFailure(Call<ProfileListResponse> call, Throwable t) {
                changeListener.onError(t.getLocalizedMessage());

            }
        });

    }
}
