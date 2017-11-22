package trivia;
import spark.template.mustache.MustacheTemplateEngine;
import java.util.Optional;
import static spark.Spark.*;


public class TestUtil{
    public TestUtil() {
        setConfig();
    }
    private static void setConfig() {
        setPort();
        staticFileLocation("/public");
    }
    private static void setPort() {
        String port = System.getenv("PORT");
        port(Optional.ofNullable(port).map(Integer::valueOf).orElse(4567));
    }
    public void deploy() {
        get("/registrar",UserController::registerUser,new MustacheTemplateEngine());
    }
    public void games(){
   		get("/games", GameController::gameHome, new MustacheTemplateEngine());
    }
    public void loginWParams(){
	    get("/login", UserController::userLogin,new MustacheTemplateEngine());
    }
    public void loginWOutParams(){
	    get("/login", UserController::userLogin,new MustacheTemplateEngine());
    }
    public void validateData(){
        post("/verificar",UserController::registeringUser, new MustacheTemplateEngine());
    }
    public void deleteGame(){
        get("/deleteGame", GameController::deleteGame);
    }
    public void createAdmin(){
        get("/createAdmin", UserController::createAdmin, new MustacheTemplateEngine());
    }
    public void profile(){
        get("/profile", UserController::profile, new MustacheTemplateEngine());
    }
    public void finalizedGame(){
        get("/finalizedGame",GameController::finalizedGame, new MustacheTemplateEngine());
    }
    public void fightLobby(){
        get("/fightLobby", GameController::fightLobby, new MustacheTemplateEngine());
    }
    public void logout(){
        get("/logout",UserController::userLogout,new MustacheTemplateEngine());
    }
}