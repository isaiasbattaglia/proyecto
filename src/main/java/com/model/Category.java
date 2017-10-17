package trivia;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.validation.UniquenessValidator;
import java.util.List;
import java.util.Random;


public class Category extends Model {
  static{
    validatePresenceOf("tCategory").message("Please, provide name of category");
    validateWith(new UniquenessValidator("tCategory")).message("This category name is already taken.");
  }

  /**
  *Constructor de la clase Category
  **/
  public Category(){}

  public Category(String name)
    {set("tCategory",name).saveIt();}
    
  public Integer getCategoryId()
    {return getInteger("id");}

  public String getTCategory()
    {return (String)get("tCategory");}

  public void setTCtegory(String tCategory)
    {set("tCategory",tCategory).saveIt();}

  /**
  *Metodo que obtiene una pregunta aleatoria correspondiente a esta(this) categoria
  *@Return Question aleatoria
  **/
  public Question getQuestion(){
  	List<Question> lst = this.getAll(Question.class);
    Random r = new Random();
    Integer i = r.nextInt(lst.size());
    return lst.get(i);
  }

}