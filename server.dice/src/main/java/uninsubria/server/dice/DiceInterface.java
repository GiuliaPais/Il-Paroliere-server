package uninsubria.server.dice;

import java.util.Random;

public interface DiceInterface {

	void throwDice(Random generator);
	int getDiceNo();
	String[] getFaces();
	String getResultFace();
	boolean isThrown();
	void setNotThrown();
}
