package com.aki.claimreporting;

public class VehicleselectClaimResponse {

    public String RegistrationNo;
    public String CertificateNo;
    public String VehicleRefID;
    public String InsuranceCompanyID;
    public String InsuranceCompanyName;
    public String InsurerName;
    public String TypeOfVehicleName;
    //    public String TypeOfVehicleID;
    public String CoverType;
    public String VehicleMake;
    public String VehicleModel;
    public String YearOfManufacture;
    public String PolicyNo;
    public String PolicyStartDate;
    public String PolicyEndDate;
    public String Chassisnumber;
    public String isOwnVehicle;
    public String Vechnewrefid;

    public String insurerID;

    public VehicleselectClaimResponse(String RegistrationNo, String CertificateNo, String InsuranceCompanyName, String VehicleMake, String VehicleModel, String YearOfManufacture, String PolicyStartDate, String VehicleRefID, String CoverType, String Chassisnumber, String Vechnewrefid, String insurerID) {

        this.RegistrationNo = RegistrationNo;
        this.CertificateNo = CertificateNo;
        this.InsuranceCompanyName = InsuranceCompanyName;
        this.VehicleMake = VehicleMake;
        this.VehicleModel = VehicleModel;
        this.YearOfManufacture = YearOfManufacture;
        this.PolicyStartDate = PolicyStartDate;
        this.VehicleRefID = VehicleRefID;
        this.CoverType = CoverType;
        this.Chassisnumber = Chassisnumber;
        this.Vechnewrefid = Vechnewrefid;
        this.insurerID = insurerID;

    }

    public String getRegistrationNo() {
        return RegistrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        RegistrationNo = registrationNo;
    }

    public String getCertificateNo() {
        return CertificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        CertificateNo = certificateNo;
    }

    public String getVehicleRefID() {
        return VehicleRefID;
    }

    public void setVehicleRefID(String vehicleRefID) {
        VehicleRefID = vehicleRefID;
    }

    public String getInsuranceCompanyID() {
        return InsuranceCompanyID;
    }

    public void setInsuranceCompanyID(String insuranceCompanyID) {
        InsuranceCompanyID = insuranceCompanyID;
    }

    public String getInsuranceCompanyName() {
        return InsuranceCompanyName;
    }

    public void setInsuranceCompanyName(String insuranceCompanyName) {
        InsuranceCompanyName = insuranceCompanyName;
    }

    public String getInsurerName() {
        return InsurerName;
    }

    public void setInsurerName(String insurerName) {
        InsurerName = insurerName;
    }

    public String getTypeOfVehicleName() {
        return TypeOfVehicleName;
    }

    public void setTypeOfVehicleName(String typeOfVehicleName) {
        TypeOfVehicleName = typeOfVehicleName;
    }

    public String getCoverType() {
        return CoverType;
    }

    public void setCoverType(String coverType) {
        CoverType = coverType;
    }

    public String getVehicleMake() {
        return VehicleMake;
    }

    public void setVehicleMake(String vehicleMake) {
        VehicleMake = vehicleMake;
    }

    public String getVehicleModel() {
        return VehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        VehicleModel = vehicleModel;
    }

    public String getYearOfManufacture() {
        return YearOfManufacture;
    }

    public void setYearOfManufacture(String yearOfManufacture) {
        YearOfManufacture = yearOfManufacture;
    }

    public String getPolicyNo() {
        return PolicyNo;
    }

    public void setPolicyNo(String policyNo) {
        PolicyNo = policyNo;
    }

    public String getPolicyStartDate() {
        return PolicyStartDate;
    }

    public void setPolicyStartDate(String policyStartDate) {
        PolicyStartDate = policyStartDate;
    }

    public String getPolicyEndDate() {
        return PolicyEndDate;
    }

    public void setPolicyEndDate(String policyEndDate) {
        PolicyEndDate = policyEndDate;
    }

    public String getChassisnumber() {
        return Chassisnumber;
    }

    public void setChassisnumber(String chassisnumber) {
        Chassisnumber = chassisnumber;
    }

    public String getIsOwnVehicle() {
        return isOwnVehicle;
    }

    public void setIsOwnVehicle(String isOwnVehicle) {
        this.isOwnVehicle = isOwnVehicle;
    }

    public String getVechnewrefid() {
        return Vechnewrefid;
    }

    public void setVechnewrefid(String vechnewrefid) {
        Vechnewrefid = vechnewrefid;
    }

    public String getInsurerID() {
        return insurerID;
    }

    public void setInsurerID(String insurerID) {
        this.insurerID = insurerID;
    }


}