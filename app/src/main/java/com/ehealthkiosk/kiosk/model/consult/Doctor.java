package com.ehealthkiosk.kiosk.model.consult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Doctor {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("experience")
    @Expose
    private int experience;
    @SerializedName("qb_id")
    @Expose
    private int qbId;

    @SerializedName("languages")
    @Expose
    private String languages;

    public String getAvailableTiming() {
        return availableTiming;
    }

    public void setAvailableTiming(String availableTiming) {
        this.availableTiming = availableTiming;
    }

    @SerializedName("available_timing")
    @Expose
    private String availableTiming;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("qualifications")
    @Expose
    private String qualifications;
    @SerializedName("expertise")
    @Expose
    private String expertise;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("registration_number")
    @Expose
    private String registrationNumber;
    @SerializedName("fees")
    @Expose
    private String fees;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("profile_picture")
    @Expose
    private ProfilePicture profilePicture;
    @SerializedName("available")
    @Expose
    private boolean available;

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getQbId() {
        return qbId;
    }

    public void setQbId(int qbId) {
        this.qbId = qbId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getSearchString(){
        return this.firstName + this.lastName + this.description;
    }

}
