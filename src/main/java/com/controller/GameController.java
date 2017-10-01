package trivia;
import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import spark.Request;
import spark.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class GameController{

	public static ModelAndView gameHome(Request req, Response res){
		Map map = new HashMap();
		Integer id = req.session().attribute("user");
		User actualUser = UserService.getUser(id);
    List<Game> games = GameService.getGames(id);
	  map.put("games", games);  
 		map.put("lives",actualUser.getLives());
  	map.put("level",actualUser.getLevel());
  	return new ModelAndView(map,"./views/games/home.mustache");
  }

  public static ModelAndView newGame(Request req, Response res){
  	Map map = new HashMap();
		Integer userID = req.session().attribute("user");
		User actualUser = UserService.getUser(userID);

    if (actualUser.getLives().compareTo(0)>0){
			Game game = GameService.createGame(userID);
      Integer gameID = game.getGameId();
      res.redirect ("/play"); //PREGUNTAR!!
  		return new ModelAndView(map,"./views/home.mustache");
      //return showRandomCategory(map,category.getTCategory());
      //redirect.get("/newGame", "/play");
    }
    else{
    	List<Game> games = GameService.getGames(userID);
  		map.put("games",games);
      map.put("lives",actualUser.get("lives"));
      map.put("level",actualUser.get("level"));
      map.put("error","No posee mas vidas para seguir jugando");
      return new ModelAndView(map,"./views/games/home.mustache");
    }
  }

  public static ModelAndView play(Request req, Response res){
  	Map map = new HashMap();
    Integer userID = req.session().attribute("user");

    Game game = GameService.getLastGame(userID);
  	Integer gameID = (req.queryParams("game_id")==null)? game.getGameId(): Integer.parseInt(req.queryParams("game_id"));
    Category category = CategoryService.randomCategory();
    Integer categoryID = category.getCategoryId();
    map.put("category",category.getTCategory());
    map.put("category_id",categoryID);
    map.put("game_id",gameID);
    return showRandomCategory(map,category.getTCategory());
	  //return new ModelAndView(map,"./views/category/randomCategory.mustache");  	
    //Long id = req.session().attribute("user");
    //User actualUser = UserService.getUser(id);
  }

  public static ModelAndView showRandomCategory(Map map, String tCategory){
    if (tCategory.equals("Historia"))
      map.put("historia",true);
    if (tCategory.equals("Geografia"))
      map.put("geografia",true);
    if (tCategory.equals("Ciencia"))
      map.put("ciencia",true);
    if (tCategory.equals("Entretenimiento"))
      map.put("entretenimiento",true);
    if (tCategory.equals("Deportes"))
      map.put("deportes",true);
    if (tCategory.equals("Arte"))
      map.put("arte",true);
    return new ModelAndView (map, "./views/category/randomCategory.mustache");
  }
  
  public static ModelAndView finalizedGame(Request req, Response res){
    Map map = new HashMap();
    Integer userID = (req.session().attribute("user"));
    Integer gameID = Integer.parseInt(req.queryParams("game_id"));
    Game game = GameService.getGame(gameID);
    User actualUser = UserService.getUser(userID);
    map.put("user",actualUser.get("username"));
    map.put("Co_ans",game.get("questions_Correct"));
    map.put("In_ans",game.get("questions_Incorrect"));
    return new ModelAndView(map, "./views/games/finalizedGame.mustache");
  }
}