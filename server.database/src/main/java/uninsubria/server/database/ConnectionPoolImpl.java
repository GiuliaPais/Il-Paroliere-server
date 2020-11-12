/**
 * 
 */
package uninsubria.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * @author Alessandro
 *
 */
public class ConnectionPoolImpl implements ConnectionPoolInterface {

	private String user;
	private String password;
	private String dburl;
	private String dbhost;
	private final int POOL_SIZE=100;
	private Connection con;
	private List<Connection> connectionPool;
	
	public Connection getCon(){
		return con;
	}
	
	/**
	 * 
	 */
	public ConnectionPoolImpl() {
		super();
	}

	/**
	 * @param con
	 * @param connectionPool
	 */
	public ConnectionPoolImpl(Connection con, List<Connection> connectionPool) {
		super();
		try {
			this.con = ((ConnectionPoolImpl) con).getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.connectionPool = connectionPool;
	}

	/**
	 * Chiede al metodo openConnection() di realizzare una connessione col database
	 * 
	 */
	public Connection getConnection() throws SQLException{
		
		dbhost="localhost"; //inserire l'indirizzo ip della macchina che hosta il server
		dburl = "jdbc:postgresql://" + dbhost + "/Paroliere";
		user="postgres";
		password="qwerty";
		
		try {
			if(connectionPool.size()<POOL_SIZE) {
				
				con=openConnection(dburl, user, password);
				connectionPool.add(con);
				System.out.println("connected to database");
				return con;
			}
					
		} catch(SQLException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
		}
		return null;
	}
	
	public int getPOOL_SIZE() {
		return POOL_SIZE;
	}

	/**
	 * Realizza la connessione con il database (il metodo ï¿½ privato per limitare la possibilitï¿½ di creare connessioni al database)
	 * @param url, usr, pwd
	 * @throws SQLException
	 * 
	 * */
	private Connection openConnection(String url,String usr, String pwd) throws SQLException{
		
		Properties props= new Properties();
		props.setProperty("user", usr);
		props.setProperty("password", pwd);
		
		con= DriverManager.getConnection(url, props);
		return con;
	}
	
	
	/**
	 * chiude la connessione e la rimuove dalla lista
	 */
	public void releaseConnection() throws SQLException {
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connectionPool.remove(con);
	}
}
