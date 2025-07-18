package ru.yandex.practicum.filmorate.storage.likesStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.*;
import java.util.stream.Collectors;

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
        update(query, userId, filmId);
    }


    public Collection<Integer> getLikesByUser(int userId) {
        String query = """
                SELECT film_id
                FROM likes
                WHERE user_id = ?;
                """;
        return findMany(query, (rs, rowNum) -> rs.getInt("film_id"), userId);
    }

    public Map<Integer, List<Integer>> getLikesByUsers(Collection<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String sql = String.format(
                "SELECT user_id, film_id FROM likes WHERE user_id IN (%s)",
                userIds.stream().map(id -> "?").collect(Collectors.joining(", "))
        );

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userIds.toArray());

        Map<Integer, List<Integer>> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Integer userId = (Integer) row.get("user_id");
            Integer filmId = (Integer) row.get("film_id");

            result.computeIfAbsent(userId, k -> new ArrayList<>()).add(filmId);
        }

        return result;
    }

    public List<Integer> getUsersWhoLikedFilms(Collection<Integer> filmIds) {
        if (filmIds == null || filmIds.isEmpty()) {
            return Collections.emptyList();
        }

        String sql = String.format(
                "SELECT DISTINCT user_id FROM likes WHERE film_id IN (%s)",
                filmIds.stream().map(id -> "?").collect(Collectors.joining(", "))
        );

        return jdbcTemplate.query(
                sql,
                filmIds.toArray(),
                (rs, rowNum) -> rs.getInt("user_id")
        );
    }


}
