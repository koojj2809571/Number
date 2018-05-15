package com.opp.number.bean;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class ConnectionSetting implements Serializable{

    //是否为连线起点
    private boolean isPort;
    public boolean isPort() {
        return isPort;
    }
    public void setPort(boolean port) {
        isPort = port;
    }

    //如果是连线起点，起点数字
    private int portNumber;
    public int getPortNumber() {
        return portNumber;
    }
    public void setPortNumber(int portNumber) {
        this.portNumber = isPort ? portNumber : 0;
    }

    //如果是起点，起点连接线的颜色
    private int lineColor;
    public int getLineColor() {
        return lineColor;
    }
    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }


}
