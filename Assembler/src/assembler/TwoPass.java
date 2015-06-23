package assembler;
/*
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;


//You'll be filling this one out
public class TwoPass
{
	private ALStream alstr;
	private OpTable optab;
	private AL assemblyLine;
	private int lineNumber = 0;
	private BufferedWriter outOverview;
	private BufferedWriter outRecord;
	private AL interMediateAssemblyLine;
	private String locctr = "000000";
	private ArrayList<InterMediateLine> intermediateLines = new ArrayList<InterMediateLine>();
	private boolean baseFlag = false;
	private Iterator<String> litIter;
	private ListIterator<InterMediateLine> iter;
	private String base = "";
	private String objectCodeString = "";
	private int lengthOfTextRec = 0;
	private String startAddress = "000000";
	private String targetAddress = "0";
	private String programLength = "000000";
	private HashMap<String, Symbol> symTab = new HashMap<String, Symbol>();
	private HashMap<String, Literal> litTab = new HashMap<String, Literal>();
	private String operand1;
	private String operand2;
	private String objectCode = "";
	private boolean ltorg = false;
	private String ltorgLOCCTR = "0";


	public TwoPass(OpTable opt, ALStream alstream)
	{ alstr = alstream; optab = opt; }

	public void assemble()
	{
		// 2 pass algorithm to assemble the "code" in
		// successive elements of ALStream object

		// i.e.  Y O U R  C O D E ! ! ! !

		firstPass();
		fillSymTabWithRegisters();
		secondPass();

		try {
			outOverview.close();
			outRecord.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//	PASS ONE
	public void firstPass(){
		assemblyLine = alstr.nextAL();
		if (assemblyLine.getOpmnemonic() == "START"){

			startAddress = assemblyLine.getOperand1();
			while(startAddress.length() < 6) startAddress = "0" + startAddress;

			locctr = intToHex((Integer.parseInt(startAddress)));

			InterMediateLine currentInterMediateLine = new InterMediateLine(locctr, assemblyLine);
			intermediateLines.add(currentInterMediateLine);

			assemblyLine = alstr.nextAL();
		}
		else locctr = "000000";

		while(!alstr.atEnd()){
			InterMediateLine currentInterMediateLine = new InterMediateLine(locctr, assemblyLine);
			intermediateLines.add(currentInterMediateLine);

			if(!assemblyLine.isFullComment()){
				if(assemblyLine.getLabel() != ""){
					if(symTab.containsKey(assemblyLine.getLabel())){
						//						TODO: finne ut korsjen vi kasta skikkeelige exeptions
						//						throw new IllegalDuplicateError;
					}
					else {
						Symbol sym = new Symbol(locctr, "", 0, 0);
						symTab.put(assemblyLine.getLabel(), sym);
					}
				}
				if(assemblyLine.getOpmnemonic() != "" || assemblyLine.getOpmnemonic() != "LTORG"){
					correctLOCCTR(assemblyLine);
				}			
				searchLiterals(assemblyLine);
			}

			assemblyLine = alstr.nextAL();
		}

		litIter = litTab.keySet().iterator();

		while(litIter.hasNext()){
			String litIterString = litIter.next();
			Literal tempLit = litTab.get(litIterString);

			if(tempLit.getAddress().equals("")) tempLit.setAddress(locctr);

			litTab.put(litIterString , tempLit);

			hexMath(locctr, '+', Integer.toHexString(findNumberOfBytesInConstant(tempLit.getValue())));	
		}

		InterMediateLine currentInterMediateLine = new InterMediateLine(locctr, assemblyLine);
		intermediateLines.add(currentInterMediateLine);
		programLength = hexMath(locctr, '-' ,startAddress);
		while(programLength.length() < 6) programLength = "0" + programLength;
	}

	//The Second pass
	public void secondPass(){
		alstr.reset();
		iter = intermediateLines.listIterator();
		while(iter.hasNext()){
			InterMediateLine currentInterMediateLine = iter.next();
			interMediateAssemblyLine = currentInterMediateLine.getAssemblyLine();
			locctr = currentInterMediateLine.getLocctr();
			assemblyLine = alstr.nextAL();
			//IF OPCODE IS NOT END
			if(!interMediateAssemblyLine.getOpmnemonic().equals("LTORG")){
				if(!interMediateAssemblyLine.getOpmnemonic().equals("END")){
					if(interMediateAssemblyLine.getOpmnemonic().equals("EXTREF"))writeReferRec();
					else if(interMediateAssemblyLine.getOpmnemonic().equals("EXTDEF"))writeDefineRec();
					else{
						operand1 = interMediateAssemblyLine.getOperand1();
						operand2 = interMediateAssemblyLine.getOperand2();
						if(interMediateAssemblyLine.getOpmnemonic().equals("START")){
							printHeaderRecord(interMediateAssemblyLine);
							initializeTextRecord();
						}
						//IF COMMENT
						else if(interMediateAssemblyLine.isFullComment()){}
						//IF NOT COMMENT
						else{
							//SEARCHES FOR "BASE" AND SETS IT, IF FOUND.
							searchAndProcessBase(interMediateAssemblyLine);
							//SEARCHES FOR SYMBOLS, LITERALS and '*' IN OPERANDS AND REPLACE
							replaceOperands();
							//IF BYTE OR WORD
							if(interMediateAssemblyLine.getOpmnemonic().equals("BYTE")
									|| (interMediateAssemblyLine.getOpmnemonic().equals("WORD"))){
								objectCode = constantToHex(interMediateAssemblyLine.getOperand1());
							}
							//IF NOT BYTE OR WORD
							else{
								try {
									objectCode = makeObjectCode(interMediateAssemblyLine);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							//WRITE TO TEXTRECORD
							if((fitIntoTextRec(objectCode)) 
									&& (!interMediateAssemblyLine.getOpmnemonic().equals("RESW"))
									&& (!interMediateAssemblyLine.getOpmnemonic().equals("RESB")))
								writeObjectCode(objectCode);
							//IF OBJECTCODE DOESNT FIT INTO CURRENT TEXTRECORD OR OPERATOR IS RESW OR RESB
							else {
								if((!operand1.equals("1"))){
									//	&& ((interMediateAssemblyLine.getOpmnemonic().equals("RESW"))
									//			|| (interMediateAssemblyLine.getOpmnemonic().equals("RESB")))){ 
									fixLengthInTextRecord();
									printToRecord(objectCodeString);
									initializeTextRecord();
								}
								writeObjectCode(objectCode);
							}
						}
					}
				}
				//IF OPCODE = END
				else {
					if(ltorg == false)insertLiterals();
					fixLengthInTextRecord();
					printToRecord(objectCodeString);
					printEndRecord();
				}
				//ALWAYS DO
				interMediateAssemblyLine.setAssembledOpcode(objectCode);
				printToOverviewFile(currentInterMediateLine);
			}
			//IF OPMNEMONIC = LTORG
			insertLiterals();
			ltorg = true;
		}
	}

	public void searchLiterals(AL assemblyLine){
		//		Searching for literans and adding to LITTAB if found
		if(assemblyLine.getOperand1().toCharArray()[0] == '='){
			if(litTab.containsKey(assemblyLine.getOperand1())){
				//						TODO: finne ut korsjen vi kasta skikkeelige exeptions
				//						throw new IllegalDuplicateError;
			}
			else{
				Literal lit = new Literal("", stripToValue(assemblyLine.getOperand1()), findNumberOfBytesInConstant(assemblyLine.getOperand1()));
				litTab.put(assemblyLine.getOperand1(), lit);
			}
		}
		else if(assemblyLine.getOpmnemonic().equals("LTORG")){

			litIter = litTab.keySet().iterator();

			while(litIter.hasNext()){
				String litIterString = litIter.next();
				Literal tempLit = litTab.get(litIterString);

				if(tempLit.getAddress().equals("")) tempLit.setAddress(locctr);

				litTab.put(litIterString , tempLit);

				hexMath(locctr, '+', Integer.toHexString(findNumberOfBytesInConstant(tempLit.getValue())));			
			}
		} 
	}

	public void insertLiterals(){
		litIter = litTab.keySet().iterator();
		while(litIter.hasNext()){
			String litIterString = litIter.next();
			Literal tempLit = litTab.get(litIterString);
			if((Integer.parseInt(tempLit.getAddress(),16))<(Integer.parseInt(locctr,16)) 
					&& (Integer.parseInt(tempLit.getAddress(),16)>(Integer.parseInt(ltorgLOCCTR,16)))){
				
				//Insert hond her
			
			}
		}
	}
	//	Makes object code
	public String makeObjectCode(AL assemblyLine) throws IOException{



		String objectCode = "000000", flagHex, opCodeValue, programCounter, base, disp ="000", x="0", b="0", p="0", e="0";

		//		get value of mnemonic in optable
		OpCode tempOpCode = optab.getOpCode(interMediateAssemblyLine.getOpmnemonic());
		opCodeValue = optab.lookup_AsHex(interMediateAssemblyLine.getOpmnemonic());

		//		get value of targetAddress(TA)
		targetAddress = operand1;

		//		FORMAT 1------------------------------START------------------------------------------

		//		Format 1 only has 1 byte, 2 hexcharacters, the opcodeValue
		if (tempOpCode.getFormat() == 1){
			while (opCodeValue.length() < 2) opCodeValue = "0" + opCodeValue;
			objectCode = opCodeValue;
		}
		//		FORMAT 1------------------------------END--------------------------------------------

		//		FORMAT 2------------------------------START------------------------------------------

		//		Format 2 has 2 bytes, the opcodevalue and the value of r1 and r1
		if (tempOpCode.getFormat() == 2){
			while (opCodeValue.length() < 2) opCodeValue = "0" + opCodeValue;
			if(operand1.equals("")) operand1 = "0";
			if(operand2.equals("")) operand2 = "0";
			objectCode = opCodeValue + operand1 + operand2;
		}
		//		FORMAT 2------------------------------END--------------------------------------------

		//		FORMAT 3 & 4 --------------------START-----------------------------------------------

		//		FORMAT 3 and 4 has 3 different main addressing modes. Simple, which contains the combination
		//		of the four different options indexed, base, pc or extended(or a combination), indirect which
		//		the same 4 cominations, but has the indirect flag raised, and the immediate not, and the 
		//		immediate, which also has the 4 flags, but the immediateflag is raised, and the indirect not.
		//		These 3 different modes decides what number will be added to the opcodevalue in the objectCode.
		//		The combos of the 4 flags x, b, p and e decides the 3 character of the opcode.

		// 		SIMPLE ADDRESSING------------START---------------------------------------------------
		if(tempOpCode.getFormat() == 3 || tempOpCode.getFormat() == 4){
			if(!assemblyLine.isIndirect() && !assemblyLine.isImmediate()){

				if(!assemblyLine.isIndexed() && isPCPossible() && !assemblyLine.isExtended()){
					p="1";

					//				Setting the ProgramCounter which is locctr at next line
					InterMediateLine currentInterMediateLine = iter.next();
					interMediateAssemblyLine = currentInterMediateLine.getAssemblyLine();
					programCounter = currentInterMediateLine.getLocctr();				

					currentInterMediateLine = iter.previous();	
					if(targetAddress.equals(""))targetAddress = "0";
					//	setting disp for PC-relative(=TA-PC)					
					disp = hexMath(targetAddress, '-', programCounter);
					if(disp.length() > 3) disp = disp.substring(disp.length() - 3);
					while (disp.length() < 3) disp = "0" + disp;
				}				
				else if(!assemblyLine.isIndexed() && !isBasePossible() && !isPCPossible() && !assemblyLine.isExtended()){
					disp = targetAddress;
					while(disp.length() < 3) disp = "0" + disp;
				}
				else if(!assemblyLine.isIndexed() && !isBasePossible() && !isPCPossible() && assemblyLine.isExtended()){
					e="1";
					disp = targetAddress;
					while(disp.length() < 5) disp = "0" + disp;
				}
				else if(!assemblyLine.isIndexed() && isBasePossible() && !isPCPossible() && !assemblyLine.isExtended()){
					b="1";
					//	get basevalue
					base = this.base;

					//	setting disp for base-relative(=TA-BASE)
					disp = hexMath(targetAddress, '-', base);
					while (disp.length() < 3) disp = "0" + disp;
				}
				if(assemblyLine.isIndexed() && !isBasePossible() && !isPCPossible() && !assemblyLine.isExtended()){
					x="1";
					disp = targetAddress;
					while (disp.length() < 3) disp = "0" + disp;
				}
				else if(assemblyLine.isIndexed() && !isBasePossible() && !isPCPossible() && assemblyLine.isExtended()){
					x="1"; e="1";
					disp = targetAddress;
					while(disp.length() < 5) disp = "0" + disp;
				}
				else if(assemblyLine.isIndexed() && isPCPossible() && !assemblyLine.isExtended()){
					x="1"; p="1";

					//				Setting the ProgramCounter with locctr and adding bytes of operation
					InterMediateLine currentInterMediateLine = iter.next();
					interMediateAssemblyLine = currentInterMediateLine.getAssemblyLine();
					programCounter = currentInterMediateLine.getLocctr();				

					currentInterMediateLine = iter.previous();	

					//	setting disp for PC-relative(=TA-PC)
					disp = hexMath(targetAddress, '-', programCounter);
					if(disp.length() > 3) disp = disp.substring(disp.length() - 3);
					while (disp.length() < 3) disp = "0" + disp;
				}
				else if(assemblyLine.isIndexed() && isBasePossible() && !isPCPossible() && !assemblyLine.isExtended()){
					x="1"; b="1";
					//	get basevalue
					base = this.base;

					//	setting disp for base-relative(=TA-BASE)
					disp = hexMath(targetAddress, '-', base);
					while (disp.length() < 3) disp = "0" + disp;
				}

				opCodeValue = hexMath(opCodeValue, '+', "3");
				while (opCodeValue.length() < 2) opCodeValue = "0" + opCodeValue;

				flagHex = x + b + p + e;
				objectCode = opCodeValue + Integer.toHexString(Integer.parseInt(flagHex, 2)) + disp;
			}
			if(assemblyLine.isIndexed() && assemblyLine.isImmediate()){
				if(assemblyLine.isIndexed()){
					x="1";
					disp = targetAddress;
				}

				if(!assemblyLine.isIndexed())
					disp = targetAddress;

				flagHex = x + b + p + e;
				objectCode = opCodeValue + Integer.toHexString(Integer.parseInt(flagHex, 2)) + disp;			
			}
			//		SIMPLE ADDRESSING----------------END---------------------------------------

			//		INDIRECT ADDRESSING--------------START-------------------------------------

			if(assemblyLine.isIndirect() && !assemblyLine.isImmediate()){
				if(!assemblyLine.isIndexed() && isPCPossible() && !assemblyLine.isExtended()){
					p="1";

					//				Setting the ProgramCounter which is locctr at next line
					InterMediateLine currentInterMediateLine = iter.next();
					interMediateAssemblyLine = currentInterMediateLine.getAssemblyLine();
					programCounter = currentInterMediateLine.getLocctr();				

					currentInterMediateLine = iter.previous();	
					if(targetAddress.equals(""))targetAddress = "0";
					//	setting disp for PC-relative(=TA-PC)					
					disp = hexMath(targetAddress, '-', programCounter);
					if(disp.length() > 3) disp = disp.substring(disp.length() - 3);
					while (disp.length() < 3) disp = "0" + disp;
				}				
				else if(!assemblyLine.isIndexed() && !isBasePossible() && !isPCPossible() && !assemblyLine.isExtended()){
					disp = targetAddress;
					while(disp.length() < 3) disp = "0" + disp;
				}
				else if(!assemblyLine.isIndexed() && !isBasePossible() && !isPCPossible() && assemblyLine.isExtended()){
					e="1";
					disp = targetAddress;
					while(disp.length() < 5) disp = "0" + disp;
				}
				else if(!assemblyLine.isIndexed() && isBasePossible() && !isPCPossible() && !assemblyLine.isExtended()){
					b="1";
					//	get basevalue
					base = this.base;

					//	setting disp for base-relative(=TA-BASE)
					disp = hexMath(targetAddress, '-', base);
					while (disp.length() < 3) disp = "0" + disp;
				}
				opCodeValue = hexMath(opCodeValue, '+', "2");
				while (opCodeValue.length() < 2) opCodeValue = "0" + opCodeValue;

				flagHex = x + b + p + e;
				objectCode = opCodeValue + Integer.toHexString(Integer.parseInt(flagHex, 2)) + disp;
			}
			//		INDIRECT ADDRESSING----------------------END----------------------------------

			//		IMMEDIATE ADDRESING---------------------START---------------------------------

			if(!assemblyLine.isIndirect() && assemblyLine.isImmediate()){

				if(!assemblyLine.isIndexed() && isPCPossible() && !assemblyLine.isExtended()){


					//				Setting the ProgramCounter with locctr and adding bytes of operation
					InterMediateLine currentInterMediateLine = iter.next();
					interMediateAssemblyLine = currentInterMediateLine.getAssemblyLine();
					programCounter = currentInterMediateLine.getLocctr();				

					currentInterMediateLine = iter.previous();	

					if(Integer.parseInt(targetAddress, 16) > Integer.parseInt(programCounter, 16)){

						//	setting disp for PC-relative(=TA-PC)
						p="1";
						disp = hexMath(targetAddress, '-', programCounter);
						if(disp.length() > 3) disp = disp.substring(disp.length() - 3);
					}
					else disp = targetAddress;
					while (disp.length() < 3) disp = "0" + disp;
				}

				else if(!assemblyLine.isIndexed() && !isBasePossible() && !isPCPossible() && !assemblyLine.isExtended()){
					disp = targetAddress;
					while(disp.length() < 3) disp = "0" + disp;
				}				
				else if(!assemblyLine.isIndexed() && !isBasePossible() && !isPCPossible() && assemblyLine.isExtended()){
					e="1";
					disp = intToHex(Integer.parseInt(targetAddress));
					while(disp.length() < 5) disp = "0" + disp;
				}

				else if(!assemblyLine.isIndexed() && isBasePossible() && !isPCPossible() && !assemblyLine.isExtended()){
					b="1";
					//	get basevalue
					base = this.base;

					//	setting disp for base-relative(=TA-BASE)
					disp = hexMath(targetAddress, '-', base);
					while (disp.length() < 3) disp = "0" + disp;
				}
				opCodeValue = hexMath(opCodeValue, '+', "1");
				while (opCodeValue.length() < 2) opCodeValue = "0" + opCodeValue;

				flagHex = x + b + p + e;
				objectCode = opCodeValue + Integer.toHexString(Integer.parseInt(flagHex, 2)) + disp;
			}
			//		IMMEDIATE ADDRESSING-------------------END-------------------------------------------
		}
		//		FORMAT 3 & 4 --------------------END-------------------------------------------------
		if (objectCode.equals("000000"))return "";
		else return objectCode;
	}

	//Replaces operands according to SYMTAB,'*' and LITTAB.
	public void replaceOperands(){
		//OPERAND1
		if(operand1.contains("=")){
			operand1 = findLiteralAddress(operand1);
			ltorg = false;
		}
		else if (operand1.equals("*"))operand1 = locctr;
		else if (isSymbol(operand1))operand1 = findSymbolAddress(operand1);
		//OPERAND2
		if(operand2.contains("=")){
			operand2 = findLiteralAddress(operand2);
			ltorg = false;
		}
		else if(isSymbol(operand2))operand2 = findSymbolAddress(operand2);
		else if(operand2.equals("*"))operand2 = locctr;
	}

	public void writeReferRec(){
		printToRecord("\nR");
		int length = 0;
		String operand = interMediateAssemblyLine.getOperand1();
		String symbol = "";
		while(true){
			int index = operand.indexOf(',');
			if(index != -1)symbol = operand.substring(0, index);
			else symbol = operand.substring(0);
			while(symbol.length()<6) symbol += " ";
			printToRecord(symbol);
			length++;
			if(length==12) {
				printToRecord("\nR");
				length = 0;
			}			
			operand = operand.substring(index+1);
			if(index==-1) break;
		}
	}

	public void writeDefineRec(){

		printToRecord("\nD");
		int length = 0;
		String operand = interMediateAssemblyLine.getOperand1();
		String symbol = "";
		while(true){
			int index = operand.indexOf(',');
			if(index != -1)symbol = operand.substring(0, index);
			else symbol = operand.substring(0);
			String address = symTab.get(symbol).getAddress();
			while(address.length()<6) address = "0" + address;
			while(symbol.length()<6) symbol += " ";
			printToRecord(symbol + address);
			length++;

			if(length==6){ 
				printToRecord("\nD");
				length = 0;
			}

			operand = operand.substring(index+1);
			if(index==-1) break;
		}
	}

	public String hexMath(String hex1, char operator, String hex2){
		if (operator=='+'){
			int i1= Integer.parseInt(hex1,16);
			int i2= Integer.parseInt(hex2,16);
			hex1 = Integer.toHexString(i1+i2);
		}
		if(operator=='-'){
			int i1= Integer.parseInt(hex1,16);
			int i2= Integer.parseInt(hex2,16);
			hex1 = Integer.toHexString(i1-i2);
		}
		//		else throw IllegalOperatorExeption;
		return hex1;	
	}

	//Searches for BASE and NOBASE and processes it.
	public void searchAndProcessBase(AL assemblyLine){
		//IF BASE
		if(assemblyLine.getOpmnemonic().equals("BASE")){
			baseFlag = true;
			if(isSymbol(operand1))
				base = findSymbolAddress(operand1);
			else base = operand1;
		}
		//IF NOT BASE
		else if(assemblyLine.getOpmnemonic().equals("NOBASE")){
			baseFlag = false;
			base = "";
		}
	}

	//Corrects the length in the text record.
	public void fixLengthInTextRecord(){
		char[] objectCodeArray = objectCodeString.toCharArray();

		String hex = intToHex((lengthOfTextRec - 9)/2);
		char[] tempCharArray = hex.toCharArray();
		objectCodeString = "";
		if(hex.length()==1){
			objectCodeArray[7]='0';
			objectCodeArray[8]=tempCharArray[0];
		}
		else{
			objectCodeArray[7]=(hex.toCharArray())[0];
			objectCodeArray[8]=(hex.toCharArray())[1];	
		}
		objectCodeString = new String(objectCodeArray);
		//		for(int i = 0; i<objectCodeArray.length; i++){
		//			objectCodeString += objectCodeArray[i]; 
		//		}
	}

	//Writes the given objectCode to record and increases the lengthOfTextRec.
	public void writeObjectCode(String objectCode){
		objectCodeString += objectCode;
		lengthOfTextRec += objectCode.length();
	}

	//Will objecCode fit into current TextRecord? True if Yes, False otherwise.
	public boolean fitIntoTextRec(String objectCode){
		if((objectCode.length()+lengthOfTextRec)<70)
			return true;
		else
			return false;
	}

	//Initializes a Text Record and writes it to record.
	public void initializeTextRecord(){
		lengthOfTextRec = 0;
		locctr = correctFormat(locctr);
		objectCodeString = "\nT" +  locctr + "00";
		lengthOfTextRec += 9;
	}
	//Corrects the format of LOCCTR to 6 alphanumerical.
	public String correctFormat(String string){
		while(string.length() < 6)string = "0" + string;
		return string;
	}

	//Creates the end record and prints it to file.
	public void printEndRecord(){
		printToRecord("\nE" + startAddress);
	}

	//Returns true if operand is a Symbol, false otherwise.
	public boolean isSymbol(String operand){
		//Pattern star = Pattern.compile("\h2A");
		if(!operand.equals("")){
			if(((operand.matches("[a-zA-Z]*"))&&
					(!operand.matches(".'")))){// || (operand.matches("*"))){
				return true;
			}
			else return false;
		}
		return false;
	}

	public String findSymbolAddress(String operand){
		Symbol aSymbol = symTab.get(operand);
		if (aSymbol == null){return "";}//TODO: Throw undefined Symbol exception. Set error flag?
		else return aSymbol.getAddress();	
	}

	public String findLiteralAddress(String operand){
		Literal aLiteral = litTab.get(operand);
		if(aLiteral == null){return "";}//TODO: Throw undefined Literal exception.?
		else return aLiteral.getAddress();
	}

	//Overfører bokstavene i formen X'ABC' eller C'ABC' til hex
	//	public String constantToHex(String constant){
	//		int decContent = 0;
	//		char[] byteContent = constant.toCharArray();
	//		String hex = "";
	//		for(int i = 2; i<constant.length()-1; i++){
	//			decContent = (int)(byteContent[i]);
	//			hex += intToHex(decContent);
	//		}
	//		return hex;
	//	}

	public String constantToHex(String constant){
		int decContent = 0;
		String content = "";
		char[] byteContent = constant.toCharArray();
		String hex = "";
		for(int i = 2; i<constant.length()-1; i++){
			content += byteContent[i];
			decContent = (int)(byteContent[i]);
			//if(byteContent[0]=='C') hex += intToHex(decContent);
			hex += intToHex(decContent);
		}
		if(byteContent[0]=='C') return hex;
		else return content;
	}

	public String stripToValue(String string){
		return string.substring(string.indexOf("'")+1).substring(0, string.indexOf("'"));	
	}

	//Creates the header record and prints it to file.
	public void printHeaderRecord(AL assemblyLine){
		String programName = assemblyLine.getLabel();
		boolean shortened = false;
		if (programName.equals(""))programName = "PROG  ";
		while(programName.length() > 6){
			shortened = true;
			programName = programName.substring(0, programName.length()-1);
		}
		while(programName.length() < 7) programName += " ";
		if(shortened){
			//TODO: kanskje annen behandling av dette
			System.out.println("Program name too long, has been cut to: "
					+ programName);
		}
		printToRecord("H" + programName + startAddress + programLength);
	}

	//Prints a line to the Overview text file.
	public void printToOverviewFile(InterMediateLine iAssemblyLine){
		try {
			assemblyLine = iAssemblyLine.getAssemblyLine();
			//OPEN FILE
			if(outOverview == null) 
				outOverview = new BufferedWriter(new FileWriter("Overview", true));
			//LINE NUMBER
			lineNumber++;
			outOverview.write(lineNumber + "\t");

			//IF COMMENT
			if(assemblyLine.isFullComment())
				//TODO: Skaffe CommentText. Den er Private i AL. Mail til lewis?
				outOverview.write("CommentText");
			//IF NOT COMMENT
			else{
				//LOCCTR
				outOverview.write((correctFormat(iAssemblyLine.getLocctr())) + "\t");
				//LABEL
				outOverview.write(assemblyLine.getLabel() + "\t\t");
				if(assemblyLine.getLabel().length() < 6)
					outOverview.write("\t");
				//IF EXTENDED
				if(assemblyLine.isExtended())outOverview.write("+");
				//IF LITERAL
				if(assemblyLine.isLiteral()){
					outOverview.write("=");
					outOverview.write(assemblyLine.getOperand1());
					outOverview.write("\t\t");
				}
				//IF NOT LITERAL
				else {
					//OPMNEMONIC
					outOverview.write(assemblyLine.getOpmnemonic() + "\t");
					//IF ADDRESSING
					if(assemblyLine.isIndirect())outOverview.write("@");
					else if(assemblyLine.isImmediate())outOverview.write("#");
					//IF DIRECTIVE
					if(assemblyLine.isDirective()){
						//DIRECTIVE OPERANDS
						if (operand2.equals(""))
							outOverview.write(assemblyLine.getOperand1());
						else outOverview.write(assemblyLine.getOperand2());
					}
					//IF NOT DIRECTIVE
					else {
						//OPERANDS
						switch(assemblyLine.getOperandType()){
						case 0:
							break;
						case 1:
							outOverview.write(assemblyLine.getOperand1());
							break;
						case 2:
							outOverview.write(assemblyLine.getOperand1() + "," +
									assemblyLine.getOperand2());
							break;
						}
					}
					//INDEX
					if(assemblyLine.isIndexed())outOverview.write(",X" + "\t");
					else {
						if(assemblyLine.getOperand1().length() < 6)
							outOverview.write("\t");
						outOverview.write("\t");
					}
				}
				//OBJECTCODE
				outOverview.write(objectCode);
			}
			outOverview.write("\n");

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error printing to Overview text file.");
		}

	}


	//Sets the LOCCTR to its correct position.
	public void correctLOCCTR(AL assemblyLine){
		String opmnemonic = assemblyLine.getOpmnemonic();
		if(!(opmnemonic.equals("START") || (opmnemonic.equals("END")))){
			if(!assemblyLine.isFullComment()){
				if(opmnemonic.equals("WORD"))locctr = hexMath(locctr, '+', "3");
				else if(opmnemonic.equals("RESW")){
					int change = (3 * Integer.parseInt(assemblyLine.getOperand1()));
					locctr = hexMath(locctr, '+', intToHex(change));
				}
				else if(opmnemonic.equals("RESB")){	
					int change = (Integer.parseInt(assemblyLine.getOperand1()));
					locctr = hexMath(locctr, '+', intToHex(change));
				}
				else if(opmnemonic.equals("BYTE")){
					int change = findNumberOfBytesInConstant(assemblyLine.getOperand1());
					locctr = hexMath(locctr, '+', intToHex(change));
				}
				else if(assemblyLine.isExtended())
					locctr = hexMath(locctr, '+', "4");
				else {
					OpCode tempOpCode = optab.getOpCode(opmnemonic);
					locctr = hexMath(locctr, '+', intToHex(tempOpCode.getFormat()));
				}
			}
		}
	}

	//Finds number of bytes in given constant and returns it.
	public int findNumberOfBytesInConstant(String constant){
		int LengthOfByte;
		char[] byteContent = constant.toCharArray();
		if(byteContent[0] == '='){
			constant = constant.substring(1);
			byteContent = constant.toCharArray();
		}
		if(byteContent[0]== 'X'){
			LengthOfByte = ((constant.length()-3)/2);
		}
		else LengthOfByte = (constant.length()-3);
		return LengthOfByte;
	}
	public void printToRecord(String objectCode){
		//		Takes an objectcode string and prints it as a new line in the RecordFile
		try {
			if(outRecord == null) 
				outRecord = new BufferedWriter(new FileWriter("Record", true));
			outRecord.write(objectCode);
		} catch (IOException e) {
		}
	}
	public Boolean isPCPossible(){
		//		When program-counter relative mode is used, disp
		//		is a 12-bits signed integer
		//		2’s complement

		//		TA=(PC)+ disp 
		//		Program-counter relative b=0,p=1
		targetAddress = operand1;

		InterMediateLine currentInterMediateLine = iter.next();
		interMediateAssemblyLine = currentInterMediateLine.getAssemblyLine();
		String programCounter = currentInterMediateLine.getLocctr();				

		currentInterMediateLine = iter.previous();	

		String targetAddress = this.targetAddress;
		String disp;
		int check;

		if(targetAddress.equals(""))targetAddress = "0";

		if(Integer.parseInt(targetAddress, 16) < Integer.parseInt(programCounter, 16)){
			disp = hexMath(programCounter, '-', targetAddress);
			check = Integer.parseInt(disp, 16);
			check = -check;
		}
		else {
			disp = hexMath(targetAddress, '-', programCounter);
			check = Integer.parseInt(disp, 16);
		}

		if ((-2048 <= check) && (check <= 2047))
			return true;
		else return false;
	}
	public Boolean isBasePossible(){
		//		When base relative mode is used, disp is a 12-bits
		//		unsigned integer

		//		TA=(B)+disp
		//		Base relative b=1,p=0 
		if (baseFlag){
			targetAddress = operand1;
			if(targetAddress.equals(""))targetAddress = "0";
			String base = this.base;

			//	setting disp for base-relative(=TA-BASE)
			String disp = hexMath(targetAddress, '-', base);
			int check;
			try{
				check = Integer.parseInt(disp, 16);}
			catch (NumberFormatException e) {return false;}
			if((0 <= check) && (check <=4095))
				return true;
			else return false;
		}
		return false;
	}

	//Takes a decimal int from user and converts it to hex and returns string
	public String intToHex(int inputFromUser){


		int i = inputFromUser;
		String s = Integer.toHexString(i);
		return s;
	}

	//Fills the SymTab with the register addresses.
	public void fillSymTabWithRegisters(){
		String[] regName = {"A", "X", "L", "B", "S", "T", "F", "PC", "SW"};
		String[] value = {"0", "1", "2", "3", "4", "5", "6", "8", "9"}; 
		for(int i = 0; i<regName.length; i++){
			Symbol sym = new Symbol(value[i], "", 0, 0);
			symTab.put(regName[i], sym);
		}
	}
*/
