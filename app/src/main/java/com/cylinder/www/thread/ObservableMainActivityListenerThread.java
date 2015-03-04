package com.cylinder.www.thread;

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
 * Created by hipilee on 2014/11/19.
 */
public class ObservableMainActivityListenerThread extends Thread {
    private Donor donor;


    private ObservableHint hintObservable;
    private InterfaceNetwork networkInterface;
    private InterfaceNetworkCreator networkCreatorInterface;

    public ObservableMainActivityListenerThread(Donor donor) {
        this.donor = donor;
        this.hintObservable = new ObservableHint();
        this.networkCreatorInterface = new ServerNetworkCreator();
        this.networkInterface = networkCreatorInterface.creator();
    }

    public void addObserver(Observer observer) {
        hintObservable.addObserver(observer);
    }

    public void deleteObserver(Observer observer) {
        hintObservable.deleteObserver(observer);
    }

    public void notifyObservers(Signal signal) {
        hintObservable.notifyObservers(signal);
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
            jsonObject = serverInformationTransaction.fetchJSONObjectFromServer(networkPath);

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

            if(new String("puncture").equals(result)){
                hintObservable.notifyObservers(Signal.PUNCTURE);
            }
            else if(new String("start").equals(result)){
                hintObservable.notifyObservers(Signal.START);
            }
            else if(new String("fist").equals(result)){
                hintObservable.notifyObservers(Signal.FIST);
            }
            else if(new String("end").equals(result)){
                hintObservable.notifyObservers(Signal.END);
                break;
            }

        }
    }

    private class ObservableHint extends Observable {
        private ArrayList<Observer> arrayListObserver;

        private ObservableHint() {
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
                observer.update(hintObservable, data);
            }
        }
    }


}
