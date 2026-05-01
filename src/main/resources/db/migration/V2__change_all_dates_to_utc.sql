ALTER TABLE users
    ALTER COLUMN date_created
        TYPE TIMESTAMPTZ
        USING date_created::timestamp AT TIME ZONE 'UTC';

ALTER TABLE posts
    ALTER COLUMN date_created
        TYPE TIMESTAMPTZ
        USING date_created AT TIME ZONE 'UTC';

ALTER TABLE posts
    ALTER COLUMN updated_at
        TYPE TIMESTAMPTZ
        USING updated_at AT TIME ZONE 'UTC';

ALTER TABLE comments
    ALTER COLUMN date_created
        TYPE TIMESTAMPTZ
        USING date_created AT TIME ZONE 'UTC';

ALTER TABLE comments
    ALTER COLUMN updated_at
        TYPE TIMESTAMPTZ
        USING updated_at AT TIME ZONE 'UTC';

ALTER TABLE follows
    ALTER COLUMN date_created
        TYPE TIMESTAMPTZ
        USING date_created AT TIME ZONE 'UTC';

ALTER TABLE users ALTER COLUMN date_created SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE posts ALTER COLUMN date_created SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE comments ALTER COLUMN date_created SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE follows ALTER COLUMN date_created SET DEFAULT CURRENT_TIMESTAMP;