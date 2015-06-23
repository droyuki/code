package kafka.consumer;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import main.KafkaTopicProducer;

public class LinearRegression {
    Rengine re;

    public LinearRegression() {
        System.out.println("Linear Regression start!");
    }

    public void jri(double[] inputArr, String date, String currentPrice) {
        if (re == null)
            re = new Rengine(new String[] { "--vanilla" }, false, null);
        // test code
        int[] intArray = { 33, 44, 55 };
        System.out.println(re.assign("x", intArray));
        System.out.println("JRI success !");
        // test code
        System.out.println("Get price !");
        re.eval("source('~/Desktop/rSource/TaGenerator.R')");
        re.assign("inputV", inputArr);
        System.out.println("Calculating...");
        REXP value = re.eval("inputClose(inputV)");
        // REXP value = re.eval("as.vector(data.frame(a<-inputClose(inputV)))");
        double a = value.asDouble();
        System.out.println("Done ! y = " + a);
        sendSignal(a, date, currentPrice);
    }

    public void sendSignal(double y, String date, Sting currentPrice) {
        System.out.println("Get result: " + y);
        String topic = "Rmodel";
        String sendMe = "";
        if (y <= 3.5) { // sell
            sendMe = "{\"method\":\"Linear\",\"signal\":0,\"datetime\":\""
                    + date + "\"}";
        } else { // buy
            sendMe = "{\"method\":\"Linear\",\"signal\":1,\"datetime\":\""
                    + date + "\"}";
        }
        System.out.println(sendMe);
        KafkaTopicProducer.getInstance().send(topic, sendMe);
        System.out.println("Signal has been sent.");
    }
}
