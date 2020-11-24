package uninsubria.server.scoreCounter;

import uninsubria.server.dictionary.loader.DictionaryException;
import uninsubria.server.dictionary.manager.DictionaryManager;
import uninsubria.utils.languages.Language;

import java.io.IOException;
import java.net.URISyntaxException;

public class WordAnalyzer {

    private boolean valid, duplicate;
    private String word;
    private int score;
    private Language language;

    /*-----Constructor-----*/
    /**
     * Costruttore con parametro, la stringa analizzata sarà quella passata come parametro.
     * @param word la parola da analizzare.
     */
    public WordAnalyzer(String word, Language l) {
        language = l;
        initialize(word);
    }

    /*-----Methods-----*/
    /**
     * Restituisce true se la parola è presente nel dizionario.
     * @return true se presente nel dizionario, false altrimenti.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Restituisce true se la parola è doppia.
     * @return true se doppia, false altrimenti.
     */
    public boolean isDuplicated() {
        return duplicate;
    }

    /**
     * Restituisce la parola analizzata.
     * @return la parola analizzata.
     */
    public String getWord() {
        return word;
    }

    /**
     * Restituisce il punteggio associato alla lunghezza della parola. Se non valida o doppia, sarà 0.
     * @return il punteggio.
     */
    public int getScore() {
        return score;
    }

    /**
     * Restituisce true se la parola analizzata coincide con la parola dell'oggetto WordAnalyzer passato come parametro.
     * @param otherWord l'oggetto da confrontare.
     * @return true se le parole coincidono, false altrimenti.
     */
    public boolean equals(WordAnalyzer otherWord) {
        return word.equals(otherWord.getWord());
    }

    /**
     * Restituisce true se la parola analizzata coincide con la stringa passata come parametro, indifferentemente dai
     * caratteri upper o lower case.
     * @param otherWord la parola da confrontare.
     * @return true se le parole coincidono, false altrimenti.
     */
    public boolean equals(String otherWord) {
        String tmp = setStandardWord(otherWord);
        return word.equals(tmp);
    }

    /**
     * Restituisce una stringa secondo il formato "Parola validità duplicata punteggio", come "Test true false 1".
     * @return la stringa rappresentativa dell'oggetto.
     */
    public String toString() {
        return word + " " + valid + " " + duplicate + " " + score;
    }

    /**
     * Setta una nuova parola da analizzare passata come parametro e ne ricalcola i punteggi, impostando duplicate
     * a false.
     * @param word la nuova parola da analizzare.
     */
    public void setWord(String word) {
        initialize(word);
    }

    /**
     * Setta una nuova lingua con cui analizzare la parola e ricalcolare i punteggi.
     * @param newLanguage
     */
    public void setLanguage(Language newLanguage) {
        language = newLanguage;
        initialize(word);
    }

    /**
     * Setta, in base al booleano passato come parametro, la parola come duplicata o meno. Se true, il punteggio si azzera.
     * Se false, il punteggio si ripristina. Di default è false.
     * @param b il booleano con cui settare duplicate.
     */
    public void setDuplicate(boolean b) {
        this.duplicate = b;

        if(duplicate)
            score = 0;
        else {
            if(valid)
                setScore();
        }
    }

    /*-----Private methods-----*/
    private void initialize(String s) {
       word = setStandardWord(s);
       setValid();
       setDuplicate(false);
    }

    private void setValid() {
        try {
            valid = DictionaryManager.isValid(word, language);
        } catch (DictionaryException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        if(valid)
            setScore();
        else
            score = 0;
    }

    private void setScore() {
        if(valid && !duplicate) {
            int length = word.length();

            switch(length) {
                case 3:
                case 4:
                    score = 1;
                    break;
                case 5:
                    score = 2;
                    break;
                case 6:
                    score = 3;
                    break;
                case 7:
                    score = 5;
                    break;
                default:
                    if(length < 3)
                        score = 0;
                    else
                        score = 11;
            }
        }
    }

    // Lo standard della parola è prima lettera maiuscola, successive minuscole.
    private String setStandardWord(String s) {
        String tmp = "";

        if(s.length() > 0) {
            tmp = s.substring(0, 1).toUpperCase() + s.substring(1, s.length()).toLowerCase();
        } else
            tmp = s;

        return tmp;
    }

}
