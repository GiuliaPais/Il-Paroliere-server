package uninsubria.server.match;

import uninsubria.server.scoreCounter.PlayerScore;
import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.business.Player;

import java.util.ArrayList;

public abstract class AbstractMatch implements MatchInterface {

    protected Grid grid;
    protected int matchNo;
    protected ArrayList<PlayerWrapper> participants;

    @Override
    public Grid getGrid() {
        return grid;
    }

    @Override
    public int getMatchNo() {
        return matchNo;
    }

    @Override
    public ArrayList<PlayerWrapper> getParticipants() {
        return participants;
    }

}