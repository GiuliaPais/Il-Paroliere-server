/**
 * 
 */
package uninsubria.server.database;

import java.util.List;

/**
 * @author Alessandro
 *
 */
public interface GameDAO {
	
	public void create();
	public List<Game> getAll();
	public Game getByPK(String id);
	public void update(GameRule gameRule, GAMEInfo gameInf);
	public void delete();
	
}
