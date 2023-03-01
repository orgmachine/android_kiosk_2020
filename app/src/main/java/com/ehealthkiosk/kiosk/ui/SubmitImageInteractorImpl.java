package com.ehealthkiosk.kiosk.ui;

import android.content.Context;
import android.widget.Toast;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.sendDermaImage.SendImageData;
import com.ehealthkiosk.kiosk.model.sendDermaImage.SendImageDetails;
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

public class SubmitImageInteractorImpl implements SubmitImageInteractor {
    Context context;

    @Override
    public void sendImageData(SendImageData sendImageData, SubmitImageViewListener changeListener) {
        // create part for file (photo, video, ...)
        MultipartBody.Part body = RestClient.prepareFilePart("image", sendImageData.getPhoto());
        String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);
        String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());


        Call<SendImageDetails> sendImageResponseCall = RestClient.getClient().getImageData(token, kioskId,
                submitImageDetails(sendImageData), body);
        sendImageResponseCall.enqueue(new Callback<SendImageDetails>() {
            @Override
            public void onResponse(Call<SendImageDetails> call, Response<SendImageDetails> response) {
                if (response.body().getStatus().getResult() == 1) {
                    SendImageDetails sendImageResponse = response.body();
                    changeListener.onSuccess(sendImageResponse);

                }else{
                    Toast.makeText(context, response.body().getStatus().getMessage(),Toast.LENGTH_SHORT);
                }

            }


            @Override
            public void onFailure(Call<SendImageDetails> call, Throwable t) {
                changeListener.onError(t.toString());

            }
        });
    }


    private Map<String, RequestBody> submitImageDetails(SendImageData sendImageData) {
        HashMap<String, RequestBody> comment = new HashMap<>();
        comment.put("profile_id", RestClient.createPartFromString(String.valueOf(sendImageData.getId())));
        comment.put("report_type", RestClient.createPartFromString(sendImageData.getReportType()));

        return comment;

    }


}
