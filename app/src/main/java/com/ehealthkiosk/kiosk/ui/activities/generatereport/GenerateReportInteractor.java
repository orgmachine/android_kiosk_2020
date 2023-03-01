package com.ehealthkiosk.kiosk.ui.activities.generatereport;

import com.ehealthkiosk.kiosk.model.generatereport.GenerateReportData;
import com.ehealthkiosk.kiosk.model.generatereport.GenerateReportParam;


public interface GenerateReportInteractor {
    interface GenerateReportViewListener{
        void onSuccess(GenerateReportData generateReportData, String msg);
        void onError(String msg);
    }

    void generateReport(GenerateReportParam generateReportParam, GenerateReportInteractor.GenerateReportViewListener changeListener);
}
