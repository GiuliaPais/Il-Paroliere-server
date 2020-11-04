/**
 * 
 */
package uninsubria.server.database;

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
	
	private Connection con;
	private Player pl;
	
	public Connection getCon() {
		return con;
	}
	private String selectAllPlayers="SELECT * FROM Player";
	private String selectPlayer="SELECT * FROM Player WHERE UserId=?";
	private String selectPlayerbyEmail="SELECT * FROM Player WHERE Email=?";
	private String updatePlayer="INSERT INTO Player(UserID, Email, Log_Status, Name, Surname, Password, ProfileImage) " 
				             + "VALUES(?,?,?,?,?,?,?)";
	private String deletePlayer="DELETE FROM Player WHERE UserId=?";
	private String modifyLogStatusIn="UPDATE Player SET Log_Status="+"online"+"WHERE UserId=?";
	private String modifyLogStatusOut="UPDATE Player SET Log_Status="+"offline"+"WHERE UserId=?";
	
	@Override
	public void create() {
		// TODO Auto-generated method stub
		
	}

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
				currPlayer.setStatus(rs.getString(3));
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
				player.setStatus(rs.getString(3));
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
				player.setStatus(rs.getString(3));
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

	@Override
	public void update() {
		try {
			PreparedStatement statement = con.prepareStatement(updatePlayer);
			statement.setString(1,pl.getPlayerID());
			statement.setString(2,pl.getEmail());
			statement.setString(3,pl.getStatus());
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
