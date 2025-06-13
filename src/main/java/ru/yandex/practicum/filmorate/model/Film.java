package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.model.annotations.ReleaseDateConstraint;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
public class Film {

    private final Set<Integer> likes = new HashSet<>();

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void removeLike(int userId) {
        likes.remove(userId);
    }

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
}
