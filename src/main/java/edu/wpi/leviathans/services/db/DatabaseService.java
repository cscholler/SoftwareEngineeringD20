package edu.wpi.leviathans.services.db;

import java.sql.*;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseService {
	private Connection connection;

	public DatabaseService(Properties props) {
		connect(props);
	}

	private void connect(Properties props) {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (ClassNotFoundException ex) {
			log.error("ClassNotFoundException", ex);
			return;
		}

		try {
			connection = DriverManager.getConnection("jdbc:derby:memory:myDB;create=true", props);
		} catch (SQLException ex) {
			log.error("SQLException", ex);
		}
	}

	public void disconnect() {
		try {
			if (connection != null) {
				connection.commit();
				connection.close();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void buildTestDB() {
		Statement stmt;
		String query;
		try {
			stmt = connection.createStatement();
			query = DBConstants.createMuseumsTable;
			stmt.execute(query);
			query = DBConstants.createPaintingsTable;
			stmt.execute(query);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		addMuseums();
		addPaintings();
	}

	public void addMuseums() {
		PreparedStatement pStmt;
		String query;
		query = DBConstants.addMuseum;
		try {
			// Museum 1
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Philadelphia Art Museum");
			pStmt.setString(2, "Philadelphia, PA");
			pStmt.setString(3, "215-763-8100");
			pStmt.setString(4, "1877");

			// Museum 2
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Lourvre Museum");
			pStmt.setString(2, "Paris, France");
			pStmt.setString(3, "33 (0)1 40 20 53 17");
			pStmt.setString(4, "1793");

			// Museum 3
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Museum of Modern Art");
			pStmt.setString(2, "Midtown Manhattan, New York City");
			pStmt.setString(3, "212-708-9400");
			pStmt.setString(4, "1929");

			// Museum 4
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Musée d’Orsay");
			pStmt.setString(2, "Philadelphia, PA");
			pStmt.setString(3, "33-(0)14-049-4814");
			pStmt.setString(4, "1986");

			// Museum 5
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "The Metropolitan Museum of Art");
			pStmt.setString(2, "New York, NY");
			pStmt.setString(3, "212-535-7710");
			pStmt.setString(4, "1870");
		} catch (SQLException ex) {
			log.error("SQLException", ex);
		}
	}

	public void addPaintings() {
		PreparedStatement pStmt;
		String query;
		query = DBConstants.addPainting;
		try {
			// Painting 1
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "The Large bathers");
			pStmt.setString(2, "Philadelphia Art Museum");
			pStmt.setString(3, "Paul Cezanne");
			pStmt.setString(4, "1898");

			// Painting 2
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Nude Descending a Staircase, No. 2");
			pStmt.setString(2, "Philadelphia Art Museum");
			pStmt.setString(3, "Marcel Duchamp");
			pStmt.setString(4, "1912");

			// Painting 3
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "The Gross Clinic");
			pStmt.setString(2, "Philadelphia Art Museum");
			pStmt.setString(3, "Thomas Eakins");
			pStmt.setString(4, "1875");

			// Painting 4
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "The Bride Stripping by Her Bachelors");
			pStmt.setString(2, "Philadelphia Art Museum");
			pStmt.setString(3, "Marcel Duchamp");
			pStmt.setString(4, "1923");

			// Painting 5
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "The Stary Night");
			pStmt.setString(2, "Museum of Modern Art");
			pStmt.setString(3, "Vincent Van Gogh");
			pStmt.setString(4, "1889");

			// Painting 6
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "The Persistence of Memory");
			pStmt.setString(2, "Museum of Modern Art");
			pStmt.setString(3, "Salvador Dali");
			pStmt.setString(4, "1931");

			// Painting 7
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Les Demoiselles d'Avignon");
			pStmt.setString(2, "Museum of Modern Art");
			pStmt.setString(3, "Pablo Picaso");
			pStmt.setString(4, "1907");

			// Painting 8
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Christian's World");
			pStmt.setString(2, "Museum of Modern Art");
			pStmt.setString(3, "Andrew Wyeth");
			pStmt.setString(4, "1948");

			// Painting 9
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Mona Lisa");
			pStmt.setString(2, "Louvre Museum");
			pStmt.setString(3, "Leonardo de Vinci");
			pStmt.setString(4, "1503");

			// Painting 10
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Venud De Milo");
			pStmt.setString(2, "Louvre Museum");
			pStmt.setString(3, "Alexandros of Antioch");
			pStmt.setString(4, "1820");

			// Painting 11
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Liberty Leading the People");
			pStmt.setString(2, "Louvre Museum");
			pStmt.setString(3, "Eugene Delacroix");
			pStmt.setString(4, "1830");

			// Painting 12
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "The Raft of the Medusa");
			pStmt.setString(2, "Louvre Museum");
			pStmt.setString(3, "Theodore Gericault");
			pStmt.setString(4, "1819");

			// Painting 13
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Portrait of Madame X");
			pStmt.setString(2, "The Metropolitan Museum of Art");
			pStmt.setString(3, "John Singer Sargent");
			pStmt.setString(4, "1884");

			// Painting 14
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Study of a Young Woman");
			pStmt.setString(2, "The Metropolitan Museum of Art");
			pStmt.setString(3, "Johanna Vermeer");
			pStmt.setString(4, "1667");

			// Painting 15
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Self-Portrait with a Straw Hat");
			pStmt.setString(2, "The Metropolitan Museum of Art");
			pStmt.setString(3, "Vincent van Gogh");
			pStmt.setString(4, "1887");

			// Painting 16
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Cypresses");
			pStmt.setString(2, "The Metropolitan Museum of Art");
			pStmt.setString(3, "Vincent van Gogh");
			pStmt.setString(4, "1889");

			// Painting 17
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Van Gogh self-Portrait");
			pStmt.setString(2, "Musée d’Orsay");
			pStmt.setString(3, "Vincent van Gogh");
			pStmt.setString(4, "1889");

			// Painting 18
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "L'Origine du monde");
			pStmt.setString(2, "Musée d’Orsay");
			pStmt.setString(3, "Gustave Courbet");
			pStmt.setString(4, "1866");

			// Painting 19
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "A Meeting");
			pStmt.setString(2, "Musée d’Orsay");
			pStmt.setString(3, "Marie Bashkrsteff");
			pStmt.setString(4, "1884");

			// Painting 20
			pStmt = connection.prepareStatement(query);
			pStmt.setString(1, "Le Dejeuner sur l'herbe");
			pStmt.setString(2, "Musée d’Orsay");
			pStmt.setString(3, "Edouard Manet");
			pStmt.setString(4, "1863");
		} catch (SQLException ex) {
			log.error("SQLException", ex);
		}
	}

	public void handleUserRequest(int programMode, String museumName, String newPhoneNumber) {
		switch (programMode) {
			case 1: {
				reportMuseumInfo();
			}
			break;
			case 2: {
				reportPaintingInfo();
			}
			break;
			case 3: {
				setPhoneNumber(museumName, newPhoneNumber);
			}
			break;
			case 4:
			default: {
				disconnect();
				System.exit(0);
			}
		}
	}

	private void reportMuseumInfo() {
	}

	private void reportPaintingInfo() {
	}

	private void setPhoneNumber(String museumName, String newPhoneNumber) {
	}
}
