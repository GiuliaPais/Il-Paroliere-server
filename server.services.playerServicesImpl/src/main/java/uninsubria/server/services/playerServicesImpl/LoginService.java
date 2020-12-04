package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.db.api.TransactionManager;
import uninsubria.server.services.api.Service;
import uninsubria.server.services.playerServicesTypes.PlayerServiceType;
import uninsubria.utils.business.Player;
import uninsubria.utils.serviceResults.ErrorMsgType;
import uninsubria.utils.serviceResults.Result;
import uninsubria.utils.serviceResults.ServiceResult;
import uninsubria.utils.serviceResults.ServiceResultInterface;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the service that manages the user login.
 *
 * @author Giulia Pais
 * @version 0.9.2
 */
public class LoginService implements Service {
	
	private final PlayerServiceType serviceType = PlayerServiceType.LOGIN;
	private String userid;
	private String pw;
	private List<ErrorMsgType> errorList;

	public LoginService(String id, String password) {
		this.userid = id;
		this.pw = password;
		this.errorList = new ArrayList<>();
	}

	public PlayerServiceType getServiceType() {
		return serviceType ;
	}

	@Override
	public ServiceResultInterface execute() {
		ServiceResultInterface sr = new ServiceResult("LOGIN");
		Player player;
		TransactionManager tm = new TransactionManager();
		player = tm.loginPlayer(userid, pw, errorList);
		Result<Player> pres = new Result<>("Profile", player);
		if (errorList.isEmpty()) {
			sr.addResult(pres);
			return sr;
		}
		ErrorMsgType[] errArr = new ErrorMsgType[errorList.size()];
		Result<ErrorMsgType[]> errors = new Result<>("Errors", errorList.toArray(errArr));
		sr.addResult(pres);
		sr.addResult(errors);
		return sr;
	}
	
	
}
