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
 * Entry point of database module, this class is responsible for all database functionality and offers
 * the API that can be used by other modules.
 * @author Alessandro Lerro
 * @author Giulia Pais
 */
public class TransactionManager {
	/*
	* Modifiche di Giulia:
	* - Aggiunto un metodo per controllare l'esistenza di un giocatore nel db (utile per servizi di login)
	* - Aggiunto costruttore vuoto e commentato via l'altro (dove viene usato?)
	* */
	private Connection con;
	private PlayerDAOImpl playerDAO;
	private GameRuleDAOImpl gameruleDAO;
	private GameDAOImpl gameDAO;
	
//	/**
//	 * costruttore
//	 * @param con
//	 * @param playerDAO
//	 * @param gameruleDAO
//	 * @param gameDAO
//	 * @param connectionP
//	 */
//	private TransactionManager(Connection con, PlayerDAOImpl playerDAO, GameRuleDAOImpl gameruleDAO, GameDAOImpl gameDAO,
//			ConnectionPoolImpl connectionP) {
//		super();
//		this.con = con;
//		this.playerDAO = playerDAO;
//		this.gameruleDAO = gameruleDAO;
//		this.gameDAO = gameDAO;
//		this.connectionP = connectionP;
//	}

	public TransactionManager() throws SQLException {
		this.con = ConnectionPoolImpl.getInstance().getConnection();
		this.playerDAO = new PlayerDAOImpl();
		this.gameruleDAO = new GameRuleDAOImpl();
		this.gameDAO = new GameDAOImpl();
	}
	
	/**
	 * Preleva le statistiche richieste(passate per argomento)
	 * @param statPreset
	 */
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
	
	/**
	 * Preleva le statistiche richieste(passate per argomento)
	 * @param statPreset
	 * @param arg
	 */
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
//		try {
//			connectionP.getConnection();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		gameruleDAO.create();
		gameDAO.create(gameDAO.getGameRule(),gameDAO.getTotalgame());
				
//		try {
//			connectionP.releaseConnection();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	/**
	 * Registra un nuovo giocatore
	 */
	public Player registerPlayer() {
		Player player = null;
//		try {
//			connectionP.getConnection();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if(playerDAO.getByUserId(player.getPlayerID())!=null);
			playerDAO.create();
		
//			try {
//				connectionP.releaseConnection();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		return player;
	}
	/**
	 * Login, verifica che il player esista(sia registrato) e cambia lo status in online
	 */
	public Player loginPlayer(Player player) {
//		try {
//			connectionP.getConnection();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if(playerDAO.getByUserId(player.getPlayerID())!=null);
			playerDAO.modifyLogin();
		
//		try {
//			connectionP.releaseConnection();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			}
//
		return player;
	}

	/**
	 * Logout, verifica che il player esista(sia registrato) e cambia lo status in online
	 */
	public Player logoutPlayer(Player player) {
//		try {
//			connectionP.getConnection();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if(playerDAO.getByUserId(player.getPlayerID())!=null);
			playerDAO.modifyLogout();
		
//		try {
//			connectionP.releaseConnection();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			}
//
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
