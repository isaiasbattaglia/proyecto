package trivia;

import org.javalite.activejdbc.Model;
import java.util.*;

public class Question extends Model {
  static{
    validatePresenceOf("description").message("Please, provide a description"); 
    validatePresenceOf("answer1").message("Please, provide all answers"); 
    validatePresenceOf("answer2").message("Please, provide all answers"); 
    validatePresenceOf("answer3").message("Please, provide all answers"); 
    validatePresenceOf("answer4").message("Please, provide all answers"); 
  }
  /**
  *Constructo de la clase Question
  **/
  public Question(){}
  /**
  *Constructor de la clase Question
  *@Param description descripcion pregunta, ans1,ans2,ans2,ans4 respuestas a la pregunta
  **/
  public Question(String description,String ans1, String ans2, String ans3, String ans4, Long id_C){
    validatePresenceOf("description").message("Please, provide a description"); 
    validatePresenceOf("answer1").message("Please, provide all answers"); 
    validatePresenceOf("answer2").message("Please, provide all answers"); 
    validatePresenceOf("answer3").message("Please, provide all answers"); 
    validatePresenceOf("answer4").message("Please, provide all answers"); 
	  set("description", description);
    set("answer1",ans1);
    set("answer2",ans2);
    set("answer3",ans3);
    set("answer4",ans4);
    set("category_id",id_C);
    saveIt();
	}

  public void setDescription(String description){
    set("description", description);
  }

  public void setAnswer1(String a1){
    set("answer1",a1);
  }
  public void setAnswer2(String a2){
    set("answer2",a2);
  }
  public void setAnswer3(String a3){
    set("answer3",a3);
  }
  public void setAnswer4(String a4){
    set("answer4",a4);
  }

  public void setCategoryId(Integer id){
    set("category_id",id);
  }

  public String getDescription(){
    return (String)get("description");
  }

  public String getAnswer1(){
    return (String)get("answer1");
  }

  public String getAnswer2(){
    return (String)get("answer2");
  }
  public String getAnswer3(){
    return (String)get("answer3");
  }
  public String getAnswer4(){
    return (String)get("answer4");
  }
  public Integer getCategoryId(){
    return (Integer)get("category_id");
  }

  //Obtiene la categoria(padre) de la pregunta.
  public Category getCategory(){
    Category c = this.parent(Category.class);
    return c;
  }
  
}