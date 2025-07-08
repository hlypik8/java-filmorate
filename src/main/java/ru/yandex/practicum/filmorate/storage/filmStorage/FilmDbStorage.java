package ru.yandex.practicum.filmorate.storage.filmStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
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

    private void updateGenres(Film film) {

        String query = """
                DELETE FROM films_genres
                WHERE film_id = ?;
                """;

        update(query, film.getId());

        saveGenres(film);
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

        return film;
    }

    public Collection<Film> getPopularFilms(int count) {
        String query = """
                SELECT f.*,
                       COUNT(l.user_id) AS likes_count
                FROM films AS f
                LEFT JOIN likes AS l
                ON l.film_id = f.id
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

        return findMany(query, filmRowMapper, count);
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