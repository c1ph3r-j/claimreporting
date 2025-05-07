package com.aki.claimreporting;

public class DriverAllDataModel {

    public String driverUserId;
    public String driverName;
    public String driverDLCountry;
    public String driverDLNum;
    public String driverDLValidFrom;
    public String driverDLValidTill;
    public String mobileNo;
    public boolean isSelfDriver;
    public String driverstatus;

    public DriverAllDataModel() {

    }

    public DriverAllDataModel(String driverUserId, String driverName, String driverDLCountry, String driverDLNum, String driverDLValidFrom, String driverDLValidTill, String mobileNo, boolean isSelfDriver, String driverstatus) {
        this.driverUserId = driverUserId;
        this.driverName = driverName;
        this.driverDLCountry = driverDLCountry;
        this.driverDLNum = driverDLNum;
        this.driverDLValidFrom = driverDLValidFrom;
        this.driverDLValidTill = driverDLValidTill;
        this.mobileNo = mobileNo;
        this.isSelfDriver = isSelfDriver;
        this.driverstatus = driverstatus;
    }

    public String getDriverUserId() {
        return driverUserId;
    }

    public void setDriverUserId(String driverUserId) {
        this.driverUserId = driverUserId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverDLCountry() {
        return driverDLCountry;
    }

    public void setDriverDLCountry(String driverDLCountry) {
        this.driverDLCountry = driverDLCountry;
    }

    public String getDriverDLNum() {
        return driverDLNum;
    }

    public void setDriverDLNum(String driverDLNum) {
        this.driverDLNum = driverDLNum;
    }

    public String getDriverDLValidFrom() {
        return driverDLValidFrom;
    }

    public void setDriverDLValidFrom(String driverDLValidFrom) {
        this.driverDLValidFrom = driverDLValidFrom;
    }

    public String getDriverDLValidTill() {
        return driverDLValidTill;
    }

    public void setDriverDLValidTill(String driverDLValidTill) {
        this.driverDLValidTill = driverDLValidTill;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public boolean getSelfDriver() {
        return isSelfDriver;
    }

    public void setSelfDriver(boolean selfDriver) {
        isSelfDriver = selfDriver;
    }

    public String getDriverstatus() {
        return driverstatus;
    }

    public void setDriverstatus(String driverstatus) {
        this.driverstatus = driverstatus;
    }
}