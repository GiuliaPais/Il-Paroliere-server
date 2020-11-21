package uninsubria.server.dbpopulator;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a simulation of the game grid of a match as an undirected graph.
 * The main purpose of this class is to check if a given word is derivable from the proposed grid
 * for generating fake data to populate database tables.
 *
 * <u>IMPORTANT NOTES</u><br>
 * For the purpose of not complicating the implementation logic way too much, it is possible that
 * some words, even if derivable from the grid in practice, will be flagged as invalid. This is
 * because in some languages some of the dice faces contain more than one letter (for example in ITALIAN,
 * it is possible to have "Qu" as a possible value). Since it would be really complicated to check for
 * every language the presence of one or more of those instances in a word, the method simply returns false
 * for that word. For example: the word "QUADRO" contains "Qu" and will be flagged as invalid even if it is not.
 * Since this is a class for generating dummy data for database population we deemed it to be a fair compromise
 * but there is certainly space for improvement.
 * Also the function returns false for words that are less than 2 letters long (not a word).
 *
 * PROPOSALS FOR IMPROVEMENT<br>
 * - Check for every node in the graph that contains a label with more than 1 letter<br>
 * - Check for each of these if they can be found in the word and where<br>
 * - Identify which labels can coexist (independent events) and which cannot (partially overlapping)<br>
 * - Obtain all possible decompositions of the word (combinations) based on which labels can be found<br>
 * - Check that at least one of the decompositions is obtainable from the grid
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class GraphGrid {
    /*---Fields---*/
    private Graph<DiceLetterVertex, DefaultEdge> grid;
    private Map<Integer, DiceLetterVertex> ref = new HashMap<>();

    /*---Constructors---*/
    public GraphGrid(String[] arrayGrid) {
        this.grid = new SimpleGraph<>(DefaultEdge.class);
        populateGraph(arrayGrid);
    }

    /*---Methods---*/
    private void populateGraph(String[] arrayGrid) {
        for (int i = 1; i <= arrayGrid.length; i++) {
            DiceLetterVertex v = new DiceLetterVertex(arrayGrid[i-1]);
            ref.put(i, v);
        }
        for (DiceLetterVertex vert : ref.values()) {
            grid.addVertex(vert);
        }
        grid.addEdge(ref.get(1), ref.get(2));
        grid.addEdge(ref.get(1), ref.get(5));
        grid.addEdge(ref.get(1), ref.get(6));
        grid.addEdge(ref.get(2), ref.get(3));
        grid.addEdge(ref.get(2), ref.get(5));
        grid.addEdge(ref.get(2), ref.get(6));
        grid.addEdge(ref.get(2), ref.get(7));
        grid.addEdge(ref.get(3), ref.get(6));
        grid.addEdge(ref.get(3), ref.get(7));
        grid.addEdge(ref.get(3), ref.get(8));
        grid.addEdge(ref.get(3), ref.get(4));
        grid.addEdge(ref.get(4), ref.get(7));
        grid.addEdge(ref.get(4), ref.get(8));
        grid.addEdge(ref.get(5), ref.get(6));
        grid.addEdge(ref.get(5), ref.get(9));
        grid.addEdge(ref.get(5), ref.get(10));
        grid.addEdge(ref.get(6), ref.get(7));
        grid.addEdge(ref.get(6), ref.get(9));
        grid.addEdge(ref.get(6), ref.get(10));
        grid.addEdge(ref.get(6), ref.get(11));
        grid.addEdge(ref.get(7), ref.get(10));
        grid.addEdge(ref.get(7), ref.get(11));
        grid.addEdge(ref.get(7), ref.get(12));
        grid.addEdge(ref.get(7), ref.get(8));
        grid.addEdge(ref.get(8), ref.get(11));
        grid.addEdge(ref.get(8), ref.get(12));
        grid.addEdge(ref.get(9), ref.get(10));
        grid.addEdge(ref.get(9), ref.get(13));
        grid.addEdge(ref.get(9), ref.get(14));
        grid.addEdge(ref.get(10), ref.get(13));
        grid.addEdge(ref.get(10), ref.get(14));
        grid.addEdge(ref.get(10), ref.get(15));
        grid.addEdge(ref.get(10), ref.get(11));
        grid.addEdge(ref.get(11), ref.get(14));
        grid.addEdge(ref.get(11), ref.get(15));
        grid.addEdge(ref.get(11), ref.get(16));
        grid.addEdge(ref.get(11), ref.get(12));
        grid.addEdge(ref.get(12), ref.get(15));
        grid.addEdge(ref.get(12), ref.get(16));
        grid.addEdge(ref.get(13), ref.get(14));
        grid.addEdge(ref.get(14), ref.get(15));
        grid.addEdge(ref.get(15), ref.get(16));
    }

    public boolean isDerivableFromGrid(String word) {
        if (word.length() < 3) {
            return false;
        }
        /* Re-set all nodes to not visited */
        clearVisited();
        /* Split the word to obtain the faces that generated the word */
        char[] chars = word.toCharArray();
        List<String> faces = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            String label = String.valueOf(chars[i]);
            faces.add(label);
        }
        // Check if all faces have at least a node in the graph
        // Keep references of the nodes
        Map<String, List<Integer>> nodesFound = new HashMap<>();
        for (Map.Entry<Integer, DiceLetterVertex> nodeRef : ref.entrySet()) {
            for (String face: faces) {
                List<Integer> nodePos = new ArrayList<>();
                if (face.equalsIgnoreCase(nodeRef.getValue().getLabel())) {
                    nodePos.add(nodeRef.getKey());
                }
                if (nodesFound.get(face) != null) {
                    List<Integer> pre = nodesFound.get(face);
                    for (Integer pos : nodePos) {
                        if (!pre.contains(pos)) {
                            pre.add(pos);
                        }
                    }
                    nodesFound.replace(face, pre);
                } else {
                    nodesFound.put(face, nodePos);
                }
            }
        }
        boolean allPresent = true;
        for (String f : faces) {
            if (nodesFound.get(f).isEmpty()) {
                allPresent = false;
                break;
            }
        }
        if (!allPresent) {
            return false;
        }
        return checkCurrentCombination(nodesFound, faces);
    }

    private boolean checkCurrentCombination(Map<String, List<Integer>> nodesFound, List<String> faces) {
        boolean foundAPath = false;
        int i = 0;
        List<Integer> candidates = nodesFound.get(faces.get(0));
        while (!(foundAPath | i == candidates.size())) {
            DiceLetterVertex vertex = ref.get(candidates.get(i++));
            vertex.setVisited(true);
            foundAPath = foundAPath | searchInFrontier(vertex, nodesFound.get(faces.get(1)), 0, faces, nodesFound);
        }
        return foundAPath;
    }

    private boolean searchInFrontier(DiceLetterVertex startNode, List<Integer> candidateNext, int currPosition,
                                     List<String> faces,  Map<String, List<Integer>> nodesFound) {
        Set<DiceLetterVertex> frontier = Graphs.neighborSetOf(grid, startNode);
        if (currPosition == faces.size()-2) {
            for (Integer i : candidateNext) {
                DiceLetterVertex v = ref.get(i);
                if (frontier.contains(v) & !v.isVisited()) {
                    v.setVisited(true);
                    return true;
                }
            }
            return false;
        }
        boolean atLeastOneTrue = false;
        for (Integer i : candidateNext) {
            DiceLetterVertex v = ref.get(i);
            int newPos = currPosition + 1;
            if (frontier.contains(v) & !v.isVisited()) {
                v.setVisited(true);
                List<Integer> candidatesForNext = nodesFound.get(faces.get(newPos+1));
                boolean success = searchInFrontier(v, candidatesForNext,
                        newPos, faces, nodesFound);
                atLeastOneTrue = atLeastOneTrue | success;
            }
        }
        return atLeastOneTrue;
    }

    private void clearVisited() {
        for (DiceLetterVertex vertex : ref.values()) {
            vertex.setVisited(false);
        }
    }
}
