import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import main.StateTaRuleMap;

public class StateFilter {
	public static void main(String[] args) throws IOException {
		String input = "C:\\Users\\droyuki\\Desktop\\StateName.txt";
		String tmpFile = "C:\\Users\\droyuki\\Desktop\\state2.txt";
		String result = "C:\\Users\\droyuki\\Desktop\\StateResult.txt";
		parse(input, tmpFile);
		addStateNum(tmpFile, result);
	}

	public static void parse(String file, String out) throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		FileWriter fw = new FileWriter(out);
		while (br.ready()) {
			String line = br.readLine();
			System.out.println(line);
			if (line.charAt(0) == '[')
				continue;
			if (line.indexOf("BR") != -1) {
				continue;
			}
			if (line.indexOf("_DIF") == -1) { // 不含DIF
				System.out.println("NO DIF");
				String[] lineArr = line.split("_");
				if (lineArr[1].equalsIgnoreCase("005")) {// eg: WRSI_005
					fw.write(line + "\r\n");
					continue;
				}
			} else {// 有DIF

				if (line.indexOf(".") >= 0) {// 含float，也就是純粹與數值相比的
												// WRSI_005_DIF_75.0
					if (line.split("_DIF_")[0].indexOf(".") >= 0) {
						if (line.split("_DIF_")[1].indexOf("005") != -1) {
							fw.write(line + "\r\n");
							continue;
						}
					} else {
						if (line.split("_DIF_")[0].indexOf("005") != -1) {
							fw.write(line + "\r\n");
							continue;
						}
					}
				} else { // 兩個ta相減或不同ta相減 WRSI_005_DIF_WRSI_010
					String[] lineArr = line.split("_");
					System.out.println(line);
					switch (lineArr.length) {
					case 4:
						continue;
					case 5:
						if (line.split("_DIF_")[0].split("_")[1]
								.equalsIgnoreCase("005")) {
							if (Integer.parseInt(lineArr[1]) <= Integer
									.parseInt(lineArr[4])) {
								continue;
							} else {
								fw.write(line + "\r\n");
								continue;
							}
						}
					}
				}
			}
		}
		fw.close();
		fr.close();
	}

	public static void addStateNum(String file, String out) throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		FileWriter fw = new FileWriter(out);
		StateTaRuleMap rm = new StateTaRuleMap();
		Map<String, Integer> m = rm.getMap();
		while (br.ready()) {
			String line = br.readLine();
			if (line.charAt(0) == '[')
				continue;
			fw.write(line + "," + m.get(line) + "\r\n");
		}
		fw.close();
		fr.close();
	}
}