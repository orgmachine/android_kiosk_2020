package com.ehealthkiosk.kiosk.ui.activities.otp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.broooapps.otpedittext2.OtpEditText;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.login.LoginParam;
import com.ehealthkiosk.kiosk.model.otp.OTPParam;
import com.ehealthkiosk.kiosk.ui.activities.BaseActivity;
import com.ehealthkiosk.kiosk.ui.activities.login.LoginPresenter;
import com.ehealthkiosk.kiosk.ui.activities.login.LoginPresenterImpl;
import com.ehealthkiosk.kiosk.ui.activities.login.LoginView;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OTPActivity extends BaseActivity implements Validator.ValidationListener,OTPView, LoginView {

    @BindView(R.id.top_bar)
    Toolbar toolbar;

    @NotEmpty
    @BindView(R.id.et_otp)
    OtpEditText etOtp;
    @BindView(R.id.btn_otp)
    Button btnOtp;

    Validator mValidator;

    String mobileNo;
    int phoneCode;
    String phoneNo;

    private OTPPresenter otpPresenter;
    private LoginPresenter loginPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        showSystemUI();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        mobileNo = getIntent().getStringExtra(Constants.MOBILE_NO);
        phoneCode = getIntent().getIntExtra(Constants.PHONE_CODE,91);
        phoneNo = getIntent().getStringExtra(Constants.PHONE_NO);

        otpPresenter = new OTPPresenterImpl(this);
        loginPresenter = new LoginPresenterImpl(this);
    }

    @OnClick({R.id.btn_otp, R.id.btn_resend_otp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_otp:
                String otp = etOtp.getText().toString();
                System.out.println("Otp is *********"+otp);
                if (Common_Utils.isNotNullOrEmpty(otp) && otp.length() < 6) {
                    etOtp.setError(getResources().getString(R.string.valid_otp_check));
                    etOtp.requestFocus();
                    return;
                }

                mValidator.validate();
                break;
            case R.id.btn_resend_otp:
                LoginParam loginParam = new LoginParam();
                loginParam.setMobile(mobileNo);
                loginPresenter.login(loginParam);
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.finish();
    }

    @Override
    public void onValidationSucceeded() {

        String otp = etOtp.getText().toString();
        OTPParam otpParam = new OTPParam();
        otpParam.setMobile(mobileNo);
        otpParam.setOtp(otp);
        otpPresenter.verifyOTP(otpParam);

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void showProgress() {
        Common_Utils.showProgress(this);
    }


    @Override
    public void showError(String errMsg) {
        Common_Utils.hideProgress();
        Common_Utils.responseCodePromp(getWindow().getDecorView().getRootView(), errMsg);
    }

    @Override
    public void showOTPSuccess(String msg) {
        Common_Utils.showToast(msg);

        SharedPrefUtils.setPhoneCode(this, phoneCode);
        SharedPrefUtils.setPhoneNo(this, phoneNo);
        setResult(2);
        finish();
    }

    @Override
    public void showSuccess(String msg) {
        Common_Utils.showToast(msg);

    }

    @Override
    protected void onStop() {
        setResult(2);
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        setResult(2);
        super.onDestroy();
    }

    @Override
    public void showOTPError(String msg) {
        Common_Utils.hideProgress();
        Common_Utils.responseCodePromp(getWindow().getDecorView().getRootView(), msg);

    }

}
