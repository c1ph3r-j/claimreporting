package com.aki.claimreporting;

public class BrokerInfo {
    public String brokerID;
    public String brokerName;

    public BrokerInfo() {
    }

    public BrokerInfo(String brokerID, String brokerName) {
        this.brokerID = brokerID;
        this.brokerName = brokerName;
    }

    public String getBrokerID() {
        return brokerID;
    }

    public void setBrokerID(String brokerID) {
        this.brokerID = brokerID;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }


}

