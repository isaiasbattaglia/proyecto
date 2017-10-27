package trivia;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;


public class UserService{
  private static Map<Integer,Integer> cacheFib =new HashMap<Integer,Integer> ();

  public final Integer CORRECT_ANSWER_POINT = 10;

  public static boolean userRegister(String username, String pass, String email){
    List<User> lst = User.where("username = ? or email = ?", username, email);
    if(lst.size()==0){
      createUser(username,pass,email);
      return true; 
    }
    else
      return false;
  }

  private static void createUser(String username, String password, String email){
    User newUser = new User(username,email,password);
  }

  public static boolean validUser(String username, String password){
    List<User> user = User.where("username = ? and password = ? ", username, password);
    return user.size()==1;
  }

  public static User getUser(String name, String password){
    List<User> ls = User.where("username = ? and password = ? ",name,password);
    if (ls.size()==0)
      throw new IllegalArgumentException("NO valid user");
    return ls.get(0);
  }

  public static Integer getUserId(String username, String password){
    User user = getUser(username,password);
    return user.getInteger("id");
  }

  public static User getUser(Integer id){
    return User.findById(id);
  }

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

  public static JSONObject userToJSON(User user){
    JSONObject res = new JSONObject();
    res.put("id",user.getInteger("id"));
    res.put("username",user.getUsername());
    return res;
  }

  public static Integer pointsToNextLevel(User user){
      return (memoFib(user.getLevel()))*10;
  }

  private static Integer memoFib(Integer n){
    if (!cacheFib.containsKey(n)) {
      if(n<=1)
        cacheFib.put(n,1);
      else
        cacheFib.put(n,memoFib(n-1)+memoFib(n-2));
    }
    return cacheFib.get(n);
  }

  private static void updateLevel(User user, Integer actualPoints, Integer totalPoints){
    Integer dif=actualPoints-totalPoints;
    if (dif>=0) {
      user.setLevel(user.getLevel()+1);
      user.setTotalPoints(dif);
    }
    return;
  }

  public static List<User> top10(){
    return User.findBySQL("select username, level from users order by level desc limit 10");
  }

}