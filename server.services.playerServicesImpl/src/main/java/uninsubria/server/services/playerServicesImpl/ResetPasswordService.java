package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.db.api.TransactionManager;
import uninsubria.server.services.api.Service;
import uninsubria.utils.serviceResults.ErrorMsgType;
import uninsubria.utils.serviceResults.Result;
import uninsubria.utils.serviceResults.ServiceResult;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of service responsible for password reset.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class ResetPasswordService implements Service {
    /*---Fields---*/
    private final String email;
    private final UUID generatedPW;
    private final List<ErrorMsgType> errorList;

    /*---Constructors---*/
    /**
     * Instantiates a new Reset password service.
     *
     * @param email the email
     */
    public ResetPasswordService(String email, UUID generated) {
        this.email = email;
        this.generatedPW = generated;
        this.errorList = new ArrayList<>();
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.resetPassword(email, generatedPW, errorList);
        ServiceResult serviceResult = new ServiceResult("PASSWORD RESET");
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
