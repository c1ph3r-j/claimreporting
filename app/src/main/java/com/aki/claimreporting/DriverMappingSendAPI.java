package com.aki.claimreporting;

public class DriverMappingSendAPI {

    public String driverUserId;
    public String certnum;

    public DriverMappingSendAPI() {
    }

    public DriverMappingSendAPI(String driverUserId, String certnum) {
        this.driverUserId = driverUserId;
        this.certnum = certnum;
    }

    public String getDriverUserId() {
        return driverUserId;
    }

    public void setDriverUserId(String driverUserId) {
        this.driverUserId = driverUserId;
    }

    public String getCertnum() {
        return certnum;
    }

    public void setCertnum(String certnum) {
        this.certnum = certnum;
    }


}
