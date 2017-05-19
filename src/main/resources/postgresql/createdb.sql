CREATE TABLE IF NOT EXISTS whitelist
(
	id 			BIGSERIAL PRIMARY KEY,
	email 	VARCHAR(50) NOT NULL,
	opt_lock_version INTEGER
);

CREATE TABLE IF NOT EXISTS user_type
(
	id 		BIGSERIAL PRIMARY KEY,
	name	VARCHAR(10),
	opt_lock_version INTEGER
);

CREATE TABLE IF NOT EXISTS app_user
(
	id 					BIGSERIAL PRIMARY KEY,
	email 			VARCHAR(50) NOT NULL,
	password_hash	CHAR(64) NOT NULL,
	first_name	VARCHAR(30) NOT NULL,
	last_name		VARCHAR(40) NOT NULL,
	blocked 		BOOLEAN DEFAULT TRUE,
	user_type_id BIGINT NOT NULL,
	opt_lock_version INTEGER,
	FOREIGN KEY (user_type_id) REFERENCES user_type(id)
);

CREATE TABLE IF NOT EXISTS survey
(
	id 				BIGSERIAL PRIMARY KEY,
	name 			VARCHAR(80) NOT NULL,
	description VARCHAR(500),
	url 			CHAR(32) NOT NULL,
	expiration_time TIMESTAMP,
	user_id 	BIGINT NOT NULL,
	public 		BOOLEAN DEFAULT TRUE,
	opt_lock_version INTEGER,
	FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE IF NOT EXISTS survey_page
(
	id					BIGSERIAL PRIMARY KEY,
	number			INTEGER NOT NULL,
	survey_id		BIGINT NOT NULL,
	opt_lock_version INTEGER,
	FOREIGN KEY (survey_id) REFERENCES survey(id)
);

CREATE TABLE IF NOT EXISTS question
(
	id 					BIGSERIAL PRIMARY KEY,
	text 				VARCHAR(300) NOT NULL,
	required 		BOOLEAN DEFAULT TRUE,
	position		INTEGER NOT NULL,
	survey_id 	BIGINT NOT NULL,
	page_id 		BIGINT NOT NULL,
	question_type_id BIGINT NOT NULL,
	opt_lock_version INTEGER,
	FOREIGN KEY (survey_id) REFERENCES survey(id),
	FOREIGN KEY (page_id) REFERENCES survey_page(id)
);

CREATE TABLE IF NOT EXISTS choice
(
	id 					BIGSERIAL PRIMARY KEY,
	text 				VARCHAR(200) NOT NULL,
	question_id BIGINT NOT NULL,
	opt_lock_version INTEGER,
	FOREIGN KEY (question_id) REFERENCES question(id)
);

CREATE TABLE IF NOT EXISTS interval_question
(
	id 					BIGSERIAL PRIMARY KEY,
	min_value 	INTEGER,
	max_value		INTEGER
);

CREATE TABLE IF NOT EXISTS survey_result
(
	id 								BIGSERIAL PRIMARY KEY,
	survey_id					BIGINT NOT NULL,
	url								CHAR(32),
	complete					BOOLEAN DEFAULT TRUE,
	opt_lock_version INTEGER,
	FOREIGN KEY (survey_id) REFERENCES survey(id)
);

CREATE TABLE IF NOT EXISTS answer
(
	id 					      BIGSERIAL PRIMARY KEY,
	result_id		      BIGINT NOT NULL,
	question_id       BIGINT NOT NULL,
	choice_id 	      BIGINT,
	text_answer			  VARCHAR(1000),
	number_answer     INTEGER,
	answer_type       CHAR(1),
	opt_lock_version  INTEGER,
	FOREIGN KEY (result_id) REFERENCES survey_result(id),
	FOREIGN KEY (question_id) REFERENCES question(id),
	FOREIGN KEY (choice_id) REFERENCES choice(id)
);

CREATE TABLE IF NOT EXISTS mail_expiration
(
	id											BIGSERIAL PRIMARY KEY,
	user_id									BIGINT,
	url										  CHAR(32) NOT NULL,
	expiration_date					TIMESTAMP,
	opt_lock_version 				INTEGER,
  FOREIGN KEY (user_id) REFERENCES app_user(id)
);

INSERT INTO user_type (NAME) VALUES ('Admin'), ('User');

CREATE TABLE IF NOT EXISTS free_text_question
(
	id 					BIGSERIAL PRIMARY KEY,
	FOREIGN KEY (id) REFERENCES question(id)
);

CREATE TABLE IF NOT EXISTS multiple_choice_question
(
	id 					BIGSERIAL PRIMARY KEY,
	FOREIGN KEY (id) REFERENCES question(id)
);

CREATE TABLE IF NOT EXISTS single_choice_question
(
	id 					BIGSERIAL PRIMARY KEY,
	FOREIGN KEY (id) REFERENCES question(id)
);

CREATE TABLE IF NOT EXISTS answer_choice
(
	answer_id BIGINT NOT NULL,
	choice_id BIGINT NOT NULL,
	FOREIGN KEY (answer_id) REFERENCES answer(id),
	FOREIGN KEY (choice_id) REFERENCES choice(id)
);

CREATE TABLE IF NOT EXISTS logs
(
	id BIGSERIAL PRIMARY KEY,
	user_name		VARCHAR(80) NOT NULL,
	user_email VARCHAR(50),
	rights	VARCHAR(10),
	time TIMESTAMP,
	class_name VARCHAR(100),
	method_name VARCHAR(100)

);