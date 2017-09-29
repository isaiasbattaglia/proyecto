package trivia;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class QuestionService{

  public static List<String> randomAnswers(Question q){
    List<String> arr = new ArrayList<String>();
    arr.add(0,q.getAnswer1());
    arr.add(1,q.getAnswer2());
    arr.add(2,q.getAnswer3());
    arr.add(3,q.getAnswer4());
    Collections.shuffle(arr);
    return arr;
  }

  public static Question getQuestion(Integer id){
  	return Question.findById(id);
  }

}