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

    get("/createQuestion", QuestionController::createQuestion, new MustacheTemplateEngine());

    get("/createAdmin", UserController::createAdmin, new MustacheTemplateEngine());

    get("/deleteGame", GameController::deleteGame);

    get("/validateUser", UserController::validateUser);

    post("/newUser", UserController::newUser);

    get("/getWrongAnswer", QuestionController::getWrongAnswer);

    post("newQuestion", QuestionController::newQuestion);
  }
}
