package edu.wpi.leviathans.util.io;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
public class CSVParser {
    String csvFile;
    String delimiter;
	String line = "";

    public CSVParser(String csvFile, String delimiter) {
    	this.csvFile = csvFile;
    	this.delimiter = delimiter;
		this.line = "";
	}

	public CSVParser() {
		this.csvFile = "MapLnodesFloor2.csv";
		this.line = "";
		this.delimiter = ",";
	}

	public void setCsvFile(String csvFile) {
		this.csvFile = csvFile;
	}

	public ArrayList<ArrayList<String>> readCSVFile() {
		ArrayList<ArrayList<String>> csvContents = new ArrayList<>();

        try  {
			BufferedReader br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	csvContents.add((ArrayList<String>) Arrays.asList(line.split(delimiter)));
			}
            csvContents.remove(0);
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }

        return csvContents;
    }
}