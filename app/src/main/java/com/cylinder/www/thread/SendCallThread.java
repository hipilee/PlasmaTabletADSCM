package com.cylinder.www.thread;

import com.cylinder.www.env.net.InterfaceNetwork;
import com.cylinder.www.env.net.InterfaceNetworkCreator;
import com.cylinder.www.env.net.ServerInformationTransaction;
import com.cylinder.www.env.net.ServerNetworkCreator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alvin on 2015/3/3.
 */
public class SendCallThread extends Thread {

    private InterfaceNetwork network = null;
    private InterfaceNetworkCreator NetworkCreator = null;

    @Override
    public void run() {
        super.run();
        NetworkCreator = new ServerNetworkCreator();
        network = NetworkCreator.creator();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("header","call");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerInformationTransaction serverInformationTransaction = new ServerInformationTransaction();
        serverInformationTransaction.sendToServer(network.getJsonURL()+"/rest/mails/Q001",jsonObject);
    }
}
