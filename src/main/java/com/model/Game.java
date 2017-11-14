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
    user.set("lifes",user.getLifes()-1).saveIt();
  }

    public Game(Integer user1_id, Integer user2_id){
    validatePresenceOf("user1_id").message("Please, provide a user id");
    validatePresenceOf("correct_questions1").message("Please, initialize correct_questions");
    validatePresenceOf("wrong_questions1").message("Please, initialize wrong_questions");
    set("round",0);
    set("total_rounds",5);
    set("user1_id",user1_id);
    set("user2_id",user2_id);
    set("state","Turn1");
    set("historia",0);
    set("geografia",0);
    set("Deportes",0);
    set("entretenimiento",0);
    set("arte",0);
    set("ciencia",0);
    set("amount_of_categories1",0);
    set("amount_of_categories2",0);
    set("correct_questions1",0);
    set("wrong_questions1",0);
    set("correct_questions2",0);
    set("wrong_questions2",0);
    set("mode","Turn");
    saveIt();
    User user = this.parent(User.class);  //Get user1
    user.set("lifes",user.getLifes()-1).saveIt();
  }

  public Game(Integer user1_id, Integer user2_id, String mode){
    validatePresenceOf("user1_id").message("Please, provide a user id");
    validatePresenceOf("correct_questions1").message("Please, initialize correct_questions");
    validatePresenceOf("wrong_questions1").message("Please, initialize wrong_questions");
    set("round",0);
    set("total_rounds",5);
    set("user1_id",user1_id);
    set("user2_id",user2_id);
    set("correct_questions1",0);
    set("wrong_questions1",0);
    set("correct_questions2",0);
    set("wrong_questions2",0);
    set("mode",mode);
    saveIt();
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

  public void setQuestionsCorrect1(Integer cc){
    set("correct_questions1",cc).saveIt();
  }

  public void setQuestionsIncorrect1(Integer ic){
    set("wrong_questions1",ic).saveIt();
  }

  public void setQuestionsCorrect2(Integer cc){
    set("correct_questions2",cc).saveIt();
  }

  public void setQuestionsIncorrect2(Integer ic){
    set("wrong_questions2",ic).saveIt();
  }

  public void setUser1Answer(String answer){
    set("user1_Answer",answer).saveIt();
  }

  public void setUser2Answer(String answer){
    set("user2_Answer",answer).saveIt();
  }

  public void setCurrentQuestion(String question){
    set("currentQuestion",question).saveIt();
  }
  public void setAmountOfCategories1(Integer num){
    set("amount_of_categories1",num).saveIt();
  }
  public void setAmountOfCategories2(Integer num){
    set("amount_of_categories2",num).saveIt();
  }

  public Integer getRound(){
    return (Integer) get("round");
  }

  public Integer getUser1Id(){
    return (Integer) get("user1_id");
  }
  public Integer getUser2Id(){
    return (Integer) get("user2_id");
  }
  
  public String getState(){
    return (String) get("state");
  }

  public Integer getTotalRounds(){
    return (Integer) get("total_rounds");
  }

  public Integer getQuestionsCorrect1(){
    return (Integer) get("correct_questions1");
  }

  public Integer getQuestionsIncorrect1(){
    return (Integer) get("wrong_questions1");
  }

  public Integer getQuestionsCorrect2(){
    return (Integer) get("correct_questions2");
  }

  public Integer getQuestionsIncorrect2(){
    return (Integer) get("wrong_questions2");
  }
  public Integer getAmountOfCategories1(){
    return (Integer) get("amount_of_categories1");
  }
   public Integer getAmountOfCategories2(){
    return (Integer) get("amount_of_categories2");
  }


  public Integer getGameId(){
    return getInteger("id");
  }

  public Integer getCurrentWinnerOfCategory(String name){
    return (Integer)get(name);
  }

  public void setNewWinnerOfCategory(String name, Integer newWinner){
    set(name,newWinner).saveIt();
  }

  public Integer getHistory(){
    return (Integer)get("historia");
  }
  public Integer getGeografy(){
    return (Integer)get("geografia");
  }
  public Integer getArt(){
    return (Integer)get("arte");
  }
  public Integer getScience(){
    return (Integer)get("ciencia");
  }
  public Integer getEntreteniment(){
    return (Integer)get("entretenimiento");
  }
  public Integer getSports(){
    return (Integer)get("deportes");
  }
  public String getUser1Answer(){
    return (String)get("user1_Answer");
  }
  public String getUser2Answer(){
    return (String)get("user2_Answer");
  }
  public String getCurrentQuestion(){
    return (String) get("currentQuestion");
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