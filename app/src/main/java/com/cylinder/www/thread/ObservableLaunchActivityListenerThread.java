package com.cylinder.www.thread;

import android.util.Log;

import com.cylinder.www.env.Signal;
import com.cylinder.www.env.net.InterfaceNetwork;
import com.cylinder.www.env.net.InterfaceNetworkCreator;
import com.cylinder.www.env.net.ServerInformationTransaction;
import com.cylinder.www.env.net.ServerNetworkCreator;
import com.cylinder.www.env.person.businessobject.Donor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by hipilee on 2014/12/2.
 */
public class ObservableLaunchActivityListenerThread extends Thread {
    private Donor donor;


    private ObservableLaunchHint observableLaunchHint;
    private InterfaceNetwork networkInterface;
    private InterfaceNetworkCreator networkCreatorInterface;

    public ObservableLaunchActivityListenerThread(Donor donor) {
        this.donor = donor;
        this.observableLaunchHint = new ObservableLaunchHint();
        this.networkCreatorInterface = new ServerNetworkCreator();
        this.networkInterface = networkCreatorInterface.creator();
    }

    public void addObserver(Observer observer) {
        observableLaunchHint.addObserver(observer);
    }

    public void deleteObserver(Observer observer) {
        observableLaunchHint.deleteObserver(observer);
    }

    public void notifyObservers(Signal signal) {
        observableLaunchHint.notifyObservers(signal);
    }

    @Override
    public void run() {
        super.run();

        JSONObject jsonObject;
        String result = null;

        while (true) {

            // Get jsonObject
            ServerInformationTransaction serverInformationTransaction = new ServerInformationTransaction();
            String networkPath = networkInterface.getJsonURL()+"/rest/mails/A001";
            Log.e("error",networkPath);
            jsonObject =  serverInformationTransaction.fetchJSONObjectFromServer(networkPath);
            if (null == jsonObject){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            try {
                result = jsonObject.getString("header");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(new String("confirm").equals(result)){
                String id = null;
                try {
                    id = jsonObject.getString("content");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                donor.setDonorID(id);
                initialDonorInfo(donor,donor.getDonorID(),networkInterface);
                observableLaunchHint.notifyObservers(Signal.CONFIRM);
                break;
            }

        }
    }

    private void initialDonorInfo(Donor donor, String donorid,InterfaceNetwork networkInterface){

        ServerInformationTransaction serverInformationTransaction = new ServerInformationTransaction();
        String networkPath = networkInterface.getJsonURL()+"/rest/donors/"+donorid;
        JSONObject jsonObject = serverInformationTransaction.fetchJSONObjectFromServer(networkPath);
        String donorName=null;
        try {
            donorName = jsonObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        donor.setUserName(donorName);
    }

    private class ObservableLaunchHint extends Observable {
        private ArrayList<Observer> arrayListObserver;

        private ObservableLaunchHint() {
            arrayListObserver = new ArrayList<Observer>();
        }

        @Override
        public void addObserver(Observer observer) {
            super.addObserver(observer);
            arrayListObserver.add(observer);
        }

        @Override
        public synchronized void deleteObserver(Observer observer) {
            super.deleteObserver(observer);
            arrayListObserver.remove(observer);
        }

        @Override
        public void notifyObservers(Object data) {
            super.notifyObservers(data);
            for (Observer observer : arrayListObserver) {
                observer.update(observableLaunchHint, data);
            }
        }
    }
}
