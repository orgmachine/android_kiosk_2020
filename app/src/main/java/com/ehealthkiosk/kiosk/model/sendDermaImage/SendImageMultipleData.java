package com.ehealthkiosk.kiosk.model.sendDermaImage;

import java.io.File;
import java.util.List;

public class SendImageMultipleData {

    private Integer profile_id;
    private String report_type;
    private List<File> images;
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

    public List<File> getImages() {
        return images;
    }

    public void setImages(List<File> images) {
        this.images = images;
    }
}
