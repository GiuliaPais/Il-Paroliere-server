package uninsubria.server.scoreCounter;

import uninsubria.utils.languages.Language;

import java.util.ArrayList;

public class DuplicateWords {

    private ArrayList<WordAnalyzer> list, duplicatedWords;
    private Language language;

    /*-----Constructor-----*/
    public DuplicateWords(Language l) {
        list = new ArrayList<>();
        duplicatedWords = new ArrayList<>();
        language = l;
    }

    /*-----Methods-----*/
    /**
     * Aggiunge l'array di parole del tipo WordAnalyzer passato come parametro.
     * @param words le parole da aggiungere.
     */
    public void addWords(WordAnalyzer[] words) {
        for(WordAnalyzer wa : words) {
            if(!containsWord(wa))
                list.add(wa);
            else {
                this.getWord(wa).setDuplicate(true);
            }
        }
    }

    /**
     * Aggiunge l'array di parole del tipo String passato come parametro. Il metodo convertirà l'array di String
     * in array di WordAnalyzer.
     * @param words le parole da aggiungere.
     */
    public void addWords(String[] words) {
        WordAnalyzer[] tmp = fromStringToWA(words);
        this.addWords(tmp);
    }

    /**
     * Restituisce tutte le parole attualmente analizzate, senza ripetizioni.
     * @return la lista delle parole analizzate.
     */
    public ArrayList<WordAnalyzer> getWords() {
        return list;
    }

    /**
     * Restituisce tutte le doppie attualmente trovate, senza ripetizioni.
     * @return la lista di doppie.
     */
    public ArrayList<WordAnalyzer> getDuplicatedWords() {
        setDuplicatedWords();
        return duplicatedWords;
    }

    /**
     * Restituisce un array di String ricavato dall'ArrayList passato come parametro.
     * @param list l'ArrayList di parole.
     * @return un array di String.
     */
    public String[] convertToStringArray(ArrayList<WordAnalyzer> list) {
        String[] tmp = new String[list.size()];

        for(int i = 0; i < tmp.length; i++) {
            tmp[i] = list.get(i).toString();
        }

        return tmp;
    }

    /*-----Private methods-----*/
    // Per convertire da String[] a WordAnalyzer[].
    private WordAnalyzer[] fromStringToWA(String[] array) {
        WordAnalyzer[] tmp = new WordAnalyzer[array.length];

        for(int i = 0; i < array.length; i++)
            tmp[i] = new WordAnalyzer(array[i], language);

        return tmp;
    }

    // Per aggiungere le parole duplicate alla lista duplicateWords.
    private void setDuplicatedWords() {
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).isDuplicated())
                duplicatedWords.add(list.get(i));
        }
    }

    // Per controllare che la parola sia contenuta in lista. Il metodo list.contains(word) non funziona.
    private boolean containsWord(WordAnalyzer word) {
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).equals(word))
                return true;
        }

        return false;
    }

    // Per ottenere dalla lista la parola corrispondente.
    private WordAnalyzer getWord(WordAnalyzer word) {
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).equals(word))
                return list.get(i);
        }

        return null;
    }
}
