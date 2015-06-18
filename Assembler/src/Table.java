import java.util.*;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
/*==========================OpTable==========================*/
class Code{ //opcode structure
	private int format;
	private String code;
	public Code(int f, String c){
		this.format = f;
		this.code = c;
	}
	public String getCode(){
		return code;
	}
	public int getFormat(){
		return format;
	}
	public void print(){
		System.out.print(format);
		System.out.println(code);
	}
}
class OpTable{
	public OpTable(){};
    private String[] mnemonic = {"ADD","ADDF","ADDR","AND","CLEAR","COMP","COMPF","COMPR","DIV","DIVF","DIVR","FIX","FLOAT","HIO","J","JEQ",
    		"JGT","JLT","JSUB","LDA","LDB","LDCH","LDF","LDL","LDS","LDT","LDX","LPS","MUL","MULF","MULR","NORM","OR","RD","RMO","RSUB","SHIFTL",
    		"SHIFTR","SIO","SSK","STA","STB","STCH","STF","STI","STL","STS","STSW","STT","STX","SUB","SUBF","SUBR","SVC","TD","TIO","TIX","TIXR","WD"};
    private int[] format= {3,3,2,3,2,3,3,2,3,3,2,1,1,1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,1,3,3,3,3,2,2,1,3,3,3,3,3,3,3,3,3,3,3,3,3,2,2,3,1,3,2,3};
    private String[] opcode= {"18","58","90","40","B4","28","88","A0","24","64","9C","C4","C0","F4","3C","30","34","38","48","00","68","50","70","08",
    		"6C","74","04","D0","20","60","98","C8","44","D8","AC","4C","A4","A8","F0","EC","0C","78","54","80","D4","14","7C","E8","84","10","1C","5C",
    		"94","B0","E0","F8","2C","B8","DC"};
    public Hashtable<String, Code> createOpTable(){
    	Hashtable<String, Code> opTable = new Hashtable<String, Code>();
    	for(int i = 0; i < mnemonic.length; i++)  {
    		Code c = new Code(format[i], opcode[i]);
    		opTable.put(mnemonic[i], c);
    	}
    	return opTable;
    }
}
/*=================================================================*/
/*==========================Symbol Table==========================*/
class Symbol {
    private int length;
    private int flag;
    private String type;
    private String address;
    public Symbol(){}
    public Symbol(String address, String type, int flag, int length){
        this.address = address;
        this.type = type;
        this.flag = flag;
        this.length = length;
    }
    public void print(){
    	System.out.println("Length:"+length+" flag:"+flag+" type:"+type+" ADD:"+address);
    }
    //Getters and setters
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public int getFlag() {
        return flag;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}
class SymTab{
	private Hashtable<String, Symbol> symTab= new Hashtable<String, Symbol>();
	public Symbol insert(String label, Symbol s){
		return symTab.put(label, s);
	}
	public Symbol get(String label){
		return symTab.get(label);
	}
}

class Line {
	  private String label;
	  private String operator;
	  private String operand;
	  private String comment;
	  private String code;
	  private long lineNum;
	  private long loc;
	  public Line() {
	  }
	  public long getLoc() {
	    return loc;
	  }
	  public void setLoc(long loc) {
	    this.loc = loc;
	  }
	  public String getLabel() {
	    return label;
	  }
	  public void setLabel(String label) {
	    this.label = label;
	  }
	  public String getOperator() {
	    return operator;
	  }
	  public void setOperator(String operator) {
	    this.operator = operator;
	  }
	  public String getOperand() {
	    return operand;
	  }
	  public void setOperand(String operand) {
	    this.operand = operand;
	  }
	  public String getComment() {
	    return comment;
	  }
	  public void setComment(String comment) {
	    this.comment = comment;
	  }
	  public String getCode() {
		  return code;
	  }
	  public void setCode(String code) {
		  this.code = code;
	  }
	  public long getLineNum() {
	    return lineNum;
	  }
	  public void setLineNum(long lineNum) {
	    this.lineNum = lineNum;
	  }

	}
/*=================================================================*/
public class Table { 
    public static void main(String[] abc) throws Exception{
    	boolean flag = false;
    	int X = 32768;
    	String errorLog = "";
    	Scanner in = new Scanner(System.in);
    	//System.out.println("Let's do something!  \n1: Op table\n2: Symol table\n3: Read File");
    	//int s = in.nextInt(); 
    	OpTable t = new OpTable();
    	Hashtable<String, Code> op= t.createOpTable();
    	SymTab test2 = new SymTab();
    	/*
    	switch(s){
    	    case 1:
    	   	    System.out.println("Op Table");
    	        System.out.println("Please input a mnemonic: ");
    	        while(!in.hasNext("#")){
    	    	     Code test = op.get(in.next().toUpperCase());
    	    	     if(test == null) System.out.println("Not found!");
    	    	     else test.print();
    	        }
    	        break;
            case 2:
                System.out.println("SymTab insert");
    	        System.out.println("Input '#' to break");
                while(!in.hasNext("#")){
               	    String lable=in.next();
                    String add=in.next();
                    Symbol s1 = new Symbol();
                    s1.setAddress(add);
                    test2.insert(lable, s1);
        	    }
                Scanner input = new Scanner(System.in);
                System.out.println("Search :");
                while(!input.hasNext("#")){
                	String r = input.next();
                    if(test2.get(r) == null){
                    	System.out.println("Not found!");
                    }else{
                    	Symbol testTab = test2.get(r);
                        testTab.print();
                    }
                }
            default:break;
        }//end switch
    	
    	*/
    	System.out.println("Read File");
        //讀檔
    	FileReader fr = new FileReader("D:/test2.txt");
    	BufferedReader br = new BufferedReader(fr);
    	
    	//pass 1,建Symbol table 和每行的 loc
    	int LOCCTR=0;
    	String name="";
    	int lineNum = 0;
    	ArrayList<Line> codeList = new ArrayList<Line>(); 
    	while(br.ready()){
    		String line = br.readLine(); 
    		StringTokenizer st = new StringTokenizer(line);
    		String lines="";
    		
    		//過濾掉註解、空行的line = lines
    		ArrayList<String> ar = new ArrayList<String>();
    		while (st.hasMoreTokens()) {
    		    String next = st.nextToken();
    		    if(next.equals("END"))
        			break;
    		    char c = next.charAt(0);
    	        if(c == '.') break; //do nothing
    	        else {
    	        	lines += next+"  ";
    	            ar.add(next);
    	        }
    	     }
    		
    		StringTokenizer st2 = new StringTokenizer(lines);
    		int tokens = st2.countTokens();//token數
    		String start ="";
    		Line l = new Line();
    		l.setLineNum(lineNum);
    		if(ar.size() == 0)
    			continue;
    		
    		if(tokens == 1){
    			if(!ar.get(0).equalsIgnoreCase("RSUB")){
    				errorLog += "Syntax error in lind "+lineNum+"\r\n";
    				flag =true;
    				l.setOperator(ar.get(0));
    			}
    		}
    		//有label
    		if(((test2.get(ar.get(0)) == null)&&(op.get(ar.get(0)) == null)) && ar.get(0) != "WORD" && ar.get(0)!= "RESW" && ar.get(0) != "RESB" && ar.get(0)!= "BYTE") {
    			
    			if(tokens == 4){
    				l.setLabel(ar.get(0)); //set label
        			l.setOperator(ar.get(1));
        			l.setOperand(ar.get(2));
    				String operand = ar.get(2)+ar.get(3);
    				operand.replaceAll("\\s+", "");
    				l.setOperand(operand);
    			}else if(tokens == 3){
    				l.setLabel(ar.get(0)); //set label
        			l.setOperator(ar.get(1));
        			l.setOperand(ar.get(2));
    			}
    		} else { //no label
    			if(tokens == 3){
    				l.setOperator(ar.get(0));
    				String operand = ar.get(1)+ar.get(2);
    				operand.replaceAll("\\s+", "");
    				l.setOperand(operand);
    			}else{
    				l.setOperator(ar.get(0));
    				if(ar.size() == 1) {
    					l.setOperand("0000");
    			    }else
    					l.setOperand(ar.get(1));
    				System.out.println(l.getOperator());
    			    
    			}
    		}
    		
    		try{
    		if(l.getOperator().equals("START")){ //設定初始位址
    			start = l.getOperand();
    			name=l.getLabel();
				LOCCTR = Integer.parseInt(start, 16);
				l.setLoc(lineNum);
    			l.setLoc(LOCCTR);
				System.out.println("START LOCCTR= "+LOCCTR);
				System.out.println();
				codeList.add(l);
				lineNum += 5;
				continue;
    		}
    		} catch (Exception e){
    			errorLog += "Syntax error: "+lineNum+"\r\n";
    			flag = true;
    		}
    		/*
    		if(!sss){ //設定START addr
    			for(int i = 0;i < ar.size();i++){
    				if(ar.get(i).equals("START")){  //set LOCCTR
    					start = ar.get(i+1);
    					LOCCTR = Integer.parseInt(start, 16);
    		    		System.out.println("LOCCTR: "+LOCCTR);
    					sss = true;
    					break;
    				}
    			}
    		}
    		*/
    		if(tokens != 0){
    			System.out.println(lines);
    		    System.out.println("Number of tokens: "+tokens);
    		}
    		
    		if(l.getLabel() == null){ //沒有label
    			l.setLoc(LOCCTR);
    			//System.out.println("now LOCCTR= "+LOCCTR);
    			LOCCTR += 3;
    			
    			/*
    			String operator = ar.get(0);
    			String operand = ar.get(1);
    			Code test = op.get(operator);
    			System.out.println(test.getCode());
    	    	fw.write(test.getCode());
    	    	Symbol testTab = test2.get(operand);
    	    	fw.write(testTab.getAddress());
    	    	*/
    	    	//System.out.println("Done!!");
    	    	
    		}else { //有label,先search，if not found，存入symbol tabel
    			String label = l.getLabel();
    			String operator = l.getOperator();
    			String operand = l.getOperand();
    			l.setLoc(LOCCTR);
    			//System.out.println("now LOCCTR= "+LOCCTR);
    			if(test2.get(label) == null){ //找不到label，自行insert
    				Symbol b = new Symbol();
    				b.setAddress(Integer.toHexString(LOCCTR));
    				//System.out.println(b.getAddress().toUpperCase());
    			    test2.insert(label, b);
    			    //System.out.println("Insert success!");
    			} else {
    				System.out.println("This Symbol has already exist !");
    				flag = true;
    			}
    			
    			if(operator.equals("WORD"))
    			    LOCCTR += 3;
    			else if(operator.equals("BYTE")){
    				 String[] tmp = l.getOperand().split("'");
    	             if (tmp[0].equalsIgnoreCase("C")) {
    	                  LOCCTR += tmp[1].length();
    	             } else if (tmp[0].equalsIgnoreCase("X")) {
    	                  LOCCTR += Integer.parseInt(String.valueOf(tmp[1].length()/2));
    	             }
        			
    			} else if (operator.equals("RESW"))
        			LOCCTR += Integer.parseInt(String.valueOf(l.getOperand()))*3;
    		    else if (operator.equals("RESB"))
        			LOCCTR += Integer.parseInt(l.getOperand());
    		    else 
    		    	LOCCTR += 3;
    		    
    			
    			
    			/*
    			    Code test = op.get(operator);
    			    fw.write(test.getCode());
    			    Symbol testTab = test2.get(operand);
    			    if(testTab == null) System.out.println();
    			    else fw.write(testTab.getAddress());
    	    	    */
    	    	
    	    	
    	    	System.out.println("Done!!");
    		}
    		codeList.add(l); //加入到code list中
    		lineNum += 5;
    	}
    	Line end = new Line();
    	end.setOperator("END");
    	end.setOperand(name);
    	end.setLineNum(lineNum);
    	end.setLoc(LOCCTR);
    	codeList.add(end);
    	FileWriter fw = new FileWriter("D:/result.txt");
    	try {
    		
    	for(int x = 0;x<codeList.size();x++){
    		Line xxx = (Line)codeList.get(x);
    		if(op.get(xxx.getOperator()) == null) {
    			String lable1 = xxx.getLabel()==null?"":xxx.getLabel();
    			String operand1 = xxx.getOperand()==null?"":xxx.getOperand();
    			fw.write(xxx.getLineNum()+"\t"+Integer.toHexString((int) xxx.getLoc()).toUpperCase()+"\t"+lable1+
        				"\t"+xxx.getOperator()+"\t"+operand1+"\r\n");
    			System.out.println(xxx.getLineNum()+"\t"+Integer.toHexString((int) xxx.getLoc()).toUpperCase()+
        				"\t"+lable1+"\t"+xxx.getOperator()+"\t"+operand1);
    		}else if(op.get(xxx.getOperator()) != null){
    			String lable1 = xxx.getLabel()==null?"":xxx.getLabel();
    			String operand1 = xxx.getOperand()==null?"":xxx.getOperand();
    		fw.write(xxx.getLineNum()+"\t"+Integer.toHexString((int) xxx.getLoc()).toUpperCase()+"\t"+lable1+
    				"\t"+xxx.getOperator()+"\t"+operand1+ "\t"+
    				op.get(xxx.getOperator()).getCode()+", "+
    				op.get(xxx.getOperator()).getFormat()+"\r\n");
    		System.out.println(xxx.getLineNum()+"\t"+Integer.toHexString((int) xxx.getLoc()).toUpperCase()+
    				"\t"+lable1+"\t"+xxx.getOperator()+"\t"+operand1 + "\t"+
    				op.get(xxx.getOperator()).getCode()+", "+
    				op.get(xxx.getOperator()).getFormat());
    		}
    	}
    	
    }catch(Exception e){
    	
    }
    	fw.write(errorLog);
    	System.out.println(errorLog);
    	fw.close();
    	fr.close();
    	
    	//Pass 2
    	System.out.println("Pass 2 START!!");
    	StringBuffer sb = new StringBuffer();
    	Iterator itr = codeList.iterator();
    	FileWriter fw2 = new FileWriter("D:/code.txt");
    	itr.next();
    	ArrayList<Line> objectCode = new ArrayList<Line>();
    	ArrayList<String> obcBuffer = new ArrayList<String>();
    	ArrayList<String> textLoc = new ArrayList<String>();
    	String length = "";
    	length = Long.toHexString(codeList.get(codeList.size()-1).getLoc() - codeList.get(0).getLoc());
    	sb.append("H"+getFixedField(name,6)+getFixedFieldByZero(Long.toHexString(codeList.get(0).getLoc()),6).toUpperCase()+","+getFixedFieldByZero(length,6).toUpperCase()+"\r\n"); //第一行
    	int bufferIndex=0;
    	while(itr.hasNext()){
    		if(flag == true) break;
    		Line ll = (Line)itr.next();
    		if(!ll.getOperator().equals("END")){
    			if(op.get(ll.getOperator().toUpperCase()) != null){ //if found
    				if(test2.get(ll.getOperand())!= null){ //if there is a symbol is Operand filed
    					if(test2.get(ll.getOperand()) != null){//search symbol table 找的到
    						Symbol tmpSym = test2.get(ll.getOperand());
    						ll.setCode(op.get(ll.getOperator()).getCode()+tmpSym.getAddress());
    					}else {
    						
    						ll.setOperand("0000");
    						ll.setCode(op.get(ll.getOperator()).getCode()+"0000");
    						System.out.println("Undifined Symbol in line " + ll.getLineNum());
    					}
    				}else{
    					if(ll.getOperand().contains(",X")){ //有X
    						String[] ss = ll.getOperand().split(",");
    						if(test2.get(ss[0])==null){
    							System.out.println("Undefined symbol in line "+ (lineNum-5));
    							flag = true;
    							break;
    						}else{
    						String s = test2.get(ss[0]).getAddress();
    						
    						//System.out.println("!!!" + s);
    	                    long l = Integer.parseInt(s, 16);
    	                    ll.setCode(op.get(ll.getOperator()).getCode()+Long.toHexString((l+X)).toUpperCase());
    	                    System.out.println(Long.toHexString((l+X)).toUpperCase());
    						}
    	                    //ll.setCode(ll.getOperator()+Long.toHexString((l+X)).toUpperCase());
    					}else{
    					    ll.setOperand("0000");
    					    ll.setCode(op.get(ll.getOperator()).getCode()+"0000");
    					}
    				}
    				System.out.println(ll.getCode());
    				//buffer += op.get(ll.getOperator()).getCode() + ll.getOperator();
    			}else if(ll.getOperator().equalsIgnoreCase("BYTE") ){ //op =BYTE 計算長度
    			    String[] tmp = ll.getOperand().split("'");
    			    if (tmp[0].equalsIgnoreCase("C")) { //計算長度
    	                  char[] c = tmp[1].toCharArray();
    	                  String s = "";
    	                  for(int i=0;i<c.length;i++){
    	                    s += String.valueOf(Integer.toHexString(c[i]).toUpperCase());
    	                  }
    	                  ll.setCode(s);
    	                } else if (tmp[0].equalsIgnoreCase("X")) { //直接
    	                  ll.setCode(tmp[1]);
    	                }
    			} else if (ll.getOperator().equalsIgnoreCase("WORD")) {
    				int i = Integer.parseInt(ll.getOperand());
                    ll.setCode(getFixedFieldByZero(Integer.toHexString(i), 6));
                } else if (ll.getOperator().equalsIgnoreCase("RESW")) {
                	ll.setCode("      ");
                } else if (ll.getOperator().equalsIgnoreCase("RESB")) {
                	ll.setCode("      ");
                }
    			if(obcBuffer.size() ==  0){ //第一次
    				textLoc.add(Long.toHexString(ll.getLoc()));
    				obcBuffer.add(ll.getCode());
    			} else { //還沒超過大小 
    			    String tmp = obcBuffer.get(bufferIndex); 
    			    if(ll.getCode()!=null){ //抓出來放放看
    			        String tmp2 = tmp+ll.getCode();
    			        if(tmp2.length() > 60){ //加進去會超過大小的話
    				        bufferIndex ++; //往下一格放
    				        textLoc.add(Long.toHexString(ll.getLoc()));
    				        obcBuffer.add(bufferIndex, ll.getCode());
    			        }else{ //不會超過大小，放同一格就好
        			    	obcBuffer.remove(bufferIndex); 
        			    	obcBuffer.add(tmp2);
    	    		    }
    			    }
    			}
    			objectCode.add(ll);
    		}
    	}
    	
    	for(int i = 0;i < obcBuffer.size();i++){
    	    sb.append("T,"+
    	            getFixedFieldByZero(textLoc.get(i).toUpperCase(),6) +","+
    	    		getFixedFieldByZero(Integer.toHexString(obcBuffer.get(i).replaceAll(" ", "").length()/2).toUpperCase(),2)+","+
    				obcBuffer.get(i).toUpperCase().replaceAll(" ", "")+"\r\n");
    	}
    	if(flag == false){
    	sb.append("E"+getFixedFieldByZero(textLoc.get(0).toUpperCase(),6));
    	System.out.println(sb.toString());
    	}
    	fw2.close();
    	
    	
    	
    	
    	//寫入
    	//FileWriter fw = new FileWriter("D:/result.txt");
    	//fw.write(str);
    	//fw.close();
    	
    }
    /**
     * 將不夠位數的部分補空白
     * @param s String
     * @param i int
     * @return String
     */
    public static String getFixedField(String s, int i){
      StringBuffer tmp = new StringBuffer();
      tmp.append(s);
      int diff = i - (s==null?0:s.length());
      if(diff>0){
        for(int j=0;j<diff;j++){
          tmp.append(" ");
        }
      }
      return tmp.toString();
    }

    public static String getFixedFieldByZero(String s, int i){
      StringBuffer tmp = new StringBuffer();
      int diff = i - (s==null?0:s.length());
      if(diff>0){
        for(int j=0;j<diff;j++){
          tmp.append("0");
        }
      }
      tmp.append(s);
      return tmp.toString();

    }
}
