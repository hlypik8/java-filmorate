package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Review {
    private int reviewId;

    @NotBlank(message = "Текст отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва не может быть пустым")
    private Boolean isPositive;

    @NotNull(message = "ID пользователя не может быть пустым")
    private Integer userId;

    @NotNull(message = "ID фильма не может быть пустым")
    private Integer filmId;

    @PositiveOrZero(message = "Рейтинг полезности не может быть отрицательным")
    private int useful = 0;

    private LocalDateTime createdAt = LocalDateTime.now();
}