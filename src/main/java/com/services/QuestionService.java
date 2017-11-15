package trivia;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class QuestionService{
  public static final String wrongAnswer = 
  "EnCt25825d2e696fa42fbf615e86068852097ae49c4f45825d2e696fa42fbf615e8602JTQhEfkiQGgCI2iDFqHsTNgk2+MIwEmS";
   /**
   * This method returns a list that contains a answers for a given question in random order,
   * and the last position contains the wrong Answer that significances that an user not answered in time.
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
    arr.add(4,wrongAnswer);
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