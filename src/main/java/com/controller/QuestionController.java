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
		if(req.queryParams("category").equals("Comodin")){
      Integer gameID = Integer.parseInt(req.queryParams("game_id"));
      return chooseCategory(req,gameID);
    }
    else{
      Integer categoryID = Integer.parseInt(req.queryParams("category_id"));
      Category category = CategoryService.getCategory(categoryID);
      Question question = category.getQuestion();
      List<String> answerList = QuestionService.randomAnswers(question);  //Preguntar!
      map.put("category",req.queryParams("category"));
      map.put("Question",question.getDescription());
      map.put("answer1",answerList.get(0));
      map.put("answer2",answerList.get(1));
      map.put("answer3",answerList.get(2));
      map.put("answer4",answerList.get(3));
      map.put("quest",question);
      map.put("game_id",req.queryParams("game_id"));
      Boolean chooseCategory = (req.queryParams("chooseCategory")==null)? false : true;
      map.put("chooseCategory", chooseCategory);
      return new ModelAndView(map, "./views/questions/show.mustache");
    }
	}

  public static ModelAndView answerQuestion(Request req, Response res){
    Map map = new HashMap();
    String userAnswer = req.queryParams("answer");
    Integer questionID = Integer.parseInt(req.queryParams("question_id"));
    Question currentQuestion = QuestionService.getQuestion(questionID);
    Integer userID = req.session().attribute("userID");
    Integer gameID = Integer.parseInt(req.queryParams("game_id"));
    if(userAnswer.equals(currentQuestion.getAnswer1())){
      return correctAnswer(req, userID, gameID, Boolean.valueOf(req.queryParams("chooseCategory")));
    }
    else
      return wrongAnswer(req, userID, gameID);
  }

  private static ModelAndView correctAnswer(Request req, Integer userID, Integer gameID,Boolean chooseCategory){
    UserService.updateProfile(UserService.getUser(userID), true);
    Boolean canChooseCategory = false;
    if(!chooseCategory)
       canChooseCategory = updateLifes(req, userID);

    if (GameService.isPlayerOne(gameID,userID)){
      GameService.winACategory(Integer.parseInt(req.queryParams("question_id")),gameID,chooseCategory,true);
      GameService.updateGame(GameService.getGame(gameID),true,true);
    }
    else{
      GameService.winACategory(Integer.parseInt(req.queryParams("question_id")),gameID,chooseCategory,false);
      GameService.updateGame(GameService.getGame(gameID),true,false);
    }
    if(canChooseCategory)
      return chooseCategory(req, gameID);
    else
      return checkLastRound(req,gameID,true);
  }

  private static ModelAndView wrongAnswer(Request req, Integer userID, Integer gameID){
    UserService.updateProfile(UserService.getUser(userID), false);
    if (GameService.isPlayerOne(gameID,userID))
      GameService.updateGame(GameService.getGame(gameID),false,true);
    else
      GameService.updateGame(GameService.getGame(gameID),false,false);
    Game currentGame = GameService.getGame(gameID);
    currentGame.setRound(currentGame.getRound()+1);
    User rival= GameService.getRival(gameID,userID);
    App.broadcastMessage(rival.getInteger("id"));
    return checkLastRound(req,gameID,false);
  }

  private static Boolean updateLifes(Request req, Integer userID){
    User actualUser = UserService.getUser(userID);
    Integer correct_ans = req.session().attribute("correct_answer");
    if(correct_ans>3){
      req.session().removeAttribute("correct_answer");
      req.session().attribute("correct_answer",0);
    }
    else{
      req.session().removeAttribute("correct_answer");
      req.session().attribute("correct_answer",correct_ans+1);
      if((correct_ans+1)==3){
        actualUser.setLifes(actualUser.getLifes()+1);
        req.session().removeAttribute("correct_answer");
        req.session().attribute("correct_answer",0);
        return true;
      }
    }
    return false;
  }

  private static ModelAndView checkLastRound(Request req, Integer gameID, Boolean correctAnswer){
    Map map = new HashMap();
    Game game = GameService.getGame(gameID);
    if(game.getRound().compareTo(game.getTotalRounds())==0){
      game.setState("Finalized");
      map.put("final",true);
    }
    else
      map.put("no_final",true);
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

  private static void putInMapMissingCategories(Map map, List<String> lst){
    for(String categoryName : lst)
      map.put(categoryName,true);
  }

  private static void putInMapIdsMissCat(Map map, List<String> lst){
    for(String categoryName : lst)
      map.put(categoryName+"_id",CategoryService.getCategoryId(categoryName));
  }

  public static ModelAndView chooseCategory(Request req, Integer gameID){
      Map map = new HashMap();
      List<String> lst = GameService.getMissingCategories(req.session().attribute("userID"),gameID);
      putInMapMissingCategories(map,lst);
      putInMapIdsMissCat(map,lst);
      map.put("game_id",gameID);
      map.put("chooseCategory",true);
      return new ModelAndView(map, "./views/category/chooseCategory.html");
  }
}