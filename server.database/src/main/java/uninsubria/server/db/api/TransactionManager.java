/**
 * 
 */
package uninsubria.server.db.api;

import uninsubria.server.db.dao.*;
import uninsubria.utils.business.Player;

import java.sql.SQLException;
import java.util.List;


/**
 * Entry point of database module, this class is responsible for all database functionality and offers
 * the API that can be used by other modules.
 *
 * @author Alessandro Lerro
 * @author Giulia Pais
 */
public class TransactionManager {
	/*---Fields---*/
	private PlayerDAO playerDAO;
	private GameInfoDAO gameInfoDAO;
	private GameEntryDAO gameEntryDAO;

	/**
	 * Instantiates a new Transaction manager.
	 */
	/*---Constructors---*/
	public TransactionManager() {
		this.playerDAO = new PlayerDAOImpl();
		this.gameInfoDAO = new GameInfoDAOImpl();
		this.gameEntryDAO = new GameEntryDAOImpl();
	}

	/*---Methods---*/
	/**
	 * Manages the player login.
	 *
	 * @param identifier the identifier, either the userID or the email
	 * @param password   the password
	 * @param errorCode  a list where the function can add error codes
	 * @return A player object or null
	 */
	public Player loginPlayer(String identifier, String password, List<Byte> errorCode) {
		Player player;
		try {
			playerDAO.setConnection(ConnectionPool.getConnection());
			playerDAO.setAutocommit(false);
			boolean email = identifier.contains("@");
			if (email) {
				player = playerDAO.getByEmailForUpdate(identifier);
			} else {
				player = playerDAO.getByUserIdForUpdate(identifier);
			}
			if (player == null) {
				errorCode.add((byte) 2);
				playerDAO.getConnection().rollback();
				playerDAO.setAutocommit(true);
				return null;
			}
			if (!player.getPassword().equals(password)) {
				errorCode.add((byte) 3);
				playerDAO.getConnection().rollback();
				playerDAO.setAutocommit(true);
				return null;
			}
			if (player.isLogStatus()) {
				errorCode.add((byte) 4);
				playerDAO.getConnection().rollback();
				playerDAO.setAutocommit(true);
				return null;
			}
			playerDAO.update(player.getPlayerID(),
					new PlayerDAO.TableAttributes[] {PlayerDAO.TableAttributes.Log_Status},
					new Boolean[] {Boolean.valueOf(true)});
			playerDAO.getConnection().commit();
			playerDAO.setAutocommit(true);
			player.setLogStatus(true);
			return player;
		} catch (SQLException | InterruptedException e) {
			errorCode.add((byte) 1);
//			e.printStackTrace();
		} finally {
			if (playerDAO.getConnection() != null) {
				ConnectionPool.releaseConnection(playerDAO.getConnection());
			}
		}
		return null;
	}

	/**
	 * Manages player logout.
	 *
	 * @param id The userID
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public void logoutPlayer(String id) throws SQLException, InterruptedException {
		playerDAO.setConnection(ConnectionPool.getConnection());
		playerDAO.update(id, new PlayerDAO.TableAttributes[] {PlayerDAO.TableAttributes.Log_Status},
				new Boolean[] {Boolean.valueOf(false)});
		if (playerDAO.getConnection() != null) {
			ConnectionPool.releaseConnection(playerDAO.getConnection());
		}
	}

	/**
	 * Manages player registration.
	 *
	 * @param userId   the user id
	 * @param email    the email
	 * @param password the password
	 * @param name     the name
	 * @param surname  the surname
	 * @return the player
	 */
	public Player registerPlayer(String userId, String email, String password, String name, String surname) {
		//TODO
		return null;
	}

}
