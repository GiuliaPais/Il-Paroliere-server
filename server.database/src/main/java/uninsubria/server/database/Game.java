
package uninsubria.server.database;

import java.util.UUID;

/**
 * @author Alessandro
 *
 */
public class Game {
	
	private String game;
	private String PlayerId;	
	private int match;
	private String word;
	private boolean Requested, Duplicated, Wrong;
	private int points;
	
	protected Game() {
		super();
	}
	/**
	 * @param game
	 * @param playerId
	 * @param grid
	 * @param date
	 * @param match
	 * @param word
	 * @param requested
	 * @param duplicated
	 * @param wrong
	 * @param points
	 */
	protected Game(String game, String playerId, int match, String word, boolean requested,
			boolean duplicated, boolean wrong, int points) {
		super();

		UUID id = UUID.randomUUID();
		this.game = id.toString();
		PlayerId = playerId;
		this.match = match;
		this.word = word;
		Requested = requested;
		Duplicated = duplicated;
		Wrong = wrong;
		this.points = points;
	}
	
	//getter e setter del game id
	public String getGame() {
		return game;
	}
	public void setGame(String game) {
		this.game = game;
	}
	
	//getter e setter del player id
	public String getPlayerId() {
		return PlayerId;
	}
	public void setPlayerId(String playerId) {
		PlayerId = playerId;
	}

	//getter e setter del numero del match di una partina(1+)
	public int getMatch() {
		return match;
	}
	public void setMatch(int match) {
		this.match = match;
	}
	
	//getter e setter delle parole
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	
	//getter e setter dell'informazione relativa alla presenza di una richiesta di una parola
	public boolean isRequested() {
		return Requested;
	}
	public void setRequested(boolean requested) {
		Requested = requested;
	}
	
	//getter e setter dell'informazione relativa alla conferma che la parola sia doppia (ovvero che qualcuno abbia giÃ  preso)
	public boolean isDuplicated() {
		return Duplicated;
	}
	public void setDuplicated(boolean duplicated) {
		Duplicated = duplicated;
	}
	
	//getter e setter dell'informazione relativa alla presenza di una parola sbagliata(non derivante dalla configurazione o priva di significato)
	public boolean isWrong() {
		return Wrong;
	}
	public void setWrong(boolean wrong) {
		Wrong = wrong;
	}
	
	//getter e setter del punteggio di una parola
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	
}