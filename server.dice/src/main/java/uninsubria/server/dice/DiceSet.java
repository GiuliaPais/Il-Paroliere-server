package uninsubria.server.dice;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class DiceSet {

	private static final int nOfDices = 16;
	private ArrayList<Dice> poolDices = new ArrayList<>();
	private Dice[] dices = new Dice[nOfDices];
	private DiceSetStandard standard = DiceSetStandard.STANDARD;
	boolean thrown;
	
	/**
	 * Costruttore del set di dadi. Al suo interno vengono inizializzati l'array dei dadi,
	 * la variabile booleana del lancio settata su <code>false</code>.
	 * @throws URISyntaxException 
	 */
	public DiceSet() throws IOException, URISyntaxException {
		dices = new StandardParser(standard).getDices();
		thrown = false;

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
	 * La "pool" di dadi viene riempita col set di dadi selezionato.
	 */
	public void fillPoolDices() {
		for(int i = 0; i < nOfDices; i++)
			poolDices.add(dices[i]);
	}
	
	public ArrayList<Dice> getPoolDices() {
		fillPoolDices();
		return poolDices;
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
