package com.ehealthkiosk.kiosk.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.api.RestInterface;
import com.ehealthkiosk.kiosk.model.commonresponse.Status;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SyncService extends Service {

    private Handler mHandler;
    // default interval for syncing data
    public static final long DEFAULT_SYNC_INTERVAL = 300 * 1000;

    // task to be run here
    private Runnable runnableService = new Runnable() {
        @Override
        public void run() {
            callHeartbeatAPI();
            mHandler.postDelayed(runnableService, DEFAULT_SYNC_INTERVAL);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
        mHandler = new Handler();
        mHandler.post(runnableService);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void callHeartbeatAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestInterface service = retrofit.create(RestInterface.class);


        String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);

        if (kioskId != null && !kioskId.equals("")) {
            Call<Status> call = service.getPolling(kioskId);
            call.enqueue(new Callback<Status>() {
                @Override
                public void onResponse(Call<Status> call, Response<Status> response) {
                    Log.d("SERVICE", "Started successfully");

                }

                @Override
                public void onFailure(Call<Status> call, Throwable t) {
                    Log.d("SERVICE", "Failed");


                }
            });
        } else {
            Toast.makeText(this, "Service Api is not called", Toast.LENGTH_SHORT);
        }

    }
}

