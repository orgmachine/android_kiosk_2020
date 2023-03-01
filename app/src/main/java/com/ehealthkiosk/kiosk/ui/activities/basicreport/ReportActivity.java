package com.ehealthkiosk.kiosk.ui.activities.basicreport;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.generatereport.GenerateReportData;
import com.ehealthkiosk.kiosk.model.profilelist.ProfilesItem;
import com.ehealthkiosk.kiosk.ui.activities.BaseActivity;
import com.ehealthkiosk.kiosk.ui.activities.TestTypesActivity;
import com.ehealthkiosk.kiosk.ui.adapters.VerticalTabPagerAdapter;
import com.ehealthkiosk.kiosk.ui.fragments.reports.BloodPressureReportFragment;
import com.ehealthkiosk.kiosk.ui.fragments.reports.BodyStatsReportFragment;
import com.ehealthkiosk.kiosk.ui.fragments.reports.ECGReportFragment;
import com.ehealthkiosk.kiosk.ui.fragments.reports.PulseReportFragment;
import com.ehealthkiosk.kiosk.ui.fragments.reports.SpirometryReportFragment;
import com.ehealthkiosk.kiosk.ui.fragments.reports.TemperatureReportFragment;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import q.rorbin.verticaltablayout.VerticalTabLayout;

public class ReportActivity extends BaseActivity {

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;


    @BindView(R.id.top_bar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_health_score)
    TextView tvHealthScore;
    @BindView(R.id.img_smiley)
    ImageView imgSmiley;
    @BindView(R.id.tablayout)
    VerticalTabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.card_view)
    CardView cardView;
    @BindView(R.id.btn_print)
    Button btnPrint;
    @BindView(R.id.btn_email)
    Button btnEmail;
    Uri mUri = null;

    File mFile;

    private VerticalTabPagerAdapter mPagerAdapter;

    GenerateReportData generateReportData;

    ProfilesItem profilesItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarTitle.setText("Reports");

        generateReportData = getIntent().getParcelableExtra(Constants.REPORT_DATA);
        profilesItem = SharedPrefUtils.getProfile(HealthKioskApp.getAppContext());
        mPagerAdapter = new VerticalTabPagerAdapter(this, getSupportFragmentManager());

        tvName.setText(profilesItem.getName());
        tvGender.setText(Common_Utils.toCamelCase(profilesItem.getGender()));
        tvAge.setText(""+profilesItem.getAge());


        if (Common_Utils.isNotNullOrEmpty(String.valueOf(generateReportData.getReport().getBmi().getInference()))) {
            mPagerAdapter.addFragment(getBodyStatsReportFragment(), "BODY STATS");
        }

        if (Common_Utils.isNotNullOrEmpty(String.valueOf(generateReportData.getReport().getSystolic().getInference()))
                || Common_Utils.isNotNullOrEmpty(String.valueOf(generateReportData.getReport().getDiastolic().getInference()))) {
            mPagerAdapter.addFragment(getBloodPressureFragment(), "BLOOD PRESSURE");
        }
        if (Common_Utils.isNotNullOrEmpty(String.valueOf(generateReportData.getReport().getPulse().getInference()))
                || Common_Utils.isNotNullOrEmpty(String.valueOf(generateReportData.getReport().getOxygen_sat().getInference()))) {
            mPagerAdapter.addFragment(getPulseReportFragment(), "PULSE");
        }

        if (Common_Utils.isNotNullOrEmpty(String.valueOf(generateReportData.getReport().getTemperature().getInference()))) {
            mPagerAdapter.addFragment(getTemperatureReportFragment(), "TEMPERATURE");
        }
//        mPagerAdapter.addFragment(getSpirometryReportFragment(), "SPIROMETRY");
//        mPagerAdapter.addFragment(getECGReportFragment(), "ECG");

        viewpager.setAdapter(mPagerAdapter);

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        tablayout.setupWithViewPager(viewpager);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        String extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();
        File folder = new File(extStorageDirectory, "com.ehealthkiosk.kiosk.pdf");
        folder.mkdir();
        mFile = new File(folder, "Report_"+c+".pdf");
        try {
            mFile.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Common_Utils.DownloadFile(generateReportData.getPdf_link(), mFile);
    }

    public Fragment getBodyStatsReportFragment() {

        Fragment fragment;
        fragment = new BodyStatsReportFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.REPORT_DATA, generateReportData);
        fragment.setArguments(args);

        return fragment;
    }

    public Fragment getBloodPressureFragment() {

        Fragment fragment;
        fragment = new BloodPressureReportFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.REPORT_DATA, generateReportData);
        fragment.setArguments(args);

        return fragment;
    }

    public Fragment getPulseReportFragment() {
        Fragment fragment;
        fragment = new PulseReportFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.REPORT_DATA, generateReportData);
        fragment.setArguments(args);

        return fragment;
    }

    public Fragment getTemperatureReportFragment() {
        Fragment fragment;
        fragment = new TemperatureReportFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.REPORT_DATA, generateReportData);
        fragment.setArguments(args);

        return fragment;
    }

    public Fragment getSpirometryReportFragment() {
        Fragment fragment;
        fragment = new SpirometryReportFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.REPORT_DATA, generateReportData);
        fragment.setArguments(args);

        return fragment;
    }

    public Fragment getECGReportFragment() {
        Fragment fragment;
        fragment = new ECGReportFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.REPORT_DATA, generateReportData);
        fragment.setArguments(args);

        return fragment;
    }

    @OnClick({R.id.btn_print, R.id.btn_email})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_print:
                if (checkPermission(this, STORAGE_PERMISSION_REQUEST_CODE, view)) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(generateReportData.getPdf_link())));
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                }

                break;
            case R.id.btn_email:
                if (Common_Utils.isNotNullOrEmpty(profilesItem.getEmail())){
                    sendEmail(profilesItem.getEmail());
                }else{
                    openEmailDialog();
                }
                break;
        }
    }

    public void requestPermission(final Activity activity, int request_code, View view) {
        switch (request_code) {
            case 1:
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, activity.getString(R.string.permission_request_message), Snackbar.LENGTH_LONG).setAction("Allow", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(activity, activity.getString(R.string.permission_request_message), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        }
                    }).show();

                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, request_code);
                }
                break;
            default:
                break;
        }
    }

    public boolean checkPermission(Activity activity, int request_code, View view) {
        int result = 0;
        switch (request_code) {
            case 1:
                result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            default:
                break;
        }

        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermission(activity, request_code, view);
            return false;
        }
    }

    void openEmailDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_offline_email, viewGroup, false);
        Button btnEnter = dialogView.findViewById(R.id.btn_send);
        final EditText etEmail = dialogView.findViewById(R.id.et_email);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etEmail.getText().toString()) ) {
                    Toast.makeText(ReportActivity.this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                alertDialog.dismiss();
                sendEmail(etEmail.getText().toString());
            }
        });
    }

    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public void sendEmail(String email)
    {
        try
        {
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = df.format(c);

            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { email });
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,profilesItem.getName() +" Basic Report - "+formattedDate);
            //emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mFile));
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Click on the link to view your report - "+generateReportData.getPdf_link());
            this.startActivity(Intent.createChooser(emailIntent,"Sending email..."));
        }
        catch (Throwable t)
        {
            Toast.makeText(this, "Request failed try again: " + t.toString(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);

        MenuItem item1 = menu.findItem(R.id.action_logout);
        item1.setVisible(false);

        MenuItem item2 = menu.findItem(R.id.action_profile);
        item2.setVisible(false);

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, TestTypesActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(this, TestTypesActivity.class);
        startActivity(i);
        finish();
        return super.onOptionsItemSelected(item);
    }
}
