package com.jiajunhui.udp_service;

import java.io.Serializable;
import java.net.SocketAddress;

/**
 * Created by Taurus on 16/11/13.
 */

public class UDPEvent implements Serializable {
    private int eventCode;
    private String eventMessage;
    private String deviceInfo;
    private SocketAddress socketAddress;

    public UDPEvent() {
    }

    public UDPEvent(int eventCode, String eventMessage, String deviceInfo, SocketAddress socketAddress) {
        this.eventCode = eventCode;
        this.eventMessage = eventMessage;
        this.deviceInfo = deviceInfo;
        this.socketAddress = socketAddress;
    }

    public int getEventCode() {
        return eventCode;
    }

    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }
}
