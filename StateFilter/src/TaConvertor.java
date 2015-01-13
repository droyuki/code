import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
public class TaConvertor {
	public static void main(String[] args){
	    String thisLine = null;
	    try{
	    	String count = "count";
	    	String dif = "DIF";
	    	String taNum = "";
	    	int num = 0;
	    	int i = 1000;
	    	int b = 1;
	    	FileReader fr = new FileReader("/Users/WanEnFu/Downloads/StateName.txt");
	    	BufferedReader br = new BufferedReader(fr);
	    	while ((thisLine = br.readLine()) != null) {
	    		if (thisLine.indexOf(count)>0){
//	    			System.out.println(thisLine);
	    			i = 1000 * b;
	    			b = b + 1;
	    		}else{
	    			i = i + 1;
//	    			System.out.println("map.put(" + i + ", " + "\"" + thisLine + "\");");
//	    			System.out.println("map.put(\""+ thisLine +"\", " + i +");");
//	    			System.out.println(thisLine +"," + i);
//	    			if(thisLine.indexOf("LAM")>0 | thisLine.indexOf("LAP")>0 | thisLine.indexOf("DIF")<0){
//	    				num = num+1;
//	    				System.out.println(thisLine);
//	    				taNum = taNum + Integer.toString(i) + "/";
//	    			}
	    			if(thisLine.indexOf("005")>0){
	    				if(thisLine.indexOf("_DIF_")>0){
	    					if(thisLine.split("_").length-1 == 4){
	    						if(Double.parseDouble(thisLine.split("_")[1]) < Double.parseDouble(thisLine.split("_")[4])){
	    		    				num = num+1;
	    		    				taNum = taNum + Integer.toString(i) + "/";
		    						System.out.println(thisLine);
	    						}
	    					}else {
	    						num = num + 1;
	    						taNum = taNum + Integer.toString(i) + "/";
	    						System.out.println(thisLine);
	    					}
	    				}else{
		    				num = num+1;
		    				taNum = taNum + Integer.toString(i) + "/";
    						System.out.println(thisLine);
	    				}
	    			}
	    		}
	    	}   
	    	System.out.println(taNum);
	    	System.out.println(num);
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    
	}
}