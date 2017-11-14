package trivia;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class QuestionService{

   /**
   * This method returns a list that contains a answers for a given question in random order.
   * @param q a Question.
   * @pre. true.
   * @return a list that contains a answers for a given question in random order.
   * @post. a list that contains a answers for a given question in random order, is returned.
  */
  public static List<String> randomAnswers(Question q){
    List<String> arr = new ArrayList<String>();
    arr.add(0,q.getAnswer1());
    arr.add(1,q.getAnswer2());
    arr.add(2,q.getAnswer3());
    arr.add(3,q.getAnswer4());
    Collections.shuffle(arr);
    return arr;
  }

   /**
   * This method returns a Question that corresponds to the id.
   * @param id id of the question.
   * @pre. true.
   * @return a Question.
   * @post. a question that corresponds to the id, is returned.
  */
  public static Question getQuestion(Integer id){
  	return Question.findById(id);
  }

}