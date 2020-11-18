package uninsubria.server.dice;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class DiceSet {

	private static final int nOfDices = 16;
	private Dice[] dices;
	private String[] lettersOccurrences;
	private DiceSetStandard standard = DiceSetStandard.STANDARD;
	private boolean thrown;
	private Random generator;
	
	/**
	 * Costruttore del set di dadi. Al suo interno vengono inizializzati l'array dei dadi,
	 * la variabile booleana del lancio settata su <code>false</code>.
	 * @throws URISyntaxException
	 */
	public DiceSet() throws IOException, URISyntaxException {
		dices = new StandardParser(standard).getDices();
		thrown = false;
		generator = new Random();

		setLettersOccurrences();
	}
	
	/**
	 * Ottieni il numero di dadi utilizzati per la partita.
	 * @return <code>int</code> contenente il numero di dadi utilizzati.
	 */
	public int getNOfDices() {
		return nOfDices;
	}
	
	/**
	 * Ottieni l'array contenente il set di dadi di gioco.
	 * @return <code>Array</code> contenente i dadi.
	 */
	public Dice[] getDices() {
		return dices;
	}
	
	/**
	 * Restituisce il valore della variabile booleana thrown. <code>True</code> se i dadi sono
	 * stati lanciati. <code>False</code> altrimenti.
	 */
	public boolean areThrown() {
		return thrown;
	}
	
	/**
	 * Lancia i dadi e setta la variabile thrown a <code>True</code>.
	 */
	public void throwDices() {
		for(Dice d : dices) {
			d.throwDice(generator);
		}
		thrown = true;
	}
	
	/**
	 * Setta la variabile thrown a <code>False</code> e resetta le facce dei dadi.
	 */
	public void setNotThrown() {
		for(Dice d : dices) {
			d.setNotThrown();
		}
		thrown = false;
	}

	/**
	 * Setta un nuovo standard di dadi da utilizzare.
	 * @param newStandard il nuovo standard di dadi da utilizzare.
	 */
	public void setDiceSetStandard(DiceSetStandard newStandard) {
		standard = newStandard;
	}
	
	/**
	 * Restituisce un array di stringhe contenenti i risultati del lancio dei dadi, se lanciati. Null altrimenti.
	 * @return Array di stringhe contenente i risultati del lancio.
	 */
	public String[] getResultFaces() {
		String[] faces = new String[dices.length];
		
		for(int i = 0; i < faces.length; i++) {
			faces[i] = dices[i].getResultFace();
		}
		
		return faces;
	}

	/**
	 * Restituisce un array di Integer contenenti i numeri dei dadi lanciati. Null altrimenti.
	 * @return Array di Integer contenente il numero dei dadi del lancio.
	 */
	public Integer[] getResultNumb() {
		Integer[] numb = new Integer[dices.length];
		
		for(int i = 0; i < numb.length; i++) {
			numb[i] = dices[i].getDiceNo();
		}
		
		return numb;
	}

	/**
	 * Restituisce un array di String contenente le singole lettere sulle facce dei dadi, partendo dal primo,
	 * senza ripetizioni.
	 * @return String[], un array di String con le occorrenze delle lettere.
	 */
	public String[] getLettersOccurrences() {
		return lettersOccurrences;
	}
	
	/**
	 * Restituisce una stringa contenente il numero ed i singoli valori delle facce dei dadi.
	 */
	public String toString() {
		String tmp = "";

		for(int i = 0; i < dices.length; i++) {
			if(i == dices.length -1)
				tmp += dices[i].toString();
			else
				tmp += dices[i].toString() + " - ";
		}

		return tmp;
	}
	
	/**
	 * Randomizza la posizione dei dadi lanciati.
	 */
	public void randomizePosition() {
		ArrayList<Dice> pool = new ArrayList<>(Arrays.asList(dices));
		
		for(int i = 0; i < dices.length; i++) {
			int pos = generator.nextInt(pool.size());
			dices[i] = pool.get(pos);
			pool.remove(pos);
		}
	}

	// Crea un array contenente le singole facce, senza ripetizioni, a partire dal primo dado.
	private void setLettersOccurrences() {
		ArrayList<String> occurrences = new ArrayList<>();

		for(Dice d : dices) {
			for(String s : d.getFaces()) {
				if(!occurrences.contains(s))
					occurrences.add(s);
			}
		}

		lettersOccurrences = new String[occurrences.size()];

		for(int i = 0; i < lettersOccurrences.length; i++)
			lettersOccurrences[i] = occurrences.get(i);
	}
	
}
