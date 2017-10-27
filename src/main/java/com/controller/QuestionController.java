package trivia;
import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import spark.Request;
import spark.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class QuestionController{

	public static ModelAndView question(Request req, Response res){
		Map map = new HashMap();
		Integer categoryID = Integer.parseInt(req.queryParams("category_id"));
  	Category category = CategoryService.getCategory(categoryID);
  	Question question = category.getQuestion();
  	List<String> answerList = QuestionService.randomAnswers(question);	//Preguntar!
    map.put("category",req.queryParams("category"));
    map.put("Question",question.getDescription());
    map.put("answer1",answerList.get(0));
    map.put("answer2",answerList.get(1));
    map.put("answer3",answerList.get(2));
    map.put("answer4",answerList.get(3));
    map.put("quest",question);
    map.put("game_id",req.queryParams("game_id"));
	  return new ModelAndView(map, "./views/questions/show.mustache");
	}

  public static ModelAndView answerQuestion(Request req, Response res){
    Map map = new HashMap();
    String userAnswer = req.queryParams("answer");
    Integer questionID = Integer.parseInt(req.queryParams("question_id"));
    Question currentQuestion = QuestionService.getQuestion(questionID);

    Integer userID = req.session().attribute("user");
    Integer gameID = Integer.parseInt(req.queryParams("game_id"));
    
    Game currentGame = GameService.getGame(gameID);
    currentGame.setRound(currentGame.getRound()+1);

    if(userAnswer.equals(currentQuestion.getAnswer1()))
      return correctAnswer(req, userID, gameID);
    else
      return wrongAnswer(req, userID, gameID);
  }


  private static ModelAndView correctAnswer(Request req, Integer userID, Integer gameID){
    UserService.updateProfile(UserService.getUser(userID), true);
    GameService.updateGame(GameService.getGame(gameID),true);
    updateLives(req, userID);
    return checkLastRound(req,gameID,true);
  }

  private static ModelAndView wrongAnswer(Request req, Integer userID, Integer gameID){
    UserService.updateProfile(UserService.getUser(userID), false);
    GameService.updateGame(GameService.getGame(gameID),false);
    return checkLastRound(req,gameID,false);
  }


  private static void updateLives(Request req, Integer userID){
    User actualUser = UserService.getUser(userID);
    Integer correct_ans = req.session().attribute("correct_answer");
    if(correct_ans>=3){
      actualUser.setLives(actualUser.getLives()+1);
      req.session().removeAttribute("correct_answer");
      req.session().attribute("correct_answer",0);
    }
    else{
      req.session().removeAttribute("correct_answer");
      req.session().attribute("correct_answer",correct_ans+1);
    }
  }

  private static ModelAndView checkLastRound(Request req, Integer gameID, Boolean correctAnswer){
    Map map = new HashMap();
    Game game = GameService.getGame(gameID);
    if(game.getRound().compareTo(game.getTotalRounds())==0){
      game.setState("finalizado");
      map.put("final",true);
      map.put("no_final",false);
    }
    else{
      map.put("final", false);
      map.put("no_final",true);
    }
    if(correctAnswer){
      map.put("correct","Respuesta correcta");
      map.put("game_id",gameID);
      return new ModelAndView(map,"./views/games/correct.mustache");
    }
    else{
      Integer questionID = Integer.parseInt(req.queryParams("question_id"));
      Question question = QuestionService.getQuestion(questionID);
      map.put("incorrect","Respuesta incorrecta");
      map.put("game_id",gameID);
      map.put ("correct_answer",question.getAnswer1());
      return new ModelAndView(map,"./views/games/incorrect.mustache");   
    }          
  }

}