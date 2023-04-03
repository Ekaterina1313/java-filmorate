MERGE INTO rating
    USING (VALUES (1, 'G')) s(rating_id, name)
    ON rating.RATING_ID=s.rating_id
    WHEN NOT MATCHED THEN INSERT VALUES (1, 'G');

MERGE INTO rating
    USING (VALUES (2, 'PG')) s(rating_id, name)
    ON rating.RATING_ID=s.rating_id
    WHEN NOT MATCHED THEN INSERT VALUES (2, 'PG');

MERGE INTO rating
    USING (VALUES (3, 'PG-13')) s(rating_id, name)
    ON rating.RATING_ID=s.rating_id
    WHEN NOT MATCHED THEN INSERT VALUES (3, 'PG-13');

MERGE INTO rating
    USING (VALUES (4, 'R')) s(rating_id, name)
    ON rating.RATING_ID=s.rating_id
    WHEN NOT MATCHED THEN INSERT VALUES (4, 'R');

MERGE INTO rating
    USING (VALUES (5, 'NC-17')) s(rating_id, name)
    ON rating.RATING_ID=s.rating_id
    WHEN NOT MATCHED THEN INSERT VALUES (5, 'NC-17');

MERGE INTO genres
    USING (VALUES (1, 'Комедия')) s(genre_id, name)
    ON genres.GENRE_ID=s.genre_id
    WHEN NOT MATCHED THEN INSERT VALUES (1, 'Комедия');

MERGE INTO genres
    USING (VALUES (2, 'Драма')) s(genre_id, name)
    ON genres.GENRE_ID=s.genre_id
    WHEN NOT MATCHED THEN INSERT VALUES (2, 'Драма');

MERGE INTO genres
    USING (VALUES (3, 'Мультфильм')) s(genre_id, name)
    ON genres.GENRE_ID=s.genre_id
    WHEN NOT MATCHED THEN INSERT VALUES (3, 'Мультфильм');

MERGE INTO genres
    USING (VALUES (4, 'Триллер')) s(genre_id, name)
    ON genres.GENRE_ID=s.genre_id
    WHEN NOT MATCHED THEN INSERT VALUES (4, 'Триллер');

MERGE INTO genres
    USING (VALUES (5, 'Документальный')) s(genre_id, name)
    ON genres.GENRE_ID=s.genre_id
    WHEN NOT MATCHED THEN INSERT VALUES (5, 'Документальный');

MERGE INTO genres
    USING (VALUES (6, 'Боевик')) s(genre_id, name)
    ON genres.GENRE_ID=s.genre_id
    WHEN NOT MATCHED THEN INSERT VALUES (6, 'Боевик');


