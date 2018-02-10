package com.dchip.door.smartdoorsdk.Bean;

import java.util.List;

/**
 * Created by jelly on 2017/9/4.
 */

public class ApiGetCardListModel {
    List<CardsModel> list;

    public List<CardsModel> getData() {
        return list;
    }

    public void setData(List<CardsModel> list) {
        this.list = list;
    }
}
