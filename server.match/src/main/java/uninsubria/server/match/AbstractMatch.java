package uninsubria.server.match;

import tmpClasses.Player;

public abstract class AbstractMatch implements MatchInterface {

    protected Grid grid;
    protected int matchNo;
    protected int[] scores;
    protected Player[] participants;

    @Override
    public Grid getGrid() {
        return grid;
    }

    @Override
    public int getMatchNo() {
        return matchNo;
    }

    @Override
    public Player[] getParticipants() {
        return participants;
    }

    @Override
    public int[] getScores() {
        return scores;
    }

}