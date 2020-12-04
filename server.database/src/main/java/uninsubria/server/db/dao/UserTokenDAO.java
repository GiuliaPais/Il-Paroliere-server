package uninsubria.server.db.dao;

import uninsubria.server.db.businesslayer.UserToken;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The interface UserTokenDAO (Data Access Model).
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public interface UserTokenDAO {
    /**
     * Names of the attributes in the UserToken table.
     */
    enum TableAttributes{
        USERID(1),
        EMAIL(2),
        NAME(3),
        LASTNAME(4),
        PASSWORD(5),
        REQUEST_TYPE(6),
        TOKEN(7),
        GEN_TIME(8),
        EXPIRY_TIME(9);

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
     * Gets a row by primary key.
     *
     * @param userID      the user id
     * @param requestType the request type
     * @return a UserToken object or null
     * @throws SQLException the sql exception
     */
    UserToken getByPk(String userID, String requestType) throws SQLException;

    /**
     * Gets a row by the email and request type.
     *
     * @param email the email
     * @param requestType the request type
     * @return a UserToken object or null
     * @throws SQLException
     */
    UserToken getByEmail(String email, String requestType) throws SQLException;

    /**
     * Creates a row in the UserToken table.
     *
     * @param userToken the user token
     * @throws SQLException the sql exception
     */
    void create(UserToken userToken) throws SQLException;

    /**
     * Updates a row provided the primary key.
     *
     * @param userID      the user id
     * @param requestType the request type
     * @param attributes  the attributes
     * @param values      the values
     * @throws SQLException
     */
    void update(String userID, String requestType, TableAttributes[] attributes, Object[] values) throws SQLException;

    /**
     * Deletes a row provided the primary key.
     *
     * @param userID      the user id
     * @param requestType the request type
     * @throws SQLException
     */
    void delete(String userID, String requestType) throws SQLException;

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
