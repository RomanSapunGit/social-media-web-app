CREATE TABLE IF NOT EXISTS users
(
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    name                VARCHAR(255) NOT NULL,
    username            VARCHAR(255) NOT NULL UNIQUE,
    email               VARCHAR(255) NOT NULL UNIQUE,
    password            VARCHAR(255),
    not_blocked         CHAR(50) DEFAULT 'true',
    token               VARCHAR(255),
    google_token        VARCHAR(3000),
    token_creation_date DATETIME,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS user_statistics
(
    id      BIGINT NOT NULL AUTO_INCREMENT,
    consent             CHAR(50) DEFAULT 'false',
    user_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    UNIQUE KEY uk_user_id (user_id)
);

CREATE TABLE IF NOT EXISTS online_times
(
    user_statistics_id   BIGINT NOT NULL,
    online_time_duration BIGINT NOT NULL,
    PRIMARY KEY (user_statistics_id, online_time_duration),
    FOREIGN KEY (user_statistics_id) REFERENCES user_statistics (id)
);

CREATE TABLE IF NOT EXISTS user_followers
(
    user_id      BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, following_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS roles
(
    id   BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(20) UNIQUE,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT FK_user_roles_users FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT FK_user_roles_roles FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS roles
(
    id   BIGINT      NOT NULL AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL UNIQUE,
    PRIMARY KEY (id)
) ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS posts
(
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    title         VARCHAR(100)  NOT NULL,
    description   VARCHAR(2550) NOT NULL,
    creation_time TIMESTAMP     NOT NULL,
    identifier    VARCHAR(255)  NOT NULL UNIQUE,
    author        BIGINT        NOT NULL,
    statistics_id BIGINT        NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_posts_statistics FOREIGN KEY (statistics_id) REFERENCES user_statistics (id),
    CONSTRAINT FK_posts_users FOREIGN KEY (author) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS comments
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    title         VARCHAR(25)  NOT NULL,
    description   VARCHAR(255) NOT NULL,
    identifier    VARCHAR(255) NOT NULL UNIQUE,
    creation_time TIMESTAMP    NOT NULL,
    user_id       BIGINT       NOT NULL,
    post_id       BIGINT       NOT NULL,
    statistics_id BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_comments_statistics FOREIGN KEY (statistics_id) REFERENCES user_statistics (id),
    CONSTRAINT FK_comments_users FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT FK_comments_posts FOREIGN KEY (post_id) REFERENCES posts (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS notifications
(
    id            BIGINT NOT NULL AUTO_INCREMENT,
    message       VARCHAR(255),
    creation_date TIMESTAMP,
    user_id       BIGINT,
    post_id       BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (post_id) REFERENCES posts (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS tags
(
    id   BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS post_tags
(
    post_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT FK_post_tags_posts FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT FK_post_tags_tags FOREIGN KEY (tag_id) REFERENCES tags (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS saved_posts
(
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, post_id),
    CONSTRAINT FK_saved_posts_users FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT FK_saved_posts_posts FOREIGN KEY (post_id) REFERENCES posts (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS saved_comments
(
    user_id    BIGINT NOT NULL,
    comment_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, comment_id),
    CONSTRAINT FK_saved_comments_users FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT FK_saved_comments_comments FOREIGN KEY (comment_id) REFERENCES comments (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS post_upvotes
(
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, user_id),
    CONSTRAINT FK_post_likes_posts FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT FK_post_likes_users FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;
CREATE TABLE IF NOT EXISTS post_downvotes
(
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, user_id),
    CONSTRAINT FK_post_dislikes_posts FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT FK_post_dislikes_users FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS viewed_posts
(
    user_statistics_id BIGINT NOT NULL,
    post_id            BIGINT NOT NULL,
    PRIMARY KEY (user_statistics_id, post_id),
    FOREIGN KEY (user_statistics_id) REFERENCES user_statistics (id),
    FOREIGN KEY (post_id) REFERENCES posts (id)
);

CREATE TABLE IF NOT EXISTS images
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    identifier VARCHAR(255) NOT NULL UNIQUE,
    name       VARCHAR(255) NOT NULL,
    type       VARCHAR(25)  NOT NULL,
    image_data LONGBLOB     NOT NULL,
    user_id    BIGINT UNIQUE,
    post_id    BIGINT,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (post_id) REFERENCES posts (id)
) ENGINE = InnoDB;

INSERT IGNORE INTO roles (id, name)
VALUES (1, 'ROLE_USER');
INSERT IGNORE INTO roles (id, name)
VALUES (2, 'ROLE_ADMIN');

INSERT IGNORE INTO users(id, name, username, email)
VALUES (1, 'Admin1', 'Admin1', 'admin@admin');
INSERT IGNORE INTO user_roles (user_id, role_id)
VALUES (1, 1);
INSERT IGNORE INTO user_statistics (id, consent,user_id)
VALUES (1,'true',1);
INSERT IGNORE INTO images (id, identifier, name, type, image_data, user_id, post_id)
VALUES (1, '1', '1', '1', '1', 1, null);