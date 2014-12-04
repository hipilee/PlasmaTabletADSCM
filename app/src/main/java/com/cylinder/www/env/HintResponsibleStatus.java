package com.cylinder.www.env;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hipilee on 2014/11/21.
 */
public class HintResponsibleStatus {
    private static HintResponsibleStatus ourInstance = new HintResponsibleStatus();

    public static HintResponsibleStatus getInstance() {
        return ourInstance;
    }

    private Map<String,Integer> responsibleHintStatus;

    private HintResponsibleStatus() {
        this.responsibleHintStatus = new HashMap<String,Integer>();
        this.responsibleHintStatus.put("confirm",new Integer(0));
        this.responsibleHintStatus.put("puncture",new Integer(0));
        this.responsibleHintStatus.put("confirm",new Integer(0));
        this.responsibleHintStatus.put("fist",new Integer(0));
        this.responsibleHintStatus.put("end",new Integer(0));
    }
}
