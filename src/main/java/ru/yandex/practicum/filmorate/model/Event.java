package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.eventEnums.EventType;
import ru.yandex.practicum.filmorate.model.eventEnums.Operation;


@Data
public class Event {
    private Long timestamp;
    private int userId;
    private EventType eventType;
    private Operation operation;
    private int eventId;
    private int entityId;
}
