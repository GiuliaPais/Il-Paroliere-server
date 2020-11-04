/**
 * 
 */
package uninsubria.server.database;

import java.util.List;

/**
 * @author Alessandro
 *
 */
public interface PlayerDAO {
	
	public void create();
	public List<Player> getAll();
	public Player getByUserId(String id);
	public Player getByEmail(String email);
	public void update();
	public void delete();
	
}
