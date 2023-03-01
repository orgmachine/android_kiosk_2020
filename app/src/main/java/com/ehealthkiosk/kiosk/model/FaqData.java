package com.ehealthkiosk.kiosk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FaqData {
    @SerializedName("faqs")
    @Expose
    private List<FaqList> faqs = null;

    public List<FaqList> getFaqs() {
        return faqs;
    }

    public void setFaqs(List<FaqList> faqs) {
        this.faqs = faqs;
    }
}
