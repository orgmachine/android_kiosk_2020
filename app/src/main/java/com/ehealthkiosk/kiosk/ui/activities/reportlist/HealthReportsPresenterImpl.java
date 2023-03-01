package com.ehealthkiosk.kiosk.ui.activities.reportlist;


import com.ehealthkiosk.kiosk.model.healthreports.Report;
import com.ehealthkiosk.kiosk.model.healthreports.ReportsParam;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;

import java.util.List;

public class HealthReportsPresenterImpl implements HealthReportsPresenter, HealthReportsInteractor.HealthReportsChangeListener {

    private HealthReportsView healthReportsView;
    private HealthReportsInteractor healthReportsInteractor;

    public HealthReportsPresenterImpl(HealthReportsView healthReportsView) {
        this.healthReportsView = healthReportsView;
        this.healthReportsInteractor = new HealthReportsInteractorImpl();
    }

    @Override
    public void getHealthReports(ReportsParam reportsParam, String from){
        if(Common_Utils.isNetworkAvailable()){
            if(healthReportsView!=null &&
                    from.equalsIgnoreCase(Constants.ONCREATE)) {
                healthReportsView.showProgress();
            }
            healthReportsInteractor.getHealthReports(reportsParam,this, from);

        }else{
            if(healthReportsView!=null)
                healthReportsView.showNetworkError(Constants.NO_INTERNET_CONNECTION);
        }
    }

    @Override
    public void onError(String msg) {
        if(healthReportsView!=null)
            healthReportsView.showError(msg);
    }

    @Override
    public void onReportsList(List<Report> reportList, int offset, String from) {
        if(healthReportsView!=null){
            healthReportsView.getHealthReports(reportList,offset,from);
        }

    }
}
