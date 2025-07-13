package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.model.annotations.ReleaseDateConstraint;

import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode(of = {"id"})
public class Film {

    private int id;

    @NotBlank(message = "Название фильма не может быть пустым!")
    private String name;

    @Size(max = 200, message = "Описание фильма должно содержать менее 200 символов!")
    private String description;

    @NotNull(message = "Дата выхода не может быть пустой!")
    @ReleaseDateConstraint(message = "Дата выхода фильма не может быть раньше 28.12.1895!")
    private LocalDate releaseDate;

    @NotNull(message = "Длительность не может быть пустой!")
    @Positive(message = "Длительность должна быть положительной!")
    private int duration;

    @NotNull
    @Valid
    private Mpa mpa;

    @NotNull
    @Valid
    private Set<Genre> genres = new LinkedHashSet<>();

    @NotNull
    @Valid
    private Set<Director> directors = new HashSet<>();
}
