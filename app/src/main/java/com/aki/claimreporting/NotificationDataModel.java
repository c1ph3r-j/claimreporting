package com.aki.claimreporting;

public class NotificationDataModel {

    public String notificationID;
    public String userID;
    public String title;
    public String description;
    public String notificationTypeID;
    public String notificationInfo;
    public String androidFireBaseID;
    public String email;
    public String phoneNo;
    public String typeID;

    public NotificationDataModel(String notificationID, String userID, String title, String description, String notificationTypeID, String notificationInfo, String androidFireBaseID, String email, String phoneNo, String typeID) {
        this.notificationID = notificationID;
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.notificationTypeID = notificationTypeID;
        this.notificationInfo = notificationInfo;
        this.androidFireBaseID = androidFireBaseID;
        this.email = email;
        this.phoneNo = phoneNo;
        this.typeID = typeID;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotificationTypeID() {
        return notificationTypeID;
    }

    public void setNotificationTypeID(String notificationTypeID) {
        this.notificationTypeID = notificationTypeID;
    }

    public String getNotificationInfo() {
        return notificationInfo;
    }

    public void setNotificationInfo(String notificationInfo) {
        this.notificationInfo = notificationInfo;
    }

    public String getAndroidFireBaseID() {
        return androidFireBaseID;
    }

    public void setAndroidFireBaseID(String androidFireBaseID) {
        this.androidFireBaseID = androidFireBaseID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }


}