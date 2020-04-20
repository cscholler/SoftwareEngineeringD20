package edu.wpi.cs3733.d20.teamL.util.io;

import java.io.*;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Slf4j
public class CSVHelper {
	public static final String ROOT_DIR = "/edu/wpi/cs3733/d20/teamL/csv/";

	public ArrayList<ArrayList<String>> readCSVFile(String fileName) {
		return readCSVFile(fileName, false);
	}

	public ArrayList<ArrayList<String>> readCSVFile(String fileName, boolean removeHeaders) {
		ArrayList<ArrayList<String>> data = new ArrayList<>();

		BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(ROOT_DIR + fileName + ".csv")));
		try {
			CSVParser parser = CSVParser.parse(br, CSVFormat.EXCEL);
			for (CSVRecord record : parser) {
				ArrayList<String> row = new ArrayList<>();
				for (int i = 0; i < record.size(); i++) {
					row.add(record.get(i).trim());
				}
				data.add(row);
			}

		} catch (IOException ex) {
			log.error("Encountered IOException.", ex);
		}
		if (removeHeaders) {
			data.remove(0);
		}
		return data;
	}

	public void printTable(ArrayList<ArrayList<String>> data) {
		for (ArrayList<String> row : data) {
			for (int i = 0; i < row.size(); i++) {
				System.out.print(row.get(i) + (i != row.size() - 1 ? ", " : ""));
			}
			System.out.print("\n");
		}
	}

	public void writeToCSV(String filePath, ArrayList<ArrayList<String>> table) {
		try {
			FileWriter csvWriter = new FileWriter(filePath, false);
			for (int i = 0; i < table.size(); i++) {
				for (int j = 0; j < table.get(i).size(); j++) {
					csvWriter.append(table.get(i).get(j)).append(j != table.get(i).size() - 1 ? "," : "");
				}
				csvWriter.append(i != table.size() - 1 ? "\n" : "");
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException ex) {
			log.error("Encountered IOException", ex);
		}
	}
}
