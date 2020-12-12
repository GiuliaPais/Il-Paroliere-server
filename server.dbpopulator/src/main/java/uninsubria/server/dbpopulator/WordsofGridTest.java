package uninsubria.server.dbpopulator;

import uninsubria.server.dice.DiceSet;
import uninsubria.server.dictionary.loader.DictionaryException;
import uninsubria.server.dictionary.manager.DictionaryManager;
import uninsubria.utils.languages.Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class WordsofGridTest {


    public static void main(String[] args) throws IOException, DictionaryException, URISyntaxException {
        String n;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = br.readLine();
        if(n.equals("create")) {
        //Chiamato così generi la griglia italiana
        DiceSet diceSet = new DiceSet();
        //Così la griglia inglese
        //DiceSet diceSet1 = new DiceSet(DiceSetStandard.ENGLISH);
        diceSet.throwDices();
        diceSet.randomizePosition();
        String[] griglia = diceSet.getResultFaces();
        System.out.println("Griglia generata: " + Arrays.toString(griglia));
        //Qui per verificare se una parola è valida, sostituisci la parola ovviamente con quello che ti serve

        String[] parole = new String[15];
        boolean valid;

            for (int i = 0; i < parole.length; i++) {
                parole[i] = br.readLine();
                valid = DictionaryManager.isValid(parole[i], Language.ITALIAN);
                System.out.println("Parola valida? " + valid);
            }
        }else{
            validateGrid(wordsOfGrid.GRID12);
        }
    }

    public static void validateGrid(wordsOfGrid grid) throws IOException, DictionaryException, URISyntaxException {
        String gridM = grid.getGrid();
        List<String> words = grid.getWords();
        boolean valid;
        for (String x: words) {
            valid = DictionaryManager.isValid(x, Language.ITALIAN);
            if(valid)
                System.out.println(x + ", ");
        }

    }
}
