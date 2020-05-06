package edu.wpi.cs3733.d20.teamL.services.db;

import java.util.ArrayList;

public class SQLEntry {
	private String statement;
	private ArrayList<String> values;

	public SQLEntry(String statement, ArrayList<String> values) {
		this.statement = statement;
		this.values = values;
	}

	public SQLEntry(String statement) {
		this.statement = statement;
		this.values = new ArrayList<>();
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public ArrayList<String> getValues() {
		return values;
	}

	public void setValues(ArrayList<String> values) {
		this.values = values;
	}
}
