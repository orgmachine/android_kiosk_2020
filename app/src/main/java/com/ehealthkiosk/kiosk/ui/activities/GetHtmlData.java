package com.ehealthkiosk.kiosk.ui.activities;

public class GetHtmlData {
    private String pdf_link;
    private String pdf_html;
    private Integer report_id;

    public String getPdfLink() {
        return pdf_link;
    }

    public void setPdfLink(String pdf_link) {
        this.pdf_link = pdf_link;
    }

    public String getPdfHtml() {
        return pdf_html;
    }

    public void setPdfHtml(String pdf_html) {
        this.pdf_html = pdf_html;
    }

    public Integer getReportId() {
        return report_id;
    }

    public void setReportId(Integer report_id) {
        this.report_id = report_id;
    }
}
