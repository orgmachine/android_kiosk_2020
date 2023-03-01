package com.ehealthkiosk.kiosk.model.generatereport;

import android.os.Parcel;
import android.os.Parcelable;

public class TemperatureReport implements Parcelable {
    /**
     * value : 99
     * inference : High
     * color : #c60a0a
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

    public TemperatureReport() {
    }

    protected TemperatureReport(Parcel in) {
        this.value = in.readDouble();
        this.inference = in.readString();
        this.color = in.readString();
    }

    public static final Parcelable.Creator<TemperatureReport> CREATOR = new Parcelable.Creator<TemperatureReport>() {
        @Override
        public TemperatureReport createFromParcel(Parcel source) {
            return new TemperatureReport(source);
        }

        @Override
        public TemperatureReport[] newArray(int size) {
            return new TemperatureReport[size];
        }
    };
}
