package uninsubria.server.match;

import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.business.Player;

import java.util.ArrayList;

public interface MatchInterface {

    public Grid getGrid();
    public int getMatchNo();
    public ArrayList<PlayerWrapper> getParticipants();

}