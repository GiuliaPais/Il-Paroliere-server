package uninsubria.server.db.dao;

import uninsubria.server.db.businesslayer.UserToken;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Class implementing the DAO (Data Access Object) for the "UserToken" table in the database.
 * @author Giulia Pais
 * @version 0.9.1
 */
public class UserTokenDAOImpl implements UserTokenDAO {
    /*---Fields---*/
    private Connection connection;

    private final String insert = createInsertQuery();
    private final String delete = "DELETE FROM USERTOKEN WHERE " + TableAttributes.USERID + "=? AND " +
                                    TableAttributes.REQUEST_TYPE + "=?";
    private final String getByPk = "SELECT * FROM USERTOKEN WHERE " + TableAttributes.USERID + "=? AND " +
                                    TableAttributes.REQUEST_TYPE + "=?";
    private final String getByEmail = "SELECT * FROM USERTOKEN WHERE " + TableAttributes.EMAIL + "=? AND " +
                                        TableAttributes.REQUEST_TYPE + "=?";

    /*---Constructors---*/
    /**
     * Instantiates a new UserTokenDAOImpl.
     */
    public UserTokenDAOImpl() {
    }

    /*---Methods---*/
    @Override
    public UserToken getByPk(String userID, String requestType) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(getByPk);
        statement.setString(1, userID);
        statement.setString(2, requestType);
        ResultSet rs = statement.executeQuery();
        UserToken userToken = null;
        if (rs.next()) {
            userToken = new UserToken();
            userToken.setUserID(rs.getString(TableAttributes.USERID.getColumn_index()));
            userToken.setEmail(rs.getString(TableAttributes.EMAIL.getColumn_index()));
            userToken.setName(rs.getString(TableAttributes.NAME.getColumn_index()));
            userToken.setLastname(rs.getString(TableAttributes.LASTNAME.getColumn_index()));
            userToken.setPassword(rs.getString(TableAttributes.PASSWORD.getColumn_index()));
            userToken.setRequestType(rs.getString(TableAttributes.REQUEST_TYPE.getColumn_index()));
            userToken.setToken(rs.getObject(TableAttributes.TOKEN.getColumn_index(), UUID.class));
            userToken.setGenTime(rs.getObject(TableAttributes.GEN_TIME.getColumn_index(), Timestamp.class).toLocalDateTime());
            userToken.setExpiryTime(rs.getObject(TableAttributes.EXPIRY_TIME.getColumn_index(), Timestamp.class).toLocalDateTime());
        }
        rs.close();
        statement.close();
        return userToken;
    }

    @Override
    public UserToken getByEmail(String email, String requestType) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(getByEmail);
        statement.setString(1, email);
        statement.setString(2, requestType);
        ResultSet rs = statement.executeQuery();
        UserToken userToken = null;
        if (rs.next()) {
            userToken = new UserToken();
            userToken.setUserID(rs.getString(TableAttributes.USERID.getColumn_index()));
            userToken.setEmail(rs.getString(TableAttributes.EMAIL.getColumn_index()));
            userToken.setName(rs.getString(TableAttributes.NAME.getColumn_index()));
            userToken.setLastname(rs.getString(TableAttributes.LASTNAME.getColumn_index()));
            userToken.setPassword(rs.getString(TableAttributes.PASSWORD.getColumn_index()));
            userToken.setRequestType(rs.getString(TableAttributes.REQUEST_TYPE.getColumn_index()));
            userToken.setToken(rs.getObject(TableAttributes.TOKEN.getColumn_index(), UUID.class));
            userToken.setGenTime(rs.getObject(TableAttributes.GEN_TIME.getColumn_index(), Timestamp.class).toLocalDateTime());
            userToken.setExpiryTime(rs.getObject(TableAttributes.EXPIRY_TIME.getColumn_index(), Timestamp.class).toLocalDateTime());
        }
        rs.close();
        statement.close();
        return userToken;
    }

    @Override
    public void create(UserToken userToken) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(insert);
        statement.setString(1, userToken.getUserID());
        statement.setString(2, userToken.getEmail());
        statement.setString(3, userToken.getName());
        statement.setString(4, userToken.getLastname());
        statement.setString(5, userToken.getPassword());
        statement.setString(6, userToken.getRequestType());
        statement.setObject(7, userToken.getToken());
        statement.executeUpdate();
        statement.close();
    }

    @Override
    public void update(String userID, String requestType, TableAttributes[] attributes, Object[] values) throws SQLException {
        StringBuilder query = new StringBuilder("UPDATE USERTOKEN SET ");
        for (int i = 0; i < attributes.length; i++) {
            query.append(attributes[i].name()).append("=");
            if (i < values.length - 1) {
                query.append((String) values[i]).append(", ");
            } else {
                query.append((String) values[i]).append(" ");
            }
        }
        query.append("WHERE " + TableAttributes.USERID + "='").append(userID).append("' AND ").append(TableAttributes.REQUEST_TYPE).append("=").append(requestType);
        PreparedStatement statement = connection.prepareStatement(query.toString());
        statement.executeUpdate();
        statement.close();
    }

    @Override
    public void delete(String userID, String requestType) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(delete);
        statement.setString(1, userID);
        statement.setString(2, requestType);
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

    private String createInsertQuery() {
        StringBuilder query = new StringBuilder("INSERT INTO USERTOKEN(");
        List<TableAttributes> fields = Arrays.stream(TableAttributes.values())
                .filter(value -> !value.equals(TableAttributes.GEN_TIME) & !value.equals(TableAttributes.EXPIRY_TIME))
                .collect(Collectors.toUnmodifiableList());
        for (int i = 0; i < fields.size(); i++) {
            if (i < fields.size() - 1) {
                query.append(fields.get(i)).append(", ");
            } else {
                query.append(fields.get(i));
            }
        }
        query.append(") VALUES(");
        for (int i = 0; i < fields.size(); i++) {
            if (i < fields.size() - 1) {
                query.append("?, ");
            } else {
                query.append("?)");
            }
        }
        return query.toString();
    }
}
