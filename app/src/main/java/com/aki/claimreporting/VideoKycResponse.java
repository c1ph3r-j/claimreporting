package com.aki.claimreporting;

public class VideoKycResponse {
    public String Videokycseqno;
    public String Displaysec;
    public String VideoKyctext;

    public VideoKycResponse(String Videokycseqno, String Displaysec, String VideoKyctext) {

        this.Videokycseqno = Videokycseqno;
        this.Displaysec = Displaysec;
        this.VideoKyctext = VideoKyctext;

    }

    public String getVideokycseqno() {
        return Videokycseqno;
    }

    public void setVideokycseqno(String videokycseqno) {
        Videokycseqno = videokycseqno;
    }

    public String getDisplaysec() {
        return Displaysec;
    }

    public void setDisplaysec(String displaysec) {
        Displaysec = displaysec;
    }

    public String getVideoKyctext() {
        return VideoKyctext;
    }

    public void setVideoKyctext(String videoKyctext) {
        VideoKyctext = videoKyctext;
    }
}