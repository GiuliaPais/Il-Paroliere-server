package server.match;

public class Main {

	public static void main(String[] args) {
		
		Grid grid = new Grid();
		
		grid.throwDices();
		String[] results = grid.generateGrid();
		
		for(String s : results)
			System.out.print(s + " ");
	}

}
