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
  last_life_update DATETIME,
  created_at DATETIME,
  updated_at DATETIME
)ENGINE=InnoDB;
SET GLOBAL event_scheduler = ON;
CREATE EVENT updateLives
ON SCHEDULE EVERY 60 SECOND  STARTS '2017-01-01 00:00:00' DO 
   UPDATE users SET lifes = lifes+1, updated_at=now(), last_life_update=now() 
   where lifes<3 and timestampdiff(minute,last_life_update,now())>=30;