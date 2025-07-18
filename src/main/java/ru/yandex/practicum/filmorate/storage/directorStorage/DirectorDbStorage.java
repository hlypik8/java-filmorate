package ru.yandex.practicum.filmorate.storage.directorStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.util.Collection;

@Slf4j
@Repository
public class DirectorDbStorage extends BaseStorage<Director> {

    private final DirectorRowMapper directorRowMapper;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate, DirectorRowMapper directorRowMapper) {
        super(jdbcTemplate);
        this.directorRowMapper = directorRowMapper;
    }

    public Collection<Director> getDirectorsList() {

        String query = """
                SELECT *
                FROM directors;
                """;

        return findMany(query, directorRowMapper);
    }

    public Director getDirectorById(int filmId) {

        String query = """
                SELECT *
                FROM directors
                WHERE id = ?
                """;

        Director director = findOne(query, directorRowMapper, filmId);
        if (director == null) {
            throw new NotFoundException("Режиссер не найден!");
        }

        return director;
    }

    public Collection<Director> getDirectorsByFilmId(int filmId) {

        String query = """
                SELECT *
                FROM directors
                WHERE id IN (
                    SELECT director_id
                    FROM films_directors
                    WHERE film_id = ?
                )
                """;

        return findMany(query, directorRowMapper, filmId);
    }

    public Director newDirector(Director director) {

        String query = """
                INSERT INTO directors (name)
                VALUES (?)
                """;

        int id = insert(query, director.getName());

        director.setId(id);

        return director;
    }

    public Director updateDirector(Director director) {

        String query = """
                UPDATE directors SET name = ?
                WHERE id = ?;
                """;

        update(query, director.getName(), director.getId());

        return getDirectorById(director.getId());
    }

    public void deleteDirector(int id) {

        String query = """
                DELETE FROM directors
                WHERE id = ?
                """;

        delete(query, id);
    }
}
