package com.ehealthkiosk.kiosk.ui.activities.register;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.profilelist.ProfilesItem;
import com.ehealthkiosk.kiosk.model.register.RegisterParam;
import com.ehealthkiosk.kiosk.ui.activities.BaseActivity;
import com.ehealthkiosk.kiosk.ui.activities.MainActivity;
import com.ehealthkiosk.kiosk.ui.activities.TestTypesActivity;
import com.ehealthkiosk.kiosk.ui.activities.otp.OTPActivity;
import com.ehealthkiosk.kiosk.ui.activities.selectprofile.SelectProfileActivity;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class RegisterActivity extends BaseActivity implements Validator.ValidationListener, RegisterView {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.top_bar)
    Toolbar toolbar;
    @BindView(R.id.ll_email)
    LinearLayout emailLL;
    @BindView(R.id.ll_mobile)
    LinearLayout mobileLL;

    @NotEmpty
    @BindView(R.id.et_name)
    EditText etName;
    @NotEmpty
    @BindView(R.id.et_age)
    EditText etAge;
    @NotEmpty
    @BindView(R.id.et_mobile)
    EditText etMobile;
    @BindView(R.id.country_picker)
    CountryCodePicker countryPicker;

    @BindView(R.id.et_email)
    EditText etEmail;

    @BindView(R.id.btn_continue_guest)
    Button btnContinueGuest;
    @BindView(R.id.btn_register)
    Button btnRegister;
    DatePickerDialog picker;
    String from = "";

    Validator mValidator;
//    @BindView(R.id.btn_select_profile)
//    Button btnSelectProfile;
//    @BindView(R.id.profile_image)
//    ImageView profileImage;

    @Checked(message = "You must agree to the terms.")
    @BindView(R.id.terms_checkbox)
    CheckBox termsCheckbox;

    RegisterPresenter registerPresenter;
    @BindView(R.id.rb_male)
    RadioButton rbMale;
    @BindView(R.id.rb_female)
    RadioButton rbFemale;
    @BindView(R.id.rb_other)
    RadioButton rbOther;
    @BindView(R.id.rg_gender)
    RadioGroup rgGender;
    @BindView(R.id.btn_terms_conditions)
    Button btnTermsConditions;
    @BindView(R.id.tnc)
    TextView tnc;
    @BindView(R.id.tv_name_lbl)
    TextView tvNameLbl;
    @BindView(R.id.tv_age_lbl)
    TextView tvAgeLbl;
    @BindView(R.id.tv_mobile_lbl)
    TextView tvMobileLbl;
    @BindView(R.id.tv_email_lbl)
    TextView tvEmailLbl;
    @BindView(R.id.tv_gender_lbl)
    TextView tvGenderLbl;

    Boolean isRailwayEmployee = false;

    private File fileImage;

    String gender = "male";

    PhoneNumberUtil util;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        util = PhoneNumberUtil.createInstance(getApplicationContext());

        registerPresenter = new RegisterPresenterImpl(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        showSystemUI();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        from = getIntent().getStringExtra(Constants.FROM);

        if (from.equalsIgnoreCase(Constants.ADD_PROFILE)) {
            btnRegister.setText("Add");
            etMobile.setText(SharedPrefUtils.getPhoneNo(this));
            countryPicker.setCountryForPhoneCode(SharedPrefUtils.getPhoneCode(this));
            etMobile.setClickable(false);
            etMobile.setFocusable(false);
            countryPicker.setEnabled(false);
            countryPicker.setClickable(false);
            emailLL.setVisibility(View.GONE);
            mobileLL.setVisibility(View.GONE);
        } else {
            btnRegister.setText("Register");
        }
        etAge.setInputType(InputType.TYPE_NULL);

        etAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String day = "" + dayOfMonth;
                                if (dayOfMonth < 10){
                                    day = "0" + dayOfMonth;
                                }
                                String month = "" + (monthOfYear + 1);
                                if (monthOfYear < 9){
                                    month = "0" + (monthOfYear + 1);
                                }
                                etAge.setText(year + "-" +month + "-" + day);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        String emailInp = etEmail.getText().toString();
        if (emailInp != null && !emailInp.equals("")) {
            DeviceIdPrefHelper.setUserEmail(this, Constants.USER_EMAIL, emailInp);
        }

        countryPicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {

            }
        });
        TextView railwayEmpLabel = findViewById(R.id.railway_employee);

        CheckBox railwayEmployee = findViewById(R.id.check_railway_employee);
//        railwayEmployee.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (railwayEmployee.isChecked()) {
//                    DeviceIdPrefHelper.setrailwayEmployee(RegisterActivity.this, true);
//                    isRailwayEmployee = true;
//                } else if (!railwayEmployee.isChecked()) {
//                    DeviceIdPrefHelper.setrailwayEmployee(RegisterActivity.this, false);
//                    isRailwayEmployee = false;
//                }
//            }
//        });

        railwayEmployee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                DeviceIdPrefHelper.setrailwayEmployee(RegisterActivity.this, isChecked);
                isRailwayEmployee = isChecked;
                Log.d("pranav", "checked " + isRailwayEmployee);
            }
        });

        railwayEmpLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                railwayEmployee.toggle();
            }
        });


        Spannable word = new SpannableString("Name*");
        word.setSpan(new ForegroundColorSpan(Color.RED), 4, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvNameLbl.setText(word);
        Spannable word1 = new SpannableString("Date Of Birth*");
        word1.setSpan(new ForegroundColorSpan(Color.RED), 13, word1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvAgeLbl.setText(word1);

        Spannable word2 = new SpannableString("Gender*");

        word2.setSpan(new ForegroundColorSpan(Color.RED), 6, word2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvGenderLbl.setText(word2);

        Spannable word3 = new SpannableString("Mobile*");

        word3.setSpan(new ForegroundColorSpan(Color.BLUE), 6, word3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvMobileLbl.setText(word3);

//        Spannable word4 = new SpannableString("I agree to Terms and conditions*");
//
//        word4.setSpan(new ForegroundColorSpan(Color.BLUE), 31, word4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        btnTermsConditions.setText(word4);


    }

    @OnCheckedChanged({R.id.rb_male, R.id.rb_female, R.id.rb_other})
    public void onRadioButtonCheckChanged(CompoundButton button, boolean checked) {
        if (checked) {
            switch (button.getId()) {
                case R.id.rb_male:
                    gender = "Male";
                    break;
                case R.id.rb_female:
                    gender = "Female";
                    break;
                case R.id.rb_other:
                    gender = "Other";
                    break;
            }
        }
    }

    @OnClick({R.id.btn_continue_guest, R.id.btn_register, R.id.tnc})
    public void onViewClicked(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.btn_continue_guest:
                validateGuest();
                break;
            case R.id.btn_register:
                mValidator.validate();
                break;
            case R.id.tnc:
                Common_Utils.openLinkInBrowser(this, "https://home.yolohealth.co.in/termsandcondition");
                break;
        }
    }

    void validateGuest() {

        String name = etName.getText().toString();
        String age = etAge.getText().toString();
        String email = etEmail.getText().toString();

        String mobileNo = etMobile.getText().toString();


        String fullMobile = countryPicker.getSelectedCountryCodeWithPlus() + mobileNo;
        try {
            final Phonenumber.PhoneNumber phoneNumber = util.parse(fullMobile, "IN");
        } catch (NumberParseException e) {
            Toast.makeText(this, "Please enter valid phone number.", Toast.LENGTH_SHORT).show();
            return;
        }

        String gender = "";
        if (!Common_Utils.isNotNullOrEmpty(name) || name.length() < 3) {
            Toast.makeText(this, "Please enter valid name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Common_Utils.isNotNullOrEmpty(age) || Integer.parseInt(age) <= 0 || Integer.parseInt(age) >= 100) {
            Toast.makeText(this, "Please enter valid age.", Toast.LENGTH_SHORT).show();
            return;
        }


//        if (!termsCheckbox.isChecked()) {
//            Toast.makeText(this, "You must agree to the terms.", Toast.LENGTH_SHORT).show();
//            return;
//        }

        ProfilesItem profilesItem = new ProfilesItem();
        profilesItem.setName(name);
        profilesItem.setAge(Integer.parseInt(age));
        profilesItem.setEmail(email);
        if (isRailwayEmployee) {
            profilesItem.setRailwayEmployeeCheck(true);
        }

        if (rbMale.isChecked()) {
            gender = "Male";
        } else if (rbFemale.isChecked()) {
            gender = "Female";
        } else if (rbOther.isChecked()) {
            gender = "Other";
        } else {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        profilesItem.setGender(gender);

        Intent i = new Intent(this, TestTypesActivity.class);
        SharedPrefUtils.setProfileId(HealthKioskApp.getAppContext(), "guest");
        SharedPrefUtils.setProfile(HealthKioskApp.getAppContext(), profilesItem);
        startActivity(i);
        finish();
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
        if (from.equalsIgnoreCase(Constants.ADD_PROFILE)) {
            Intent i = new Intent(this, SelectProfileActivity.class);
            startActivity(i);
            this.finish();
        } else {
            this.finish();
        }
    }

    @Override
    public void onValidationSucceeded() {
        String mobileNo = etMobile.getText().toString();
        String fullMobile = countryPicker.getSelectedCountryCodeWithPlus() + mobileNo;
        try {
            final Phonenumber.PhoneNumber phoneNumber = util.parse(fullMobile, "IN");
        } catch (NumberParseException e) {
            Toast.makeText(this, "Please enter valid phone number.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etName.getText().toString();
        String age = etAge.getText().toString();
        String email = etEmail.getText().toString();
        mobileNo = etMobile.getText().toString();


        RegisterParam registerParam = new RegisterParam();
        registerParam.setMobile(countryPicker.getSelectedCountryCodeWithPlus() + mobileNo);
        registerParam.setName(name);
        registerParam.setAge(age);
        registerParam.setEmail(email);
        registerParam.setPhoto(fileImage);
        registerParam.setGender(gender);
        if (isRailwayEmployee == true) {
            registerParam.setRailwayEmployeeCheck(true);
        } else {
            registerParam.setRailwayEmployeeCheck(false);
        }

        if (from.equalsIgnoreCase(Constants.ADD_PROFILE)) {
            registerPresenter.addProfile(registerParam);
        } else {
            registerPresenter.register(registerParam);
        }
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

    private void selectImage_dialog() {
        ListView list_view;
        String[] items = new String[0];
        /** condition added to hide remove buttom*/

        items = new String[]{"Camera", "Photo Gallery"};


        AlertDialog.Builder _builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        _builder.setTitle("Choose an image action");
        LayoutInflater layoutinflater = LayoutInflater.from(this);
        View promptView = layoutinflater.inflate(
                R.layout.custom_dialog_setcamera, null);
        _builder.setView(promptView);

        list_view = (ListView) promptView.findViewById(R.id.listview_camera);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        list_view.setAdapter(adapter);


        _builder.setNegativeButton(Constants.DIALOG_DISMISS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        _builder.setCancelable(false);
        final AlertDialog dialog = _builder.create();
        dialog.show();


        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                getPermissionInfo(position, dialog);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String imgUrl;
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {

                try {
                    imgUrl = createImageFile().getAbsolutePath();
//                    setImage(imgUrl);
                    //startCropImage(imgUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == SELECT_FILE) {

                try {
                    Uri selectedImageUri = data == null ? null : data.getData();

                    copyStream(selectedImageUri);
                    imgUrl = createImageFile().getAbsolutePath();
                    //System.out.println(("gallary:" + imgUrl);
                    if (imgUrl.startsWith("/storage")) {
//                        setImage(imgUrl);
                        //startCropImage(imgUrl);
                    } else {
//                        profileImage.setImageResource(R.drawable.ic_user);
                        Common_Utils.showToast(Constants.IMAGE_DOESNOT_EXIST);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        } else if (requestCode == 2 && resultCode == 2) {
            Intent i = new Intent(this, SelectProfileActivity.class);
            i.putExtra(Constants.FROM, "Register");
            startActivity(i);
            finish();
        }
    }

//    private void setImage(String url) throws Exception {
//        Bitmap bitmap;
//
//        bitmap = getRotatedBitmap(url);
//        fileImage = createDir(bitmap);
//        if (fileImage != null) {
//            profileImage.setImageResource(R.drawable.ic_user);
//
//            url = fileImage.getAbsolutePath();
//            Common_Utils.getCircularImageFromLocal(profileImage, url, R.drawable.ic_user);
//        }
//    }

    @Override
    public void showProgress() {
        Common_Utils.showProgress(this);
    }

    @Override
    public void showSuccess(String msg) {
        if (from.equalsIgnoreCase(Constants.ADD_PROFILE)) {
            Intent i = new Intent(this, SelectProfileActivity.class);
            startActivity(i);
            finish();
        } else {
            String phone = etMobile.getText().toString();
            Intent i = new Intent(this, OTPActivity.class);
            i.putExtra(Constants.MOBILE_NO, countryPicker.getSelectedCountryCodeWithPlus() + phone);
            i.putExtra(Constants.PHONE_CODE, countryPicker.getSelectedCountryCodeAsInt());
            i.putExtra(Constants.PHONE_NO, phone);
            startActivityForResult(i, 2);
        }
    }

    @Override
    public void showError(String msg) {
        Common_Utils.hideProgress();
        Common_Utils.responseCodePromp(getWindow().getDecorView().getRootView(), msg);
    }

    @Override
    protected void onStop() {
        if (from.equalsIgnoreCase(Constants.ADD_PROFILE)) {
            setResult(3);
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (from.equalsIgnoreCase(Constants.ADD_PROFILE)) {
            setResult(3);
        }
        super.onDestroy();
    }

}
