package ru.yandex.practicum.filmorate.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/users/{id}")
public class RecommendationController {

    private final FilmService filmService;

    public RecommendationController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<Film>> getRecommendations(@PathVariable int id) {
        List<Film> recommendations = filmService.getRecommendations(id);
        return ResponseEntity.ok(recommendations);
    }
}
