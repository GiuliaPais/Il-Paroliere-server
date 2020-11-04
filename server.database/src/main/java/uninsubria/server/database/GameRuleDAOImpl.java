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
public class GameRuleDAOImpl implements GameRuleDAO {

	private Connection con;
	private GameRule gr;
	public Connection getCon() {
		return con;
	}
	public String selectAllGameRule="SELECT * FROM GameRule";
	public String selectGameRule="SELECT * FROM GameRule WHERE game=?";
	public String updateGame="INSERT INTO GameRule(game," + " num_player, ruleset, Language" +") VALUES(?,?,?,?)";
	public String deleteGameRule="DELETE FROM GameRule WHERE game=?";

	public GameRule getGr() {
		return gr;
	}

	public void setGr(GameRule gr) {
		this.gr = gr;
	}

	public void create() {
		// TODO Auto-generated method stub

	}

	public List<GameRule> getAll() {
		List<GameRule> listaGame=new ArrayList<GameRule>();
		
		try {
			PreparedStatement statement = con.prepareStatement(selectAllGameRule);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				GameRule currGR = new GameRule();
				currGR.setGame(rs.getString(1));
				currGR.setNum_players(rs.getInt(2));
				currGR.setRuleset(rs.getString(3));
				currGR.setLanguage(rs.getString(4));
				
				listaGame.add(currGR);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listaGame;
	}


	public void update() {
		// TODO Auto-generated method stub
		try {
			PreparedStatement statement = con.prepareStatement(updateGame);
			statement.setString(1, gr.getGame());
			statement.setInt(2, gr.getNum_players());
			statement.setString(3, gr.getRuleset());
			statement.setString(4, gr.getLanguage());
			statement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		}
	}

	public void delete() {
		// TODO Auto-generated method stub
		try {
			PreparedStatement statement = con.prepareStatement(deleteGameRule);
			statement.setString(1, gr.getGame());
			statement.executeQuery(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
