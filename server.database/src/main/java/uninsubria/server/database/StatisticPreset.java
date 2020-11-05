/**
 * 
 */
package uninsubria.server.database;

/**
 * @author Alessandro
 *
 */
public class StatisticPreset {
	
	private String query;
	
	/**
	 * @param query
	 */
	public StatisticPreset(String query) {
		super();
		this.query = query;
	}
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}



	//Player Statistics
	private String BestScorePl  = 
			"SELECT sumGame.PlayerId, sumMatch.SUM_MATCH, sumGame.SUM_GAME\r\n" + 
			"FROM	(SELECT PlayerId, SUM(points) as SUM_MATCH\r\n" + 
					"FROM Game\r\n" + 
					"WHERE Duplicated='0' AND Wrong='0'\r\n" + 
					"GROUP BY PlayerId, Game, Match\r\n" + 
					"HAVING SUM(points)>(	SELECT SUM(points)\r\n" + 
										   "FROM Game\r\n" + 
										   "GROUP BY PlayerId, Game, Match))sumMatch\r\n" +
			"FULL JOIN\r\n" + 
			"(SELECT PlayerId, SUM(points) as SUM_GAME\r\n" + 
			"FROM Game\r\n" + 
			"WHERE Duplicated='0' AND Wrong='0'\r\n" + 
			"GROUP BY PlayerId, Game\r\n" + 
			"HAVING SUM(points)>(	SELECT SUM(points)\r\n" + 
									"FROM Game\r\n" + 
									"GROUP BY PlayerId, Game))sumGame\r\n" + 
			"ON sumMatch.PlayerId=sumGame.PlayerId\r\n";
	
	private String BestAvgScorePl = 
			"SELECT avgGame.PlayerId, avgMatch.AVG_MATCH, avgGame.AVG_GAME\r\n" + 
			"FROM(	SELECT PlayerId, AVG(points) as AVG_MATCH\r\n" + 
					"FROM Game\r\n" + 
					"WHERE Duplicated='0' AND Wrong='0'\r\n" + 
					"GROUP BY PlayerId, Game\r\n" + 
					"HAVING AVG(points) >(	SELECT AVG(points)\r\n" + 
											"FROM Game\r\n" + 
											"WHERE Duplicated='0' AND Wrong='0'\r\n" + 
											"GROUP BY PlayerId, Game))avgMatch\r\n" + 
			"FULL JOIN (SELECT PlayerId, AVG(points) as AVG_GAME\r\n" + 
						"FROM Game\r\n" + 
						"WHERE Duplicated='0' AND Wrong='0'\r\n" + 
						"GROUP BY PlayerId, Game,Match\r\n" + 
						"HAVING AVG(points) >(	SELECT AVG(points)\r\n" + 
												"FROM Game\r\n" + 
												"WHERE Duplicated='0' AND Wrong='0'\r\n" + 
												"GROUP BY PlayerId, Game, Match))avgGame\r\n" + 
			"ON avgMatch.PlayerId=avgGame.PlayerId";
	
	private String  MostGamePlayedPl= 
			"SELECT PlayerId, COUNT(*) as num_Game_played\r\n" + 
			"FROM Game\r\n" + 
			"GROUP BY PlayerId, Game\r\n" + 
			"HAVING COUNT(*) > (	SELECT COUNT(*)\r\n" + 
			"FROM Game\r\n" + 
			"GROUP BY PlayerId, Game)";
	
	private String MostDuplicateWordPl =
			"SELECT PlayerId, COUNT(*)\r\n" + 
			"FROM Game\r\n" + 
			"WHERE Duplicated>'0' AND Wrong='0'\r\n" + 
			"GROUP BY Game, PlayerId, Word\r\n" + 
			"HAVING COUNT(*)>( SELECT COUNT(*)\r\n" + 
			"FROM Game\r\n" + 
			"WHERE Duplicated>'0' AND Wrong='0'\r\n" + 
			"GROUP BY Game, PlayerId, Word)\r\n";
	
	private String MostNotValidWordPl =
			"SELECT PlayerId, COUNT(*)\r\n" + 
			"FROM Game\r\n" + 
			"WHERE Wrong>'0'\r\n" + 
			"GROUP BY PlayerId, Word\r\n" + 
			"HAVING  COUNT(*) > (	 SELECT COUNT(*)\r\n" + 
									"FROM Game\r\n" + 
									"WHERE Wrong>'0'\r\n" + 
									"GROUP BY PlayerId, Word)\r\n";
	
	//Game Statistics
	private String AvgTurns = 
			"SELECT Game, AVG(Match) as Avarage_Match_Number\r\n" + 
			"FROM Game NATURAL JOIN GameRule\r\n" + 
			"GROUP BY Num_players, Game\r\n" + 
			"HAVING MAX(Match) = (	 SELECT MAX(Match)\r\n" + 
									"FROM Game NATURAL JOIN GameRule\r\n" + 
									"GROUP BY Num_players, Game)";
	
	private String MinMaxTurns = 
			"SELECT minTurn.Game, minTurn.Num_Players, maxTurn.MAX_Match_Number, minTurn.MIN_Match_Number\r\n" + 
			"FROM(	SELECT Game, Num_players, MAX(Match) as MAX_Match_Number\r\n" + 
					"FROM Game NATURAL JOIN GameRule\r\n" + 
					"GROUP BY Num_players, Game\r\n" + 
					"HAVING MAX(Match) = (	SELECT MAX(Match)\r\n" + 
											"FROM Game NATURAL JOIN GameRule\r\n" + 
											"GROUP BY Num_players, Game))maxTurn\r\n" + 
			"FULL JOIN (	SELECT  Game, Num_players, MIN(Match) as MIN_Match_Number\r\n" + 
							"FROM Game NATURAL JOIN GameRule\r\n" + 
							"GROUP BY Num_players, Game\r\n" + 
							"HAVING MIN(Match) = (	SELECT MIN(Match)\r\n" + 
													"FROM Game NATURAL JOIN GameRule\r\n" + 
													"GROUP BY Num_players, Game))minTurn\r\n" + 
			"ON maxTurn.Game = minTurn.Game";
	
	private String AvgGridOccurences =
			"SELECT grid\r\n" + 
			"FROM Game NATURAL JOIN GameRule\r\n" + 
			"WHERE Language=?\r\n" + 
			"GROUP BY Game, Match, grid\r\n"; 
	
	private String GameWordRequested = 
			"SELECT word, COUNT(*) as Definition_Request\r\n" + 
			"FROM Game\r\n" + 
			"WHERE Requested='1'\r\n" + 
			"GROUP BY word \r\n" + 
			"ORDER BY COUNT(*) DESC\r\n";
	
	//Word Statistics
	private String WordRankOccourences = 
			"SELECT Word, COUNT(*) as word_occurences\r\n" + 
			"FROM Game\r\n" + 
			"WHERE Duplicated='0' AND Wrong='0'\r\n" + 
			"GROUP BY Word\r\n" + 
			"ORDER BY COUNT(*) DESC";
	
	private String MostImportantWord =
			"SELECT Game, Word, Points \r\n" + 
			"FROM Game\r\n" + 
			"WHERE Points = (	 SELECT MAX(Points)\r\n" + 
								"FROM Game\r\n" + 
								"GROUP BY Game)";
	
	private String ShowDefinitionRequest = 
			"SELECT Game, word\r\n" + 
			"FROM Game\r\n" + 
			"WHERE word = 'Parola scelta' AND Requested > '0'";

	public String getBestScorePl() {
		return BestScorePl;
	}

	public String getMostGamePlayedPl() {
		return MostGamePlayedPl;
	}

	public String getBestAvgScorePl() {
		return BestAvgScorePl;
	}

	public String getMostDuplicateWordPl() {
		return MostDuplicateWordPl;
	}

	public String getMostNotValidWordPl() {
		return MostNotValidWordPl;
	}

	public String getAvgTurns() {
		return AvgTurns;
	}

	public String getMinMaxTurns() {
		return MinMaxTurns;
	}

	public String getAvgGridOccurences() {
		return AvgGridOccurences;
	}

	public String getGameWordRequested() {
		return GameWordRequested;
	}

	public String getWordRankOccourences() {
		return WordRankOccourences;
	}

	public String getMostImportantWord() {
		return MostImportantWord;
	}

	public String getShowDefinitionRequest() {
		return ShowDefinitionRequest;
	}
	
	
	
}
