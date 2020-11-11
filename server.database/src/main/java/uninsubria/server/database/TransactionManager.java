/**
 * 
 */
package uninsubria.server.database;

import uninsubria.utils.business.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author Alessandro
 *
 */
public class TransactionManager {

	private Connection con;
	private PlayerDAOImpl playerDAO;
	private GameRuleDAOImpl gameruleDAO;
	private GameDAOImpl gameDAO;
	private ConnectionPoolImpl connectionP;
	
	
	public ResultSet fetchStatistics(StatisticPreset statPreset) {
		ResultSet rs = null;
		try {
		PreparedStatement statement = con.prepareStatement(statPreset.getQuery());
		rs = statement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	public ResultSet fetchStatistics(StatisticPreset statPreset, String arg) {
		ResultSet rs = null;
		try {
			PreparedStatement statement = con.prepareStatement(statPreset.getQuery());
			statement.setString(1, arg);
			rs = statement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	/**
	 * Inserisce una partita nel database
	 */
	public void archiveGame(GAMEInfo gameInf) {
		try {
			connectionP.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		gameruleDAO.update();
		gameDAO.update(gameruleDAO.getGr(), gameInf);
				
		try {
			connectionP.releaseConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Player registerPlayer() {
		Player player = null;
		try {
			connectionP.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		playerDAO.update();
		
			try {
				connectionP.releaseConnection();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return player;
	}
	/**
	 * Login, verifica che il player esista(sia registrato) e cambia lo status in online
	 */
	public Player loginPlayer(Player player) {
		try {
			connectionP.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(playerDAO.getByUserId(player.getPlayerID())!=null);
			playerDAO.modifyLogin();
		
		try {
			connectionP.releaseConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		
		return player;
	}
	/**
	 * Logoutin, verifica che il player esista(sia registrato) e cambia lo status in online
	 */
	public Player logoutPlayer(Player player) {
		try {
			connectionP.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(playerDAO.getByUserId(player.getPlayerID())!=null);
			playerDAO.modifyLogout();
		
		try {
			connectionP.releaseConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		
		return player;
	}

	/**
	 * Lookup user player given either userID or email.
	 * This is used in login phase to check if a user exists in the database.
	 *
	 * @param id userID or email
	 * @return a Player object or null
	 * @author Giulia Pais
	 */
	public Player lookupUser(String id) {
		boolean email = id.contains("@");
		Player user = null;
		if (email) {
			user = playerDAO.getByEmail(id);
		} else {
			user = playerDAO.getByUserId(id);
		}
		return user;
	}
}
