CREATE TABLE users (
  id  int(11) auto_increment PRIMARY KEY,
  username  VARCHAR(128),
  password	VARCHAR(128),
  email	VARCHAR(128),
  lifes INT,
  level INT,
  total_points 	INT,
  correct_questions INT,
  incorrect_questions INT,
  total_questions INT,
  created_at DATETIME,
  updated_at DATETIME
)ENGINE=InnoDB;
