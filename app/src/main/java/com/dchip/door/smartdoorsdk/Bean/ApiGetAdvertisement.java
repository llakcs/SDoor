package com.dchip.door.smartdoorsdk.Bean;

import java.util.List;

/**
 * Created by jelly on 2018/1/4.
 */

public class ApiGetAdvertisement {
    int advType;
    List<AdvertisementModel> bannerVideoList;
    List<AdvertisementModel> bannerPicList;

    public List<AdvertisementModel> getBannerVideoList() {
        return bannerVideoList;
    }

    public void setBannerVideoList(List<AdvertisementModel> bannerVideoList) {
        this.bannerVideoList = bannerVideoList;
    }

    public List<AdvertisementModel> getBannerPicList() {
        return bannerPicList;
    }

    public void setBannerPicList(List<AdvertisementModel> bannerPicList) {
        this.bannerPicList = bannerPicList;
    }

    public int getAdvType() {
        return advType;
    }

    public void setAdvType(int advType) {
        this.advType = advType;
    }
}
