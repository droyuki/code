import java.util.ArrayList;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class Test {
	public static void main(String[] args) {
		Rengine re = new Rengine(new String[]{"--vanilla"}, false, null);
		// the engine creates R is a new thread, so we should wait until it's
		// ready
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			System.exit(0);
		}
		// load own R script
		re.eval("source('../LinearRegression/resource/TaGenerator.R')");

		// Test input double array
		ArrayList<Double> testInput = new ArrayList<Double>();
		for(double i = 9000;i<9389;i++) {
		    testInput.add(i);
		}
		double[] inputArr = new double[testInput.size()];
	    for(int i = 0;i<testInput.size();i++) {
	        inputArr[i] = testInput.get(i);
	    }
		re.assign("inputV", inputArr);
		REXP value = re.eval("inputClose(inputV)");
		// REXP value = re.eval("as.vector(data.frame(a<-inputClose(inputV)))");
		double a = value.asDouble();
		System.out.println(a);
	}
}