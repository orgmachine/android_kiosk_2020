package com.ehealthkiosk.kiosk.ui.activities.reportlist;


import com.ehealthkiosk.kiosk.model.healthreports.Report;

import java.util.List;

public interface HealthReportsView {
    void showError(String msg);
    void showNetworkError(String msg);
    void showProgress();
    void getHealthReports(List<Report> reportList, int offset, String from);
}
