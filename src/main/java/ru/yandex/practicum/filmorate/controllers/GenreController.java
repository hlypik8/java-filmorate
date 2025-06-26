package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/genres")
@ConfigurationPropertiesScan
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> findAll(){
        return genreService.getGenresList();
    }

    @GetMapping("/{id}")
    public Genre findOne(@PathVariable int id){
        return genreService.getGenreById(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(GenreNotFoundException e){
        return Map.of("error", "жанр не найден",
                "errorMessage", e.getMessage());
    }
}
