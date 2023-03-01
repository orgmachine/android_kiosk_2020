package com.ehealthkiosk.kiosk.model.generatereport;

import android.os.Parcel;
import android.os.Parcelable;

public class Checkup implements Parcelable {
    /**
     * bmi : 23.6
     * hydration : 58.05
     * fat : 20.45
     * bonemass : 4.37
     * muscle : 41.9
     * systolic : 120
     * diastolic : 80
     * temperature : 99
     * pulse : 80
     * oxygen_sat : 120
     * height : 150
     * weight : 52
     */

    private String bmi;
    private String hydration;
    private String fat;
    private String bonemass;
    private String muscle;
    private String obesity;
    private String protein;
    private String vfi;
    private String bmr;
    private String body_age;
    private String systolic;
    private String diastolic;
    private String temperature;
    private String pulse;
    private String oxygen_sat;
    private String height;
    private String weight;
    private String hb;
    private String glucose;
    private String report_type;
    private String bg_type;
    private Boolean is_free;
    private String data_source;

    public String getData_source() {
        return data_source;
    }

    public void setData_source(String data_source) {
        this.data_source = data_source;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getHydration() {
        return hydration;
    }

    public void setHydration(String hydration) {
        this.hydration = hydration;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getBonemass() {
        return bonemass;
    }

    public void setBonemass(String bonemass) {
        this.bonemass = bonemass;
    }

    public String getObesity() {
        return obesity;
    }

    public void setObesity(String obesity) {
        this.obesity = obesity;
    }

    public String getProtien() {
        return protein;
    }

    public void setProtien(String protien) {
        this.protein = protien;
    }

    public String getVfi() {
        return vfi;
    }

    public void setVfi(String vfi) {
        this.vfi = vfi;
    }

    public String getBmr() {
        return bmr;
    }

    public void setBmr(String bmr) {
        this.bmr = bmr;
    }

    public String getBodyAge() {
        return body_age;
    }

    public void setBodyage(String body_age) {
        this.body_age = body_age;
    }


    public String getMuscle() {
        return muscle;
    }

    public void setMuscle(String muscle) {
        this.muscle = muscle;
    }

    public String getSystolic() {
        return systolic;
    }

    public void setSystolic(String systolic) {
        this.systolic = systolic;
    }

    public String getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(String diastolic) {
        this.diastolic = diastolic;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getOxygen_sat() {
        return oxygen_sat;
    }

    public void setOxygen_sat(String oxygen_sat) {
        this.oxygen_sat = oxygen_sat;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHb() {
        return hb;
    }

    public void setHb(String hb) {
        this.hb = hb;
    }

    public String getGlucose() {
        return glucose;
    }

    public void setGlucose(String glucose) {
        this.glucose = glucose;
    }

    public String getReportType() {
        return glucose;
    }

    public void setReportType(String report_type) {
        this.report_type = report_type;
    }

    public String getBgTestType() {
        return bg_type;
    }

    public void setBgTestType(String bg_type) {
        this.bg_type = bg_type;
    }

    public Boolean getIsFree() {
        return is_free;
    }

    public void setIsFree(Boolean is_free) {
        this.is_free = is_free;
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bmi);
        dest.writeString(this.hydration);
        dest.writeString(this.fat);
        dest.writeString(this.bonemass);
        dest.writeString(this.muscle);
        dest.writeString(this.systolic);
        dest.writeString(this.diastolic);
        dest.writeString(this.temperature);
        dest.writeString(this.pulse);
        dest.writeString(this.oxygen_sat);
        dest.writeString(this.height);
        dest.writeString(this.weight);
        dest.writeString(this.hb);
        dest.writeString(this.glucose);
        dest.writeString(this.report_type);
        dest.writeString(this.bg_type);
        dest.writeString(String.valueOf(this.is_free));


    }

    public Checkup() {
    }

    protected Checkup(Parcel in) {
        this.bmi = in.readString();
        this.hydration = in.readString();
        this.fat = in.readString();
        this.bonemass = in.readString();
        this.muscle = in.readString();
        this.systolic = in.readString();
        this.diastolic = in.readString();
        this.temperature = in.readString();
        this.pulse = in.readString();
        this.oxygen_sat = in.readString();
        this.height = in.readString();
        this.weight = in.readString();
        this.hb = in.readString();
        this.glucose = in.readString();
        this.report_type = in.readString();
        this.bg_type = in.readString();
        this.is_free = Boolean.valueOf(in.readString());


    }

    public static final Parcelable.Creator<Checkup> CREATOR = new Parcelable.Creator<Checkup>() {
        @Override
        public Checkup createFromParcel(Parcel source) {
            return new Checkup(source);
        }

        @Override
        public Checkup[] newArray(int size) {
            return new Checkup[size];
        }
    };
}
