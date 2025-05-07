package com.aki.claimreporting.professionalindemnityinsurance.models;

public class ProfessionalIndemnityModel {
    private String certificateNumber;
    private String memberCompanyName;
    private int entityId;
    private String policyNumber;
    private String occupation;
    private String specialisation;
    private String startDate;
    private String endDate;
    private String insuredBy;
    private boolean isCancelled;
    private String cancellationOn;
    private String kenyaDateTime;
    private String cancellationReason;
    private String certificateStatus;
    private int certificateClassificationId;
    private int statusCode;
    private String requestLocalDate;
    private String policyStartDate;
    private String policyEndDate;

    public ProfessionalIndemnityModel(String certificateNumber, String memberCompanyName, int entityId, String policyNumber, String occupation, String specialisation, String startDate, String endDate, String insuredBy, boolean isCancelled, String cancellationOn, String kenyaDateTime, String cancellationReason, String certificateStatus, int certificateClassificationId, int statusCode, String requestLocalDate, String policyStartDate, String policyEndDate) {
        this.certificateNumber = certificateNumber;
        this.memberCompanyName = memberCompanyName;
        this.entityId = entityId;
        this.policyNumber = policyNumber;
        this.occupation = occupation;
        this.specialisation = specialisation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.insuredBy = insuredBy;
        this.isCancelled = isCancelled;
        this.cancellationOn = cancellationOn;
        this.kenyaDateTime = kenyaDateTime;
        this.cancellationReason = cancellationReason;
        this.certificateStatus = certificateStatus;
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

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(String specialisation) {
        this.specialisation = specialisation;
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

    public String getInsuredBy() {
        return insuredBy;
    }

    public void setInsuredBy(String insuredBy) {
        this.insuredBy = insuredBy;
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
