package com.cylinder.www.thread;

import com.cylinder.www.env.Signal;
import com.cylinder.www.env.net.FilterSignal;
import com.cylinder.www.env.net.RecordState;
import com.cylinder.www.env.person.businessobject.Donor;

import java.util.Observable;
import java.util.Random;

/**
 * Created by Administrator on 2015/9/30 0030.
 */
public class ObserverZSDCSignalRecordAndFilter implements java.util.Observer {
    public ObserverZSDCSignalRecordAndFilter(RecordState recordState, FilterSignal filterSignal) {
        this.recordState = recordState;
        this.filterSignal = filterSignal;
    }

    RecordState recordState;
    FilterSignal filterSignal;

    @Override
    public void update(Observable observable, Object data) {
        switch ((Signal) data) {

            case CONFIRM:
                // Modify the signal filter.
                filterSignal.setConfirm(false);
                filterSignal.setPuncture(true);
                filterSignal.setFist(false);
                filterSignal.setStart(true);
                filterSignal.setEnd(true);

                // Record the state
                recordState.setConfirm(true);
                recordState.setDonorID(Donor.getDonorID());
                recordState.setDonorName(Donor.getUserName());
                break;

            case PUNCTURE:
                // Modify the signal filter.
                filterSignal.setPuncture(false);
                filterSignal.setStart(true);
                filterSignal.setFist(false);
                filterSignal.setEnd(true);

                // Record the state
                recordState.setPuncture(true);
                break;

            case START:
                // Modify the signal filter.
                filterSignal.setStart(false);
                filterSignal.setFist(true);
                filterSignal.setEnd(true);

                // Record the state
                recordState.setStart(true);
                break;

            case END:
                // Modify the signal filter.
                filterSignal.setFist(false);
                filterSignal.setEnd(true);

                // Record the state
                recordState.setEnd(true);
                break;

            default:
                break;

        }
    }
}

