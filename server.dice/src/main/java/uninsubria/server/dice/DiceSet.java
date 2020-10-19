package uninsubria.server.dice;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;

public class DiceSet {

	private static final int nOfDices = 16;
	private Dice[] dices = new Dice[nOfDices];
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
	 * Restituisce una stringa contenente il numero ed i singoli valori delle facce dei dadi.
	 */
	public String toString() {
		String tmp = "";
		for(Dice d : dices)
			tmp += d.toString();
		return tmp;
	}
	
}