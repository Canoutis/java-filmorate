CREATE TABLE IF NOT EXISTS GENRE (
	GENRE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	NAME VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS MPA_RATING (
    RATING_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	RATING_NAME VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS FILM (
	FILM_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	NAME VARCHAR(40) NOT NULL,
	DESCRIPTION VARCHAR,
	RELEASE_DATE DATE NOT NULL,
	DURATION INTEGER,
	RATING_ID INTEGER NOT NULL,
	CONSTRAINT FILM_FK FOREIGN KEY (RATING_ID) REFERENCES MPA_RATING(RATING_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS FILM_GENRE (
	FILM_GENRE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	FILM_ID INTEGER NOT NULL,
	GENRE_ID INTEGER NOT NULL,
	CONSTRAINT FILM_GENRE_FK_1 FOREIGN KEY (GENRE_ID) REFERENCES GENRE(GENRE_ID) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FILM_GENRE_FK_2 FOREIGN KEY (FILM_ID) REFERENCES FILM(FILM_ID) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE IF NOT EXISTS "USER" (
	USER_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	EMAIL VARCHAR(320) NOT NULL,
	LOGIN VARCHAR(40) NOT NULL,
	NAME VARCHAR(100) NOT NULL,
	BIRTHDAY DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS FRIEND_REQUEST (
	--FRIEND_REQUEST_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	INITIATOR_USER_ID INTEGER NOT NULL,
	TARGET_USER_ID INTEGER NOT NULL,
	CONFIRMED BOOLEAN DEFAULT FALSE,
	CONSTRAINT FRIEND_REQUEST_UNIQUE UNIQUE (INITIATOR_USER_ID, TARGET_USER_ID),
	CONSTRAINT FRIEND_REQUEST_FK_1 FOREIGN KEY (INITIATOR_USER_ID) REFERENCES "USER"(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FRIEND_REQUEST_FK_2 FOREIGN KEY (TARGET_USER_ID) REFERENCES "USER"(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS LIKES (
	--LIKE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	USER_ID INTEGER NOT NULL,
	FILM_ID INTEGER NOT NULL,
	CONSTRAINT LIKES_UNIQUE UNIQUE (USER_ID, FILM_ID),
	CONSTRAINT LIKES_FK_1 FOREIGN KEY (USER_ID) REFERENCES "USER"(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT LIKES_FK_2 FOREIGN KEY (FILM_ID) REFERENCES FILM(FILM_ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS REVIEW (
  REVIEW_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  CONTENT VARCHAR(1024),
  IS_POSITIVE BOOLEAN NOT NULL,
  USER_ID INTEGER NOT NULL,
  FILM_ID INTEGER NOT NULL,
  USEFUL INTEGER DEFAULT 0,
  CONSTRAINT REVIEW_FK_1 FOREIGN KEY (USER_ID) REFERENCES "USER"(USER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT REVIEW_FK_2 FOREIGN KEY (FILM_ID) REFERENCES FILM(FILM_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS FEEDBACK (
  FEEDBACK_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  REVIEW_ID INTEGER,
  USER_ID INTEGER NOT NULL,
  IS_USEFUL BOOLEAN DEFAULT NULL,
  CONSTRAINT FEEDBACK_FK_1 FOREIGN KEY (REVIEW_ID) REFERENCES REVIEW(REVIEW_ID) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT FEEDBACK_FK_2 FOREIGN KEY (USER_ID) REFERENCES "USER"(USER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS DIRECTOR (
  DIRECTOR_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  NAME VARCHAR(1024)
);

CREATE TABLE IF NOT EXISTS FILM_DIRECTOR (
	FILM_DIRECTOR_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	FILM_ID INTEGER NOT NULL,
	DIRECTOR_ID INTEGER NOT NULL,
	CONSTRAINT FILM_DIRECTOR_FK_1 FOREIGN KEY (DIRECTOR_ID) REFERENCES DIRECTOR(DIRECTOR_ID) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FILM_DIRECTOR_FK_2 FOREIGN KEY (FILM_ID) REFERENCES FILM(FILM_ID) ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS EVENT (
    EVENT_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    TS BIGINT,
    USER_ID INTEGER,
    EVENT_TYPE VARCHAR(256),
    OPERATION VARCHAR(256),
    ENTITY_ID INTEGER,
    CONSTRAINT EVENT_FK FOREIGN KEY (USER_ID) REFERENCES "USER"(USER_ID) ON DELETE CASCADE ON UPDATE RESTRICT
);