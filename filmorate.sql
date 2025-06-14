CREATE TABLE "user" (
  "id" integer PRIMARY KEY,
  "email" varchar NOT NULL,
  "login" varchar NOT NULL,
  "name" varchar,
  "birthday" date
);

CREATE TABLE "film" (
  "id" integer PRIMARY KEY,
  "name" varchar NOT NULL,
  "description" varchar(200),
  "release_date" date NOT NULL,
  "duration" integer NOT NULL,
  "mpa_rating_id" integer NOT NULL
);

CREATE TABLE "friends" (
  "id" integer PRIMARY KEY,
  "user_id" integer NOT NULL,
  "friend_id" integer NOT NULL,
  "accepted" boolean
);

CREATE TABLE "likes" (
  "id" integer PRIMARY KEY,
  "user_id" integer NOT NULL,
  "film_id" integer NOT NULL
);

CREATE TABLE "film_genres" (
  "id" integer PRIMARY KEY,
  "film_id" integer NOT NULL,
  "genre_id" integer NOT NULL
);

CREATE TABLE "genre" (
  "id" integer PRIMARY KEY,
  "name" varchar NOT NULL
);

CREATE TABLE "mpa_rating" (
  "id" integer PRIMARY KEY,
  "name" varchar NOT NULL,
  "description" varchar(5)
);

ALTER TABLE "friends" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");

ALTER TABLE "friends" ADD FOREIGN KEY ("friend_id") REFERENCES "user" ("id");

ALTER TABLE "likes" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");

ALTER TABLE "likes" ADD FOREIGN KEY ("film_id") REFERENCES "film" ("id");

ALTER TABLE "film_genres" ADD FOREIGN KEY ("film_id") REFERENCES "film" ("id");

ALTER TABLE "film_genres" ADD FOREIGN KEY ("genre_id") REFERENCES "genre" ("id");

ALTER TABLE "film" ADD FOREIGN KEY ("mpa_rating_id") REFERENCES "mpa_rating" ("id");
