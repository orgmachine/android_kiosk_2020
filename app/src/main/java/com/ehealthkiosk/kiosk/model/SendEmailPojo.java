package com.ehealthkiosk.kiosk.model;

public class SendEmailPojo {
    private Integer report_id;
    private String email;
    private Integer profile_id;
    private String name;
    private String url;

    public Integer getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(Integer profile_id) {
        this.profile_id = profile_id;
    }

    public Integer getReport_id() {
        return report_id;
    }

    public void setReport_id(Integer report_id) {
        this.report_id = report_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getReportId() {
        return report_id;
    }

    public void setReportId(Integer report_id) {
        this.report_id = report_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
