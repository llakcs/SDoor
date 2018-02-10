package com.dchip.door.smartdoorsdk.Bean;

/**
 * Created by jelly on 2017/8/3.
 */

public class AppUpdateModel {
    //文件名
    String address ;
    //状态暂时无用
    int status;
    //更新信息
    String remark;
    //下载地址
    String detailAddress;
    //校验码
    String md5;
    //命令分类 默认为1
    int type;
    //
    String version;


    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


}
