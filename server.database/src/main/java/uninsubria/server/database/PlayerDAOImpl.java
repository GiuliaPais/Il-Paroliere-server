/**
 * 
 */
package uninsubria.server.database;

import uninsubria.utils.business.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro
 *
 */
public class PlayerDAOImpl implements PlayerDAO{
	/*
	 * Modifiche di Giulia:
	 * - Modificato costruttore, rimosso campo del pool
	 * */
	private Connection con;
	private Player pl;
	
	//query della tabella Player
	
	private String selectAllPlayers="SELECT * FROM Player";
	private String selectPlayer="SELECT * FROM Player WHERE UserId=?";
	private String selectPlayerbyEmail="SELECT * FROM Player WHERE Email=?";
	private String createPlayer="INSERT INTO Player(UserID, Email, Log_Status, Name, Surname, Password, ProfileImage) " 
				             + "VALUES(?,?,?,?,?,?,?)";
	private String updatePlayer="UPDATE Player SET ? = ? WHERE UserId=?";
	private String deletePlayer="DELETE FROM Player WHERE UserId=?";
	private String modifyLogStatusIn="UPDATE Player SET Log_Status="+"true"+"WHERE UserId=?";
	private String modifyLogStatusOut="UPDATE Player SET Log_Status="+"false"+"WHERE UserId=?";
	
	public PlayerDAOImpl() throws SQLException {
		this.con = ConnectionPoolImpl.getInstance().getConnection();
    }
	
	/**
	 * crea una tupla nella tabella Player
	 */	
	@Override
	public void create() {
		// TODO Auto-generated method stub
		try {
			PreparedStatement statement = con.prepareStatement(createPlayer);
			statement.setString(1,pl.getPlayerID());
			statement.setString(2,pl.getEmail());
			statement.setBoolean(3,pl.isLogStatus());
			statement.setString(4,pl.getName());
			statement.setString(5,pl.getSurname());
			statement.setString(6,pl.getPassword());
			statement.setInt(7,pl.getProfileImage());;
			statement.executeQuery();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * preleva tutte le tuple dalla tabella Player
	 * 
	 */
	@Override
	public List<Player> getAll() {
		List<Player> listaGiocatori=new ArrayList<Player>();
		
		try {
			PreparedStatement statement = con.prepareStatement(selectAllPlayers);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				Player currPlayer = new Player();
				currPlayer.setPlayerID(rs.getString(1));
				currPlayer.setEmail(rs.getString(2));
				currPlayer.setLogStatus(rs.getBoolean(3));
				currPlayer.setName(rs.getString(4));
				currPlayer.setSurname(rs.getString(5));
				currPlayer.setPassword(rs.getString(6));
				
				listaGiocatori.add(currPlayer);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listaGiocatori;
	}

	/**
	 * preleva la tupla dalla tabella Game dato un Player id
	 * 
	 */
	@Override
	public Player getByUserId(String id) {
		Player player = null;
		try {
			PreparedStatement statement = con.prepareStatement(selectPlayer);
			statement.setString(1, pl.getPlayerID());
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				player = new Player();
				player.setPlayerID(rs.getString(1));
				player.setEmail(rs.getString(2));
				player.setLogStatus(rs.getBoolean(3));
				player.setName(rs.getString(4));
				player.setSurname(rs.getString(5));
				player.setPassword(rs.getString(6));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return player;
	}

	/**
	 * preleva la tupla dalla tabella Game data la mail di un Player
	 * 
	 */
	@Override
	public Player getByEmail(String email) {
		Player player = null;
		try {
			PreparedStatement statement = con.prepareStatement(selectPlayerbyEmail);
			statement.setString(1, pl.getEmail());
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				player = new Player();
				player.setPlayerID(rs.getString(1));
				player.setEmail(rs.getString(2));
				player.setLogStatus(rs.getBoolean(3));
				player.setName(rs.getString(4));
				player.setSurname(rs.getString(5));
				player.setPassword(rs.getString(6));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return player;
	}

	/**
	 * update di Player
	 * @param type
	 * @param value
	 * il metodo crea una query che modifica l'attributo(type) e il valore(value) 
	 */
	@Override
	public void update(String[] type, String[] value) {
		if(type.length!=value.length)
			return;//poi devo pensare ad una alternativa
		try {
			PreparedStatement statement;
			for (int i=0; i<type.length; i++) {
				while(type[i].equals("playerID"))
					i++;
				if(i>=type.length)
					break;
				statement = con.prepareStatement(updatePlayer);
				statement.setString(1, type[i]);
				statement.setString(2, value[i]);
				statement.executeQuery(); 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * delete di Player
	 * il metodo cancella una tupla dato il Player id 
	 */
	@Override
	public void delete() {
		try {
			PreparedStatement statement = con.prepareStatement(deletePlayer);
			statement.setString(1, pl.getPlayerID());
			statement.executeQuery(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * modifica il login (durante l'operazione di login)
	 */
	public void modifyLogin() {
		try {
			PreparedStatement statement = con.prepareStatement(modifyLogStatusIn);
			statement.setString(1,pl.getPlayerID());
			
			statement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * modifica il logout (durante l'operazione di logout)
	 */
	public void modifyLogout() {
		try {
			PreparedStatement statement = con.prepareStatement(modifyLogStatusOut);
			statement.setString(1,pl.getPlayerID());
			
			statement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
