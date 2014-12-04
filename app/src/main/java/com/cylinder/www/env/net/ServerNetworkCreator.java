package com.cylinder.www.env.net;

/**
 * Created by hipilee on 2014/11/21.
 */
public class ServerNetworkCreator implements InterfaceNetworkCreator {
    @Override
    public InterfaceNetwork creator() {
        return ServerNetwork.getInstance();
    }
}
