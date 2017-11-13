package trivia;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;


public class UserService{
  private static Map<Integer,Integer> cacheFib =new HashMap<Integer,Integer> ();
  public final Integer CORRECT_ANSWER_POINT = 10;

   /**
   * This method returns a Boolean value that indicates whether it was possible to create a user.
   * @param username username of the user.
   * @param pass a password of the user.
   * @param email email of the user.
   * @pre. true.
   * @return a boolean value that indicates whether it was possible to create a user.
   * @post. a boolean value that indicates whether it was possible to create a user, is reurned.
  */
  public static boolean userRegister(String username, String pass, String email){
    List<User> lst = User.where("username = ? or email = ?", username, email);
    if(lst.size()==0){
      createUser(username,pass,email);
      return true; 
    }
    else
      return false;
  }

   /**
   * This method allows create an user with the data given.
   * @param username username of the user.
   * @param password a password of the user.
   * @param email email of the user.
   * @pre. true.
   * @post. database updated.
  */
  private static void createUser(String username, String password, String email){
    User newUser = new User(username,email,password);
  }

   /**
   * This method returns a boolean value that indicates whether data given by the user are valid or not.
   * @param username username of the user.
   * @param password a password of the user.
   * @pre. true.
   * @return a boolean value that indicates whether data given by the user are valid or not.
   * @post. a boolean value that indicates whether data given by the user are valid or not, is returned.
  */
  public static boolean validUser(String username, String password){
    List<User> user = User.where("username = ? and password = ? ", username, password);
    return user.size()==1;
  }

   /**
   * This method returns a user that matches the data provided.
   * @param name username of the user.
   * @param password a password of the user.
   * @pre. true.
   * @return a user that matches the data provided.
   * @post. a user that matches the data provided, is returned.
  */
  public static User getUser(String name, String password){
    List<User> ls = User.where("username = ? and password = ? ",name,password);
    if (ls.size()==0)
      throw new IllegalArgumentException("NO valid user");
    return ls.get(0);
  }

   /**
   * This method returns an id of the user that matches the data provided.
   * @param username username of the user.
   * @param password a password of the user.
   * @pre. true.
   * @return an id of the user that matches the data provided.
   * @post. an id of the user that matches the data provided, is returned.
  */
  public static Integer getUserId(String username, String password){
    User user = getUser(username,password);
    return user.getInteger("id");
  }

   /**
   * This method returns a user that corresponds to the id.
   * @param id id of the user.
   * @pre. true.
   * @return a user.
   * @post. a user that corresponds to the id, is returned.
  */
  public static User getUser(Integer id){
    return User.findById(id);
  }

   /**
   * This method update a user's profile one way or another depending on whether it responds well or badly.
   * @param user a user.
   * @param correctAnswer a boolean value that indicates whether a user responded well or badly.
   * @pre. true.
   * @post. database updated.
  */
  public static void updateProfile(User user, boolean correctAnswer ){
    user.setTotalQuestions(user.getTotalQuestions()+1);
   //upadteQuestionScore(correctAnswer);
    if (correctAnswer){
      user.setCorrectQuestions(user.getCorrectQuestions()+1);
      user.setTotalPoints(user.getTotalPoints()+10);
      Integer points= user.getTotalPoints();
      Integer pointsToNextLevel=pointsToNextLevel(user);
      if (pointsToNextLevel.compareTo(points)<=0)
        updateLevel(user,points,pointsToNextLevel);    
    }
    else
      user.setIncorrectQuestions(user.getIncorrectQuestions()+1);
  }

  //public static Boolean upadteQuestionScore() {
    //set("correct_questions",cQuestion).set("", totalQuestion).saveIt();
  //}

   /**
   * This method returns a JSON Object containing the user's info.
   * @param user a user.
   * @pre. true.
   * @return a JSON Object containing the user's info.
   * @post. a JSON Object containing the user's info, is returned.
  */
  public static JSONObject userToJSON(User user){
    JSONObject res = new JSONObject();
    res.put("id",user.getInteger("id"));
    res.put("username",user.getUsername());
    return res;
  }

   /**
   * This method returns an integer value that indicates the points a user needs to upgrade to the next level.
   * @param user a user.
   * @pre. true.
   * @return an integer value that indicates the points a user needs to upgrade to the next level.
   * @post. an integer value that indicates the points a user needs to upgrade to the next level, is returned.
  */
  public static Integer pointsToNextLevel(User user){
      return (memoFib(user.getLevel()))*10;
  }

   /**
   * support method for pointsToNextLevel method
   * This method calculates the n-th Fibonacci number by using Memoization.
   * @param n n-th fibonacci number to calculate.
   * @pre. true.
   * @return n-th Fibonacci number.
   * @post. n-th Fibonacci number, is returned.
  */
  private static Integer memoFib(Integer n){
    if (!cacheFib.containsKey(n)) {
      if(n<=1)
        cacheFib.put(n,1);
      else
        cacheFib.put(n,memoFib(n-1)+memoFib(n-2));
    }
    return cacheFib.get(n);
  }

   /**
   * This method update a user's level and if necessary give points to new level.
   * @param user an user.
   * @param currentPoints user's current points.
   * @param totalPoints necessary points to up level.
   * @pre. true.
   * @post. database updated
  */
  private static void updateLevel(User user, Integer currentPoints, Integer totalPoints){
    Integer dif=currentPoints-totalPoints;
    if (dif>=0) {
      user.setLevel(user.getLevel()+1);
      user.setTotalPoints(dif);
    }
    return;
  }
   /**
   * This method returns a list that contains a top 10 users in the game.
   * @pre. true.
   * @return a list that contains a top 10 users in the game.
   * @post. a list that contains a top 10 users in the game, is returned.
  */
  public static List<User> top10(){
    return User.findBySQL("select username, level from users order by level desc limit 10");
  }
}