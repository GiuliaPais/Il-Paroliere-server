package uninsubria.server.match;

import java.util.Map;

public class ActiveMatch extends AbstractMatch implements ActiveMatchInterface {

    private Map<String, String> wordsFound;
    private int[] duplicatedWords;
    private Game game;

    public ActiveMatch() {
        super.matchNo = 1;
    }

    /**
     * Lancia i dadi e li dispone sulla griglia.
     */
    @Override
    public Grid throwDices() {
        super.grid.throwDices();
        return super.grid;
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
        super.matchNo++;
    }

}
