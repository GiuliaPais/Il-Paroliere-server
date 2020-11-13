package uninsubria.server.match;

public class GridTest {

	public static void main(String[] args) {
		
		Grid grid = new Grid();
		
		grid.throwDices();
		show(grid.generateGridString());
		show(grid.generateGridNumber());
		
		grid.throwDices();
		show(grid.generateGridString());
		show(grid.generateGridNumber());
		

	}
	
	private static void show(Object[] o) {
		String tmp = "";
		for(int i = 0; i < o.length; i++)
			tmp += o[i] + " ";
		
		System.out.print(tmp);
		System.out.println();
	}

}
