package uninsubria.server.match;

import tmpClasses.Player;

public interface MatchInterface {

    public Grid getGrid();
    public int getMatchNo();
    public Player[] getParticipants();
    public int[] getScores();
}