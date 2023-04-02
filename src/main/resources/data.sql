MERGE INTO rating
    USING (VALUES (1, 'G')) s(rating_id, name)
    ON rating.RATING_ID=s.rating_id
    WHEN NOT MATCHED THEN INSERT VALUES (1, 'G');

MERGE INTO rating
    USING (VALUES (2, 'PG')) s(rating_id, name)
    ON rating.RATING_ID=s.rating_id
    WHEN NOT MATCHED THEN INSERT VALUES (2, 'PG');

MERGE INTO rating
    USING (VALUES (3, 'PG_13')) s(rating_id, name)
    ON rating.RATING_ID=s.rating_id
    WHEN NOT MATCHED THEN INSERT VALUES (3, 'PG_13');

MERGE INTO rating
    USING (VALUES (4, 'R')) s(rating_id, name)
    ON rating.RATING_ID=s.rating_id
    WHEN NOT MATCHED THEN INSERT VALUES (4, 'R');

MERGE INTO rating
    USING (VALUES (5, 'NC_17')) s(rating_id, name)
    ON rating.RATING_ID=s.rating_id
    WHEN NOT MATCHED THEN INSERT VALUES (5, 'NC_17');




