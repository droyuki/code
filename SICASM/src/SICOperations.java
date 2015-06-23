import java.util.HashMap;
import java.util.Map;

public class SICOperations {
	private static Map<String, Integer> opMap;
	public static final int OPCODE_WORD = -1;
	public static final int OPCODE_BYTE = -2;
	public static final int OPCODE_NONE = -3;
	public static final int OPCODE_RESW = -4;
	public static final int OPCODE_RESB = -5;
	
	private static void setOpcodeMap()
	{
		opMap = new HashMap<String, Integer>();
		//operator
		opMap.put("J",     0x3C);
		opMap.put("LDA",   0x00);
		opMap.put("SUB",   0x1C);
		opMap.put("CLEAR", 0xB4);
		opMap.put("JLT",   0x38);
		opMap.put("STX",   0x10);
		opMap.put("LDCH",  0x50);
		opMap.put("STCH",  0x54);
		opMap.put("TD",    0xE0);
		opMap.put("RSUB",  0x4C);
		opMap.put("STC",   0x14);
		opMap.put("LDX",   0x04);
		opMap.put("STA",   0x0C);
		opMap.put("ADD",   0x18);
		opMap.put("JSUB",  0x48);
		opMap.put("STL",   0x14);
		opMap.put("COMP",  0x28);
		opMap.put("JEQ",   0x30);
		opMap.put("LDL",   0x08);
		opMap.put("RD",    0xD8);
		opMap.put("TIX",   0x2C);
		opMap.put("WD",    0xDC);
		opMap.put("MUL",   0x20);
		opMap.put("HLT",   0x76);
		opMap.put("JGT",   0x34);
		opMap.put("DIV",   0x24);
		
		opMap.put("WORD",  OPCODE_WORD);
		opMap.put("BYTE",  OPCODE_BYTE);
		opMap.put("RESW",  OPCODE_RESW);
		opMap.put("RESB",  OPCODE_RESB);
	}
	
	public static int getOpecode(String op)
	{
		if(opMap == null)
		{
			setOpcodeMap();
		}
		
		try
		{
			return opMap.get(op);
		}
		catch (NullPointerException e) 
		{
			return OPCODE_NONE;
		}
	}
	
	public static boolean isOperator(int opcode)
	{
		if(opcode >= 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static boolean isOperator(String op)
	{
		return isOperator(getOpecode(op));
	}
}
