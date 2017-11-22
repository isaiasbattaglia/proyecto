package trivia;
import org.javalite.activejdbc.Model;
public class GamesQuestions extends Model {
	public GamesQuestions(Integer quest_id, Integer game_id){
		set("game_id",game_id);
		set("question_id",quest_id);
		saveIt();
	}
}