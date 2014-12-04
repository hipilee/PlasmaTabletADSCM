package com.cylinder.www.env.net;

/**
 * Created by hipilee on 2014/11/21.
 */
public class ServerNetwork implements InterfaceNetwork {
    private static ServerNetwork ourInstance = new ServerNetwork();

    public static ServerNetwork getInstance() {
        return ourInstance;
    }

    private ServerNetwork() {
    }

    @Override
    public String getJsonURL() {
        return "http://192.168.0.92";
    }
}
