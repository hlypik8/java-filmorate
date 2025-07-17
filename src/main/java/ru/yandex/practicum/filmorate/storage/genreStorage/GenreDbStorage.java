package ru.yandex.practicum.filmorate.storage.genreStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;

@Repository
public class GenreDbStorage extends BaseStorage<Genre> {

    private final GenreRowMapper genreRowMapper;

    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper genreRowMapper) {
        super(jdbcTemplate);
        this.genreRowMapper = genreRowMapper;
    }

    public Collection<Genre> getGenreList() {

        String query = """
                SELECT *
                FROM genres;
                """;

        return findMany(query, genreRowMapper);
    }

    public Genre getGenreById(int genreId) {

        String query = """
                SELECT *
                FROM genres
                WHERE id = ?;
                """;

        return findOne(query, genreRowMapper, genreId);
    }

    public Collection<Genre> getGenresByFilmId(int filmId) {

        String query = """
                SELECT g.id, g.name
                FROM films_genres fg
                JOIN genres g ON fg.genre_id = g.id
                WHERE fg.film_id = ?
                ORDER BY fg.genre_id;
                """;

        return findMany(query, genreRowMapper, filmId);
    }

}
