package uninsubria.server.dice;

import java.util.Random;

public interface DiceInterface {

	public void throwDice(Random generator);
	public int getDiceNo();
	public String[] getFaces();
	public String getResultFace();
	public boolean isThrown();
	public void setNotThrown();
}
