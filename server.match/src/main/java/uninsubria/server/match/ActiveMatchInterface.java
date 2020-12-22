package uninsubria.server.match;


import uninsubria.server.scoreCounter.PlayerScore;

public interface ActiveMatchInterface extends MatchInterface {

	void throwDices();
	void calculateScore(PlayerScore[] playerScores);
	void conclude();
}
