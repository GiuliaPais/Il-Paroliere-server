package uninsubria.server.db.dao;

import uninsubria.server.db.businesslayer.UserToken;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The interface UserTokenDAO (Data Access Model).
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public interface UserTokenDAO {
    /**
     * Names of the attributes in the UserToken table.
     */
    enum TableAttributes{
        /**
         * Userid table attributes.
         */
        USERID(1),
        /**
         * Email table attributes.
         */
        EMAIL(2),
        /**
         * Name table attributes.
         */
        NAME(3),
        /**
         * Lastname table attributes.
         */
        LASTNAME(4),
        /**
         * Password table attributes.
         */
        PASSWORD(5),
        /**
         * Request type table attributes.
         */
        REQUEST_TYPE(6),
        /**
         * Token table attributes.
         */
        TOKEN(7),
        /**
         * Gen time table attributes.
         */
        GEN_TIME(8),
        /**
         * Expiry time table attributes.
         */
        EXPIRY_TIME(9);

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
     * Gets a row by primary key.
     *
     * @param userID      the user id
     * @param requestType the request type
     * @return a UserToken object
     * @throws SQLException the sql exception
     */
    public UserToken getByPk(String userID, String requestType) throws SQLException;

    /**
     * Creates a row in the UserToken table.
     *
     * @param userToken the user token
     * @throws SQLException the sql exception
     */
    public void create(UserToken userToken) throws SQLException;

    /**
     * Updates a row provided the primary key.
     *
     * @param userID      the user id
     * @param requestType the request type
     * @param attributes  the attributes
     * @param values      the values
     * @throws SQLException
     */
    public void update(String userID, String requestType, TableAttributes[] attributes, Object[] values) throws SQLException;

    /**
     * Deletes a row provided the primary key.
     *
     * @param userID      the user id
     * @param requestType the request type
     * @throws SQLException
     */
    public void delete(String userID, String requestType) throws SQLException;

    /**
     * Gets the connection being used by this DAO object.
     *
     * @return A Connection object
     */
    public Connection getConnection();

    /**
     * Sets connection for this DAO object.
     *
     * @param connection the connection
     */
    public void setConnection(Connection connection);
}
