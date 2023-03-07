package com.ehealthkiosk.kiosk.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.commonresponse.Base;
import com.ehealthkiosk.kiosk.model.consult.Url;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import java.io.File;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ECGTestActivity extends BaseActivity {
    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);
    String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());

    private static final String KEY_INTEGRATION_TOKEN = "KEY_INTEGRATION_TOKEN";
    private static final String KEY_NAME = "KEY_NAME";
    private static final String KEY_AGE = "KEY_AGE";
    private static final String KEY_HEIGHT = "KEY_HEIGHT";
    private static final String KEY_WEIGHT = "KEY_WEIGHT";
    private static final String KEY_GENDER = "KEY_GENDER";
    private static final String KEY_PHONE_NUMBER = "KEY_PHONE_NUMBER";
    private static final String KEY_EXTRA_DATA = "KEY_EXTRA_DATA";
    private static final int SPANDAN_REQUEST_CODE = 10;

    String pdfUrl, report_path;
    //EditText height, weight;
    private Button start;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_c_g_test);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        start = findViewById(R.id.btn_ecg_start);
//        height = findViewById(R.id.et_height);
//        weight = findViewById(R.id.et_weight);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                report_path = "/sdcard/Download/output-1c4fa03c9bcb.pdf";
//                callUploadAPI();
//                if(height.getText().toString().equals("") || weight.getText().toString().equals("")){
//                    Common_Utils.showToast("Please fill your height and weight");
//                    return;
//                }
                openSpandanApp();
            }
        });

    }

    private void openSpandanApp() {
        Intent intent = new Intent();
        intent.setAction("in.sunfox.healthcare.spandanecg.TAKE_ECG_TEST");

        if (intent.resolveActivity(getPackageManager()) != null){
            intent.setFlags(0);
            Bundle bundle = new Bundle();
            bundle.putString(KEY_INTEGRATION_TOKEN, "OCgBm5yNqWAjdpWG");
            bundle.putString(KEY_NAME, SharedPrefUtils.getProfile(this).getName());
            bundle.putInt(KEY_AGE, SharedPrefUtils.getProfile(this).getAge());
            bundle.putString(KEY_GENDER, "male");
            bundle.putString(KEY_PHONE_NUMBER, "9999999999");
//            bundle.putInt(KEY_HEIGHT, Integer.parseInt(height.getText().toString()));
//            bundle.putInt(KEY_HEIGHT, Integer.parseInt(weight.getText().toString()));
            bundle.putString(KEY_WEIGHT, "9999999999");
            intent.putExtra(KEY_EXTRA_DATA, bundle);
            startActivityForResult(intent, SPANDAN_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Please install the Spandan App first", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){

        Intent myIntent = new Intent(getApplicationContext(), TestTypesActivity.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(getApplicationContext(), TestTypesActivity.class);
        startActivityForResult(myIntent, 0);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SPANDAN_REQUEST_CODE) {
            report_path = data.getStringExtra("KEY_REPORT_PDF_PATH");
            callUploadAPI();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void callUploadAPI() {
        Common_Utils.showProgress(this);

        HashMap<String, RequestBody> comment = new HashMap<>();
        comment.put("profile_id", RestClient.createPartFromString("" +SharedPrefUtils.getProfile(this).getId()));
        File file = new File(report_path);
        MultipartBody.Part body = RestClient.prepareFilePart("pdf_file", file);
        Call<Base<Url>> call = RestClient.getClient().uploadECG(token, kioskId, comment, body);
        call.enqueue(new Callback<Base<Url>>() {
            @Override
            public void onResponse(Call<Base<Url>> call, Response<Base<Url>> response) {
                Common_Utils.hideProgress();
                pdfUrl = response.body().getData().getUrl();
                Intent i = new Intent(ECGTestActivity.this, PDFViewActivity.class);
                i.putExtra(Constants.PDF_PATH, pdfUrl);
                startActivity(i);
                finish();

            }

            @Override
            public void onFailure(Call<Base<Url>> call, Throwable t) {
                Common_Utils.hideProgress();
                Common_Utils.showToast("Error sending report to servers");
            }
        });


    }
}