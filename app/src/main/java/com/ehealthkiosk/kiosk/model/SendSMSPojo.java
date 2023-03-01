package com.ehealthkiosk.kiosk.model;

public class SendSMSPojo {

    private String report_url;

    public SendSMSPojo(String report_url) {
        this.report_url = report_url;
    }

    public String getReport_url() {
        return report_url;
    }

    public void setReport_url(String report_url) {
        this.report_url = report_url;
    }
}
