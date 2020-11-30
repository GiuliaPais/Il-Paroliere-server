package uninsubria.server.match;

import uninsubria.utils.business.Player;

public interface MatchInterface {

    public Grid getGrid();
    public int getMatchNo();
    public Player[] getParticipants();
    void calculateScore();
}