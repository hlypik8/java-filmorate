package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class Review {
    private int reviewId;

    @NotBlank(message = "Отзыв не может быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва обязателен")
    private Boolean isPositive;

    @Positive(message = "ID пользователя должен быть положительным")
    private Integer userId;

    @Positive(message = "ID фильма должен быть положительным")
    private Integer filmId;

    private int useful = 0;
}