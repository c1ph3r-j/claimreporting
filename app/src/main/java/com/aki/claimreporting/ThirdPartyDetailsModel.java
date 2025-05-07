package com.aki.claimreporting;

public class ThirdPartyDetailsModel {
    String Name;
    String VehicleNo;
    String IncidentId;
    String MobileNo;

    public ThirdPartyDetailsModel(String name, String vehicleNo, String incidentId, String mobileNo) {
        Name = name;
        VehicleNo = vehicleNo;
        IncidentId = incidentId;
        MobileNo = mobileNo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getVehicleNo() {
        return VehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        VehicleNo = vehicleNo;
    }

    public String getIncidentId() {
        return IncidentId;
    }

    public void setIncidentId(String incidentId) {
        IncidentId = incidentId;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }
}
