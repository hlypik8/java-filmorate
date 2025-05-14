package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.model.annotations.PositiveDuration;
import ru.yandex.practicum.filmorate.model.annotations.ReleaseDateConstraint;
import ru.yandex.practicum.filmorate.model.durationSerializerDeserializer.DurationToMinutesSerializer;
import ru.yandex.practicum.filmorate.model.durationSerializerDeserializer.MinutesToDurationDeserializer;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
@EqualsAndHashCode(of = {"id"})
public class Film {


    private int id;

    @NotBlank(message = "Название фильма не может быть пустым!")
    private String name;

    @Size(max = 200, message = "Название фильма должно содержать менее 200 символов!")
    private String description;

    @NotNull(message = "Дата выхода не может быть пустой!")
    @ReleaseDateConstraint(message = "Дата выхода фильма не может быть раньше 28.12.1895!")
    private LocalDate releaseDate;

    @NotNull(message = "Длительность не может быть пустой!")
    @PositiveDuration(message = "Длительность фильма должна быть положительной!")
    @JsonSerialize(using = DurationToMinutesSerializer.class) // Сериализация в минуты
    @JsonDeserialize(using = MinutesToDurationDeserializer.class) // Десериализация из минут
    private Duration duration;
}
