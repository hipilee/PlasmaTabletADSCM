package com.cylinder.www.env.net.server;

import com.cylinder.www.env.net.server.AbstractNetworkAddress;

/**
 * Created by Administrator on 2015/9/19 0019.
 */
public class VideoServerNetworkAddress extends AbstractNetworkAddress {
    @Override
    public String getIp() {
        return super.ip;
    }

    @Override
    public void setIp(String ip) {
        super.ip = ip;
    }

    @Override
    public int getPort() {
        return super.port;
    }

    @Override
    public void setPort(int port) {
        super.port = port;
    }
}
