package com.ehealthkiosk.kiosk.model.generatereport;

import android.os.Parcel;
import android.os.Parcelable;

public class OxygenSatReport implements Parcelable {
    /**
     * value : 120
     * inference : Normal
     * color : #0ac680
     */

    private double value;
    private String inference;
    private String color;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getInference() {
        return inference;
    }

    public void setInference(String inference) {
        this.inference = inference;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.value);
        dest.writeString(this.inference);
        dest.writeString(this.color);
    }

    public OxygenSatReport() {
    }

    protected OxygenSatReport(Parcel in) {
        this.value = in.readDouble();
        this.inference = in.readString();
        this.color = in.readString();
    }

    public static final Parcelable.Creator<OxygenSatReport> CREATOR = new Parcelable.Creator<OxygenSatReport>() {
        @Override
        public OxygenSatReport createFromParcel(Parcel source) {
            return new OxygenSatReport(source);
        }

        @Override
        public OxygenSatReport[] newArray(int size) {
            return new OxygenSatReport[size];
        }
    };
}
