
package uninsubria.server.db.dao;

import uninsubria.server.db.businesslayer.GameEntry;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * The interface GameEntryDAO (Data Access Model).
 *
 * @author Giulia Pais
 * @author Alessandro Lerro
 * @version 0.9.1
 */
public interface GameEntryDAO {

    /**
     * Names of the attributes in the GameEntry table.
     */
    enum TableAttributes {
        Game(1),
        PlayerId(2),
        Match(3),
        Word(4),
        Requested(5),
        Duplicated(6),
        Wrong(7),
        Points(8);

        private int column_index;

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
     * Inserts a new game entry row in the GameEntry table.
     *
     * @param gameEntry the game entry
     * @throws SQLException
     */
    public void create(GameEntry gameEntry) throws SQLException;

    /**
     * Gets all rows in the GameEntry table.
     *
     * @return the all
     * @throws SQLException
     */
    public List<GameEntry> getAll() throws SQLException;

    /**
     * Gets a row in GameEntry by its primary key.
     *
     * @param gameID   the game id
     * @param playerID the player id
     * @param match    the match
     * @param word     the word
     * @return the by pk
     * @throws SQLException
     */
    public GameEntry getByPK(UUID gameID, String playerID, short match, String word) throws SQLException;

    /**
     * Updates a row of GameEntry given the appropriate parameters.
     *
     * @param gameID     the game id
     * @param playerID   the player id
     * @param match      the match
     * @param word       the word
     * @param attributes the attributes to update
     * @param values     the values to associate with the attributes
     * @throws SQLException
     */
    public void update(UUID gameID, String playerID, short match, String word,
                       TableAttributes[] attributes, Object[] values) throws SQLException;

    /**
     * Deletes a row from GameEntry given the primary key.
     *
     * @param gameID   the game id
     * @param playerID the player id
     * @param match    the match
     * @param word     the word
     * @throws SQLException
     */
    public void delete(UUID gameID, String playerID, short match, String word) throws SQLException;

    /**
     * Gets connection.
     *
     * @return the connection
     */
    public Connection getConnection();

    /**
     * Sets connection.
     *
     * @param connection the connection
     */
    public void setConnection(Connection connection);
	
}
