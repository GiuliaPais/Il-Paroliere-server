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
 * Implementation of Service responsible for obtaining an activation code during registration procedure.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class ActivationCodeService implements Service {
    /*---Fields---*/
    private List<ErrorMsgType> errorList;
    private String userID, email, name, lastname, password;

    /*---Constructors---*/
    /**
     * Instantiates a new Activation code service.
     *
     * @param userID   the user id
     * @param email    the email
     * @param name     the name
     * @param lastname the lastname
     * @param password the password
     */
    public ActivationCodeService(String userID, String email, String name, String lastname, String password) {
        this.errorList = new ArrayList<>();
        this.userID = userID;
        this.email = email;
        this.name = name;
        this.lastname = lastname;
        this.password = password;
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.obtainActivationCode(userID, name, lastname, email, password, errorList);
        ServiceResult serviceResult = new ServiceResult("ACTIVATION CODE REQUEST");
        if (errorList.isEmpty()) {
            Result<Boolean> result = new Result<>("Success", true);
            serviceResult.addResult(result);
            return serviceResult;
        }
        Result<Boolean> result = new Result<>("Success", false);
        ErrorMsgType[] errArr = new ErrorMsgType[errorList.size()];
        Result<ErrorMsgType[]> errors = new Result<ErrorMsgType[]>("Errors", errorList.toArray(errArr));
        serviceResult.addResult(result);
        serviceResult.addResult(errors);
        return serviceResult;
    }
}
