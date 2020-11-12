/**
 * 
 */
package uninsubria.server.database;

import java.sql.Array;
import java.util.UUID;

/**
 * @author Alessandro
 *
 */
public class GameRule {
	
	private String game;
	private int Num_players;
	private String Ruleset, Language;
	private Array grid;
	
	protected GameRule() {
		super();
	}
	
	/**
	 * costruttore di gamerule con id generato dall'oggetto UUID
	 * @param num_players
	 * @param ruleset
	 * @param language
	 * @param grid
	 */
	public GameRule(int num_players, String ruleset, String language, Array grid) {
		super();
		UUID id = UUID.randomUUID();
		this.game =id.toString();
		Num_players = num_players;
		Ruleset = ruleset;
		Language = language;
		this.grid = grid;
	}

	/**
	 * costruttore
	 * @param game
	 * @param num_players
	 * @param ruleset
	 * @param language
	 * @param grid
	 */
	protected GameRule(String game, int num_players, String ruleset, String language, Array grid) {
		super();
		this.game = game;
		Num_players = num_players;
		Ruleset = ruleset;
		Language = language;
		this.grid = grid;
	}
	//getter e setter del game id
	public String getGame() {
		return game;
	}
	public void setGame(String game) {
		this.game = game;
	}
	//getter e setter del numero di giocatori della partita
	public int getNum_players() {
		return Num_players;
	}
	public void setNum_players(int num_players) {
		Num_players = num_players;
	}
	//getter e setter del tipo di regole applicate in quella partita(standard o special)
	public String getRuleset() {
		return Ruleset;
	}
	public void setRuleset(String ruleset) {
		Ruleset = ruleset;
	}
	//getter e setter della lingua (ITA o ENG)
	public String getLanguage() {
		return Language;
	}
	public void setLanguage(String language) {
		Language = language;
	}
	
	//getter e setter del'array contenente le griglie dei vari match
	public Array getGrid() {
		return grid;
	}
	public void setGrid(Array grid) {
		this.grid = grid;
	}
	
}
