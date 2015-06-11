import java.util.Arrays;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class Test {

	public static void main(String[] args) {

		Rengine re = new Rengine(new String[] { "--vanilla" }, false, null);

		// the engine creates R is a new thread, so we should wait until it's
		// ready
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			System.exit(0);
		}

		// evaluate R built-in function
		REXP result2 = re.eval("x1 <- runif(1, 5.0, 7.5)");
		System.out.println(result2.asDouble());

		// assign java variable to R
		int[] intArray = { 33, 44, 55 };

		System.out.println(re.assign("x", intArray));
		System.out.println(Arrays.toString(re.eval("x").asIntArray()));

		// load own R script
		re.eval("source('../LinearRegression/resource/TaGenerator.R')");
		re.eval("source('../LinearRegression/resource/runModel.R')");
		// REXP result = re.eval("demo(x)");
		// System.out.println(result.asInt());
		re.eval("x=c(1:100)");
		REXP value = re.eval("inputClose(x)");
		double[] a = value.asDoubleArray();
		for (double d : a)
			System.out.println(d);

	}
}