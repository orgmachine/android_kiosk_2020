package com.ehealthkiosk.kiosk.model.generatereport;

import com.ehealthkiosk.kiosk.model.commonresponse.Status;

public class GenerateReportResponse {


    /**
     * status : {"result":1,"message":"Report submitted succefully"}
     * data : {"report":{"bmi":{"value":23.6,"inference":"Normal","color":"#0ac680"},"hydration":{"value":58.05,"inference":"Normal","color":"#0ac680"},"fat":{"value":20.45,"inference":"Acceptable","color":"#ddbe24"},"bonemass":{"value":4.37,"inference":"Normal","color":"#0ac680"},"muscle":{"value":41.9,"inference":"Normal","color":"#0ac680"},"systolic":{"value":120,"inference":"Normal","color":"#0ac680"},"diastolic":{"value":80,"inference":"Hypertension I","color":"#c60a0a"},"temperature":{"value":99,"inference":"High","color":"#c60a0a"},"pulse":{"value":80,"inference":"Normal","color":"#0ac680"},"oxygen_sat":{"value":120,"inference":"Normal","color":"#0ac680"}},"pdf_link":"http://ec2-13-235-2-112.ap-south-1.compute.amazonaws.com/media/basic_report_11.pdf"}
     */

    private Status status;
    private GenerateReportData data;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public GenerateReportData getData() {
        return data;
    }

    public void setData(GenerateReportData data) {
        this.data = data;
    }

}
