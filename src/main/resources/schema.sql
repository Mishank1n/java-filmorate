CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    login VARCHAR(40) NOT NULL,
    name VARCHAR(40) NOT NULL,
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
    id INTEGER PRIMARY KEY,
    name VARCHAR(40) NOT NULL
);


CREATE TABLE IF NOT EXISTS MPA (
    id INTEGER PRIMARY KEY,
    name VARCHAR(8) NOT NULL,
    description VARCHAR(80)
);

CREATE TABLE IF NOT EXISTS films (
    id INTEGER PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    description VARCHAR(200),
    releaseDate DATE,
    duration INTEGER,
    MPA_id INTEGER REFERENCES MPA(id)
);

CREATE TABLE IF NOT EXISTS friends (
    user_id INTEGER NOT NULL REFERENCES users(id),
    friend_id INTEGER NOT NULL REFERENCES users(id),
    confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (user_id, friend_id)
);


CREATE TABLE IF NOT EXISTS films_genres (
    film_id INTEGER NOT NULL REFERENCES films(id),
    genre_id INTEGER NOT NULL REFERENCES genres(id),
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
    user_id INTEGER NOT NULL REFERENCES users(id),
    film_id INTEGER NOT NULL REFERENCES films(id),
    PRIMARY KEY (user_id, film_id)
);