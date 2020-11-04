/**
 * 
 */
package uninsubria.server.database;

import java.util.UUID;

/**
 * @author Alessandro
 *
 */
public class GameRule {
	
	private String game;
	private int Num_players;
	private String Ruleset, Language;
	
	/**
	 * 
	 */
	protected GameRule() {
		super();
	}
	
	
	/**
	 * @param num_players
	 * @param ruleset
	 * @param language
	 */
	public GameRule(int num_players, String ruleset, String language) {
		super();
		UUID id = UUID.randomUUID();
		this.game =id.toString();
		Num_players = num_players;
		Ruleset = ruleset;
		Language = language;
	}


	/**
	 * @param game
	 * @param num_players
	 * @param ruleset
	 * @param language
	 */
	protected GameRule(String game, int num_players, String ruleset, String language) {
		super();
		this.game = game;
		Num_players = num_players;
		Ruleset = ruleset;
		Language = language;
	}
	public String getGame() {
		return game;
	}
	public void setGame(String game) {
		this.game = game;
	}
	public int getNum_players() {
		return Num_players;
	}
	public void setNum_players(int num_players) {
		Num_players = num_players;
	}
	public String getRuleset() {
		return Ruleset;
	}
	public void setRuleset(String ruleset) {
		Ruleset = ruleset;
	}
	public String getLanguage() {
		return Language;
	}
	public void setLanguage(String language) {
		Language = language;
	}
	
}
