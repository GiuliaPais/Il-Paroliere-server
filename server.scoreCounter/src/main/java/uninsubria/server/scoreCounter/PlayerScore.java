package uninsubria.server.scoreCounter;

import uninsubria.server.wrappers.PlayerWrapper;
import uninsubria.utils.business.Player;
import uninsubria.utils.languages.Language;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayerScore {

    private int totalScore;
    private Language language;
    private WordAnalyzer[] words;
    private PlayerWrapper player;

    /*-----Constructor-----*/
    /**
     * Crea l'oggetto PlayScore, per il calcolo delle parole trovate dal player.
     * @param p il player.
     * @param words le parole trovate dal player.
     * @param l la lingua sulla cui base analizzare le parole.
     */
    public PlayerScore(PlayerWrapper p, String[] words, Language l) {
        player = p;
        language = l;

        if(words == null) {
            String[] arrayTmp = {""};
            this.words = setWords(arrayTmp);

        } else
            this.words = setWords(words);

        setTotalScore();
    }

    /*-----Methods-----*/
    /**
     * Setta le parole prese in esame come doppie, se contenute nella lista passata come parametro.
     * @param duplicateWords la lista di doppie.
     */
    public void setDuplicateWords(ArrayList<WordAnalyzer> duplicateWords) {
        WordAnalyzer[] duplicate = duplicateWords.toArray(new WordAnalyzer[0]);

        for(int i = 0; i < words.length; i++) {
            for(int y = 0; y < duplicate.length; y++) {
                if(words[i].equals(duplicate[y]))
                    words[i].setDuplicate(true);
            }
        }

        setTotalScore();
    }

    /**
     * Restituisce le parole prese in esame.
     * @return le parole prese in esame.
     */
    public WordAnalyzer[] getWords() {
        return words;
    }

    /**
     * Restituisce il punteggio totale sulla base delle parole prese in esame.
     * @return il punteggio totale.
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * Restituisce il player le cui parole sono state prese in esame.
     * @return il player che ha trovato le parole.
     */
    public PlayerWrapper getPlayer() {
        return player;
    }

    public String toString() {
        return player.getPlayer().getName() + " " + Arrays.toString(words);
    }

    /*-----Private methods-----*/
    // Per trasformare un array di stringhe in array di WordAnalyzer.
    private WordAnalyzer[] setWords(String[] array) {
        WordAnalyzer[] tmp = new WordAnalyzer[array.length];

        for(int i = 0; i < array.length; i++) {
            tmp[i] = new WordAnalyzer(array[i], language);
        }

        return tmp;
    }

    // Ricalcola da 0 il punteggio di tutte le parole prese in esame.
    private void setTotalScore() {
        totalScore = 0;

        for(int i = 0; i < words.length; i++) {
            totalScore += words[i].getScore();
        }
    }
}
