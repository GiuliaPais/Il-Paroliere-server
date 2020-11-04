/**
 * 
 */
package uninsubria.server.database;


/**
 * @author Alessandro
 *
 */
public class Player {
	
	private String playerID;
	private String email;
	
	private String Name, Surname, Password;
	private int profileImage;
	
	protected Player() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	/**
	 * @param email
	 * @param name
	 * @param surname
	 * @param password
	 * @param profileImage
	 * @param log_Status
	 */
	public Player(String playerID, String email, String name, String surname, String password, int profileImage, String log_Status) {
		super();

		this.playerID = playerID;
		this.email = email;
		Name = name;
		Surname = surname;
		Password = password;
		this.profileImage = profileImage;
		Log_Status = log_Status;
	}



	/**
	*
	*@param playerID, email, name, surname, password
	*
	**/	
	protected Player(String playerID, String email, String name, String surname, String password, int profileImage) {
		super();
		this.playerID = playerID;
		this.email = email;
		Name = name;
		Surname = surname;
		Password = password;
		this.profileImage = profileImage;
	}
	
	private String Log_Status;
	//private PlayerManager manager;
	
	

	public String getPlayerID() {
		return playerID;
	}
	public void setPlayerID(String playerID) {
		this.playerID = playerID;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	
	public String getSurname() {
		return Surname;
	}
	public void setSurname(String surname) {
		Surname = surname;
	}
	
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	
	public String getStatus() {
		return Log_Status;
	}
	public void setStatus(String status) {
		this.Log_Status = status;
	}
	
	public int getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(int profileImage) {
		this.profileImage = profileImage;
	}
	
}
