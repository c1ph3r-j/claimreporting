package com.aki.claimreporting;

public class ThirdPartyModel {
    private String registrationNumber;
    private String model;
    private String make;
    private String color;

    public ThirdPartyModel(String registrationNumber, String model, String make, String color) {
        this.registrationNumber = registrationNumber;
        this.model = model;
        this.make = make;
        this.color = color;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
