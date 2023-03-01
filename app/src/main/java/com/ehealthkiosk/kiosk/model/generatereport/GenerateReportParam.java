package com.ehealthkiosk.kiosk.model.generatereport;

public class GenerateReportParam {


    /**
     * profile_id : 3
     * checkup : {"bmi":23.6,"hydration":58.05,"fat":20.45,"bonemass":4.37,"muscle":41.9,"systolic":120,"diastolic":80,"temperature":99,"pulse":80,"oxygen_sat":120,"height":150,"weight":52}
     */

    private String profile_id;
    private Checkup checkup;

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }

    public Checkup getCheckup() {
        return checkup;
    }

    public void setCheckup(Checkup checkup) {
        this.checkup = checkup;
    }


}
