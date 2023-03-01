package com.ehealthkiosk.kiosk.model.generatereport;

import android.os.Parcel;
import android.os.Parcelable;

public class DiastolicReport implements Parcelable {
    /**
     * value : 80
     * inference : Hypertension I
     * color : #c60a0a
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

    public DiastolicReport() {
    }

    protected DiastolicReport(Parcel in) {
        this.value = in.readInt();
        this.inference = in.readString();
        this.color = in.readString();
    }

    public static final Parcelable.Creator<DiastolicReport> CREATOR = new Parcelable.Creator<DiastolicReport>() {
        @Override
        public DiastolicReport createFromParcel(Parcel source) {
            return new DiastolicReport(source);
        }

        @Override
        public DiastolicReport[] newArray(int size) {
            return new DiastolicReport[size];
        }
    };
}
