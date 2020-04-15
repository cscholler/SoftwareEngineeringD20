package edu.wpi.leviathans.util.io;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
public class CSVParser {
	public static final String mapDir = "edu/wpi/leviathans/csvFiles/";
    File csvFile;
    String delimiter;
	String line = "";

    public CSVParser(String csvFileName, String delimiter) {
		this.csvFile = new File(getClass().getClassLoader().getResource(mapDir + csvFileName).getFile());
    	this.delimiter = delimiter;
		this.line = "";
	}

	public CSVParser() {
    	this("MapLnodesFloor2.csv", ",");
	}

	public ArrayList<ArrayList<String>> readCSVFile() {
		ArrayList<ArrayList<String>> csvContents = new ArrayList<>();
		System.out.println(csvFile.getPath());
        try  {
			BufferedReader br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	ArrayList<String> newContent = new ArrayList<>();
				newContent.addAll(Arrays.asList(line.split(delimiter)));
            	csvContents.add(newContent);
			}
            csvContents.remove(0);
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }

        return csvContents;
    }
}