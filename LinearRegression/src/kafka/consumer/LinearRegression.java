package kafka.consumer;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import main.KafkaTopicProducer;

public class LinearRegression {
    //private static LinearRegression lr = null;
//    private Queue<Double> priceBuffer = new LinkedList<Double>();

    // public static LinearRegression getInstance() {
    // return lr == null ? (lr = new LinearRegression()) : lr;
    // }
    Rengine re;
    public LinearRegression() {
        System.out.println("LR create!");
    }

//     public void collectBuffer(double price) {
//     // 暫存近90分鐘內的close price
//     if (priceBuffer.size() < 90) {
//     System.out.println("Collecting price...");
//     priceBuffer.add(price);
//     System.out.println("Now buffer size is: "+priceBuffer.size());
//     } else {
//     System.out.println("Buffer queue is full, size = "+priceBuffer.size());
//     double[] priceArray = new double[priceBuffer.size()];
//     Iterator<Double> iterator = priceBuffer.iterator();
//     for (int i = 0; i < priceBuffer.size(); i++) {
//     priceArray[i] = iterator.next();
//     }
//     jri(priceArray);
//     }
//     }

    public void jri(double[] inputArr) {
        if(re == null)
            re = new Rengine(new String[] {"--vanilla"}, false, null);
        //test code
        int[] intArray = {33, 44, 55};
        System.out.println(re.assign("x", intArray));
        System.out.println("JRI success !");
        //test code
        System.out.println("Get price !");
        re.eval("source('~/Desktop/rSource/TaGenerator.R')");
        re.assign("inputV", inputArr);
        System.out.println("Calculating...");
        REXP value = re.eval("inputClose(inputV)");
        // REXP value = re.eval("as.vector(data.frame(a<-inputClose(inputV)))");
        double a = value.asDouble();
        System.out.println("Done ! y = "+a);
        sendSignal(a);
    }

    public void sendSignal(double y) {
        System.out.println("Get result: "+y);
        String topic = "LinearRegression";
        String sendMe="";
        if (y < 3.5) { 
            sendMe="=========Sell!!=========";
        } else { 
            sendMe="=========Buy!!=========";
        }
        System.out.println(sendMe);
        KafkaTopicProducer.getInstance().send(topic, sendMe);
        System.out.println("Signal has been sent.");
    }
}
