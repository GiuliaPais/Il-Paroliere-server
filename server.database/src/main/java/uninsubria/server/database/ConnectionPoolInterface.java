/**
 * 
 */
package uninsubria.server.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Alessandro
 *
 */
public interface ConnectionPoolInterface {

	public Connection getConnection() throws SQLException;
	public void releaseConnection() throws SQLException;
	
}
