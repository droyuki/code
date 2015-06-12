package kafka.consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class KafkaTest{
    public static void main(String[] args) {
        Map<String, String> msgMap = new HashMap<String, String>();
        Map<String, String> resultMap = new HashMap<String, String>();
        String message="msgKey:taiwan_future_TXM5_5s@1434082773031;8:9261.0;5:9261.0;7:9261.0;Date:1434082773031;9:270216.0;6:9261.0;KBAR:taiwan_future_TXM5_5s_1434082773031;";
        msgMap = transToPriceMap(message);
        resultMap = filterKBarID(msgMap);   
        System.out.println(resultMap.get("KBAR").split("_")[3]);
    }
    public static Map<String, String> transToPriceMap(String valStr) {
        Map<String, String> retMe = new HashMap<String, String>();
        String[] valStrAry = valStr.split(";");
        for (String val : valStrAry) {
            String[] kvEntry = val.split(":");
            retMe.put(kvEntry[0], kvEntry[1]);
        }
        return retMe;
    }
    public static Map<String, String> filterKBarID(Map<String, String> valMap) {
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