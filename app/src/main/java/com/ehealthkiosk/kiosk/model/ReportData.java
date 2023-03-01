
package com.ehealthkiosk.kiosk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReportData {

    @SerializedName("report_list")
    @Expose
    private List<ReportItem> reportData = null;

    public List<ReportItem> getReportData() {
        return reportData;
    }

    public void setVitalData(List<ReportItem> reportData) {
        this.reportData = reportData;
    }

}
