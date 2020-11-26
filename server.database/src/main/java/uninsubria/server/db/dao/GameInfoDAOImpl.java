/**
 * 
 */
package uninsubria.server.db.dao;

import uninsubria.server.db.businesslayer.GameInfo;
import uninsubria.utils.languages.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class implementing the DAO (Data Access Object) for the "GameInfo" table in the database.
 *
 * @author Alessandro Lerro
 * @author Giulia Pais
 * @version 0.9.2
 */
public class GameInfoDAOImpl implements GameInfoDAO {
	private Connection connection;

	private final String selectAll = "SELECT * FROM GameInfo";
	private final String selectById = "SELECT * FROM GameInfo WHERE " + TableAttributes.GameID + "=?";
	private final String create = createInsertQuery();
	private final String delete = "DELETE FROM GameInfo WHERE " + TableAttributes.GameID + "=?";

	/**
	 * Instantiates a new GameInfoDAOImpl.
	 */
	public GameInfoDAOImpl() {
	}

	private String createInsertQuery() {
		String query = "INSERT INTO GameInfo(";
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

	@Override
	public void create(GameInfo gameInfo) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(create);
		statement.setObject(1, gameInfo.getGameId());
		statement.setArray(2, connection.createArrayOf("VARCHAR", gameInfo.getAllMatchesGrid()));
		statement.setInt(3, gameInfo.getNumPlayers());
		statement.setString(4, gameInfo.getRuleset());
		statement.setString(5, gameInfo.getLanguage().name());
		statement.executeUpdate();
		statement.close();
	}

	@Override
	public List<GameInfo> getAll() throws SQLException {
		List<GameInfo> gameInfoList = new ArrayList<>();
		PreparedStatement statement = connection.prepareStatement(selectAll);
		ResultSet rs = statement.executeQuery();
		while(rs.next()) {
			GameInfo curr = new GameInfo();
			curr.setGameId(rs.getObject(TableAttributes.GameID.getColumn_index(), UUID.class));
			curr.setAllMatchesGrid((String[]) rs.getArray(TableAttributes.Grid.getColumn_index()).getArray());
			curr.setNumPlayers(rs.getByte(TableAttributes.Num_players.getColumn_index()));
			curr.setRuleset(rs.getString(TableAttributes.Ruleset.getColumn_index()));
			curr.setLanguage(Language.valueOf(rs.getString(TableAttributes.Language.getColumn_index())));
			gameInfoList.add(curr);
		}
		rs.close();
		statement.close();
		return gameInfoList;
	}

	@Override
	public GameInfo getByGameId(UUID gameId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(selectById);
		statement.setObject(1, gameId);
		ResultSet rs = statement.executeQuery();
		GameInfo gameinfo = new GameInfo();
		while(rs.next()) {
			gameinfo.setGameId(rs.getObject(TableAttributes.GameID.getColumn_index(), UUID.class));
			gameinfo.setAllMatchesGrid((String[]) rs.getArray(TableAttributes.Grid.getColumn_index()).getArray());
			gameinfo.setNumPlayers(rs.getByte(TableAttributes.Num_players.getColumn_index()));
			gameinfo.setRuleset(rs.getString(TableAttributes.Ruleset.getColumn_index()));
			gameinfo.setLanguage(Language.valueOf(rs.getString(TableAttributes.Language.getColumn_index())));
		}
		rs.close();
		statement.close();
		return gameinfo;
	}

	@Override
	public void update(UUID gameID, TableAttributes[] attributes, Object[] values) throws SQLException {
		String query = "UPDATE GameInfo SET ";
		for (int i = 0; i < attributes.length; i++) {
			query += (attributes[i].name() + "=");
			switch (attributes[i]) {
				case GameID:
					if (i < values.length-1) {
						query += ((UUID) values[i] + ", ");
					} else {
						query += ((UUID) values[i] + " ");
					}
					break;
				case Ruleset:
				case Language:
					if (i < values.length-1) {
						query += ((String) values[i] + ", ");
					} else {
						query += ((String) values[i] + " ");
					}
					break;
				case Num_players:
					if (i < values.length-1) {
						query += ((Byte) values[i] + ", ");
					} else {
						query += ((Byte) values[i] + " ");
					}
					break;
				case Grid:
					String[] value = (String[]) values[i];
					String value_to_write = "'{";
					for (int j = 0; j < value.length; j++) {
						if (j < value.length-1) {
							value_to_write += "\"" + value[j] + "\", ";
						} else {
							value_to_write += "\"" + value[j] + "\"}'";
						}
					}
					if (i < values.length-1) {
						query += (value_to_write + ", ");
					} else {
						query += (value_to_write + " ");
					}
					break;
			}
		}
		query += "WHERE " + TableAttributes.GameID + "='" + gameID + "'";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.executeUpdate();
		statement.close();
	}

	@Override
	public void delete(UUID gameID) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(delete);
		statement.setObject(1, gameID);
		statement.executeUpdate();
		statement.close();
	}

	@Override
	public Connection getConnection() {
		return this.connection;
	}

	@Override
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
