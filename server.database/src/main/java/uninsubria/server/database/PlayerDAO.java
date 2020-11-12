/**
 * 
 */
package uninsubria.server.database;

import uninsubria.utils.business.Player;

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
	public void update(String[] type, String[] value);
	public void delete();
	
}
