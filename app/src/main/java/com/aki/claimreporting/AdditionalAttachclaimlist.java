package com.aki.claimreporting;

public class AdditionalAttachclaimlist {

    public String Attachmentbyte;
    public String Comments;

    public AdditionalAttachclaimlist(String Attachmentbyte, String Comments) {
        this.Attachmentbyte = Attachmentbyte;
        this.Comments = Comments;
    }

    public String getAttachmentbyte() {
        return Attachmentbyte;
    }

    public void setAttachmentbyte(String attachmentbyte) {
        Attachmentbyte = attachmentbyte;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }


}