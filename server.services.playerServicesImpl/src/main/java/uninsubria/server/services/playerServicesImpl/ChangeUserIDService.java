package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.db.api.TransactionManager;
import uninsubria.server.services.api.Service;
import uninsubria.utils.business.Player;
import uninsubria.utils.serviceResults.ErrorMsgType;
import uninsubria.utils.serviceResults.Result;
import uninsubria.utils.serviceResults.ServiceResult;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a service responsible for the change userID procedure.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class ChangeUserIDService implements Service {
    /*---Fields---*/
    private final String oldUser;
    private final String newUser;
    private final List<ErrorMsgType> errorList;

    /*---Constructors---*/
    /**
     * Instantiates a new Change user id service.
     *
     * @param oldUser the old userID
     * @param newUser the new userID
     */
    public ChangeUserIDService(String oldUser, String newUser) {
        this.oldUser = oldUser;
        this.newUser = newUser;
        this.errorList = new ArrayList<>();
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        TransactionManager transactionManager = new TransactionManager();
        Player player = transactionManager.changeUserID(oldUser, newUser, errorList);
        ServiceResultInterface serviceResult = new ServiceResult("CHANGE USER ID");
        Result<Player> playerResult;
        if (player != null && errorList.isEmpty()) {
            playerResult = new Result<>("Profile", player);
            serviceResult.addResult(playerResult);
            return serviceResult;
        }
        playerResult = new Result<>("Profile", null);
        ErrorMsgType[] errArr = new ErrorMsgType[errorList.size()];
        Result<ErrorMsgType[]> errors = new Result<>("Errors", errorList.toArray(errArr));
        serviceResult.addResult(playerResult);
        serviceResult.addResult(errors);
        return serviceResult;
    }
}
