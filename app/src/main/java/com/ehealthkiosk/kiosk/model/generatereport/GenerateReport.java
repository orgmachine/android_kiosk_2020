package com.ehealthkiosk.kiosk.model.generatereport;

import android.os.Parcel;
import android.os.Parcelable;

public class GenerateReport implements Parcelable {
    /**
     * bmi : {"value":23.6,"inference":"Normal","color":"#0ac680"}
     * hydration : {"value":58.05,"inference":"Normal","color":"#0ac680"}
     * fat : {"value":20.45,"inference":"Acceptable","color":"#ddbe24"}
     * bonemass : {"value":4.37,"inference":"Normal","color":"#0ac680"}
     * muscle : {"value":41.9,"inference":"Normal","color":"#0ac680"}
     * systolic : {"value":120,"inference":"Normal","color":"#0ac680"}
     * diastolic : {"value":80,"inference":"Hypertension I","color":"#c60a0a"}
     * temperature : {"value":99,"inference":"High","color":"#c60a0a"}
     * pulse : {"value":80,"inference":"Normal","color":"#0ac680"}
     * oxygen_sat : {"value":120,"inference":"Normal","color":"#0ac680"}
     */

    private BmiReport bmi;
    private HydrationReport hydration;
    private FatReport fat;
    private BonemassReport bonemass;
    private MuscleReport muscle;
    private SystolicReport systolic;
    private DiastolicReport diastolic;
    private TemperatureReport temperature;
    private PulseReport pulse;
    private OxygenSatReport oxygen_sat;

    public BmiReport getBmi() {
        return bmi;
    }

    public void setBmi(BmiReport bmi) {
        this.bmi = bmi;
    }

    public HydrationReport getHydration() {
        return hydration;
    }

    public void setHydration(HydrationReport hydration) {
        this.hydration = hydration;
    }

    public FatReport getFat() {
        return fat;
    }

    public void setFat(FatReport fat) {
        this.fat = fat;
    }

    public BonemassReport getBonemass() {
        return bonemass;
    }

    public void setBonemass(BonemassReport bonemass) {
        this.bonemass = bonemass;
    }

    public MuscleReport getMuscle() {
        return muscle;
    }

    public void setMuscle(MuscleReport muscle) {
        this.muscle = muscle;
    }

    public SystolicReport getSystolic() {
        return systolic;
    }

    public void setSystolic(SystolicReport systolic) {
        this.systolic = systolic;
    }

    public DiastolicReport getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(DiastolicReport diastolic) {
        this.diastolic = diastolic;
    }

    public TemperatureReport getTemperature() {
        return temperature;
    }

    public void setTemperature(TemperatureReport temperature) {
        this.temperature = temperature;
    }

    public PulseReport getPulse() {
        return pulse;
    }

    public void setPulse(PulseReport pulse) {
        this.pulse = pulse;
    }

    public OxygenSatReport getOxygen_sat() {
        return oxygen_sat;
    }

    public void setOxygen_sat(OxygenSatReport oxygen_sat) {
        this.oxygen_sat = oxygen_sat;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.bmi, flags);
        dest.writeParcelable(this.hydration, flags);
        dest.writeParcelable(this.fat, flags);
        dest.writeParcelable(this.bonemass, flags);
        dest.writeParcelable(this.muscle, flags);
        dest.writeParcelable(this.systolic, flags);
        dest.writeParcelable(this.diastolic, flags);
        dest.writeParcelable(this.temperature, flags);
        dest.writeParcelable(this.pulse, flags);
        dest.writeParcelable(this.oxygen_sat, flags);
    }

    public GenerateReport() {
    }

    protected GenerateReport(Parcel in) {
        this.bmi = in.readParcelable(BmiReport.class.getClassLoader());
        this.hydration = in.readParcelable(HydrationReport.class.getClassLoader());
        this.fat = in.readParcelable(FatReport.class.getClassLoader());
        this.bonemass = in.readParcelable(BonemassReport.class.getClassLoader());
        this.muscle = in.readParcelable(MuscleReport.class.getClassLoader());
        this.systolic = in.readParcelable(SystolicReport.class.getClassLoader());
        this.diastolic = in.readParcelable(DiastolicReport.class.getClassLoader());
        this.temperature = in.readParcelable(TemperatureReport.class.getClassLoader());
        this.pulse = in.readParcelable(PulseReport.class.getClassLoader());
        this.oxygen_sat = in.readParcelable(OxygenSatReport.class.getClassLoader());
    }

    public static final Parcelable.Creator<GenerateReport> CREATOR = new Parcelable.Creator<GenerateReport>() {
        @Override
        public GenerateReport createFromParcel(Parcel source) {
            return new GenerateReport(source);
        }

        @Override
        public GenerateReport[] newArray(int size) {
            return new GenerateReport[size];
        }
    };
}
