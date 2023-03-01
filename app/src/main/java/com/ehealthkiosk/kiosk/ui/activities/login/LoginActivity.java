package com.ehealthkiosk.kiosk.ui.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.login.LoginParam;
import com.ehealthkiosk.kiosk.ui.activities.BaseActivity;
import com.ehealthkiosk.kiosk.ui.activities.otp.OTPActivity;
import com.ehealthkiosk.kiosk.ui.activities.selectprofile.SelectProfileActivity;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements Validator.ValidationListener,LoginView {

    @BindView(R.id.top_bar)
    Toolbar toolbar;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.country_picker)
    CountryCodePicker countryPicker;

    @NotEmpty
    @BindView(R.id.et_login_number)
    EditText etLoginNumber;

    Validator mValidator;
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        toolbar = findViewById(R.id.top_bar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        showSystemUI();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        loginPresenter = new LoginPresenterImpl(this);

        countryPicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {

            }
        });
    }

    @OnClick({R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.btn_login:
                String phone = etLoginNumber.getText().toString();
                if (Common_Utils.isNotNullOrEmpty(phone) && phone.length() < 10) {
                    etLoginNumber.setError("Please enter valid number");
                    etLoginNumber.requestFocus();
                    return;
                }

                mValidator.validate();
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
        String phone = etLoginNumber.getText().toString();
        LoginParam loginParam = new LoginParam();
        loginParam.setMobile(countryPicker.getSelectedCountryCodeWithPlus()+phone);
        loginPresenter.login(loginParam);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Common_Utils.showError(this, errors);
    }

    @Override
    public void showProgress() {
        Common_Utils.showProgress(this);
    }

    @Override
    public void showSuccess(String msg) {
        Common_Utils.showToast(msg);
        String phone = etLoginNumber.getText().toString();
        Intent i = new Intent(this, OTPActivity.class);
        i.putExtra(Constants.MOBILE_NO,countryPicker.getSelectedCountryCodeWithPlus()+phone);
        i.putExtra(Constants.PHONE_CODE,countryPicker.getSelectedCountryCodeAsInt());
        i.putExtra(Constants.PHONE_NO,phone);
        startActivityForResult(i,2);
    }

    @Override
    public void showError(String errMsg) {
        Common_Utils.hideProgress();
        Common_Utils.responseCodePromp(getWindow().getDecorView().getRootView(), errMsg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == 2) {
            Intent i = new Intent(this, SelectProfileActivity.class);
            startActivity(i);
            finish();
        }
    }
}
