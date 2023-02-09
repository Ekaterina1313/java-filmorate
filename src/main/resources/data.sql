INSERT INTO "rating" (rating_id, name)
VALUES (1, 'G');

INSERT INTO "rating" (rating_id, name)
VALUES (2, 'PG');

INSERT INTO "rating" (rating_id, name)
VALUES (3, 'PG_13');

INSERT INTO "rating" (rating_id, name)
VALUES (4, 'R');

INSERT INTO "rating" (rating_id, name)
VALUES (5, 'NC_17');

INSERT INTO "genres" (genre_id, name)
VALUES (1, 'Комедия');

INSERT INTO "genres" (genre_id, name)
VALUES (2, 'Драма');

INSERT INTO "genres" (genre_id, name)
VALUES (3, 'Мультфильм');

INSERT INTO "genres" (genre_id, name)
VALUES (4, 'Триллер');

INSERT INTO "genres" (genre_id, name)
VALUES (5, 'Документальный');

INSERT INTO "genres" (genre_id, name)
VALUES (6, 'Боевик');

INSERT INTO "user" (id, name, login, birthday, email)
VALUES (1, 'Evan', 'god_of_war', '1994-11-11', 'gdofwar@mail.ru');

INSERT INTO "user" (id, name, login, birthday, email)
VALUES (2, 'Eva', 'goddess_of_war', '1994-10-10', 'ost@mail.ru');

INSERT INTO "user" (id, name, login, birthday, email)
VALUES (3, 'Odetta', 'lady_swan', '1990-01-01', 'swanlake@mail.ru');

INSERT INTO "film" (id, name, description, release_date, duration, rating)
VALUES (1, 'Ergo-Proxy', 'post-apocalyptic movie', '2006-01-01', 300, 4);

INSERT INTO "film" (id, name, description, release_date, duration, rating)
VALUES (2, 'Star Wars', 'la-la-la', '1984-01-01', 600, 3);


