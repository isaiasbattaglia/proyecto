package trivia;
import java.util.List;

public class GameService{
	
	public static List<Game> getGames(Integer user_Id){
		return Game.where("user_id = ? and state=?", user_Id, "En_Proceso");
	}

	public static Game createGame(Integer id){
		return new Game(id);
	} 
 
 	public static Game getGame(Integer id){
    	return Game.findById(id);
  	}

  	//Ver lo del id de usuario
  	public static Game getLastGame(Integer id_u){
    	return Game.findFirst("user_id=? order by id desc", id_u);
  	}

  public static void updateGame(Game game, boolean correct){
    if(correct)
      game.setQuestionsCorrect(game.getQuestionsCorrect()+1);
    else
      game.setQuestionsIncorrect(game.getQuestionsIncorrect()+1);
  }
}