package ru.yandex.practicum.filmorate.storage.eventDbStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.EventRowMapper;

import java.util.Collection;

@Repository
public class EventDbStorage extends BaseStorage<Event> {

    private final EventRowMapper eventRowMapper;

    public EventDbStorage(JdbcTemplate jdbcTemplate, EventRowMapper eventRowMapper) {
        super(jdbcTemplate);
        this.eventRowMapper = eventRowMapper;
    }

    public Event newEvent(Event event) {

        String query = """
                INSERT INTO events (timestamp, user_id, event_type, operation, entity_id)
                VALUES (?,?,?,?,?);
                """;

        int id = insert(query,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId()
        );

        event.setEventId(id);

        return event;
    }

    public Collection<Event> getEventsByUserId(int id) {

        String query = """
                SELECT *
                FROM events
                WHERE user_id = ?
                ORDER BY timestamp ASC;
                """;

        return findMany(query, eventRowMapper, id);
    }

}
