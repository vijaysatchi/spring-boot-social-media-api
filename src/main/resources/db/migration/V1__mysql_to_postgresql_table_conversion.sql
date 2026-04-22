-- USERS
CREATE TABLE users (
                       id BIGINT GENERATED ALWAYS AS IDENTITY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       date_created DATE DEFAULT CURRENT_DATE,
                       profile_picture_url VARCHAR(255),
                       bio TEXT,
                       followers_count INT NOT NULL DEFAULT 0,
                       following_count INT NOT NULL DEFAULT 0,
                       banner_colour VARCHAR(7) DEFAULT '1DA1F2',
                       CONSTRAINT users_pk PRIMARY KEY (id),
                       CONSTRAINT users_email_unique UNIQUE (email)
);

-- POSTS
CREATE TABLE posts (
                       id BIGINT GENERATED ALWAYS AS IDENTITY,
                       caption TEXT,
                       date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       user_id BIGINT NOT NULL,
                       like_count BIGINT DEFAULT 0,
                       updated_at TIMESTAMP NULL,
                       CONSTRAINT posts_pk PRIMARY KEY (id),
                       CONSTRAINT posts_user_id_fk
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- COMMENTS
CREATE TABLE comments (
                          id BIGINT GENERATED ALWAYS AS IDENTITY,
                          content TEXT NOT NULL,
                          user_id BIGINT NOT NULL,
                          post_id BIGINT NOT NULL,
                          date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          like_count BIGINT DEFAULT 0,
                          updated_at TIMESTAMP NULL,
                          CONSTRAINT comments_pk PRIMARY KEY (id),
                          CONSTRAINT user_comment_id_fk
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          CONSTRAINT post_comment_id_fk
                              FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- FOLLOWS
CREATE TABLE follows (
                         id BIGINT GENERATED ALWAYS AS IDENTITY,
                         follower_id BIGINT NOT NULL,
                         following_id BIGINT NOT NULL,
                         date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT follows_pk PRIMARY KEY (id),
                         CONSTRAINT follower_id_fk
                             FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
                         CONSTRAINT following_id_fk
                             FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
                         CONSTRAINT follower_following_unique UNIQUE (follower_id, following_id)
);

-- POST LIKES (final version with composite PK)
CREATE TABLE post_likes (
                            user_id BIGINT NOT NULL,
                            post_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, post_id),
                            CONSTRAINT post_likes_user_fk
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT post_likes_post_fk
                                FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- COMMENT LIKES (final version with composite PK)
CREATE TABLE comment_likes (
                               user_id BIGINT NOT NULL,
                               comment_id BIGINT NOT NULL,
                               PRIMARY KEY (user_id, comment_id),
                               CONSTRAINT comment_likes_user_fk
                                   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                               CONSTRAINT comment_likes_comment_fk
                                   FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE
);