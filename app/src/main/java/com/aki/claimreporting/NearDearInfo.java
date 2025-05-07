package com.aki.claimreporting;

public class NearDearInfo {
    public String nearname;
    public String nearphnum;
    public String nearemailid;


    public NearDearInfo() {
    }

    public NearDearInfo(String nearname, String nearphnum, String nearemailid) {
        this.nearname = nearname;
        this.nearphnum = nearphnum;
        this.nearemailid = nearemailid;
    }

    public String getNearname() {
        return nearname;
    }

    public void setNearname(String nearname) {
        this.nearname = nearname;
    }

    public String getNearphnum() {
        return nearphnum;
    }

    public void setNearphnum(String nearphnum) {
        this.nearphnum = nearphnum;
    }

    public String getNearemailid() {
        return nearemailid;
    }

    public void setNearemailid(String nearemailid) {
        this.nearemailid = nearemailid;
    }


}