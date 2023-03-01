package com.ehealthkiosk.kiosk.ui.activities.reportlist;

import com.ehealthkiosk.kiosk.model.healthreports.ReportsParam;

public interface HealthReportsPresenter {
    void getHealthReports(ReportsParam reportsParam, String from);
}
