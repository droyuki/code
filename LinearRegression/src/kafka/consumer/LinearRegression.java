package kafka.consumer;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import main.KafkaTopicConsumer;
import main.KafkaTopicProducer;

public class LinearRegression {
    private static LinearRegression lr = null;

    public static LinearRegression getInstance() {
        return lr == null ? (lr = new LinearRegression()) : lr;
    }

    private LinearRegression() {
    }

    public void collectBuffer(double price) {
        // 暫存近90分鐘內的close price
        Queue<Double> priceBuffer = new LinkedList<Double>();
        if (priceBuffer.size() < 90) {
            priceBuffer.add(price);
        } else {
            double[] priceArray = new double[priceBuffer.size()];
            Iterator<Double> iterator = priceBuffer.iterator();
            for (int i = 0; i < priceBuffer.size(); i++) {
                priceArray[i] = iterator.next();
            }
            jri(priceArray);
        }
    }

    public void jri(double[] inputArr) {
        Rengine re = new Rengine(new String[] { "--vanilla" }, false, null);
        re.eval("source('../LinearRegression/resource/TaGenerator.R')");
        re.assign("inputV", inputArr);
        REXP value = re.eval("inputClose(inputV)");
        // REXP value = re.eval("as.vector(data.frame(a<-inputClose(inputV)))");
        double a = value.asDouble();
        System.out.println(a);
        sendSignal(a);
    }

    public void sendSignal(double y) {
        String topic = "LinearRegression";
        String sendMe="";
        if (y >= 3.5) { //buy

        } else { //sell
            
        }
        KafkaTopicProducer.getInstance().send(topic, sendMe);
    }
}