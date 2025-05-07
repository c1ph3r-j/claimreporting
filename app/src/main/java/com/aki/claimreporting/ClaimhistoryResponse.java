package com.aki.claimreporting;

public class ClaimhistoryResponse {
    public String ClaimRefID;
    public String CertificateNo;
    public String RegistrationNo;
    public String ClaimType;
    public String Make;
    public String Model;
    public String YearOfRegistration;
    public String ChassisNo;
    public String TypeCertificate;
    public String Coverage;
    public String Claimdate;
    public String isSubmitted;
    public String isvehicleId;
    public String isLocationval;
    public String isIncidentdate;

//    public String TypeOfVehicleName;
//    public String TypeOfClaimName;
//    public String PolicyNo;
//
//    //    public DateTimeFormatter ReportedDate ;
//    public String ReportedLocation ;
//    public String EngineNo ;
//    public String ClaimDocument ;


    public ClaimhistoryResponse(String ClaimRefID, String CertificateNo, String RegistrationNo, String ClaimType, String Make, String Model, String YearOfRegistration, String ChassisNo, String TypeCertificate, String Coverage, String Claimdate, String isSubmitted, String isvehicleId, String isLocationval, String isIncidentdate) {

        this.ClaimRefID = ClaimRefID;
        this.CertificateNo = CertificateNo;
        this.RegistrationNo = RegistrationNo;
        this.ClaimType = ClaimType;
        this.Make = Make;
        this.Model = Model;
        this.YearOfRegistration = YearOfRegistration;
        this.ChassisNo = ChassisNo;
        this.TypeCertificate = TypeCertificate;
        this.Coverage = Coverage;
        this.Claimdate = Claimdate;
        this.isSubmitted = isSubmitted;
        this.isvehicleId = isvehicleId;
        this.isLocationval = isLocationval;
        this.isIncidentdate = isIncidentdate;
        //this.ReportedDate = ReportedDate;
    }

    public String getClaimRefID() {
        return ClaimRefID;
    }

    public void setClaimRefID(String claimRefID) {
        ClaimRefID = claimRefID;
    }

    public String getCertificateNo() {
        return CertificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        CertificateNo = certificateNo;
    }

    public String getRegistrationNo() {
        return RegistrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        RegistrationNo = registrationNo;
    }

    public String getClaimType() {
        return ClaimType;
    }

    public void setClaimType(String claimType) {
        ClaimType = claimType;
    }

    public String getMake() {
        return Make;
    }

    public void setMake(String make) {
        Make = make;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getYearOfRegistration() {
        return YearOfRegistration;
    }

    public void setYearOfRegistration(String yearOfRegistration) {
        YearOfRegistration = yearOfRegistration;
    }

    public String getChassisNo() {
        return ChassisNo;
    }

    public void setChassisNo(String chassisNo) {
        ChassisNo = chassisNo;
    }

    public String getTypeCertificate() {
        return TypeCertificate;
    }

    public void setTypeCertificate(String typeCertificate) {
        TypeCertificate = typeCertificate;
    }

    public String getCoverage() {
        return Coverage;
    }

    public void setCoverage(String coverage) {
        Coverage = coverage;
    }

    public String getClaimdate() {
        return Claimdate;
    }

    public void setClaimdate(String claimdate) {
        Claimdate = claimdate;
    }

    public String getIsSubmitted() {
        return isSubmitted;
    }

    public void setIsSubmitted(String isSubmitted) {
        this.isSubmitted = isSubmitted;
    }

    public String getIsvehicleId() {
        return isvehicleId;
    }

    public void setIsvehicleId(String isvehicleId) {
        this.isvehicleId = isvehicleId;
    }

    public String getIsLocationval() {
        return isLocationval;
    }

    public void setIsLocationval(String isLocationval) {
        this.isLocationval = isLocationval;
    }

    public String getIsIncidentdate() {
        return isIncidentdate;
    }

    public void setIsIncidentdate(String isIncidentdate) {
        this.isIncidentdate = isIncidentdate;
    }


}
