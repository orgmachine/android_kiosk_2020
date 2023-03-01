package com.ehealthkiosk.kiosk.model.profilelist;

import com.google.gson.annotations.SerializedName;

public class ProfilesItem{

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	@SerializedName("photo_url")
	private String photoUrl;

	@SerializedName("gender")
	private String gender;

	@SerializedName("age")
	private int age;

	@SerializedName("email")
	private String email;

    @SerializedName("qb_id")
    private String qb_id;

    @SerializedName("qb_login")
    private String qb_login;

    @SerializedName("qb_password")
    private String qb_password;

	@SerializedName("has_free_test")
	private Boolean has_free_test;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setPhotoUrl(String photoUrl){
		this.photoUrl = photoUrl;
	}

	public String getPhotoUrl(){
		return photoUrl;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

    public String getQbId() {
        return qb_id;
    }

    public void setQbId(String qb_id) {
        this.qb_id = qb_id;
    }

    public String getQbPassword() {
        return qb_password;
    }

    public void setQbPassword(String qb_password) {
        this.qb_password = qb_password;
    }

    public String getQbLogin() {
        return qb_login;
    }

    public void setQbLogin(String qb_login) {
        this.qb_login = qb_login;
    }

	public void setRailwayEmployeeCheck(Boolean has_free_test) {
		this.has_free_test = has_free_test;
	}

	public Boolean getRailwayEmployeeCheck() {
		return has_free_test;
	}


	@Override
 	public String toString(){
		return 
			"ProfilesItem{" + 
			"name = '" + name + '\'' + 
			",id = '" + id + '\'' + 
			",photo_url = '" + photoUrl + '\'' + 
			"}";
		}
}