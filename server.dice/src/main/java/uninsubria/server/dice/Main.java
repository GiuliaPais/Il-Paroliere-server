package uninsubria.server.dice;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

public class Main {

	public static void main(String[] args) throws IOException, URISyntaxException {
		// TODO Auto-generated method stub

		DiceSet dices = new DiceSet();
		Random rdm = new Random();
		
		// La griglia
		String[] grid = new String[dices.getNOfDices()];
		// Il pool di dadi
		ArrayList<Dice> pool = dices.getPoolDices();
		
		for(int i = 0; i < grid.length; i++) {
			Dice d = pool.get(rdm.nextInt(pool.size()));	// Estrai il dado dal pool
			d.throwDice(rdm);	// Lancia il dado
			grid[i] = d.getResultFace();	// Il risultato del lancio
		}
		
		System.out.println("Griglia:");
		for(String s : grid)
			System.out.print(s + " ");
		
	}

}
