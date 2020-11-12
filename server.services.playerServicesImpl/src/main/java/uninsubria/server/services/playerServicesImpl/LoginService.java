package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.database.TransactionManager;
import uninsubria.server.services.api.Service;
import uninsubria.server.services.playerServicesTypes.PlayerServiceType;
import uninsubria.utils.business.Player;
import uninsubria.utils.serviceResults.ErrorMsgType;
import uninsubria.utils.serviceResults.Result;
import uninsubria.utils.serviceResults.ServiceResult;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import java.sql.SQLException;

public class LoginService implements Service {
	
	private final PlayerServiceType serviceType = PlayerServiceType.LOGIN;
	private String userid;
	private String pw;

	public LoginService(String id, String password) {
		this.userid = id;
		this.pw = password;
	}

	public PlayerServiceType getServiceType() {
		return serviceType ;
	}

	@Override
	public ServiceResultInterface execute() {
		ServiceResultInterface sr = new ServiceResult("LOGIN");
		Result<Player> player;
		Result<ErrorMsgType> error;
		TransactionManager tm = null;
		try {
			tm = new TransactionManager();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		/* Check user exists in DB */
		Player user = tm.lookupUser(userid);
		if (user == null) {
			player = new Result<>("Player", null);
			error = new Result<>("Error", ErrorMsgType.LOGIN_ERR_NOMATCH);
			sr.addResult(player);
			sr.addResult(error);
			return sr;
		}
		/* Check if the pw is correct */
		boolean pwOk = false;
//		boolean pwOk = SCryptUtil.check(pw, user.getPassword());
		if (!pwOk) {
			player = new Result<>("Player", null);
			error = new Result<>("Error", ErrorMsgType.LOGIN_ERR_PW);
			sr.addResult(player);
			sr.addResult(error);
			return sr;
		}
		/* Check if user is not already logged in - da rivedere, non credo sia appropriato metterlo qui il check, va fatto dal db */
		if (user.isLogStatus()) {
			player = new Result<>("Player", null);
			error = new Result<>("Error", ErrorMsgType.LOGIN_ERR_USER_ONLINE);
			sr.addResult(player);
			sr.addResult(error);
			return sr;
		}
		tm.loginPlayer(user); //questo dovrebbe lanciare un'eccezione se log status è già true
		player = new Result<>("Player", user);
		error = new Result<>("Error", null);
		sr.addResult(player);
		sr.addResult(error);
		return sr;
	}
	
	
}
