package com.aki.claimreporting;

public class DamageImageClaim {

    public String ImageURL;

    public DamageImageClaim() {
    }

    public DamageImageClaim(String ImageURL) {

        this.ImageURL = ImageURL;
    }

    public String getImageURL() {
        return this.ImageURL;
    }
}