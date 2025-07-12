package ru.yandex.practicum.filmorate.storage.filmStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.util.Collection;

@Repository
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {

    private final FilmRowMapper filmRowMapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper) {
        super(jdbcTemplate);
        this.filmRowMapper = filmRowMapper;
    }

    public Collection<Film> getFilmsList() {

        String query = """
                SELECT *
                FROM films;
                """;

        return findMany(query, filmRowMapper);
    }

    public Film getFilmById(int filmId) {

        String query = """
                SELECT *
                FROM films
                WHERE id = ?;
                """;

        Film film = findOne(query, filmRowMapper, filmId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }

        return film;
    }

    public Collection<Film> getDirectorsFilmsByLikes(int directorId) {

        String query = """
            SELECT f.*
            FROM films f
            JOIN films_directors fd ON f.id = fd.film_id
            LEFT JOIN (
                SELECT film_id, COUNT(*) AS like_count
                FROM likes
                GROUP BY film_id
            ) l ON f.id = l.film_id
            WHERE fd.director_id = ?
            ORDER BY COALESCE(l.like_count, 0) DESC;
            """;

        return findMany(query, filmRowMapper, directorId);
    }

    public Collection<Film> getDirectorsFilmsByYear(int directorId) {

        String query = """
            SELECT f.*
            FROM films f
            JOIN films_directors fd ON f.id = fd.film_id
            LEFT JOIN (
                SELECT film_id, COUNT(*) AS like_count
                FROM likes
                GROUP BY film_id
            ) l ON f.id = l.film_id
            WHERE fd.director_id = ?
            ORDER BY f.release_date ASC;
            """;

        return findMany(query, filmRowMapper, directorId);
    }

    public Film newFilm(Film film) {

        String query = """
                INSERT INTO films (name, description, release_date, duration, mpa_rating_id)
                VALUES (?,?,?,?,?);
                """;

        int id = insert(query,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId());

        film.setId(id);

        saveGenres(film);
        saveDirectors(film);

        return film;
    }

    private void saveGenres(Film film) {

        String query = """
                INSERT INTO films_genres (film_id, genre_id)
                VALUES (?,?);
                """;

        for (Genre genre : film.getGenres()) {
            insert(query, film.getId(), genre.getId());
        }
    }

    private void saveDirectors(Film film) {

        String query = """
                INSERT INTO films_directors (film_id, director_id)
                VALUES (?, ?)
                """;

        for (Director director : film.getDirectors()) {
            jdbcTemplate.update(query, film.getId(), director.getId());
        }
    }

    private void updateGenres(Film film) {

        String query = """
                DELETE FROM films_genres
                WHERE film_id = ?;
                """;

        update(query, film.getId());

        saveGenres(film);
    }

    private void updateDirectors(Film film) {

        String query = """
                DELETE FROM films_directors
                WHERE film_id = ?;
                """;

        jdbcTemplate.update(query, film.getId());

        saveDirectors(film);
    }

    public void removeFilm(int filmId) {

        String query = """
                DELETE FROM films
                WHERE id = ?;
                """;

        if (!delete(query, filmId)) {
            throw new NotFoundException("Фильм не найден");
        }
    }

    public Film updateFilm(Film film) {

        String query = """
                UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?
                WHERE id = ?;
                """;

        update(query,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        updateGenres(film);
        updateDirectors(film);

        return film;
    }

    public Collection<Film> getPopularFilms(int count, Integer genreId, Integer year) {

        String query = """
                 SELECT f.*,
                        COUNT(l.user_id) AS likes_count
                FROM films f
                LEFT JOIN likes l ON l.film_id = f.id
                LEFT JOIN films_genres fg ON fg.film_id = f.id
                WHERE ( ? IS NULL OR fg.genre_id = ? )
                       AND ( ? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ? )
                GROUP BY
                f.id,
                f.name,
                f.description,
                f.release_date,
                f.duration,
                f.mpa_rating_id
                ORDER BY likes_count DESC
                LIMIT ?;
                """;

        return findMany(query, filmRowMapper, genreId, genreId, year, year, count);
    }

    public Collection<Film> getCommonFilms(int userId, int friendId) {

        String query = """
                SELECT
                    f.*,
                    COUNT(l.user_id) AS likes_count
                FROM films AS f
                JOIN mpa_ratings AS m ON f.mpa_rating_id = m.id
                JOIN likes AS l ON f.id = l.film_id
                WHERE f.id IN (
                    SELECT film_id
                    FROM likes
                    WHERE user_id = ?
                )
                AND f.id IN (
                    SELECT film_id
                    FROM likes
                    WHERE user_id = ?
                )
                GROUP BY f.id, m.name
                ORDER BY likes_count DESC;
                """;

        return findMany(query, filmRowMapper, userId, friendId);
    }
}