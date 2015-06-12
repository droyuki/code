package kafka.consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;

import main.MsgReceiver;

public class Receiver extends MsgReceiver {
    Rengine re = new Rengine(new String[] { "--vanilla" }, false, null);

    public void execute(String message) {
        Map<String, String> msgMap = new HashMap<String, String>();
        Map<String, String> resultMap = new HashMap<String, String>();
        msgMap = transToPriceMap(message);
        resultMap = filterKBarID(msgMap);
        double closePrice = Double.parseDouble(resultMap.get("Close"));
    }
    private Map<String, String> transToPriceMap(String valStr) {
        Map<String, String> retMe = new HashMap<String, String>();
        String[] valStrAry = valStr.split(";");
        for (String val : valStrAry) {
            String[] kvEntry = val.split(":");
            retMe.put(kvEntry[0], kvEntry[1]);
        }
        return retMe;
    }
    private Map<String, String> filterKBarID(Map<String, String> valMap) {
        Map<String, String> retMe = new HashMap<String, String>();
        for (Entry<String, String> val : valMap.entrySet()) {
            if (val.getKey().equals("5"))
                retMe.put("Open", val.getValue());
            else if (val.getKey().equals("6"))
                retMe.put("High", val.getValue());
            else if (val.getKey().equals("7"))
                retMe.put("Close", val.getValue());
            else if (val.getKey().equals("8"))
                retMe.put("Low", val.getValue());
            else if (val.getKey().equals("9"))
                retMe.put("Volume", val.getValue());
            else
                retMe.put(val.getKey(), val.getValue());
        }
        return retMe;
    }

}