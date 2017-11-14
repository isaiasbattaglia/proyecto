package trivia;
import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import spark.Request;
import spark.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.javalite.activejdbc.Base;
import org.json.JSONObject;


public class UserController{

  /**
   * This method returns the sign up view.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. true.
   * @return a ModelAndView containing the sign up view.
   * @post. a ModelAndView containing the sign up view, is returned.
  */
	public static ModelAndView registerUser(Request req, Response res){
		return new ModelAndView(new HashMap(), "./views/users/new.mustache");
	}

  /**
   * This method checks the sign up data, if their are correct the home's view is returned, 
   * otherwise the sign up view is returned informating the error.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. true.
   * @return a ModelAndView.
   * @post. a ModelAndView, is returned.
  */
	public static ModelAndView registeringUser(Request req, Response res){
		Map map = new HashMap();
		String password1 = req.queryParams("password");
		String password2 = req.queryParams("password2");
		String username = req.queryParams("nickname");
		String email = req.queryParams("Email");
		if(!password1.equals(password2)){
			map.put("error","Las contraseñas no coinciden");
			return new ModelAndView(map, "./views/users/new.mustache");
		}
		if(UserService.userRegister(username,password1,email)){
			return new ModelAndView(map, "./views/home.mustache");
		}
		else{
			map.put("error","Verifique nombre de usuario o email");
			return new ModelAndView(map, "./views/users/new.mustache");
		}
	}

  /**
   * This method checks the sign in data, if their are correct it creates a sessions for the user
   * and the game's home view is returned, 
   * otherwise if the session is already open or the sign in data aren't correct the home's view is returned informating the error.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. true.
   * @return a ModelAndView that contains a game's home view if the sign in data are correct,
   * otherwise contains home's view.
   * @post. a ModelAndView, is returned.
  */
	public static ModelAndView userLogin(Request req, Response res){
		Map map = new HashMap();
		String username = req.queryParams("nickname");
		String password = req.queryParams("password");
		if(UserService.validUser(username,password)){
			if(sessionOpen(req)){
				map.put("SessionOpen","Ya has iniciado sesion como: " + req.session().attribute("user"));
				return new ModelAndView(map,"./views/home.mustache");
			}
			else{
				Integer userID = UserService.getUserId(username,password);
				User currentUser = UserService.getUser(username,password);
				createUserSession(req,userID,currentUser.getUsername());
				List<Game> games = GameService.getGames(userID);
				res.redirect("/games");
				return new ModelAndView(map,"./views/home.mustache");
			}
		}
		else{
			map.put("error","usuario o contraseña incorrecta");
			return new ModelAndView(map, "./views/home.mustache");
		}
	}

  /**
   * This method allows the user to log out.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. HTTP Session must be initialized.
   * @return a ModelAndView that contains  home's view.
   * @post. a HTTP Session is removed.
  */
	public static ModelAndView userLogout(Request req, Response res){
		if (req.session().attribute("user")!=null){ 
      		req.session().removeAttribute("user");
      		req.session().removeAttribute("userID");
      		req.session().removeAttribute("correct_answer");
    	}
    	return new ModelAndView(new HashMap(), "./views/home.mustache");
	}

  /**
   * support method for the userLogin method
   * This method allows create a session for the user.
   * @param req Provides information about the HTTP request.
   * @param id a user ID.
   * @param username username of the user.
   * @pre. true.
   * @post. a HTTP Session is created.
  */
	private static void createUserSession(Request req, Integer id, String username){
		req.session(true);
		req.session().attribute("user",username);
		req.session().attribute("userID",id);
		req.session().attribute("correct_answer",0); //Sacar
	}

  /**
   * This method returns a user's profile view.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. HTTP Session must be initialized.
   * @return a ModelAndView containing the user's profile view.
   * @post. a ModelAndView containing the user's profile view, is returned.
  */
	public static ModelAndView profile(Request req, Response res){
		Map data = new HashMap();
		Integer userID = req.session().attribute("userID");
		User actualUser = UserService.getUser(userID);
		data.put("lifes",actualUser.getLifes());
		data.put("Total_Points",actualUser.getTotalPoints());
		data.put("correct_questions",actualUser.getCorrectQuestions());
		data.put("incorrect_questions",actualUser.getIncorrectQuestions());  
		data.put("total_questions",actualUser.getTotalQuestions());
		data.put("level",actualUser.getLevel());
		data.put("points_to_next_level",UserService.pointsToNextLevel(actualUser));
		data.put("user",actualUser.getUsername());
		return new ModelAndView(data,"./views/users/profile.mustache");
	}

  /**
   * This method returns a top 10 view.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. true.
   * @return a ModelAndView containing the top 10 view.
   * @post. a ModelAndView containing the top 10 view, is returned.
  */
	public static ModelAndView ranking(Request req, Response res){
		Map map = new HashMap();
    List<User> top10 =  UserService.top10();
    map.put("ranking",top10);
    return new ModelAndView(map,"./views/ranking.mustache");
	}

  /**
   * This method returns a JSON Object containing the user's data.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. HTTP Session must be initialized.
   * @return a JSON Object containing the user's data.
   * @post. a JSON Object containing the user's data, is returned.
  */
	public static JSONObject getUserInfo(Request req, Response res){
	    res.type("application/json");
	    int userID = req.session().attribute("userID");
	    User actualUser = User.findById(userID);
	    return UserService.userToJSON(actualUser);
	}
	
  /**
   * This method checks if the given session is already open.
   * @param req Provides information about the HTTP request.
   * @pre. true.
   * @return a boolean value that indicates if the given session is already open.
   * @post. a boolean value that indicates if the given session is already open, is returned.
  */	
	private static boolean sessionOpen(Request req){
		return (req.session().attribute("userID")!=null);
	}
}