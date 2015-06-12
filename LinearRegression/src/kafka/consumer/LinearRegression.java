package kafka.consumer;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import main.KafkaTopicConsumer;

public class LinearRegression {
    private static LinearRegression lr = null;
    public static LinearRegression getInstance() {
        return lr == null ? (lr = new LinearRegression()) : lr;
    }
    private LinearRegression(){}
	public void collectBuffer(double price) {
	    
	}
	public void jri() {
	    Rengine re = new Rengine(new String[]{"--vanilla"}, false, null);
	    re.eval("source('../LinearRegression/resource/TaGenerator.R')");
	    REXP value = re.eval("inputClose(inputV)");
        // REXP value = re.eval("as.vector(data.frame(a<-inputClose(inputV)))");
        double a = value.asDouble();
        System.out.println(a);
	}
}