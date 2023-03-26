MERGE INTO PUBLIC."rating"
    USING (VALUES (1, 'G')) s(rating_id, name)
    ON PUBLIC."rating".RATING_ID=s.rating_id
    WHEN NOT MATCHED THEN INSERT VALUES (1, 'G');

MERGE INTO PUBLIC."rating"
    USING (VALUES (2, 'PG')) s(rating_id, name)
    ON PUBLIC."rating".RATING_ID=s.rating_id
    WHEN NOT MATCHED THEN INSERT VALUES (2, 'PG');

MERGE INTO PUBLIC."rating"
    USING (VALUES (3, 'PG_13')) s(rating_id, name)
    ON PUBLIC."rating".RATING_ID=s.rating_id
    WHEN NOT MATCHED THEN INSERT VALUES (3, 'PG_13');

MERGE INTO PUBLIC."rating"
    USING (VALUES (4, 'R')) s(rating_id, name)
    ON PUBLIC."rating".RATING_ID=s.rating_id
    WHEN NOT MATCHED THEN INSERT VALUES (4, 'R');

MERGE INTO PUBLIC."rating"
    USING (VALUES (5, 'NC_17')) s(rating_id, name)
    ON PUBLIC."rating".RATING_ID=s.rating_id
    WHEN NOT MATCHED THEN INSERT VALUES (5, 'NC_17');

MERGE INTO PUBLIC."genres"
    USING (VALUES (1, 'Комедия')) s(genre_id, name)
    ON PUBLIC."genres".GENRE_ID=s.genre_id
    WHEN NOT MATCHED THEN INSERT VALUES (1, 'Комедия');

MERGE INTO PUBLIC."genres"
    USING (VALUES (2, 'Драма')) s(genre_id, name)
    ON PUBLIC."genres".GENRE_ID=s.genre_id
    WHEN NOT MATCHED THEN INSERT VALUES (2, 'Драма');

MERGE INTO PUBLIC."genres"
    USING (VALUES (3, 'Мультфильм')) s(genre_id, name)
    ON PUBLIC."genres".GENRE_ID=s.genre_id
    WHEN NOT MATCHED THEN INSERT VALUES (3, 'Мультфильм');

MERGE INTO PUBLIC."genres"
    USING (VALUES (4, 'Триллер')) s(genre_id, name)
    ON PUBLIC."genres".GENRE_ID=s.genre_id
    WHEN NOT MATCHED THEN INSERT VALUES (4, 'Триллер');

MERGE INTO PUBLIC."genres"
    USING (VALUES (5, 'Документальный')) s(genre_id, name)
    ON PUBLIC."genres".GENRE_ID=s.genre_id
    WHEN NOT MATCHED THEN INSERT VALUES (5, 'Документальный');

MERGE INTO PUBLIC."genres"
    USING (VALUES (6, 'Боевик')) s(genre_id, name)
    ON PUBLIC."genres".GENRE_ID=s.genre_id
    WHEN NOT MATCHED THEN INSERT VALUES (6, 'Боевик');

MERGE INTO PUBLIC."user"
    USING (VALUES (1, 'Evan', 'god_of_war', '1994-11-11', 'gdofwar@mail.ru')) s(id, name, login, birthday, email)
    ON PUBLIC."user".ID=s.id
    WHEN NOT MATCHED THEN INSERT VALUES (1, 'Evan', 'god_of_war', '1994-11-11', 'gdofwar@mail.ru');

MERGE INTO PUBLIC."user"
    USING (VALUES (2, 'Eva', 'goddess_of_war', '1994-10-10', 'ost@mail.ru')) s(id, name, login, birthday, email)
    ON PUBLIC."user".ID=s.id
    WHEN NOT MATCHED THEN INSERT VALUES (2, 'Eva', 'goddess_of_war', '1994-10-10', 'ost@mail.ru');

MERGE INTO PUBLIC."user"
    USING (VALUES (3, 'Odetta', 'lady_swan', '1990-01-01', 'swanlake@mail.ru')) s(id, name, login, birthday, email)
    ON PUBLIC."user".ID=s.id
    WHEN NOT MATCHED THEN INSERT VALUES (3, 'Odetta', 'lady_swan', '1990-01-01', 'swanlake@mail.ru');

MERGE INTO PUBLIC."film"
    USING (VALUES (1, 'Ergo-Proxy', 'post-apocalyptic movie', '2006-01-01', 300, 4)) s(id, name, description, release_date, duration, rating)
    ON PUBLIC."film".ID=s.id
    WHEN NOT MATCHED THEN INSERT VALUES (1, 'Ergo-Proxy', 'post-apocalyptic movie', '2006-01-01', 300, 4);

MERGE INTO PUBLIC."film"
    USING (VALUES (2, 'Star Wars', 'la-la-la', '1984-01-01', 600, 3)) s(id, name, description, release_date, duration, rating)
    ON PUBLIC."film".ID=s.id
    WHEN NOT MATCHED THEN INSERT VALUES (2, 'Star Wars', 'la-la-la', '1984-01-01', 600, 3);


