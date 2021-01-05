package uninsubria.server.db.api;

import uninsubria.server.db.businesslayer.GameEntry;
import uninsubria.server.db.businesslayer.GameInfo;
import uninsubria.server.db.businesslayer.UserToken;
import uninsubria.server.db.dao.*;
import uninsubria.server.db.statistics.StatisticsManager;
import uninsubria.server.email.EmailManager;
import uninsubria.server.wrappers.GameEntriesWrapper;
import uninsubria.utils.business.Player;
import uninsubria.utils.business.Word;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;
import uninsubria.utils.security.PasswordEncryptor;
import uninsubria.utils.serviceResults.ErrorMsgType;
import uninsubria.utils.serviceResults.ServiceResultAggregate;
import uninsubria.utils.serviceResults.ServiceResultInterface;

import javax.mail.MessagingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Entry point of database module, this class is responsible for all database functionality and offers
 * the API that can be used by other modules.
 *
 * @author Alessandro Lerro
 * @author Giulia Pais
 * @version 0.9.5
 */
public class TransactionManager {
	/*---Fields---*/
	private final PlayerDAO playerDAO;
	private final GameInfoDAO gameInfoDAO;
	private final GameEntryDAO gameEntryDAO;
	private final UserTokenDAO userTokenDAO;
	private final StatisticsManager statisticsManager;

	/*---Constructors---*/
	/**
	 * Instantiates a new Transaction manager.
	 */
	public TransactionManager() {
		this.playerDAO = new PlayerDAOImpl();
		this.gameInfoDAO = new GameInfoDAOImpl();
		this.gameEntryDAO = new GameEntryDAOImpl();
		this.userTokenDAO = new UserTokenDAOImpl();
		this.statisticsManager = new StatisticsManager();
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
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (InterruptedException throwables) {
			throwables.printStackTrace();
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
			if (userTokenDAO.getByEmail(email, "ACTIVATION") != null) {
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

	/**
	 * Confirm activation code for the user registration procedure.
	 *
	 * @param email     the email
	 * @param code      the code
	 * @param errorCode a list where to add possible errors
	 */
	public Player confirmActivationCode(String email, String code, List<ErrorMsgType> errorCode) {
		/* Obtain a connection first */
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (InterruptedException throwables) {
			throwables.printStackTrace();
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			return null;
		}
		/* Begin transaction */
		try {
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement("LOCK PLAYER, USERTOKEN IN ROW EXCLUSIVE MODE");
			statement.executeUpdate();
			userTokenDAO.setConnection(connection);
			UserToken userToken = userTokenDAO.getByEmail(email, "ACTIVATION");
			if (userToken == null) {
				errorCode.add(ErrorMsgType.REG_NO_REQUEST_FOUND);
				connection.rollback();
				resetConnections(connection);
				return null;
			}
			if (!userToken.getToken().equals(UUID.fromString(code))) {
				System.out.println("CODE: " + code);
				System.out.println("TOKEN: "+userToken.getToken());
				errorCode.add(ErrorMsgType.REG_CODE_WRONG);
				connection.rollback();
				resetConnections(connection);
				return null;
			}
			if (userToken.getExpiryTime().isBefore(LocalDateTime.now())) {
				errorCode.add(ErrorMsgType.REG_CODE_EXPIRED);
				userTokenDAO.delete(userToken.getUserID(), userToken.getRequestType());
				userToken.setExpiryTime(null);
				userToken.setGenTime(null);
				userToken.setToken(UUID.randomUUID());
				userTokenDAO.create(userToken);
				connection.commit();
				EmailManager emailManager = new EmailManager();
				try {
					emailManager.sendActivationCode(email, userToken.getToken());
				} catch (MessagingException e) {
					errorCode.add(ErrorMsgType.REG_EMAIL_FAILURE);
				}
				resetConnections(connection);
				return null;
			}
			playerDAO.setConnection(connection);
			Player player = new Player(userToken.getUserID(), userToken.getEmail(), userToken.getName(),
					userToken.getLastname(), userToken.getPassword(), 1, true, null, null);
			playerDAO.create(player);
			userTokenDAO.delete(userToken.getUserID(), userToken.getRequestType());
			player = playerDAO.getByUserId(player.getPlayerID());
			connection.commit();
			resetConnections(connection);
			return player;
		} catch (SQLException throwables) {
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			throwables.printStackTrace();
			resetConnections(connection);
			return null;
		}
	}

	/**
	 * Resends a code via email.
	 *
	 * @param email       the email
	 * @param requestType the request type
	 * @param errorCode   a list where to add possible errors
	 */
	public void resendCode(String email, String requestType,  List<ErrorMsgType> errorCode) {
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (InterruptedException throwables) {
			throwables.printStackTrace();
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			return;
		}
		try {
			userTokenDAO.setConnection(connection);
			UserToken userToken = userTokenDAO.getByEmail(email, requestType);
			if (userToken == null) {
				errorCode.add(ErrorMsgType.REG_NO_REQUEST_FOUND);
				resetConnections(connection);
				return;
			}
			if (userToken.getExpiryTime().isBefore(LocalDateTime.now())) {
				connection.setAutoCommit(false);
				PreparedStatement preparedStatement = connection.prepareStatement("LOCK USERTOKEN IN ROW EXCLUSIVE MODE");
				preparedStatement.executeUpdate();
				userTokenDAO.delete(userToken.getUserID(), userToken.getRequestType());
				userToken.setExpiryTime(null);
				userToken.setGenTime(null);
				userToken.setToken(UUID.randomUUID());
				userTokenDAO.create(userToken);
				connection.commit();
				resetConnections(connection);
			}
			EmailManager emailManager = new EmailManager();
			try {
				emailManager.sendActivationCode(email, userToken.getToken());
			} catch (MessagingException e) {
				errorCode.add(ErrorMsgType.REG_EMAIL_FAILURE);
			}
		} catch (SQLException throwables) {
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			resetConnections(connection);
		}
	}

	/**
	 * Manages the player login.
	 *
	 * @param identifier the identifier, either the userID or the email
	 * @param password   the password
	 * @param errorCode  a list where the function can add error codes
	 * @return A player object or null
	 */
	public Player loginPlayer(String identifier, String password, List<ErrorMsgType> errorCode) {
		Player player;
		Connection connection = null;
		try {
			connection = ConnectionPool.getConnection();
			playerDAO.setConnection(connection);
			playerDAO.setAutocommit(false);
			boolean email = identifier.contains("@");
			if (email) {
				player = playerDAO.getByEmailForUpdate(identifier);
			} else {
				player = playerDAO.getByUserIdForUpdate(identifier);
			}
			if (player == null) {
				errorCode.add(ErrorMsgType.LOGIN_ERR_NOMATCH);
				playerDAO.getConnection().rollback();
				resetConnections(connection);
				return null;
			}
			if (!player.getPassword().equals(password)) {
				errorCode.add(ErrorMsgType.LOGIN_ERR_PW);
				playerDAO.getConnection().rollback();
				resetConnections(connection);
				return null;
			}
			if (player.isLogStatus()) {
				errorCode.add(ErrorMsgType.LOGIN_ERR_USER_ONLINE);
				playerDAO.getConnection().rollback();
				resetConnections(connection);
				return null;
			}
			playerDAO.update(player.getPlayerID(),
					new PlayerDAO.TableAttributes[] {PlayerDAO.TableAttributes.Log_Status},
					new Boolean[] {Boolean.TRUE});
			playerDAO.getConnection().commit();
			resetConnections(connection);
			player.setLogStatus(true);
			return player;
		} catch (SQLException | InterruptedException e) {
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			if (connection != null) {
				resetConnections(connection);

			}
		}
		return null;
	}

	/**
	 * Manages player logout.
	 *
	 * @param id The userID
	 */
	public void logoutPlayer(String id) {
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (InterruptedException e) {
			return;
		}
		playerDAO.setConnection(connection);
		try {
			playerDAO.update(id, new PlayerDAO.TableAttributes[] {PlayerDAO.TableAttributes.Log_Status},
					new Boolean[] {Boolean.FALSE});
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			resetConnections(connection);
		}
	}

	/**
	 * Logouts out every player in the player table.
	 */
	public void logoutEveryone() {
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (InterruptedException e) {
			return;
		}
		playerDAO.setConnection(connection);
		try {
			playerDAO.updateAll(new PlayerDAO.TableAttributes[] {PlayerDAO.TableAttributes.Log_Status}, new Boolean[] {Boolean.FALSE});
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			resetConnections(connection);
		}
	}

	/**
	 * Updates the player info (only non-critical attributes).
	 *
	 * @param player the player
	 */
	public void updatePlayerInfo(Player player) {
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (InterruptedException e) {
			return;
		}
		playerDAO.setConnection(connection);
		PlayerDAO.TableAttributes[] attributes = new PlayerDAO.TableAttributes[] {
				PlayerDAO.TableAttributes.Name,
				PlayerDAO.TableAttributes.Surname,
				PlayerDAO.TableAttributes.ProfileImage,
				PlayerDAO.TableAttributes.ImageColor,
				PlayerDAO.TableAttributes.BgColor
		};
		Object[] values = new Object[] {
				player.getName(),
				player.getSurname(),
				player.getProfileImage(),
				player.getImgColor(),
				player.getBgColor()
		};
		try {
			playerDAO.update(player.getPlayerID(), attributes, values);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			resetConnections(connection);
		}
	}

	/**
	 * Changes the userID for a player.
	 *
	 * @param oldId     the old id
	 * @param newId     the new id
	 * @param errorCode a list where the function can add error codes
	 * @return A player object if the procedure succeeds, null otherwise
	 */
	public Player changeUserID(String oldId, String newId, List<ErrorMsgType> errorCode) {
		Connection connection;
		Player player;
		try {
			connection = ConnectionPool.getConnection();
		} catch (InterruptedException e) {
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			return null;
		}
		try {
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement("LOCK PLAYER IN ROW EXCLUSIVE MODE");
			statement.executeUpdate();
			playerDAO.setConnection(connection);
			Player available = playerDAO.getByUserId(newId);
			if (available != null) {
				errorCode.add(ErrorMsgType.REG_ERR_USERID);
				connection.rollback();
				return null;
			}
			playerDAO.update(oldId, new PlayerDAO.TableAttributes[] {PlayerDAO.TableAttributes.UserID}, new Object[] {newId});
			player = playerDAO.getByUserId(newId);
			connection.commit();
			return player;
		} catch (SQLException throwables) {
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			return null;
		} finally {
			resetConnections(connection);
		}
	}

	/**
	 * Changes the password for a player.
	 *
	 * @param email     the email
	 * @param oldPw     the old pw
	 * @param newPw     the new pw
	 * @param errorCode a list where the function can add error codes
	 * @return true if the password was changed, false otherwise
	 */
	public boolean changePassword(String email, String oldPw, String newPw, List<ErrorMsgType> errorCode) {
		Connection connection;
		Player player;
		try {
			connection = ConnectionPool.getConnection();
		} catch (InterruptedException e) {
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			return false;
		}
		try {
			connection.setAutoCommit(false);
			playerDAO.setConnection(connection);
			player = playerDAO.getByEmailForUpdate(email);
			if (!player.getPassword().equals(oldPw)) {
				errorCode.add(ErrorMsgType.LOGIN_ERR_PW);
				connection.rollback();
				return false;
			}
			playerDAO.update(player.getPlayerID(), new PlayerDAO.TableAttributes[] {PlayerDAO.TableAttributes.Password},
					new Object[]{newPw});
			connection.commit();
			EmailManager emailManager = new EmailManager();
			emailManager.sendModNotification(email);
			return true;
		} catch (SQLException throwables) {
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			return false;
		} catch (MessagingException ignored) {
			return true;
		} finally {
			resetConnections(connection);
		}
	}

	/**
	 * Resets password for a user.
	 *
	 * @param email       the email
	 * @param generatedPw the generated pw
	 * @param errorCode   a list where the function can add error codes
	 */
	public void resetPassword(String email, UUID generatedPw, List<ErrorMsgType> errorCode) {
		Connection connection;
		Player player;
		try {
			connection = ConnectionPool.getConnection();
		} catch (InterruptedException e) {
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			return;
		}
		try {
			connection.setAutoCommit(false);
			playerDAO.setConnection(connection);
			player = playerDAO.getByEmailForUpdate(email);
			if (player == null) {
				errorCode.add(ErrorMsgType.LOGIN_ERR_NOMATCH);
				connection.rollback();
				return;
			}
			if (player.isLogStatus()) {
				errorCode.add(ErrorMsgType.LOGIN_ERR_USER_ONLINE);
				connection.rollback();
				return;
			}
			String hashedPw = PasswordEncryptor.hashPassword(generatedPw.toString());
			playerDAO.update(player.getPlayerID(), new PlayerDAO.TableAttributes[] {PlayerDAO.TableAttributes.Password},
					new Object[] {hashedPw});
			EmailManager emailManager = new EmailManager();
			emailManager.sendTemporaryPassword(email, generatedPw);
			connection.commit();
			return;
		} catch (SQLException throwables) {
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			return;
		} catch (MessagingException ignored) {
			errorCode.add(ErrorMsgType.REG_EMAIL_FAILURE);
			return;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			resetConnections(connection);
		}
	}

	/**
	 * Deletes a player.
	 *
	 * @param id        the id
	 * @param password  the password
	 * @param errorCode a list where the function can add error codes
	 */
	public void deletePlayer(String id, String password, List<ErrorMsgType> errorCode) {
		Connection connection;
		Player player;
		try {
			connection = ConnectionPool.getConnection();
		} catch (InterruptedException e) {
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			return;
		}
		try {
			connection.setAutoCommit(false);
			playerDAO.setConnection(connection);
			if (id.contains("@")) {
				player = playerDAO.getByEmail(id);
			} else {
				player = playerDAO.getByUserId(id);
			}
			if (player == null) {
				errorCode.add(ErrorMsgType.LOGIN_ERR_NOMATCH);
				connection.rollback();
				return;
			}
			if (player.isLogStatus()) {
				errorCode.add(ErrorMsgType.LOGIN_ERR_USER_ONLINE);
				connection.rollback();
				return;
			}
			if (!player.getPassword().equals(password)) {
				errorCode.add(ErrorMsgType.LOGIN_ERR_PW);
				connection.rollback();
				return;
			}
			playerDAO.delete(player.getPlayerID());
			connection.commit();
			return;
		} catch (SQLException throwables) {
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			return;
		} finally {
			resetConnections(connection);
		}
	}

	/**
	 * Fetch statistics as a single aggregate.
	 *
	 * @param errorCode a list where the function can add error codes
	 * @return the service result aggregate
	 */
	public ServiceResultAggregate fetchStatistics(List<ErrorMsgType> errorCode) {
		/* Obtain a connection first */
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (InterruptedException throwables) {
			throwables.printStackTrace();
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			return null;
		}
		statisticsManager.setConnection(connection);
		ServiceResultAggregate allStats = new ServiceResultAggregate("STATISTICS");
		ServiceResultAggregate playerStats = new ServiceResultAggregate("PLAYERS STATS");
		ServiceResultAggregate gameStats = new ServiceResultAggregate("GAME STATS");
		ServiceResultAggregate wordStats = new ServiceResultAggregate("WORD STATS");
		try {
			playerStats.addComponent(statisticsManager.topPlayerGame());
			playerStats.addComponent(statisticsManager.topPlayerMatch());
			playerStats.addComponent(statisticsManager.topPlayerGameAvg());
			playerStats.addComponent(statisticsManager.topPlayerMatchAvg());
			playerStats.addComponent(statisticsManager.topPlayerGamesN());
			playerStats.addComponent(statisticsManager.topPlayerDuplicated());
			playerStats.addComponent(statisticsManager.topPlayerWrong());
			gameStats.addComponent(statisticsManager.turnStats());
			gameStats.addComponent(statisticsManager.avgLetterOnGridOccurrences());
			wordStats.addComponent(statisticsManager.validWordsRanking());
			wordStats.addComponent(statisticsManager.requestedWordsRanking());
			wordStats.addComponent(statisticsManager.getGameMaxPointWords());
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			errorCode.add(ErrorMsgType.GENERIC_DB_ERROR);
			return null;
		} finally {
			resetConnections(connection);
		}
		allStats.addComponent(playerStats);
		allStats.addComponent(gameStats);
		allStats.addComponent(wordStats);
		return allStats;
	}

	/**
	 * Fetches stats for a word provided as input.
	 *
	 * @param word the word
	 * @return the service result interface
	 */
	public ServiceResultInterface requestWordStats(String word) {
		/* Obtain a connection first */
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (InterruptedException throwables) {
			throwables.printStackTrace();
			return null;
		}
		statisticsManager.setConnection(connection);
		try {
			ServiceResultInterface serviceResult = statisticsManager.getGameByRequestedWord(word);
			return serviceResult;
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			return null;
		} finally {
			resetConnections(connection);
		}
	}

	/**
	 * Registers the finished game statistics in the database.
	 *
	 * @param gameID      the game id
	 * @param gameGrids   the game grids
	 * @param numPlayers  the num players
	 * @param ruleset     the ruleset
	 * @param language    the language
	 * @param entriesInfo the entries info
	 */
	public void registerGameStats(UUID gameID, String[] gameGrids, Integer numPlayers, Ruleset ruleset, Language language, GameEntriesWrapper entriesInfo) {
		GameInfo gameInfoTuple = new GameInfo(gameID, gameGrids, numPlayers, ruleset, language);
		List<GameEntry> gameEntries = new ArrayList<>();
		/* For each match */
		for (int i = 0; i < entriesInfo.getMatchesInfo().size(); i++) {
			/* For each player and proposed words in the match */
			for (Map.Entry<String, Word[]> mapentry : entriesInfo.getMatchWords(i+1).entrySet()) {
				for (Word w : mapentry.getValue()) {
					GameEntry entry = new GameEntry();
					entry.setGame(gameID);
					entry.setMatch((short) (i+1));
					entry.setPlayerID(mapentry.getKey());
					entry.setWord(w.getWord());
					entry.setWrong(w.isWrong());
					entry.setDuplicated(w.isDuplicated());
					entry.setPoints((short) w.getPoints());
					//set requested
					boolean req = entriesInfo.wasRequested(w.getWord(), i+1);
					entry.setRequested(req);
					gameEntries.add(entry);
				}
			}
		}
		Connection connection;
		try {
			connection = ConnectionPool.getConnection();
		} catch (InterruptedException throwables) {
			throwables.printStackTrace();
			return;
		}
		try {
			connection.setAutoCommit(false);
			gameInfoDAO.setConnection(connection);
			gameInfoDAO.create(gameInfoTuple);
			gameEntryDAO.setConnection(connection);
			for (GameEntry ge : gameEntries) {
				gameEntryDAO.create(ge);
			}
			connection.commit();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			resetConnections(connection);
		}
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
