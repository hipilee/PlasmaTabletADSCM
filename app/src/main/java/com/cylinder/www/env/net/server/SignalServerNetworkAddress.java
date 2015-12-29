package com.cylinder.www.env.net.server;

/**
 * Created by Administrator on 2015/10/4 0004.
 */
public class SignalServerNetworkAddress extends AbstractNetworkAddress {
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
