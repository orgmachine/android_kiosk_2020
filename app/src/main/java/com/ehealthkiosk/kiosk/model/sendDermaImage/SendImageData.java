package com.ehealthkiosk.kiosk.model.sendDermaImage;

import java.io.File;

public class SendImageData {

    private Integer profile_id;
    private String report_type;
    private File image;
    private String description = "";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return profile_id;
    }

    public void setId(Integer profile_id) {
        this.profile_id = profile_id;
    }

    public String getReportType() {
        return report_type;
    }

    public void setReportType(String report_type) {
        this.report_type = report_type;
    }

    public File getPhoto() {
        return image;
    }

    public void setPhoto(File image) {
        this.image = image;
    }
}
