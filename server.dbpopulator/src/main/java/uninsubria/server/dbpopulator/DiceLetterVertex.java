package uninsubria.server.dbpopulator;

/**
 * @author Giulia Pais
 * @version 0.9.0
 */
public class DiceLetterVertex {
    /*---Fields---*/
    private String label;
    private boolean visited = false;

    /*---Constructors---*/
    public DiceLetterVertex(String label) {
        this.label = label;
    }

    /*---Methods---*/
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}
