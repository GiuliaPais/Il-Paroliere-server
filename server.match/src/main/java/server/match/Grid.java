package server.match;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

import uninsubria.server.dice.DiceSet;

public class Grid implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final static int SIZE = 16;
	
	private DiceSet dices;
	private ArrayList<String> pool;
	private String[] diceFaces = new String[SIZE];
	
	public Grid() {
		
		try {
			dices = new DiceSet();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		
		pool = new ArrayList<String>();
	}
	
	/**
	 * Genera la griglia su cui vengono disposti randomicamente i risultati del lancio dei dadi
	 * @return un array di stringhe contenente in maniera randomica i risultati del lancio dei dadi
	 */
	public String[] generateGrid() {
		this.fillPool();
		Random rdm = new Random();
		
		for(int i = 0; i < SIZE; i++) {
			int pos = rdm.nextInt(pool.size());
			diceFaces[i] = pool.get(pos);
			pool.remove(pos);
		}
		
		return diceFaces;
	}
	
	/**
	 * Lancia i dadi
	 */
	public void throwDices() {
		dices.throwDices();
	}
	
	// Per riempire la pool coi risultati del lancio dei dadi
	private void fillPool() {
		String[] faces = dices.getResultFaces();
		
		for(int i = 0; i < SIZE; i++) {
			pool.add(faces[i]);
		}
	}
	
	
}
