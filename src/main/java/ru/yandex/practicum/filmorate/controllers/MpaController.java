package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/mpa")
@ConfigurationPropertiesScan
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> findAll(){
        return mpaService.getMpaList();
    }

    @GetMapping("/{id}")
    public Mpa findOne(@PathVariable int id){
        return mpaService.getMpaById(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(MpaNotFoundException e){
        return Map.of("error", "рейтинг mpa не найден",
                "errorMessage", e.getMessage());
    }
}
