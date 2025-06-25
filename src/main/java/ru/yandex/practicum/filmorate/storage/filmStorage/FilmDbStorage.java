package ru.yandex.practicum.filmorate.storage.filmStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.util.Collection;

@Component
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
                VALUES (?,?,?,?,?)
                """;
        int id = insert(query,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId());

        film.setId(id);

       // if(film.getGenres() != null && !film.getGenres().isEmpty()){
       //     saveGenres(film);
       // }

        return film;
    }

    private void saveGenres(Film film){
        /*String deleteQuery = """
                DELETE FROM films_genres
                WHERE film_id = ?;
                """;
        update(deleteQuery, film.getId());
*/
        String insertQuery = """
                INSERT INTO films_genres (film_id, genre_id)
                VALUES (?,?);
                """;
        for (Genre genre : film.getGenres()){
            update(insertQuery, film.getId(), genre.getId());
        }
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

    public Film updateFilm(Film film){

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

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveGenres(film);
        }

        return film;
    }

}
