package edu.wpi.leviathans.services.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class NodeGeneration {
    String csvFile = "MapLnodesFloor2.csv";
    String line = "";
    String cvsSplitBy = ",";
    ArrayList<String[]> csvContents = new ArrayList<>();

    public ArrayList<String[]> nodeGeneration() {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) csvContents.add(line.split((cvsSplitBy)));
            csvContents.remove(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return csvContents;
    }
}