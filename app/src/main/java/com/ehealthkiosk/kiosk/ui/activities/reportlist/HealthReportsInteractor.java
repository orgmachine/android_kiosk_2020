package com.ehealthkiosk.kiosk.ui.activities.reportlist;


import com.ehealthkiosk.kiosk.model.healthreports.Report;
import com.ehealthkiosk.kiosk.model.healthreports.ReportsParam;

import java.util.List;

public interface HealthReportsInteractor {

    interface HealthReportsChangeListener {
        void onError(String msg);

        void onReportsList(List<Report> reportList, int offset, String from);
    }

    void getHealthReports(ReportsParam reportsParam, HealthReportsChangeListener listener, String from);
}
