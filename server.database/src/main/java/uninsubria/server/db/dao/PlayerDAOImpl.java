/**
 * 
 */
package uninsubria.server.db.dao;

import uninsubria.utils.business.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing the DAO (Data Access Object) for the "Player" table in the database.
 *
 * @author Alessandro Lerro
 * @author Giulia Pais
 * @version 0.9.1
 */
public class PlayerDAOImpl implements PlayerDAO{

	private Connection connection;
	
	private final String selectAllPlayers = "SELECT * FROM Player";
	private final String selectPlayer = "SELECT * FROM Player WHERE "+ TableAttributes.UserID +"=?";
	private final String selectPlayerbyEmail = "SELECT * FROM Player WHERE " + TableAttributes.Email + "=?";
	private final String createPlayer = createInsertQuery();
	private final String deletePlayer = "DELETE FROM Player WHERE " + TableAttributes.UserID + "=?";

	/**
	 * Instantiates a new PlayerDAOImpl.
	 */
	public PlayerDAOImpl(){
    }

	@Override
	public void create(Player player) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(createPlayer);
		statement.setString(1, player.getPlayerID());
		statement.setString(2, player.getEmail());
		statement.setBoolean(3, player.isLogStatus());
		statement.setString(4, player.getName());
		statement.setString(5, player.getSurname());
		statement.setString(6, player.getPassword());
		statement.setInt(7, player.getProfileImage());;
		statement.executeUpdate();
	}

	@Override
	public List<Player> getAll() throws SQLException {
		List<Player> playerList = new ArrayList<>();
		PreparedStatement statement = connection.prepareStatement(selectAllPlayers);
		ResultSet rs = statement.executeQuery();
		while(rs.next()) {
			Player player = new Player();
			player.setPlayerID(rs.getString(TableAttributes.UserID.getColumn_index()));
			player.setEmail(rs.getString(TableAttributes.Email.getColumn_index()));
			player.setLogStatus(rs.getBoolean(TableAttributes.Log_Status.getColumn_index()));
			player.setName(rs.getString(TableAttributes.Name.getColumn_index()));
			player.setSurname(rs.getString(TableAttributes.Surname.getColumn_index()));
			player.setPassword(rs.getString(TableAttributes.Password.getColumn_index()));
			player.setProfileImage(rs.getShort(TableAttributes.ProfileImage.getColumn_index()));
			playerList.add(player);
		}
		return playerList;
	}

	@Override
	public Player getByUserId(String id) throws SQLException {
		Player player = null;
		PreparedStatement statement = connection.prepareStatement(selectPlayer);
		statement.setString(1, id);
		ResultSet rs = statement.executeQuery();
		while(rs.next()) {
			player = new Player();
			player.setPlayerID(rs.getString(TableAttributes.UserID.getColumn_index()));
			player.setEmail(rs.getString(TableAttributes.Email.getColumn_index()));
			player.setLogStatus(rs.getBoolean(TableAttributes.Log_Status.getColumn_index()));
			player.setName(rs.getString(TableAttributes.Name.getColumn_index()));
			player.setSurname(rs.getString(TableAttributes.Surname.getColumn_index()));
			player.setPassword(rs.getString(TableAttributes.Password.getColumn_index()));
			player.setProfileImage(rs.getShort(TableAttributes.ProfileImage.getColumn_index()));
		}
		return player;
	}

	@Override
	public Player getByEmail(String email) throws SQLException {
		Player player = null;
		PreparedStatement statement = connection.prepareStatement(selectPlayerbyEmail);
		statement.setString(1, email);
		ResultSet rs = statement.executeQuery();
		while(rs.next()) {
			player = new Player();
			player.setPlayerID(rs.getString(TableAttributes.UserID.getColumn_index()));
			player.setEmail(rs.getString(TableAttributes.Email.getColumn_index()));
			player.setLogStatus(rs.getBoolean(TableAttributes.Log_Status.getColumn_index()));
			player.setName(rs.getString(TableAttributes.Name.getColumn_index()));
			player.setSurname(rs.getString(TableAttributes.Surname.getColumn_index()));
			player.setPassword(rs.getString(TableAttributes.Password.getColumn_index()));
			player.setProfileImage(rs.getShort(TableAttributes.ProfileImage.getColumn_index()));
		}
		return player;
	}

	@Override
	public Player getByUserIdForUpdate(String id) throws SQLException {
		Player player = null;
		PreparedStatement statement = connection.prepareStatement(selectPlayer + " FOR UPDATE");
		statement.setString(1, id);
		ResultSet rs = statement.executeQuery();
		while(rs.next()) {
			player = new Player();
			player.setPlayerID(rs.getString(TableAttributes.UserID.getColumn_index()));
			player.setEmail(rs.getString(TableAttributes.Email.getColumn_index()));
			player.setLogStatus(rs.getBoolean(TableAttributes.Log_Status.getColumn_index()));
			player.setName(rs.getString(TableAttributes.Name.getColumn_index()));
			player.setSurname(rs.getString(TableAttributes.Surname.getColumn_index()));
			player.setPassword(rs.getString(TableAttributes.Password.getColumn_index()));
			player.setProfileImage(rs.getShort(TableAttributes.ProfileImage.getColumn_index()));
		}
		return player;
	}

	@Override
	public Player getByEmailForUpdate(String email) throws SQLException {
		Player player = null;
		PreparedStatement statement = connection.prepareStatement(selectPlayerbyEmail + " FOR UPDATE");
		statement.setString(1, email);
		ResultSet rs = statement.executeQuery();
		while(rs.next()) {
			player = new Player();
			player.setPlayerID(rs.getString(TableAttributes.UserID.getColumn_index()));
			player.setEmail(rs.getString(TableAttributes.Email.getColumn_index()));
			player.setLogStatus(rs.getBoolean(TableAttributes.Log_Status.getColumn_index()));
			player.setName(rs.getString(TableAttributes.Name.getColumn_index()));
			player.setSurname(rs.getString(TableAttributes.Surname.getColumn_index()));
			player.setPassword(rs.getString(TableAttributes.Password.getColumn_index()));
			player.setProfileImage(rs.getShort(TableAttributes.ProfileImage.getColumn_index()));
		}
		return player;
	}

	@Override
	public void update(String userID, TableAttributes[] attributes, Object[] values) throws SQLException {
		String query = "UPDATE Player SET ";
		for (int i = 0; i < attributes.length; i++) {
			query += (attributes[i].name() + "=");
			switch (attributes[i]) {
				case UserID:
				case Email:
				case Name:
				case Surname:
				case Password:
					if (i < values.length-1) {
						query += ((String) values[i] + ", ");
					} else {
						query += ((String) values[i] + " ");
					}
					break;
				case Log_Status:
					if (i < values.length-1) {
						query += ((Boolean) values[i] + ", ");
					} else {
						query += ((Boolean) values[i] + " ");
					}
					break;
				case ProfileImage:
					if (i < values.length-1) {
						query += ((Integer) values[i] + ", ");
					} else {
						query += ((Integer) values[i] + " ");
					}
					break;
			}
			query += "WHERE userID='" + userID + "'";
		}
		PreparedStatement statement = connection.prepareStatement(query);
		statement.executeUpdate();
	}

	@Override
	public void delete(String userID) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(deletePlayer);
		statement.setString(1, userID);
		statement.executeUpdate();
	}

	@Override
	public void setAutocommit(boolean value) throws SQLException {
		this.connection.setAutoCommit(value);
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	private String createInsertQuery() {
		String query = "INSERT INTO Player(";
		TableAttributes[] fields = TableAttributes.values();
		for (int i = 0; i < fields.length; i++) {
			if (i < fields.length - 1) {
				query += fields[i] + ", ";
			} else {
				query += fields[i];
			}
		}
		query += ") VALUES(";
		for (int i = 0; i < fields.length; i++) {
			if (i < fields.length - 1) {
				query += "?, ";
			} else {
				query += "?)";
			}
		}
		return query;
	}
}
