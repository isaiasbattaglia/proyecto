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

public class App{
  static Map<Session, User> userUsernameMap = new ConcurrentHashMap<>();

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

    get("/deleteGame", (req,res)->{
      Integer id = new Integer(req.queryParams("id"));
      Game game = GameService.getGame(id);
      game.delete();      
      return id;
    });
  }

     //Sends a message from one user to all users, along with a list of current usernames
    public static void broadcastMessage(Integer rivalID) {
        Session rival = getKeyByValue(userUsernameMap, User.findById(rivalID));
        try {
          rival.getRemote().sendString(String.valueOf(new JSONObject()
            .put("msg","UpdateTurn")
            ));
          } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONArray generateJsonArray(Collection<User> c){
      Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
      JSONArray array = new JSONArray();
      for (User u : c)
        array.put(UserService.userToJSON(u));
      Base.close();
      return array;
    }

      //Sends a message from one user to all users, along with a list of current usernames
    public static void updateOnlineUsers() {
      userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
          try {
              session.getRemote().sendString(String.valueOf(new JSONObject()
                  .put("userlist",generateJsonArray(userUsernameMap.values()))
              ));
          } catch (Exception e) {
              e.printStackTrace();
          }
      });
    }

    private static Session getKeyByValue(Map<Session,User> map, User user){
     for (Map.Entry<Session, User> entry : map.entrySet()) {
        if (user.getInteger("id").equals((entry.getValue()).getInteger("id"))) {
            return entry.getKey();
        }
      }
      return null;
    }
  }
