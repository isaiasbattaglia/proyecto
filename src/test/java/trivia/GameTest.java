package trivia;

import trivia.User;

import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameTest{
  @Before
  public void before(){
    Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia_test", "root", "root");
    System.out.println("UserTest setup");
    Base.openTransaction();
  }

  @After
  public void after(){
    System.out.println("UserTest tearDown");
    Base.rollbackTransaction();
    Base.close();
  }

  @Test
  public void validateIncrementCorrectQuestions(){
    User u = new User();
    u.set("username","matias");
    u.set("password","cabj");
    u.set("email","maty@cabj.com");
    u.set("lifes", 3);
    u.set("lifes", 3);
    u.set("total_points",0);
    u.set("correct_questions",0);
    u.set("incorrect_questions",0);
    u.set("total_questions",0);
    u.set("level",0);
    u.saveIt();

    Object id_O_u = u.getId();
    Long id = Long.parseLong(id_O_u.toString());
    Game game = new Game();
    game.set("user1_id",id);
    game.set("correct_questions1",0);
    game.set("wrong_questions1",0);
    game.saveIt();
    
    Category c = new Category("Historia");
    c.saveIt();
    Object id_O_C = c.getId();
    String id_S = id_O_C.toString();
    Long id_cat = Long.parseLong(id_S);

    Question question = new Question();
    question.set("description","pregunta");
    question.set("answer1","answer1");
    question.set("answer2","answer2");
    question.set("answer3","answer3");
    question.set("answer4","answer4");
    question.set("category_id",id_cat);
    question.saveIt();
    answerQuestion(true,game);
    assertEquals(((((Integer)game.get("correct_questions1")).compareTo(new Integer(0)))>0),true);  //Se incremento la cantidad de preguntas correctas.
  }

  @Test
  public void validateIncrementIncorrectQuestions(){
    User u = new User();
    u.set("username","matias");
    u.set("password","cabj");
    u.set("email","maty@cabj.com");
    u.set("lifes", 3);
    u.set("lifes", 3);
    u.set("total_points",0);
    u.set("correct_questions",0);
    u.set("incorrect_questions",0);
    u.set("total_questions",0);
    u.set("level",0);
    u.saveIt();

    Object id_O = u.getId();
    Long id = Long.parseLong(id_O.toString());
    Game game = new Game();
    game.set("user1_id",id);
    game.set("correct_questions1",0);
    game.set("wrong_questions1",0);
    game.saveIt();

    Category c = new Category("Historia");
    c.saveIt();
    Object id_O_C = c.getId();
    String id_S = id_O_C.toString();
    Long id_cat = Long.parseLong(id_S);

    Question question = new Question();
    question.set("description","pregunta");
    question.set("answer1","answer1");
    question.set("answer2","answer2");
    question.set("answer3","answer3");
    question.set("answer4","answer4");
    question.set("category_id",id_cat);
    question.saveIt();
    answerQuestion(false,game);
    assertEquals(((((Integer)game.get("wrong_questions1")).compareTo(new Integer(0)))>0),true);// Se incremento la cantidad de preguntas incorrectas.
  }

  @Test
  public void validatePositiveAmountOfQuestionsCorrect(){
    User u = new User();
    u.set("username","matias");
    u.set("password","cabj");
    u.set("email","maty@cabj.com");
    u.set("lifes", 3);
    u.set("lifes", 3);
    u.set("total_points",0);
    u.set("correct_questions",0);
    u.set("incorrect_questions",0);
    u.set("total_questions",0);
    u.set("level",0);
    u.saveIt();

    Object id_O = u.getId();
    Long id = Long.parseLong(id_O.toString());
    Game game = new Game();
    game.set("user1_id",id);
    game.set("correct_questions1",-1);
    game.set("wrong_questions1",0);
    game.saveIt();
    assertEquals((((Integer)game.get("correct_questions1"))>=0),false);
  }

  private void answerQuestion(boolean question, Game game){
    if (question)
      game.updateGame(true);
    else
      game.updateGame(false);
    game.saveIt();    
  }
}


  /*@Test
  public void validateSubtractLive(){
    User u = new User();
    u.set("username","matias");
    u.set("password","cabj");
    u.set("email","maty@cabj.com");
    u.set("lifes", 3);
    u.set("lifes", 3);
    u.set("total_points",0);
    u.set("correct_questions",0);
    u.set("incorrect_questions",0);
    u.set("total_questions",0);
    u.set("level",0);
    u.saveIt();
    Object id_O = u.getId();
    Long id = Long.parseLong(id_O.toString());
    Game game = new Game(id);
    System.out.println(u.get("lifes"));
    game.saveIt();
    assertEquals((((Integer)u.get("lifes")).compareTo(new Integer(2)))==0,true);    
  }*/ 
  
//}

  /**
  *Metodo que permite jugar en una partida hasta que se responda mal, o se alcance el maximo de rondas permitidas
  **/
 /* public void play(Category c, Question q){
    User user = this.parent(User.class);
    if(!(this.get("state").equals("finalizado")) && (Integer)user.get("lifes")>0){
      user.set("lifes",(Integer)user.get("lifes")-1).saveIt();
      boolean valid = true;
      while(valid && (Integer)this.get("round")<5){ 
        this.add(category);
        valid = answerQuestion(true);
        this.set("round",(Integer)this.get("round")+1).saveIt();
      }
      if((Integer)this.get("round")>=20)
        this.set("state","finalizada").saveIt();
    }
  }
*/

    /*Category c = new Category("Deportes");
    Object id_O_c = c.getId();
    String id_S_C = id_S_C.toString();
    Long id_C = Long.parseLong(id_S_C);
    Question q = new Question("Pregunta","ans_1","ans_2","ans_3","ans_4",id_C);
    play();*
}*/