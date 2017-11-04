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

	public static ModelAndView registerUser(Request req, Response res){
		return new ModelAndView(new HashMap(), "./views/users/new.mustache");
	}

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

	public static ModelAndView userLogout(Request req, Response res){
		if (req.session().attribute("user")!=null){ 
      		req.session().removeAttribute("user");
      		req.session().removeAttribute("userID");
      		req.session().removeAttribute("correct_answer");
    	}
    	return new ModelAndView(new HashMap(), "./views/home.mustache");
	}

	private static void createUserSession(Request req, Integer id, String username){
		req.session(true);
		req.session().attribute("user",username);
		req.session().attribute("userID",id);
		req.session().attribute("correct_answer",0); //Sacar
	}

	public static ModelAndView profile(Request req, Response res){
		Map data = new HashMap();
		Integer userID = (req.session().attribute("userID"));
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

	public static ModelAndView ranking(Request req, Response res){
		Map map = new HashMap();
    List<User> top10 =  UserService.top10();
    map.put("ranking",top10);
    return new ModelAndView(map,"./views/ranking.mustache");
	}

	public static JSONObject getUserInfo(Request req, Response res){
	    res.type("application/json");
	    int userID = req.session().attribute("userID");
	    User actualUser = User.findById(userID);
	    return UserService.userToJSON(actualUser);
	}	
	private static boolean sessionOpen(Request req){
		return (req.session().attribute("userID")!=null);
	}
}