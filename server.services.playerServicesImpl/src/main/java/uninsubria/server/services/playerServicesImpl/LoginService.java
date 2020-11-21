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
 * @version 0.9.1
 */
public class LoginService implements Service {
	
	private final PlayerServiceType serviceType = PlayerServiceType.LOGIN;
	private String userid;
	private String pw;
	private List<Byte> errorList;

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
		TransactionManager tm = new TransactionManager(); // valutare se mettere unico tm per player
		player = tm.loginPlayer(userid, pw, this.errorList);
		Result<Player> pres = new Result<>("Player", player);
		sr.addResult(pres);
		if (player == null) {
			int errorN = 1;
			for (Byte code : errorList) {
				Result<ErrorMsgType> error;
				switch(code) {
					case 1:
						error = new Result<>("Error" + errorN, ErrorMsgType.GENERIC_DB_ERROR);
						sr.addResult(error);
						break;
					case 2:
						error = new Result<>("Error" + errorN, ErrorMsgType.LOGIN_ERR_NOMATCH);
						sr.addResult(error);
						break;
					case 3:
						error = new Result<>("Error" + errorN, ErrorMsgType.LOGIN_ERR_PW);
						sr.addResult(error);
						break;
					case 4:
						error = new Result<>("Error" + errorN, ErrorMsgType.LOGIN_ERR_USER_ONLINE);
						sr.addResult(error);
						break;
				}
			}
		}
		return sr;
	}
	
	
}
