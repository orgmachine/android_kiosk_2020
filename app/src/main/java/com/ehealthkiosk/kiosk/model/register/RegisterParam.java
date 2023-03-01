package com.ehealthkiosk.kiosk.model.register;

import java.io.File;

public class RegisterParam {
    private String name;
    private String mobile;
    private String date_of_birth;
    private String email;
    private String gender;
    private File photo;
    private Boolean is_railway_employee;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAge() {
        return date_of_birth;
    }

    public void setAge(String age) {
        this.date_of_birth = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

    public Boolean getRailwayEmployeeCheck() {
        return is_railway_employee;
    }

    public void setRailwayEmployeeCheck(Boolean is_railway_employee) {
        this.is_railway_employee = is_railway_employee;
    }
}
