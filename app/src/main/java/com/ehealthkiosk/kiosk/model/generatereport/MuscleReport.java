package com.ehealthkiosk.kiosk.model.generatereport;

import android.os.Parcel;
import android.os.Parcelable;

public class MuscleReport implements Parcelable {
    /**
     * value : 41.9
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

    public MuscleReport() {
    }

    protected MuscleReport(Parcel in) {
        this.value = in.readDouble();
        this.inference = in.readString();
        this.color = in.readString();
    }

    public static final Parcelable.Creator<MuscleReport> CREATOR = new Parcelable.Creator<MuscleReport>() {
        @Override
        public MuscleReport createFromParcel(Parcel source) {
            return new MuscleReport(source);
        }

        @Override
        public MuscleReport[] newArray(int size) {
            return new MuscleReport[size];
        }
    };
}
