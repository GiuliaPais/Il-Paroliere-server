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
 * @version 0.9.4
 */
@SuppressWarnings("SpellCheckingInspection")
public class PlayerDAOImpl implements PlayerDAO{

	private Connection connection;

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
		statement.setInt(7, player.getProfileImage());
		statement.executeUpdate();
		statement.close();
	}

	@Override
	public List<Player> getAll() throws SQLException {
		List<Player> playerList = new ArrayList<>();
		String selectAllPlayers = "SELECT * FROM Player";
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
			player.setImgColor(rs.getString(TableAttributes.ImageColor.getColumn_index()));
			player.setBgColor(rs.getString(TableAttributes.BgColor.getColumn_index()));
			playerList.add(player);
		}
		rs.close();
		statement.close();
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
			player.setImgColor(rs.getString(TableAttributes.ImageColor.getColumn_index()));
			player.setBgColor(rs.getString(TableAttributes.BgColor.getColumn_index()));
		}
		rs.close();
		statement.close();
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
			player.setImgColor(rs.getString(TableAttributes.ImageColor.getColumn_index()));
			player.setBgColor(rs.getString(TableAttributes.BgColor.getColumn_index()));
		}
		rs.close();
		statement.close();
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
			player.setImgColor(rs.getString(TableAttributes.ImageColor.getColumn_index()));
			player.setBgColor(rs.getString(TableAttributes.BgColor.getColumn_index()));
		}
		rs.close();
		statement.close();
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
			player.setImgColor(rs.getString(TableAttributes.ImageColor.getColumn_index()));
			player.setBgColor(rs.getString(TableAttributes.BgColor.getColumn_index()));
		}
		rs.close();
		statement.close();
		return player;
	}

	@Override
	public void update(String userID, TableAttributes[] attributes, Object[] values) throws SQLException {
		StringBuilder query = new StringBuilder("UPDATE Player SET ");
		for (int i = 0; i < attributes.length; i++) {
			query.append(attributes[i].name()).append("=");
			if (i < values.length - 1) {
				if (values[i] instanceof String) {
					query.append("'").append(values[i]).append("', ");
				} else {
					query.append(values[i]).append(", ");
				}
			} else {
				if (values[i] instanceof String) {
					query.append("'").append(values[i]).append("' ");
				} else {
					query.append(values[i]).append(" ");
				}
			}
		}
		query.append("WHERE userID='").append(userID).append("'");
		PreparedStatement statement = connection.prepareStatement(query.toString());
		statement.executeUpdate();
		statement.close();
	}

	@Override
	public void updateAll(TableAttributes[] attributes, Object[] values) throws SQLException {
		StringBuilder query = new StringBuilder("UPDATE Player SET ");
		for (int i = 0; i < attributes.length; i++) {
			query.append(attributes[i].name()).append("=");
			if (i < values.length - 1) {
				query.append(values[i]).append(", ");
			} else {
				query.append(values[i]).append(" ");
			}
		}
		PreparedStatement statement = connection.prepareStatement(query.toString());
		statement.executeUpdate();
		statement.close();
	}

	@Override
	public void delete(String userID) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(deletePlayer);
		statement.setString(1, userID);
		statement.executeUpdate();
		statement.close();
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
		StringBuilder query = new StringBuilder("INSERT INTO Player(");
		TableAttributes[] fields = TableAttributes.values();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].equals(TableAttributes.ImageColor) | fields[i].equals(TableAttributes.BgColor)) {
				continue;
			}
			if (i < fields.length - 3) {
				query.append(fields[i]).append(", ");
			} else {
				query.append(fields[i]);
			}
		}
		query.append(") VALUES(");
		for (int i = 0; i < fields.length-2; i++) {
			if (i < fields.length - 3) {
				query.append("?, ");
			} else {
				query.append("?)");
			}
		}
		return query.toString();
	}
}
