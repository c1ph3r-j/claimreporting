package com.aki.claimreporting;

public class GrivienceInfo {

    public String idval;
    public String dateval;
    public String regnoval;
    public String grivStatus;

    public GrivienceInfo() {
    }


    public GrivienceInfo(String idval, String dateval, String regnoval, String grivStatus) {
        this.idval = idval;
        this.dateval = dateval;
        this.regnoval = regnoval;
        this.grivStatus = grivStatus;
    }

    public String getIdval() {
        return idval;
    }

    public void setIdval(String idval) {
        this.idval = idval;
    }

    public String getDateval() {
        return dateval;
    }

    public void setDateval(String dateval) {
        this.dateval = dateval;
    }

    public String getRegnoval() {
        return regnoval;
    }

    public void setRegnoval(String regnoval) {
        this.regnoval = regnoval;
    }

    public String getGrivStatus() {
        return grivStatus;
    }

    public void setGrivStatus(String grivStatus) {
        this.grivStatus = grivStatus;
    }


}