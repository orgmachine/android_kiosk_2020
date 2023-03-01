package com.ehealthkiosk.kiosk.model.profilelist;

import com.ehealthkiosk.kiosk.model.commonresponse.Status;
import com.google.gson.annotations.SerializedName;

public class ProfileListResponse{

	@SerializedName("data")
	private ProfileData profileData;

	@SerializedName("status")
	private Status status;

	public void setProfileData(ProfileData profileData){
		this.profileData = profileData;
	}

	public ProfileData getProfileData(){
		return profileData;
	}

	public void setStatus(Status status){
		this.status = status;
	}

	public Status getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"ProfileListResponse{" + 
			"profileData = '" + profileData + '\'' +
			",status = '" + status + '\'' + 
			"}";
		}
}