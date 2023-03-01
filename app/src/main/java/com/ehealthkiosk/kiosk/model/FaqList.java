package com.ehealthkiosk.kiosk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FaqList {
    @SerializedName("seqment")
    @Expose
    private String seqment;
    @SerializedName("items")
    @Expose
    private List<QnAList> items = null;

    public String getSeqment() {
        return seqment;
    }

    public void setSeqment(String seqment) {
        this.seqment = seqment;
    }

    public List<QnAList> getItems() {
        return items;
    }

    public void setItems(List<QnAList> items) {
        this.items = items;
    }
}
