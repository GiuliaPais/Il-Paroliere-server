/**
 * 
 */
package uninsubria.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The type Connection pool.
 *
 * @author Alessandro Lerro
 * @author Giulia Pais
 */
public class ConnectionPoolImpl implements ConnectionPoolInterface {
	/**
	 * Reference to the only instance of this class (singleton pattern)
	 */
	private static ConnectionPoolImpl instance;

	private String user;
	private String password;
	private String dburl;
	private String dbhost;
	private String dbName;
	private int INITIAL_POOL_SIZE;
	private ConcurrentLinkedQueue<Connection> connectionPool;

	private ConnectionPoolImpl() {}

	/**
	 * Gets the only instance of ConnectionPoolImpl (singleton pattern).
	 * This method should be used instead of the constructor.
	 *
	 * @return the instance
	 */
	public static ConnectionPoolImpl getInstance() {
		if (instance != null) {
			return instance;
		} else {
			instance = new ConnectionPoolImpl();
			return instance;
		}
	}

	/**
	 * Initialize pool.
	 *
	 * @param admin    the admin
	 * @param password the password
	 * @param host     the host
	 * @param dbName   the db name
	 * @throws SQLException
	 */
	public static void initializeConnectionPool(String admin, String password, String host, String dbName) throws SQLException {
		ConnectionPoolImpl instance = getInstance();
		instance.user = admin;
		instance.password = password;
		instance.dbhost = host;
		instance.dbName = dbName;
		instance.dburl = "jdbc:postgresql://" + host + "/" + dbName;
		instance.INITIAL_POOL_SIZE = 100;
		instance.initPool();
	}

	@Override
	public Connection getConnection() throws SQLException {
		if (connectionPool.peek() != null) {
			return connectionPool.poll();
		} else {
			return DriverManager.getConnection(dburl, user, password);
		}
	}

	@Override
	public void releaseConnection(Connection con) {
		instance.connectionPool.offer(con);
	}

	/**
	 * Creates a fixed amount of connections and puts them in the pool.
	 * @throws SQLException
	 */
	private void initPool() throws SQLException {
		this.connectionPool = new ConcurrentLinkedQueue<>();
		for(int i=0; i<INITIAL_POOL_SIZE; i++) {
			Connection con = DriverManager.getConnection(dburl, user, password);
			connectionPool.offer(con);
		}
	}

	/**
	 * Closes all connections and clears all elements in the pool.
	 * To call before closing server.
	 *
	 * @throws SQLException
	 */
	public void clearPool() throws SQLException {
		for (Connection c : instance.connectionPool) {
			c.close();
		}
		instance.connectionPool.clear();
	}

//	/**
//	 * @param con
//	 * @param connectionPool
//	 */
//	public ConnectionPoolImpl(Connection con, List<Connection> connectionPool) {
//		super();
//		try {
//			this.con = ((ConnectionPoolImpl) con).getConnection();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		this.connectionPool = connectionPool;
//	}
//
//	/**
//	 * Chiede al metodo openConnection() di realizzare una connessione col database
//	 *
//	 */
//	public Connection getConnection() throws SQLException{
//
//		dbhost="localhost"; //inserire l'indirizzo ip della macchina che hosta il server
//		dburl = "jdbc:postgresql://" + dbhost + "/Paroliere";
//		user="postgres";
//		password="qwerty";
//
//		try {
//			if(connectionPool.size()<POOL_SIZE) {
//
//				con=openConnection(dburl, user, password);
//				connectionPool.add(con);
//				System.out.println("connected to database");
//				return con;
//			}
//
//		} catch(SQLException e) {
//			// TODO Auto-generated catch block
//				e.printStackTrace();
//		}
//		return null;
//	}
//
//	public int getPOOL_SIZE() {
//		return POOL_SIZE;
//	}

//	/**
//	 * Realizza la connessione con il database (il metodo ï¿½ privato per limitare la possibilitï¿½ di creare connessioni al database)
//	 * @param url, usr, pwd
//	 * @throws SQLException
//	 *
//	 * */
//	private Connection openConnection(String url,String usr, String pwd) throws SQLException{
//
//		Properties props= new Properties();
//		props.setProperty("user", usr);
//		props.setProperty("password", pwd);
//
//		con= DriverManager.getConnection(url, props);
//		return con;
//	}
	
//
//	/**
//	 * chiude la connessione e la rimuove dalla lista
//	 */
//	public void releaseConnection() throws SQLException {
//		try {
//			con.close();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		connectionPool.remove(con);
//	}


}
