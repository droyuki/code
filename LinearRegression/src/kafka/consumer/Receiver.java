package kafka.consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import main.MsgReceiver;

public class Receiver extends MsgReceiver {

    public void execute(String message) {
        // message sample
        // msgKey:taiwan_future_TFM5_1s@1434082774154;8:1194.4;5:1194.4;7:1194.4;Date:1434082774154;9:3945.0;6:1194.4;KBAR:taiwan_future_TFM5_1s_1434082774154;
        // msgKey:taiwan_future_TFM5_1m@1434087245764;8:1192.0;5:1192.2;7:1192.2;Date:1434087245764;9:50866.0;6:1192.6;KBAR:taiwan_future_TFM5_1m_1434087245764;
        Map<String, String> msgMap = new HashMap<String, String>();
        Map<String, String> resultMap = new HashMap<String, String>();
        msgMap = transToPriceMap(message);
        resultMap = filterKBarID(msgMap);

        // I only want 1m
        String kbar = resultMap.get("KBAR");
        String time = kbar.split("_")[3];
        if (time.equalsIgnoreCase("1m")) {
            double closePrice = Double.parseDouble(resultMap.get("Close"));
            LinearRegression.getInstance().collectBuffer(closePrice);
        }
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