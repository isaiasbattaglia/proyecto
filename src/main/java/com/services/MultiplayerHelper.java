package trivia;
import org.javalite.activejdbc.Base;
import java.util.List;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;

public class MultiplayerHelper{
  //Users online in the game.
  static Map<Session,User> userUsernameMap = new ConcurrentHashMap<>();
  //Users online in the duel lobby.
  static Map<Session,User> usersInDuelLobby = new ConcurrentHashMap<>();

   /**
   * This method sends a message from one user to their rival in the game.
   * @param rivalID id of the rival.
   * @pre. true.
  */
  public static void broadcastMessage(Integer rivalID) {
    Session rival = getKeyByValue(userUsernameMap, User.findById(rivalID));
    try {
      rival.getRemote().sendString(String.valueOf(new JSONObject().put("msg","UpdateTurn")));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

   /**
   * This method updates a list of online users.
   * @param message message that indicates if the online users list in waiting Room must be updated, or online
   * users list in fightLobby must be updated.
   * @pre. true.
   * @Post Online users list, in waiting room or fight lobby as appropiated, is updated.
  */
  public static void updateOnlineUsers(String message) {
    if(message.equals("updateOnlineUsers")){
      userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
      try {
        session.getRemote().sendString(String.valueOf(new JSONObject().put("userlist",userUsernameMap.values()).put("msg",message)));
        } catch (Exception e) {
          e.printStackTrace();
        }
      });        
    }
    else{
      usersInDuelLobby.keySet().stream().filter(Session::isOpen).forEach(session -> {
      try {
        session.getRemote().sendString(String.valueOf(new JSONObject().put("userlist",usersInDuelLobby.values()).put("msg",message)));
        } catch (Exception e) {
          e.printStackTrace();
        }
      });   
    }
  }

   /**
   * This method reports to the requester user that the requested user rejected a petition of game.
   * @param userID id of the requester user.
   * @pre. true.
   * @Post Message to the requester user is sended.
  */
  public static void ReqRejected(Integer userID){
    Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
    Session requesterUser = getKeyByValue(userUsernameMap, User.findById(userID));
    Base.close();
    try {
      requesterUser.getRemote().sendString(String.valueOf(new JSONObject().put("msg","ReqRejected")));
      } catch (Exception e) {
        e.printStackTrace();
    }
  }

   /**
   * This method returns the key in the map that corresponds to the value.
   * @param map a map.
   * @param user a user who needs know his key.
   * @pre. true.
   * @return. A key in the map that corresponds to the value.
   * @Post A key in the map that corresponds to the value, is returned.
  */
  public static Session getKeyByValue(Map<Session,User> map, User user){
   for (Map.Entry<Session, User> entry : map.entrySet()) {
      if (user.getInteger("id").equals((entry.getValue()).getInteger("id"))) {
          return entry.getKey();
      }
    }
    return null;
  }

   /**
   * This method sends a waiting message to requester user, and 
   * send a notification to accept or reject the request to the requested user. 
   * @param userID a requester user.
   * @param rivalID a requested user.
   * @pre. true.
   * @Post a message is sended.
  */
  public static void sendGameReq(Integer userID, Integer rivalID) {
    Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
    Session requesterUser = getKeyByValue(userUsernameMap, User.findById(userID));
    Session requestedUser = getKeyByValue(userUsernameMap, User.findById(rivalID));
    Base.close();
    try {
      requesterUser.getRemote().sendString(String.valueOf(new JSONObject().put("msg","wait").put("rivalID",rivalID)));
      requestedUser.getRemote().sendString(String.valueOf(new JSONObject().put("requesterUser",userID).put("msg","acceptReject")));
      } catch (Exception e) {
        e.printStackTrace();
    }
  }

   /**
   * This method sends a new question to the 2 users in the game. 
   * @param gameID a id of the game where the users play.
   * @pre. true.
   * @Post a new question to the 2 users in the game, is sended.
  */
  public static void sendQuestion(Integer gameID){
    Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
    Game game = Game.findById(gameID);
    //If there is no a current question in the game, set and send new.
    if(game.getCurrentQuestion()==null){ 
        game.setUser1Answer(null); game.setUser2Answer(null);
        Session u1 = getKeyByValue(userUsernameMap, User.findById(game.getUser1Id()));
        Session u2 = getKeyByValue(userUsernameMap, User.findById(game.getUser2Id()));
        JSONObject newQuestion = generateQuestionJSON(game);
        Base.close();
        try{
          u1.getRemote().sendString(String.valueOf(newQuestion));
          u2.getRemote().sendString(String.valueOf(newQuestion));
        } catch(Exception e){
          e.printStackTrace();
        }   
    }
    else
      Base.close();
  }

   /**
   * This method generates a JSON with the new question to be sent.
   * @param game a game where the current users play.
   * @pre. true.
   * @return. a JSONObject that contains a new question to be sent.
   * @Post a JSONObject that contains a new question to be sent, is returned.
  */
  private static JSONObject generateQuestionJSON(Game game){
    JSONObject json = new JSONObject();
    Category category = CategoryService.randomCategoryForDuel();
    Question question = category.getQuestion();
    game.setCurrentQuestion(question.getDescription());
    List<String> options = QuestionService.randomAnswers(question);
    json.put("msg","showQuestion");
    json.put("gameID",game.getInteger("id"));
    json.put("questionID",question.getInteger("id"));
    json.put("question",question.getDescription());
    json.put("option1", options.get(0));
    json.put("option2", options.get(1));
    json.put("option3", options.get(2));
    json.put("option4", options.get(3));      
    return json;
  }

   /**
   * This method informs users if they answered correctly or incorrectly, and if the game ends, the winner is informed.
   * If any user does not respond, this method does nothing.
   * @param json a JSONObject that contains a currentUserID, gameID, QuestionID and user's answer.
   * @pre. true.
   * @Post database updated.
  */
  public static void sendResults(JSONObject json){
    Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
    Game game = Game.findById(json.getInt("gameID"));
    Question question = Question.findById(json.getInt("questionID"));
    game.setCurrentQuestion(null);
    //If no user answered, return.
    if(game.getUser2Answer()==null || game.getUser1Answer()==null){
      Base.close();return;
    }
    else{
      game.setRound(game.getRound()+1);
      boolean correct1 = isCorrect(question,game.getUser1Answer());
      boolean correct2 = isCorrect(question,game.getUser2Answer());
      GameService.updateDuelGame(game,correct1,true);
      GameService.updateDuelGame(game,correct2,false);
      boolean finalizedGame = game.getRound().compareTo(game.getTotalRounds())==0;
      Base.close();
      if(finalizedGame)
          sendFinalResults(correct1,correct2,game);
      else
          sendMessages(correct1,correct2,game);
    }
  }
   /**
   * This method return if the user's answer is correct.
   * @param question A question that the user answered.
   * @param userAnswer user's answer.
   * @pre. true.
   * @return. true iff the user responded correctly.
   * @Post a Boolean value that indicates whether the user responded correctly, is returned.
  */
  private static boolean isCorrect(Question question, String userAnswer){
    return question.getAnswer1().compareTo(userAnswer)==0;
  }


   /**
   * This method informs users if they answered correctly or incorrectly.
   * @param correct1 a boolean value that indicates if the user 1 responded correctly.
   * @param correct2 a boolean value that indicates if the user 2 responded correctly.
   * @param game a game where the users play.
   * @pre. true.
   * @Post a message that informs users if they answered correctly or incorrectly, is sended.
  */
  private static void sendMessages(boolean correct1, boolean correct2, Game game){
    Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
    Session u1 = getKeyByValue(userUsernameMap, User.findById(game.getUser1Id()));
    Session u2 = getKeyByValue(userUsernameMap, User.findById(game.getUser2Id()));
    Integer gameID = game.getInteger("id");
    Base.close();
    try{
      u1.getRemote().sendString(String.valueOf(new JSONObject()
        .put("msg","showResult").put("gameID",gameID).put("correct",correct1)));
      u2.getRemote().sendString(String.valueOf(new JSONObject()
        .put("msg","showResult").put("gameID",gameID).put("correct",correct2)));
    } catch(Exception e){
      e.printStackTrace();
    }         
  }

   /**
   * This method informs users of their final results, i.e , who won.
   * @param correct1 a boolean value that indicates if the user 1 responded correctly.
   * @param correct2 a boolean value that indicates if the user 2 responded correctly.
   * @param game a game where the users play.
   * @pre. true.
   * @Post database updated.
  */
  private static void sendFinalResults(boolean correct1, boolean correct2, Game game){
    Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
    game.setState("Finalized");
    Session u1 = getKeyByValue(userUsernameMap, User.findById(game.getUser1Id()));
    Session u2 = getKeyByValue(userUsernameMap, User.findById(game.getUser2Id()));
    JSONObject user1Results = GameService.obtainDuelResultsOfUser(game,true);
    JSONObject user2Results = GameService.obtainDuelResultsOfUser(game,false);
    Base.close();
    try{
      u1.getRemote().sendString(String.valueOf(user1Results));
      u2.getRemote().sendString(String.valueOf(user2Results));
    } catch(Exception e){
      e.printStackTrace();
    }         
  }

   /**
   * This method informs the rivals of a given user, that he abandoned the game.
   * @param user user that abandoned the game.
   * @pre. true.
   * @Post a message that informs the rivals of a given user, that he abandoned the game, is sended.
  */
  public static void leaveTheGame(User user){
    Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
    List<Game> duelGames = GameService.getDuelGames(user.getInteger("id"));
    List<User> rivals = new LinkedList<User>();
    List<Session> rivalSession = new LinkedList<Session>();
    for(Game game : duelGames){
      rivals.add(GameService.getRival(game.getInteger("id"),user.getInteger("id")));
      game.setState("Finalized");
    }
    for(User u : rivals)
      rivalSession.add(getKeyByValue(userUsernameMap,u));
    Base.close();
    rivalSession.stream().filter(Session::isOpen).forEach(session -> {
          try {
              session.getRemote().sendString(String.valueOf(new JSONObject().put("msg","leaveTheGame")));
          } catch (Exception e) {
              e.printStackTrace();
          }
      });
  }

   /**
   * This method informs the requested users that the requester user cancel the petition of game.
   * @param requestedUser requestedUser.
   * @pre. true.
   * @Post a message that informs the requested users that the requester user cancel the petition of game, is sended.
  */
  public static void cancelReq(Integer requestedUser){
    Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
    Session requestedUser2 = getKeyByValue(userUsernameMap, User.findById(requestedUser));
    Base.close();
    try{
      requestedUser2.getRemote().sendString(String.valueOf(new JSONObject().put("msg","gameReqCanceled")));
    } catch(Exception e){
      e.printStackTrace();
    }   
  }
}