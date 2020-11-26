package uninsubria.server.db.dao;

import uninsubria.server.db.businesslayer.UserToken;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Class implementing the DAO (Data Access Object) for the "UserToken" table in the database.
 * @author Giulia Pais
 * @version 0.9.0
 */
public class UserTokenDAOImpl implements UserTokenDAO {
    /*---Fields---*/
    private Connection connection;

    private final String insert = createInsertQuery();
    private final String delete = "DELETE * FROM USERTOKEN WHERE " + TableAttributes.USERID + "=? AND " +
                                    TableAttributes.REQUEST_TYPE + "=?";
    private final String getByPk = "SELECT * FROM USERTOKEN WHERE " + TableAttributes.USERID + "=? AND " +
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
        String query = "UPDATE USERTOKEN SET ";
        for (int i = 0; i < attributes.length; i++) {
            query += (attributes[i].name() + "=");
            switch (attributes[i]) {
                case USERID:
                case EMAIL:
                case NAME:
                case LASTNAME:
                case PASSWORD:
                case REQUEST_TYPE:
                    if (i < values.length-1) {
                        query += ((String) values[i] + ", ");
                    } else {
                        query += ((String) values[i] + " ");
                    }
                    break;
                case TOKEN:
                    if (i < values.length-1) {
                        query += ((UUID) values[i] + ", ");
                    } else {
                        query += ((UUID) values[i] + " ");
                    }
                    break;
                case GEN_TIME:
                case EXPIRY_TIME:
                    if (i < values.length-1) {
                        query += ((LocalDateTime) values[i] + ", ");
                    } else {
                        query += ((LocalDateTime) values[i] + " ");
                    }
                    break;
            }
            query += "WHERE " + TableAttributes.USERID + "='" + userID + "' AND " + TableAttributes.REQUEST_TYPE +
            "=" + requestType;
        }
        PreparedStatement statement = connection.prepareStatement(query);
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
        String query = "INSERT INTO USERTOKEN(";
        List<TableAttributes> fields = Arrays.stream(TableAttributes.values())
                .filter(value -> !value.equals(TableAttributes.GEN_TIME) & !value.equals(TableAttributes.EXPIRY_TIME))
                .collect(Collectors.toUnmodifiableList());
        for (int i = 0; i < fields.size(); i++) {
            if (i < fields.size() - 1) {
                query += fields.get(i) + ", ";
            } else {
                query += fields.get(i);
            }
        }
        query += ") VALUES(";
        for (int i = 0; i < fields.size(); i++) {
            if (i < fields.size() - 1) {
                query += "?, ";
            } else {
                query += "?)";
            }
        }
        return query;
    }
}
