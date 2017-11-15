package trivia;
import org.javalite.activejdbc.Base;
import trivia.User;
import java.util.List;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;
import static j2html.TagCreator.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;
import java.util.LinkedList;

public class App{
  static Map<Session,User> userUsernameMap = new ConcurrentHashMap<>();
  static Map<Session,User> usersInDuelLobby = new ConcurrentHashMap<>();

  public static void main( String[] args ){

    staticFileLocation("/public");

    webSocket("/onlineGame", MultiplayerWebSocket.class);init();

    before((req, res)->{Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");});
    
    after((req, res) -> {Base.close();});

    get("/", (req,res) -> {return new ModelAndView(new HashMap(),"./views/home.mustache");},new MustacheTemplateEngine());

    get("/login", UserController::userLogin,new MustacheTemplateEngine());

    get("/registrar",UserController::registerUser,new MustacheTemplateEngine());

    post("/verificar",UserController::registeringUser, new MustacheTemplateEngine());

    get("/logout",UserController::userLogout,new MustacheTemplateEngine());

    get("/games", GameController::gameHome, new MustacheTemplateEngine());

    post("/newGame", GameController::newGame, new MustacheTemplateEngine());
    
    get("/play",GameController::play, new MustacheTemplateEngine());  
    
    get("/questions", QuestionController::question, new MustacheTemplateEngine());

    post("/answer", QuestionController::answerQuestion, new MustacheTemplateEngine());
  
    get("/finalizedGame",GameController::finalizedGame, new MustacheTemplateEngine());

    get("/profile", UserController::profile, new MustacheTemplateEngine());

    get("/ranking", UserController::ranking, new MustacheTemplateEngine());

    get("/waitingRoom", GameController::waitingRoom, new MustacheTemplateEngine());

    get("/userInfo", "application/json", UserController::getUserInfo);

    get("/fightLobby", GameController::fightLobby, new MustacheTemplateEngine());

    get("/deleteGame", (req,res)->{
      Integer gameID = new Integer(req.queryParams("id"));
      Integer userID = new Integer(req.queryParams("uid"));
      Game game = GameService.getGame(gameID);
      if(GameService.isPlayerOne(gameID,userID))
        game.setDeletedByUser1(true);
      else
        game.setDeletedByUser2(true);
      return 0;
    });
  }

     //Sends a message from one user to all users, along with a list of current usernames
    public static void broadcastMessage(Integer rivalID) {
        Session rival = getKeyByValue(userUsernameMap, User.findById(rivalID));
        try {
          rival.getRemote().sendString(String.valueOf(new JSONObject().put("msg","UpdateTurn")));
          } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Sends a message from one user to all users, along with a list of current usernames
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

    public static Session getKeyByValue(Map<Session,User> map, User user){
     for (Map.Entry<Session, User> entry : map.entrySet()) {
        if (user.getInteger("id").equals((entry.getValue()).getInteger("id"))) {
            return entry.getKey();
        }
      }
      return null;
    }


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

    public static void sendQuestion(Integer gameID){
      Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
      Game game = Game.findById(gameID);
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

    private static JSONObject generateQuestionJSON(Game game){
      JSONObject json = new JSONObject();
      Category category = CategoryService.randomCategory();
      Question question = category.getQuestion();      //change name
      game.setCurrentQuestion(question.getDescription());
      List<String> options = QuestionService.randomAnswers(question);  //change name
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

    public static void sendResults(JSONObject json){
      Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
      Game game = Game.findById(json.getInt("gameID"));
      Question question = Question.findById(json.getInt("questionID"));
      game.setCurrentQuestion(null);
      if(game.getUser2Answer()==null || game.getUser1Answer()==null){
        Base.close();return;
      }
      else{
        game.setRound(game.getRound()+1);
        boolean correct1 = isCorrect(question,game.getUser1Answer());  //service?????????
        boolean correct2 = isCorrect(question,game.getUser2Answer());  //service?????????
        GameService.updateDuelGame(game,correct1,true);
        GameService.updateDuelGame(game,correct2,false);
        boolean finalizedGame = game.getRound().compareTo(game.getTotalRounds())==0;
        Base.close();
        if(finalizedGame)      //CHANGE THIS!!!!!!!!!!!!
            sendFinalResults(correct1,correct2,game);
        else
            sendMessages(correct1,correct2,game);
      }
    }

    private static boolean isCorrect(Question question, String userAnswer){
      return question.getAnswer1().compareTo(userAnswer)==0;
    }

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
    public static void cancelReq(Integer rivalID){
      Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
      Session requestedUser = getKeyByValue(userUsernameMap, User.findById(rivalID));
      Base.close();
      try{
        requestedUser.getRemote().sendString(String.valueOf(new JSONObject().put("msg","gameReqCanceled")));
      } catch(Exception e){
        e.printStackTrace();
      }   
    }

  }
