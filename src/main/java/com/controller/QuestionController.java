package trivia;
import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import spark.Request;
import spark.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.json.JSONObject;

public class QuestionController{

  /**
   * This method returns the view of random question if the chose category isn't a "comodin"
   * otherwise a view for choose category is returned.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. HTTP Session must be initialized.
   * @return a ModelAndView that contains the view of random questions if the chosen category isn't a "comodin"
   * otherwise ModelAndView contains a view for choose category.
   * @post. a ModelAndView, is returned.
  */
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
      List<String> answerList = QuestionService.randomAnswers(question);
      map.put("category",req.queryParams("category"));
      map.put("Question",question.getDescription());
      map.put("answer1",answerList.get(0));
      map.put("answer2",answerList.get(1));
      map.put("answer3",answerList.get(2));
      map.put("answer4",answerList.get(3));
      map.put("answer5",answerList.get(4));
      map.put("quest",question);
      map.put("game_id",req.queryParams("game_id"));
      Boolean chooseCategory = (req.queryParams("chooseCategory")==null)? false : true;
      map.put("chooseCategory", chooseCategory);
      return new ModelAndView(map, "./views/questions/show.mustache");
    }
	}

  /**
   * This method checks if the user's answer is correctly, if it is correct the correct answer view is returned
   * otherwise the wrong answer view is returned. 
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. HTTP Session must be initialized.
   * @return a ModelAndView that contains correct answer view if the user's answer if correctly
   * otherwise ModelAndView contains wrong answer view.
   * @post. a ModelAndView, is returned.
  */
  public static ModelAndView answerQuestion(Request req, Response res){
    Map map = new HashMap();
    String userAnswer = req.queryParams("answer");
    Integer questionID = Integer.parseInt(req.queryParams("question_id"));
    Question currentQuestion = QuestionService.getQuestion(questionID);
    Integer userID = req.session().attribute("userID");
    Integer gameID = Integer.parseInt(req.queryParams("game_id"));
    if(userAnswer.equals(currentQuestion.getAnswer1())){ //The answer1 is the correct option.
      return correctAnswer(req, userID, gameID, Boolean.valueOf(req.queryParams("chooseCategory")));
    }
    else
      return wrongAnswer(req, userID, gameID);
  }

  /**
   * Support method for answerQuestion method
   * This method updates the user's profile with a new correct answer,
   * and update game with a new correct answer
   * if the user chose the category, it is set a new winner of this category, if the user can choose the category
   * i.e answered three correct questions the choose category view is returned otherwise the correct answer view is returned. 
   * @param req Provides information about the HTTP request.
   * @param userID id of the user.
   * @param gameID id of the game where the user plays.
   * @param chooseCategory a boolean value that represents if the user has chosen a category.
   * @pre. true.
   * @return a ModelAndView that contains correct answer view if the user can't choose a category
   * otherwise a ModelAndView contains choose category view.
   * @post. database updated.
  */
  private static ModelAndView correctAnswer(Request req, Integer userID, Integer gameID,Boolean chooseCategory){
    UserService.updateProfile(UserService.getUser(userID), true);
    Boolean canChooseCategory = false;
    if(!chooseCategory)
       canChooseCategory = updateLifes(req, userID, gameID);

    if (GameService.isPlayerOne(gameID,userID)){
      GameService.winACategory(Integer.parseInt(req.queryParams("question_id")),gameID,chooseCategory,true);
      GameService.updateGame(GameService.getGame(gameID),true,true);
    }
    else{
      GameService.winACategory(Integer.parseInt(req.queryParams("question_id")),gameID,chooseCategory,false);
      GameService.updateGame(GameService.getGame(gameID),true,false);
    }
    Boolean sixthCategoryReached = GameService.sixthCategoryReached(gameID,GameService.isPlayerOne(gameID,userID));
    if(sixthCategoryReached){
      return checkLastRound(req,gameID,true,sixthCategoryReached);
    }
    else if(canChooseCategory)
      return chooseCategory(req, gameID);
    else
      return checkLastRound(req,gameID,true,sixthCategoryReached);
  }

  /**
   * Support method for answerQuestion method
   * This method updates the user's profile with a new wrong answer,
   * and update game with a new wrong answer, increment the game's round, send a message to a its rival for that it can play
   * and the wrong answer view is returned. 
   * @param req Provides information about the HTTP request.
   * @param userID id of the user.
   * @param gameID id of the game where the user plays.
   * @pre. true.
   * @return a ModelAndView that contains wrong answer view 
   * @post. database updated.
  */
  private static ModelAndView wrongAnswer(Request req, Integer userID, Integer gameID){
    UserService.updateProfile(UserService.getUser(userID), false);
    if (GameService.isPlayerOne(gameID,userID))
      GameService.updateGame(GameService.getGame(gameID),false,true);
    else
      GameService.updateGame(GameService.getGame(gameID),false,false);
    Game currentGame = GameService.getGame(gameID);
    currentGame.setRound(currentGame.getRound()+1);
    User rival= GameService.getRival(gameID,userID);
    MultiplayerHelper.broadcastMessage(rival.getInteger("id"));
    return checkLastRound(req,gameID,false,false);
  }

  /**
   * This method increases the correct answers and if the third correct answer is reached, life increases in one.
   * @param req Provides information about the HTTP request.
   * @param userID id of the user.
   * @param gameID id of the game where the user plays.
   * @pre. HTTP Session must be initialized.
   * @return a boolean value that indicates if the life of the user has been increases in one. (i.e the third correct answer was reached).
   * @post. a boolean value that indicates if the life of the user has been increases in one, is returned.
  */
  private static Boolean updateLifes(Request req, Integer userID, Integer gameID){
    Game game = GameService.getGame(gameID);
    Integer correct_ans;
    if(GameService.isPlayerOne(gameID,userID))
      correct_ans = game.getAmountOfCorrect1();
    else
      correct_ans = game.getAmountOfCorrect2();
    User actualUser = UserService.getUser(userID);
    if(correct_ans.compareTo(3)>0){
      if(GameService.isPlayerOne(gameID,userID))
        game.setAmountOfCorrect1(0);
      else
        game.setAmountOfCorrect2(0);
    }
    else{
      if(GameService.isPlayerOne(gameID,userID))
        game.setAmountOfCorrect1(correct_ans+1);
      else
        game.setAmountOfCorrect2(correct_ans+1);
      if(new Integer(correct_ans+1).compareTo(3)==0){
        actualUser.setLifes(actualUser.getLifes()+1);
        if(GameService.isPlayerOne(gameID,userID))
          game.setAmountOfCorrect1(0);
        else
          game.setAmountOfCorrect2(0);
        return true;
      }
    }
    return false;
  }

  /**
   * This method checks if the last round of the game was reached, and returns correct or wrong answer view as MultiplayerHelperropiate
   * and sends message to rival if the game was finalized.
   * @param req Provides information about the HTTP request.
   * @param gameID id of the game where the user plays.
   * @param correctAnswer a boolean value that indicates if the user answered well or badly.
   * @param sixthCategoryReached a boolean values that indicates if the user won all six categories.
   * @pre. true.
   * @return a ModelAndWiew that contains a correct answer view if the user answered well,
   * otherwise a ModelAndView contains a wrong answer view.
   * @post. database updated.
  */
  private static ModelAndView checkLastRound(Request req, Integer gameID, Boolean correctAnswer, Boolean sixthCategoryReached){

    Map map = new HashMap();
    Game game = GameService.getGame(gameID);
    if(sixthCategoryReached || game.getRound().compareTo(game.getTotalRounds())==0){
      game.setState("Finalized");
      map.put("final",true);
      Integer userID = req.session().attribute("userID");
      User rival= GameService.getRival(gameID,userID);
      MultiplayerHelper.broadcastMessage(rival.getInteger("id"));
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

  /**
   * This method puts in the map the categories that a user needs to win the game.
   * @param map a Map.
   * @param lst a list that contains the categories that a user needs to win the game.
   * @pre. true.
   * @post. the map contains all categories in lst.
  */
  private static void putInMapMissingCategories(Map map, List<String> lst){
    for(String categoryName : lst)
      map.put(categoryName,true);
  }

  /**
   * This method puts in the map the id of the categories that a user needs to win the game.
   * @param map a Map.
   * @param lst a list that contains the categories that a user needs to win the game.
   * @pre. true.
   * @post. the map contains all the id of the categories in lst.
  */
  private static void putInMapIdsMissCat(Map map, List<String> lst){
    for(String categoryName : lst)
      map.put(categoryName+"_id",CategoryService.getCategoryId(categoryName));
  }

  /**
   * This method returns a choose category view.
   * @param req Provides information about the HTTP request.
   * @param gameID id of the game where the user plays.
   * @pre. HTTP Session must be initialized.
   * @return a ModelAndView that contains a choose category view.
   * @post. a ModelAndView that contains a choose category view, is returned.
  */
  public static ModelAndView chooseCategory(Request req, Integer gameID){
      Map map = new HashMap();
      List<String> lst = GameService.getMissingCategories(req.session().attribute("userID"),gameID);
      putInMapMissingCategories(map,lst);
      putInMapIdsMissCat(map,lst);
      map.put("game_id",gameID);
      map.put("chooseCategory",true);
      return new ModelAndView(map, "./views/category/chooseCategory.html");
  }

  /**
   * This method returns a createQuestion view.
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. true.
   * @return a ModelAndView that contains a createQuestion view.
   * @post. a ModelAndView that contains a createQuestion view, is returned.
  */
  public static ModelAndView createQuestion(Request req, Response res){
      return new ModelAndView(new HashMap(), "./views/questions/createQuestion.html");
  }

  /**
   * This method returns a String value that represents a wrong Answer. 
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. true.
   * @return a String value that represents a wrong Answer. 
   * @post. a String value that represents a wrong Answer, is returned.
  */
  public static String getWrongAnswer(Request req, Response res){
    return QuestionService.wrongAnswer;
  }

  /**
   * This method allows create a new question
   * @param req Provides information about the HTTP request.
   * @param res Provides information about the HTTP response.
   * @pre. true.
   * @return an integer value indicating that the question was created.
   * @post. database updated.
  */
  public static Integer newQuestion(Request req, Response res){
    JSONObject json = new JSONObject(req.queryParams("json"));
    Integer categoryID = CategoryService.getCategoryId(new String(json.getString("category")));
    Question question = new Question(new String(json.getString("question")),new String(json.getString("correct")),new String(json.getString("answer2")),
      new String(json.getString("answer3")),new String(json.getString("answer4")),categoryID);
    return 0;
  }
}