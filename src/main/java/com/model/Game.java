package trivia;

import org.javalite.activejdbc.Model;

public class Game extends Model {
  static{
    validatePresenceOf("user_id").message("Please, provide a user id");
    validatePresenceOf("questions_Correct").message("Please, initialize questions_Correct");
    validatePresenceOf("questions_Incorrect").message("Please, initialize questions_Incorrect");
  }
  
  /**
  *Constructor por defecto de la clase Game
  **/
  public Game(){}

  public Game(Integer user_id){
    validatePresenceOf("user_id").message("Please, provide a user id");
    validatePresenceOf("questions_Correct").message("Please, initialize questions_Correct");
    validatePresenceOf("questions_Incorrect").message("Please, initialize questions_Incorrect");
  	set("round",0);
    set("total_rounds",5);
  	set("user_id",user_id);
    set("state","en_proceso");
    set("questions_Correct",0);
    set("questions_Incorrect",0);
    saveIt();
    User user = this.parent(User.class);
    user.set("lives",user.getLives()-1).saveIt();
  }

  public void setRound(Integer round){
    set("round",round).saveIt();
  }

  public void setUserId(Integer uId){
    set("user_id",uId).saveIt();
  }

  public void setState(String state){
    set("state",state).saveIt();
  }

  public void setTotalRounds(Integer tRound){
    set("total_rounds",tRound).saveIt();
  }

  public void setQuestionsCorrect(Integer cc){
    set("questions_Correct",cc).saveIt();
  }

  public void setQuestionsIncorrect(Integer ic){
    set("questions_Incorrect",ic).saveIt();
  }

  public Integer getRound(){
    return (Integer) get("round");
  }

  public Long getUserId(){
    return (Long) get("user_id");
  }
  
  public String getState(){
    return (String) get("state");
  }

  public Integer getTotalRounds(){
    return (Integer) get("total_rounds");
  }

  public Integer getQuestionsCorrect(){
    return (Integer) get("questions_Correct");
  }

  public Integer getQuestionsIncorrect(){
    return (Integer) get("questions_Incorrect");
  }

  public Integer getGameId(){
    return getInteger("id");
  }

















  /**
  *Metodo que permite rendirse durante una partida
  **/
  public void whiteFlag()
    {set("state","finalizada");}
  

  public String state(){
    return (String) this.get("state");
  }

  public void incrementRound(){
    this.set("round",(Integer)this.get("round")+1).saveIt();
  }

  public void finalized(){
    this.set("state","finalizado").saveIt();
  }

  public Integer getActualRound()
  {return (Integer)this.get("round");}

  public void updateGame(boolean correct){
    if(correct)
      set("questions_Correct",(Integer)get("questions_Correct")+1).saveIt();
    else
      set("questions_Incorrect",(Integer)get("questions_Incorrect")+1).saveIt();
  }
}