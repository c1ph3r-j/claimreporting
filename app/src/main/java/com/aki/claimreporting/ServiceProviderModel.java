package com.aki.claimreporting;

public class ServiceProviderModel {

    public String entityName;
    public String Phnnum;
    public String sector;
    public String address1;
    public String address2;
    public String address3;
    public String city;
    public String county;
    public String distance;
    public String doctype;
    public String entityID;
    public String latdt;
    public String longdt;


    public ServiceProviderModel(String entityName, String sector, String address1, String address2, String address3, String city, String county, String Phnnum, String distance, String doctype, String entityID, String latdt, String longdt) {
        this.entityName = entityName;
        this.sector = sector;
        this.Phnnum = Phnnum;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.city = city;
        this.county = county;
        this.distance = distance;
        this.doctype = doctype;
        this.entityID = entityID;
        this.latdt = latdt;
        this.longdt = longdt;

    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getPhnnum() {
        return Phnnum;
    }

    public void setPhnnum(String phnnum) {
        Phnnum = phnnum;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public String getLatdt() {
        return latdt;
    }

    public void setLatdt(String latdt) {
        this.latdt = latdt;
    }

    public String getLongdt() {
        return longdt;
    }

    public void setLongdt(String longdt) {
        this.longdt = longdt;
    }


}