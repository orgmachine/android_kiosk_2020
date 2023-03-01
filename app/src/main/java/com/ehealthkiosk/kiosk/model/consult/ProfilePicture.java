package com.ehealthkiosk.kiosk.model.consult;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfilePicture {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("doc")
    @Expose
    private String doc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

}
