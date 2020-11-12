
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
public class GameDAOImpl implements GameDAO {
	
	private Connection con;
	private Game gm;
	private GameRule gameRule; 
	private GAMEInfo totalgame;
	private ConnectionPoolImpl conn;	
	
	//query della tabella Game
	public String selectAllGame = "SELECT * FROM Game";
	public String selectGame = "SELECT * FROM Game WHERE UserId=?";
	public String selectGamebyPK = "SELECT * FROM Game WHERE Game=?";
	public String createGame = "INSERT INTO Game(game," + " playerId, grid, match, word, requested," + 
			" duplicated," + " wrong," + " points," +") VALUES(?,?,?,?,?,?,?,?,?,?)";
	public String updateGameRule = "UPDATE Game SET ? = ? WHERE UserId=?";
	public String deleteGame = "DELETE FROM Game WHERE Game=?";
	
	public GameDAOImpl() {
        try {
			this.con = conn.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	/**
	 * @param con
	 * @param gm
	 * @param gameRule
	 * @param totalgame
	 */
	public GameDAOImpl(Connection con, Game gm, GameRule gameRule, GAMEInfo totalgame) {
		super();
		this.con = con;
		this.gm = gm;
		this.gameRule = gameRule;
		this.totalgame = totalgame;
	}

	public Connection getCon() {
		return con;
	}
	
	//getter e setter di gameRule
	public GameRule getGameRule() {
		return gameRule;
	}
	public void setGameRule(GameRule gameRule) {
		this.gameRule = gameRule;
	}

	//getter e setter di totalgame
	public GAMEInfo getTotalgame() {
		return totalgame;
	}

	public void setTotalgame(GAMEInfo totalgame) {
		this.totalgame = totalgame;
	}
	
	/**
	 * crea una tupla nella tabella Game
	 * @param gamerule, totalgame
	 * 
	 */
	@Override
	public void create(GameRule gameRule, GAMEInfo totalgame) {
		String gameId=gameRule.getGame();
		PreparedStatement statement;
			for(int i=0; i<totalgame.getnumMatch(); i++) {
				for(int j=0; j<gameRule.getNum_players(); j++) {
					for(int k=0; k<totalgame.getnumWord(); k++) {
						try {
						statement = con.prepareStatement(createGame);
						statement.setString(1, gameId);
						statement.setString(2, totalgame.getPlayer(j));
						statement.setInt(3, totalgame.getMatch(i));
						statement.setString(4, totalgame.getWord(k));
						statement.setBoolean(5, totalgame.isRequested(k));
						statement.setBoolean(6, totalgame.isDuplicated(k));
						statement.setBoolean(7, totalgame.isWrong(k));
						statement.setInt(8, totalgame.getPoints(k));
						statement.executeQuery();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
	}

	/**
	 * preleva tutte le tuple dalla tabella Game
	 * 
	 */
	@Override
	public List<Game> getAll() {
		List<Game> listaGame=new ArrayList<Game>();
		
		try {
			PreparedStatement statement = con.prepareStatement(selectAllGame);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				Game currGame = new Game();
				currGame.setGame(rs.getString(1));
				currGame.setPlayerId(rs.getString(2));
				currGame.setMatch(rs.getInt(3));
				currGame.setWord(rs.getString(4));
				currGame.setRequested(rs.getBoolean(5));
				currGame.setDuplicated(rs.getBoolean(6));
				currGame.setWrong(rs.getBoolean(7));
				currGame.setPoints(rs.getInt(8));
				
				listaGame.add(currGame);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listaGame;
	}

	/**
	 * preleva la tupla dalla tabella Game dato un game id
	 * 
	 */
	@Override
	public Game getByPK(String id) {
		Game game = null;
		try {
			PreparedStatement statement = con.prepareStatement(selectGamebyPK);
			statement.setString(1, gm.getGame());
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				game = new Game();
				game.setGame(rs.getString(1));
				game.setPlayerId(rs.getString(2));
				game.setMatch(rs.getInt(3));
				game.setWord(rs.getString(4));
				game.setRequested(rs.getBoolean(5));
				game.setDuplicated(rs.getBoolean(6));
				game.setWrong(rs.getBoolean(7));
				game.setPoints(rs.getInt(8));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return game;
	}

	/**
	 * update di GameRule
	 * @param type
	 * @param value
	 * il metodo crea una query che modifica l'attributo(type) e il valore(value) 
	 */
	@Override
	public void update(String[] type, String[] value) {
		if(type.length!=value.length)//controllo che non si desideri modificare un'atributo chiave
			return;//poi devo pensare ad una alternativa(sostanzialmente non si deve modifcare una chiave primaria)
		try {
			PreparedStatement statement;
			for (int i=0; i<type.length; i++) {
				while(type[i].equals("game") || type[i].equals("PlayerId") || type[i].equals("match") || type[i].equals("word"))
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
	 * delete di Game
	 * il metodo cancella una tupla dato il game id 
	 */
	@Override
	public void delete() {
		try {
			PreparedStatement statement = con.prepareStatement(deleteGame);
			statement.setString(1, gm.getGame());
			statement.executeQuery(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
