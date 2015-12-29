package com.cylinder.www.thread;

import android.util.Log;

import com.cylinder.www.env.Signal;
import com.cylinder.www.env.net.FilterSignal;
import com.cylinder.www.env.net.RecordState;
import com.cylinder.www.env.net.ServerInformationTransaction;
import com.cylinder.www.env.person.businessobject.Donor;
import com.cylinder.www.utils.file.SelfFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * Created by hipilee on 2014/11/19.
 */
// Consider using AsyncTask or HandlerThread
public class ObservableZXDCSignalListenerThread extends Thread {

    private HashMap<String, Boolean> stringBooleanMap;
    private Donor donor = Donor.getInstance();

    private ObservableHint observableHint;

    public Boolean getIsContinue() {
        return isContinue;
    }

    private Boolean isContinue = true;
    private Boolean isPause = true;

    private int n = 0;
    private RecordState recordState;
    private RecoverState recoverState;
    private FilterSignal filterSignal;
    private CheckSignal checkSignal;

    public ObservableZXDCSignalListenerThread(RecordState recordState, FilterSignal filterSignal) {
        Log.e("camera", "ObservableZXDCSignalListenerThread constructor" + "construct");
        this.observableHint = new ObservableHint();

        this.recordState = recordState;
        this.recoverState = new RecoverState();
        this.filterSignal = filterSignal;
        this.checkSignal = new CheckSignal(this.filterSignal);


    }

    private void initialization(Map<String, Boolean> map) {
        map.put("confirm", false);
        map.put("puncture", false);
        map.put("start", false);
        map.put("fist", false);
        map.put("end", false);
    }

    public void addObserver(Observer observer) {
        observableHint.addObserver(observer);
    }

    public void deleteObserver(Observer observer) {
        observableHint.deleteObserver(observer);
    }

    public void notifyObservers(Signal signal) {
        observableHint.notifyObservers(signal);
    }

    public void setIsContinue(Boolean isContinue) {
        this.isContinue = isContinue;
    }

    @Override
    public void run() {
        super.run();

        // there must be a pause if without there will be something wrong.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // there must be a pause if without there will be something wrong.

        recoverState.recover(recordState, observableHint);
        ServerInformationTransaction serverInformationTransaction = new ServerInformationTransaction();


        while (isContinue) {

            //*************************************************************
            Log.e("camera", " signal n is " + n);

//            n++;
//            if (1 == n && checkSignal.check(Signal.CONFIRM)) {
//                Donor.setUserName(Integer.toString((new Random()).nextInt(100)));
//
//                dealSignal(Signal.CONFIRM);
//
//
//            }
//
//            if (2 == n && checkSignal.check(Signal.PUNCTURE)) {
////                try {
////                    Thread.sleep(3000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
//
////                dealSignal(Signal.PUNCTURE);
//
//            }
//
//            if (3 == n && checkSignal.check(Signal.START)) {
////                try {
////                    Thread.sleep(3000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
//                dealSignal(Signal.START);
//            }
//
//            if (4 == n && checkSignal.check(Signal.FIST)) {
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                dealSignal(Signal.FIST);
//            }
//
//            if (20 == n && checkSignal.check(Signal.END)) {
//
//                dealSignal(Signal.END);
//
//            }

//                *******************************


            // Recover the state.
            String header = null;
            // Get jsonObject
            JSONObject jsonObject = serverInformationTransaction.fetchJSONObjectFromServer("http://192.168.0.94:8989/api/mail/001");
            Log.e("camera", "run");
            if (jsonObject != null) {
                try {
                    header = jsonObject.getString("header");

                    if (header.equals("confirm") && checkSignal.check(Signal.CONFIRM)) {
                        Donor donor = Donor.getInstance();
                        donor.setUserName(jsonObject.getString("content"));
                        dealSignal(Signal.CONFIRM);
                        Log.e("camera", "CONFIRM");
                        selfSleep(60000);

                    } else if (header.equals("start") && checkSignal.check(Signal.START)) {

                        dealSignal(Signal.START);
                        Donor donor = Donor.getInstance();
                        donor.setDonorID(jsonObject.getString("content"));
                        Log.e("camera", "START");

                    } else if (header.equals("end") && checkSignal.check(Signal.END)) {
                        dealSignal(Signal.END);
                        Log.e("camera", "START");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                }

            }

            //**********************************************************
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        finishReceivingSignal();
    }

    public synchronized void finishReceivingSignal() {
        Log.e("camera", " finish");
        notify();
    }

    public synchronized void commitSignal(Boolean isInitiative) {
        try {
            Log.e("camera", "waitToCommitSignal " + 1);

            wait();

            Log.e("camera", "waitToCommitSignal " + 2);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }

        // If we close the APP initiative,then reset the states.
        if (isInitiative) {
            recordState.reset();
        }
        recordState.commit();
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
                observer.update(observableHint, data);
            }
        }
    }


    private void dealSignal(Signal signal) {
        switch (signal) {

            case CONFIRM:
                observableHint.notifyObservers(Signal.CONFIRM);
                break;

            case PUNCTURE:
                observableHint.notifyObservers(Signal.PUNCTURE);
                break;

            case START:
                observableHint.notifyObservers(Signal.START);
                break;

            case FIST:
                observableHint.notifyObservers(Signal.FIST);
                break;

            case END:
                observableHint.notifyObservers(Signal.END);
                break;

            default:
                break;

        }
    }

    private class RecoverState {
        public void recover(RecordState recordState, ObservableHint observerZXDCSignalHandler) {
            if (!recordState.getEnd()) {
                Log.e("camera", "recover " + true);

                Boolean flag[] = new Boolean[3];
                flag[0] = recordState.getConfirm();
//                flag[1] = recordState.getPuncture();
                flag[2] = recordState.getStart();

                if (flag[0]) {
                    dealSignal(Signal.CONFIRM);
                    Log.e("camera", "recover " + "confirm");
                    selfSleep(1000);
                    if (flag[2]) {
                        dealSignal(Signal.START);
                        Log.e("camera", "recover " + "puncture");
                    }
                }
            } else {
                Log.e("camera", "recover else" + false);

                recordState.reset();
            }
        }
    }

    private class CheckSignal {
        private FilterSignal filterSignal;

        public CheckSignal(FilterSignal filterSignal) {
            this.filterSignal = filterSignal;
        }

        public Boolean check(Signal signal) {

            switch (signal) {
                case CONFIRM:
                    return filterSignal.checkConfirm();

                case PUNCTURE:
                    return filterSignal.checkPuncture();

                case START:
                    return filterSignal.checkStart();

                case FIST:
                    return filterSignal.checkFist();

                case END:
                    return filterSignal.checkEnd();
            }
            return false;
        }
    }

    public void selfSleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
