package trivia;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;

public class GameService{

	 /**
   * This method returns a list of games in which it is the turn of a given user.
   * @param user_Id a id of the user.
   * @pre. true.
   * @return a list of the games in wich it is the turn of a given user.
   * @post. a list of the games in wich it is the turn of a given user, is returned.
  */
	public static List<Game> getGames(Integer user_Id){
		List<Game> l1= Game.where("user1_id = ? and state=?", user_Id, "Turn1");
    List<Game> l2= Game.where("user2_id = ? and state=?", user_Id, "Turn2");
    l1.addAll(l2);
    return l1;
	}

   /**
   * This method returns a list of the games that the user finalized.
   * @param user_Id a id of the user.
   * @pre. true.
   * @return a list of the games that the user finalized.
   * @post. a list of the games that the user finalized, is returned.
  */
  public static List<Game> getFinalizedGames(Integer user_Id){
    List<Game> l3= Game.where("user2_id = ? and state=?", user_Id, "Finalized");
    List<Game> l4= Game.where("user1_id = ? and state=?", user_Id, "Finalized");
    l3.addAll(l4);
    return l3;
  }

   /**
   * This method allows create a turn mode game.
   * @param id a id of the user.
   * @pre. true.
   * @return The game created recently.
   * @post. database updated with the new game in turn mode.
  */
	public static Game createGame(Integer id){
		return new Game(id);
	} 

   /**
   * This method returns a game that corresponds to the id.
   * @param id a id of the game.
   * @pre. true.
   * @return a game.
   * @post. a game that corresponds to the id, is reurned.
  */
 	public static Game getGame(Integer id){
    	return Game.findById(id);
  	}

   /**
   * This method returns a last game created by the user.
   * @param id_u a id of the user.
   * @pre. true.
   * @return a last game created by the user.
   * @post. a last game created by the user, is reurned.
  */
  	public static Game getLastGame(Integer id_u){
    	return Game.findFirst("user1_id=? order by id desc", id_u);
  	}

   /**
   * This method allows update a game depending on whether the user responds well or badly,
   * and in case it is necessary to change the game's turn.
   * @param game a game where the user plays.
   * @param correct a boolean value indicating if an user answered well or badly a question.
   * @param isPlayerOne true iff user is a player 1 in game.
   * @pre. true.
   * @post. database updated.
  */
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

   /**
   * This method allows change the game's turn.
   * @param playerOne true iff user is a player 1 in game.
   * @param game a game where the user plays.
   * @pre. true.
   * @post. database updated.
  */
  private static void changeTurn(boolean playerOne, Game game){
    String currentState= game.getState();
    if (currentState.equals("Turn1")) {
      game.setState("Turn2");
    }
    else if (currentState.equals("Turn2")) {
      game.setState("Turn1");
    }
  }

   /**
   * This method allows create a turn mode game.
   * @param id1 a id of the user that will be a player 1.
   * @param id2 a id of the user that will be a player 2.
   * @pre. true.
   * @return The game created recently.
   * @post. database updated with the new game in turn mode.
  */
  public static Game createGame(Integer id1, Integer id2){
    return new Game(id1,id2);
  } 

   /**
   * This method returns the rival of an user in the game
   * @param GameID id of the game where the user plays.
   * @param userID id of the user.
   * @pre. true.
   * @return a User object that is the rival of an user in the game.
   * @post. a User object that is the rival of an user in the game, is returned.
  */
  public static User getRival(Integer gameID, Integer userID){
    if (isPlayerOne(gameID,userID)) {
      return getPlayer(gameID,2);
    }
    else{
      return getPlayer(gameID,1);
    }
  }

   /**
   * This method returns true iff a given user is player 1 in the game.
   * @param GameID id of the game where the user plays.
   * @param userID id of the user.
   * @pre. true.
   * @return a boolean value, representing if the given user is player 1 or not.
   * @post. a boolean value, representing if the given user is player 1 or not, is returned.
  */
  public static boolean isPlayerOne(Integer gameID, Integer userID){
    Game game= getGame(gameID);
    Integer id1=game.getUser1Id(); 
    return (userID.compareTo(id1))==0;
  } 

   /**
   * This method returns player 1 or player 2 of the game.
   * @param GameID id of the game where the user plays.
   * @param number an Integer value representing the player who must be returned.
   * @pre. true.
   * @return a User Object representing the player 1 or player 2, depending the number,
   * this user object represents the player 1 if the number is 1
   * otherwise, if the number is 2, the user object represents the player 2.
   * @post. a user Object representing the player 1 or player 2, is returned.
  */
  private static User getPlayer(Integer gameID,Integer number){
    Game game= getGame(gameID);
    Integer id;
    if(number.compareTo(1)==0)
      id=game.getUser1Id();
    else
      id=game.getUser2Id();
    return User.findById(id); 
  }

   /**
   * This method returns the winner of the game.
   * @param GameID id of the game where the user plays.
   * @pre. true.
   * @return an Integer value, representing the winning user,
   * this value is 1 if the player 1 is the winner,
   * 2 if the player 2 is the winner,
   * and -1 if a tie has ocurred.
   * @post. an Integer value, representing the winning user, is returned.
  */
  public static Integer getUserWinner(Integer gameID){
    Game game = getGame(gameID);
    Integer user1Categories = game.getAmountOfCategories1();
    Integer user2Categories = game.getAmountOfCategories2();
    if(user1Categories.compareTo(user2Categories)==0){
      return -1;
    }
    else if (user1Categories.compareTo(user2Categories)>0)
      return game.getUser1Id();
    else
      return game.getUser2Id();
  }

   /**
   * This method returns a list that contains the categories that a user needs to win the game.
   * @param userID id of the user.
   * @param gameID id of the game where the user plays.
   * @pre. true.
   * @return a list that contains the categories that a user needs to win the game.
   * @post. a list that contains the categories that a user needs to win the game, is returned.
  */
  public static List<String> getMissingCategories(Integer userID, Integer gameID){
    if(isPlayerOne(gameID,userID))
      return generateList(gameID,2);      
    else
      return generateList(gameID,1);      
  }

   /**
   * This method  is a support method for getMissingCategories method,
   * This method returns a list that contains the categories that a user needs to win the game.
   * @param gameID id of the game where the user plays.
   * @param missingCategories an integer value representing a categories that the user not won,
   * if the user is player 1 this value should be 2, because the categories marked with 2 have been won by the player 2.
   * if the user is player 2 this value should be 1, because the categories marked with 1 have been won by the player 1. 
   * @pre. true.
   * @return a list that contains the categories that a user needs to win the game.
   * @post. a list that contains the categories that a user needs to win the game, is returned.
  */
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
   * This method allows set in game table a new winner of a category if the user chose a category.
   * @param QuestionID the id of the question answered by the user.
   * @param GameID the id of the game where user plays.
   * @param ChooseCategory indicate if an user chose a category or not.
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
    Game game= getGame(gameID);
    Integer currentWinnerOfCategory = game.getCurrentWinnerOfCategory(categoryName);
    if(currentWinnerOfCategory.compareTo(2)==0 || currentWinnerOfCategory.compareTo(1)==0){
        game.setNewWinnerOfCategory(categoryName,3); //Significates that player1 and player2 won this category,
        if(playerOne)
          game.setAmountOfCategories1(game.getAmountOfCategories1()+1);
        else
          game.setAmountOfCategories2(game.getAmountOfCategories2()+1);
    }
    else{
      if(playerOne){ 
        getGame(gameID).setNewWinnerOfCategory(categoryName,1); 
        game.setAmountOfCategories1(game.getAmountOfCategories1()+1);
      }
      else{
        getGame(gameID).setNewWinnerOfCategory(categoryName,2); 
        game.setAmountOfCategories2(game.getAmountOfCategories2()+1);
        }
    }
  }

  /**
   * This method returns a JSON Object containing a results of an user in duel mode game.
   * @param Game the game where user plays.
   * @param playerOne true iff user is a player 1 in game.
   * @pre. true.
   * @return a JSON Object containing a results of an user in duel mode game.
   * @post. a JSON Object containing a results of an user in duel mode game, is returned.
  */
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


  /**
   * This method allows create a game in mode duel.
   * @param id1 id of the user that will be a player 1.
   * @param id2 id of the user that will be a player 2.
   * @pre. true.
   * @return The game created recently.
   * @post. database updated with the new game in duel mode.
  */
  public static Game createDuelGame(Integer id1, Integer id2){
    return new Game(id1,id2,"Duel");
  } 

  /**
   * This method update a duel game.
   * @param game the game where the user plays.
   * @param correct a boolean value indicating if an user answered correctly or wrongly a question.
   * @param player1 true iff user is a player 1 in game.
   * @pre. true.
   * @post. database updated.
  */
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

  /**
   * This method returns a list that contains the duel mode games that have a given user as player1 or player2.
   * @param userID a user id.
   * @pre. true.
   * @return a list that contains the duel mode games that have a given user as player1 or player2.
   * @post. a list that contains the duel mode games that have a given user as player1 or player2, is returned.
  */
  public static List<Game> getDuelGames(Integer userID){
    List<Game> l1 = Game.where("user1_id=? and mode=? and state is null",userID,"Duel");
    List<Game> games2 = Game.where("mode=? and user2_id=? and state is null","Duel",userID);
    games2.addAll(l1);
    return games2;
  }

  /**
   * This method returns a list that contains the results of a given user in the game.
   * @param game a game where the user plays.
   * @param player1 true iff user is a player 1 in game.
   * @pre. true.
   * @return a list that contains the results of a given user in the game.
   * @post. a list that contains the results of a given user in the game, is returned.
  */
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

  /**
   * This method allows set the answer of a given user in the duel mode game.
   * @param gameID id of the game where the user plays.
   * @param id1 id of the user.
   * @param answer answer of the user.
   * @pre. true.
   * @post. database updated.
  */
  public static void setAnswerForUser(Integer gameID, Integer id1, String answer){
    Game game = getGame(gameID);
    if(isPlayerOne(gameID, id1)){
      game.setUser1Answer(answer);
    }
    else{
      game.setUser2Answer(answer);
    }
  }

  /**
   * This method returns true iff the user won the six categories.
   * @param gameID id of the game where the user plays.
   * @param playerOne a boolean value that indicates if the user is player 1.
   * @pre. true.
   * @post. a boolean value that indicates if the user won the six categories, is returned.
  */
 public static boolean sixthCategoryReached(Integer gameID, boolean playerOne){
    Game game = Game.findById(gameID);
    if (playerOne)
      return game.getAmountOfCategories1().compareTo(6)==0;
    else
      return game.getAmountOfCategories2().compareTo(6)==0;
 }
}