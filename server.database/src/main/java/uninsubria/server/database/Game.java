/**
 * 
 */
package uninsubria.server.database;

import java.sql.Array;
import java.sql.Date;
import java.util.UUID;

/**
 * @author Alessandro
 *
 */
public class Game {
	
	private String game;
	private String PlayerId;	
	private Array Grid;
	private java.sql.Date date;
	private int match;
	private String word;
	private boolean Requested, Duplicated, Wrong;
	private int points, occurrences;
	
	/**
	 * 
	 */
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
	protected Game(String game, String playerId, Array grid, Date date, int match, String word, boolean requested,
			boolean duplicated, boolean wrong, int points) {
		super();

		UUID id = UUID.randomUUID();
		this.game = id.toString();
		PlayerId = playerId;
		Grid = grid;
		this.date = date;
		this.match = match;
		this.word = word;
		Requested = requested;
		Duplicated = duplicated;
		Wrong = wrong;
		this.points = points;
	}
	
	public String getGame() {
		return game;
	}
	public void setGame(String game) {
		this.game = game;
	}
	public String getPlayerId() {
		return PlayerId;
	}
	public void setPlayerId(String playerId) {
		PlayerId = playerId;
	}
	public Array getGrid() {
		return Grid;
	}
	public void setGrid(Array grid) {
		Grid = grid;
	}
	public java.sql.Date getDate() {
		return date;
	}
	public void setDate(java.sql.Date date) {
		this.date = date;
	}
	public int getMatch() {
		return match;
	}
	public void setMatch(int match) {
		this.match = match;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public boolean isRequested() {
		return Requested;
	}
	public void setRequested(boolean requested) {
		Requested = requested;
	}
	public boolean isDuplicated() {
		return Duplicated;
	}
	public void setDuplicated(boolean duplicated) {
		Duplicated = duplicated;
	}
	public boolean isWrong() {
		return Wrong;
	}
	public void setWrong(boolean wrong) {
		Wrong = wrong;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	
}