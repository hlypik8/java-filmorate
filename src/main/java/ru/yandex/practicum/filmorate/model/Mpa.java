package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Mpa {

    @NotNull
    private int id;
    private String name;
    private String description;
}
