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
	private ConnectionPoolImpl conn;	
	
	//query della tabella GameRule
	public String selectAllGameRule="SELECT * FROM GameRule";
	public String selectGameRule="SELECT * FROM GameRule WHERE game=?";
	public String createGameRule="INSERT INTO GameRule(game," + " num_player, ruleset, Language" +") VALUES(?,?,?,?)";
	public String updateGameRule="UPDATE GameRule SET ? = ? WHERE UserId=?";
	public String deleteGameRule="DELETE FROM GameRule WHERE game=?";
	
	public GameRuleDAOImpl() {
        try {
			this.con = conn.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public Connection getCon() {
		return con;
	}
	
	public GameRule getGr() {
		return gr;
	}

	public void setGr(GameRule gr) {
		this.gr = gr;
	}
	/**
	 * crea una tupla nella tabella Gamerule
	 * 
	 */
	public void create() {
		// TODO Auto-generated method stub
		try {
			PreparedStatement statement = con.prepareStatement(createGameRule);
			statement.setString(1, gr.getGame());
			statement.setInt(2, gr.getNum_players());
			statement.setString(3, gr.getRuleset());
			statement.setString(4, gr.getLanguage());
			statement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		}
	}
	/**
	 * preleva tutte le tuple dalla tabella GameRule
	 * 
	 */
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

	/**
	 * update di GameRule
	 * @param type
	 * @param value
	 * il metodo crea una query che modifica l'attributo(type) e il valore(value) 
	 */
	public void update(String[] type, String[] value) {
		if(type.length!=value.length)//controllo che non si desideri modificare un'atributo chiave
			return;//poi devo pensare ad una alternativa(sostanzialmente non si deve modifcare una chiave primaria)
		try {
			PreparedStatement statement;
			for (int i=0; i<type.length; i++) {
				while(type[i].equals("game"))
					i++;
				if(i>=type.length)
					break;
				statement = con.prepareStatement(updateGameRule);
				statement.setString(1, type[i]);
				statement.setString(2, value[i]);
				statement.executeQuery(); 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * delete di GameRule
	 * il metodo cancella una tupla dato il game id 
	 */
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
