package uninsubria.server.db.dao;

import uninsubria.utils.business.Player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * The interface PlayerDAO (Data Access Model).
 *
 * @author Alessandro Lerro
 * @author Giulia Pais
 * @version 0.9.2
 */
public interface PlayerDAO {
	/**
	 * Names of the attributes in the Player table.
	 */
	enum TableAttributes {
		UserID(1),
		Email(2),
		Log_Status(3),
		Name(4),
		Surname(5),
		Password(6),
		ProfileImage(7),
		ImageColor(8),
		BgColor(9);

		private final int column_index;

		TableAttributes(int i) {
			this.column_index = i;
		}

		public int getColumn_index() {
			return column_index;
		}
	}

	/**
	 * Inserts a new player row in the Player table.
	 *
	 * @param player the player
	 * @throws SQLException
	 */
	void create(Player player) throws SQLException;

	/**
	 * Gets all rows in the Player table.
	 *
	 * @return All the players registered
	 * @throws SQLException
	 */
	List<Player> getAll() throws SQLException;

	/**
	 * Gets a player by user id.
	 *
	 * @param id the id
	 * @return A Player object if the id exists in the table, null otherwise
	 * @throws SQLException
	 */
	Player getByUserId(String id) throws SQLException;

	/**
	 * Gets a player by email.
	 *
	 * @param email the email
	 * @return A Player object if the email exists in the table, null otherwise
	 * @throws SQLException
	 */
	Player getByEmail(String email) throws SQLException;

	/**
	 * Gets a player row by user id for update. It differs from the method {@link #getByUserId(String)}
	 * because when retrieving the player the row gets locked until an update or a rollback action is
	 * executed. This is necessary when managing login/registering procedures.
	 *
	 * @param id The user id to retrieve
	 * @return A Player object if the id exists in the table, null otherwise
	 * @throws SQLException
	 */
	Player getByUserIdForUpdate(String id) throws SQLException;

	/**
	 * Gets a player row by email for update. It differs from the method {@link #getByEmail(String)}
	 * because when retrieving the player the row gets locked until an update or a rollback action is
	 * executed. This is necessary when managing login/registering procedures.
	 *
	 * @param email The email
	 * @return A Player object if the id exists in the table, null otherwise
	 * @throws SQLException
	 */
	Player getByEmailForUpdate(String email) throws SQLException;

	/**
	 * Updates a row corresponding to the userID in the Player table.
	 * The method supports the update of multiple attributes.
	 *
	 * @param userID    The user id to update
	 * @param attributes The attribute in the table that should be changed
	 * @param values     The value to assign to the attribute
	 * @throws SQLException
	 */
	void update(String userID, TableAttributes[] attributes, Object[] values) throws SQLException;

	/**
	 * Updates all rows in the Player table.
	 * The method supports the update of multiple attributes.
	 *
	 * @param attributes The attribute in the table that should be changed
	 * @param values     The value to assign to the attribute
	 * @throws SQLException
	 */
	void updateAll(TableAttributes[] attributes, Object[] values) throws SQLException;

	/**
	 * Deletes a row from the Player table.
	 *
	 * @param userID the user id
	 * @throws SQLException
	 */
	void delete(String userID) throws SQLException;

	/**
	 * Wrapper for connection.setAutocommit() method.
	 * Useful for setting the transaction mode in certain methods.
	 *
	 * @param value true or false
	 * @throws SQLException
	 */
	void setAutocommit(boolean value) throws SQLException;

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
