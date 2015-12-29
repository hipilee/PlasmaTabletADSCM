package com.cylinder.www.env.net.server;

/**
 * Created by hipilee on 2014/11/21.
 */
public abstract class AbstractNetworkAddress {
    protected String ip;
    protected  int port;

    abstract public String getIp();

    abstract public void setIp(String ip);

    abstract public int getPort();

    abstract public void setPort(int port);
}
