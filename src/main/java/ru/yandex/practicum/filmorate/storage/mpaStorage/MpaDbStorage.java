package ru.yandex.practicum.filmorate.storage.mpaStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.Collection;

@Repository
public class MpaDbStorage extends BaseStorage<Mpa> {

    private final MpaRowMapper mpaRowMapper;

    public MpaDbStorage(JdbcTemplate jdbcTemplate,MpaRowMapper mpaRowMapper){
        super(jdbcTemplate);
        this.mpaRowMapper = mpaRowMapper;
    }

    public Collection<Mpa> getMpaList(){
        String query = """
                SELECT *
                FROM mpa_ratings;
                """;

        return findMany(query, mpaRowMapper);
    }

    public Mpa getMpaById(int mpaId){
        String query = """
                SELECT *
                FROM mpa_ratings
                WHERE id = ?;
                """;

        return findOne(query, mpaRowMapper, mpaId);
    }
}
