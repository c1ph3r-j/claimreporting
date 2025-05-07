package com.aki.claimreporting;

public class TimeLineModel {

    public String Claimdatetime;
    /* public String Claimstepimg;*/
    public String Claimevent;
    public String Claimeventname;

    public TimeLineModel(String Claimdatetime, String Claimevent, String Claimeventname) {
        this.Claimdatetime = Claimdatetime;
        // this.Claimstepimg = Claimstepimg;
        this.Claimevent = Claimevent;
        this.Claimeventname = Claimeventname;
    }

    public String getClaimdatetime() {
        return Claimdatetime;
    }

    /* public String getClaimstepimg() {
         return Claimstepimg;
     }*/
    public String getClaimevent() {
        return Claimevent;
    }

    public String getClaimeventname() {
        return Claimeventname;
    }
}

