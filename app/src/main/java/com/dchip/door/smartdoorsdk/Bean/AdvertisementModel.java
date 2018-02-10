package com.dchip.door.smartdoorsdk.Bean;

/**
 * Created by jelly on 2018/1/4.
 */

public class AdvertisementModel {
    String photo="";
    String content = "";
    String md5 = "";

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
