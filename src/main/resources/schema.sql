drop table IF EXISTS feedback;
drop table IF EXISTS review;
drop table IF EXISTS LIKES;
drop table IF EXISTS FRIEND_REQUEST;
drop table IF EXISTS "USER";
drop table IF EXISTS FILM_GENRE;
drop table IF EXISTS FILM;
drop table IF EXISTS event;

create TABLE IF NOT EXISTS GENRE (
	GENRE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	NAME VARCHAR(40) NOT NULL,
	CONSTRAINT GENRE_PK PRIMARY KEY (GENRE_ID)
);
create unique index IF NOT EXISTS PRIMARY_KEY_4 ON GENRE (GENRE_ID);

create TABLE IF NOT EXISTS MPA_RATING (
    RATING_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	RATING_NAME VARCHAR(40) NOT NULL,
	CONSTRAINT MPA_RATING_PK PRIMARY KEY (RATING_ID)
);
create unique index IF NOT EXISTS PRIMARY_KEY_44 ON MPA_RATING (RATING_NAME);

create TABLE IF NOT EXISTS FILM (
	FILM_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	NAME VARCHAR(40) NOT NULL,
	DESCRIPTION VARCHAR,
	RELEASE_DATE DATE NOT NULL,
	DURATION INTEGER,
	RATING_ID INTEGER NOT NULL,
	CONSTRAINT FILM_PK PRIMARY KEY (FILM_ID),
	CONSTRAINT FILM_FK FOREIGN KEY (RATING_ID) REFERENCES MPA_RATING(RATING_ID) ON delete RESTRICT ON update RESTRICT
);
create index IF NOT EXISTS FILM_FK_INDEX_2 ON FILM (RATING_ID);
create unique index IF NOT EXISTS PRIMARY_KEY_2 ON FILM (FILM_ID);

create TABLE IF NOT EXISTS FILM_GENRE (
	FILM_GENRE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	FILM_ID INTEGER NOT NULL,
	GENRE_ID INTEGER NOT NULL,
	CONSTRAINT FILM_GENRE_PK PRIMARY KEY (FILM_GENRE_ID),
	CONSTRAINT FILM_GENRE_FK FOREIGN KEY (GENRE_ID) REFERENCES GENRE(GENRE_ID) ON delete CASCADE ON update CASCADE,
	CONSTRAINT FILM_GENRE_FK_1 FOREIGN KEY (FILM_ID) REFERENCES FILM(FILM_ID) ON delete CASCADE ON update CASCADE
);
create index IF NOT EXISTS FILM_GENRE_FK_1_INDEX_7 ON FILM_GENRE (FILM_ID);
create index IF NOT EXISTS FILM_GENRE_FK_INDEX_7 ON FILM_GENRE (GENRE_ID);
create unique index IF NOT EXISTS PRIMARY_KEY_7 ON FILM_GENRE (FILM_GENRE_ID);

create TABLE IF NOT EXISTS "USER" (
	USER_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	EMAIL VARCHAR(320) NOT NULL,
	LOGIN VARCHAR(40) NOT NULL,
	NAME VARCHAR(100) NOT NULL,
	BIRTHDAY DATE NOT NULL,
	CONSTRAINT USER_PK PRIMARY KEY (USER_ID)
);
create unique index IF NOT EXISTS PRIMARY_KEY_3 ON "USER" (USER_ID);

create TABLE IF NOT EXISTS FRIEND_REQUEST (
	FRIEND_REQUEST_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	INITIATOR_USER_ID INTEGER NOT NULL,
	TARGET_USER_ID INTEGER NOT NULL,
	CONFIRMED BOOLEAN DEFAULT FALSE,
	CONSTRAINT FRIEND_REQUEST_PK PRIMARY KEY (FRIEND_REQUEST_ID),
	CONSTRAINT FRIEND_REQUEST_FK FOREIGN KEY (INITIATOR_USER_ID) REFERENCES "USER"(USER_ID) ON delete CASCADE ON update CASCADE,
	CONSTRAINT FRIEND_REQUEST_FK_1 FOREIGN KEY (TARGET_USER_ID) REFERENCES "USER"(USER_ID) ON delete CASCADE ON update CASCADE
);
create index IF NOT EXISTS FRIEND_REQUEST_FK_1_INDEX_4 ON FRIEND_REQUEST (TARGET_USER_ID);
create index IF NOT EXISTS FRIEND_REQUEST_FK_INDEX_4 ON FRIEND_REQUEST (INITIATOR_USER_ID);
create unique index IF NOT EXISTS PRIMARY_KEY_45 ON FRIEND_REQUEST (FRIEND_REQUEST_ID);

create TABLE IF NOT EXISTS LIKES (
	LIKE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	USER_ID INTEGER NOT NULL,
	FILM_ID INTEGER NOT NULL,
	CONSTRAINT LIKES_PK PRIMARY KEY (LIKE_ID),
	CONSTRAINT LIKES_FK FOREIGN KEY (USER_ID) REFERENCES "USER"(USER_ID) ON delete CASCADE ON update CASCADE,
	CONSTRAINT LIKES_FK_1 FOREIGN KEY (FILM_ID) REFERENCES FILM(FILM_ID) ON delete CASCADE ON update CASCADE

);
create index IF NOT EXISTS LIKES_FK_1_INDEX_4 ON LIKES (FILM_ID);
create index IF NOT EXISTS LIKES_FK_INDEX_4 ON LIKES (USER_ID);
create unique index IF NOT EXISTS PRIMARY_KEY_451 ON LIKES (LIKE_ID);

create TABLE IF NOT EXISTS review (
  review_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  content VARCHAR(1024),
  is_positive boolean default null,
  user_id integer NOT NULL,
  film_id integer NOT NULL,
  useful integer default 0,
  CONSTRAINT review_fk_1 FOREIGN KEY (USER_ID) REFERENCES "USER"(USER_ID) ON delete RESTRICT ON update RESTRICT,
  CONSTRAINT review_fk_2 FOREIGN KEY (FILM_ID) REFERENCES FILM(FILM_ID) ON delete RESTRICT ON update RESTRICT
);

create TABLE IF NOT EXISTS feedback (
  feedback_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  review_id integer,
  user_id integer NOT NULL,
  is_useful boolean default null,
  CONSTRAINT feedback_fk_1 FOREIGN KEY (review_id) REFERENCES review(review_id) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT feedback_fk_2 FOREIGN KEY (USER_ID) REFERENCES "USER"(USER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

create TABLE IF NOT EXISTS DIRECTOR (
  DIRECTOR_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(1024),
  CONSTRAINT DIRECTOR_PK PRIMARY KEY (DIRECTOR_ID)
);

create TABLE IF NOT EXISTS FILM_DIRECTOR (
	FILM_DIRECTOR_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	FILM_ID INTEGER NOT NULL,
	DIRECTOR_ID INTEGER NOT NULL,
	CONSTRAINT FILM_DIRECTOR_PK PRIMARY KEY (FILM_DIRECTOR_ID),
	CONSTRAINT FILM_DIRECTOR_FK FOREIGN KEY (DIRECTOR_ID) REFERENCES DIRECTOR(DIRECTOR_ID) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FILM_DIRECTOR_FK_1 FOREIGN KEY (FILM_ID) REFERENCES FILM(FILM_ID) ON DELETE CASCADE ON UPDATE RESTRICT
);

create table if not exists event (
    event_id integer generated by default as identity primary key,
    ts bigint,
    user_id integer,
    event_type varchar(256),
    operation varchar(256),
    entity_id integer,
    constraint user_id_fk foreign key (user_id) references "USER"(user_id) on delete cascade on update restrict
);