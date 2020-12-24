package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.db.api.TransactionManager;
import uninsubria.server.services.api.Service;
import uninsubria.utils.serviceResults.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a service that fetches statistics from the database.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class FetchStatsService implements Service {
    /*---Fields---*/
    private final List<ErrorMsgType> errorList;

    /*---Constructors---*/
    /**
     * Instantiates a new Fetch stats service.
     */
    public FetchStatsService() {
        this.errorList = new ArrayList<>();
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        TransactionManager tm = new TransactionManager();
        ServiceResultAggregate res = tm.fetchStatistics(errorList);
        if (res == null) {
            ServiceResult serviceResult = new ServiceResult("STATISTICS");
            ErrorMsgType[] errArr = new ErrorMsgType[errorList.size()];
            Result<ErrorMsgType[]> errors = new Result<>("Errors", errorList.toArray(errArr));
            serviceResult.addResult(errors);
            return serviceResult;
        } else {
            return res;
        }
    }
}
