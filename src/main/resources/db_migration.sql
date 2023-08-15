CREATE TABLE IF NOT EXISTS users
(
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    name                VARCHAR(255) NOT NULL,
    username            VARCHAR(255) NOT NULL UNIQUE,
    email               VARCHAR(255) NOT NULL UNIQUE,
    password            VARCHAR(255),
    not_blocked         CHAR(255) DEFAULT 'true',
    token               VARCHAR(255),
    token_creation_date DATETIME,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS user_followers
(
    user_id     BIGINT NOT NULL,
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
    user_id       BIGINT        NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_posts_users FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS tags
(
    id   BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
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

CREATE TABLE IF NOT EXISTS comments
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    title         VARCHAR(25)  NOT NULL,
    description   VARCHAR(255) NOT NULL,
    identifier    VARCHAR(255) NOT NULL UNIQUE,
    creation_time TIMESTAMP    NOT NULL,
    user_id       BIGINT       NOT NULL,
    post_id       BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_comments_users FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT FK_comments_posts FOREIGN KEY (post_id) REFERENCES posts (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS images
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
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