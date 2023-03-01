package com.ehealthkiosk.kiosk.model.register;


import com.ehealthkiosk.kiosk.model.profilelist.ProfilesItem;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegisterData {


    @SerializedName("token")
    private String token;

    @SerializedName("profile")
    private ProfilesItem profile;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ProfilesItem getProfile() {
        return profile;
    }

    public void setProfile(ProfilesItem profile) {
        this.profile = profile;
    }
}