package com.ehealthkiosk.kiosk.model.consult;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Patient {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("photo_url")
    @Expose
    private String photoUrl;
    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("is_primary")
    @Expose
    private Boolean isPrimary;
    @SerializedName("height")
    @Expose
    private Object height;
    @SerializedName("weight")
    @Expose
    private Object weight;
    @SerializedName("qb_id")
    @Expose
    private String qbId;
    @SerializedName("qb_login")
    @Expose
    private String qbLogin;
    @SerializedName("qb_password")
    @Expose
    private String qbPassword;
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("relationship")
    @Expose
    private Object relationship;
    @SerializedName("bmi")
    @Expose
    private Object bmi;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("is_railway_employee")
    @Expose
    private Boolean isRailwayEmployee;
    @SerializedName("has_free_test")
    @Expose
    private Boolean hasFreeTest;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public Object getHeight() {
        return height;
    }

    public void setHeight(Object height) {
        this.height = height;
    }

    public Object getWeight() {
        return weight;
    }

    public void setWeight(Object weight) {
        this.weight = weight;
    }

    public String getQbId() {
        return qbId;
    }

    public void setQbId(String qbId) {
        this.qbId = qbId;
    }

    public String getQbLogin() {
        return qbLogin;
    }

    public void setQbLogin(String qbLogin) {
        this.qbLogin = qbLogin;
    }

    public String getQbPassword() {
        return qbPassword;
    }

    public void setQbPassword(String qbPassword) {
        this.qbPassword = qbPassword;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Object getRelationship() {
        return relationship;
    }

    public void setRelationship(Object relationship) {
        this.relationship = relationship;
    }

    public Object getBmi() {
        return bmi;
    }

    public void setBmi(Object bmi) {
        this.bmi = bmi;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Boolean getIsRailwayEmployee() {
        return isRailwayEmployee;
    }

    public void setIsRailwayEmployee(Boolean isRailwayEmployee) {
        this.isRailwayEmployee = isRailwayEmployee;
    }

    public Boolean getHasFreeTest() {
        return hasFreeTest;
    }

    public void setHasFreeTest(Boolean hasFreeTest) {
        this.hasFreeTest = hasFreeTest;
    }

}