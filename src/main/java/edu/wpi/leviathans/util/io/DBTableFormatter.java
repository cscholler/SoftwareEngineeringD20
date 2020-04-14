package edu.wpi.leviathans.util.io;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.google.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.leviathans.services.db.DatabaseService;

@Slf4j
public class DBTableFormatter {
	@Inject
	DatabaseService db;

	private void reportQueryResults(ResultSet resSet) {
		try {
			StringBuilder sb = new StringBuilder();
			ResultSetMetaData resSetMD = resSet.getMetaData();
			int totalCols = resSetMD.getColumnCount();
			int[] colCounts = new int[totalCols];
			String[] colLabels = new String[totalCols];
			for (int i = 0; i < totalCols; i++) {
				colCounts[i] = resSetMD.getColumnDisplaySize(i + 1);
				colLabels[i] = resSetMD.getColumnLabel(i + 1);
				if (colLabels[i].length() > colCounts[i]) {
					colLabels[i] = colLabels[i].substring(0, colCounts[i]);
				}
				sb.append(String.format("| %" + colCounts[i] + "s ", colLabels[i]));
			}
			sb.append("|\n");
			String horizontalLine = getHorizontalLine(colCounts);
			while (resSet.next()) {
				sb.append(horizontalLine);
				for (int i = 0; i < totalCols; i++) {
					sb.append(String.format("| %" + colCounts[i] + "s ", resSet.getString(i + 1)));
				}
				sb.append("|\n");
			}
			db.collectUsedResultSet(resSet);
			System.out.println("\n" + getHorizontalLine(colCounts) + sb.toString());
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
	}

	private String getHorizontalLine(int[] colCounts) {
		StringBuilder sb = new StringBuilder();
		for (int colCount : colCounts) {
			sb.append("+");
			sb.append("-".repeat(Math.max(0, colCount + 2)));
		}
		sb.append("+\n");
		return sb.toString();
	}
}
