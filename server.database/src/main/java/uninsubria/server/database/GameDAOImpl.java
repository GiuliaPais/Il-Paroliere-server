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
public class GameDAOImpl implements GameDAO {
	
	private Connection con;
	private Game gm;
	public Connection getCon() {
		return con;
	}
	public String selectAllGame="SELECT * FROM Game";
	public String selectGame="SELECT * FROM Game WHERE UserId=?";
	public String selectGamebyPK="SELECT * FROM Game WHERE Game=?";
	public String updateGame="INSERT INTO Game(game," + " playerId, grid, date, match, word, requested," + 
			" duplicated," + " wrong," + " points," +") VALUES(?,?,?,?,?,?,?,?,?,?)";
	public String deleteGame="DELETE FROM Game WHERE Game=?";
	@Override
	public void create() {
		// TODO Auto-generated method stub
	}

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
				currGame.setGrid(rs.getArray(3));
				currGame.setDate(rs.getDate(4));
				currGame.setMatch(rs.getInt(5));
				currGame.setWord(rs.getString(6));
				currGame.setRequested(rs.getBoolean(7));
				currGame.setDuplicated(rs.getBoolean(8));
				currGame.setWrong(rs.getBoolean(9));
				currGame.setPoints(rs.getInt(10));
				
				listaGame.add(currGame);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listaGame;
	}

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
				game.setGrid(rs.getArray(3));
				game.setDate(rs.getDate(4));
				game.setMatch(rs.getInt(5));
				game.setWord(rs.getString(6));
				game.setRequested(rs.getBoolean(7));
				game.setDuplicated(rs.getBoolean(8));
				game.setWrong(rs.getBoolean(9));
				game.setPoints(rs.getInt(10));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return game;
	}

	@Override
	public void update(GameRule gameRule, GAMEInfo totalgame) {
		String gameId=gameRule.getGame();
		PreparedStatement statement;
			for(int i=0; i<totalgame.getnumMatch(); i++) {
				for(int j=0; j<gameRule.getNum_players(); j++) {
					for(int k=0; k<totalgame.getnumWord(); k++) {
						try {
						statement = con.prepareStatement(updateGame);
						statement.setString(1, gameId);
						statement.setString(2, totalgame.getPlayer(j));
						statement.setArray(3, totalgame.getGrid(i));
						statement.setDate(4, totalgame.getDate());
						statement.setInt(5, totalgame.getMatch(i));
						statement.setString(6, totalgame.getWord(k));
						statement.setBoolean(7, totalgame.isRequested(k));
						statement.setBoolean(8, totalgame.isDuplicated(k));
						statement.setBoolean(9, totalgame.isWrong(k));
						statement.setInt(10, totalgame.getPoints(k));
						statement.executeQuery();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
	}

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
