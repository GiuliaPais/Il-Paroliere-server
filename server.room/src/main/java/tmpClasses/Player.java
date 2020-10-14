package tmpClasses;

import java.io.IOException;
import java.net.InetAddress;

public class Player {

	private InetAddress addr;
	
	public Player() {
		try {
			addr = InetAddress.getByName(null);
		} catch(IOException e) { }
	}
	
	public InetAddress getAddr() {
		return addr;
	}
}
