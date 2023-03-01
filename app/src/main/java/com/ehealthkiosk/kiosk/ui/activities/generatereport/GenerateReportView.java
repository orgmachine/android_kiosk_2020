package com.ehealthkiosk.kiosk.ui.activities.generatereport;

import com.ehealthkiosk.kiosk.model.generatereport.GenerateReportData;


public interface GenerateReportView {

    void showProgress();
    void showSuccess(GenerateReportData generateReportData, String msg);
    void showError(String msg);

}
