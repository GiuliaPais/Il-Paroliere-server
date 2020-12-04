package uninsubria.server.db.dao;

import uninsubria.server.db.businesslayer.GameInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * The interface GameInfoDAO (Data Access Model).
 *
 * @author Alessandro Lerro
 * @author Giulia Pais
 * @version 0.9.1
 */
public interface GameInfoDAO {

	/**
	 * Names of the attributes in the GameInfo table.
	 */
	enum TableAttributes {
		GameID(1),
		Grid(2),
		Num_players(3),
		Ruleset(4),
		Language(5);

		private final int column_index;

		TableAttributes(int i) {
			this.column_index = i;
		}

		/**
		 * Gets column index.
		 *
		 * @return the column index
		 */
		public int getColumn_index() {
			return column_index;
		}
	}

	/**
	 * Inserts a new row in the GameInfo table.
	 *
	 * @param gameInfo the GameInfo object to insert
	 * @throws SQLException
	 */
	void create(GameInfo gameInfo) throws SQLException;

	/**
	 * Gets all rows in the GameInfo table.
	 *
	 * @return A list of all the rows in the GameInfo table
	 * @throws SQLException
	 */
	List<GameInfo> getAll() throws SQLException;

	/**
	 * Gets a row by game id.
	 *
	 * @param gameId the game id
	 * @return The row with the corresponding gameID (null if none was found)
	 * @throws SQLException
	 */
	GameInfo getByGameId(UUID gameId) throws SQLException;

	/**
	 * Updates a row in the GameInfo table.
	 * The method supports the update of multiple attributes.
	 *
	 * @param gameID     the game id to update
	 * @param attributes the attributes to update
	 * @param values     the values
	 * @throws SQLException
	 */
	void update(UUID gameID, TableAttributes[] attributes, Object[] values) throws SQLException;

	/**
	 * Deletes the row with the corresponding game id.
	 *
	 * @param gameID the game id
	 * @throws SQLException
	 */
	void delete(UUID gameID) throws SQLException;

	/**
	 * Gets the connection being used by this DAO object.
	 *
	 * @return A Connection object
	 */
	Connection getConnection();

	/**
	 * Sets connection for this DAO object.
	 *
	 * @param connection the connection
	 */
	void setConnection(Connection connection);
	
}
