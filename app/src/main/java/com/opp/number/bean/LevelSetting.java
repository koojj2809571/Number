package com.opp.number.bean;


import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.BmobObject;

public class LevelSetting extends BmobObject implements Serializable {
    private String name;
    private List<ConnectionSetting> initSetting;
    private Integer orderNumber;

    public Integer getOrder() {
        return orderNumber;
    }

    public void setOrder(Integer order) {
        this.orderNumber = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ConnectionSetting> getInitSetting() {
        return initSetting;
    }

    public void setInitSetting(List<ConnectionSetting> initSetting) {
        this.initSetting = initSetting;
    }

}