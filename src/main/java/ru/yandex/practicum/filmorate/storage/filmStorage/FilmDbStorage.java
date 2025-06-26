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

    public Collection<Film> getPopular(int count) {
        String query = """
                SELECT f.id,
                       f.name,
                       COUNT(l.user_id) AS rating
                FROM films AS f
                LEFT JOIN likes AS l ON f.id = l.film_id
                GROUP BY f.id
                ORDER BY rating DESC
                LIMIT ?;
                """;
        return findMany(query,filmRowMapper,count);
    }

}