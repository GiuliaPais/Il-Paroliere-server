package uninsubria.server.db.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Implementation of a connection pool for faster access to the database.
 *
 * @author Alessandro Lerro
 * @author Giulia Pais
 * @version 0.9.1
 */
public class ConnectionPool {
	/**
	 * Reference to the only instance of this class (singleton pattern)
	 */
	private static ConnectionPool instance;

	private String user;
	private String password;
	private String dburl;
	private String dbhost;
	private String dbName;
	private LinkedBlockingQueue<Connection> connectionPool;

	private ConnectionPool() {}

	/**
	 * Gets the only instance of ConnectionPoolImpl (singleton pattern).
	 * This method should be used instead of the constructor.
	 *
	 * @return the instance
	 */
	public static ConnectionPool getInstance() {
		if (instance == null) {
			instance = new ConnectionPool();
		}
		return instance;
	}

	/**
	 * Initializes the connection pool. Must be called when server is starting.
	 *
	 * @param admin    the admin
	 * @param password the password
	 * @param host     the host
	 * @param dbName   the db name
	 * @throws SQLException
	 */
	public static void initializeConnectionPool(String admin, String password, String host, String dbName) throws SQLException {
		ConnectionPool instance = getInstance();
		instance.user = admin;
		instance.password = password;
		instance.dbhost = host;
		instance.dbName = dbName;
		instance.dburl = "jdbc:postgresql://" + host + "/" + dbName;
		instance.initPool();
	}


	/**
	 * Retrieves a connection from the pool. If the pool is empty the calling thread waits until a
	 * connection is available in the queue.
	 *
	 * @return the connection
	 * @throws InterruptedException the interrupted exception
	 */
	public static Connection getConnection() throws InterruptedException {
		return instance.connectionPool.take();
	}

	/**
	 * Release connection and put it back in the pool.
	 *
	 * @param con the connection
	 */
	public static void releaseConnection(Connection con) {
		instance.connectionPool.offer(con);
	}

	/**
	 * Creates a fixed amount of connections and puts them in the pool.
	 * @throws SQLException
	 */
	private void initPool() throws SQLException {
		int POOL_SIZE = 90;
		this.connectionPool = new LinkedBlockingQueue<>(POOL_SIZE);
		for(int i = 0; i < POOL_SIZE; i++) {
			Connection con = DriverManager.getConnection(dburl, user, password);
			connectionPool.offer(con);
		}
	}

	/**
	 * Closes all connections and clears all elements in the pool.
	 * To call before closing server.
	 *
	 * @throws SQLException the sql exception
	 */
	public static void clearPool() throws SQLException {
		for (Connection c : instance.connectionPool) {
			c.close();
		}
		instance.connectionPool.clear();
	}

	/**
	 * Regenerates a connection. This method is called when there is no other choice but aborting
	 * the current connection to the database (mainly because it's not responding and blocking tables).
	 * The connection is not put back in the pool but closed and a new one is created and offered to the pool instead.
	 *
	 * @param connection the connection
	 */
	public static void regenConnection(Connection connection) {
		try {
			connection.abort(Executors.newSingleThreadExecutor());
		} catch (SQLException ignored) {
		}
		try {
			Connection con;
			con = DriverManager.getConnection(getInstance().dburl, getInstance().user, getInstance().password);
			getInstance().connectionPool.offer(con);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}

}
