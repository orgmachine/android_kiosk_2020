package com.ehealthkiosk.kiosk.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestInterface;
import com.ehealthkiosk.kiosk.model.PrintParam;
import com.ehealthkiosk.kiosk.model.SendEmailPojo;
import com.ehealthkiosk.kiosk.model.SendSMSPojo;
import com.ehealthkiosk.kiosk.model.commonresponse.Status;
import com.ehealthkiosk.kiosk.ui.activities.reportlist.ReportsListActivity;
import com.ehealthkiosk.kiosk.ui.fragments.SendReportPresenter;
import com.ehealthkiosk.kiosk.ui.fragments.SendReportPresenterImpl;
import com.ehealthkiosk.kiosk.ui.fragments.SendReportView;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PDFViewActivity extends BaseActivity implements SendReportView, GetReportHtmlView, SendSmsView {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.top_bar)
    Toolbar toolbar;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.fab_print)
    FloatingActionButton printAction;
    @BindView(R.id.fab_email)
    FloatingActionButton emailAction;
    @BindView(R.id.print_webView)
    WebView printWebview;
    @BindView(R.id.fab_retry)
    FloatingActionButton btnRetry;
    @BindView(R.id.fab_sms)
    FloatingActionButton btnsms;

    private float m_downX;
    String emailInp;
    private String path = "", pdfHtml = "";
    private Integer reportId;
    String pdfType = "Report";

    boolean isFromReports = false;
    private SendReportPresenter sendReportPresenter;
    private SendSmsPresenter sendSmsPresenter;

    public static final String PDF_TYPE = "prescription";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_pdfview);
        ButterKnife.bind(this);
        showSystemUI();
        if(getIntent().getStringExtra("from") !=null && getIntent().getStringExtra("from").equals("report_list")){
            isFromReports = true;
        }

        if (getIntent().getStringExtra(Constants.PDF_PATH) != null) {
            reportId = getIntent().getIntExtra(Constants.REPORT_DATA, 0);
            path = getIntent().getStringExtra(Constants.PDF_PATH);
            if (getIntent().hasExtra(PDF_TYPE) ){
                pdfType = getIntent().getStringExtra(PDF_TYPE);
            }else {
                pdfType = "Report";
            }

            Log.d("TAG", path);
        }

//        if(pdfType.equals("Prescription")){
//            emailAction.setVisibility(View.GONE);
//            btnsms.setVisibility(View.GONE);
            //printAction.setVisibility(View.GONE);
//        }

        printAction.show();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbarTitle.setText("View " + pdfType);
        }


        emailAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DeviceIdPrefHelper.getUserEmail(PDFViewActivity.this, Constants.USER_EMAIL) != null &&
                        !DeviceIdPrefHelper.getUserEmail(PDFViewActivity.this, Constants.USER_EMAIL).equals("")) {
                    sendReportOnEmail(DeviceIdPrefHelper.getUserEmail(PDFViewActivity.this, Constants.USER_EMAIL));
                } else {
                    openGetEmailDialog();
                }
            }
        });

        btnsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSmsPresenter = new SendSmsPresenterImpl(PDFViewActivity.this);
                SendSMSPojo sendSMSPojo = new SendSMSPojo(path);
                sendSmsPresenter.sendMessage(sendSMSPojo);
            }
        });
        callPdfView();


        printAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DeviceIdPrefHelper.getWifiPrinter(PDFViewActivity.this, true)) {
                    createWebPrintJob();
                } else if (DeviceIdPrefHelper.getRaspberryPi(PDFViewActivity.this, true)) {
                    callPrintAPI(path);
                } else {
                    Toast.makeText(PDFViewActivity.this, "Please select one option for printer to function", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPdfView();
            }
        });
    }

    private void callPrintAPI(String url) {
        //Common_Utils.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DeviceIdPrefHelper.getPrintUrl(this, Constants.PRINT_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestInterface service = retrofit.create(RestInterface.class);

        PrintParam pp = new PrintParam();
        pp.setUrl(url);
        Call<Status> call = service.print(pp);
        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                Toast.makeText(PDFViewActivity.this, "Print Job Initiated", Toast.LENGTH_SHORT).show();
                //Common_Utils.hideProgress();
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                Toast.makeText(PDFViewActivity.this, "Print Job failed", Toast.LENGTH_SHORT).show();
                //Common_Utils.hideProgress();
            }
        });

    }

    private void createWebPrintJob() {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        String jobName = getString(R.string.app_name) + " Document";

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = printWebview.createPrintDocumentAdapter(jobName);

        // Create a print job with name and adapter instance
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());

        Toast.makeText(getApplicationContext(), "Print Inititated" + printJob, Toast.LENGTH_SHORT).show();

        // Save the job object for later status checking
//        printJobs.add(printJob);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    void sendReportOnEmail(String email) {
        sendReportPresenter = new SendReportPresenterImpl(PDFViewActivity.this);
        SendEmailPojo sendEmailpojo = new SendEmailPojo();

        sendEmailpojo.setName("User");
        sendEmailpojo.setUrl(path);
        sendEmailpojo.setProfile_id(SharedPrefUtils.getProfile(this).getId());

        sendEmailpojo.setEmail(email);
        sendReportPresenter.sendEmail(sendEmailpojo);
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

    private void callPdfView() {
        Common_Utils.showProgress(this);
        initWebView();
        webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + path);
        Log.d("TAG", "https://drive.google.com/viewerng/viewer?embedded=true&url=" + path);
//        webView.loadDataWithBaseURL(null, path, "text/HTML", "UTF-8", null);
    }



    private void initWebView() {
        webView.setWebChromeClient(new MyWebChromeClient(this));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("pranav", "page load started");
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                try {
                    Common_Utils.showProgress(PDFViewActivity.this);
                }catch (Exception e){

                }
                invalidateOptionsMenu();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("pranav", "should override");
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (view.getTitle().equals("")) {
                    view.reload();
                    return;
                }
                Log.d("pranav", "page load finished");
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                //invalidateOptionsMenu();
                try {
                    Common_Utils.hideProgress();
                }catch (Exception e){

                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                Log.d("pranav", "error " + error.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    Common_Utils.hideProgress();
                }catch (Exception e){

                }
                Common_Utils.showToast("Please refresh. Unable to reach servers");
                //invalidateOptionsMenu();
            }
        });
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
    }

    @Override
    public void showProgress() {
        //
        // Common_Utils.showProgress(this);
    }

    @Override
    public void showSuccess(String msg) {
        Toast.makeText(this, "" + msg,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError(String errMsg) {
        Common_Utils.responseCodePromp(getWindow().getDecorView().getRootView(), errMsg);
    }

    @Override
    public void showApiProgress() {

        //Common_Utils.showProgress(this);
    }

    @Override
    public void showApiSuccess(GetHtmlData getHtmlData, String msg) {

    }

    @Override
    public void showApiError(String errMsg) {
        Common_Utils.responseCodePromp(getWindow().getDecorView().getRootView(), errMsg);
    }

    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        public MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }
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
        if(isFromReports){
            Intent intent = new Intent(this, ReportsListActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if(pdfType.equals("Prescription")){
            finish();

        }else {
            Intent intent = new Intent(this, TestTypesActivity.class);
            startActivity(intent);
            finish();
        }
    }

    void openGetEmailDialog() {

        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);


        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_get_email, viewGroup, false);
        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();


        Button btnEnter = dialogView.findViewById(R.id.btn_enter);
        ImageView cancelDialog = dialogView.findViewById(R.id.dialog_cancel);
        EditText email = dialogView.findViewById(R.id.et_email);
        emailInp = email.getText().toString();
        email.setText(emailInp);


        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email.getText().toString().equals("")) {
                    DeviceIdPrefHelper.setUserEmail(PDFViewActivity.this, Constants.USER_EMAIL, email.getText().toString());
                    sendReportOnEmail(email.getText().toString());
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(PDFViewActivity.this,
                            getResources().getString(R.string.email_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}

