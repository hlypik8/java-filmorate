package ru.yandex.practicum.filmorate.model.durationSerializerDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;
import java.time.Duration;

import com.fasterxml.jackson.databind.JsonDeserializer;

public class MinutesToDurationDeserializer extends JsonDeserializer<Duration> {
    @Override
    public Duration deserialize(JsonParser jsonParser,
                                DeserializationContext deserializationContext) throws IOException {
        // Преобразуем минуты в Duration
        long minutes = jsonParser.getLongValue();
        return Duration.ofMinutes(minutes);
    }
}
