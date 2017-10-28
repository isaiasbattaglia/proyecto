package trivia;
import java.util.List;

public class GameService{
	
	public static List<Game> getGames(Integer user_Id){
		List<Game> l1= Game.where("user1_id = ? and state=?", user_Id, "Turn1");
    List<Game> l2= Game.where("user2_id = ? and state=?", user_Id, "Turn2");
    l1.addAll(l2);
    return l1;
	}

  public static List<Game> getFinalizedGames(Integer user_Id){
    List<Game> l3= Game.where("user2_id = ? and state=?", user_Id, "Finalized");
    List<Game> l4= Game.where("user1_id = ? and state=?", user_Id, "Finalized");
    l3.addAll(l4);
    return l3;
  }

	public static Game createGame(Integer id){
		return new Game(id);
	} 
 
 	public static Game getGame(Integer id){
    	return Game.findById(id);
  	}

  	//Ver lo del id de usuario
  	public static Game getLastGame(Integer id_u){
    	return Game.findFirst("user1_id=? order by id desc", id_u);
  	}

  public static void updateGame(Game game, boolean correct, boolean isPlayerOne){
    if(isPlayerOne){
      if(correct)
        game.setQuestionsCorrect1(game.getQuestionsCorrect1()+1);
      else{ 
        game.setQuestionsIncorrect1(game.getQuestionsIncorrect1()+1);
        String currentState= game.getState();
        if (currentState.equals("Turn1")) {
          game.setState("Turn2");
        }
        else if (currentState.equals("Turn2")) {
          game.setState("Turn1");
        }
      }
    }
    else{
      if(correct)
        game.setQuestionsCorrect2(game.getQuestionsCorrect2()+1);
      else{
        game.setQuestionsIncorrect2(game.getQuestionsIncorrect2()+1);
        String currentState= game.getState();
        if (currentState.equals("Turn1")) {
          game.setState("Turn2");
        }
        else if (currentState.equals("Turn2")) {
          game.setState("Turn1");
        } 
      }
    }
  }

  public static Game createGame(Integer id1, Integer id2){
    return new Game(id1,id2);
  } 

  public static User getRival(Integer gameID, Integer userID){
    if (isPlayerOne(gameID,userID)) {
      return getPlayer(gameID,2);
    }
    else{
      return getPlayer(gameID,1);
    }
  }

  public static boolean isPlayerOne(Integer gameID, Integer userID){
    Game game= getGame(gameID);
    Integer id1=game.getUser1Id(); 
    return (userID.compareTo(id1))==0;
  } 

  private static User getPlayer(Integer gameID,Integer number){
    Game game= getGame(gameID);
    Integer id;
    if(number.compareTo(1)==0)
      id=game.getUser1Id();
    else
      id=game.getUser2Id();
    return User.findById(id); 
  }

  public static Integer getUserWinner(Integer gameID){
    Game game = getGame(gameID);
    Integer user1Answers = game.getQuestionsCorrect1();
    Integer user2Answers = game.getQuestionsCorrect2();
    if(user1Answers.compareTo(user2Answers)==0){
      return -1;
    }
    else if (user1Answers.compareTo(user2Answers)>0)
      return game.getUser1Id();
    else
      return game.getUser2Id();
  }
}