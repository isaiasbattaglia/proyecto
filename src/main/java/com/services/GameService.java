package trivia;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;

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
        changeTurn(isPlayerOne,game);
      }
    }
    else{
      if(correct)
        game.setQuestionsCorrect2(game.getQuestionsCorrect2()+1);
      else{
        game.setQuestionsIncorrect2(game.getQuestionsIncorrect2()+1);
        changeTurn(isPlayerOne,game);
      }
    }
  }

  private static void changeTurn(boolean playerOne, Game game){
    String currentState= game.getState();
    if (currentState.equals("Turn1")) {
      game.setState("Turn2");
    }
    else if (currentState.equals("Turn2")) {
      game.setState("Turn1");
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

  public static List<String> getMissingCategories(Integer userID, Integer gameID){
    if(isPlayerOne(gameID,userID))
      return generateList(gameID,2);      
    else
      return generateList(gameID,1);      
  }

  private static List<String> generateList(Integer gameID, Integer missingCartegories){
    Game game = getGame(gameID);
    List<String> lst = new ArrayList<String>();
    if (game.getHistory().compareTo(missingCartegories)==0 || game.getHistory().compareTo(0)==0)
      lst.add("Historia");
    if (game.getSports().compareTo(missingCartegories)==0 || game.getSports().compareTo(0)==0)
      lst.add("Deportes");
    if (game.getGeografy().compareTo(missingCartegories)==0 || game.getGeografy().compareTo(0)==0)
      lst.add("Geografia");
    if (game.getEntreteniment().compareTo(missingCartegories)==0 || game.getEntreteniment().compareTo(0)==0)
      lst.add("Entretenimiento");
    if (game.getArt().compareTo(missingCartegories)==0 || game.getArt().compareTo(0)==0)
      lst.add("Arte");
    if (game.getScience().compareTo(missingCartegories)==0 || game.getScience().compareTo(0)==0)
      lst.add("Ciencia");
    return lst;
  }

  /**
   * This method allows set in game table a new winner of a category.
   * @param QuestionID the id of the question answered by the user.
   * @param GameID the id of the game where user plays.
   * @param ChooseCategory indicate if an user choosed a category or not.
   * @param playerOne true iff user is a player 1 in game.
   * @pre. true.
   * @post. database updated with the new winner of category.
  */
  public static void winACategory(Integer questionID, Integer gameID, boolean chooseCategory, boolean playerOne){
    if(chooseCategory){
      Question question = Question.findById(questionID);
      Category category = question.getCategory();
      String categoryName = category.getTCategory();
      if(playerOne)
        newWinnerOfCategory(categoryName, gameID,true);
      else
        newWinnerOfCategory(categoryName, gameID, false);
    }
  }

  /**
   * This method returns the current winner of a category given in the game.
   * @param name name of category.
   * @param GameID the id of the game where user plays.
   * @pre. true.
   * @return an integer value, representing current winner of category
   * this value is 1 if player 1 is the winner, 2 if the player 2 is the winner and 3 if both are the winners.
   * @post. an integer value, representing current winner of category, is returned.
  */
  private static Integer currentWinnerOfCategory(String name, Integer gameID)
    {return (getGame(gameID)).getCurrentWinnerOfCategory(name);}

  /**
   * This method is a support of a winACategory method, allows set in game table a new winner of a category.
   * @param CategoryName name of category wich user won.
   * @param GameID the id of the game where user plays.
   * @param playerOne true iff user is a player 1 in game.
   * @pre. true.
   * @post. database updated with the new winner of category.
  */
  private static void newWinnerOfCategory(String categoryName, Integer gameID, boolean playerOne){
    Integer currentWinnerOfCategory = (getGame(gameID)).getCurrentWinnerOfCategory(categoryName);
    if(currentWinnerOfCategory.compareTo(2)==0 || currentWinnerOfCategory.compareTo(1)==0)
        getGame(gameID).setNewWinnerOfCategory(categoryName,3); //Significates a draw
    else{
      if(playerOne) 
        getGame(gameID).setNewWinnerOfCategory(categoryName,1); 
      else
        getGame(gameID).setNewWinnerOfCategory(categoryName,2); 
    }
  }

  public static JSONObject obtainDuelResultsOfUser(Game game, boolean playerOne){
    JSONObject json = new JSONObject();
    boolean  draw = game.getQuestionsCorrect1().compareTo(game.getQuestionsCorrect2())==0;

    if(playerOne){
      boolean  win1 = game.getQuestionsCorrect1().compareTo(game.getQuestionsCorrect2())>0;
      List<Integer> user1Results = getUserResult(game,true);
      json.put("win",win1);
      json.put("correct",user1Results.get(0));
      json.put("wrong",user1Results.get(1));
    }
    else{
      boolean  win2 = game.getQuestionsCorrect2().compareTo(game.getQuestionsCorrect1())>0;
      List<Integer> user2Results = getUserResult(game,false);
      json.put("win",win2);
      json.put("correct",user2Results.get(0));
      json.put("wrong",user2Results.get(1));
    }
    json.put("msg","showFinalResults");
    json.put("draw", draw);
    return json;
  }

  public static Game createDuelGame(Integer id1, Integer id2){
    return new Game(id1,id2,"Duel");
  } 
  public static void updateDuelGame(Game game, boolean correct, boolean player1){
    if(player1){
      if(correct)
        game.setQuestionsCorrect1(game.getQuestionsCorrect1()+1);
      else
        game.setQuestionsIncorrect1(game.getQuestionsIncorrect1()+1);
    }
    else{
     if(correct)
        game.setQuestionsCorrect2(game.getQuestionsCorrect2()+1);
      else
        game.setQuestionsIncorrect2(game.getQuestionsIncorrect2()+1);     
    }
  }
  public static List<Game> getDuelGames(Integer userID){
    List<Game> l1 = Game.where("user1_id=? and mode=? and state is null",userID,"Duel");
    List<Game> games2 = Game.where("mode=? and user2_id=? and state is null","Duel",userID);
    games2.addAll(l1);
    return games2;
  }

  public static List<Integer> getUserResult(Game game, boolean player1){
    List<Integer> list = new ArrayList<Integer>();
    if(player1){
      list.add(0,game.getQuestionsCorrect1());
      list.add(1,game.getQuestionsIncorrect1());
    }
    else{
      list.add(0,game.getQuestionsCorrect2());
      list.add(1,game.getQuestionsIncorrect2());   
    }
    return list;
  }
  public static void setAnswerForUser(Integer gameID, Integer id1, String answer){
    Game game = getGame(gameID);
    if(isPlayerOne(gameID, id1)){
      game.setUser1Answer(answer);
    }
    else{
      game.setUser2Answer(answer);
    }
  }
}