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
 * Implementation of Service responsible for confirming an activation code during registration procedure.
 * @author Giulia Pais
 * @version 0.9.0
 */
public class ConfirmActivationCodeService implements Service {
    /*---Fields---*/
    private final List<ErrorMsgType> errorList;
    private final String email;
    private final String code;

    /*---Constructors---*/
    /**
     * Instantiates a new Confirm activation code service.
     *
     * @param email the email
     * @param code  the code
     */
    public ConfirmActivationCodeService(String email, String code) {
        this.email = email;
        this.code = code;
        this.errorList = new ArrayList<>();
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        TransactionManager transactionManager = new TransactionManager();
        Player player = transactionManager.confirmActivationCode(email, code, errorList);
        ServiceResultInterface serviceResult = new ServiceResult("CONFIRM ACTIVATION CODE");
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
