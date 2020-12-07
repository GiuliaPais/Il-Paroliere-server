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
 * Implementation of service responsible for password change.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class ChangePwService implements Service {
    /*---Fields---*/
    private final String email;
    private final String oldPw;
    private final String newPw;
    private final List<ErrorMsgType> errorList;

    /*---Constructors---*/
    /**
     * Instantiates a new Change pw service.
     *
     * @param email the email
     * @param oldPw the old pw
     * @param newPw the new pw
     */
    public ChangePwService(String email, String oldPw, String newPw) {
        this.email = email;
        this.oldPw = oldPw;
        this.newPw = newPw;
        this.errorList = new ArrayList<>();
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        TransactionManager transactionManager = new TransactionManager();
        boolean changed = transactionManager.changePassword(email, oldPw, newPw, errorList);
        ServiceResultInterface serviceResult = new ServiceResult("CHANGE PASSWORD");
        Result<Boolean> changedRes = new Result<>("Changed", changed);
        serviceResult.addResult(changedRes);
        if (changed) {
            return serviceResult;
        }
        ErrorMsgType[] errArr = new ErrorMsgType[errorList.size()];
        Result<ErrorMsgType[]> errors = new Result<>("Errors", errorList.toArray(errArr));
        serviceResult.addResult(errors);
        return serviceResult;
    }
}
