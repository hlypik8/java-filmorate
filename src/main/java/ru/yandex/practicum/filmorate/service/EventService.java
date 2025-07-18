package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.eventEnums.EventType;
import ru.yandex.practicum.filmorate.model.eventEnums.Operation;
import ru.yandex.practicum.filmorate.storage.eventDbStorage.EventDbStorage;

import java.time.Instant;
import java.util.Collection;

@Service
public class EventService {

    private final EventDbStorage eventDbStorage;

    public EventService(EventDbStorage eventDbStorage) {
        this.eventDbStorage = eventDbStorage;
    }

    public Collection<Event> getFeed(int id) {
        return eventDbStorage.getEventsByUserId(id);
    }

    private Event createEvent(int userID, EventType eventType, Operation operation, int entityId) {
        Event event = new Event();
        event.setTimestamp(Instant.now().toEpochMilli());
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setUserId(userID);
        event.setEntityId(entityId);

        return eventDbStorage.newEvent(event);
    }

    public Event createAddLikeEvent(int userID, int filmId) {
        return createEvent(userID, EventType.LIKE, Operation.ADD, filmId);
    }

    public Event createRemoveLikeEvent(int userID, int filmId) {
        return createEvent(userID, EventType.LIKE, Operation.REMOVE, filmId);
    }

    public Event createReviewEvent(int userID, Operation operation, int reviewId) {
        return createEvent(userID, EventType.REVIEW, operation, reviewId);
    }

    public Event createAddFriendEvent(int userID, int friendId) {
        return createEvent(userID, EventType.FRIEND, Operation.ADD, friendId);
    }

    public Event createRemoveFriendEvent(int userID, int friendId) {
        return createEvent(userID, EventType.FRIEND, Operation.REMOVE, friendId);
    }

}
