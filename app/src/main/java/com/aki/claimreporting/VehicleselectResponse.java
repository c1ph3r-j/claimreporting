package com.aki.claimreporting;

public class VehicleselectResponse {


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
    public String isSubmitted;
    public String ownVehicleID;
    public String VehicleReferernceID;

//    public VehicleselectResponse()
//    {
//
//    }

    public VehicleselectResponse(String RegistrationNo, String CertificateNo, String VehicleRefID, String InsuranceCompanyID, String InsuranceCompanyName, String InsurerName, String TypeOfVehicleName, String CoverType, String VehicleMake, String VehicleModel, String YearOfManufacture, String PolicyNo, String PolicyStartDate, String PolicyEndDate, String ChassisNumber, String isOwnVehicle, String isSubmitted, String ownVehicleID, String VehicleReferernceID) {

        this.RegistrationNo = RegistrationNo;
        this.VehicleRefID = VehicleRefID;
        this.CertificateNo = CertificateNo;
        this.InsuranceCompanyID = InsuranceCompanyID;
        this.InsuranceCompanyName = InsuranceCompanyName;
        this.InsurerName = InsurerName;
        this.TypeOfVehicleName = TypeOfVehicleName;
        this.CoverType = CoverType;
        this.VehicleMake = VehicleMake;
        this.VehicleModel = VehicleModel;
        this.YearOfManufacture = YearOfManufacture;
        this.PolicyNo = PolicyNo;
        this.PolicyStartDate = PolicyStartDate;
        this.PolicyEndDate = PolicyEndDate;
        this.Chassisnumber = ChassisNumber;
        this.isOwnVehicle = isOwnVehicle;
        this.isSubmitted = isSubmitted;
        this.ownVehicleID = ownVehicleID;
        this.VehicleReferernceID = VehicleReferernceID;
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

    public String getIsSubmitted() {
        return isSubmitted;
    }

    public void setIsSubmitted(String isSubmitted) {
        this.isSubmitted = isSubmitted;
    }

    public String getOwnVehicleID() {
        return ownVehicleID;
    }

    public void setOwnVehicleID(String ownVehicleID) {
        this.ownVehicleID = ownVehicleID;
    }

    public String getVehicleReferernceID() {
        return VehicleReferernceID;
    }

    public void setVehicleReferernceID(String VehicleReferernceID) {
        this.VehicleReferernceID = VehicleReferernceID;
    }


}
