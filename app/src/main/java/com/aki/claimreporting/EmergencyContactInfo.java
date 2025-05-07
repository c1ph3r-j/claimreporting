package com.aki.claimreporting;

public class EmergencyContactInfo {

    public String nearid;
    public String nearname;
    public String nearemail;
    public String nearphone;

    public EmergencyContactInfo() {
    }


    public EmergencyContactInfo(String nearid, String nearname, String nearemail, String nearphone) {
        this.nearid = nearid;
        this.nearname = nearname;
        this.nearemail = nearemail;
        this.nearphone = nearphone;
    }

    public String getNearid() {
        return nearid;
    }

    public void setNearid(String nearid) {
        this.nearid = nearid;
    }

    public String getNearname() {
        return nearname;
    }

    public void setNearname(String nearname) {
        this.nearname = nearname;
    }

    public String getNearemail() {
        return nearemail;
    }

    public void setNearemail(String nearemail) {
        this.nearemail = nearemail;
    }

    public String getNearphone() {
        return nearphone;
    }

    public void setNearphone(String nearphone) {
        this.nearphone = nearphone;
    }

}
