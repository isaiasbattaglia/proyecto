package trivia;
import org.javalite.activejdbc.Model;
public class QuestionsUsers extends Model {
	public QuestionsUsers(Long id_user, Long question_id){
		set("user_id",id_user);
		set("question_id",question_id);
		saveIt();
	}
}