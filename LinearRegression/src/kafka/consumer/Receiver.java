package kafka.consumer;

import java.util.ArrayList;

import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;

import main.MsgReceiver;

public class Receiver extends MsgReceiver {
	Rengine re = new Rengine(new String[] { "--vanilla" }, false, null);
	public void execute(String message) {
		int msg = Integer.parseInt(message);
		RVector inputVector = new RVector();
        ArrayList<Integer> al = new ArrayList<Integer>();
        if(al.size() < 90){
        	al.add(msg);
        }
	}

}