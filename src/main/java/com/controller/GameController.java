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

   /**
   * This method returns the game's home view.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. HTTP Session must be initialized.
   * @return a ModelAndView containing the game's home view.
   * @post. a ModelAndView containing the game's home view, is returned.
  */
	public static ModelAndView gameHome(Request req, Response res){
		Map map = new HashMap();
    boolean isAdmin = req.session().attribute("admin");
		Integer id = req.session().attribute("userID");
		User actualUser = UserService.getUser(id);
    List<Game> games = GameService.getGames(id);
    List<Game> finalizedGames = GameService.getFinalizedGames(id);
    boolean notEmptyGames = !games.isEmpty();
    boolean notEmptyFGames = !finalizedGames.isEmpty();
    System.out.println(notEmptyFGames+"asd asd"+notEmptyGames);
	  map.put("games", games);
    map.put("finalizedGames", finalizedGames);  
 		map.put("lifes",actualUser.getLifes());
  	map.put("level",actualUser.getLevel());
    map.put("notEmptyFGames",notEmptyFGames);
    map.put("notEmptyGames", notEmptyGames);
    map.put("admin",isAdmin);
  	return new ModelAndView(map,"./views/games/home.mustache");
  }

   /**
   * This method redirect to a play's view if the user can create a new game
   * otherwise the game's home view is returned showing the error.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. HTTP Session must be initialized.
  */
  public static ModelAndView newGame(Request req, Response res){
  	Map map = new HashMap();
		Integer userID = req.session().attribute("userID");
		User actualUser = UserService.getUser(userID);
    if (actualUser.getLifes().compareTo(0)>0){
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
      map.put("lifes",actualUser.getLifes());
      map.put("level",actualUser.getLevel());
      map.put("error","No posee mas vidas para seguir jugando");
      return new ModelAndView(map,"./views/games/home.mustache");
    }
  }

   /**
   * This method returns the random category's view, to allow the user start the game.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. HTTP Session must be initialized.
   * @return a ModelAndView containing the random category's view.
   * @post. a ModelAndView containing the random category's view, is returned.
  */
  public static ModelAndView play(Request req, Response res){
  	Map map = new HashMap();
    Integer userID = req.session().attribute("userID");
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

   /**
   * support method for the play method
   * This method returns the random category's view, to allow the user start the game.
   * @param map a Map.
   * @param tCategory name of the category.
   * @pre. true.
   * @return a ModelAndView containing the random category's view.
   * @post. a ModelAndView containing the random category's view, is returned.
  */
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
  
   /**
   * This method returns the view when the game ends, showing the final results
   * depending on whether if the user is player1 or not.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. HTTP Session must be initialized.
   * @return a ModelAndView containing the view with the final results of the game.
   * @post. a ModelAndView containing the view with the final results of the game, is returned.
  */
  public static ModelAndView finalizedGame(Request req, Response res){
    Map map = new HashMap();
    Integer userID = req.session().attribute("userID");
    Integer gameID = Integer.parseInt(req.queryParams("game_id"));
    Integer userIDwin = GameService.getUserWinner(gameID);
    Game game = GameService.getGame(gameID);

    if(GameService.isPlayerOne(gameID,userID))
      putInMapFinalResults(map,userIDwin,userID,true,game);
    else
      putInMapFinalResults(map, userIDwin,userID,false,game);

    return new ModelAndView(map, "./views/games/finalizedGame.mustache");
  }

   /**
   * support method for the finalizedGame method
   * This method allow put in map a final results of the game for user 1 or user 2 as appropriate
   * @param map a Map.
   * @param userIDWin winner user's id.
   * @param userID current user id.
   * @param player1 true iff user is a player 1 in game.
   * @param game game where the user plays.
   * @pre. true.
   * @post. database updated.
  */
  private static void putInMapFinalResults(Map map, Integer userIDwin, Integer userID, boolean player1,Game game){
    User currentUser = User.findById(userID);
    map.put("user",currentUser.getUsername());   
    if(userIDwin.compareTo(-1)==0)
      map.put("draw",true);
    else if(userID.compareTo(userIDwin)==0)
      map.put("win",true);
    else
      map.put("lose",true);
    if(player1){
      map.put("Co_ans",game.getAmountOfCategories1());
      map.put("In_ans",game.getAmountOfCategories2());   
    }
    else{
      map.put("Co_ans",game.getAmountOfCategories2());
      map.put("In_ans",game.getAmountOfCategories1());       
    }
  }

   /**
   * This method returns the waiting room view.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. true.
   * @return a ModelAndView containing the waiting room view.
   * @post. a ModelAndView containing the waiting room view,is returned.
  */
  public static ModelAndView waitingRoom(Request req, Response res){
    return new ModelAndView(new HashMap(), "./views/games/waitingRoom.html");
  }

   /**
   * This method returns the fight lobby view.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. true.
   * @return a ModelAndView containing the fight lobby view.
   * @post. a ModelAndView containing the fight lobby view,is returned.
  */
  public static ModelAndView fightLobby(Request req, Response res){
    return new ModelAndView(new HashMap(), "./views/games/playDuel.html");
  }

   /**
   * This method allows delete a game.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. true.
   * @return an integer value that indicates that the game was deleted correctly.
   * @post. database updated.
  */
  public static Integer deleteGame(Request req, Response res){
    Integer gameID = new Integer(req.queryParams("id"));
    Integer userID = new Integer(req.queryParams("uid"));
    Game game = GameService.getGame(gameID);
    if(GameService.isPlayerOne(gameID,userID))
      game.setDeletedByUser1(true);
    else
      game.setDeletedByUser2(true);
    return 0;
  }
}