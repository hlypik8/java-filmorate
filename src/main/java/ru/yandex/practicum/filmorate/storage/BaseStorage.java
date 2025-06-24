package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class BaseStorage<T> {

    protected final JdbcTemplate jdbcTemplate;

    protected T findOne(String query, RowMapper<T> rowMapper, Object... params) {
        try {
            return jdbcTemplate.queryForObject(query, rowMapper, params);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    protected Collection<T> findMany(String query, RowMapper<T> rowMapper, Object... params) {
        return jdbcTemplate.query(query, rowMapper, params);
    }

    protected boolean delete(String query, Object... params) {
        int rowsDeleted = jdbcTemplate.update(query, params);
        return rowsDeleted > 0;
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query);
                  for(int i = 0; i < params.length; i++){
                      ps.setObject(i + 1, params[i]);
                  }
                  return ps;
        });

        if (rowsUpdated == 0) {
            throw new NotFoundException("Данные не были обновлены");
        }
    }

    protected int insert(String query, Object... params) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps;
        }, generatedKeyHolder);

        Integer id = generatedKeyHolder.getKeyAs(Integer.class);
        if (id != null) {
            return id;
        } else {
            throw new NotFoundException("Данные не были сохранены");
        }
    }

}
