package uninsubria.server.match;

public enum GameState {

    ONGOING("On going"),
    FINISHED("Finished"),
    INTERRUPTED("Interrupted");

    private String nameState;

    private GameState(String name) {
        nameState = name;
    }

    public String toString() {
        return nameState;
    }

    /**
     * Restituisce il GameState corrispondende all'id passatogli come argomento.
     * @param id
     * @return il GameState il cui ordinale è uguale all'id passato come argomento.
     */
    public static GameState getById(int id) {
        for(GameState gs : GameState.values()) {
            if(gs.ordinal() == id)
                return gs;
        }

        return null;
    }
}
