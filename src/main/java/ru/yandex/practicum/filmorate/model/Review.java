package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class Review {
    private Integer reviewId;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 2000, message = "Content must be less than 2000 characters")
    private String content;

    @NotNull(message = "isPositive cannot be null")
    private Boolean isPositive;

    @Positive(message = "User ID must be positive")
    private Integer userId;

    @Positive(message = "Film ID must be positive")
    private Integer filmId;

    private int useful = 0;
}