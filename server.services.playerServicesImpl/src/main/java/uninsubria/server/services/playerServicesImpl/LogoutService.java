package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.db.api.TransactionManager;
import uninsubria.server.services.api.Service;
import uninsubria.utils.serviceResults.ServiceResultInterface;

/**
 * Service responsible for player logout.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class LogoutService implements Service {
    /*---Fields---*/
    private final String userID;

    /*---Constructors---*/
    /**
     * Instantiates a new Logout service.
     *
     * @param userID the user id
     */
    public LogoutService(String userID) {
        this.userID = userID;
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        TransactionManager tm = new TransactionManager();
        tm.logoutPlayer(userID);
        return null;
    }
}
