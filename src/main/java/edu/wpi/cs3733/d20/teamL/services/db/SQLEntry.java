package edu.wpi.cs3733.d20.teamL.services.db;

import java.sql.PreparedStatement;
import java.util.ArrayList;

public class SQLEntry {
	private String statement;

	private PreparedStatement prepStatement;
	private ArrayList<String> values;

	public SQLEntry(String statement, ArrayList<String> values) {
		this.statement = statement;
		this.values = values;
	}

	public SQLEntry(PreparedStatement prepStatement, ArrayList<String> values) {
		this.prepStatement = prepStatement;
		this.values = values;
	}

	public SQLEntry(PreparedStatement prepStatement) {
		this.prepStatement = prepStatement;
		this.values = new ArrayList<>();
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

	public PreparedStatement getPrepStatement() {
		return prepStatement;
	}

	public void setPrepStatement(PreparedStatement prepStatement) {
		this.prepStatement = prepStatement;
	}
}
