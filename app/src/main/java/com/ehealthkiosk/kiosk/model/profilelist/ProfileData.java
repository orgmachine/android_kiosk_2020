package com.ehealthkiosk.kiosk.model.profilelist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileData {

	@SerializedName("profiles")
	private List<ProfilesItem> profiles;

	public void setProfiles(List<ProfilesItem> profiles){
		this.profiles = profiles;
	}

	public List<ProfilesItem> getProfiles(){
		return profiles;
	}

	@Override
 	public String toString(){
		return 
			"ProfileData{" +
			"profiles = '" + profiles + '\'' + 
			"}";
		}
}