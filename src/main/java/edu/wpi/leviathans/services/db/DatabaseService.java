package edu.wpi.leviathans.services.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.leviathans.services.Service;

@Slf4j
public class DatabaseService extends Service {
	private Connection connection;
	private Properties props;
	private ArrayList<ResultSet> usedResSets = new ArrayList<>();
	private ArrayList<Statement> usedStmts = new ArrayList<>();

	public DatabaseService(Properties props) {
		super();
		this.props = props;
	}

	public DatabaseService() {
		super();
		this.props = null;
	}

	@Override
	public void startService() {
		if (connection == null) {
			connect(props);
		}
	}

	@Override
	public void stopService() {
		disconnect();
	}

	private void connect(Properties props) {
		try {
			Class.forName(DBConstants.DB_DRIVER);
		} catch (ClassNotFoundException ex) {
			log.error("ClassNotFoundException", ex);
			return;
		}

		try {
			connection = DriverManager.getConnection(DBConstants.DB_URL, props);

			log.info("Connection established.");
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
	}

	private void disconnect() {
		try {
			for (ResultSet rs : usedResSets) {
				rs.close();
			}
			for (Statement stmt : usedStmts) {
				stmt.close();
			}
			if (connection != null) {
				connection.commit();
				connection.close();
			}

			log.info("Connection closed.");
		} catch (SQLException ex) {
			log.error("SQL Exception", ex);
		}
	}

	public void handleUserRequest(int programMode, String museumName, String newPhoneNumber) {
		switch (programMode) {
			case 1: {
				reportQueryResults(DBConstants.selectAllMuseums);
			}
			break;
			case 2: {
				reportQueryResults(DBConstants.selectAllPaintings);
			}
			break;
			case 3: {
				setPhoneNumber(museumName, newPhoneNumber);
			}
			break;
			case 4:
			default: {
				stopService();
				System.exit(0);
			}
		}
	}

	public void buildTestDB() {
		String query;
		Statement stmt;
		ResultSet rs;

		try {
			stmt = connection.createStatement();

			rs = connection.getMetaData().getTables(null, "APP", "MUSEUMS", null);
			if (rs.next()) {
				query = DBConstants.dropMuseumsTable;
				stmt.execute(query);
				log.info("Museums table already exists. Dropping table.");
			}
			query = DBConstants.createMuseumsTable;
			stmt.execute(query);
			log.info("Added Museums table to test database.");

			rs = connection.getMetaData().getTables(null, "APP", "PAINTINGS", null);
			if (rs.next()) {
				query = DBConstants.dropPaintingsTable;
				stmt.execute(query);
				log.info("Paintings table already exists. Dropping table.");
			}
			query = DBConstants.createPaintingsTable;
			stmt.execute(query);
			log.info("Added Paintings table to test database.");

			usedStmts.add(stmt);
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}

		addMuseums();
		addPaintings();

		log.info("Test database built.");
	}

	public void addMuseums() {
		String query = DBConstants.addMuseum;
		PreparedStatement pStmt;

		try {
			// Museum 1
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Philadelphia Art Museum");
			pStmt.setString(2, "Philadelphia, PA");
			pStmt.setString(3, "215-763-8100");
			pStmt.setString(4, "1877");
			pStmt.execute();

			// Museum 2
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Lourvre Museum");
			pStmt.setString(2, "Paris, France");
			pStmt.setString(3, "33 (0)1 40 20 53 17");
			pStmt.setString(4, "1793");
			pStmt.execute();

			// Museum 3
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Museum of Modern Art");
			pStmt.setString(2, "Midtown Manhattan, New York City");
			pStmt.setString(3, "212-708-9400");
			pStmt.setString(4, "1929");
			pStmt.execute();

			// Museum 4
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Musee d'Orsay");
			pStmt.setString(2, "Philadelphia, PA");
			pStmt.setString(3, "33-(0)14-049-4814");
			pStmt.setString(4, "1986");
			pStmt.execute();

			// Museum 5
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "The Metropolitan Museum of Art");
			pStmt.setString(2, "New York, NY");
			pStmt.setString(3, "212-535-7710");
			pStmt.setString(4, "1870");
			pStmt.execute();

			usedStmts.add(pStmt);
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}

		log.info("Added museums to test database.");
	}

	public void addPaintings() {
		PreparedStatement pStmt;
		String query = DBConstants.addPainting;

		try {
			// Painting 1
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "The Large bathers");
			pStmt.setString(2, "Philadelphia Art Museum");
			pStmt.setString(3, "Paul Cezanne");
			pStmt.setString(4, "1898");
			pStmt.execute();

			// Painting 2
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Nude Descending a Staircase, No. 2");
			pStmt.setString(2, "Philadelphia Art Museum");
			pStmt.setString(3, "Marcel Duchamp");
			pStmt.setString(4, "1912");
			pStmt.execute();

			// Painting 3
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "The Gross Clinic");
			pStmt.setString(2, "Philadelphia Art Museum");
			pStmt.setString(3, "Thomas Eakins");
			pStmt.setString(4, "1875");
			pStmt.execute();

			// Painting 4
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "The Bride Stripping by Her Bachelors");
			pStmt.setString(2, "Philadelphia Art Museum");
			pStmt.setString(3, "Marcel Duchamp");
			pStmt.setString(4, "1923");
			pStmt.execute();

			// Painting 5
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "The Stary Night");
			pStmt.setString(2, "Museum of Modern Art");
			pStmt.setString(3, "Vincent Van Gogh");
			pStmt.setString(4, "1889");
			pStmt.execute();

			// Painting 6
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "The Persistence of Memory");
			pStmt.setString(2, "Museum of Modern Art");
			pStmt.setString(3, "Salvador Dali");
			pStmt.setString(4, "1931");
			pStmt.execute();

			// Painting 7
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Les Demoiselles d'Avignon");
			pStmt.setString(2, "Museum of Modern Art");
			pStmt.setString(3, "Pablo Picaso");
			pStmt.setString(4, "1907");
			pStmt.execute();

			// Painting 8
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Christian's World");
			pStmt.setString(2, "Museum of Modern Art");
			pStmt.setString(3, "Andrew Wyeth");
			pStmt.setString(4, "1948");
			pStmt.execute();

			// Painting 9
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Mona Lisa");
			pStmt.setString(2, "Louvre Museum");
			pStmt.setString(3, "Leonardo de Vinci");
			pStmt.setString(4, "1503");
			pStmt.execute();

			// Painting 10
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Venud De Milo");
			pStmt.setString(2, "Louvre Museum");
			pStmt.setString(3, "Alexandros of Antioch");
			pStmt.setString(4, "1820");
			pStmt.execute();

			// Painting 11
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Liberty Leading the People");
			pStmt.setString(2, "Louvre Museum");
			pStmt.setString(3, "Eugene Delacroix");
			pStmt.setString(4, "1830");
			pStmt.execute();

			// Painting 12
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "The Raft of the Medusa");
			pStmt.setString(2, "Louvre Museum");
			pStmt.setString(3, "Theodore Gericault");
			pStmt.setString(4, "1819");
			pStmt.execute();

			// Painting 13
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Portrait of Madame X");
			pStmt.setString(2, "The Metropolitan Museum of Art");
			pStmt.setString(3, "John Singer Sargent");
			pStmt.setString(4, "1884");
			pStmt.execute();

			// Painting 14
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Study of a Young Woman");
			pStmt.setString(2, "The Metropolitan Museum of Art");
			pStmt.setString(3, "Johanna Vermeer");
			pStmt.setString(4, "1667");
			pStmt.execute();

			// Painting 15
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Self-Portrait with a Straw Hat");
			pStmt.setString(2, "The Metropolitan Museum of Art");
			pStmt.setString(3, "Vincent van Gogh");
			pStmt.setString(4, "1887");
			pStmt.execute();

			// Painting 16
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Cypresses");
			pStmt.setString(2, "The Metropolitan Museum of Art");
			pStmt.setString(3, "Vincent van Gogh");
			pStmt.setString(4, "1889");
			pStmt.execute();

			// Painting 17
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Van Gogh self-Portrait");
			pStmt.setString(2, "Musee d'Orsay");
			pStmt.setString(3, "Vincent van Gogh");
			pStmt.setString(4, "1889");
			pStmt.execute();

			// Painting 18
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "L'Origine du monde");
			pStmt.setString(2, "Musee d'Orsay");
			pStmt.setString(3, "Gustave Courbet");
			pStmt.setString(4, "1866");
			pStmt.execute();

			// Painting 19
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "A Meeting");
			pStmt.setString(2, "Musee d'Orsay");
			pStmt.setString(3, "Marie Bashkrsteff");
			pStmt.setString(4, "1884");
			pStmt.execute();

			// Painting 20
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Le Dejeuner sur l'herbe");
			pStmt.setString(2, "Musee d'Orsay");
			pStmt.setString(3, "Edouard Manet");
			pStmt.setString(4, "1863");
			pStmt.execute();

			usedStmts.add(pStmt);
			log.info("Added paintings to test database.");
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
	}

	private void reportQueryResults(String query) {
		ResultSet rs;
		Statement stmt;

		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);

			StringBuilder sb = new StringBuilder();
			ResultSetMetaData rsmd = rs.getMetaData();
			int totalCols = rsmd.getColumnCount();
			int[] colCounts = new int[totalCols];
			String[] colLabels = new String[totalCols];
			for (int i = 0; i < totalCols; i++) {
				colCounts[i] = rsmd.getColumnDisplaySize(i + 1);
				colLabels[i] = rsmd.getColumnLabel(i + 1);
				if (colLabels[i].length() > colCounts[i]) {
					colLabels[i] = colLabels[i].substring(0, colCounts[i]);
				}
				sb.append(String.format("| %" + colCounts[i] + "s ", colLabels[i]));
			}
			sb.append("|\n");

			String horizontalLine = getHorizontalLine(colCounts);
			while (rs.next()) {
				sb.append(horizontalLine);
				for (int i = 0; i < totalCols; i++) {
					sb.append(String.format("| %" + colCounts[i] + "s ", rs.getString(i + 1)));
				}
				sb.append("|\n");
			}

			usedResSets.add(rs);
			usedStmts.add(stmt);
			log.info("Query successful. Displaying results.");
			System.out.println("\n" + getHorizontalLine(colCounts) + sb.toString());
		} catch (SQLException ex) {
			log.error("Encountered SQLException.", ex);
		}
	}

	private void setPhoneNumber(String museumName, String newPhoneNumber) {
		PreparedStatement pStmt;
		String query = DBConstants.updateMuseumPhone;

		try {
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, newPhoneNumber);
			pStmt.setString(2, museumName);
			pStmt.execute();

			usedStmts.add(pStmt);
			log.info("Successfully updated phone number for museum '" + museumName + "' to '" + newPhoneNumber + "'.");
		} catch (SQLException ex) {
			log.error("SQL Exception", ex);
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
