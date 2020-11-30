package uninsubria.server.match;

import uninsubria.utils.business.Player;

public abstract class AbstractMatch implements MatchInterface {

    protected Grid grid;
    protected int matchNo;
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

}