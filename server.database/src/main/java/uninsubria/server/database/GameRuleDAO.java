package uninsubria.server.database;

import java.util.List;

public interface GameRuleDAO {

	public void create();
	public List<GameRule> getAll();
	public void update(String[] type, String[] value);
	public void delete();
	
}
