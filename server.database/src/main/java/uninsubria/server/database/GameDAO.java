
package uninsubria.server.database;

import java.util.List;

/**
 * @author Alessandro
 *
 */
public interface GameDAO {
	
	public void create(GameRule gameRule, GAMEInfo totalgame);
	public List<Game> getAll();
	public Game getByPK(String id);
	public void update(String[] type, String[] value);
	public void delete();
	
	
}
