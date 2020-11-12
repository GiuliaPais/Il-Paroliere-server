package uninsubria.server.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface for ConnectionPool.
 *
 * @author Alessandro Lerro
 * @author Giulia Pais
 */
public interface ConnectionPoolInterface {

	/**
	 * Gets a Connection object from the connection pool.
	 * If the pool is empty a new Connection is created and returned instead.
	 *
	 * @return A connection
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException;

	/**
	 * Release a connection and puts it back in the pool.
	 *
	 * @param con the connection
	 */
	public void releaseConnection(Connection con);
	
}
