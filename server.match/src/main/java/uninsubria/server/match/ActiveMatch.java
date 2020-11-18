package uninsubria.server.match;

import uninsubria.server.roomReference.RoomManager;
import uninsubria.utils.business.Player;

import java.util.Map;

public class ActiveMatch extends AbstractMatch implements ActiveMatchInterface {

    private Map<String, String> wordsFound;
    private int[] duplicatedWords;
    private Game game;

    public ActiveMatch(int numMatch, Player[] p) {
        super.matchNo = numMatch;
        super.participants = p;
    }

    /**
     * Lancia i dadi e li dispone sulla griglia.
     */
    @Override
    public void throwDices() {
        super.grid.throwDices();
    }

    @Override
    public void calculateScore() {
        // TODO Auto-generated method stub
    }

    /**
     * Conclude il turno e resetta i dadi.
     */
    @Override
    public void Conclude() {
        super.grid.resetDices();
    }

}
