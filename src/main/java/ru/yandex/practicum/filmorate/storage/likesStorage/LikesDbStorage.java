package ru.yandex.practicum.filmorate.storage.likesStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public class LikesDbStorage extends BaseStorage<Integer> {


    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void addLike(int userId, int filmId) {
        String addLikeQuery = """
                INSERT INTO likes (user_id, film_id)
                VALUES (?, ?);
                """;
        insert(addLikeQuery, userId, filmId);

        String addEventQuery = """
                INSERT INTO user_feed (user_id, timestamp, event_type, operation, entity_id, entity_type)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(addEventQuery,
                userId,
                LocalDateTime.now(),
                "LIKE",
                "ADD",
                filmId,
                "FILM"
        );
    }


    public void removeLike(int userId, int filmId) {

        String query = """
            DELETE FROM likes
            WHERE user_id = ?
                  AND film_id = ?;
            """;
        delete(query, userId, filmId);

        String removeEventQuery = """
            DELETE FROM user_feed
            WHERE user_id = ?
                  AND entity_id = ?
                  AND entity_type = 'FILM'
                  AND event_type = 'LIKE'
                  AND operation = 'REMOVE';
            """;
        jdbcTemplate.update(removeEventQuery, userId, filmId);
    }


    public Collection<Integer> getLikesByUser(int userId) {
        String query = """
                SELECT film_id
                FROM likes
                WHERE user_id = ?;
                """;
        return findMany(query, (rs, rowNum) -> rs.getInt("film_id"), userId);
    }


    public Collection<Integer> getUsersWhoLikedFilm(int filmId) {
        String query = """
                SELECT user_id
                FROM likes
                WHERE film_id = ?;
                """;
        return findMany(query, (rs, rowNum) -> rs.getInt("user_id"), filmId);
    }

}
