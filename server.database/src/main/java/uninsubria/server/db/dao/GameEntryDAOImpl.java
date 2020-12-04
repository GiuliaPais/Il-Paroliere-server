
package uninsubria.server.db.dao;

import uninsubria.server.db.businesslayer.GameEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class implementing the DAO (Data Access Object) for the "GameEntry" table in the database.
 *
 * @author Alessandro Lerro
 * @author Giulia Pais
 * @version 0.9.2
 */
public class GameEntryDAOImpl implements GameEntryDAO {

    private Connection connection;

    private final String selectByPK = "SELECT * FROM GameEntry WHERE " + TableAttributes.Game + "=? AND " +
            TableAttributes.PlayerId + "=? AND " + TableAttributes.Match + "=? AND " +
            TableAttributes.Word + "=?";
	private final String delete = "DELETE FROM GameEntry WHERE " + TableAttributes.Game + "=? AND " +
            TableAttributes.PlayerId + "=? AND " + TableAttributes.Match + "=? AND " +
            TableAttributes.Word + "=?";
	private final String insert = createInsertQuery();

    /**
     * Instantiates a new GameEntryDAO.
     */
    public GameEntryDAOImpl() {}

    private String createInsertQuery() {
        StringBuilder query = new StringBuilder("INSERT INTO GameEntry(");
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
    public void create(GameEntry gameEntry) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(insert);
        statement.setObject(1, gameEntry.getGame());
        statement.setString(2, gameEntry.getPlayerID());
        statement.setShort(3, gameEntry.getMatch());
        statement.setString(4, gameEntry.getWord());
        statement.setBoolean(5, gameEntry.isRequested());
        statement.setBoolean(6, gameEntry.isDuplicated());
        statement.setBoolean(7, gameEntry.isWrong());
        statement.setShort(8, gameEntry.getPoints());
        statement.executeUpdate();
        statement.close();
    }

    @Override
    public List<GameEntry> getAll() throws SQLException {
        List<GameEntry> gameEntries = new ArrayList<>();
        String selectAll = "SELECT * FROM GameEntry";
        PreparedStatement statement = connection.prepareStatement(selectAll);
        ResultSet rs = statement.executeQuery();
        while(rs.next()) {
            GameEntry gameEntry = new GameEntry();
            gameEntry.setGame(rs.getObject(TableAttributes.Game.getColumn_index(), UUID.class));
            gameEntry.setPlayerID(rs.getString(TableAttributes.PlayerId.getColumn_index()));
            gameEntry.setMatch(rs.getShort(TableAttributes.Match.getColumn_index()));
            gameEntry.setWord(rs.getString(TableAttributes.Word.getColumn_index()));
            gameEntry.setRequested(rs.getBoolean(TableAttributes.Requested.getColumn_index()));
            gameEntry.setDuplicated(rs.getBoolean(TableAttributes.Duplicated.getColumn_index()));
            gameEntry.setWrong(rs.getBoolean(TableAttributes.Wrong.getColumn_index()));
            gameEntry.setPoints(rs.getShort(TableAttributes.Points.getColumn_index()));
            gameEntries.add(gameEntry);
        }
        rs.close();
        statement.close();
        return gameEntries;
    }

    @Override
    public GameEntry getByPK(UUID gameID, String playerID, short match, String word) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(selectByPK);
        statement.setObject(1, gameID);
        statement.setString(2, playerID);
        statement.setShort(3, match);
        statement.setString(4, word);
        ResultSet rs = statement.executeQuery();
        GameEntry gameEntry = new GameEntry();
        while(rs.next()) {
            gameEntry.setGame(rs.getObject(TableAttributes.Game.getColumn_index(), UUID.class));
            gameEntry.setPlayerID(rs.getString(TableAttributes.PlayerId.getColumn_index()));
            gameEntry.setMatch(rs.getShort(TableAttributes.Match.getColumn_index()));
            gameEntry.setWord(rs.getString(TableAttributes.Word.getColumn_index()));
            gameEntry.setRequested(rs.getBoolean(TableAttributes.Requested.getColumn_index()));
            gameEntry.setDuplicated(rs.getBoolean(TableAttributes.Duplicated.getColumn_index()));
            gameEntry.setWrong(rs.getBoolean(TableAttributes.Wrong.getColumn_index()));
            gameEntry.setPoints(rs.getShort(TableAttributes.Points.getColumn_index()));
        }
        rs.close();
        statement.close();
        return gameEntry;
    }

    @Override
    public void update(UUID gameID, String playerID, short match, String word,
                       TableAttributes[] attributes, Object[] values) throws SQLException {
        StringBuilder query = new StringBuilder("UPDATE GameEntry SET ");
        for (int i = 0; i < attributes.length; i++) {
            query.append(attributes[i].name()).append("=");
            if (i < values.length-1) {
                query.append(values[i]).append(", ");
            } else {
                query.append(values[i]).append(" ");
            }
        }
        query.append("WHERE " + TableAttributes.Game + "='").append(gameID).append("' AND ").append(TableAttributes.PlayerId).append("='").append(playerID).append("' AND ").append(TableAttributes.Match).append("=").append(match).append(" AND ").append(TableAttributes.Word).append("='").append(word).append("'");
        System.out.println(query);
        PreparedStatement statement = connection.prepareStatement(query.toString());
        statement.executeUpdate();
        statement.close();
    }

    @Override
    public void delete(UUID gameID, String playerID, short match, String word) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(delete);
        statement.setObject(1, gameID);
        statement.setString(2, playerID);
        statement.setShort(3, match);
        statement.setString(4, word);
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
