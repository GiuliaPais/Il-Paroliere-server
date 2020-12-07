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
 * Implementation of Service responsible for resending a code via email on user request.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class ResendCodeService implements Service {
    /*---Fields---*/
    private final String email;
    private final String requestType;
    private final List<ErrorMsgType> errorList;

    /*---Constructors---*/
    /**
     * Instantiates a new Resend code service.
     *
     * @param email       the email
     * @param requestType the request type
     */
    public ResendCodeService(String email, String requestType) {
        this.email = email;
        this.requestType = requestType;
        this.errorList = new ArrayList<>();
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.resendCode(email, requestType, errorList);
        ServiceResultInterface serviceResult = new ServiceResult("RESEND CODE");
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
