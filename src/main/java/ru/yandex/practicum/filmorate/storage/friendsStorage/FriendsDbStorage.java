package ru.yandex.practicum.filmorate.storage.friendsStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public class FriendsDbStorage extends BaseStorage<Integer> {

    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void addFriend(int userId, int friendId) {

        String findReverse = """
                SELECT id
                FROM friends
                WHERE user_id = ? AND friend_id = ?;
                """;

        Integer reverseId = findOne(findReverse, (rs, rowNum) -> rs.getInt("id"), friendId, userId);

        if (reverseId != null) {
            String updateAccepted = """
                    UPDATE friends
                    SET accepted = true
                    WHERE (user_id = ? AND friend_id = ?)
                          OR (user_id = ? AND friend_id = ?);
                    """;
            update(updateAccepted, userId, friendId, friendId, userId);
        } else {
            String insertSql = """
                    INSERT INTO friends (user_id, friend_id, accepted)
                    VALUES (?, ?, true);
                    """;
            insert(insertSql, userId, friendId);
        }

        String addEventQuery = """
                INSERT INTO user_feed (user_id, timestamp, event_type, operation, entity_id, entity_type)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(addEventQuery,
                userId,
                LocalDateTime.now(),
                "FRIEND",
                "ADD",
                friendId,
                "FRIEND"
        );
    }

    public void deleteFriend(int userId, int friendId) {

        String deleteSql = """
                DELETE FROM friends
                WHERE user_id = ? AND friend_id = ?;
                """;
        delete(deleteSql, userId, friendId);

        String updateReverse = """
                UPDATE friends
                SET accepted = false
                WHERE user_id = ? AND friend_id = ?;
                """;
        jdbcTemplate.update(updateReverse, friendId, userId);

        String addEventQuery = """
                INSERT INTO user_feed (user_id, timestamp, event_type, operation, entity_id, entity_type)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(addEventQuery,
                userId,
                LocalDateTime.now(),
                "FRIEND",
                "REMOVE",
                friendId,
                "FRIEND"
        );
    }


    public Collection<Integer> getFriendIds(int userId) {

        String query = """
                SELECT friend_id
                FROM friends
                WHERE user_id = ?;
                """;

        return findMany(query, (rs, rowNum) -> rs.getInt("friend_id"), userId);
    }
}