package ru.yandex.practicum.filmorate.storage.likesStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

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
}
