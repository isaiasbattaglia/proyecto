CREATE TABLE games (
  id INT(11) auto_increment PRIMARY KEY,
  round INT,
  user1_id INT(11),
  user2_id INT(11),
  state ENUM('Turn1','Turn2','Finalized'),
  deleteBy Int,
  Historia INT,
  Geografia INT,
  Deportes INT,
  Entretenimiento INT,
  Arte INT,
  Ciencia INT,
  amount_of_categories1 INT,
  amount_of_categories2 INT,
  total_rounds INT,
  correct_questions1 INT,
  wrong_questions1 INT,
  correct_questions2 INT,
  wrong_questions2 INT,
  created_at DATETIME,
  updated_at DATETIME
)ENGINE=InnoDB;
