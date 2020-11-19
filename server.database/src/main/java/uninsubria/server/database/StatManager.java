package uninsubria.server.database;

import java.util.ArrayList;
import java.util.Map;

public class StatManager {


	private ArrayList<Integer> Game;
	private ArrayList<String> letters;
	private ArrayList<Integer> occurences;
	private ArrayList<Integer> nMatch;
	private ArrayList<double> avg;
	
	
	/**
	 * @param String[]
	 * 
	 * Il metodo restituisce la lista delle occorrenze medie delle lettere di tutte le griglie passate per argomento.
	 * Ogni lettera e' associata ad una posizione dell'array e quindi anche le sue occorrenze inoltre per evitare di rifare il conteggio da capo ogni volta vengono salvati i riferimenti 
	 * 
	 * @return ArrayList<>
	 */
	public ArrayList<double> avg_grid_occourrencies(ArrayList<Integer> game, String[] grid, ArrayList<Integer> nMatch) {
		int index;
		for(int g: Game) {
			for(int i=0; i<grid.length; i++) {
				if(letters.isEmpty() || !letters.contains(grid[i]) || !game.contains(g)) {
					
					letters.add(grid[i]);
					index = letters.indexOf(grid[i]);
					occurences.set(index,occurences.get(index)+1);
					
				}else if(game.contains(g)){
					
					break;
					
				}else {
					
					index = letters.indexOf(grid[i]);
					occurences.set(index,occurences.get(index)+1);
					
				}
			}
			for(int j=0; j<letters.size(); j++) {
				
				avg.set(j, occurences.get(j) / nMatch.get(j));
				
			}
			
		}		
		return avg;
	}
	
}