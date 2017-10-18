package trivia;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsToParents;
import org.javalite.activejdbc.annotations.BelongsTo;

@BelongsToParents({ 
@BelongsTo(foreignKeyName="user1_id",parent=User.class), 
@BelongsTo(foreignKeyName="user2_id",parent=User.class) 
}) 
 
public class Game extends Model {
  static{
    validatePresenceOf("user1_id").message("Please, provide a user id");
    validatePresenceOf("correct_questions1").message("Please, initialize correct_questions");
    validatePresenceOf("wrong_questions1").message("Please, initialize wrong_questions");
  }

  /**
  *Constructor por defecto de la clase Game
  **/
  public Game(){}

  public Game(Integer user_id){
    validatePresenceOf("user1_id").message("Please, provide a user id");
    validatePresenceOf("correct_questions1").message("Please, initialize correct_questions");
    validatePresenceOf("wrong_questions1").message("Please, initialize wrong_questions");
  	set("round",0);
    set("total_rounds",5);
  	set("user1_id",user_id);
    set("state","Turn1");
    set("correct_questions1",0);
    set("wrong_questions1",0);
    saveIt();
    User user = this.parent(User.class);
    user.set("lives",user.getLives()-1).saveIt();
  }

  public void setRound(Integer round){
    set("round",round).saveIt();
  }

  public void setUserId(Integer uId){
    set("user1_id",uId).saveIt();
  }

  public void setState(String state){
    set("state",state).saveIt();
  }

  public void setTotalRounds(Integer tRound){
    set("total_rounds",tRound).saveIt();
  }

  public void setQuestionsCorrect(Integer cc){
    set("correct_questions1",cc).saveIt();
  }

  public void setQuestionsIncorrect(Integer ic){
    set("wrong_questions1",ic).saveIt();
  }

  public Integer getRound(){
    return (Integer) get("round");
  }

  public Long getUserId(){
    return (Long) get("user1_id");
  }
  
  public String getState(){
    return (String) get("state");
  }

  public Integer getTotalRounds(){
    return (Integer) get("total_rounds");
  }

  public Integer getQuestionsCorrect(){
    return (Integer) get("correct_questions1");
  }

  public Integer getQuestionsIncorrect(){
    return (Integer) get("wrong_questions1");
  }

  public Integer getGameId(){
    return getInteger("id");
  }

  /**
  *Metodo que permite rendirse durante una partida
  **/
  public void whiteFlag()
    {set("state","Finalized");}
  

  public String state(){
    return (String) this.get("state");
  }

  public void incrementRound(){
    this.set("round",(Integer)this.get("round")+1).saveIt();
  }

  public void finalized(){
    this.set("state","Finalized").saveIt();
  }

  public Integer getActualRound()
  {return (Integer)this.get("round");}

  public void updateGame(boolean correct){
    if(correct)
      set("correct_questions1",(Integer)get("correct_questions1")+1).saveIt();
    else
      set("wrong_questions1",(Integer)get("wrong_questions1")+1).saveIt();
  }
}