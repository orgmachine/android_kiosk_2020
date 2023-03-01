package com.ehealthkiosk.kiosk.ui.activities.generatereport;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.generatereport.GenerateReportData;
import com.ehealthkiosk.kiosk.model.generatereport.GenerateReportParam;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import static com.ehealthkiosk.kiosk.HealthKioskApp.context;

public class GenerateReportPresenterImpl implements GenerateReportPresenter, GenerateReportInteractor.GenerateReportViewListener {

    private GenerateReportView generateReportView;
    private GenerateReportInteractor generateReportInteractor;

    public GenerateReportPresenterImpl(GenerateReportView generateReportView) {
        this.generateReportView = generateReportView;
        this.generateReportInteractor = new GenerateReportInteractorImpl();
    }
    @Override
    public void onSuccess(GenerateReportData generateReportData, String msg) {
        if(generateReportView!=null)
            generateReportView.showSuccess(generateReportData,msg);
    }

    @Override
    public void onError(String msg) {
        if(generateReportView!=null)
            generateReportView.showError(msg);
    }

    @Override
    public void generateReport(GenerateReportParam generateReportParam) {
        if (!Common_Utils.isNetworkAvailable()) {
            Common_Utils.showToast(context.getResources().getString(R.string.no_internet));
            return;
        }


        if(generateReportView!=null)
            generateReportView.showProgress();

        generateReportInteractor.generateReport(generateReportParam,this);
    }
}
