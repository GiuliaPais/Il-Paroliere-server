package uninsubria.server.match;

public interface ActiveMatchInterface extends MatchInterface {

	public Grid throwDices();
	public void calculateScore(); // da sistemare
	public void Conclude();
}
