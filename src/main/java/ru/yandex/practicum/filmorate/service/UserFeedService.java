package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserFeedService {

    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getUserFeed(int userId) {
        String query = """
                SELECT event_id, user_id, timestamp, event_type, operation, entity_id, entity_type
                FROM user_feed
                WHERE user_id = ?
                ORDER BY timestamp DESC
                """;

        return jdbcTemplate.queryForList(query, userId);
    }
}