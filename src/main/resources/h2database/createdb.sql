CREATE TABLE IF NOT EXISTS whitelist
(
	id 		BIGINT NOT NULL IDENTITY,
	email 	VARCHAR(50),
	OPT_LOCK_VERSION INTEGER,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_type
(
	id 		BIGINT NOT NULL IDENTITY,
	name	VARCHAR(10),
	opt_lock_version INTEGER,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS app_user
(
	id 			BIGINT NOT NULL IDENTITY,
	email 		VARCHAR(50) NOT NULL,
	password_hash	CHAR(64) NOT NULL,
	first_name	VARCHAR(30) NOT NULL,
	last_name	VARCHAR(40) NOT NULL,
	blocked 	BOOLEAN DEFAULT TRUE,
	user_type_id BIGINT NOT NULL,
	opt_lock_version INTEGER,
	PRIMARY KEY (id),
	FOREIGN KEY (user_type_id) REFERENCES user_type(id)
);

CREATE TABLE IF NOT EXISTS survey
(
	id 			BIGINT NOT NULL IDENTITY,
	name 		VARCHAR(80) NOT NULL,
	description VARCHAR(500),
	url 		CHAR(32) NOT NULL,
	user_id 	BIGINT NOT NULL,
	opt_lock_version INTEGER,
	PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE IF NOT EXISTS question_type
(
	id 		BIGINT NOT NULL IDENTITY,
	name 	VARCHAR(20),
	opt_lock_version INTEGER,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS question
(
	id 			BIGINT NOT NULL IDENTITY,
	text 		VARCHAR(300) NOT NULL,
	required 	BOOLEAN DEFAULT TRUE,
	survey_id 	BIGINT NOT NULL,
	question_type_id BIGINT NOT NULL,
	opt_lock_version INTEGER,
	PRIMARY KEY (id),
	FOREIGN KEY (survey_id) REFERENCES survey(id),
	FOREIGN KEY (question_type_id) REFERENCES question_type(id)
);


CREATE TABLE IF NOT EXISTS choice
(
	id 		BIGINT NOT NULL IDENTITY,
	text 	VARCHAR(200) NOT NULL,
	question_id BIGINT NOT NULL,
	opt_lock_version INTEGER,
	PRIMARY KEY (id),
	FOREIGN KEY (question_id) REFERENCES question(id)
);

CREATE TABLE IF NOT EXISTS interval_choice
(
	id 			BIGINT NOT NULL IDENTITY,
	min_value 	INTEGER,
	max_value	INTEGER,
	question_id BIGINT NOT NULL,
	opt_lock_version INTEGER,
	PRIMARY KEY (id),
	FOREIGN KEY (question_id) REFERENCES question(id)
);

CREATE TABLE IF NOT EXISTS survey_result
(
	id 			BIGINT NOT NULL IDENTITY,
	survey_id	BIGINT NOT NULL,
	opt_lock_version INTEGER,
	PRIMARY KEY (id),
	FOREIGN KEY (survey_id) REFERENCES survey(id)
);

CREATE TABLE IF NOT EXISTS answer
(
	id 			BIGINT NOT NULL IDENTITY,
	result_id	BIGINT NOT NULL,
	question_id BIGINT NOT NULL,
	choice_id 	BIGINT,
	answer		VARCHAR(1000),
	opt_lock_version INTEGER,
	PRIMARY KEY (id),
	FOREIGN KEY (result_id) REFERENCES survey_result(id),
	FOREIGN KEY (question_id) REFERENCES question(id),
	FOREIGN KEY (choice_id) REFERENCES choice(id)
);

ALTER TABLE answer ADD IF NOT EXISTS opt_lock_version INTEGER;
ALTER TABLE app_user ADD IF NOT EXISTS opt_lock_version INTEGER;
ALTER TABLE choice ADD IF NOT EXISTS opt_lock_version INTEGER;
ALTER TABLE interval_choice ADD IF NOT EXISTS opt_lock_version INTEGER;
ALTER TABLE question ADD IF NOT EXISTS opt_lock_version INTEGER;
ALTER TABLE question_type ADD IF NOT EXISTS opt_lock_version INTEGER;
ALTER TABLE survey ADD IF NOT EXISTS opt_lock_version INTEGER;
ALTER TABLE survey_result ADD IF NOT EXISTS opt_lock_version INTEGER;
ALTER TABLE user_type ADD IF NOT EXISTS opt_lock_version INTEGER;
ALTER TABLE whitelist ADD IF NOT EXISTS opt_lock_version INTEGER;