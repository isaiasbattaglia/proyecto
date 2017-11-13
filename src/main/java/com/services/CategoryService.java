package trivia;
import java.util.List;
import java.util.Random;
import java.util.List;
public class CategoryService{

   /**
   * This method returns a random category.
   * @pre. true.
   * @return a Category object representing a random category.
   * @post. a Category object representing a random category, is returned.
  */
  public static Category randomCategory(){
  	List<Category> list = Category.findAll(); 
  	Random r = new Random();
  	return list.get(r.nextInt(list.size()));
  }

   /**
   * This method returns a category that corresponds to the id.
   * @param id id of the category.
   * @pre. true.
   * @return a Category.
   * @post. a category that corresponds to the id, is returned.
  */
  public static Category getCategory(Integer id){
  	return Category.findById(id);
  }

   /**
   * This method returns a category that matches with the given name.
   * @param name name of category, this value must correspond with some in tCategory.
   * @pre. true.
   * @return a Category.
   * @post. a category that matches with the given name, is returned.
  */
  public static Integer getCategoryId(String name){
  	Category category = Category.findFirst("tCategory=?",name);
  	return category.getCategoryId();
  }

}