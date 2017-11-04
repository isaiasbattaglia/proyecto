package trivia;
import java.util.List;
import java.util.Random;
import java.util.List;
public class CategoryService{

  public static Category randomCategory(){
  	List<Category> list = Category.findAll(); 
  	Random r = new Random();
  	return list.get(r.nextInt(list.size()));
  }

  public static Category getCategory(Integer id){
  	return Category.findById(id);
  }

  public static Integer getCategoryId(String name){
  	Category category = Category.findFirst("tCategory=?",name);
  	return category.getCategoryId();
  }

}