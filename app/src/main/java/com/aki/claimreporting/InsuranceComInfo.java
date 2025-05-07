package com.aki.claimreporting;

public class InsuranceComInfo {

    public String insurerID;
    public int dMVICMemberCompanyID;
    public int iMIDSMemberCompanyID;
    public String insurerName;

    public InsuranceComInfo() {
    }

    public InsuranceComInfo(String insurerID, int dMVICMemberCompanyID, int iMIDSMemberCompanyID, String insurerName) {
        this.insurerID = insurerID;
        this.dMVICMemberCompanyID = dMVICMemberCompanyID;
        this.iMIDSMemberCompanyID = iMIDSMemberCompanyID;
        this.insurerName = insurerName;
    }

    public String getInsurerID() {
        return insurerID;
    }

    public void setInsurerID(String insurerID) {
        this.insurerID = insurerID;
    }

    public int getdMVICMemberCompanyID() {
        return dMVICMemberCompanyID;
    }

    public void setdMVICMemberCompanyID(int dMVICMemberCompanyID) {
        this.dMVICMemberCompanyID = dMVICMemberCompanyID;
    }

    public int getiMIDSMemberCompanyID() {
        return iMIDSMemberCompanyID;
    }

    public void setiMIDSMemberCompanyID(int iMIDSMemberCompanyID) {
        this.iMIDSMemberCompanyID = iMIDSMemberCompanyID;
    }

    public String getInsurerName() {
        return insurerName;
    }

    public void setInsurerName(String insurerName) {
        this.insurerName = insurerName;
    }


}