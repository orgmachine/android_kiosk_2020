package com.ehealthkiosk.kiosk.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportItem implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("color_code")
    @Expose
    private String colorCode;
    @SerializedName("report_url")
    @Expose
    private String reportUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.date);
        dest.writeString(this.type);
        dest.writeString(this.colorCode);
        dest.writeString(this.reportUrl);
    }

    public ReportItem() {
    }

    protected ReportItem(Parcel in) {
        this.name = in.readString();
        this.date = in.readString();
        this.type = in.readString();
        this.colorCode = in.readString();
        this.reportUrl = in.readString();
    }

    public static final Parcelable.Creator<ReportItem> CREATOR = new Parcelable.Creator<ReportItem>() {
        @Override
        public ReportItem createFromParcel(Parcel source) {
            return new ReportItem(source);
        }

        @Override
        public ReportItem[] newArray(int size) {
            return new ReportItem[size];
        }
    };
}
