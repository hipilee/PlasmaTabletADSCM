package com.cylinder.www.thread;

import com.cylinder.www.env.person.businessobject.Donor;
import com.cylinder.www.env.Signal;
import com.cylinder.www.env.net.InterfaceNetworkCreator;
import com.cylinder.www.env.net.ServerNetworkCreator;
import com.cylinder.www.env.net.InterfaceNetwork;

import org.apache.http.client.methods.HttpGet;

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
        // Generate the httpGet for confirm/puncture/start/fist/end.
        HttpGet requestCPSFE = new HttpGet(networkInterface.getJsonURL()+"/rest/mails/A001");
        requestCPSFE.setHeader("json", "application/json");
        requestCPSFE.setHeader("Content-Type", "application/json");

        while (true) {

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
