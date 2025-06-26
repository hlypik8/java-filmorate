package ru.yandex.practicum.filmorate.storage.likesStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;

@Repository
public class LikesDbStorage extends BaseStorage<Integer> {


    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void addLike(int userId, int filmId) {

        String query = """
                INSERT INTO likes (user_id, film_id)
                VALUES (?, ?);
                """;

        insert(query, userId, filmId);
    }

    public void removeLike(int userId, int filmId) {

        String query = """
                DELETE FROM likes
                WHERE user_id = ?
                      AND film_id = ?;
                """;

        delete(query, userId, filmId);
    }

    public Collection<Integer> getLikesByFilmId(int filmId) {

        String query = """
                SELECT user_id
                FROM likes
                WHERE film_id = ?;
                """;

        return findMany(query, (resultSet, rowNum) -> resultSet.getInt("user_id"), filmId);
    }

    public Collection<Integer> getPopularFilmsIds(int count) {

        String query = """
                SELECT film_id,
                       COUNT(*) AS likes_count
                FROM likes
                GROUP BY film_id
                ORDER BY likes_count DESC, film_id
                LIMIT ?;
                """;

        return findMany(query, (resultSet, rowNum) -> resultSet.getInt("film_id"), count);
    }
}
