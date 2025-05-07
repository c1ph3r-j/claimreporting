package com.aki.claimreporting;

public class SupportObjectList {

    public String objectTypeID;
    public String objectTypeName;
    public String object1;
    public String object2;
    public String object3;
    public String object4;
    public String object5;


    public SupportObjectList(String objectTypeID, String objectTypeName, String object1, String object2, String object3, String object4, String object5) {
        this.objectTypeID = objectTypeID;
        this.objectTypeName = objectTypeName;
        this.object1 = object1;
        this.object2 = object2;
        this.object3 = object3;
        this.object4 = object4;
        this.object5 = object5;
    }

    public String getObjectTypeID() {
        return objectTypeID;
    }

    public void setObjectTypeID(String objectTypeID) {
        this.objectTypeID = objectTypeID;
    }

    public String getObjectTypeName() {
        return objectTypeName;
    }

    public void setObjectTypeName(String objectTypeName) {
        this.objectTypeName = objectTypeName;
    }

    public String getObject1() {
        return object1;
    }

    public void setObject1(String object1) {
        this.object1 = object1;
    }

    public String getObject2() {
        return object2;
    }

    public void setObject2(String object2) {
        this.object2 = object2;
    }

    public String getObject3() {
        return object3;
    }

    public void setObject3(String object3) {
        this.object3 = object3;
    }

    public String getObject4() {
        return object4;
    }

    public void setObject4(String object4) {
        this.object4 = object4;
    }

    public String getObject5() {
        return object5;
    }

    public void setObject5(String object5) {
        this.object5 = object5;
    }

}
