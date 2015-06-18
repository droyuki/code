import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import vo.SICInterFileNode;


public class SICASM 
{
	private static final byte MAX_OUTPUT_LENGTH = 60;
	
	private String [][] sourceFile;
	private SICInterFileNode [] interFile;
	private String outputFile;
	private Map<String, Integer> symbolMap;
	private Map<String, LinkedList<Integer>> labelReserve;
	
	private int start = 0;
	private int locctr = 0;
	private int length = 0;
	private String programName;
	
	private BufferedWriter dos;
	private int lineStart;
	private int nextLoc;
	private String lineBuffer = "";
	
	public static void main(String [] args)
	{
		if(args.length != 2)
		{
			System.out.println("usage: java SICASM source.txt output.txt");
			return;
		}
		long time = System.nanoTime();
		new SICASM(args[0], args[1]);
		System.out.println(System.nanoTime() - time);
		//new SICASM("tex.txt", "output.txt");
	}
	
	private SICASM(String source, String output)
	{
		outputFile = output;
		sourceFile = SICFileReader.read(source);
		symbolMap = new HashMap<String, Integer>();
		labelReserve = new HashMap<String, LinkedList<Integer>>();
		interFile = new SICInterFileNode[sourceFile.length - 1];
		
		readFile();
		writeFile();
	}
	
	private void readFile()
	{
		//read first line START
		readFirstLine();
		
		//read other lines
		for(int i = 1; i < sourceFile.length - 1; i++)
		{
			readLine(i);
		}
		
		//read last line END
		readLastLine();
	}
	
	private void readFirstLine()
	{
		String [] line = sourceFile[0];
		if(line[1].equals("START"))
		{
			locctr = start = Integer.parseInt(line[line.length - 1], 16);
		}
		programName = line[0];
	}
	
	private void readLine(int index)
	{
		String op;
		int opcode;
		String [] line = sourceFile[index];
		SICInterFileNode node = interFile[index] = new SICInterFileNode();
		node.locctr = locctr;
		
		///////////////////////
		//-----GET Label-----//
		///////////////////////
		if(line.length == 3)
		//must has label
		{
			String label = line[0];
			if(symbolMap.containsKey(label))
			//label is in symbolMap
			{
				System.out.println("line: " + index + " duplicate label: " + label);
				return;
			}
			else
			{
				putLabel(label);
			}
		}
		
		
		///////////////////////////////
		//-----GET op (operator)-----//
		///////////////////////////////
		if(line.length == 1)//only operator. ex: RSUB
		{
			op = line[0];
		}
		else if(line.length == 2 && line[1].equals("RSUB")) //RSUB with label!?
		{
			op = line[1];
			putLabel(line[0]);//save label
		}
		else//label + operator + operand OR operator + operand
		{
			op = line[line.length - 2];
		}
		
		
		///////////////////////////////
		//---------GET opcode--------//
		///////////////////////////////
		opcode = SICOperations.getOpecode(op);
		
		
		
		///////////////////////////////
		//--------check opcode-------//
		///////////////////////////////
		if(opcode == SICOperations.OPCODE_NONE)
		//is not operator
		{
			System.out.println("line: " + index + " can't find operator " + op);
			return;
		}
		else
		//is operator
		{
			node.opcode = opcode;//save opcode
			switch (opcode) 
			{
				//-------WORD------//
				case SICOperations.OPCODE_WORD:
					locctr += 3;
					node.address = Integer.parseInt(line[2]);
				break;
				
				//-------RESW------//
				case SICOperations.OPCODE_RESW:
					locctr +=  (3 * Integer.parseInt(line[2]));
				break;
					
				//-------RESB------//
				case SICOperations.OPCODE_RESB:
					locctr += Integer.parseInt(line[2]);
				break;
					
				//-------BYTE------//
				case SICOperations.OPCODE_BYTE:
					String [] sta = line[2].split("'");
					if(sta[0].equals("C"))
					//BYTE Char
					{
						int i = sta[1].length();
						int value = 0;
						locctr += i;
						i--;
						for(int bit = 0; i >= 0 ; i--, bit+=8)
						{
							int tmp = sta[1].charAt(i);
							tmp <<= bit;
							value += tmp;
						}
						node.address = value;
						node.byteLength = sta[1].length() * 2;
					}
					else if(sta[0].equals("X"))
					//BYTE HEX
					{
						int value = Integer.parseInt(sta[1], 16);
						locctr += (int)(sta[1].length() * 0.5 + 0.5);
						node.address = value;
						node.byteLength = sta[1].length();
					}
				break;
					
				//-------SIMPLE OPERATOR------//
				default:
					locctr+=3;
					String operand = line[line.length - 1];//get operand String
					
					if(line[line.length - 1].contains(",X"))
					//indirect addressing
					{
						node.address = (1 << 15);
						operand = line[line.length - 1].split(",X")[0];
					}
					
					if(symbolMap.containsKey(operand))
					{
						node.address += symbolMap.get(operand);
					}
					else
					//forward reference
					{
						LinkedList<Integer> link;
						if(labelReserve.containsKey(operand))
						{
							link = labelReserve.get(operand);
							link.add(index);
						}
						else
						{
							link = new LinkedList<Integer>();
							link.add(index);
							labelReserve.put(operand, link);
						}
					}
				break;
			}
		}
	}
	
	private void readLastLine()
	{
		String [] line = sourceFile[sourceFile.length - 1];
		if(line.length == 2 && line[0].equals("END"))
		{
			length = locctr - start;
		}
	}
	
	private void putLabel(String label)
	{
		symbolMap.put(label, locctr);
		
		if(labelReserve.containsKey(label))
		//this label has forward reference
		{
	    	LinkedList<Integer> lr = labelReserve.get(label);
	    	while(lr.size() != 0)
	    	{
	    		interFile[lr.pop()].address += locctr;
	    	}
		}
	}
	
	private void writeFile()
	{
		try
		{
			FileWriter fo = new FileWriter(new File(outputFile));
			dos = new BufferedWriter(fo);
			
			writeFirstLine();
			
			writeOtherLines();
			
			writeLastLine();
			
			dos.close();
		}
		catch (Exception e) 
		{
			System.out.println("Can't write Object File");
		}
	}
	
	private void writeFirstLine() throws Exception
	{
		dos.write("H" + getSpaceString(programName, 6));
		dos.write(intToString(start, 6));
		dos.write(intToString(length, 6));
		dos.newLine();
	}
	
	private void writeOtherLines() throws Exception
	{
		int address, opcode, loc;
		String s;
		
		nextLoc = start;
		
		for(int i = 1 ; i < interFile.length; i++)
		{
			opcode = interFile[i].opcode;
			loc = interFile[i].locctr;
			address = interFile[i].address;
			
			if(SICOperations.isOperator(opcode))
			//is operator
			{
				//get opcode
				s = intToString(opcode, 2);
				
				//get address
				s += intToString(address, 4);
			}
			//is not operator
			else
			{
				if(opcode <= SICOperations.OPCODE_NONE)//has no value
				{
					continue;
				}
				else if (opcode == SICOperations.OPCODE_WORD)
				{
					s = intToString(address, 6);
				}
				else if (opcode == SICOperations.OPCODE_BYTE)
				{
					s = intToString(address, interFile[i].byteLength);
				}
				else
				{
					s = "";
				}
			}
			write(loc, s);
		}
		//output last line
		if(lineBuffer.length() != 0) writeBuffer();
	}
	
	private void write(int loc, String objcode) throws Exception
	{
		int objcodeLength = (int)(objcode.length() * 0.5 + 0.5);
		if(nextLoc == loc)//continue
		{
			if(lineBuffer.isEmpty())
			{
				lineStart = loc;
			}
			else if(lineBuffer.length() >= MAX_OUTPUT_LENGTH)
			{
				writeBuffer();
			}
		}
		else
		{
			writeBuffer();
			lineStart = loc;
		}
		lineBuffer += objcode;
		nextLoc = loc + objcodeLength;
	}
	
	private void writeLastLine() throws Exception
	{
		dos.write("E");
		dos.write(intToString(start, 6));
	}
	
	private void writeBuffer() throws Exception
	{
		String lineOutput;
		if(lineBuffer.length() < MAX_OUTPUT_LENGTH)
		{
			lineOutput = lineBuffer;
			lineBuffer = "";
		}
		else
		{
			lineOutput = lineBuffer.substring(0, MAX_OUTPUT_LENGTH - 1);
			lineBuffer = lineBuffer.substring(MAX_OUTPUT_LENGTH - 1);
		}
		
		
		dos.write("T");
		dos.write(intToString(lineStart, 6));
		dos.write(getBufferLengthString(lineOutput));
		dos.write(lineOutput);
		dos.newLine();
		
		lineStart += (int)(lineOutput.length() * 0.5 + 0.5);
	}
	
	private static String getBufferLengthString(String buf)
	{
		return intToString((int) (buf.length() * 0.5 + 0.5), 2);
	}
	
	private static String intToString(int value, int length)
	{
		String s = Integer.toHexString(value).toUpperCase();
		length = length - s.length();
		for(int i = 0; i < length; i++)
		{
			s = "0" + s;
		}
		return s;
	}
	
	private static String getSpaceString(String s, int length)
	{
		length = length - s.length();
		for(int i = 0; i < length; i++)
		{
			s = s + " ";
		}
		return s;
	}
}