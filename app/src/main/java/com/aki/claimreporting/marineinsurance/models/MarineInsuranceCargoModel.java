package com.aki.claimreporting.marineinsurance.models;

public class MarineInsuranceCargoModel {
    private String certificateNumber;
    private String memberCompanyName;
    private int entityId;
    private String policyNumber;
    private String startDate;
    private String endDate;
    private String registrationNumber;
    private String insuredBy;
    private String nameOfVessel;
    private String insuredOn;
    private boolean isCancelled;
    private String cancellationOn;
    private String kenyaDateTime;
    private String cancellationReason;
    private String certificateStatus;
    private String tradeType;
    private String mode;
    private int modeId;
    private String voyageFrom;
    private String voyageTo;
    private String loadingAt;
    private String portOfDischarge;
    private String transhipping;
    private String coverType;
    private double sumInsured;
    private int certificateClassificationId;
    private int statusCode;
    private String requestLocalDate;
    private String policyStartDate;
    private String policyEndDate;

    public MarineInsuranceCargoModel(String certificateNumber, String memberCompanyName, int entityId, String policyNumber, String startDate, String endDate, String registrationNumber, String insuredBy, String nameOfVessel, String insuredOn, boolean isCancelled, String cancellationOn, String kenyaDateTime, String cancellationReason, String certificateStatus, String tradeType, String mode, int modeId, String voyageFrom, String voyageTo, String loadingAt, String portOfDischarge, String transhipping, String coverType, double sumInsured, int certificateClassificationId, int statusCode, String requestLocalDate, String policyStartDate, String policyEndDate) {
        this.certificateNumber = certificateNumber;
        this.memberCompanyName = memberCompanyName;
        this.entityId = entityId;
        this.policyNumber = policyNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.registrationNumber = registrationNumber;
        this.insuredBy = insuredBy;
        this.nameOfVessel = nameOfVessel;
        this.insuredOn = insuredOn;
        this.isCancelled = isCancelled;
        this.cancellationOn = cancellationOn;
        this.kenyaDateTime = kenyaDateTime;
        this.cancellationReason = cancellationReason;
        this.certificateStatus = certificateStatus;
        this.tradeType = tradeType;
        this.mode = mode;
        this.modeId = modeId;
        this.voyageFrom = voyageFrom;
        this.voyageTo = voyageTo;
        this.loadingAt = loadingAt;
        this.portOfDischarge = portOfDischarge;
        this.transhipping = transhipping;
        this.coverType = coverType;
        this.sumInsured = sumInsured;
        this.certificateClassificationId = certificateClassificationId;
        this.statusCode = statusCode;
        this.requestLocalDate = requestLocalDate;
        this.policyStartDate = policyStartDate;
        this.policyEndDate = policyEndDate;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public String getMemberCompanyName() {
        return memberCompanyName;
    }

    public void setMemberCompanyName(String memberCompanyName) {
        this.memberCompanyName = memberCompanyName;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getInsuredBy() {
        return insuredBy;
    }

    public void setInsuredBy(String insuredBy) {
        this.insuredBy = insuredBy;
    }

    public String getNameOfVessel() {
        return nameOfVessel;
    }

    public void setNameOfVessel(String nameOfVessel) {
        this.nameOfVessel = nameOfVessel;
    }

    public String getInsuredOn() {
        return insuredOn;
    }

    public void setInsuredOn(String insuredOn) {
        this.insuredOn = insuredOn;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public String getCancellationOn() {
        return cancellationOn;
    }

    public void setCancellationOn(String cancellationOn) {
        this.cancellationOn = cancellationOn;
    }

    public String getKenyaDateTime() {
        return kenyaDateTime;
    }

    public void setKenyaDateTime(String kenyaDateTime) {
        this.kenyaDateTime = kenyaDateTime;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getCertificateStatus() {
        return certificateStatus;
    }

    public void setCertificateStatus(String certificateStatus) {
        this.certificateStatus = certificateStatus;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getModeId() {
        return modeId;
    }

    public void setModeId(int modeId) {
        this.modeId = modeId;
    }

    public String getVoyageFrom() {
        return voyageFrom;
    }

    public void setVoyageFrom(String voyageFrom) {
        this.voyageFrom = voyageFrom;
    }

    public String getVoyageTo() {
        return voyageTo;
    }

    public void setVoyageTo(String voyageTo) {
        this.voyageTo = voyageTo;
    }

    public String getLoadingAt() {
        return loadingAt;
    }

    public void setLoadingAt(String loadingAt) {
        this.loadingAt = loadingAt;
    }

    public String getPortOfDischarge() {
        return portOfDischarge;
    }

    public void setPortOfDischarge(String portOfDischarge) {
        this.portOfDischarge = portOfDischarge;
    }

    public String getTranshipping() {
        return transhipping;
    }

    public void setTranshipping(String transhipping) {
        this.transhipping = transhipping;
    }

    public String getCoverType() {
        return coverType;
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }

    public double getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(double sumInsured) {
        this.sumInsured = sumInsured;
    }

    public int getCertificateClassificationId() {
        return certificateClassificationId;
    }

    public void setCertificateClassificationId(int certificateClassificationId) {
        this.certificateClassificationId = certificateClassificationId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getRequestLocalDate() {
        return requestLocalDate;
    }

    public void setRequestLocalDate(String requestLocalDate) {
        this.requestLocalDate = requestLocalDate;
    }

    public String getPolicyStartDate() {
        return policyStartDate;
    }

    public void setPolicyStartDate(String policyStartDate) {
        this.policyStartDate = policyStartDate;
    }

    public String getPolicyEndDate() {
        return policyEndDate;
    }

    public void setPolicyEndDate(String policyEndDate) {
        this.policyEndDate = policyEndDate;
    }
}
