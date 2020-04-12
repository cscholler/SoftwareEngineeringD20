package edu.wpi.leviathans.views;

import com.google.inject.Inject;
import edu.wpi.leviathans.services.db.DatabaseService;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class DatabaseViewController {
	@Inject
	DatabaseService db;



	// Print query results to terminal
	/*private void reportQueryResults(String query) {
		ResultSet resSet;
		Statement stmt;

		try {
			stmt = connection.createStatement();
			resSet = stmt.executeQuery(query);

			StringBuilder sb = new StringBuilder();
			ResultSetMetaData resSetmd = resSet.getMetaData();
			int totalCols = resSetmd.getColumnCount();
			int[] colCounts = new int[totalCols];
			String[] colLabels = new String[totalCols];
			for (int i = 0; i < totalCols; i++) {
				colCounts[i] = resSetmd.getColumnDisplaySize(i + 1);
				colLabels[i] = resSetmd.getColumnLabel(i + 1);
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

			usedResSets.add(resSet);
			usedStmts.add(stmt);
			log.info("Query successful. Displaying results.");
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
	}*/
}
