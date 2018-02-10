package com.dchip.door.smartdoorsdk.Bean;

import java.util.List;

/**
 * Created by jelly on 2018/1/3.
 */

public class ApiGetPropManagement {
    List<ManagementMemberModel> list;

    public List<ManagementMemberModel> getList() {
        return list;
    }

    public void setList(List<ManagementMemberModel> list) {
        this.list = list;
    }
}
