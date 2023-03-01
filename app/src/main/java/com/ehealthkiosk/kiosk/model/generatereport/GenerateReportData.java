package com.ehealthkiosk.kiosk.model.generatereport;

import android.os.Parcel;
import android.os.Parcelable;

public class GenerateReportData implements Parcelable {
    /**
     * report : {"bmi":{"value":23.6,"inference":"Normal","color":"#0ac680"},"hydration":{"value":58.05,"inference":"Normal","color":"#0ac680"},"fat":{"value":20.45,"inference":"Acceptable","color":"#ddbe24"},"bonemass":{"value":4.37,"inference":"Normal","color":"#0ac680"},"muscle":{"value":41.9,"inference":"Normal","color":"#0ac680"},"systolic":{"value":120,"inference":"Normal","color":"#0ac680"},"diastolic":{"value":80,"inference":"Hypertension I","color":"#c60a0a"},"temperature":{"value":99,"inference":"High","color":"#c60a0a"},"pulse":{"value":80,"inference":"Normal","color":"#0ac680"},"oxygen_sat":{"value":120,"inference":"Normal","color":"#0ac680"}}
     * pdf_link : http://ec2-13-235-2-112.ap-south-1.compute.amazonaws.com/media/basic_report_11.pdf
     */

    private GenerateReport report;
    private String pdf_link;
    private String pdf_html;
    private Integer report_id;
    private Float height;
    private Float weight;

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public GenerateReport getReport() {
        return report;
    }

    public void setReport(GenerateReport report) {
        this.report = report;
    }

    public String getPdf_link() {
        return pdf_link;
    }

    public void setPdf_link(String pdf_link) {
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



    public GenerateReportData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.report, flags);
        dest.writeString(this.pdf_link);
        dest.writeString(this.pdf_html);
        dest.writeValue(this.height);
        dest.writeValue(this.weight);
    }

    protected GenerateReportData(Parcel in) {
        this.report = in.readParcelable(GenerateReport.class.getClassLoader());
        this.pdf_link = in.readString();
        this.pdf_html = in.readString();
        this.height = (Float) in.readValue(Float.class.getClassLoader());
        this.weight = (Float) in.readValue(Float.class.getClassLoader());
    }

    public static final Creator<GenerateReportData> CREATOR = new Creator<GenerateReportData>() {
        @Override
        public GenerateReportData createFromParcel(Parcel source) {
            return new GenerateReportData(source);
        }

        @Override
        public GenerateReportData[] newArray(int size) {
            return new GenerateReportData[size];
        }
    };
}
