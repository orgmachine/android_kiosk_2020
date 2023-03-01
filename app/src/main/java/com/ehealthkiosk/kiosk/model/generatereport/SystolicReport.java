package com.ehealthkiosk.kiosk.model.generatereport;

import android.os.Parcel;
import android.os.Parcelable;

public class SystolicReport implements Parcelable {
    /**
     * value : 120
     * inference : Normal
     * color : #0ac680
     */

    private int value;
    private String inference;
    private String color;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
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
        dest.writeInt(this.value);
        dest.writeString(this.inference);
        dest.writeString(this.color);
    }

    public SystolicReport() {
    }

    protected SystolicReport(Parcel in) {
        this.value = in.readInt();
        this.inference = in.readString();
        this.color = in.readString();
    }

    public static final Parcelable.Creator<SystolicReport> CREATOR = new Parcelable.Creator<SystolicReport>() {
        @Override
        public SystolicReport createFromParcel(Parcel source) {
            return new SystolicReport(source);
        }

        @Override
        public SystolicReport[] newArray(int size) {
            return new SystolicReport[size];
        }
    };
}