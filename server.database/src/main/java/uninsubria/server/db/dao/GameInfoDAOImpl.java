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
 * @version 0.9.3
 */
public class GameInfoDAOImpl implements GameInfoDAO {
	private Connection connection;

	private final String selectById = "SELECT * FROM GameInfo WHERE " + TableAttributes.GameID + "=?";
	private final String create = createInsertQuery();
	private final String delete = "DELETE FROM GameInfo WHERE " + TableAttributes.GameID + "=?";

	/**
	 * Instantiates a new GameInfoDAOImpl.
	 */
	public GameInfoDAOImpl() {
	}

	private String createInsertQuery() {
		StringBuilder query = new StringBuilder("INSERT INTO GameInfo(");
		TableAttributes[] fields = TableAttributes.values();
		for (int i = 0; i < fields.length; i++) {
			if (i < fields.length - 1) {
				query.append(fields[i]).append(", ");
			} else {
				query.append(fields[i]);
			}
		}
		query.append(") VALUES(");
		for (int i = 0; i < fields.length; i++) {
			if (i < fields.length - 1) {
				query.append("?, ");
			} else {
				query.append("?)");
			}
		}
		return query.toString();
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
		String selectAll = "SELECT * FROM GameInfo";
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
		StringBuilder query = new StringBuilder("UPDATE GameInfo SET ");
		for (int i = 0; i < attributes.length; i++) {
			query.append(attributes[i].name()).append("=");
			if (attributes[i] == TableAttributes.Grid) {
				String[] value = (String[]) values[i];
				StringBuilder value_to_write = new StringBuilder("'{");
				for (int j = 0; j < value.length; j++) {
					if (j < value.length - 1) {
						value_to_write.append("\"").append(value[j]).append("\", ");
					} else {
						value_to_write.append("\"").append(value[j]).append("\"}'");
					}
				}
				if (i < values.length - 1) {
					query.append(value_to_write).append(", ");
				} else {
					query.append(value_to_write).append(" ");
				}
			} else {
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
		}
		query.append("WHERE ").append(TableAttributes.GameID).append("='").append(gameID).append("'");
		PreparedStatement statement = connection.prepareStatement(query.toString());
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
