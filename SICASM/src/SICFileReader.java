import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;


public class SICFileReader 
{
	public static String [][] read(String source)
	{
		LinkedList<String []> sourceFile = new LinkedList<String []>();
		BufferedReader bf = new BufferedReader(readSourceFile(source));
		String line;
		try
		{
			while((line = bf.readLine()) != null)
			{
				line = line.trim().replaceAll("\\s+", " ");//replace space
				if(line.equals("")) continue;//skip space line
				if(line.charAt(0) == '.') continue;//skip comment 
				sourceFile.add(line.split(" "));
			}
		}
		catch (IOException e) 
		{
			System.out.println("read file error");
		}
		return sourceFile.toArray(new String[sourceFile.size()][]);
	}
	
	private static FileReader readSourceFile(String source)
	{
		try 
		{
			return new FileReader(source);
		}
		catch (FileNotFoundException e) 
		{
			System.out.println("file "+ source +" not found");
			return null;
		}
	}
}
