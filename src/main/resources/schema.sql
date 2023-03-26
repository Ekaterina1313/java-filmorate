
CREATE TABLE IF NOT EXISTS "user" (
    id long PRIMARY KEY,
    name varchar(100),
    login varchar(100) NOT NULL,
    birthday timestamp,
    email varchar(100) NOT NULL
    );

CREATE TABLE IF NOT EXISTS "friendship" (
    friendship_id long GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    from_id long REFERENCES "user" (id),
    to_id long REFERENCES "user" (id),
    status varchar(50)
    );

CREATE TABLE IF NOT EXISTS "rating" (
    rating_id INTEGER PRIMARY KEY,
     name varchar(20) NOT NULL
    );

CREATE TABLE IF NOT EXISTS "film" (
    id long PRIMARY KEY,
     name varchar(100) NOT NULL,
    description varchar(200),
    release_date timestamp,
    duration int,
    rating varchar(100) REFERENCES "rating" (rating_id)
    );

CREATE TABLE IF NOT EXISTS "genres" (
     genre_id INTEGER PRIMARY KEY,
    name varchar(100) NOT NULL
    );

CREATE TABLE IF NOT EXISTS "film_genres" (
    film_genres_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id long REFERENCES "film" (id),
    genre_id int REFERENCES "genres" (genre_id)
    );

CREATE TABLE IF NOT EXISTS "likes" (
    likes_id long GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id long REFERENCES "film" (id),
    user_id long REFERENCES "user" (id)
    );