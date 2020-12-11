package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.db.api.TransactionManager;
import uninsubria.server.services.api.Service;
import uninsubria.utils.serviceResults.ErrorMsgType;
import uninsubria.utils.serviceResults.Result;
import uninsubria.utils.serviceResults.ServiceResult;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of service that manages profile deletion for a user.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class DeleteProfileService implements Service {
    /*---Fields---*/
    private final String identifier;
    private final String password;
    private final List<ErrorMsgType> errorList;

    /*---Constructors---*/
    /**
     * Instantiates a new Delete profile service.
     *
     * @param identifier the identifier
     * @param password   the password
     */
    public DeleteProfileService(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
        this.errorList = new ArrayList<>();
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.deletePlayer(identifier, password, errorList);
        ServiceResult serviceResult = new ServiceResult("DELETE PROFILE");
        if (errorList.isEmpty()) {
            Result<Boolean> result = new Result<>("Success", true);
            serviceResult.addResult(result);
            return serviceResult;
        }
        Result<Boolean> result = new Result<>("Success", false);
        ErrorMsgType[] errArr = new ErrorMsgType[errorList.size()];
        Result<ErrorMsgType[]> errors = new Result<>("Errors", errorList.toArray(errArr));
        serviceResult.addResult(result);
        serviceResult.addResult(errors);
        return serviceResult;
    }
}
