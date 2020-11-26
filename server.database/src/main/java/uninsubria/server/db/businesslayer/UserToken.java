package uninsubria.server.db.businesslayer;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a mapping of a tuple in the UserToken table of the database.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class UserToken {
    /*---Fields---*/
    private String userID;
    private String email;
    private String name;
    private String lastname;
    private String password;
    private String requestType;
    private UUID token;
    private LocalDateTime genTime;
    private LocalDateTime expiryTime;

    /*---Constructors---*/
    public UserToken() {
    }

    public UserToken(String userID, String email, String name, String lastname, String password, String requestType, UUID token) {
        this.userID = userID;
        this.email = email;
        this.name = name;
        this.lastname = lastname;
        this.password = password;
        this.requestType = requestType;
        this.token = token;
    }

    public UserToken(String userID, String email, String requestType, UUID token) {
        this.userID = userID;
        this.email = email;
        this.requestType = requestType;
        this.token = token;
    }

    /*---Methods---*/
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public LocalDateTime getGenTime() {
        return genTime;
    }

    public void setGenTime(LocalDateTime genTime) {
        this.genTime = genTime;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }
}
