package edu.wpi.leviathans.util.io;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class CSVReader {
    String csvFile;
    String delimiter;
	String line = "";

    public CSVReader(String csvFile, String delimiter) {
    	this.csvFile = csvFile;
    	this.delimiter = delimiter;
		this.line = "";
	}

	public CSVReader() {
		this.csvFile = "MapLnodesFloor2.csv";
		this.line = "";
		this.delimiter = ",";
	}

    public ArrayList<String[]> readCSVFile() {
		ArrayList<String[]> csvContents = new ArrayList<>();

        try  {
			BufferedReader br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) csvContents.add(line.split((delimiter)));
            csvContents.remove(0);
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }

        return csvContents;
    }
}