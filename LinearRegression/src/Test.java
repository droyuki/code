import java.util.Arrays;

import org.rosuda.JRI.RBool;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RVector;
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

		// Test input
		re.eval("inputV <- c(9100:9389)");
		REXP value = re.eval("inputClose(inputV)");
		// REXP value = re.eval("as.vector(data.frame(a<-inputClose(inputV)))");
		double a = value.asDouble();
		System.out.println(a);
	}
}