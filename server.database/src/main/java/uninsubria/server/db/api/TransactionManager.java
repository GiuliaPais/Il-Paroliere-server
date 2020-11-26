/**
 * 
 */
package uninsubria.server.db.api;

import uninsubria.server.db.businesslayer.UserToken;
import uninsubria.server.db.dao.*;
import uninsubria.server.email.EmailManager;
import uninsubria.utils.business.Player;
import uninsubria.utils.serviceResults.ErrorMsgType;

import javax.mail.MessagingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;


/**
 * Entry point of database module, this class is responsible for all database functionality and offers
 * the API that can be used by other modules.
 *
 * @author Alessandro Lerro
 * @author Giulia Pais
 * @version 0.9.1
 */
public class TransactionManager {
	/*---Fields---*/
	private PlayerDAO playerDAO;
	private GameInfoDAO gameInfoDAO;
	private GameEntryDAO gameEntryDAO;
	private UserTokenDAO userTokenDAO;

	/**
	 * Instantiates a new Transaction manager.
	 */
	/*---Constructors---*/
	public TransactionManager() {
		this.playerDAO = new PlayerDAOImpl();
		this.gameInfoDAO = new GameInfoDAOImpl();
		this.gameEntryDAO = new GameEntryDAOImpl();
		this.userTokenDAO = new UserTokenDAOImpl();
	}

	/*---Methods---*/
	/**
	 * Obtain activation code for the user registration procedure.
	 *
	 * @param userID    the user id
	 * @param name      the name
	 * @param lastname  the lastname
	 * @param email     the email
	 * @param password  the password
	 * @param errorCode list to save possible errors
	 */
	public void obtainActivationCode(String userID, String name, String lastname, String email, String password,
									 List<ErrorMsgType> errorCode) {
		/* Obtain a connection first */
		Connection connection = null;
		try {
			connection = ConnectionPool.getConnection();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			return;
		} catch (InterruptedException e) {
			e.printStackTrace();
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			return;
		}
		/* Begin transaction */
		try {
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement("LOCK PLAYER, USERTOKEN IN ROW EXCLUSIVE MODE");
			statement.executeUpdate();
			playerDAO.setConnection(connection);
			Player pbyid = playerDAO.getByUserId(userID);
			if (pbyid != null) {
				errorCode.add(ErrorMsgType.REG_ERR_USERID);
				connection.rollback();
				resetConnections(connection);
				return;
			}
			Player pbymail = playerDAO.getByEmail(email);
			if (pbymail != null) {
				errorCode.add(ErrorMsgType.REG_ERR_EMAIL);
				connection.rollback();
				resetConnections(connection);
				return;
			}
		} catch (SQLException throwables) {
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			throwables.printStackTrace();
			resetConnections(connection);
			return;
		}
		UserToken userToken = new UserToken(userID, email, name, lastname, password,
				"ACTIVATION", UUID.randomUUID());
		userTokenDAO.setConnection(connection);
		try {
			if (userTokenDAO.getByPk(userID, "ACTIVATION") != null) {
				errorCode.add(ErrorMsgType.REG_DUPL_REQUEST);
				connection.rollback();
				return;
			}
			userTokenDAO.create(userToken);
			connection.commit();
		} catch (SQLException throwables) {
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			throwables.printStackTrace();
			return;
		} finally {
			resetConnections(connection);
		}
		/* If transaction committed send email */
		EmailManager emailManager = new EmailManager();
		try {
			emailManager.sendActivationCode(email, userToken.getToken());
		} catch (MessagingException e) {
			errorCode.add(ErrorMsgType.REG_EMAIL_FAILURE);
		}
	}

	public void confirmActivationCode() {
		//TODO
	}

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

	private void resetConnections(Connection connection) {
		playerDAO.setConnection(null);
		gameEntryDAO.setConnection(null);
		gameInfoDAO.setConnection(null);
		userTokenDAO.setConnection(null);
		try {
			connection.setAutoCommit(true);
		} catch (SQLException throwables) {
			ConnectionPool.regenConnection(connection);
			return;
		}
		ConnectionPool.releaseConnection(connection);
	}
}
