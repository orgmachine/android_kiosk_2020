package com.ehealthkiosk.kiosk.model.sendDermaImage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class sendImageResultData {


    @SerializedName("pdf_link")
    @Expose
    private String pdf_link;
    @SerializedName("image_url")
    @Expose
    private String image_url;
    @SerializedName("pdf_html")
    @Expose
    private String pdf_html;
    @SerializedName("report_id")
    @Expose
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

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
