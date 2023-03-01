package com.ehealthkiosk.kiosk.model.consult.requests;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportsAPI {

    @SerializedName("grouped")
    @Expose
    private Boolean grouped;
    @SerializedName("offset")
    @Expose
    private Integer offset;
    @SerializedName("limit")
    @Expose
    private Integer limit;

    public ReportsAPI() {
        this.grouped = false;
        this.offset = 0;
        this.limit = 200;
    }

    public Boolean getGrouped() {
        return grouped;
    }

    public void setGrouped(Boolean grouped) {
        this.grouped = grouped;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

}