
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

	private final String selectAll = "SELECT * FROM GameEntry";
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
        String query = "INSERT INTO GameEntry(";
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
        String query = "UPDATE GameEntry SET ";
        for (int i = 0; i < attributes.length; i++) {
            query += (attributes[i].name() + "=");
            switch (attributes[i]) {
                case Game:
                    if (i < values.length-1) {
                        query += ((UUID) values[i] + ", ");
                    } else {
                        query += ((UUID) values[i] + " ");
                    }
                    break;
                case PlayerId:
                case Word:
                    if (i < values.length-1) {
                        query += ((String) values[i] + ", ");
                    } else {
                        query += ((String) values[i] + " ");
                    }
                    break;
                case Match:
                case Points:
                    if (i < values.length-1) {
                        query += ((Short) values[i] + ", ");
                    } else {
                        query += ((Short) values[i] + " ");
                    }
                    break;
                case Duplicated:
                case Requested:
                case Wrong:
                    if (i < values.length-1) {
                        query += ((Boolean) values[i] + ", ");
                    } else {
                        query += ((Boolean) values[i] + " ");
                    }
                    break;
            }
        }
        query += "WHERE " + TableAttributes.Game + "='" + gameID + "' AND " + TableAttributes.PlayerId + "='" +
        playerID + "' AND " + TableAttributes.Match + "=" + match + " AND " + TableAttributes.Word + "='" + word +
        "'";
        System.out.println(query);
        PreparedStatement statement = connection.prepareStatement(query);
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
